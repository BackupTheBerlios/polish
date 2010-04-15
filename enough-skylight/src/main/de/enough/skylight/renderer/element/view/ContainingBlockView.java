package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.PartitionList;

public abstract class ContainingBlockView extends ContainerView implements LayoutDescriptor{
	ContainingBlock parentContainingBlock = null;
	
	CssElement cssElement = null;
	
	ContainingBlock containingBlock = null;
	
	BlockContainingBlock block = null;
	
	InlineLineboxList lineboxes = new InlineLineboxList();
	
	PartitionList partitions = new PartitionList();
	
	int inlineRelativeOffset = 0;
	
	Viewport viewport;
	
	public ContainingBlockView(ContainingBlock parent) {
		this.parentContainer = parent;
		this.parentItem = parent;
		this.parentContainingBlock = parent;
	}
	
	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = false;

		if (gameAction == Canvas.FIRE) {
			handled = ContentView.handleOnClick(this.cssElement);
		}
		
		if(!handled) {
			handled = super.handleKeyReleased(keyCode, gameAction);
		}
		
		return handled;
	}

	public boolean handlePointerReleased(int x, int y) {
		boolean handled = ContentView.handleOnClick(this.cssElement);
		
		if(!handled) {
			handled = super.handlePointerReleased(x, y);
		}
		
		return handled;
	}
	
	public BlockContainingBlock getBlock() {
		return this.block;
	}

	public ContainingBlock getContainingBlock() {
		return this.containingBlock;
	}

	public CssElement getCssElement() {
		return this.cssElement;
	}

	public int getInlineRelativeOffset() {
		return this.inlineRelativeOffset;
	}

	public InlineLineboxList getLineboxes() {
		return this.lineboxes;
	}

	public PartitionList getPartitions() {
		return this.partitions;
	}

	public void setBlock(BlockContainingBlock block) {
		this.block = block;
	}

	public void setContainingBlock(ContainingBlock containingBlock) {
		this.containingBlock = containingBlock;
	}

	public void setCssElement(CssElement cssElement) {
		this.cssElement =  cssElement;
	}

	public void setInlineRelativeOffset(int inlineRelativeLeft) {
		this.inlineRelativeOffset = inlineRelativeLeft;
	}

	public Viewport getViewport() {
		return this.viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}	
}
