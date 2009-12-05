package de.enough.skylight.renderer.viewport;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.CssElement;

public abstract class NodeHandler {
	Style style;
	
	Viewport viewport;
	
	public NodeHandler() {
		this.style = getDefaultStyle();
	}
	
	public abstract void handleNode(DomNode node);
	
	public int getType() {
		return CssElement.Type.CONTAINING_BLOCK;
	}
	
	public Item createNodeItem(DomNode node) {
		return null;
	}
	
	public Style getStyle(DomNode node) {
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			try {
				Style style = getViewport().getStyle(clazz);
				
				return style;
			} catch(NoSuchFieldError e) {
				//#debug error
				System.out.println("style " + clazz + " could not be found");
			}
		}
		
		return this.style;
	}
	
	public Style getDefaultStyle() {
		//#style element
		return new Style();
	}
	
	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}
}
