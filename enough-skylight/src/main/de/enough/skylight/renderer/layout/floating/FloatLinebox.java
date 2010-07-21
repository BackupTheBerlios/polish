package de.enough.skylight.renderer.layout.floating;

import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;


public class FloatLinebox {
	final BlockContainingBlock block;
	
	final int orientation;
	
	int availableWidth;
	
	int contextRelativeLeft;
	
	int contextRelativeTop;
	
	int width;
	
	int height;
	
	int inlineRelativeLeft;
	
	int inlineRelativeRight;
	
	PartitionList partitions;
	
	public FloatLinebox(int orientation, BlockContainingBlock block) {
		this.block = block;
		this.orientation = orientation;
		this.width = 0;
		this.height = 0;
		this.partitions = new PartitionList();
	}
	
	public void add(Partition partition) {
		this.partitions.add(partition);
		
		addPartitionDimension(partition);
	}
	
	void addPartitionDimension(Partition partition) {
		this.width += partition.getWidth();
		
		if(partition.getHeight() > this.height) {
			this.height = partition.getHeight();
		}
	}
	
	public BlockContainingBlock getBlock() {
		return this.block;
	}
	
	public int getAvailableWidth() {
		return this.availableWidth;
	}

	public void setAvailableWidth(int availableWidth) {
		this.availableWidth = availableWidth;
	}
	
	public void setContextRelativeLeft(int contextRelativeLeft) {
		this.contextRelativeLeft = contextRelativeLeft;
	}
	
	public void setContextRelativeTop(int contextRelativeTop) {
		this.contextRelativeTop = contextRelativeTop;
	}
	
	public int getContextRelativeLeft() {
		return this.contextRelativeLeft;
	}
	
	public int getContextRelativeRight() {
		return this.contextRelativeLeft + this.width;
	}
	
	public int getContextRelativeTop() {
		return this.contextRelativeTop;
	}
	
	public int getContextRelativeBottom() {
		return this.contextRelativeTop + this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public boolean fits(Partition partition) {
		return (this.width + partition.getWidth()) < this.availableWidth;
	}
	
	public boolean isInLine(int contextRelativeY) {
		return 	contextRelativeY > this.contextRelativeTop && 
				contextRelativeY < (this.contextRelativeTop + this.height);
	}
	
	public int getExtend(int contextRelativeY) {
		if(this.orientation == FloatLayout.ORIENTATION_LEFT) {
			return getLeftExtend(contextRelativeY);
		} else {
			return getRightExtend(contextRelativeY);
		}
	}
	
	int getLeftExtend(int contextRelativeY) {
		int leftExtend = this.contextRelativeLeft;
		int lineRelativeY = contextRelativeY - this.contextRelativeTop;
		for (int index = 0; index < this.partitions.size(); index++) {
			Partition partition = this.partitions.get(index);
			
			if(isPartitionInLine(partition, lineRelativeY)) {
				break;
			} else {
				leftExtend += partition.getWidth();
			}
		}
		
		return leftExtend;
	}
	
	int getRightExtend(int contextRelativeY) {
		int rightExtend = getContextRelativeRight();
		int lineRelativeY = contextRelativeY - this.contextRelativeTop;
		for (int index = this.partitions.size(); index > 0; --index) {
			Partition partition = this.partitions.get(index);
			
			if(isPartitionInLine(partition, lineRelativeY)) {
				break;
			} else {
				rightExtend -= partition.getWidth();
			}
		}
		
		return rightExtend;
	}
	
	boolean isPartitionInLine(Partition partition, int lineRelativeY) {
		return partition.getHeight() > lineRelativeY;
	}
	
	public String toString() {
		return new ToStringHelper("FloatLineBox").
		set("orientation",this.orientation).
		set("contextRelativeLeft",this.contextRelativeLeft).
		set("contextRelativeTop",this.contextRelativeTop).
		set("width",this.width).
		set("height",this.height).
		toString();
	}
}
