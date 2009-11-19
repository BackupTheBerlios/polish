package de.enough.polish.skylight.builder.view.handler;

import de.enough.polish.skylight.builder.view.NodeHandler;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;

public class TextHandler extends NodeHandler{

	public void handleNode(Container parent, DomNode node) {
		String value = node.getNodeValue();

		if(value != null) {
			value = value.trim();
			if(!value.equals("")) {
				//#style text
				StringItem textItem = new StringItem(null,value);
				
				parent.add(textItem);
			}
		}
	}
}
