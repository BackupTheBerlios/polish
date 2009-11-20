package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.NodeHandler;

public class EmHandler extends NodeHandler{
	
	public void handleNode(Container parent, DomNode node) {}

	public Style getTextStyle() {
		//#style em
		return new Style();
	}
}
