package de.enough.skylight.renderer.view.css;

import de.enough.polish.ui.Style;



public class FloatHandler extends CssAttributeHandler {
	
	public String getName() {
		return "float";
	}
	
	int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	Object[] getRange() {
		return new Object[]{CssElement.Float.LEFT,
							CssElement.Float.RIGHT,
							CssElement.Float.NONE};
	}
	
	void addAttribute(Style style, Object value) {
		style.addAttribute("float", value);
	}
}
