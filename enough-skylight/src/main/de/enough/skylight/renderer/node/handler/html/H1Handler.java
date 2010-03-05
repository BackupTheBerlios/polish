package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;

public class H1Handler extends BodyNodeHandler{
	
	public String getTag() {
		return "h1";
	}
	
	public void handleNode(CssElement element) {}
	
	public Style getDefaultStyle(CssElement element) {
		//#style h1
		return new Style();
	}

	public Item createContent(CssElement element) {
		return null;
	}
}
