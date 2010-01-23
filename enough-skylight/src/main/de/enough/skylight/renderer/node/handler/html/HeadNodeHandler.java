package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class HeadNodeHandler extends HtmlNodeHandler {

	public boolean isValid(DomNode node) {
		return super.isValid(node) && NodeUtils.getAncestor(node, "head") != null; 
	}

	public Item createContent(CssElement element) {
		return null;
	}
	
	
}
