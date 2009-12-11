package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class ContainingBlock extends Container implements CssElement {

	String display = CssElement.Display.BLOCK_LEVEL;
	
	String position = CssElement.Position.STATIC;
	
	String floating = CssElement.Float.NONE;
	
	ContainingBlock parent;
	
	DomNode node;
	
	ArrayList elements;
	
	public ContainingBlock(ContainingBlock parent, DomNode node) {
		this(parent, node, null);
	}
	
	public ContainingBlock(ContainingBlock parent, DomNode node, Style style) {
		super(false, style);
		
		this.parent = parent;
		this.node = node;
		this.elements = new ArrayList();
	}
	
	public ArrayList getElements() {
		return this.elements;
	}
	
	public void reset() {
		this.elements.clear();
	}
	
	public DomNode getNode() {
		return node;
	}
	
	public int getType() {
		return CssElement.Type.CONTAINING_BLOCK;
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
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.display
		String displayStr = style.getProperty("display");
		if(displayStr != null) {
			this.display = displayStr;
		}
		
		if(this.display == CssElement.Display.BLOCK_LEVEL) {
			setView(new BlockLevelView());
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

	public Item getItem() {
		return this;
	}
	
	public ContainingBlock getParent() {
		return this.parent;
	}
	
	public String toString() {
		String string = "ContainingBlock [";

		for (int i = 0; i < this.elements.size(); i++) {
			string += this.elements.get(i);
			if(i != this.elements.size() -1 ) {
				string += ",";
			}
		}
		
		string += "]";
		
		return string;
	}
}
