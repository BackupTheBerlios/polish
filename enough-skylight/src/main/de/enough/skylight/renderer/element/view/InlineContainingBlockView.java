package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.ElementAttributes;
import de.enough.skylight.renderer.element.render.Culling;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;

public class InlineContainingBlockView extends ContainerView {

	transient ContainingBlock containingBlock;
	
	transient int[] itemOffsets;
	
	public InlineContainingBlockView(ContainingBlock containingBlock) {
		this.containingBlock = containingBlock;
	}
	
	protected void initItem(Item item, BlockContainingBlock block) {
		if(item instanceof BlockContainingBlock) {
			ItemPreinit.preinit(item,	block.getAvailableContentWidth(),
										block.getAvailableContentHeight());
		} else {
			ItemPreinit.preinit(item,Integer.MAX_VALUE,Integer.MAX_VALUE);
		}
	}
	
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) {
		int maxHeight = 0;
		int completeWidth = 0;
		
		Container container = (Container)parentContainerItem;
		BlockContainingBlock block = ElementAttributes.getBlock(container);
		CssElement element = ElementAttributes.getCssElement(container);
		
		boolean interactive = false;
		
		if(element != null && element.isInteractive()) {
			interactive = true;
		}
		
		Item[] items = container.getItems();
		
		int length = items.length;
		
		this.itemOffsets = new int[length];

		for (int i = 0; i < length; i++) {
			Item item = items[i];
			
			initItem(item, block);
			
			if (item.isInteractive()) {
				interactive = true;
			}
			
			//TODO cant be used 
			item.relativeX = completeWidth;
			item.relativeY = 0;
			
			int itemHeight = item.itemHeight;
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			
			this.itemOffsets[i] = completeWidth;
			
			completeWidth += itemWidth;
		}
		
		if(interactive) {
			//#debug sl.debug.event
			System.out.println(this.containingBlock + " has interactive children, setting to interactive");
			
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		
		//#debug sl.debug.layout
		System.out.println(this.containingBlock + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	protected void paintContent(Container container, Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		Partition partition = ElementAttributes.getPartition(container);
		LineBox linebox = ElementAttributes.getBlock(container).getPaintLineBox();
		
		if(Culling.isVisible(partition, linebox)) {
			paintLine(myItems,x,y,leftBorder,rightBorder,clipX,clipY,clipWidth,clipHeight,g);
			//#debug sl.debug.render
			System.out.println("rendered " + this.containingBlock + " : linebox : " + linebox + " : partition : " + partition );
		} else {
			//#debug sl.debug.render
			System.out.println("culled " + this.containingBlock + " : linebox : " + linebox + " : partition : " + partition );
		}
	}
	
	protected void paintLine(Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		for (int i = 0; i < myItems.length; i++) {
			if (i != this.focusedIndex) {
				Item item = myItems[i];
				int itemX = x + this.itemOffsets[i];
				int itemY = y;
				leftBorder = itemX;
				rightBorder = itemX + item.itemWidth;
				paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		Item focItem = this.focusedItem;
		if (focItem != null) {
			int focItemX = x + this.itemOffsets[this.focusedIndex];
			int focItemY = y;
			paintItem(focItem, this.focusedIndex, focItemX, focItemY, focItemX, focItemX + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
		}
	}
	
	public boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		
		if(gameAction == Canvas.FIRE) {
			CssElement element = ElementAttributes.getCssElement(this.parentItem);
			
			if(element != null && element.isInteractive()) {
				System.out.println("FIRE " + element);
			}
		}
		
		return handled;
	}
}
