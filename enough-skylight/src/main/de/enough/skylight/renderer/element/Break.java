package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class Break extends Item implements Partable {

	protected String createCssSelector() {
		return null;
	}

	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {}

	public void partition(PartitionList partitions) {
		LayoutAttributes attributes = LayoutAttributes.get(this);
		PartitionList itemPartitions = attributes.getPartitions();
		
		Partition partition = new Partition(attributes.getInlineOffset(),attributes.getInlineOffset(),0,this);
		partition.setAttribute(Partition.ATTRIBUTE_NEWLINE);
		
		itemPartitions.add(partition);
		partitions.add(partition);
	}
}
