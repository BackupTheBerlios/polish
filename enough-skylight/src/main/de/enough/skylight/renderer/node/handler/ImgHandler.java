package de.enough.skylight.renderer.node.handler;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.NodeUtils;

public class ImgHandler extends NodeHandler{
	public String getTag() {
		return "img";
	}
	
	public void handleNode(DomNode node) {

	}

	public Item createContent(DomNode node, Style style) {
		String src = NodeUtils.getAttributeValue(node, "src");
		
		Image image;
		try {
			image = Image.createImage(src);
			
			ImageItem item = new ImageItem(null,image,Graphics.LEFT | Graphics.TOP,null,style);
			
			item.setAppearanceMode(Item.PLAIN);
			
			return item;
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to open image " + src);
		} 

		return null;
	}
	
	
}
