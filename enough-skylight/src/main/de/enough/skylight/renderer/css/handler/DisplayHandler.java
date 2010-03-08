package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;



public class DisplayHandler extends CssAttributeHandler {
	
	protected String getName() {
		return "display";
	}
	
	protected int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.Display.BLOCK_LEVEL,
							HtmlCssElement.Display.INLINE};
	}

	protected void addAttribute(Style style, Object value) {
		style.addAttribute("display", value);
	}
}
