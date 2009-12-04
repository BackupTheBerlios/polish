package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class EmHandler extends NodeHandler{
	
	public Style getDefaultStyle() {
		//#style em
		return new Style();
	}

	public void handleNode(DomNode node) {}
}
