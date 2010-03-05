package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;

public class PHandler extends BodyNodeHandler {
	
	public String getTag() {
		return "p";
	}
	
	public void handleNode(CssElement element) {}

	public Style getDefaultStyle(CssElement element) {
		//#style p
		return new Style();
	}

	public Item createContent(CssElement element) {
		return null;
	}
}
