package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.css.CssElement;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class DivHandler extends NodeHandler{
	
	public String getTag() {
		return "div";
	}
	
	public Style getDefaultStyle() {
		//#style div
		return new Style();
	}

	public void handleNode(DomNode node) {}

	public Item createContent(DomNode node) {
		return null;
	}

}
