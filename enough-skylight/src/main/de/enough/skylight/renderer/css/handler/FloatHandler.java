package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;



public class FloatHandler extends CssAttributeHandler {
	
	protected String getName() {
		return "float";
	}
	
	protected int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.Float.LEFT,
							HtmlCssElement.Float.RIGHT,
							HtmlCssElement.Float.NONE};
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("float", value);
	}
}
