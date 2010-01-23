package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class PHandler extends BodyElementHandler {
	
	public String getTag() {
		return "p";
	}
	
	public void handleNode(CssElement element) {}

	public Style getDefaultStyle() {
		//#style p
		return new Style();
	}

	public Item createContent(CssElement element) {
		return null;
	}

}
