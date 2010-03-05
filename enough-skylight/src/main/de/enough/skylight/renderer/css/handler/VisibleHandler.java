package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;



public class VisibleHandler extends AttributeHandler {
	
	public String getName() {
		return "visible";
	}
	
	protected int getType() {
		return AttributeHandler.TYPE_BOOLEAN;
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("visible", value);
	}
}
