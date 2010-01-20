package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.css.HtmlCssElement.Display;



public class DisplayHandler extends AttributeHandler {
	
	protected String getName() {
		return "display";
	}
	
	protected int getType() {
		return AttributeHandler.TYPE_RANGE;
	}

	protected Object[] getRange() {
		return new Object[]{HtmlCssElement.Display.BLOCK_LEVEL,
							HtmlCssElement.Display.INLINE};
	}

	protected void addAttribute(Style style, Object value) {
		style.addAttribute("display", value);
	}
}
