package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.polish.util.TextUtil;

public class MarginHandler extends CssAttributeHandler {

	protected String getName() {
		return "margin";
	}

	protected int getType() {
		return TYPE_DIMENSION;
	}
	
	protected void addAttribute(Style style, Object value) {
		// unused
	}

	public void addToStyle(Style style, String text) {
		String[] values = TextUtil.split(text, ' ');
		if (values.length == 1) {
			//style.addAttribute("margin", new Dimension(text));
                        style.addAttribute("margin-top", new Dimension(text));
                        style.addAttribute("margin-left", new Dimension(text));
                        style.addAttribute("margin-right", new Dimension(text));
                        style.addAttribute("margin-bottom", new Dimension(text));
		} else if (values.length == 2) {
			Dimension vertical = new Dimension( values[0]);
			Dimension horizontal = new Dimension( values[1]);
			style.addAttribute("margin-top", vertical);
			style.addAttribute("margin-right", horizontal);
			style.addAttribute("margin-bottom", vertical);
			style.addAttribute("margin-left", horizontal);
		} else if (values.length == 3) {
			Dimension horizontal = new Dimension( values[1]);
			style.addAttribute("margin-top", new Dimension( values[0]));
			style.addAttribute("margin-right", horizontal);
			style.addAttribute("margin-bottom", new Dimension( values[2]));
			style.addAttribute("margin-left", horizontal);
		} else {
			style.addAttribute("margin-top", new Dimension( values[0]));
			style.addAttribute("margin-right", new Dimension( values[1]));
			style.addAttribute("margin-bottom", new Dimension( values[2]));
			style.addAttribute("margin-left", new Dimension( values[3]));
		}
	}
}
