package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;

public class UlHandler extends BodyNodeHandler{
	
	public String getTag() {
		return "ul";
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
