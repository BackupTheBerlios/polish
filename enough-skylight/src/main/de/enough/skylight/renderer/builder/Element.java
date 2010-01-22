package de.enough.skylight.renderer.builder;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.ContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.handler.NodeHandler;

public class Element implements HtmlCssElement{
	String display = HtmlCssElement.Display.INLINE;
	
	String position = HtmlCssElement.Position.STATIC;
	
	String floating = HtmlCssElement.Float.NONE;
	
	NodeHandler handler;
	
	DomNode node;
	
	Item content;
	
	Style style;
	
	Element parent;
	
	ArrayList children;
	
	public Element(NodeHandler handler, DomNode node, Element parent) {
		this.handler = handler;
		
		this.node = node;
		
		this.style = handler.getStyle(node);
		
		this.parent = parent;
		
		this.children = new ArrayList();
		
		setStyle(this.style);
	}
	
	public void add(Element child) {
		this.children.add(child);
	}
	
	public Element get(int index) {
		return (Element)this.children.get(index);
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
			return this.handler.createContent(this.node);
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
			return new BlockContainingBlock(this);
		} else {
			return new InlineContainingBlock(this, parentBlock);
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
	
	public Element getParent() {
		return this.parent;
	}
}
