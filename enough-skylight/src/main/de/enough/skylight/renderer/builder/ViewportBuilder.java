package de.enough.skylight.renderer.builder;

import de.enough.polish.benchmark.Benchmark;
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
import de.enough.skylight.renderer.element.ElementAttributes;
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
			//#debug sl.profile.build
			Benchmark.start("description");
			
			CssElement rootElement = buildDescription(this.document, null);
			
			//#debug sl.profile.build
			Benchmark.stop("description","done");
			
			this.viewport.setVisible(false);
			
			this.viewport.reset();
			
			this.viewport.setRootElement(rootElement);
			
			//#debug sl.profile.build
			Benchmark.start("layout");
			
			buildLayout(this.viewport, this.viewport, rootElement);
			
			//#debug sl.profile.build
			Benchmark.stop("layout","done");
			
			this.viewport.setVisible(true);
			
			//#debug sl.profile.build
			Benchmark.start("preinit");
			
			this.viewport.requestInit();
			
			ItemPreinit.preinit(this.viewport);
			
			//#debug sl.profile.build
			Benchmark.stop("preinit","done");
			
			this.viewport.setReady(true);
			
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
			ElementAttributes.set(block.getContainer(), element, parent, parentBlock);
			
			if(element.isInteractive()) {
				//#debug sl.debug.event
				System.out.println("setting " + block + " to interactive");
				block.getContainer().setAppearanceMode(Item.INTERACTIVE);
			}
			
			if(element.isFloat()) {
				if(element.isFloat(HtmlCssElement.Float.LEFT)) {
					parent.addToLeftFloat((Item)block);
					
					//#debug sl.debug.build
					System.out.println("added " + block + " to left float of " + parent);
				} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
					parent.addToRightFloat((Item)block);
					
					//#debug sl.debug.build
					System.out.println("added " + block + " to right float of " + parent);
				} 
			} else {
				parent.addToBody((Item)block);
				
				//#debug sl.debug.build
				System.out.println("added " + block + " to body of " + parent);
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
			
			if(element.isInteractive()) {
				//#debug sl.debug.event
				System.out.println("setting " + item + " to interactive");
				item.setAppearanceMode(Item.INTERACTIVE);
			}
			
			if(item != null) {
				ElementAttributes.set(item, element, parent, parentBlock);
				if(element.isFloat()) {
					if(element.isFloat(HtmlCssElement.Float.LEFT)) {
						parent.addToLeftFloat(item);
						
						//#debug sl.debug.build
						System.out.println("added " + item + " to left float of " + parent);
					} else if(element.isFloat(HtmlCssElement.Float.RIGHT)) {
						parent.addToRightFloat(item);
						
						//#debug sl.debug.build
						System.out.println("added " + item + " to right float of " + parent);
					}
				} else {
					parent.addToBody(item);
					
					//#debug sl.debug.build
					System.out.println("added " + item + " to body of " + parent);
				}
			}
		}
	}
}
