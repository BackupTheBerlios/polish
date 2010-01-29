package de.enough.skylight.renderer;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.event.EventListener;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.node.CssElement;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends BlockContainingBlock {
	boolean ready;
	
	CssElement rootElement;
	
	ArrayList eventListeners;
	
	public Viewport() {
		//#style viewport
		super(null);
		
		this.eventListeners = new ArrayList();
	}
	
	public void addEventListener(EventListener listener) {
		this.eventListeners.add(listener);
	}
	
	public void removeEventListener(EventListener listener) {
		this.eventListeners.remove(listener);
	}
	
	public void setTitle(String title) {
		setLabel(title);
	}
	
	public void reset() {
		this.ready = false;
		
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
	
	public String toString() {
		return "Viewport";
	}

	public boolean isReady() {
		return this.ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if(isReady()) {
			super.paint(x, y, leftBorder, rightBorder, g);
		}
	}
}
