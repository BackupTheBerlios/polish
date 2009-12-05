package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class SpanHandler extends NodeHandler{
	
	public void handleNode(DomNode node) {}
	
	public Style getDefaultStyle() {
		//#style span
		return new Style();
	}
}
