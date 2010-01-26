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
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeHandlerDirectory;
import de.enough.skylight.renderer.node.handler.html.TextHandler;

public class ViewportBuilder {
	Document document;
	
	Viewport viewport;
	
	public ViewportBuilder() {
	}
	
	public ViewportBuilder(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public void build() throws IllegalArgumentException {
		if(this.viewport == null) {
			throw new IllegalStateException("viewport is null");
		}
		
		if(this.document == null) {
			throw new IllegalStateException("document is null");
		}
		
		try {
			CssElement rootElement = buildDescription(this.document, null);
			
			this.viewport.setVisible(false);
			this.viewport.getLabelItem().setVisible(false);
			
			this.viewport.reset();
			
			this.viewport.setRootElement(rootElement);
			
			buildLayout(this.viewport, this.viewport, rootElement);
			
			this.viewport.setVisible(true);
			this.viewport.getLabelItem().setVisible(true);
			
			this.viewport.requestInit();
			
			ItemPreinit.preinit(this.viewport);
			
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}

	protected CssElement buildDescription(DomNode node, CssElement parent) {
		NodeHandler handler = NodeHandlerDirectory.getInstance().getNodeHandler(node);
		
		if(handler != null) {
			if(handler.isValid(node)) {
				CssElement element = handler.createElement(node, parent, this.viewport); 
				
				try {
					element.build();
				} catch(IllegalArgumentException e) {
					//#debug error
					System.out.println("illegal argument for " + node + ":" + e.getMessage());
				} catch(ClassCastException e) {
					//#debug error
					System.out.println("could not cast the given element, please implement createElement()");
				}
				
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
			} else {
				if(handler != TextHandler.getInstance()) {
					//#debug warn
					System.out.println(node.getNodeName() + " is not valid");
				}
			}
		}
		
		return null;
	}
	
	protected void buildLayout(ContainingBlock parent, BlockContainingBlock parentBlock, CssElement element) {
		if(element.hasElements())
		{
			ContainingBlock block = element.getContainingBlock();
			block.setParentBlock(parentBlock);
		
			if(element.isFloat()) {
				if(element.isFloat(HtmlCssElement.Float.LEFT)) {
					parent.addToLeftFloat((Item)block);
				} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
					parent.addToRightFloat((Item)block);
				} 
			} else {
				parent.addToBody((Item)block);
				
				//#debug debug
				System.out.println("added " + block + " to " + parent);
			}
			
			BlockContainingBlock childBlock;
			if(block instanceof BlockContainingBlock) {
				childBlock = (BlockContainingBlock)block;
			} else {
				childBlock = parentBlock;
			}
			
			for (int index = 0; index < element.size(); index++) {
				CssElement childElement = element.get(index);
				
				buildLayout(block,childBlock,childElement);
			}
		} else {
			Item item = element.getContent();
			
			if(item != null) {
				if(element.isFloat()) {
					if(element.isFloat(HtmlCssElement.Float.LEFT)) {
						parent.addToLeftFloat(item);
					} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
						parent.addToRightFloat(item);
					}
				} else {
					parent.addToBody(item);
					
					//#debug debug
					System.out.println("added " + item + " to " + parent);
				}
			}
		}
	}
}
