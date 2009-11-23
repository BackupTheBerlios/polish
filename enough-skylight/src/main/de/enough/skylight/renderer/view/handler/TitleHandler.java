package de.enough.skylight.renderer.view.handler;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.viewport.ElementHandler;

public class TitleHandler extends ElementHandler{
	
	public void handleNode(Container parent, DomNode node) {}

	public void handleText(Container parent, String text, Style style, DomNode parentNode) {
		getViewport().setTitle(text);
	}
}
