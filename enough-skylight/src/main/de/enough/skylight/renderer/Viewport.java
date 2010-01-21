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
		this.floatLeft.clear();
	}

	/*protected void init(int firstLineWidth, int availWidth, int availHeight) {
		if(!this.build) {
			return;
		}
		
		super.init(firstLineWidth, availWidth, availHeight);
	}

	public boolean isBuild() {
		return this.build;
	}

	public void setBuild(boolean build) {
		this.build = build;
	}*/
}
