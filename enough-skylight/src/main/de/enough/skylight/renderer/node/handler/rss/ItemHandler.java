package de.enough.skylight.renderer.node.handler.rss;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class ItemHandler extends NodeHandler {

	public Item createContent(CssElement element) {
		return null;
	}

	public String getTag() {
		return "item";
	}
	
	public boolean isValid(DomNode node) {
		return NodeUtils.getAncestor(node, "channel") != null;
	}
	
	public void handleNode(CssElement element) throws ClassCastException,
			IllegalArgumentException {
	}

	public Style getDefaultStyle() {
		//#style item
		return new Style();
	}

}
