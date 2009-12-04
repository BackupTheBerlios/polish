package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class Text implements CssElement{
	final String display = CssElement.Display.INLINE;
	
	final String position = CssElement.Position.STATIC;
	
	final String floating = CssElement.Float.NONE;
	
	DomNode node;
	
	String value;
	
	ContainingBlock parent;
	
	public Text(DomNode node, ContainingBlock parent) {
		this.node = node;
		this.value = trim(node.getNodeValue());
		this.parent = parent;
	}
	
	String trim(String value) {
		return value.trim();
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
	
	public String getValue() {
		return this.value;
	}
	
	public int getType() {
		return CssElement.Type.TEXT;
	}

	public Item getItem() {
		//TODO temporary until lineboxes are implemented
		return new StringItem(null,this.value,getParent().getStyle());
	}
	
	public ContainingBlock getParent() {
		return this.parent;
	}
}
