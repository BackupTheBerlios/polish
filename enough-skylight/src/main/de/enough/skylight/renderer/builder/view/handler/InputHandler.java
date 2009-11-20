package de.enough.skylight.renderer.builder.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.builder.view.NodeHandler;
import de.enough.skylight.renderer.builder.view.NodeUtils;

public class InputHandler extends NodeHandler{
	public void handleNode(Container parent, DomNode node) {
		String type = NodeUtils.getAttributeValue(node, "type");
		
		if(type != null) {
			if(type.equals("text")) {
				handleTypeText(parent, node);
			}
		}
	}
	
	public void handleTypeText(Container parent, DomNode node) {
		String value = NodeUtils.getAttributeValue(node, "value");
		
		//#style input_text
		TextField textField = new TextField(null,value,512,TextField.ANY);
		
		parent.add(textField);
	}
}
