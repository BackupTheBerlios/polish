package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.DebugHelper;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ItemPreinit;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.Break;
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
			ItemPreinit.preinit(viewport);
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
			
			layoutBlock(this.viewport, null);
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
			ContainingBlock block = new ContainingBlock(parent,node);
			
			block.setStyle(style);
			
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
				} else if(handler.getType() == CssElement.Type.BREAK) {
					element = new Break(parent,node);
				}
			}
		}
		
		if(element != null) {
			parent.getElements().add(element);
		} 
	}
	
	protected void layoutBlock(ContainingBlock parentBlock, Style lineStyle) throws IllegalArgumentException {
		if(!parentBlock.isInitialized()) {
			ItemPreinit.preinit(parentBlock);
		}
		
		System.out.println("layout : " + parentBlock);
		if(parentBlock.isDisplay(CssElement.Display.INLINE)) {
			System.out.println("layout : parent is inline");
		} else {
			System.out.println("layout : parent is block");
		}
		
		int space = parentBlock.getAvailableContentWidth();
		Style style = parentBlock.getStyle();
		
		LineBox blockLine = null;
		
		ArrayList elements = parentBlock.getElements();
		for (int index = 0; index < elements.size(); index++) {
			CssElement element = (CssElement)elements.get(index);
			
			if(element.getType() == CssElement.Type.CONTAINING_BLOCK) {
				ContainingBlock block = (ContainingBlock)element.getItem();
				
				if(element.isDisplay(CssElement.Display.INLINE)) {
					/*LineBox inlineLine = LineBox.createForInline(blockLine, space, style);
					layoutInline(parentBlock,inlineLine,style);*/
				} else if(element.isDisplay(CssElement.Display.BLOCK_LEVEL)) {
					parentBlock.add(block);
					layoutBlock(block, style);
				}
			} else if(element.getType() == CssElement.Type.ELEMENT) {
				if(element.isDisplay(CssElement.Display.INLINE)) {
					// inline : add to linebox
					Item item = element.getItem();
					
					blockLine = addToBlockLine(item, space, parentBlock, blockLine);
				} else if(element.isDisplay(CssElement.Display.BLOCK_LEVEL)) {
					// block : add as single expanded linebox, set linebox as new item				
				} 
			} else if(element.getType() == CssElement.Type.BREAK) {
				blockLine = addBreak(space, parentBlock, blockLine);
			} else if(element.getType() == CssElement.Type.TEXT) {
				// get text items and add to lineboxes
				Text text = (Text)element;
				Item[] items = text.getItems();
				
				// check if this is first or last to add margins / paddings to linebox
				// complicated
				
				for (int i = 0; i < items.length; i++) {
					Item item = items[i];
					
					blockLine = addToBlockLine(item, space, parentBlock, blockLine);
				}
			}
		}
	}
	
	protected void layoutInline(ContainingBlock parentBlock, LineBox parentLine, Style lineStyle) throws IllegalArgumentException {
		
		System.out.println("layout inline : " + parentLine);
	}
	
	LineBox addToBlockLine(Item item, int space, ContainingBlock block, LineBox blockLine) {
		// extract inline style and apply
		// Style style = block.getStyle();
		
		LineBox result = blockLine;
		
		if(result == null || !result.hasSpace(item)) {
			result = LineBox.createForBlock(space, null);
			System.out.println("creating block line for " + block);
			block.add(result.getWorkLine());
		}
		
		System.out.println("adding to block line : " + result);
		result.add(item);
		
		return result;
	}
	
	LineBox addBreak(int space, ContainingBlock block, LineBox blockLine) {
		System.out.println("addding break");
		
		LineBox result = blockLine;
		result = LineBox.createForBlock(space, null);
		block.add(result.getWorkLine());
		
		return result;
	}
}
