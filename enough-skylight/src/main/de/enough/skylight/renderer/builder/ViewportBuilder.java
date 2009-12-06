package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ItemPrefetch;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.ContainingBlock;
import de.enough.skylight.renderer.view.element.CssElement;
import de.enough.skylight.renderer.view.element.Element;
import de.enough.skylight.renderer.view.element.LineBox;
import de.enough.skylight.renderer.view.element.Text;
import de.enough.skylight.renderer.viewport.NodeHandler;
import de.enough.skylight.renderer.viewport.NodeHandlerDirectory;

public class ViewportBuilder {
	Document document;
	Viewport viewport;
	
	public ViewportBuilder(Document document) {
		this.document = document;
	}
	
	public ViewportBuilder(Viewport viewport, Document document) throws IllegalArgumentException {
		if(!viewport.isInitialized()) {
			ItemPrefetch.prefetch(viewport);
		}
		
		this.viewport = viewport;
		this.document = document;
	}
	
	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public void update(DomNode node, Object object) {
		// build();
	}
	
	public void build() throws IllegalArgumentException {
		if(this.viewport == null) {
			throw new IllegalStateException("viewport is null");
		}
		
		try {
			this.viewport.reset();
			
			build(this.viewport, this.document);
			
			layout(this.viewport);
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}
	
	protected void build(ContainingBlock parent, DomNode node) {
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
			System.out.println(node + " has children");
			
			NodeList nodes = node.getChildNodes();
			for (int index = 0; index < nodes.getLength(); index++) {
				DomNode childNode = nodes.item(index);
				
				build(block,childNode);
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
	
	protected void layout(ContainingBlock parent) throws IllegalArgumentException {
		if(!parent.isInitialized()) {
			ItemPrefetch.prefetch(parent);
		}
		
		int space = parent.getAvailableContentWidth();
		
		LineBox linebox = new LineBox(space);
		
		ArrayList elements = parent.getElements();
		for (int index = 0; index < elements.size(); index++) {
			CssElement element = (CssElement)elements.get(index);
			
			if(element.getType() == CssElement.Type.CONTAINING_BLOCK) {
				ContainingBlock block = (ContainingBlock)element.getItem(); 
				if(element.isDisplay(CssElement.Display.INLINE)) {
					// pass down linebox with build 
				} else if(element.isDisplay(CssElement.Display.BLOCK_LEVEL)) {
					parent.add(block);
					layout(block);
				}
			} else if(element.getType() == CssElement.Type.ELEMENT) {
				if(element.isDisplay(CssElement.Display.INLINE)) {
					// inline : add to linebox
					Item item = element.getItem();
					
					if(!linebox.hasSpace(item)) {
						parent.add(linebox.getLine());
						linebox = new LineBox(space);
					}
					
					linebox.add(item);
				} else if(element.isDisplay(CssElement.Display.BLOCK_LEVEL)) {
					// block : add as single expanded linebox, set linebox as new item				
				} 
			} else if(element.getType() == CssElement.Type.BREAK) {
				parent.add(linebox.getLine());
				linebox = new LineBox(space);
			} else if(element.getType() == CssElement.Type.TEXT) {
				// get text items and add to lineboxes
				Text text = (Text)element;
				Item[] items = text.getItems();
				
				// check if this is first or last to add margins / paddings to linebox
				// complicated
				
				for (int i = 0; i < items.length; i++) {
					Item item = items[i];
					
					if(!linebox.hasSpace(item)) {
						parent.add(linebox.getLine());
						linebox = new LineBox(space);
					}
					
					linebox.add(item);
				}
			}
		}
	}
}
