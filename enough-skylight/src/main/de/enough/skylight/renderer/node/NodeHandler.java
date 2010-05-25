package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ToStringHelper;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.renderer.ViewportContext;

public abstract class NodeHandler {
		
	public abstract String getTag();
	
	public CssElement createElement(DomNodeImpl node, CssElement parent, ViewportContext viewportContext) {
		return new CssElement(this,node,parent, viewportContext);
	}
	
	public void handle(CssElement element) {
		handleNode(element);
		
		handleResources(element);
		
		handleInteraction(element);
	}
	
	public void handleInteraction(CssElement element) {
		DomNode node = element.getNode();
		String onClick = NodeUtils.getAttributeValue(node, "onclick");
		
		if(onClick != null) {
			//#debug sl.debug.event
			System.out.println(element + " has events");
			
			element.setInteractive(true);
		}
	}
	
	public void handleResources(CssElement element) {
		// handle node resources here
	}
	
	public abstract void handleNode(CssElement element) throws ClassCastException, IllegalArgumentException;
	
	public abstract int getContentType();
	
	public abstract Item createContent(CssElement element);
	
	public abstract void setContent(CssElement element, Item item);
	
	public boolean isValid(DomNode node) {
		return true;
	}
	
	public Style getDefaultStyle(CssElement element) {
		//#style element
		return new Style();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper("NodeHandler").
		add("tag", getTag()).
		toString();
	}
}
