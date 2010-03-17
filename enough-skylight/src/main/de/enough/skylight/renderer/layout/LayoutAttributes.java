package de.enough.skylight.renderer.layout;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.linebox.Linebox;
import de.enough.skylight.renderer.linebox.LineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class LayoutAttributes {
	static final Integer KEY = new Integer(Integer.MAX_VALUE);

	public static int getRelativeX(Item item) {
		LayoutAttributes attributes = get(item);
		PartitionList partitions = attributes.getPartitions();
		Partition firstPartition = partitions.get(0);
		return firstPartition.getLineboxRelativeX();
	}
	
	public static int getRelativeY(Item item) {
		LayoutAttributes attributes = get(item);
		LineboxList lineboxes = attributes.getLineboxes();
		if(lineboxes.size() > 0) {
			Linebox linebox = lineboxes.get(0);
			return linebox.getBlockRelativeTop(); 
		} else {
			return 0;
		}
	}
	
	public static LayoutAttributes get(Item item) {
		LayoutAttributes attributes = (LayoutAttributes)item.getAttribute(KEY);
		
		if(attributes == null) {
			attributes = new LayoutAttributes();
			item.setAttribute(KEY, attributes);
		}
		
		return attributes;
	}
	
	CssElement element;
	
	ContainingBlock containingBlock;
	
	BlockContainingBlock block;
	
	LineboxList lineboxes;
	
	PartitionList partitions;
	
	int inlineRelativeLeft = 0;
	
	int inlineRelativeRight = 0;

	public LayoutAttributes() {
		this.lineboxes = new LineboxList();
		this.partitions = new PartitionList();
	}

	public CssElement getElement() {
		return this.element;
	}

	public void setElement(CssElement element) {
		this.element = element;
	}

	public ContainingBlock getContainingBlock() {
		return this.containingBlock;
	}

	public void setContainingBlock(ContainingBlock containingBlock) {
		this.containingBlock = containingBlock;
	}

	public BlockContainingBlock getBlock() {
		return this.block;
	}

	public void setBlock(BlockContainingBlock block) {
		this.block = block;
	}
	
	public PartitionList getPartitions() {
		return this.partitions;
	}

	public LineboxList getLineboxes() {
		return this.lineboxes;
	}
	
	public int getInlineRelativeLeft() {
		return this.inlineRelativeLeft;
	}

	public void setInlineRelativeLeft(int inlineOffset) {
		this.inlineRelativeLeft = inlineOffset;
	}

}
