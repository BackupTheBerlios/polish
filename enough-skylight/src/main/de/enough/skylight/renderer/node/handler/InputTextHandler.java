package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputTextHandler extends NodeHandler {
	public String getTag() {
		return null;
	}

	public Item createContent(DomNode node, Style style) {
		String value = NodeUtils.getAttributeValue(node, "value");

		TextField textfield = new TextField(null, value, 512, TextField.ANY, style);

		return textfield;
	}

	public void handleNode(DomNode node) {
	}
}
