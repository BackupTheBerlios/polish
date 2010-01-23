package de.enough.skylight.renderer.node.handler.rss;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class ItemNodeHandler extends NodeHandler {
	public void handleNode(CssElement element) throws ClassCastException,
			IllegalArgumentException {
	}
	
	public boolean isValid(DomNode node) {
		return NodeUtils.getAncestor(node, "item") != null;
	}
}
