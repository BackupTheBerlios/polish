package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class PHandler extends NodeHandler{
	
	public String getTag() {
		return "p";
	}
	
	public void handleNode(DomNode node) {}

	public Style getDefaultStyle() {
		//#style p
		return new Style();
	}

	public Item createContent(DomNode node) {
		return null;
	}

}
