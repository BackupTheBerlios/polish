package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class BrHandler extends ElementHandler{
	
	public void handleNode(Container parent, DomNode node) {}
	
	public Style getDefaultStyle() {
		//#style br
		return new Style();
	}
}
