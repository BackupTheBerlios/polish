package de.enough.skylight.renderer.node.handler.rss;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgElement;
import de.enough.skylight.renderer.node.NodeUtils;

public class EnclosureHandler extends ItemNodeHandler {

	public String getTag() {
		return "enclosure";
	} 
	
	public CssElement createElement(DomNode node, CssElement parent, Viewport viewport) {
		return new ImgElement(this, node, parent, viewport);
	}
	
	public void handleNode(CssElement element) {
		ImgElement imgElement = (ImgElement)element;
		
		String url = NodeUtils.getAttributeValue(element.getNode(), "url");
		
		Image image;
		try {
			HttpConnection connection = (HttpConnection)Connector.open(url);
			InputStream stream = connection.openInputStream();
			image = Image.createImage(stream);
			imgElement.setImage(image);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to open image " + url);
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

	public Style getDefaultStyle() {
		//#style enclosure
		return new Style();
	}	
}
