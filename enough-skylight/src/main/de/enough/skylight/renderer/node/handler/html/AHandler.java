package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeUtils;

public class AHandler extends BodyNodeHandler{

	public String getTag() {
		return "a";
	}
	
	public void handleNode(CssElement element) {}
	
	public void handleInteraction(CssElement element) {
		DomNode node = element.getNode();
		String href = NodeUtils.getAttributeValue(node, "href");
		
		if(href != null) {
			//#debug sl.debug.event
			System.out.println(element + " has events");
			
			element.setInteractive(true);
		}
	}

	public Style getDefaultStyle(CssElement element) {
		//#style a
		return new Style();
	}

	public Item createContent(CssElement element) {
		return null;
	}
}
