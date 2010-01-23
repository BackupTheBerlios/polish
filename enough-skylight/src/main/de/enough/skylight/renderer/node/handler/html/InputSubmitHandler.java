package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputSubmitHandler extends BodyNodeHandler {
	public String getTag() {
		return null;
	}
	
	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		DomNode node = element.getNode();
		String value = NodeUtils.getAttributeValue(node, "value");
		
		if(value == null) {
			value = Locale.get("default.submit");
		} 
		
		Style style = element.getStyle();
		StringItem submitItem = new StringItem(null,value, style);
		
		submitItem.setAppearanceMode(Item.INTERACTIVE);
		
		return submitItem;
	}
}
