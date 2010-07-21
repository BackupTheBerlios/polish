package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.modeller.StyleManager;
import de.enough.skylight.renderer.element.TextBlock;
import de.enough.skylight.renderer.node.CssElement;

public class LiHandler extends BodyNodeHandler{
	
	public String getTag() {
		return "li";
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style div
		Style style = new Style();
                style.addAttribute(StyleManager.DISPLAY_ID, "list-item");
                return style;
	}

	public void handleNode(CssElement element) {}

	public Item createContent(CssElement element) {
		return null;
	}	
}
