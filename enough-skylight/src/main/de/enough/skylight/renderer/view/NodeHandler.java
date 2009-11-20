package de.enough.skylight.renderer.view;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.Common;

public abstract class NodeHandler {
	
	HashMap directory;
	
	Container root;
	
	public NodeHandler() {
		this.directory = new HashMap();
	}
	
	public abstract void handleNode(Container parent, DomNode node);
	
	public Style getTextStyle() {
		//#style text
		return new Style();
	}
	
	public void handleText(Container parent, String text, Style style, DomNode parentNode) {
		text = text.trim();
		if(!text.equals("")) {
			StringItem textItem = new StringItem(null,text, style);
			
			parent.add(textItem);
		}
	}
	
	public void setRoot(Container root) {
		this.root = root;
	}
	
	public Container getRoot() {
		return this.root;
	}
	
	public HashMap getDirectory() {
		return this.directory;
	}

	public Container createContainingBlock() {
		return Common.block();
	}
}
