package de.enough.skylight.renderer.node.handler.rss;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class TextHandler extends ItemNodeHandler{
	
	static TextHandler instance = new TextHandler();

	public static NodeHandler getInstance() {
		return instance;
	}
	
	public String getTag() {
		return "#text";
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

	public boolean isValid(DomNode node) {
		return super.isValid(node) && node.getNodeType() == DomNode.TEXT_NODE;
	}
}
