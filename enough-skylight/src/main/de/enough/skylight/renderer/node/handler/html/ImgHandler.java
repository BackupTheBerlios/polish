package de.enough.skylight.renderer.node.handler.html;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgElement;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class ImgHandler extends BodyElementHandler{
	
	public String getTag() {
		return "img";
	}

	public CssElement createElement(DomNode node, CssElement parent, Viewport viewport) {
		return new ImgElement(this, node, parent, viewport);
	}
	
	public void handleNode(CssElement element) {
		ImgElement imgElement = (ImgElement)element;
		
		String src = NodeUtils.getAttributeValue(element.getNode(), "src");
		
		Image image;
		try {
			image = Image.createImage(src);
			imgElement.setImage(image);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to open image " + src);
		} 
	}

	public Item createContent(CssElement element) {
		ImgElement imgElement = (ImgElement)element;
		
		Style style = element.getStyle();
		Image image = imgElement.getImage();
		ImageItem item = new ImageItem(null,image,Graphics.LEFT | Graphics.TOP,null,style);
			
		item.setAppearanceMode(Item.PLAIN);
		
		return item;
	}
}
