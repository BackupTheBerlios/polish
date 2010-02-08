package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.InputPasswordField;
import de.enough.skylight.renderer.element.InputTextField;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputPasswordHandler extends BodyNodeHandler {
	public String getTag() {
		return null;
	}

	public Item createContent(CssElement element) {
		InputPasswordField field = new InputPasswordField();
		
		setContent(element, field);

		return field;
	}
	
	public void handleNode(CssElement element) {
	}

	public Style getDefaultStyle(CssElement element) {
		//#style input_text
		return new Style();
	}

	public void setContent(CssElement element, Item item) {
		InputPasswordField field = (InputPasswordField)item; 
		
		DomNode node = element.getNode();
		String value = NodeUtils.getAttributeValue(node, "value");

		Style style = element.getStyle();

		field.setStyle(style);
		field.setString(value);
	}
}
