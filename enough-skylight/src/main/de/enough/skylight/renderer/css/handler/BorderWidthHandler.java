package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class BorderWidthHandler extends AttributeHandler {

	protected String getName() {
		return "border-width";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("border-width", value);
	}
}
