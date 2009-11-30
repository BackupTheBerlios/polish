package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class ContainingBlock extends Container {
	ElementHandler handler;

	public static Integer DISPLAY_BLOCK = new Integer(0x00);
	public static Integer DISPLAY_INLINE = new Integer(0x01);
	
	Integer display = DISPLAY_BLOCK;
	
	public ContainingBlock(ElementHandler handler) {
		super(false);
		
		this.handler = handler;
		
		setView(new ContainingBlockView(this));
	}
	
	public ContainingBlock(ElementHandler handler, Style style) {
		super(false, style);
		
		this.handler = handler;
		
		setView(new ContainingBlockView(this));
	}
	
	/**
	 * @return
	 */
	protected boolean isInline() {
		return this.display == DISPLAY_INLINE;
	}
	
	/**
	 * @return
	 */
	protected boolean isBlock() {
		return this.display == DISPLAY_BLOCK;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.display
		Integer displayInt = style.getIntProperty("display");
		if(displayInt != null) {
			this.display = displayInt;
		}
		//#endif
	}
}
