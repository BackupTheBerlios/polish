package de.enough.skylight.renderer;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.node.CssElement;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends BlockContainingBlock {

	public static class RenderStage {
		public static final int BODY = 0x00;
		
		public static final int FLOAT = 0x01;
		
		public static final int RELATIVE = 0x02;
		
		public static final int ABSOLUTE = 0x03;
	}
	
	int renderStage = RenderStage.BODY;
	
	boolean ready;
	
	CssElement rootElement;
	
	ArrayList userEventListeners;
	
	ViewportContext context;
	
	public Viewport(ViewportContext context) {
		//#style viewport
		super();
		
		this.userEventListeners = new ArrayList();
		
		this.context = context;
		
		getLayoutDescriptor().setViewport(this);
	}
	
	public void reset() {
		if(this.body != null) {
			this.body.clear();
		}
		
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

	public int getRenderStage() {
		return this.renderStage;
	}

	public void setRenderStage(int renderStage) {
		this.renderStage = renderStage;
	}

	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if(isReady()) {
			setRenderStage(RenderStage.BODY);
			super.paint(x, y, leftBorder, rightBorder, g);
			
			setRenderStage(RenderStage.FLOAT);
			super.paint(x, y, leftBorder, rightBorder, g);
			
			setRenderStage(RenderStage.RELATIVE);
			super.paint(x, y, leftBorder, rightBorder, g);
			
			setRenderStage(RenderStage.ABSOLUTE);
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
