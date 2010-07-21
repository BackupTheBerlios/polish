package de.enough.skylight.renderer.element;

import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.renderer.element.view.ContainingBlockView;
import de.enough.skylight.renderer.element.view.FloatContainingBlockView;

public class FloatContainingBlock extends BlockContainingBlock {
	
	public FloatContainingBlock() {
		this(null);
	}
	
	public FloatContainingBlock(Style style) {
		super( style );
	}
	
	ContainingBlockView buildView() {
		return new FloatContainingBlockView(this);
	}
	
	public String toString() {
		return new ToStringHelper("FloatContainingBlock").
		set("hashcode", this.hashCode()).
		set("focused", this.isFocused).
		set("element", this.layoutDescriptor.getCssElement()).
		toString();
	}
}
