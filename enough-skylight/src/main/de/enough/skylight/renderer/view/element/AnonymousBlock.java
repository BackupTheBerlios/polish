package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public class AnonymousBlock extends StringItem implements Element{
	String display = Element.Display.INLINE;
	
	String position = Element.Position.STATIC;
	
	String floating = Element.Float.NONE;
	
	DomNode node;
	
	String value;
	
	public AnonymousBlock(DomNode node) {
		this(node,null);
	}
	
	public AnonymousBlock(DomNode node, Style style) {
		super(null,null,style);
	
		this.node = node;
		this.value = node.getNodeValue();
		
		//TODO temporary until line breaks are suitable
		if(this.value != null) {
			setText(this.value.trim());
		}
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
}
