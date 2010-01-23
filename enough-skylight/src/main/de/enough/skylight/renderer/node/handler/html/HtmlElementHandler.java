package de.enough.skylight.renderer.node.handler.html;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class HtmlElementHandler extends NodeHandler {

	public boolean isValid(DomNode node) {
		return NodeUtils.getAncestor(node, "html") != null; 
	}
}
