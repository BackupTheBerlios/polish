package de.enough.skylight.renderer.node.handler.html;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.ContentException;
import de.enough.polish.content.transform.impl.ImageContentTransform;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgCssElement;
import de.enough.skylight.renderer.node.NodeUtils;
import de.enough.util.UrlUtil;

public class ImgHandler extends BodyNodeHandler{
	
	public String getTag() {
		return "img";
	}

	public CssElement createElement(DomNodeImpl node, CssElement parent, ViewportContext viewportContext) {
		return new ImgCssElement(this, node, parent, viewportContext);
	}
	
	public void handleNode(CssElement element) {
		ImgCssElement imgElement = (ImgCssElement)element;
		ViewportContext context = element.getViewportContext();
		
		String src = NodeUtils.getAttributeValue(element.getNode(), "src");
		
		try {
			String url = UrlUtil.completeUrl(src, context.getLocationHost());
			
			ContentDescriptor descriptor = new ContentDescriptor(url);
			descriptor.setTransformID(ImageContentTransform.ID);
			Image image = (Image) context.getContentLoader().loadContent(descriptor);
			
			imgElement.setImage(image);
		} catch (ContentException e) {
			//#debug error
			System.out.println("unable to load image " + src);
		} 
	}

	public Item createContent(CssElement element) {
		ImageItem item = new ImageItem(null,null,Graphics.LEFT | Graphics.TOP,null);
		
		setContent(element,item);
		
		return item;
	}
	
	public void setContent(CssElement element, Item item) {
		super.setContent(element, item);
		
		ImgCssElement imgElement = (ImgCssElement)element;
		ImageItem imgItem = (ImageItem)item; 
		
		Image image = imgElement.getImage();
		
		imgItem.setImage(image);
		if(element.isInteractive()) {
			imgItem.setAppearanceMode(Item.INTERACTIVE);
		} else {
			imgItem.setAppearanceMode(Item.PLAIN);
		}
	}
	
	public int getContentType() {
		return CssElement.CONTENT_IMAGE;
	}
}
