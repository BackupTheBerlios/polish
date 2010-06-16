package de.enough.skylight.renderer.node;

import de.enough.ovidiu.StyleManager;
import javax.microedition.lcdui.Image;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;

public class ImgCssElement extends CssElement {
	Image image;
	
	public ImgCssElement(NodeHandler handler, DomNodeImpl node, CssElement parent,
			ViewportContext viewportContext) {
		super(handler, node, parent, viewportContext);
	}

        protected void buildStyle()
        {
            super.buildStyle();
            style.addAttribute(StyleManager.DISPLAY_ID, "inline-block");
        }
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
}
