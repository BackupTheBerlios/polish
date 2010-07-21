package de.enough.skylight.renderer;

import de.enough.polish.ui.Container;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;

/**
 * A viewport to render to
 * @author Andre Schmidt
 *
 */
public class Viewport extends Container {

	public static class RenderStage {
		public static final int BODY = 0x00;
		public static final int FLOAT = 0x01;
		public static final int RELATIVE = 0x02;
		public static final int ABSOLUTE = 0x03;
	}
	
	private int renderStage = RenderStage.BODY;
	private boolean ready;
	private CssElement rootElement;
	private ViewportContext context;
	
	public Viewport(ViewportContext context) {
		//#style viewport
		super(false);
		this.context = context;
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
	
	

//	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
//		LayoutModeler.paintRootBox(g);
//	}
	
	public void nodeUpdated(DomNode node) {
		CssElement element = CssElement.getElementWithNode(this.rootElement, node);
		element.update();
	}
	
	public ViewportContext getContext() {
		return this.context;
	}

	@Override
	public String toString() {
		return "Viewport";
	}

}
