package de.enough.skylight.renderer.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class Break extends Item implements Partable {

	LayoutDescriptor layoutDescriptor;
	
	protected String createCssSelector() {
		return null;
	}

	protected void initContent(int firstLineWidth, int availWidth,
			int availHeight) {
		this.layoutDescriptor = ContentView.getLayoutDescriptor(this);
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {}

	public void partition(PartitionList partitions) {
		PartitionList itemPartitions = this.layoutDescriptor.getPartitions();
		
		Partition partition = new Partition(Partition.TYPE_BLOCK,this.layoutDescriptor.getInlineRelativeOffset(),this.layoutDescriptor.getInlineRelativeOffset(),0,this);
		partition.setAttribute(Partition.ATTRIBUTE_NEWLINE);
		
		itemPartitions.add(partition);
		partitions.add(partition);
	}
}
