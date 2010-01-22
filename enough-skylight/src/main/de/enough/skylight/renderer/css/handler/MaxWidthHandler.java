package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class MaxWidthHandler extends AttributeHandler {

	protected String getName() {
		return "max-width";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("max-width", value);
	}
}
