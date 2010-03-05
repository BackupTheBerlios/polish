package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.element.Break;
import de.enough.skylight.renderer.node.CssElement;

public class BrHandler extends BodyNodeHandler {
	public String getTag() {
		return "br";
	}
	
	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return new Break();
	}
}
