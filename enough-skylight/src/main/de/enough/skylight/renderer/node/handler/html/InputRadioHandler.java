package de.enough.skylight.renderer.node.handler.html;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class InputRadioHandler extends BodyElementHandler {
	public String getTag() {
		return null;
	}

	public void handleNode(CssElement element) {
	}

	public Item createContent(CssElement element) {
		// TODO make independent choice item
		Style style = element.getStyle();
		return new ChoiceItem("", null, ChoiceGroup.EXCLUSIVE, style);
	}
}
