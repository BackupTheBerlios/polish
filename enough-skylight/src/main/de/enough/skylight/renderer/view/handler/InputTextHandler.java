package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.AttributeUtils;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class InputTextHandler extends ElementHandler{
	public void handleNode(Container parent, DomNode node) {
		String value = AttributeUtils.getValue(node, "value");
		
		//#style input_text
		TextField textField = new TextField(null,value,512,TextField.ANY);
		
		parent.add(textField);
	}
}
