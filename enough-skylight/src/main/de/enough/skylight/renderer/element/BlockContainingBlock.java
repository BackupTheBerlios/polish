package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.view.BlockContainingBlockView;
import de.enough.skylight.renderer.element.view.ContainingBlockView;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends ContainingBlock {
	
	InlineContainingBlock currentInline;
	
	public BlockContainingBlock() {
		this(null);
	}
	
	public BlockContainingBlock(Style style) {
		super( style );
	}
	
	ContainingBlockView buildView() {
		return new BlockContainingBlockView(this);
	}
	
	public void addInline(Item item) {
		if(this.currentInline == null) {
			this.currentInline = createInlineContainingBlock();
			add(this.currentInline);
		}
		
		this.currentInline.add(item);
	}

	
	public void addBlock(Item item) {
		add(item);
		this.currentInline = null;
	}
	
	public void addLeftFloat(Item item) {
		addBlock(item);
	}
	
	public void addRightFloat(Item item) {
		addBlock(item);
	}
	
	public InlineContainingBlock createInlineContainingBlock() {
		//#style element
		InlineContainingBlock inlineContainingBlock = new InlineContainingBlock();
		LayoutDescriptor layoutDescriptor = (LayoutDescriptor)inlineContainingBlock.getContainingBlockView();
		
		layoutDescriptor.setContainingBlock(this);
		layoutDescriptor.setBlock(this);
		layoutDescriptor.setViewport(getLayoutDescriptor().getViewport());
		
		return inlineContainingBlock;
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
		set("hashcode", this.hashCode()).
		set("focused", this.isFocused).
		set("element", this.layoutDescriptor.getCssElement()).
		toString();
	}
}
