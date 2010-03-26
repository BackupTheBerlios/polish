package de.enough.skylight.renderer;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.event.EventListener;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.event.UserEventListener;
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
	
	ArrayList userEventListeners;
	
	ViewportContext context;
	
	public Viewport(ViewportContext context) {
		//#style viewport
		super(null);
		
		this.userEventListeners = new ArrayList();
		
		this.context = context;
	}
	
	public void addUserEventListener(UserEventListener listener) {
		this.userEventListeners.add(listener);
	}
	
	public void removeUserEventListener(EventListener listener) {
		this.userEventListeners.remove(listener);
	}
	
	public void notifyUserEvent(CssElement element, UserEvent event) {
		for (int i = 0; i < this.userEventListeners.size(); i++) {
			UserEventListener listener = (UserEventListener)this.userEventListeners.get(i);
			listener.onUserEvent(element, event);
		}
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
	
	public void nodeUpdated(DomNode node) {
		CssElement element = CssElement.getElementWithNode(this.rootElement, node);
		element.update();
	}
	
	public ViewportContext getContext() {
		return this.context;
	}

	public String toString() {
		return "Viewport";
	}
}
