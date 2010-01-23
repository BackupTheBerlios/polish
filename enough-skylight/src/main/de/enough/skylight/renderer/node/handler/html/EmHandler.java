package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class EmHandler extends BodyElementHandler{
	
	public String getTag() {
		return "em";
	}
	
	public Style getDefaultStyle() {
		//#style em
		return new Style();
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}
}
