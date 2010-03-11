package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.view.LayoutAttributes;
import de.enough.skylight.renderer.linebox.LineBox;


public class Partition {
	
	public static int getRelativeX(Item item) {
		BlockContainingBlock block = LayoutAttributes.getBlock(item);
		int blockContentX = block.getAbsoluteX() + block.getContentX();
		int result = item.getAbsoluteX() - blockContentX;
		//System.out.println("relative X for " + item + " : " + result );
		return result;
	}
	
	public static int getRelativeY(Item item) {
		BlockContainingBlock block = LayoutAttributes.getBlock(item);
		int blockContentY = block.getAbsoluteY() + block.getContentY();
		int result = item.getAbsoluteY() - blockContentY;
		//System.out.println("relative Y for " + item + " : " + result );
		return result;
	}
	
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
			
			int x = getRelativeX(item);
		
			int marginLeft = x;
			int paddingLeft = x + UiAccess.getMarginLeft(item);
			int contentLeft = paddingLeft + UiAccess.getPaddingLeft(item);
			int contentRight = contentLeft + item.getContentWidth();
			int paddingRight = contentRight + UiAccess.getPaddingRight(item);
			int marginRight = paddingRight + UiAccess.getMarginRight(item);
			
			partitions.add(marginLeft, paddingLeft, height, item);
			partitions.add(paddingLeft, contentLeft, height, item);
			partitions.add(contentLeft, contentRight, height, item);
			partitions.add(contentRight, paddingRight, height, item);
			partitions.add(paddingRight, marginRight, height, item);
		}
	}
	
	public static Partition partitionBlock(Item item) {
		int x = getRelativeX(item);
		
		if(item.isVisible()) {
			int width = item.itemWidth;
			int height = item.itemHeight;
			
			int left = x;
			int right = x + width;
			return new Partition(left,right, height, item);
		} else {
			return new Partition(x,x, 0, item);
		}	
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
	LineBox linebox = null;
	
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
	
	public LineBox getLinebox() {
		return this.linebox;
	}

	public void setLinebox(LineBox linebox) {
		this.linebox = linebox;
	}
	public int getRelativeX() {
		return this.lineboxRelativeX;
	}

	public void setRelativeX(int relativeX) {
		this.lineboxRelativeX = relativeX;
	}

	public int getRelativeY() {
		return this.lineboxRelativeY;
	}

	public void setRelativeY(int relativeY) {
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
		add("relative X", this.lineboxRelativeX).
		add("relative Y", this.lineboxRelativeY).
		add("width", this.width).
		add("height", this.height).
		add("is newline", hasAttribute(ATTRIBUTE_NEWLINE)).
		add("is whitespace", hasAttribute(ATTRIBUTE_WHITESPACE)).
		add("parent item", this.parentItem).
		add("linebox", this.linebox).
		toString();
	}
}
