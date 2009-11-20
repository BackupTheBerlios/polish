package de.enough.skylight.renderer.builder.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.builder.view.Common;
import de.enough.skylight.renderer.builder.view.NodeHandler;

public class H1Handler extends NodeHandler{
	
	public void handleNode(Container parent, DomNode node) {}

	public void handleText(Container parent, String text) {
		text = text.trim();
		if(!text.equals("")) {
			//#style h1
			StringItem textItem = new StringItem(null,text);
			
			parent.add(textItem);
		}
	}
}
