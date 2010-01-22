package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class TitleHandler extends NodeHandler{
	
	public String getTag() {
		return "title";
	}
	
	public void handleNode(NodeElement element) {
		DomNode firstChild = element.getNode().getFirstChild();
		
		if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
			String text = firstChild.getNodeValue();
			element.getViewport().setTitle(text);
		}
	}
	
	public Item createContent(NodeElement element) {
		return null;
	}

}
