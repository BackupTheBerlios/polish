package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;

public class FormHandler extends BodyNodeHandler{

	public String getTag() {
		return "form";
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style form
		return new Style();
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}
}
