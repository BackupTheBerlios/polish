package de.enough.polish.skylight.builder.view;

import de.enough.polish.ui.Container;
import de.enough.skylight.dom.DomNode;

public abstract class NodeHandler {
	
	public Container createContainingBlock() {
		return Common.block();
	}
	
	public abstract void handleNode(Container parent, DomNode node);
	
	
}
