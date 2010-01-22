package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;

public abstract class NodeHandler {
	public abstract String getTag();
	
	public abstract void handleNode(NodeElement element) throws ClassCastException, IllegalArgumentException;
	
	public abstract Item createContent(NodeElement element);
	
	public Style getDefaultStyle() {
		//#style element
		return new Style();
	}
	
	public NodeElement createElement(DomNode node, NodeElement parent, Viewport viewport) {
		return new NodeElement(this,node,parent,viewport);
	}
}
