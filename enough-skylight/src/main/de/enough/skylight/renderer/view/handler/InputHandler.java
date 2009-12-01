package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.AttributeUtils;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class InputHandler extends ElementHandler{
	
	ElementHandler textHandler;
	
	ElementHandler radioHandler;
	
	ElementHandler submitHandler;
	
	public InputHandler() {
		this.textHandler = new InputTextHandler();
		this.radioHandler = new InputRadioHandler();
		this.submitHandler = new InputSubmitHandler();
	}
	
	ElementHandler getTypeHandler(DomNode node) {
		String type = AttributeUtils.getValue(node, "type");
		ElementHandler handler = null;
		
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
	
	public void handleNode(Container parent, DomNode node) {
		ElementHandler handler = getTypeHandler(node);
		
		handler.handleNode(parent, node);
	}
	
	public void handleText(Container parent, DomNode node, Style style, DomNode parentNode) {
		ElementHandler handler = getTypeHandler(parentNode);
		
		handler.handleText(parent, node, style, parentNode);
	}
}
