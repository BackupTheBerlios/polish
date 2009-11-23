package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.FormattingBlock;
import de.enough.skylight.renderer.view.handler.PHandler;
import de.enough.skylight.renderer.viewport.ElementHandler;
import de.enough.skylight.renderer.viewport.HandlerDirectory;

public class ViewportBuilder {
	Document document;
	Viewport viewport;
	
	public ViewportBuilder(Document document) {
		this.document = document;
	}
	
	public ViewportBuilder(Viewport viewport, Document document) {
		this.viewport = viewport;
		this.document = document;
	}
	

	
	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}

	
	public void build() {
		if(this.viewport == null) {
			throw new IllegalStateException("viewport is null");
		}
		
		try {
			this.viewport.reset();
			
			handleNode(this.viewport, this.document);
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}
	
	protected void handleNode(FormattingBlock parentContainingBlock, DomNode node) {
		ElementHandler handler = HandlerDirectory.getHandler(node);
		
		//#debug debug
		System.out.println("found handler " + handler + " for " + node);
		
		if(handler != null) {
			FormattingBlock handlerBlock = handler.getFormattingBlock();
			
			FormattingBlock containingBlock;
			
			if( handlerBlock.isBlock()) {
				parentContainingBlock.add(handlerBlock);
				containingBlock = handlerBlock;
				//#debug debug
				System.out.println(handler + " is block : " + containingBlock);
			} else {
				containingBlock = parentContainingBlock;
				//#debug debug
				System.out.println(handler + " is inline : " + containingBlock);
			}
			
			handler.setViewport(this.viewport);
			handler.handleNode(containingBlock, node);
				
			if(node.hasChildNodes())
			{
				//#debug debug
				System.out.println(node + " has children");
				
				NodeList nodes = node.getChildNodes();
				for (int index = 0; index < nodes.getLength(); index++) {
					DomNode childNode = nodes.item(index);
					
					handleNodeByType(handler, containingBlock, childNode);
				}
			}
		} 
	}
	
	protected void handleNodeByType(ElementHandler handler, FormattingBlock containingBlock, DomNode node) {
		switch(node.getNodeType()) {
			case DomNode.TEXT_NODE : 
				//#debug debug
				System.out.println("adding text : " + node.getNodeValue() + " to " + containingBlock);
				Style textStyle = handler.getTextStyle();
				handler.handleText(containingBlock, node.getNodeValue(), textStyle, node.getParentNode()); 
				break;
			default: 
				//#debug debug
				System.out.println("adding node : " + node.getNodeName() + " to " + containingBlock);
				handleNode(containingBlock, node);  
		}
	}
}
