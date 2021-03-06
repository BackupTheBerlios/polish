package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;

public class SpanHandler extends BodyNodeHandler {
	
	public String getTag() {
		return "span";
	}
	
	public void handleNode(CssElement element) {}
	
	public Style getDefaultStyle(CssElement element) {
		//#style span
		return new Style();
	}

	public Item createContent(CssElement element) {
		return null;
	}
}
