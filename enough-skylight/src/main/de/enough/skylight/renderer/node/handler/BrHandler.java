package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.Break;

public class BrHandler extends NodeHandler{
	public String getTag() {
		return "br";
	}
	
	public void handleNode(DomNode node) {}

	public Item createContent(DomNode node, Style style) {
		return new Break();
	}
	
	
}
