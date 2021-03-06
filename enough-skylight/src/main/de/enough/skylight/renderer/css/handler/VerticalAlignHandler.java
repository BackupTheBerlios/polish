package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;

public class VerticalAlignHandler extends CssAttributeHandler {
	
	protected String getName() {
		return "vertical-align";
	}
	
	protected int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.VerticalAlign.TOP,
							HtmlCssElement.VerticalAlign.MIDDLE,
							HtmlCssElement.VerticalAlign.BOTTOM};
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("vertical-align", value);
	}

}
