package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputTextHandler extends BodyNodeHandler {
	public String getTag() {
		return null;
	}

	public Item createContent(CssElement element) {
		DomNode node = element.getNode();
		String value = NodeUtils.getAttributeValue(node, "value");

		Style style = element.getStyle();
		TextField textfield = new TextField(null, value, 512, TextField.ANY, style);

		return textfield;
	}
	
	public void handleNode(CssElement element) {
	}

	public Style getDefaultStyle() {
		//#style input_text
		return new Style();
	}
}
