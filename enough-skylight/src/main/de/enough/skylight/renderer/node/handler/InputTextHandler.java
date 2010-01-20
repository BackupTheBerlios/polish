package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputTextHandler extends NodeHandler {
	public String getTag() {
		return null;
	}

	public Item createContent(DomNode node) {
		String value = NodeUtils.getAttributeValue(node, "value");

		TextField textfield = new TextField(null, value, 512, TextField.ANY);

		return textfield;
	}

	public void handleNode(DomNode node) {
	}
}
