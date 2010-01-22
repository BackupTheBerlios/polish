package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class SpanHandler extends NodeHandler{
	
	public String getTag() {
		return "span";
	}
	
	public void handleNode(DomNode node) {}
	
	public Style getDefaultStyle() {
		//#style span
		return new Style();
	}

	public Item createContent(DomNode node, Style style) {
		return null;
	}

}
