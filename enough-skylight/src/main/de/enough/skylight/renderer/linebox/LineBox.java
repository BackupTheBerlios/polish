package de.enough.skylight.renderer.linebox;

import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;


public class LineBox {
	
	PartitionList partitions;
	
	int availableWidth;
	
	int left = Integer.MAX_VALUE;

	int right = Integer.MIN_VALUE;
	
	int height = Integer.MIN_VALUE;
	
	public LineBox(Partition partition, int availableWidth) {
		this.availableWidth = availableWidth;
		this.partitions = new PartitionList();
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
		
		partitions.add(partition);
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
	
	public int getLeft() {
		return left;
	}
	
	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getRight() {
		return right;
	}
	
	public void setRight(int right) {
		this.right = right;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getTrimmedWidth() {
		return getTrimmedRight() - getTrimmedLeft();
	}
	
	public int getWidth() {
		return this.right - this.left;
	}
	
	public String toString() {
		return "LineBox [" + this.left + "," + this.right + "," + this.height + "]";
	}
}
