package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class H1Handler extends ElementHandler{
	
	public void handleNode(Container parent, DomNode node) {}
	
	public Style getStyle() {
		//#style h1
		return new Style();
	}

	public Style getTextStyle() {
		//#style h1
		return new Style();
	}
	
	
}
