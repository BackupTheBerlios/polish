package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class H1Handler extends NodeHandler{
	
	public String getTag() {
		return "h1";
	}
	
	public void handleNode(DomNode node) {}
	
	public Style getDefaultStyle() {
		//#style h1
		return new Style();
	}

	public Item createContent(DomNode node, Style style) {
		return null;
	}

}
