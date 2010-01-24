package de.enough.skylight.renderer.linebox;

import javax.microedition.lcdui.Graphics;

import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;


public class LineBox {
	
	PartitionList partitions;
	
	InlineContainingBlock block;
	
	int availableWidth;
	
	int left = Integer.MAX_VALUE;

	int right = Integer.MIN_VALUE;
	
	int top;
	
	int height = Integer.MIN_VALUE;
	
	int offset = 0;
	
	public LineBox(Partition partition, InlineContainingBlock block, int offset, int top, int availableWidth) {
		this.availableWidth = availableWidth;
		this.partitions = new PartitionList();
		this.block = block;
		this.offset = offset;
		this.top = top;
		
		addPartition(partition);
	}
	
	public void addPartition(Partition partition) {
		if(this.left > partition.getLeft()) {
			this.left = partition.getLeft();
		}
		
		if(this.right < partition.getRight()) {
			this.right = partition.getRight();
		}
		
		if(this.height < partition.getHeight()) {
			this.height = partition.getHeight();
		}
		
		this.partitions.add(partition);
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
	
	public void setTop(int top) {
		this.top = top;
	}
	
	public int getTop() {
		return this.top;
	}
	
	public int getBottom() {
		return this.top + this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.right - this.left;
	}
	
	public boolean fits(Partition partition) {
		int maxRight = (this.left + this.availableWidth);
		return partition.getRight() < maxRight;
	}
	
	public boolean overflows() {
		int width = this.right - this.left;
		return width > this.availableWidth;
	}
	
	public int getTrimmedLeft() {
		Partition first = this.partitions.get(0);
		if(first.isWhitespace()) {
			return this.left + first.getWidth();
		} else {
			return this.left;
		}
	}
	
	public int getTrimmedRight() {
		Partition last = this.partitions.get(this.partitions.size() - 1);
		if(last.isWhitespace()) {
			return this.right - last.getWidth();
		} else {
			return this.right;
		}
	}
	
	public int getTrimmedWidth() {
		return getTrimmedRight() - getTrimmedLeft();
	}
	
	public boolean isInLine(int top) {
		return (top >= getTop()) && (top <= getBottom() - 1); 
	}
	
	public void paint(int x, int y, Graphics g) {
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		x = x + this.offset;

		int left = getTrimmedLeft();
		int width = getTrimmedWidth();
		int top = getTop();
		int height = getHeight();

		g.clipRect(x, y + top, width, height);

		int leftBorder = x - left;
		int rightBorder = (x - left) +  width;
		
		this.block.paint(x - left, y + top, leftBorder, rightBorder, g);

		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
	
	public String toString() {
		return "LineBox [" + this.left + "," + this.right + "," + this.top + "," + this.height + "," + this.offset + "," + this.availableWidth + "]";
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public PartitionList getPartitions() {
		return this.partitions;
	}
}
