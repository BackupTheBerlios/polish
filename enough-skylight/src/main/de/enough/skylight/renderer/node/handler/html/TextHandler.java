package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class TextHandler extends BodyNodeHandler{
	
	static TextHandler instance = new TextHandler();

	public static NodeHandler getInstance() {
		return instance;
	}
	
	public String getTag() {
		return "#text";
	}
	
	public void handleNode(CssElement element) {
	}

	public Item createContent(CssElement element) {
		Style style = element.getTextStyle();
		TextBlock textBlock = new TextBlock(style);
		
		DomNode node = element.getNode();
		textBlock.setText(node.getNodeValue());
		
		return textBlock;
	}
	
	public boolean isValid(DomNode node) {
		return super.isValid(node) && node.getNodeType() == DomNode.TEXT_NODE;
	}
	
	public void setContent(CssElement element, Item item) {}
	
	public int getContentType() {
		return CssElement.CONTENT_TEXT;
	}
}
