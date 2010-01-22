package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class PaddingRightHandler extends AttributeHandler {

	protected String getName() {
		return "padding-right";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("padding-right", value);
	}
}
