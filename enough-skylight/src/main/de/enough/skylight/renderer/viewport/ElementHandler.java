package de.enough.skylight.renderer.viewport;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.AnonymousBlock;
import de.enough.skylight.renderer.view.element.ContainingBlock;

public abstract class ElementHandler {
	Style style;
	
	Style textStyle;
	
	ContainingBlock formatting;
	
	Viewport viewport;
	
	public abstract void handleNode(Container parent, DomNode node);
	
	public void handleText(Container parent, DomNode node, Style style, DomNode parentNode) {
		AnonymousBlock anonymousBlock = new AnonymousBlock(node, style);
			
		parent.add(anonymousBlock);
	}
	
	public Style getStyle(DomNode node) {
		String clazz = AttributeUtils.getValue(node, "class");
		
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
		
		return getDefaultStyle();
	}
	
	public Style getDefaultStyle() {
		if(this.style == null) {
			//#style element
			this.style = new Style();
		} 
		
		return this.style;
	}
	
	public Style getDefaultTextStyle() {
		if(this.textStyle == null) {
			//#style text
			this.textStyle = new Style();
		} 
		
		return this.textStyle;
	}
	
	public HashMap getDirectory() {
		HashMap viewportDirectory = this.viewport.getDirectory();
		HashMap handlerDirectory = (HashMap)this.viewport.getDirectory().get(this);
		
		if(handlerDirectory == null) {
			handlerDirectory = new HashMap();
			viewportDirectory.put(this, handlerDirectory);
		}
		
		return handlerDirectory;
	}
	
	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public Viewport getViewport() {
		return this.viewport;
	}
}
