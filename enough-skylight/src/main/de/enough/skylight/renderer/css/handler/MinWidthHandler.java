package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class MinWidthHandler extends CssAttributeHandler {

	protected String getName() {
		return "min-width";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("min-width", value);
	}
}
