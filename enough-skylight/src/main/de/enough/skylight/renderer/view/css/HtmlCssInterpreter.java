package de.enough.skylight.renderer.view.css;

import java.io.Reader;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.view.element.CssElement;

public class HtmlCssInterpreter extends CssInterpreter{

	public HtmlCssInterpreter(Reader reader) {
		super(reader);
	}

	protected void addAttribute(String name, String value, Style style) {
		super.addAttribute(name, value, style);
		
		//#if polish.css.display
		if ("display".equals(name)) {
			if(value.equals("block")) {
				style.addAttribute("display", CssElement.Display.BLOCK_LEVEL);
			} else if(value.equals(CssElement.Display.INLINE)) {
				style.addAttribute("display", CssElement.Display.INLINE);
			}
			return;
		}
		//#endif
		
		//#if polish.css.display
		if ("position".equals(name)) {
			if(value.equals(CssElement.Position.STATIC)) {
				style.addAttribute("position", CssElement.Position.STATIC);
			} else if(value.equals(CssElement.Position.ABSOLUTE)) {
				style.addAttribute("position", CssElement.Position.ABSOLUTE);
			} else if(value.equals(CssElement.Position.RELATIVE)) {
				style.addAttribute("position", CssElement.Position.RELATIVE);
			}
			return;
		}
		//#endif
		
		//#if polish.css.float
		if ("float".equals(name)) {
			if(value.equals(CssElement.Float.NONE)) {
				style.addAttribute("float", CssElement.Float.NONE);
			} else if(value.equals(CssElement.Float.LEFT)) {
				style.addAttribute("float", CssElement.Float.LEFT);
			} else if(value.equals(CssElement.Float.RIGHT)) {
				style.addAttribute("float", CssElement.Float.RIGHT);
			}
			return;
		}
		//#endif
	}
}
