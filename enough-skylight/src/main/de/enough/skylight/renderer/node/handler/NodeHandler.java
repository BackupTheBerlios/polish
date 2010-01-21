package de.enough.skylight.renderer.node.handler;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.builder.Element;
import de.enough.skylight.renderer.node.NodeUtils;

public abstract class NodeHandler {
	Style style;
	
	Viewport viewport;
	
	public NodeHandler() {
		this.style = getDefaultStyle();
	}
	
	public abstract String getTag();
	
	public abstract void handleNode(DomNode node);
	
	public abstract Item createContent(DomNode node);
	
	public Style getStyle(DomNode node) {
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			Style style = StyleSheet.getStyle(clazz);
			
			if(style != null) {
				return style;
			} else {
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
