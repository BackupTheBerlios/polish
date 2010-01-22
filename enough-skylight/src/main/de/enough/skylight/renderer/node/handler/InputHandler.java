package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputHandler extends NodeHandler{
	
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
	
	NodeHandler getTypeHandler(NodeElement element) {
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

		element.setViewport(element.getViewport());
		
		return handler;
	}
	
	public void handleNode(NodeElement element) {
		NodeHandler handler = getTypeHandler(element);
		handler.handleNode(element);
	}
	
	public Item createContent(NodeElement element) {
		NodeHandler handler = getTypeHandler(element);
		return handler.createContent(element);
	}
}
