package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class BodyHandler extends HtmlNodeHandler {

	public String getTag() {
		return "body";
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style body
		return new Style();
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}
	
	public void setContent(CssElement element, Item item) {
	}
}
