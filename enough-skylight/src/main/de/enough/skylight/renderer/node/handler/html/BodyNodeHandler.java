package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.CssStyle;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class BodyNodeHandler extends HtmlNodeHandler {

	public void setContent(CssElement element, Item item) {
		Style style = element.getStyle();
		CssStyle.apply(style, item);
	}

	public boolean isValid(DomNode node) {
		return super.isValid(node) && NodeUtils.getAncestor(node, "body") != null; 
	}
}
