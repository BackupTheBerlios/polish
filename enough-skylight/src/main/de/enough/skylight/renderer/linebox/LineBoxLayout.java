package de.enough.skylight.renderer.linebox;

import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class LineBoxLayout {
	
	LineBoxList lineboxes;
	
	LineBoxList leftLines;
	
	LineBoxList rightLines;
	
	int availableWidth;
	
	int width;
	
	int height;
	
	public LineBoxLayout(int availableWidth) {
		this(availableWidth,null,null);
	}
	
	public LineBoxLayout(int availableWidth, LineBoxList leftLineBoxes, LineBoxList rightLineBoxes) {
		this.availableWidth = availableWidth;
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
		
		if(partitions.size() > 0) { 
			for (int index = 0; index < partitions.size(); index++) {
				Partition partition = partitions.get(index);
				
				if(partition.getWidth() > 0) {
					if(linebox != null && !linebox.overflows() && linebox.fits(partition) && !partition.isNewline()) {
						linebox.addPartition(partition);
					} else {
						if(linebox != null) {
							top += linebox.getHeight();
							int lineWidth = linebox.getWidth();
							if(lineWidth > this.width) {
								this.width = lineWidth;
							}
						}
						
						int offset = getLineOffset(top);
						int width = getLineWidth(top);
						linebox = null;
						if(partition.isWhitespace()) {
							continue;
						} else {
							linebox = new LineBox(partition, block, offset, top, width);
							lineboxes.add(linebox);
						}
					}
				}
			}
		}
		
		if(linebox != null) {
			top += linebox.getHeight();
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
				if(linebox.isInLine(top)) {
					return linebox.getTrimmedWidth();
				}
			}
		}
		
		return 0;
	}
	
	public int getLineWidth(int top) {
		int result = this.availableWidth;
		
		if(this.leftLines != null) {
			for (int i = 0; i < this.leftLines.size(); i++) {
				LineBox linebox = this.leftLines.get(i);
				if(linebox.isInLine(top)) {
					result -= linebox.getTrimmedWidth();
				}
			}
		}
		
		if(this.rightLines != null) {
			for (int i = 0; i < this.rightLines.size(); i++) {
				LineBox linebox = this.rightLines.get(i);
				if(linebox.isInLine(top)) {
					result -= linebox.getTrimmedWidth();
				}
			}
		}
		
		return result;
	}
	
	public LineBoxList getLineBoxes() {
		return this.lineboxes;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
