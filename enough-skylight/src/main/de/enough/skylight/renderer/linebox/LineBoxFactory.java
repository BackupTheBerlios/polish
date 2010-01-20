package de.enough.skylight.renderer.linebox;

import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class LineBoxFactory {
	
	public static LineBoxList getLineBoxes(PartitionList partitions, int availableWidth) {
		LineBoxList lineboxes = new LineBoxList();
		
		LineBox linebox = null;
		
		if(partitions.size() > 0) { 
			for (int index = 0; index < partitions.size(); index++) {
				Partition partition = partitions.get(index);
				
				if(partition.getWidth() > 0) {
					if(linebox != null && !linebox.overflows() && linebox.fits(partition) && !partition.isNewline()) {
						linebox.addPartition(partition);
					} else {
						linebox = new LineBox(partition,availableWidth);
						lineboxes.add(linebox);
					}
				}
			}
		}
		
		return lineboxes;
	}
}
