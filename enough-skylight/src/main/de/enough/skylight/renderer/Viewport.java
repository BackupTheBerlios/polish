package de.enough.skylight.renderer;

import java.util.Hashtable;

import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.skylight.renderer.view.element.ContainingBlock;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends ContainingBlock {
	
	HashMap directory;
	
	Hashtable styles;
	
	public Viewport() {
		//#style viewport
		super(null,null);
		
		this.directory = new HashMap();
	}
	
	public HashMap getDirectory() {
		return this.directory;
	}
	
	public void setStylesheet(Hashtable stylesheet) {
		this.styles = stylesheet;
	}
	
	public Style getStyle(String name) throws NoSuchFieldError{
		Style style = (Style)this.styles.get(name);
		if(style == null) {
			throw new NoSuchFieldError();
		} else {
			return style;
		}
	}
	
	public void reset() {
		clear();
		this.directory.clear();
	}

	public void setTitle(String title) {
		setLabel(title);
	}
}
