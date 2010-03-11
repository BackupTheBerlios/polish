package de.enough.skylight.renderer.linebox;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.view.LayoutAttributes;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class LineBoxLayout {
	
	LineBoxList lineboxes;
	
	LineBoxList leftLines;
	
	LineBoxList rightLines;
	
	int blockAvailableWidth;
	
	int width;
	
	int height;
	
	public LineBoxLayout(int availableWidth) {
		this(availableWidth,null,null);
	}
	
	public LineBoxLayout(int blockAvailableWidth, LineBoxList leftLineBoxes, LineBoxList rightLineBoxes) {
		this.blockAvailableWidth = blockAvailableWidth;
		this.leftLines = leftLineBoxes;
		this.rightLines = rightLineBoxes;
	}
	
	public void addPartitions(PartitionList partitions, InlineContainingBlock block) {
		this.lineboxes = getLineboxes(partitions,block);
	}
	
	 LineBoxList getLineboxes(PartitionList partitions,InlineContainingBlock block) {
		LineBoxList lineboxes = new LineBoxList();
		LineBox linebox = null;
		
		int top = 0;
		
		Item currentPartitionItem = null;
		
		if(partitions.size() > 0) { 
			for (int index = 0; index < partitions.size(); index++) {
				Partition partition = partitions.get(index);
				
				Item partitionItem = partition.getParentItem();
				if(currentPartitionItem != partitionItem ) {
					currentPartitionItem = partitionItem;
				}
				
				if(partition.getWidth() > 0) {
					if(linebox != null && !linebox.overflows() && linebox.fits(partition) && !partition.hasAttribute(Partition.ATTRIBUTE_NEWLINE)) {
						linebox.addPartition(partition);
						LayoutAttributes.setLinebox(currentPartitionItem, linebox);
					} else {
						if(linebox != null) {
							top += linebox.getLineHeight();
							int lineWidth = linebox.getWidth();
							if(lineWidth > this.width) {
								this.width = lineWidth;
							}
						}
						
						int offset = getLineOffset(top);
						int width = getLineWidth(top);
						linebox = null;
						
						if(!partition.hasAttribute(Partition.ATTRIBUTE_WHITESPACE)) {
							linebox = new LineBox(partition, block, offset, top, width);
							LayoutAttributes.setLinebox(currentPartitionItem, linebox);
							lineboxes.add(linebox);
						} else {
							continue;
						}
					}
				}
			}
		}
		
		if(linebox != null) {
			top += linebox.getLineHeight();
			int lineWidth = linebox.getWidth();
			if(lineWidth > this.width) {
				this.width = lineWidth;
			}
		}
		
		if(top > this.height) {
			this.height = top;
		}
		
		return lineboxes;
	}
	
	public int getLineOffset(int top) {
		if(this.leftLines != null) {
			for (int i = 0; i < this.leftLines.size(); i++) {
				LineBox linebox = this.leftLines.get(i);
				if(linebox.matchesArea(top)) {
					return linebox.getTrimmedWidth();
				}
			}
		}
		
		return 0;
	}
	
	public int getLineWidth(int top) {
		int result = this.blockAvailableWidth;
		
		if(this.leftLines != null) {
			for (int i = 0; i < this.leftLines.size(); i++) {
				LineBox linebox = this.leftLines.get(i);
				if(linebox.matchesArea(top)) {
					result -= linebox.getTrimmedWidth();
				}
			}
		}
		
		if(this.rightLines != null) {
			for (int i = 0; i < this.rightLines.size(); i++) {
				LineBox linebox = this.rightLines.get(i);
				if(linebox.matchesArea(top)) {
					result -= linebox.getTrimmedWidth();
				}
			}
		}
		
		return result;
	}
	
	public LineBoxList getLineBoxes() {
		return this.lineboxes;
	}
	
	public int getLayoutWidth() {
		return this.width;
	}
	
	public int getLayoutHeight() {
		return this.height;
	}
}
