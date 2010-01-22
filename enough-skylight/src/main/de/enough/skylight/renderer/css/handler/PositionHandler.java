package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;



public class PositionHandler extends AttributeHandler {
	
	public String getName() {
		return "float";
	}
	
	protected int getType() {
		return AttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.Position.STATIC,
							HtmlCssElement.Position.ABSOLUTE,
							HtmlCssElement.Position.RELATIVE};
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("float", value);
	}
}
