package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class PHandler extends NodeHandler{
	
	public String getTag() {
		return "p";
	}
	
	public void handleNode(NodeElement element) {}

	public Style getDefaultStyle() {
		//#style p
		return new Style();
	}

	public Item createContent(NodeElement element) {
		return null;
	}

}
