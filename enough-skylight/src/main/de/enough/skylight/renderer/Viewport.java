package de.enough.skylight.renderer;

import de.enough.skylight.renderer.element.BlockContainingBlock;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends BlockContainingBlock {
	
	boolean build = false;
	
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
}
