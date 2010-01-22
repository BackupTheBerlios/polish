package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class EmHandler extends NodeHandler{
	
	public String getTag() {
		return "em";
	}
	
	public Style getDefaultStyle() {
		//#style em
		return new Style();
	}

	public void handleNode(DomNode node) {}

	public Item createContent(DomNode node, Style style) {
		return null;
	}
}
