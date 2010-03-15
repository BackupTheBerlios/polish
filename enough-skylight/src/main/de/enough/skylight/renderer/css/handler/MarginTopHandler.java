package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class MarginTopHandler extends CssAttributeHandler {

	protected String getName() {
		return "margin-top";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("margin-top", value);
	}
}
