package de.enough.skylight.renderer.view.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class Element implements CssElement{
	String display = CssElement.Display.INLINE;
	
	String position = CssElement.Position.STATIC;
	
	String floating = CssElement.Float.NONE;
	
	DomNode node;
	
	Item item;

	ContainingBlock parent;
	
	public Element(Item item, ContainingBlock parent, DomNode node) {
		this.item = item;
		this.node = node;
		this.parent = parent;
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
	
	public DomNode getNode() {
		return this.node;
	}
	
	public int getType() {
		return CssElement.Type.TEXT;
	}

	public Item getItem() {
		return this.item;
	}

	public void setStyle(Style style) {
		this.item.setStyle(style);
		
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

	public ContainingBlock getParent() {
		return this.parent;
	}
}
