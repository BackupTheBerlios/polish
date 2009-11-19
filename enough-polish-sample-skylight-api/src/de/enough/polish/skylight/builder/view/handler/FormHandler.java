package de.enough.polish.skylight.builder.view.handler;

import de.enough.polish.skylight.builder.view.Common;
import de.enough.polish.skylight.builder.view.NodeHandler;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;

public class FormHandler extends NodeHandler{
	
	public Container createContainingBlock() {
		return Common.inline();
	}

	public void handleNode(Container parent, DomNode node) {
		// handle action
	}
}
