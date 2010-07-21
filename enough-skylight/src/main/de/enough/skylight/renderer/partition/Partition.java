package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.node.CssElement;

public class Partition {
	
	public final static byte TYPE_MARGIN_LEFT = 0x00;
	
	public final static byte TYPE_PADDING_LEFT = 0x01;
	
	public final static byte TYPE_CONTENT = 0x02;
	
	public final static byte TYPE_PADDING_RIGHT = 0x03;
	
	public final static byte TYPE_MARGIN_RIGHT = 0x04;
	
	public final static byte TYPE_TEXT = 0x05;
	
	public final static byte TYPE_BLOCK = 0x06;
	
	public final static byte TYPE_FLOAT_LEFT = 0x07;
	
	public final static byte TYPE_FLOAT_RIGHT = 0x08;
	
	public static void partitionInline(Item item, PartitionList partitions) {
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(item);
		
		PartitionList itemPartitions = layoutDescriptor.getPartitions();
		itemPartitions.clear();
		
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
			
			int x = layoutDescriptor.getInlineRelativeOffset();
		
			int marginLeft = x;
			int paddingLeft = x + UiAccess.getMarginLeft(item);
			int contentLeft = paddingLeft + UiAccess.getPaddingLeft(item);
			int contentRight = contentLeft + item.getContentWidth();
			int paddingRight = contentRight + UiAccess.getPaddingRight(item);
			int marginRight = paddingRight + UiAccess.getMarginRight(item);
			
			addToPartitionList(itemPartitions, TYPE_MARGIN_LEFT, marginLeft, paddingLeft, height, item);
			addToPartitionList(itemPartitions, TYPE_PADDING_LEFT, paddingLeft, contentLeft, height, item);
			addToPartitionList(itemPartitions, TYPE_CONTENT, contentLeft, contentRight, height, item);
			addToPartitionList(itemPartitions, TYPE_PADDING_RIGHT, contentRight, paddingRight, height, item);
			addToPartitionList(itemPartitions, TYPE_MARGIN_RIGHT, paddingRight, marginRight, height, item);
			
			partitions.addAll(itemPartitions);
		}
	}
	
	static void addToPartitionList(PartitionList list, byte type, int left, int right, int height, Item parent) {
		if(isValidPartition(left,right)) {
			Partition partition = new Partition(type, left, right, height, parent);
			list.add(partition);
		}
	}	
	
	static boolean isValidPartition(int left, int right) {
		return (right - left) > 0; 
	}
	
	public static void partition(Item item, PartitionList partitions) {
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(item);
		
		PartitionList itemPartitions = layoutDescriptor.getPartitions();
		itemPartitions.clear();
		
		if(item.isVisible()) {
			
			CssElement element = layoutDescriptor.getCssElement();
			
			int x = layoutDescriptor.getInlineRelativeOffset();
			
			int width = item.itemWidth;
			int height = item.itemHeight;
			
			int left = x;
			int right = x + width;
			
			byte type = TYPE_BLOCK;	
			if(element.isFloat()) {
				if(element.isFloat(HtmlCssElement.Float.LEFT)) {
					type = TYPE_FLOAT_LEFT;
				} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
					type = TYPE_FLOAT_RIGHT;
				}
			}
			
			Partition partition = new Partition(type,left, right, height, item);;
			itemPartitions.add(partition);
			
			partitions.addAll(itemPartitions);
		}
	}
	
	/**
	 * the attribute for newline
	 */
	public final static int ATTRIBUTE_NEWLINE = 1;
	
	/**
	 * the attribute for whitespace
	 */
	public final static int ATTRIBUTE_WHITESPACE = 2;
	
	
	byte type;
	
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
	InlineLinebox linebox = null;
	
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
	
	public Partition(byte type, int inlineRelativeLeft, int inlineRelativeRight, int height, Item parentItem) {
		this.type = type;
		this.inlineRelativeLeft = inlineRelativeLeft;
		this.inlineRelativeRight = inlineRelativeRight;
		this.width = inlineRelativeRight - inlineRelativeLeft;
		this.height = height;
		this.parentItem = parentItem;
	}
	
	public void set(Partition partition) {
		this.type = partition.getType();
		this.inlineRelativeLeft = partition.getInlineRelativeLeft();
		this.inlineRelativeRight = partition.getInlineRelativeRight();
		this.width = this.inlineRelativeRight - this.inlineRelativeLeft;
		this.height = partition.getHeight();
		this.parentItem = partition.getParentItem();
			
		if(partition.hasAttribute(ATTRIBUTE_NEWLINE)) {
			setAttribute(ATTRIBUTE_NEWLINE);
		}
	}
	
	public byte getType() {
		return this.type;
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
	
	public InlineLinebox getLinebox() {
		return this.linebox;
	}

	public void setLinebox(InlineLinebox linebox) {
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
		set("type", toString(this.type)).
		set("inline relative X", this.inlineRelativeLeft).
		set("linebox relative X", this.lineboxRelativeX).
		set("linebox relative Y", this.lineboxRelativeY).
		set("width", this.width).
		set("height", this.height).
		set("is newline", hasAttribute(ATTRIBUTE_NEWLINE)).
		set("is whitespace", hasAttribute(ATTRIBUTE_WHITESPACE)).
		set("parent item", this.parentItem).
		set("linebox", this.linebox).
		toString();
	}
	
	String toString(byte type) {
		switch(type) {
			case TYPE_TEXT: return "TEXT";	
			case TYPE_BLOCK : return "BLOCK";
			case TYPE_MARGIN_LEFT : return "MARGIN_LEFT";
			case TYPE_PADDING_LEFT : return "PADDING_LEFT";
			case TYPE_CONTENT : return "CONTENT";
			case TYPE_PADDING_RIGHT: return "PADDING_RIGHT";
			case TYPE_MARGIN_RIGHT: return "MARGIN_RIGHT";
		}
		
		return "UNKNOWN";
	}
}
