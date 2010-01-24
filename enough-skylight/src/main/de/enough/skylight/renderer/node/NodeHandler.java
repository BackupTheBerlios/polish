package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.element.BlockContainingBlock;

public abstract class NodeHandler {
	public abstract String getTag();
	
	public CssElement createElement(DomNode node, CssElement parent, Viewport viewport) {
		return new CssElement(this,node,parent, viewport);
	}
	
	public void handleResources(CssElement element) {
		// handle node resources here
	}
	
	public abstract void handleNode(CssElement element) throws ClassCastException, IllegalArgumentException;
	
	public abstract Item createContent(CssElement element);
	
	public boolean isValid(DomNode node) {
		return true;
	}
	
	public Style getDefaultStyle() {
		//#style element
		return new Style();
	}
}
