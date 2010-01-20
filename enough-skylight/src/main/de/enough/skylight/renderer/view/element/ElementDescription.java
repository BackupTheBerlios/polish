package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.view.css.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class ElementDescription implements CssElement{
	String display = CssElement.Display.INLINE;
	
	String position = CssElement.Position.STATIC;
	
	String floating = CssElement.Float.NONE;
	
	NodeHandler handler;
	
	DomNode node;
	
	Style style;
	
	ElementDescription parent;
	
	public ElementDescription(NodeHandler handler, DomNode node, ElementDescription parent) {
		this.handler = handler;
		
		this.node = node;
		
		this.style = handler.getStyle(node);
		
		this.parent = parent;
		
		setStyle(this.style);
	}
		
	public DomNode getNode() {
		return this.node;
	}
	
	public Item getContent() {
		if(isNodeType(DomNode.TEXT_NODE)) {
			TextBlock textBlock = new TextBlock(this.style);
			textBlock.setText(this.node.getNodeValue());
			return textBlock;
		} else {
			return this.handler.createContent(this.node);
		}
	}
	
	public Container getContainingBlock(BlockContainingBlock parentBlock) {
		if(isDisplay(CssElement.Display.BLOCK_LEVEL)) {
			return new BlockContainingBlock(this.style);
		} else {
			return new InlineContainingBlock(parentBlock, this.style);
		} 
	}
	
	public void setStyle(Style style) {
		//#if polish.css.display
		String displayStr = style.getProperty("display");
		if(displayStr != null) {
			this.display = displayStr;
		}
		//#endif
		
		//#if polish.css.position
		String positionStr = style.getProperty("position");
		if(positionStr != null) {
			this.position = positionStr;
		}
		//#endif
		
		//#if polish.css.float
		String floatStr = style.getProperty("float");
		if(floatStr != null) {
			this.floating = floatStr;
		}
		//#endif
	}
	

	/**
	 * @return
	 */
	public boolean isDisplay(String display) {
		return this.display == display;
	}
	
	/**
	 * @return
	 */
	public boolean isPosition(String position) {
		return this.position == position;
	}
	
	/**
	 * @return
	 */
	public boolean isFloat(String floating) {
		return this.floating == floating;
	}
	
	public boolean isNodeType(int type) {
		return this.node.getNodeType() == type;
	}
	
	public String toString() {
		return "Element []";
	}
}
