package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeUtils;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class InputTextHandler extends NodeHandler{
	public Item createNodeItem(DomNode node) {
		String value = NodeUtils.getAttributeValue(node, "value");
		
		TextField textfield = new TextField(null,value,512,TextField.ANY);
		
		return textfield;
	}

	public void handleNode(DomNode node) {}
	
	
}
