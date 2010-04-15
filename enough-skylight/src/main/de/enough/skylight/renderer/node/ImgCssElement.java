package de.enough.skylight.renderer.node;

import javax.microedition.lcdui.Image;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;

public class ImgCssElement extends CssElement {
	Image image;
	
	public ImgCssElement(NodeHandler handler, DomNode node, CssElement parent,
			ViewportContext viewportContext) {
		super(handler, node, parent, viewportContext);
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
}
