package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class DefaultHandler extends ElementHandler{
	
	static DefaultHandler instance = new DefaultHandler();

	public static ElementHandler getInstance() {
		return instance;
	}
	
	public void handleNode(Container parent, DomNode node) {
		// hey hey what can i do ?
	}

	
}
