package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class WidthHandler extends CssAttributeHandler {

	protected String getName() {
		return "width";
	}
	
	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("width", value);
	}	
}
