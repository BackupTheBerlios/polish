package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;

public class TextPartition extends Partition{

	int index;
	int length;
	
	public TextPartition(int index, int length, int left, int right, int height, Item parent) {
		super(left, right, height, parent);
		
		this.index = index;
		this.length = length;
	}
}
