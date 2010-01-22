package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class TitleHandler extends NodeHandler{
	
	public String getTag() {
		return "title";
	}
	
	public void handleNode(DomNode node) {
		DomNode firstChild = node.getFirstChild();
		
		if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
			String text = firstChild.getNodeValue();
			getViewport().setTitle(text);
		}
	}
	
	public Item createContent(DomNode node, Style style) {
		return null;
	}

}
