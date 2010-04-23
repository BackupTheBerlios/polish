package de.enough.skylight.renderer.builder;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.NodeListImpl;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.debug.BuildDebug;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.layout.floating.FloatLayout;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.CssStyle;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeHandlerDirectory;
import de.enough.skylight.renderer.node.handler.html.TextHandler;

public class ViewportBuilder {
	DocumentImpl document;
	
	Viewport viewport;
	
	ViewportContext viewportContext;
	
	public ViewportBuilder(Viewport viewport) {
		setViewport(viewport);
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
		this.viewportContext = this.viewport.getContext();
	}

	public void setDocument(DocumentImpl document) {
		this.document = document;
	}
	
	public DocumentImpl getDocument() {
		return this.document;
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
			
			this.viewport.setReady(false);
			
			this.viewport.setVisible(false);
			
			this.viewport.reset();
			
			CssElement rootElement = buildDescription(this.document, null);
			
			//#debug sl.debug.build
			BuildDebug.printCssElement(rootElement);
			
			//#debug sl.profile.build
			Benchmark.stop("description","done");
			
			this.viewport.setRootElement(rootElement);
			
			//#debug sl.profile.build
			Benchmark.start("layout");
			
			buildLayout(this.viewport, this.viewport, rootElement);
			
			//#debug sl.debug.build
			BuildDebug.printBlock(this.viewport);
			
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
			
			// #debug sl.debug.build
			System.out.println("build done");
			
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building view : " + e);
			e.printStackTrace();
		}
	}

	protected CssElement buildDescription(DomNodeImpl node, CssElement parent) {
		NodeHandler handler = NodeHandlerDirectory.getInstance().getNodeHandler(node);
		
		if(handler != null) {
			if(handler.isValid(node)) {
				CssElement element = handler.createElement(node, parent, this.viewportContext); 
				
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
					NodeListImpl nodes = node.getChildNodes();
					for (int index = 0; index < nodes.getLength(); index++) {
						DomNodeImpl childNode = nodes.item(index);
						
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
			ContainingBlock containingBlock = element.getContainingBlock();
			
			LayoutDescriptor layoutDescriptor = containingBlock.getLayoutDescriptor();
			layoutDescriptor.setCssElement(element);
			layoutDescriptor.setBlock(parentBlock);
			layoutDescriptor.setViewport(this.viewport);
			
			if(element.isPosition(HtmlCssElement.Position.ABSOLUTE)) {
				layoutDescriptor.setFloatLayout(new FloatLayout());
			}
			
			BlockContainingBlock childBlock;
			if(containingBlock instanceof BlockContainingBlock) {
				childBlock = (BlockContainingBlock)containingBlock;
			} else {
				childBlock = parentBlock;
			}
			
			for (int index = 0; index < element.size(); index++) {
				CssElement childElement = element.get(index);
				
				buildLayout(containingBlock,childBlock,childElement);
			}
			
			if(element.isDisplay(HtmlCssElement.Display.INLINE)) {
				parent.addInline(containingBlock);
			} else if(element.isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
				parent.addBlock(containingBlock);
			}
			
			//#debug sl.debug.build
			System.out.println("added " + containingBlock + " to body of " + parent);
			
		} else {
			Item item = element.getContent();
			
			if(item != null) {
				LayoutDescriptor layoutDescriptor = ContentView.getLayoutDescriptor(item);
				layoutDescriptor.setCssElement(element);
				layoutDescriptor.setBlock(parentBlock);
				layoutDescriptor.setViewport(this.viewport);
				
				if(element.isDisplay(HtmlCssElement.Display.INLINE)) {
					parent.addInline(item);
				} else if(element.isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
					parent.addBlock(item);
				}
					
				//#debug sl.debug.build
				System.out.println("added " + item + " to body of " + parent);
			}
		}
	}
}
