package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class MaxHeightHandler extends CssAttributeHandler {

	protected String getName() {
		return "max-height";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("max-height", value);
	}
}
