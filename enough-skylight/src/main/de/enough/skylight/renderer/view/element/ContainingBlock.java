package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class ContainingBlock extends Container implements Element {

	String display = Element.Display.BLOCK_LEVEL;
	
	String position = Element.Position.STATIC;
	
	String floating = Element.Float.NONE;
	
	ElementHandler handler;
	
	DomNode node;
	
	public ContainingBlock(ElementHandler handler, DomNode node) {
		super(false);
		
		this.handler = handler;
		this.node = node;
		
		setView(new ContainingBlockView(this));
	}
	
	public ContainingBlock(ElementHandler handler, DomNode node, Style style) {
		super(false, style);
		
		this.handler = handler;
		this.node = node;
		
		setView(new ContainingBlockView(this));
	}
	
	public ElementHandler getHandler() {
		return handler;
	}

	public void setHandler(ElementHandler handler) {
		this.handler = handler;
	}

	public DomNode getNode() {
		return node;
	}

	public void setNode(DomNode node) {
		this.node = node;
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
}
