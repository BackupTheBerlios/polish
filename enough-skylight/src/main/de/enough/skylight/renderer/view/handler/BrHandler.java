package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.Break;
import de.enough.skylight.renderer.view.css.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class BrHandler extends NodeHandler{
	public String getTag() {
		return "br";
	}
	
	public void handleNode(DomNode node) {}

	public Item createContent(DomNode node) {
		return new Break();
	}
	
	
}
