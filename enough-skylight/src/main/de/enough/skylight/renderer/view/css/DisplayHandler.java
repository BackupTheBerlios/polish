package de.enough.skylight.renderer.view.css;

import de.enough.polish.ui.Style;



public class DisplayHandler extends CssAttributeHandler {
	
	public String getName() {
		return "display";
	}
	
	int getType() {
		return CssAttributeHandler.TYPE_RANGE;
	}

	Object[] getRange() {
		return new Object[]{CssElement.Display.BLOCK_LEVEL,
							CssElement.Display.INLINE};
	}

	void addAttribute(Style style, Object value) {
		style.addAttribute("display", value);
	}
}
