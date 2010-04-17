package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;


public class InlineLinebox {
	
	public static int UNDEFINED = Integer.MAX_VALUE;
	
	final PartitionList partitions;
	
	final int availableWidth;
	
	int inlineRelativeLeft = UNDEFINED;

	int inlineRelativeRight = UNDEFINED;
	
	int blockRelativeLeft;
	
	int blockRelativeTop;
	
	int lineHeight = UNDEFINED;
	
	public InlineLinebox(int availableWidth) {
		this.availableWidth = availableWidth;
		this.partitions = new PartitionList();
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
	
	public void setBlockRelativeLeft(int blockRelativeLeft) {
		this.blockRelativeLeft = blockRelativeLeft;
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
	
	public void setBlockRelativeTop(int blockRelativeTop) {
		this.blockRelativeTop = blockRelativeTop;
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
		if(this.inlineRelativeLeft != UNDEFINED && this.inlineRelativeRight != UNDEFINED) {
			int maxRight = (this.inlineRelativeLeft + this.availableWidth);
			return partition.getInlineRelativeRight() <= maxRight;
		} else {
			return true;
		}
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
	
	public PartitionList getPartitions() {
		return this.partitions;
	}
	
	public String toString() {
		return new ToStringHelper("InlineLineBox").
		add("inline relative left", this.inlineRelativeLeft).
		add("inline relative right", this.inlineRelativeRight).
		add("block relative y", this.blockRelativeTop).
		add("height", this.lineHeight).
		add("width", getWidth()).
		add("available width", this.availableWidth).
		add("partitions", this.partitions.size()).
		toString();
	}
}
