package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;

public class DivHandler extends BodyNodeHandler{
	
	public String getTag() {
		return "div";
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style div
		return new Style();
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}	
}
