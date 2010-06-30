package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;



public class ClearHandler extends CssAttributeHandler {
	
	protected String getName() {
		return "clear";
	}
	
	protected int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.Clear.LEFT,
							HtmlCssElement.Clear.RIGHT,
							HtmlCssElement.Clear.NONE,
                                                        HtmlCssElement.Clear.BOTH};
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("clear", value);
	}
}
