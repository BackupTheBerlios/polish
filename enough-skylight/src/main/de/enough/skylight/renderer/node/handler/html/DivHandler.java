package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;

public class DivHandler extends BodyNodeHandler{
	
	public String getTag() {
		return "div";
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style div
		return new Style();
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}

}
