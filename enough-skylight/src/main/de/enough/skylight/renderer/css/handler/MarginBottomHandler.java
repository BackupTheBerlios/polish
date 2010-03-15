package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class MarginBottomHandler extends CssAttributeHandler {

	protected String getName() {
		return "margin-bottom";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("margin-bottom", value);
	}
}
