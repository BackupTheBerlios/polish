package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InlineContainingBlock extends Container implements ContainingBlock, Partable {

	CssElement element;
	
	BlockContainingBlock block;
	
	PartitionList blockPartitions;
	
	public InlineContainingBlock() {
		this(null,null);
	}
	
	public InlineContainingBlock(Style style) {
		this(null, style);
	}
	
	public InlineContainingBlock(CssElement element) {
		this(element, null);
	}
	
	public InlineContainingBlock(CssElement element, Style style) {
		super(false,style);
		
		this.blockPartitions = new PartitionList();
		
		setAppearanceMode(Item.PLAIN);
	}

	public void addToBody(Item item) {
		add(item);
	}

	public void addToLeftFloat(Item item) {
		this.block.addToLeftFloat(item);
	}
	
	public void addToRightFloat(Item item) {
		this.block.addToRightFloat(item);
	}
	
	public CssElement getElement() {
		return this.element;
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
	}
	
	protected void initItem(Item item) {
		if(item instanceof BlockContainingBlock) {
			ItemPreinit.preinit(item,	this.block.getAvailableContentWidth(),
										this.block.getAvailableContentHeight());
		} else {
			ItemPreinit.preinit(item,Integer.MAX_VALUE,Integer.MAX_VALUE);
		}
	}

	public void partition(BlockContainingBlock block, PartitionList partitions) {
		PartitionList.partitionBlock(this, block, this.blockPartitions);
		
		PartitionList.partitionInline(this, block, partitions);
		
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item)this.itemsList.get(i);
			if(item instanceof Partable) {
				Partable partable = (Partable)item;
				partable.partition(this.block, partitions);
			} else {
				PartitionList.partitionInline(item, this.block, partitions);
			}
		}
	}
	
	
	
	public BlockContainingBlock getParentBlock() {
		return this.block;
	}

	public void setParentBlock(BlockContainingBlock block) {
		this.block = block;
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		if(this.blockPartitions.size() > 0) { 
			Partition blockPartition = this.blockPartitions.get(0);
			
			LineBox linebox = this.block.getPaintLineBox();
			
			if(	(	blockPartition.getLeft() >= linebox.getLeft() && 
					blockPartition.getLeft() <= linebox.getRight()) ||
				(	blockPartition.getRight() >= linebox.getLeft() && 
					blockPartition.getRight() <= linebox.getRight()) ||
				(	blockPartition.getLeft() <= linebox.getLeft() && 
					blockPartition.getRight() >= linebox.getRight())) {
				super.paintContent(x, y, leftBorder, rightBorder, g);
			} 
		} else {
			super.paintContent(x, y, leftBorder, rightBorder, g);
		}
	}
}
