package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.view.BlockContainingBlockView;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.linebox.Linebox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends ContainingBlock {
	
	BlockContainingBlockView blockView;
	
	protected InlineContainingBlock body;

	protected InlineContainingBlock floatLeft;
	
	protected InlineContainingBlock floatRight;
	
	CssElement element;
	
	ContainingBlock containingBlock;
	
	BlockContainingBlock block;
	
	Partition elementPartition;
	
	public BlockContainingBlock(CssElement element) {
		this(element,element.getStyle());
	}
	
	public BlockContainingBlock(CssElement element, Style style) {
		super( false, style );
		
		this.element = element;
		
		this.blockView = new BlockContainingBlockView(this);
		setView(this.blockView);
		
		//#style element
		this.body = new InlineContainingBlock();
		LayoutAttributes attributes = LayoutAttributes.get(this.body);
		attributes.setElement(null);
		attributes.setContainingBlock(this);
		attributes.setBlock(this);
		
		add(this.body);
	}
	
	public void addToBody(Item item) {
		this.body.add(item);
		LayoutAttributes.get(item).setContainingBlock(this.body);
	}
	
	public void addToLeftFloat(Item item) {
		if(this.floatLeft == null) {
			//#style element
			this.floatLeft = new InlineContainingBlock();
			
			LayoutAttributes attributes = LayoutAttributes.get(this.floatLeft);
			attributes.setElement(null);
			attributes.setContainingBlock(this);
			attributes.setBlock(this);
			
			add(this.floatLeft);
		}
		
		this.floatLeft.add(item);
		LayoutAttributes.get(item).setContainingBlock(this.floatLeft);
	}
	
	public void addToRightFloat(Item item) {
		if(this.floatRight == null) {
			//#style element
			this.floatRight = new InlineContainingBlock();
			
			LayoutAttributes attributes = LayoutAttributes.get(this.floatRight);
			attributes.setElement(null);
			attributes.setContainingBlock(this);
			attributes.setBlock(this);
			
			add(this.floatRight);
		}
		
		this.floatRight.add(item);
		LayoutAttributes.get(item).setContainingBlock(this.floatRight);
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
	
	public Linebox getPaintLineBox() {
		return this.blockView.getPaintLineBox();
	}
//	
	public String toString() {
		return new ToStringHelper("BlockContainingBlock").
		add("focused", this.isFocused).
		add("element", LayoutAttributes.get(this).getElement()).
		toString();
	}
}
