package de.enough.skylight.renderer.builder.view;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.builder.view.Common;

public abstract class NodeHandler {
	
	public Container createContainingBlock() {
		return Common.block();
	}
	
	public abstract void handleNode(Container parent, DomNode node);
	
	public void handleText(Container parent, String text) {
		text = text.trim();
		if(!text.equals("")) {
			//#style text
			StringItem textItem = new StringItem(null,text);
			
			parent.add(textItem);
		}
	}
}
