package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class TitleHandler extends NodeHandler{
	
	public void handleNode(DomNode node) {
		DomNode firstChild = node.getFirstChild();
		
		if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
			String text = firstChild.getNodeValue();
			getViewport().setTitle(text);
		}
	}
	
	public int getType() {
		return CssElement.Type.ELEMENT;
	}
}
