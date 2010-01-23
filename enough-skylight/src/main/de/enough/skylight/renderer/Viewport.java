package de.enough.skylight.renderer;

import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.node.CssElement;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends BlockContainingBlock {
	
	CssElement rootElement;
	
	public Viewport() {
		//#style viewport
		super(null);
	}
	
	public void setTitle(String title) {
		setLabel(title);
	}
	
	public void reset() {
		this.body.clear();
		
		if(this.floatLeft != null) {
			this.floatLeft.clear();
		}
		
		if(this.floatRight != null) {
			this.floatRight.clear();
		}
	}
	
	public CssElement getRootElement() {
		return this.rootElement;
	}
	
	public void setRootElement(CssElement rootElement) {
		this.rootElement = rootElement;
	}
}
