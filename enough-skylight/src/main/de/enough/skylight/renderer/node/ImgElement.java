package de.enough.skylight.renderer.node;

import javax.microedition.lcdui.Image;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;

public class ImgElement extends CssElement {
	Image image;
	
	public ImgElement(NodeHandler handler, DomNode node, CssElement parent,
			Viewport viewport) {
		super(handler, node, parent, viewport);
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
}
