package de.enough.skylight.renderer.element.view;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.ContainerView;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.layout.LayoutDescriptor;
import de.enough.skylight.renderer.linebox.LineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.PartitionList;

public class ContainingBlockView extends ContainerView implements LayoutDescriptor{
	
	ContainingBlock parentContainingBlock = null;
	
	CssElement cssElement = null;
	
	ContainingBlock containingBlock = null;
	
	BlockContainingBlock block = null;
	
	LineboxList lineboxes = new LineboxList();
	
	PartitionList partitions = new PartitionList();
	
	int inlineRelativeOffset = 0;
	
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

	public LineboxList getLineboxes() {
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
}
