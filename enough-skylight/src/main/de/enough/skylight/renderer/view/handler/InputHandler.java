package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.viewport.AttributeUtils;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class InputHandler extends NodeHandler{
	
	NodeHandler textHandler;
	
	NodeHandler radioHandler;
	
	NodeHandler submitHandler;
	
	public InputHandler() {
		this.textHandler = new InputTextHandler();
		this.radioHandler = new InputRadioHandler();
		this.submitHandler = new InputSubmitHandler();
	}
	
	NodeHandler getTypeHandler(DomNode node) {
		String type = AttributeUtils.getValue(node, "type");
		NodeHandler handler = null;
		
		if(type != null) {
			if(type.equals("text")) {
				handler = this.textHandler;
			} else if(type.equals("radio")) {
				handler = this.radioHandler;
			} else if(type.equals("submit")) {
				handler = this.submitHandler;
			} 
		}
		
		if(handler == null) {
			//#debug error
			System.out.println("no handler for input type : " + type);
			
			handler = DefaultHandler.getInstance();
		}

		handler.setViewport(getViewport());
		
		return handler;
	}
	
	public void handleNode(DomNode node) {
		NodeHandler handler = getTypeHandler(node);
		handler.handleNode(node);
	}
	
	public int getType() {
		return CssElement.Type.ELEMENT;
	}
}
