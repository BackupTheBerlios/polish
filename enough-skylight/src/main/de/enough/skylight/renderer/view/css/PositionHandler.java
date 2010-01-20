package de.enough.skylight.renderer.view.css;

import de.enough.polish.ui.Style;



public class PositionHandler extends CssAttributeHandler {
	
	public String getName() {
		return "float";
	}
	
	int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	Object[] getRange() {
		return new Object[]{CssElement.Position.STATIC,
							CssElement.Position.ABSOLUTE,
							CssElement.Position.RELATIVE};
	}
	
	void addAttribute(Style style, Object value) {
		style.addAttribute("float", value);
	}
}
