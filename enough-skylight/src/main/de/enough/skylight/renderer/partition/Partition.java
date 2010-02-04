package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.ElementAttributes;


public class Partition {
	
	public static int getBlockRelativeX(Item item) {
		BlockContainingBlock block = ElementAttributes.getBlock(item);
		int blockContentX = block.getAbsoluteX() + block.getContentX();
		return item.getAbsoluteX() - blockContentX;
	}
	
	public static void partitionInline(Item item, PartitionList partitions) {
		int x = getBlockRelativeX(item);
		
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
			
		
			int marginLeft = x;
			int paddingLeft = x + UiAccess.getMarginLeft(item);
			int contentLeft = paddingLeft + UiAccess.getPaddingLeft(item);
			int contentRight = contentLeft + item.getContentWidth();
			int paddingRight = contentRight + UiAccess.getPaddingRight(item);
			int marginRight = paddingRight + UiAccess.getMarginRight(item);
			
			partitions.add(new Partition(marginLeft, paddingLeft, height, item));
			partitions.add(new Partition(paddingLeft, contentLeft, height, item));
			partitions.add(new Partition(contentLeft, contentRight, height, item));
			partitions.add(new Partition(contentRight, paddingRight, height, item));
			partitions.add(new Partition(paddingRight, marginRight, height, item));
		}
	}
	
	public static Partition partitionBlock(Item item) {
		int x = getBlockRelativeX(item);
		
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
	
	int left;
	
	int right;
	
	int height = 0;
	
	boolean newline = false;
	
	boolean whitespace = false;
	
	Item parent;
	
	public Partition(int left, int right, int height, Item parent) {
		this.left = left;
		this.right = right;
		this.height = height;
		this.parent = parent;
	}
	
	public void setPartition(Partition partition) {
		this.left = partition.getLeft();
		this.right = partition.getRight();
		this.height = partition.getHeight();
		this.parent = partition.getParent();
			
		if(partition.isNewline()) {
			this.newline = true;
		}
	}
	
	public int getLeft() {
		return this.left;
	}

	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getRight() {
		return this.right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return this.right - this.left;
	}
	
	public void setNewline(boolean newline) {
		this.newline = newline;
	}
	
	public boolean isNewline() {
		return this.newline;
	}
	
	public void setWhitespace(boolean whitespace) {
		this.whitespace = whitespace;
	}
	
	public boolean isWhitespace() {
		return this.whitespace;
	}
	
	public Item getParent() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		Partition partition = (Partition)object;
		return getLeft() == partition.getLeft(); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Partition [" + 
		"left:" + this.left + "," + 
		"right:"+ this.right + "," + 
		"height:" + this.height + "," + 
		"newline:" + this.newline + "," + 
		"whitespace:" + this.whitespace + "," + 
		"parent:" + this.parent + "]";
	}
	
}
