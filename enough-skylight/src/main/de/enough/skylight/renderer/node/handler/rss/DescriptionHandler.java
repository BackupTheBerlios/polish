package de.enough.skylight.renderer.node.handler.rss;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class DescriptionHandler extends ItemNodeHandler {

	public Item createContent(CssElement element) {
		return null;
	}

	public String getTag() {
		return "description";
	}
	
	public Style getDefaultStyle() {
		//#style description
		return new Style();
	}
}
