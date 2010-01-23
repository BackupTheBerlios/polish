package de.enough.skylight.renderer.node.handler.html;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;

public class TitleHandler extends HeadElementHandler {
	
	public String getTag() {
		return "title";
	}
	
	public void handleNode(CssElement element) {
		DomNode firstChild = element.getNode().getFirstChild();
		
		if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
			String text = firstChild.getNodeValue();
			element.getViewport().setTitle(text);
		}
	}
}
