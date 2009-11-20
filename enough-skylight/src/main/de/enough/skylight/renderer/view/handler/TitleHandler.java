package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.NodeHandler;

public class TitleHandler extends NodeHandler{
	
	public void handleNode(Container parent, DomNode node) {}

	public void handleText(Container parent, String text, DomNode parentNode) {
		getRoot().setLabel(text);
	}

	
}
