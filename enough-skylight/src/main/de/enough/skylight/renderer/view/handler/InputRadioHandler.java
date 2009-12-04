package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.AttributeUtils;
import de.enough.skylight.renderer.viewport.NodeHandler;

public class InputRadioHandler extends NodeHandler{
	public void handleNode(DomNode node) {}

	public Item createNodeItem(DomNode node, Style style) {
		//TODO make independent choice item
		return new ChoiceItem("",null,ChoiceGroup.EXCLUSIVE,style);
	}
	
	
}
