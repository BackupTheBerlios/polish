package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.node.NodeHandlerDirectory;
import de.enough.skylight.renderer.node.handler.NodeHandler;

public class ViewportBuilder {
	Document document;
	Viewport viewport;
	
	public ViewportBuilder(Document document) {
		this.document = document;
	}
	
	public ViewportBuilder(Viewport viewport, Document document) throws IllegalArgumentException {
		this.viewport = viewport;
		this.document = document;
	}
	
	public Viewport getViewport() {
		return this.viewport;
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
			Element root = buildDescription(this.document, null);
			
			buildLayout(this.viewport, this.viewport, root);
			
			this.viewport.requestInit();
			
			ItemPreinit.preinit(this.viewport);
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}
	
	protected Element buildDescription(DomNode node, Element parent) {
		NodeHandler handler = NodeHandlerDirectory.getHandler(node);
		
		handler.setViewport(this.viewport);
			
		handler.handleNode(node);
		
		Element element = new Element(handler,node,parent);
		
		if(parent != null) {
			parent.add(element);
		}
		
		if(node.hasChildNodes()) {
			NodeList nodes = node.getChildNodes();
			for (int index = 0; index < nodes.getLength(); index++) {
				DomNode childNode = nodes.item(index);
				
				buildDescription(childNode, element);
			}
		}
		
		return element;
	}
	
	protected void buildLayout(ContainingBlock parent, BlockContainingBlock parentBlock, Element element) {
		if(element.hasElements())
		{
			ContainingBlock block = element.createContainingBlock(parentBlock);
			
			element.setContent((Item)block);
		
			if(element.isFloat()) {
				if(element.isFloat(HtmlCssElement.Float.LEFT)) {
					parent.addToLeftFloat((Item)block);
				} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
					parent.addToRightFloat((Item)block);
				}
			} else {
				parent.addToBody((Item)block);
			}
			
			BlockContainingBlock childBlock;
			if(block instanceof BlockContainingBlock) {
				childBlock = (BlockContainingBlock)block;
			} else {
				childBlock = parentBlock;
			}
			
			for (int index = 0; index < element.size(); index++) {
				Element childElement = element.get(index);
				
				buildLayout(block,childBlock,childElement);
			}
		} else {
			Item item = element.createContent();
			
			element.setContent(item);
			
			if(item != null) {
				if(element.isFloat()) {
					if(element.isFloat(HtmlCssElement.Float.LEFT)) {
						parent.addToLeftFloat(item);
					} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
						parent.addToRightFloat(item);
					}
				} else {
					parent.addToBody(item);
				}
			}
		}
	}
}
