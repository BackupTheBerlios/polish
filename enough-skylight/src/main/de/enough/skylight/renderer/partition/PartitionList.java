package de.enough.skylight.renderer.partition;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;

public class PartitionList {
	ArrayList partitionList;
	
	public PartitionList() {
		this.partitionList = new ArrayList();
	}
	
	public void add(Partition partition) {
		if(!this.partitionList.contains(partition)) {
			this.partitionList.add(partition);
		} 
		else {
			int index = this.partitionList.indexOf(partition);
			Partition stored = (Partition)this.partitionList.get(index);
			stored.set(partition);
		}
	}
	
	public void addAll(PartitionList partitions) {
		for (int i = 0; i < partitions.size(); i++) {
			Partition partition = partitions.get(i);
			add(partition);
		}
	}
	
	public Partition get(int index) {
		return (Partition)this.partitionList.get(index);
	}
	
	public int size() {
		return this.partitionList.size();
	}
	
	public void clear() {
		this.partitionList.clear();
	}
	
	public boolean contains(Partition partition) {
		return this.partitionList.contains(partition);
	}
	
	public void sort() {
		if(this.partitionList.size() > 1) {
			Arrays.quicksort(this.partitionList.getInternalArray(), this.partitionList.size(), PartitionComparator.getInstance());
		}
	}
}
