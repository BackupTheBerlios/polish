package de.enough.skylight.renderer.partition;

import de.enough.polish.util.Comparator;

public class PartitionComparator implements Comparator {
	
	static PartitionComparator instance;
	
	public static PartitionComparator getInstance() {
		if(instance == null) {
			instance = new PartitionComparator();
		}
		
		return instance;
	}

	public int compare(Object o1, Object o2) {
		Partition first = (Partition)o1;
		Partition second = (Partition)o2;
		
		if(first.getInlineRelativeLeft() < second.getInlineRelativeLeft()) {
			return -1;
		} else if (first.getInlineRelativeLeft() > second.getInlineRelativeLeft()) {
			return 1;
		} 
		
		return 0;
	}

}
