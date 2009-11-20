package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.NodeHandler;

public class BrHandler extends NodeHandler{
	
	public void handleNode(Container parent, DomNode node) {
		//#style br
		StringItem breakItem = new StringItem(null,"br");
		parent.add(breakItem);
	}	
}
