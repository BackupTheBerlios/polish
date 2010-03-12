package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.view.InlineContainingBlockView;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InlineContainingBlock extends ContainingBlock {
	
	InlineContainingBlockView inlineView;
	
	public InlineContainingBlock() {
		this(null,null);
	}
	
	public InlineContainingBlock(Style style) {
		this(null, style);
	}
	
	public InlineContainingBlock(CssElement element) {
		this(element, null);
	}
	
	public InlineContainingBlock(CssElement element, Style style) {
		super(false,style);
		
		this.inlineView = new InlineContainingBlockView(this);
		setView(this.inlineView);
	}

	public void addToBody(Item item) {
		add(item);
		LayoutAttributes.get(item).setContainingBlock(this);
	}

	public void addToLeftFloat(Item item) {
		BlockContainingBlock block = LayoutAttributes.get(this).getBlock();
		block.addToLeftFloat(item);
	}
	
	public void addToRightFloat(Item item) {
		BlockContainingBlock block = LayoutAttributes.get(this).getBlock();
		block.addToRightFloat(item);
	}
	
	public InlineContainingBlock getBody() {
		BlockContainingBlock block = LayoutAttributes.get(this).getBlock();
		return block.getBody();
	}
	
	public InlineContainingBlock getLeftFloat() {
		BlockContainingBlock block = LayoutAttributes.get(this).getBlock();
		return block.getLeftFloat();
	}
	
	public InlineContainingBlock getRightFloat() {
		BlockContainingBlock block = LayoutAttributes.get(this).getBlock();
		return block.getRightFloat();
	}
	
	public void partition(PartitionList partitions) {
		Partition.partitionInline(this, partitions);
		
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item)this.itemsList.get(i);
			if(item instanceof Partable) {
				Partable partable = (Partable)item;
				partable.partition(partitions);
			} else {
				Partition.partitionInline(item, partitions);
			}
		}
	}
	
	public String toString() {
		return new ToStringHelper("InlineContainingBlock").
		add("focused", this.isFocused).
		add("element", LayoutAttributes.get(this).getElement()).
		toString();
	}
}
