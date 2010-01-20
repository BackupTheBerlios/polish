package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.Locale;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputSubmitHandler extends NodeHandler{
	public String getTag() {
		return null;
	}
	
	public void handleNode(DomNode node) {}

	public Item createContent(DomNode node) {
		String value = NodeUtils.getAttributeValue(node, "value");
		
		if(value == null) {
			value = Locale.get("default.submit");
		} 
		
		StringItem submitItem = new StringItem(null,value);
		
		submitItem.setAppearanceMode(Item.INTERACTIVE);
		
		return submitItem;
	}
}
