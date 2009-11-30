package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class ContainingBlock extends Container {

	public static Integer DISPLAY_BLOCK = new Integer(0x00);
	public static Integer DISPLAY_INLINE = new Integer(0x01);
	
	Integer display = DISPLAY_BLOCK;
	
	public static Integer POSITION_STATIC = new Integer(0x00);
	public static Integer POSITION_ABSOLUTE = new Integer(0x01);
	public static Integer POSITION_RELATIVE = new Integer(0x02);
	
	Integer position = POSITION_STATIC;
	
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
	protected boolean isDisplay(Integer display) {
		return this.display == display;
	}
	
	/**
	 * @return
	 */
	protected boolean isPosition(Integer position) {
		return this.position == position;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.display
		Integer displayInt = style.getIntProperty("display");
		System.out.println(displayInt);
		if(displayInt != null) {
			this.display = displayInt;
		}
		//#endif
		
		//#if polish.css.position
		Integer positionInt = style.getIntProperty("position");
		if(positionInt != null) {
			this.position = positionInt;
		}
		//#endif
	}
}
