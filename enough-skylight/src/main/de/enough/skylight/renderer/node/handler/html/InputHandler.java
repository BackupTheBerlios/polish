package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.DefaultHandler;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputHandler extends BodyNodeHandler{
	
	NodeHandler textHandler;
	
	NodeHandler radioHandler;
	
	NodeHandler submitHandler;
	
	public String getTag() {
		return "input";
	}
	
	public InputHandler() {
		this.textHandler = new InputTextHandler();
		this.radioHandler = new InputRadioHandler();
		this.submitHandler = new InputSubmitHandler();
	}
	
	NodeHandler getTypeHandler(CssElement element) {
		DomNode node = element.getNode();
		String type = NodeUtils.getAttributeValue(node, "type");
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
		
		return handler;
	}
	
	public void handleNode(CssElement element) {
		NodeHandler handler = getTypeHandler(element);
		handler.handleNode(element);
	}
	
	public Item createContent(CssElement element) {
		NodeHandler handler = getTypeHandler(element);
		return handler.createContent(element);
	}

	public Style getDefaultStyle(CssElement element) {
		NodeHandler handler = getTypeHandler(element);
		return handler.getDefaultStyle(element);
	}
	
	public void setContent(CssElement element, Item item) {
		NodeHandler handler = getTypeHandler(element);
		handler.setContent(element, item);
	}
}
