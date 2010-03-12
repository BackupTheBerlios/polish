package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.layout.LayoutUtils;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.linebox.Linebox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;

public class InlineContainingBlockView extends ContainingBlockView {

	public static Dimension DIMENSION_ZERO = new Dimension(0);
	
	transient int[] itemXOffsets;
	
	transient int[] itemYOffsets;
	
	public InlineContainingBlockView(InlineContainingBlock inlineBlock) {
		super(inlineBlock);
	}
	
	protected void initMargin(Style style, int availWidth) {
		BlockContainingBlock block = LayoutAttributes.get(this.parentContainer).getBlock();
		style.addAttribute("margin-top", DIMENSION_ZERO);
		style.addAttribute("margin-bottom", DIMENSION_ZERO);
		super.initMargin(style, block.getAvailableContentWidth());
	}

	protected void initPadding(Style style, int availWidth) {
		BlockContainingBlock block = LayoutAttributes.get(this.parentContainer).getBlock();
		style.addAttribute("padding-top", DIMENSION_ZERO);
		style.addAttribute("padding-bottom", DIMENSION_ZERO);
		super.initPadding(style, block.getAvailableContentWidth());
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
		BlockContainingBlock block = LayoutAttributes.get(container).getBlock();
		CssElement element = LayoutAttributes.get(block).getElement();
		
		boolean interactive = false;
		
		if(element != null && element.isInteractive()) {
			interactive = true;
		}
		
		Item[] items = container.getItems();
		
		int length = items.length;
		
		if(this.itemXOffsets == null || this.itemXOffsets.length != length) {
			this.itemXOffsets = new int[length];
			this.itemYOffsets = new int[length];
		}

		// item initialisation and x offset calculation
		
		for (int index = 0; index < length; index++) {
			Item item = items[index];
			
			initItem(item, block);
			
			if (item.isInteractive()) {
				interactive = true;
			}
			
			item.relativeX = completeWidth;
			item.relativeY = 0;
			
			int itemHeight = item.itemHeight;
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			
			this.itemXOffsets[index] = completeWidth;
			this.itemYOffsets[index] = 0;
			
			completeWidth += itemWidth;
		}
		
		// item initialisation and y offset calculation 
		
		if(element != null) {
			for (int index = 0; index < length; index++) {
				Item item = items[index];
				if(element.isVerticalAlign(HtmlCssElement.VerticalAlign.BOTTOM)) {
					this.itemYOffsets[index] = LayoutUtils.getBottom(0, maxHeight, item.itemHeight);
				} else if(element.isVerticalAlign(HtmlCssElement.VerticalAlign.MIDDLE)) {
					this.itemYOffsets[index] = LayoutUtils.getMiddle(0, maxHeight, item.itemHeight);
				}
			}
		}
		
		if(interactive) {
			//#debug sl.debug.event
			System.out.println(this.containingBlock + " has interactive children, setting to interactive");
			
			this.parentContainer.setAppearanceMode(Item.INTERACTIVE);
			setAppearanceMode(Item.INTERACTIVE);
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		
		Item item = this.parentContainer.getFocusedItem();
		Container parent = this.parentContainer;
		if(item != null && parent.isFocused && !(item instanceof InlineContainingBlock) && !(item instanceof BlockContainingBlock)) {
			System.out.println("scrolling to item " + item + " : relative y " + item.relativeY + " for " + this.parentContainer);
			scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
		}
		
		//#debug sl.debug.layout
		System.out.println(this.containingBlock + " has dimension : " + this.contentWidth + "/" + this.contentHeight);
	}
	
	protected void paintContent(Container container, Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		//TODO add culling
		paintLine(myItems,x,y,leftBorder,rightBorder,clipX,clipY,clipWidth,clipHeight,g);
	}
	
	protected void paintLine(Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) {
		for (int i = 0; i < myItems.length; i++) {
			if (i != this.focusedIndex) {
				Item item = myItems[i];
				int itemX = x + this.itemXOffsets[i];
				int itemY = y + this.itemYOffsets[i];
				//System.out.println(this.itemYOffsets[i]);
				leftBorder = itemX;
				rightBorder = itemX + item.itemWidth;
				paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		Item focItem = this.focusedItem;
		if (focItem != null) {
			int focItemX = x + this.itemXOffsets[this.focusedIndex];
			int focItemY = y + this.itemYOffsets[this.focusedIndex];
			paintItem(focItem, this.focusedIndex, focItemX, focItemY, focItemX, focItemX + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
		}
	}
}
