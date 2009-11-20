package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.AttributeUtils;
import de.enough.skylight.renderer.view.NodeHandler;

public class InputHandler extends NodeHandler{
	
	NodeHandler textHandler;
	
	NodeHandler radioHandler;
	
	NodeHandler submitHandler;
	
	public InputHandler() {
		this.textHandler = new InputTextHandler();
		this.radioHandler = new InputRadioHandler();
		this.submitHandler = new InputSubmitHandler();
	}
	
	public void handleNode(Container parent, DomNode node) {
		String type = AttributeUtils.getValue(node, "type");
		
		if(type != null) {
			if(type.equals("text")) {
				this.textHandler.handleNode(parent, node);
			} else if(type.equals("radio")) {
				this.radioHandler.handleNode(parent, node);
			} else if(type.equals("submit")) {
				this.submitHandler.handleNode(parent, node);
			}
		}
	}
	
	public void handleText(Container parent, String text, Style style, DomNode parentNode) {
		String type = AttributeUtils.getValue(parentNode, "type");
		
		if(type != null) {
			if(type.equals("text")) {
				this.textHandler.handleText(parent, text, style, parentNode);
			} else if(type.equals("radio")) {
				this.radioHandler.handleText(parent, text, style,  parentNode);
			} else if(type.equals("submit")) {
				this.submitHandler.handleText(parent, text, style, parentNode);
			}
		}
	}
}
