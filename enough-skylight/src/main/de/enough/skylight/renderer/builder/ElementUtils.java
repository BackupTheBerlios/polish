package de.enough.skylight.renderer.builder;

import de.enough.skylight.renderer.css.HtmlCssElement;

public class ElementUtils {
	public static boolean isFloat(Element element) {
		return !element.isFloat(HtmlCssElement.Float.NONE);
	}
}
