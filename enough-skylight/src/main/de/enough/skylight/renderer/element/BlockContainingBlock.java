package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.view.BlockContainingBlockView;
import de.enough.skylight.renderer.element.view.ContainingBlockView;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends ContainingBlock {
	
	protected InlineContainingBlock body;
	
	public BlockContainingBlock() {
		this(null);
	}
	
	public BlockContainingBlock(Style style) {
		super( style );
	}
	
	ContainingBlockView buildView() {
		return new BlockContainingBlockView(this);
	}
	
	public void addToBody(Item item) {
		if(this.body == null) {
			//#style element
			this.body = new InlineContainingBlock();
			LayoutDescriptor layoutDescriptor = (LayoutDescriptor)this.body.getContainingBlockView();
			
			layoutDescriptor.setCssElement(null);
			layoutDescriptor.setContainingBlock(this);
			layoutDescriptor.setBlock(this);
			layoutDescriptor.setViewport(getLayoutDescriptor().getViewport());
			add(this.body);
		}
	
		this.body.add(item);
		LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
		descriptor.setContainingBlock(this.body);
	}
	
	public InlineContainingBlock getBody() {
		return this.body;
	}
	
	public void partition(PartitionList partitions) {
		Partition.partition(this,partitions);
	}
	
	public InlineLinebox getPaintLineBox() {
		BlockContainingBlockView view = (BlockContainingBlockView)this.layoutDescriptor;
		return view.getPaintLineBox();
	}
	
	public String toString() {
		return new ToStringHelper("BlockContainingBlock").
		add("hashcode", this.hashCode()).
		add("focused", this.isFocused).
		add("element", this.layoutDescriptor.getCssElement()).
		toString();
	}
}
