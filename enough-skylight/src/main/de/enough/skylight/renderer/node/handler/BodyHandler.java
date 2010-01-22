package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class BodyHandler extends NodeHandler{

	public String getTag() {
		return "body";
	}
	
	public Style getDefaultStyle() {
		//#style body
		return new Style();
	}

	public void handleNode(NodeElement element) {
	}

	public Item createContent(NodeElement element) {
		return null;
	}
}
