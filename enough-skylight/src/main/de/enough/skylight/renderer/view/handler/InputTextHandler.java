package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.AttributeUtils;
import de.enough.skylight.renderer.view.NodeHandler;

public class InputTextHandler extends NodeHandler{
	public void handleNode(Container parent, DomNode node) {
		String value = AttributeUtils.getValue(node, "value");
		
		//#style input_text
		TextField textField = new TextField(null,value,512,TextField.ANY);
		
		parent.add(textField);
	}
}
