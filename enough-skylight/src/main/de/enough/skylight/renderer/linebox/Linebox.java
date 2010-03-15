package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;


public class Linebox {
	
	public static int UNDEFINED = Integer.MAX_VALUE;
	
	final PartitionList partitions;
	
	final InlineContainingBlock block;
	
	final int availableWidth;
	
	int inlineRelativeLeft = UNDEFINED;

	int inlineRelativeRight = UNDEFINED;
	
	final int blockRelativeLeft;
	
	final int blockRelativeTop;
	
	int blockRelativeBottom;
	
	int lineHeight = UNDEFINED;
	
	public Linebox(InlineContainingBlock block, int blockRelativeLeft, int blockRelativeTop, int availableWidth) {
		this.availableWidth = availableWidth;
		this.partitions = new PartitionList();
		this.block = block;
		this.blockRelativeLeft = blockRelativeLeft;
		this.blockRelativeTop = blockRelativeTop;
	}
	
	public void addPartition(Partition partition) {
		if(this.inlineRelativeLeft == UNDEFINED || this.inlineRelativeLeft > partition.getInlineRelativeLeft()) {
			this.inlineRelativeLeft = partition.getInlineRelativeLeft();
		}
		
		if(this.inlineRelativeRight == UNDEFINED || this.inlineRelativeRight < partition.getInlineRelativeRight()) {
			this.inlineRelativeRight = partition.getInlineRelativeRight();
		}
		
		if(this.lineHeight == UNDEFINED || this.lineHeight < partition.getHeight()) {
			this.lineHeight = partition.getHeight();
		}
		
		this.partitions.add(partition);
	}
	
	public int getInlineRelativeLeft() {
		return this.inlineRelativeLeft;
	}
	
	public int getInlineRelativeRight() {
		return this.inlineRelativeRight;
	}
	
	public int getBlockRelativeLeft() {
		return this.blockRelativeLeft;
	}
	
	public int getBlockRelativeRight() {
		return this.blockRelativeLeft + getTrimmedWidth();
	}
	
	public int getBlockRelativeTop() {
		return this.blockRelativeTop;
	}
	
	public int getBlockRelativeBottom() {
		return this.blockRelativeTop + this.lineHeight;
	}
	
	public int getLineHeight() {
		return this.lineHeight;
	}
	
	public int getWidth() {
		return this.inlineRelativeRight - this.inlineRelativeLeft;
	}
	
	public boolean fits(Partition partition) {
		int maxRight = (this.inlineRelativeLeft + this.availableWidth);
		return partition.getInlineRelativeRight() <= maxRight;
	}
	
	public boolean overflows() {
		int width = this.inlineRelativeRight - this.inlineRelativeLeft;
		return width > this.availableWidth;
	}
	
	public int getTrimmedInlineRelativeLeft() {
		Partition first = this.partitions.get(0);
		if(first.hasAttribute(Partition.ATTRIBUTE_WHITESPACE)) {
			return this.inlineRelativeLeft + first.getWidth();
		} else {
			return this.inlineRelativeLeft;
		}
	}
	
	public int getTrimmedInlineRelativeRight() {
		Partition last = this.partitions.get(this.partitions.size() - 1);
		if(last.hasAttribute(Partition.ATTRIBUTE_WHITESPACE)) {
			return this.inlineRelativeRight - last.getWidth();
		} else {
			return this.inlineRelativeRight;
		}
	}
	
	public int getTrimmedWidth() {
		return getTrimmedInlineRelativeRight() - getTrimmedInlineRelativeLeft();
	}
	
	public boolean matchesArea(int top) {
		return (top >= getBlockRelativeTop()) && (top <= getBlockRelativeBottom() - 1); 
	}
	
	public PartitionList getPartitions() {
		return this.partitions;
	}
	
	public String toString() {
		return new ToStringHelper("LineBox").
		add("inline relative left", this.inlineRelativeLeft).
		add("inline relative right", this.inlineRelativeRight).
		add("block relative y", this.blockRelativeTop).
		add("height", this.lineHeight).
		add("available width", this.availableWidth).
		add("partitions", this.partitions.size()).
		toString();
	}
}
