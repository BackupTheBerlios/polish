package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.AttributeUtils;
import de.enough.skylight.renderer.view.NodeHandler;

public class InputSubmitHandler extends NodeHandler{
	public void handleNode(Container parent, DomNode node) {
		String value = AttributeUtils.getValue(node, "value");
		
		if(value == null) {
			value = Locale.get("default.submit");
		} 
		
		//#style input_submit
		StringItem submitItem = new StringItem(null,value);
		
		submitItem.setAppearanceMode(Item.INTERACTIVE);
		
		parent.add(submitItem);
	}
	
	
}
