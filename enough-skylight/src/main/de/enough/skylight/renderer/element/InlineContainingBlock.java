package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.view.ContainingBlockView;
import de.enough.skylight.renderer.element.view.InlineContainingBlockView;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class InlineContainingBlock extends ContainingBlock {
	
	public InlineContainingBlock() {
		this( null );
	}
	
	public InlineContainingBlock(Style style) {
		super( style );
	}
	
	ContainingBlockView buildView() {
		return new InlineContainingBlockView(this);
	}

	public void addToBody(Item item) {
		add(item);
		this.layoutDescriptor.setContainingBlock(this);
	}

	public void addToLeftFloat(Item item) {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
		block.addToLeftFloat(item);
	}
	
	public void addToRightFloat(Item item) {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
		block.addToRightFloat(item);
	}
	
	public InlineContainingBlock getBody() {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
		return block.getBody();
	}
	
	public InlineContainingBlock getLeftFloat() {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
		return block.getLeftFloat();
	}
	
	public InlineContainingBlock getRightFloat() {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
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
				Partition.partitionBlock(item,partitions);
			}
		}
	}
	
	public boolean isInItemArea(int relX, int relY) {
		BlockContainingBlock block = this.layoutDescriptor.getBlock();
		int blockWidth = block.itemWidth;
		int blockHeight = block.itemHeight;
		
		if (relY < 0 || relY > blockHeight || relX < 0 || relX > blockWidth) { //Math.max(this.itemWidth, this.contentX + this.contentWidth)) {
			return false;
		}
		
		return true;
	}

	public String toString() {
		return new ToStringHelper("InlineContainingBlock").
		add("focused", this.isFocused).
		add("element", this.layoutDescriptor.getCssElement()).
		toString();
	}
}
