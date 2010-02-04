package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class HtmlNodeHandler extends NodeHandler {

	public boolean isValid(DomNode node) {
		return NodeUtils.getAncestor(node, "html") != null; 
	}
}
