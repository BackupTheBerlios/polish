package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.Common;
import de.enough.skylight.renderer.view.NodeHandler;

public class H1Handler extends NodeHandler{
	
	public void handleNode(Container parent, DomNode node) {}
	
	public Container createContainingBlock() {
		return Common.inline();
	}

	public Style getTextStyle() {
		//#style h1
		return new Style();
	}
}
