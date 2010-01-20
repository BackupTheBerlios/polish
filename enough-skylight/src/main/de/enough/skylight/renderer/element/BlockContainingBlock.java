package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.linebox.LineBoxFactory;
import de.enough.skylight.renderer.linebox.LineBoxList;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.PartitionList;
import de.enough.skylight.renderer.view.element.ElementDescription;

public class BlockContainingBlock extends Container implements Partable {

	LineBoxList lineboxes;
	
	public BlockContainingBlock() {
		this(null);
	}
	
	public BlockContainingBlock(Style style) {
		super(false,style);
	}
	
	protected int getContentHeight(LineBoxList lineboxes) {
		int height = 0;
		for (int index = 0; index < lineboxes.size(); index++) {
			LineBox linebox = lineboxes.get(index);
			height += linebox.getHeight();
		}

		return height;
	}
		
	protected void initItem(Item item) {
		if(item instanceof BlockContainingBlock) {
			int availWidth = getAvailableContentWidth();
			int availHeight = getAvailableContentHeight();
			
			ItemPreinit.preinit(item,availWidth,availHeight);
		} else {
			ItemPreinit.preinit(item,Integer.MAX_VALUE,Integer.MAX_VALUE);
		}
	}
	
	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		int maxHeight = 0;
		int completeWidth = 0;
		Item[] items = getItems();

		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			
			initItem(item);
			
			int itemHeight = item.itemHeight;
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			item.relativeX = completeWidth;
			item.relativeY = 0;
			completeWidth += itemWidth;
		}
		
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		
		PartitionList partitionList = new PartitionList();
		
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item)this.itemsList.get(i);
			if(item instanceof Partable) {
				Partable partable = (Partable)item;
				partable.partition(this,partitionList);
			} else {
				PartitionList.partitionInline(item, this, partitionList);
			}
		}

		partitionList.sort();
		
//		System.out.println("partitions for " + this + ":");
//		for (int i = 0; i < partitionList.size(); i++) {
//			System.out.println(partitionList.get(i));
//		}

		this.lineboxes = LineBoxFactory.getLineBoxes(partitionList, availWidth);

//		System.out.println("lineboxes for " + this + ":");
//		for (int i = 0; i < this.lineboxes.size(); i++) {
//			System.out.println(this.lineboxes.get(i));
//		}

		this.contentWidth = availWidth;
		this.contentHeight = getContentHeight(this.lineboxes);
	}
	
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		for (int index = 0; index < this.lineboxes.size(); index++) {
			LineBox linebox = this.lineboxes.get(index);

			paintLinebox(x, y, linebox, g);

			int lineboxHeight = linebox.getHeight();
			y += lineboxHeight;
		}
	}

	protected void paintLinebox(int x, int y, LineBox linebox, Graphics g) {
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();

		int left = linebox.getTrimmedLeft();
		int width = linebox.getTrimmedWidth();
		int height = linebox.getHeight();

		g.clipRect(x, y, width, height);

		int leftBorder = x - left;
		int rightBorder = (x + this.itemWidth) - left;
		
		super.paintContent(x - left, y, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public void partition(BlockContainingBlock block, PartitionList partitions) {
		PartitionList.partitionBlock(this, block, partitions);
	}

}
