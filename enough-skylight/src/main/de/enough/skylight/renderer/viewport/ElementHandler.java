package de.enough.skylight.renderer.viewport;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.view.element.FormattingBlock;

public abstract class ElementHandler {
	Style style;
	
	Style textStyle;
	
	FormattingBlock formatting;
	
	Viewport viewport;
	
	public abstract void handleNode(Container parent, DomNode node);
	
	public Style getStyle() {
		if(this.style == null) {
			//#style element
			this.style = new Style();
		} 
		
		return this.style;
	}
	
	public Style getTextStyle() {
		if(this.textStyle == null) {
			//#style text
			this.textStyle = new Style();
		} 
		
		return this.textStyle;
	}
	
	public void handleText(Container parent, String text, Style style, DomNode parentNode) {
		text = text.trim();
		if(!text.equals("")) {
			StringItem textItem = new StringItem(null,text, style);
			
			parent.add(textItem);
		}
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
	
	public FormattingBlock getFormattingBlock() {
		return new FormattingBlock(this, getStyle());
	}
}
