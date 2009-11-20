package de.enough.skylight.renderer.builder.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.builder.view.Common;
import de.enough.skylight.renderer.builder.view.NodeHandler;

public class FormHandler extends NodeHandler{
	
	public Container createContainingBlock() {
		return Common.inline();
	}

	public void handleNode(Container parent, DomNode node) {
		// handle action
	}
}
