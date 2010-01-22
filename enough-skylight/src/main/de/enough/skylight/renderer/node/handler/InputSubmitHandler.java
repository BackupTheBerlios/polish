package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputSubmitHandler extends NodeHandler{
	public String getTag() {
		return null;
	}
	
	public void handleNode(NodeElement element) {}

	public Item createContent(NodeElement element) {
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
