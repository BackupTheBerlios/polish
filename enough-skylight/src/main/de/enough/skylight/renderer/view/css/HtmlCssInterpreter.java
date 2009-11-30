package de.enough.skylight.renderer.view.css;

import java.io.Reader;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.view.element.ContainingBlock;

public class HtmlCssInterpreter extends CssInterpreter{

	public HtmlCssInterpreter(Reader reader) {
		super(reader);
	}

	protected void addAttribute(String name, String value, Style style) {
		super.addAttribute(name, value, style);
		
		//#if polish.css.display
		if ("display".equals(name)) {
			if(value.equals("block")) {
				style.addAttribute("display", ContainingBlock.DISPLAY_BLOCK);
			} else if(value.equals("inline")) {
				style.addAttribute("display", ContainingBlock.DISPLAY_INLINE);
			}
			return;
		}
		//#endif
	}
}
