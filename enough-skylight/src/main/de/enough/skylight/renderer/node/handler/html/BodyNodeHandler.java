package de.enough.skylight.renderer.node.handler.html;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class BodyNodeHandler extends HtmlNodeHandler {

	public boolean isValid(DomNode node) {
		return super.isValid(node) && NodeUtils.getAncestor(node, "body") != null; 
	}
}
