package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;

public class TextAlignHandler extends CssAttributeHandler {
	
	protected String getName() {
		return "text-align";
	}
	
	protected int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.TextAlign.LEFT,
							HtmlCssElement.TextAlign.CENTER,
							HtmlCssElement.TextAlign.RIGHT};
	}
	
	protected void addAttribute(Style style, Object value) {
		style.addAttribute("text-align", value);
	}

}
