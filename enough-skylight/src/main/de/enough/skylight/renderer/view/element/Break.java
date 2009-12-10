package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class Break implements CssElement{
	String display = CssElement.Display.INLINE;
	
	String position = CssElement.Position.STATIC;
	
	String floating = CssElement.Float.NONE;
	
	DomNode node;

	ContainingBlock parent;
	
	public Break(ContainingBlock parent, DomNode node) {
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
		return CssElement.Type.BREAK;
	}

	public Item getItem() {
		return null;
	}

	public ContainingBlock getParent() {
		return this.parent;
	}
	
	public String toString() {
		return "Break";
	}
}
