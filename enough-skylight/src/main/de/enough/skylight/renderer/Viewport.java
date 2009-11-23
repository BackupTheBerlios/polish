package de.enough.skylight.renderer;

import de.enough.polish.util.HashMap;
import de.enough.skylight.renderer.view.element.FormattingBlock;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends FormattingBlock {
	
	HashMap directory;
	
	public Viewport() {
		//#style viewport
		super(null);
		
		this.directory = new HashMap();
	}
	
	public HashMap getDirectory() {
		return this.directory;
	}
	
	public void reset() {
		clear();
		this.directory.clear();
	}

	public void setTitle(String title) {
		setLabel(title);
	}
}
