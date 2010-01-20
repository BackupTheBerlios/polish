package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.IntList;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;

public class PartitionList {
	
	public static int getBlockRelativeX(Item item, BlockContainingBlock block) {
		int blockContentX = block.getAbsoluteX() + block.getContentX();
		return item.getAbsoluteX() - blockContentX;
	}
	
	public static void partitionInline(Item item, BlockContainingBlock block, PartitionList partitions) {
		int x = getBlockRelativeX(item, block); 
		
		int height = item.itemHeight;
		
		int marginLeft = x;
		int paddingLeft = x + UiAccess.getMarginLeft(item);
		int contentLeft = paddingLeft + UiAccess.getPaddingLeft(item);
		int contentRight = contentLeft + item.getContentWidth();
		int paddingRight = contentRight + UiAccess.getPaddingRight(item);
		int marginRight = paddingRight + UiAccess.getMarginRight(item);
		
		partitions.add(new Partition(marginLeft, paddingLeft, height, item));
		partitions.add(new Partition(paddingLeft, contentLeft, height, item));
		partitions.add(new Partition(contentLeft, contentRight, height, item));
		partitions.add(new Partition(contentRight, paddingRight, height, item));
		partitions.add(new Partition(paddingRight, marginRight, height, item));
	}
	
	public static void partitionBlock(Item item, BlockContainingBlock block, PartitionList partitions) {
		int x = getBlockRelativeX(item, block);
		
		int width = item.itemWidth;
		int height = item.itemHeight;
		
		int left = x;
		int right = x + width;
		
		partitions.add(new Partition(left,right, height, item));	
	}
	
	ArrayList partitionList;
	
	public PartitionList() {
		this.partitionList = new ArrayList();
	}
	
	public void add(Partition partition) {
		if(!this.partitionList.contains(partition)) {
			this.partitionList.add(partition);
		} else {
			int index = this.partitionList.indexOf(partition);
			Partition stored = (Partition)this.partitionList.get(index);
			stored.setPartition(partition);
		}
	}
	
	public Partition get(int index) {
		return (Partition)this.partitionList.get(index);
	}
	
	public int size() {
		return this.partitionList.size();
	}
	
	public void sort() {
		if(this.partitionList.size() > 1) {
			Arrays.quicksort(this.partitionList.getInternalArray(), this.partitionList.size(), PartitionComparator.getInstance());
		}
	}
}
