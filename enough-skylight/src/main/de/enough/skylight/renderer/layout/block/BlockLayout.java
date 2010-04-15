package de.enough.skylight.renderer.layout.block;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockLayout {
	
	InlineLineboxList lineboxes;
	
	int blockAvailableWidth;
	
	int width;
	
	int height;
	
	public BlockLayout(int blockAvailableWidth) {
		this.blockAvailableWidth = blockAvailableWidth;
	}
	
	public void addPartitions(PartitionList partitions, InlineContainingBlock block) {
		this.lineboxes = getLineboxes(partitions,block);
	}
	
	 InlineLineboxList getLineboxes(PartitionList partitions,InlineContainingBlock block) {
		InlineLineboxList lineboxes = new InlineLineboxList();
		InlineLinebox linebox = null;
		
		int top = 0;
		
		if(partitions.size() > 0) { 
			for (int index = 0; index < partitions.size(); index++) {
				Partition partition = partitions.get(index);
				
				if(partition.getWidth() > 0) {
					if(linebox != null && !linebox.overflows() && linebox.fits(partition) && !partition.hasAttribute(Partition.ATTRIBUTE_NEWLINE)) {
						addToLinebox(linebox, partition);
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
							linebox = new InlineLinebox(offset, top, width);
							addToLinebox(linebox, partition);
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
	 
	public void addToLinebox(InlineLinebox linebox, Partition partition) {
		Item partitionItem = partition.getParentItem();
		LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(partitionItem);
		layoutDescriptor.getLineboxes().add(linebox);						
		
		partition.setLineboxRelativeX(linebox.getWidth());
		linebox.addPartition(partition);
	}
	
	public int getLineOffset(int top) {
		
		
		return 0;
	}
	
	public int getLineWidth(int top) {
		int result = this.blockAvailableWidth;
		
		return result;
	}
	
	public InlineLineboxList getLineBoxes() {
		return this.lineboxes;
	}
	
	public int getLayoutWidth() {
		return this.width;
	}
	
	public int getLayoutHeight() {
		return this.height;
	}
}
