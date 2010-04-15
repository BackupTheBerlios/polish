package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;

public class TitleHandler extends HeadNodeHandler {
	
	public String getTag() {
		return "title";
	}
	
	public void handleNode(CssElement element) {
		DomNode firstChild = element.getNode().getFirstChild();
		
		if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
			String text = firstChild.getNodeValue();
			element.getViewportContext().setTitle(text);
		}
	}
	
	public void setContent(CssElement element, Item item) {}
}
