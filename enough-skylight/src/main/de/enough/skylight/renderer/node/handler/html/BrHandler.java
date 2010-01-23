package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.Break;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class BrHandler extends BodyElementHandler {
	public String getTag() {
		return "br";
	}
	
	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return new Break();
	}
	
	
}
