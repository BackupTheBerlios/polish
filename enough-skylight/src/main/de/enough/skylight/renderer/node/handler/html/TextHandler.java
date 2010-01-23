package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class TextHandler extends BodyElementHandler{
	
	static TextHandler instance = new TextHandler();

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
		TextBlock textBlock = new TextBlock();
		
		DomNode node = element.getNode();
		textBlock.setText(node.getNodeValue());
		
		return textBlock;
	}
}
