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

	protected InlineContainingBlock floatLeft;
	
	protected InlineContainingBlock floatRight;
	
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
	
	public void addToLeftFloat(Item item) {
		if(this.floatLeft == null) {
			//#style element
			this.floatLeft = new InlineContainingBlock();
			
			LayoutDescriptor layoutElement = this.floatLeft.getLayoutDescriptor();
			layoutElement.setCssElement(null);
			layoutElement.setContainingBlock(this);
			layoutElement.setBlock(this);
			layoutElement.setViewport(getLayoutDescriptor().getViewport());
			add(this.floatLeft);
		}
		
		this.floatLeft.add(item);
		LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
		descriptor.setContainingBlock(this.floatLeft);
	}
	
	public void addToRightFloat(Item item) {
		if(this.floatRight == null) {
			//#style element
			this.floatRight = new InlineContainingBlock();
			
			LayoutDescriptor layoutElement = this.floatRight.getLayoutDescriptor();
			layoutElement.setCssElement(null);
			layoutElement.setContainingBlock(this);
			layoutElement.setBlock(this);
			layoutElement.setViewport(getLayoutDescriptor().getViewport());
			add(this.floatRight);
		}
		
		this.floatRight.add(item);
		LayoutDescriptor descriptor = ContentView.getLayoutDescriptor(item);
		descriptor.setContainingBlock(this.floatRight);
	}
	
	public InlineContainingBlock getBody() {
		return this.body;
	}
	
	public InlineContainingBlock getLeftFloat() {
		return this.floatLeft;
	}
	
	public InlineContainingBlock getRightFloat() {
		return this.floatRight;
	}
	
	public void partition(PartitionList partitions) {
		Partition.partitionBlock(this,partitions);
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
