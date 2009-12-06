package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class BrHandler extends NodeHandler{
	public void handleNode(DomNode node) {}
	
	public int getType() {
		// TODO Auto-generated method stub
		return CssElement.Type.BREAK;
	}
}
