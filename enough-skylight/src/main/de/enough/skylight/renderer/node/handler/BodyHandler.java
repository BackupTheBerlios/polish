package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class BodyHandler extends NodeHandler{

	public String getTag() {
		return "body";
	}
	
	public Style getDefaultStyle() {
		//#style body
		return new Style();
	}

	public void handleNode(DomNode node) {
	}

	public Item createContent(DomNode node, Style style) {
		return null;
	}
}
