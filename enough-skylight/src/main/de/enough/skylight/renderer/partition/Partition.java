package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.linebox.Linebox;


public class Partition {
	public static void partitionInline(Item item, PartitionList partitions) {
		if(item.isVisible()) {
			int height;
			if(item instanceof ContainingBlock) {
				if(item instanceof BlockContainingBlock) {
					height = item.itemHeight;
				} else {
					height = 0;
				}
			} else {
				height = item.getContentHeight();
			}
			
			LayoutAttributes attributes = LayoutAttributes.get(item);
			
			int x = attributes.getInlineOffset();
		
			int marginLeft = x;
			int paddingLeft = x + UiAccess.getMarginLeft(item);
			int contentLeft = paddingLeft + UiAccess.getPaddingLeft(item);
			int contentRight = contentLeft + item.getContentWidth();
			int paddingRight = contentRight + UiAccess.getPaddingRight(item);
			int marginRight = paddingRight + UiAccess.getMarginRight(item);
			
			PartitionList itemPartitions = attributes.getPartitions();
			itemPartitions.clear();
			
			itemPartitions.add(marginLeft, paddingLeft, height, item);
			itemPartitions.add(paddingLeft, contentLeft, height, item);
			itemPartitions.add(contentLeft, contentRight, height, item);
			itemPartitions.add(contentRight, paddingRight, height, item);
			itemPartitions.add(paddingRight, marginRight, height, item);
			
			partitions.addAll(itemPartitions);
		}
	}
	
	public static void partitionBlock(Item item, PartitionList partitions) {
		LayoutAttributes attributes = LayoutAttributes.get(item);
		int x = attributes.getInlineOffset();
		PartitionList itemPartitions = attributes.getPartitions();
		itemPartitions.clear();
		
		if(item.isVisible()) {
			int width = item.itemWidth;
			int height = item.itemHeight;
			
			int left = x;
			int right = x + width;
			itemPartitions.add(left, right, height, item);
		} else {
			itemPartitions.add(x, x, 0, item);
		}	
		
		partitions.addAll(itemPartitions);
	}
	
	/**
	 * the attribute for newline
	 */
	public static int ATTRIBUTE_NEWLINE = 1;
	
	/**
	 * the attribute for whitespace
	 */
	public static int ATTRIBUTE_WHITESPACE = 2;
	
	/**
	 * the x position relative to the linebox x position 
	 */
	int lineboxRelativeX = 0;
	
	/**
	 * the y position relative to the linebox y posiion
	 */
	int lineboxRelativeY = 0;
	
	/**
	 * the parenting linebox
	 */
	Linebox linebox = null;
	
	/**
	 * the left position relative to the inline context 
	 */
	int inlineRelativeLeft;
	
	/**
	 * the right position relative to the inline context 
	 */
	int inlineRelativeRight;

	/**
	 * the width
	 */
	int width;

	/**
	 * the height
	 */
	int height;
	
	/**
	 * the parent item of this partition
	 */
	Item parentItem;
	
	/**
	 * the attributes of this partition
	 */
	int attributes = 0;
	
	public Partition(int inlineRelativeLeft, int inlineRelativeRight, int height, Item parentItem) {
		this.inlineRelativeLeft = inlineRelativeLeft;
		this.inlineRelativeRight = inlineRelativeRight;
		this.width = inlineRelativeRight - inlineRelativeLeft;
		this.height = height;
		this.parentItem = parentItem;
	}
	
	public void set(Partition partition) {
		this.inlineRelativeLeft = partition.getInlineRelativeLeft();
		this.inlineRelativeRight = partition.getInlineRelativeRight();
		this.width = this.inlineRelativeRight - this.inlineRelativeLeft;
		this.height = partition.getHeight();
		this.parentItem = partition.getParentItem();
			
		if(partition.hasAttribute(ATTRIBUTE_NEWLINE)) {
			setAttribute(ATTRIBUTE_NEWLINE);
		}
	}
	
	public int getInlineRelativeLeft() {
		return this.inlineRelativeLeft;
	}
	
	public int getInlineRelativeRight() {
		return this.inlineRelativeRight;
	}
	
	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	
	public Linebox getLinebox() {
		return this.linebox;
	}

	public void setLinebox(Linebox linebox) {
		this.linebox = linebox;
	}
	public int getLineboxRelativeX() {
		return this.lineboxRelativeX;
	}

	public void setLineboxRelativeX(int relativeX) {
		this.lineboxRelativeX = relativeX;
	}

	public int getLineboxRelativeY() {
		return this.lineboxRelativeY;
	}

	public void setLineboxRelativeY(int relativeY) {
		this.lineboxRelativeY = relativeY;
	}
	
	public void setAttribute(int attribute) {
		this.attributes |= attribute;
	}
	
	public boolean hasAttribute(int attribute) {
		return ( this.attributes & attribute ) == attribute;
	}
	
	public Item getParentItem() {
		return this.parentItem;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		Partition partition = (Partition)object;
		return getInlineRelativeLeft() == partition.getInlineRelativeLeft(); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper("Partition").
		add("inline relative X", this.inlineRelativeLeft).
		add("linebox relative X", this.lineboxRelativeX).
		add("linebox relative Y", this.lineboxRelativeY).
		add("width", this.width).
		add("height", this.height).
		add("is newline", hasAttribute(ATTRIBUTE_NEWLINE)).
		add("is whitespace", hasAttribute(ATTRIBUTE_WHITESPACE)).
		add("parent item", this.parentItem).
		add("linebox", this.linebox).
		toString();
	}
}
