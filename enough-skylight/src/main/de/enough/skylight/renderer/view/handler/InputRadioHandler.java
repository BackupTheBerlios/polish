package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.NodeUtils;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class InputRadioHandler extends NodeHandler {
	public String getTag() {
		return null;
	}

	public void handleNode(DomNode node) {
	}

	public Item createContent(DomNode node) {
		// TODO make independent choice item
		return new ChoiceItem("", null, ChoiceGroup.EXCLUSIVE);
	}
}
