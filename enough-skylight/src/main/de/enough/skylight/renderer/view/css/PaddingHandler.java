package de.enough.skylight.renderer.view.css;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.polish.util.TextUtil;

public class PaddingHandler extends CssAttributeHandler {

	String getName() {
		return "padding";
	}

	int getType() {
		return TYPE_DIMENSION;
	}
	
	void addAttribute(Style style, Object value) {
		// unused
	}

	protected void addToStyle(Style style, String text) {
		String[] values = TextUtil.split(text, ' ');
		if (values.length == 1) {
			style.addAttribute("padding", new Dimension(text));
		} else if (values.length == 2) {
			Dimension vertical = new Dimension( values[0]);
			Dimension horizontal = new Dimension( values[1]);
			style.addAttribute("padding-top", vertical);
			style.addAttribute("padding-right", horizontal);
			style.addAttribute("padding-bottom", vertical);
			style.addAttribute("padding-left", horizontal);
		} else if (values.length == 3) {
			Dimension horizontal = new Dimension( values[1]);
			style.addAttribute("padding-top", new Dimension( values[0]));
			style.addAttribute("padding-right", horizontal);
			style.addAttribute("padding-bottom", new Dimension( values[2]));
			style.addAttribute("padding-left", horizontal);
		} else {
			style.addAttribute("padding-top", new Dimension( values[0]));
			style.addAttribute("padding-right", new Dimension( values[1]));
			style.addAttribute("padding-bottom", new Dimension( values[2]));
			style.addAttribute("padding-left", new Dimension( values[3]));
			
		}
	}
}
