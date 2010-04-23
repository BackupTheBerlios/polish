package de.enough.skylight.renderer.element.view;

import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.layout.block.InlineLayout;

public class FloatContainingBlockView extends BlockContainingBlockView {

	public FloatContainingBlockView(BlockContainingBlock parent) {
		super(parent);
	}

	public void setContentDimension(int availWidth, InlineLayout bodyLayout) {
		this.contentWidth = bodyLayout.getLayoutWidth();
		this.contentHeight = bodyLayout.getLayoutHeight();
	}
}
