package de.enough.skylight.renderer.node.handler.skylight;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeUtils;

public class PropertyHandler extends SkylightNodeHandler{

	String property;
	
	public String getTag() {
		return "sl:property";
	}

	public void handleNode(CssElement element) throws ClassCastException,
			IllegalArgumentException {
		DomNode node = element.getNode();
		String key = NodeUtils.getAttributeValue(node, "key");
		
		// get DataHandler class and get text
		
		if(key == null) {
			throw new IllegalArgumentException(getTag() + "has no key");
		}
		
		String property = System.getProperty( key );
		
		this.property = property;
	}

	public Item createContent(CssElement element) {
		TextBlock textBlock = new TextBlock();
		textBlock.setText(this.property);
		return textBlock;
	}
}
