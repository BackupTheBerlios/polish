package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputTextHandler extends NodeHandler {
	public String getTag() {
		return null;
	}

	public Item createContent(NodeElement element) {
		DomNode node = element.getNode();
		String value = NodeUtils.getAttributeValue(node, "value");

		Style style = element.getStyle();
		TextField textfield = new TextField(null, value, 512, TextField.ANY, style);

		return textfield;
	}
	
	public void handleNode(NodeElement element) {
	}

	public Style getDefaultStyle() {
		//#style input_text
		return new Style();
	}
}
