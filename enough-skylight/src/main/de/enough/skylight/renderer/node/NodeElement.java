package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.TextBlock;

public class NodeElement implements HtmlCssElement{
	String display = HtmlCssElement.Display.INLINE;
	
	String position = HtmlCssElement.Position.STATIC;
	
	String floating = HtmlCssElement.Float.NONE;
	
	Viewport viewport;
	
	NodeHandler handler;
	
	DomNode node;
	
	Item content;
	
	Style style;
	
	NodeElement parent;
	
	ArrayList children;
	
	static Style getStyle(NodeHandler handler, DomNode node) {
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		Style defaultStyle = handler.getDefaultStyle();
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			Style style = StyleSheet.getStyle(clazz);
			
			if(style != null) {
				//TODO extend style with default style
				return style;
			} else {
				//#debug error
				System.out.println("style " + clazz + " could not be found");
			}
		}
		
		return defaultStyle;
	}
	
	public NodeElement(NodeHandler handler, DomNode node, NodeElement parent, Viewport viewport) {
		this.handler = handler;
		
		this.node = node;
		
		this.parent = parent;
		
		this.children = new ArrayList();
		
		this.viewport = viewport;
	}
	
	public void handle() throws ClassCastException, IllegalArgumentException {
		this.handler.handleNode(this);
		
		this.style = getStyle(this.handler, this.node);
		
		setStyle(this.style);
	}
	
	public void add(NodeElement child) {
		this.children.add(child);
	}
	
	public NodeElement get(int index) {
		return (NodeElement)this.children.get(index);
	}
	
	public int size() {
		return this.children.size();
	}
	
	public boolean hasElements() {
		return size() > 0; 
	}
		
	public DomNode getNode() {
		return this.node;
	}
	
	public Style getStyle() {
		return this.style;
	}
	
	public Item createContent() {
		if(isNodeType(DomNode.TEXT_NODE)) {
			TextBlock textBlock = new TextBlock();
			textBlock.setText(this.node.getNodeValue());
			return textBlock;
		} else {
			return this.handler.createContent(this);
		}
	}
	
	public void setContent(Item content) {
		this.content = content;
	}
	
	public Item getContent() {
		return this.content;
	}
	
	public ContainingBlock createContainingBlock(BlockContainingBlock parentBlock) {
		if(isDisplay(HtmlCssElement.Display.BLOCK_LEVEL)) {
			return new BlockContainingBlock(this, this.style);
		} else {
			return new InlineContainingBlock(this, parentBlock, this.style);
		} 
	}
	
	public void setStyle(Style style) {
		String displayStr = style.getProperty("display");
		if(displayStr != null) {
			this.display = displayStr;
		}
		
		String positionStr = style.getProperty("position");
		if(positionStr != null) {
			this.position = positionStr;
		}
		
		String floatStr = style.getProperty("float");
		if(floatStr != null) {
			this.floating = floatStr;
		}
	}
	
	public boolean isDisplay(String display) {
		return this.display == display;
	}
	
	public boolean isPosition(String position) {
		return this.position == position;
	}
	
	public boolean isFloat(String floating) {
		return this.floating == floating;
	}
	
	public boolean isFloat() {
		return this.floating != HtmlCssElement.Float.NONE;
	}
	
	public boolean isParentFloat() {
		return this.parent.isFloat();
	}
	
	public boolean isNodeType(int type) {
		return this.node.getNodeType() == type;
	}
	
	public NodeElement getParent() {
		return this.parent;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}
}
