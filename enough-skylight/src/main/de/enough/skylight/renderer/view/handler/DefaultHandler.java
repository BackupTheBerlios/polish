package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.css.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class DefaultHandler extends NodeHandler{
	
	static DefaultHandler instance = new DefaultHandler();

	public static NodeHandler getInstance() {
		return instance;
	}
	
	public String getTag() {
		return null;
	}
	
	public void handleNode(DomNode node) {
		// hey hey what can i do ?
	}

	public Item createContent(DomNode node) {
		return null;
	}
}
