package de.enough.skylight.renderer;

import de.enough.skylight.renderer.element.BlockContainingBlock;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends BlockContainingBlock {
	
	public Viewport() {
		//#style viewport
		super();
	}
	
	public void setTitle(String title) {
		setLabel(title);
	}
}
