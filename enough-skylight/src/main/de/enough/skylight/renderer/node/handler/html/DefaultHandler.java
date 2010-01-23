package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class DefaultHandler extends NodeHandler{
	
	static DefaultHandler instance = new DefaultHandler();

	public static NodeHandler getInstance() {
		return instance;
	}
	
	public String getTag() {
		return null;
	}
	
	public void handleNode(CssElement element) {
		// hey hey what can i do ?
	}

	public Item createContent(CssElement element) {
		return null;
	}
}
