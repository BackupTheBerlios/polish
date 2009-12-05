package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.ContainingBlock;
import de.enough.skylight.renderer.view.element.ContainingBlockView;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.view.element.Element;
import de.enough.skylight.renderer.view.element.Text;
import de.enough.skylight.renderer.view.handler.PHandler;
import de.enough.skylight.renderer.viewport.NodeHandler;
import de.enough.skylight.renderer.viewport.NodeHandlerDirectory;

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
			
			prepare(this.viewport, this.document);
			
			build(this.viewport);
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}
	
	protected void prepare(ContainingBlock parent, DomNode node) {
		NodeHandler handler = NodeHandlerDirectory.getHandler(node);
		
		//#debug debug
		System.out.println("found handler " + handler + " for " + node);
		
		handler.setViewport(this.viewport);
			
		Style style = handler.getStyle(node);
		
		handler.handleNode(node);
		
		CssElement element = null;
			
		if(handler.getType() == CssElement.Type.CONTAINING_BLOCK && node.hasChildNodes())
		{
			ContainingBlock block = new ContainingBlock(parent,node,style);
			
			//#debug debug
			System.out.println(node + " is containing block has children");
			
			NodeList nodes = node.getChildNodes();
			for (int index = 0; index < nodes.getLength(); index++) {
				DomNode childNode = nodes.item(index);
				
				prepare(block,childNode);
			}
			
			element = block;
		} else {
			if(node.getNodeType() == DomNode.TEXT_NODE) {
				element = new Text(node,parent);
			} else {
				if(handler.getType() == CssElement.Type.ELEMENT) {
					Item item = handler.createNodeItem(node);
					if(item != null) {
						element = new Element(item,parent,node,style);
					}
				}
			}
		}
		
		if(element != null) {
			parent.getElements().add(element);
		} 
	}
	
	protected void build(ContainingBlock parent) {
		ArrayList elements = parent.getElements();
		for (int index = 0; index < elements.size(); index++) {
			CssElement element = (CssElement)elements.get(index);
			
			Item item = element.getItem();
			
			if(element.getType() == CssElement.Type.CONTAINING_BLOCK) {
				build((ContainingBlock)item);
			}
			
			parent.add(item);
		}
	}
}
