package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class BrHandler extends NodeHandler{
	
	public Style getDefaultStyle() {
		//#style br
		return new Style();
	}
	
	public void handleNode(DomNode node) {
	}

	public Item createNodeItem(DomNode node) {
		return new StringItem(null,null);
	}
	
	public int getType() {
		// TODO Auto-generated method stub
		return CssElement.Type.ELEMENT;
	}
}
