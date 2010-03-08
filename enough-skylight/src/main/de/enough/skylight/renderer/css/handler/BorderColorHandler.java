package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;

public class BorderColorHandler extends CssAttributeHandler {

	protected String getName() {
		return "border-color";
	}

	protected int getType() {
		return TYPE_COLOR;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("border-color", value);
	}
}
