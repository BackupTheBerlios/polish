package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.Break;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class BrHandler extends NodeHandler{
	public String getTag() {
		return "br";
	}
	
	public void handleNode(NodeElement element) {}

	public Item createContent(NodeElement element) {
		return new Break();
	}
	
	
}
