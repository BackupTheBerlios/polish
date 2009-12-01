package de.enough.skylight.renderer.view.css;

import java.io.Reader;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.view.element.Element;

public class HtmlCssInterpreter extends CssInterpreter{

	public HtmlCssInterpreter(Reader reader) {
		super(reader);
	}

	protected void addAttribute(String name, String value, Style style) {
		super.addAttribute(name, value, style);
		
		//#if polish.css.display
		if ("display".equals(name)) {
			if(value.equals("block")) {
				style.addAttribute("display", Element.Display.BLOCK_LEVEL);
			} else if(value.equals(Element.Display.INLINE)) {
				style.addAttribute("display", Element.Display.INLINE);
			}
			return;
		}
		//#endif
		
		//#if polish.css.display
		if ("position".equals(name)) {
			if(value.equals(Element.Position.STATIC)) {
				style.addAttribute("position", Element.Position.STATIC);
			} else if(value.equals(Element.Position.ABSOLUTE)) {
				style.addAttribute("position", Element.Position.ABSOLUTE);
			} else if(value.equals(Element.Position.RELATIVE)) {
				style.addAttribute("position", Element.Position.RELATIVE);
			}
			return;
		}
		//#endif
		
		//#if polish.css.float
		if ("float".equals(name)) {
			if(value.equals(Element.Float.NONE)) {
				style.addAttribute("float", Element.Float.NONE);
			} else if(value.equals(Element.Float.LEFT)) {
				style.addAttribute("float", Element.Float.LEFT);
			} else if(value.equals(Element.Float.RIGHT)) {
				style.addAttribute("float", Element.Float.RIGHT);
			}
			return;
		}
		//#endif
	}
}
