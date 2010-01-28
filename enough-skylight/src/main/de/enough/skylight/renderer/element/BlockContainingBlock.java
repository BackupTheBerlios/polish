package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.element.view.BlockContainingBlockView;
import de.enough.skylight.renderer.linebox.LineBox;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partable;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockContainingBlock extends Container implements ContainingBlock, Partable {
	
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
		ElementAttributes.set(this.body,null,this,this);
		
		add(this.body);
	}
	
	public void addToBody(Item item) {
		this.body.add(item);
		ElementAttributes.setContainingBlock(item, this.body);
	}
	
	public void addToLeftFloat(Item item) {
		if(this.floatLeft == null) {
			//#style element
			this.floatLeft = new InlineContainingBlock();
			ElementAttributes.set(this.floatLeft,null,this,this);
			add(this.floatLeft);
		}
		
		this.floatLeft.add(item);
		ElementAttributes.setContainingBlock(item, this.floatLeft);
	}
	
	public void addToRightFloat(Item item) {
		if(this.floatRight == null) {
			//#style element
			this.floatRight = new InlineContainingBlock();
			ElementAttributes.set(this.floatRight,null,this,this);
			add(this.floatRight);
		}
		
		this.floatRight.add(item);
		ElementAttributes.setContainingBlock(item, this.floatRight);
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
		Partition partition = Partition.partitionBlock(this);
		partitions.add(partition);
	}
	
	public Container getContainer() {
		return this;
	}
	
	public LineBox getPaintLineBox() {
		return this.blockView.getPaintLineBox();
	}
}
