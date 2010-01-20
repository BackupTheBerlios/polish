package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class Break extends Item implements Partable {

	protected String createCssSelector() {
		return null;
	}

	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {}

	public void partition(BlockContainingBlock block, PartitionList partitions) {
		int blockRelativeX = PartitionList.getBlockRelativeX(this, block);
		Partition partition = new Partition(blockRelativeX,blockRelativeX,0,this);
		partition.setNewline(true);
		partitions.add(partition);
	}
}
