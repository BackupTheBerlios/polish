package de.enough.skylight.renderer;

import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.modeller.Box;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgCssElement;
import de.enough.skylight.renderer.node.TextCssElement;

// TODO: Use the event system properly. The event engine should not be the renderer.
public class BoxRenderer {

	private ArrayList<RendererListener> rendererListeners = new ArrayList<RendererListener>();
	private int state;
	
	public void addRendererListener(RendererListener rendererListener) {
		this.rendererListeners.add(rendererListener);
	}
	
	// TODO: Remove the viewPort parameter. Inject the parameter in the constructor. Create a domain-clean Renderer interface
	// with method render(Box).
	public void render(Viewport viewPort, Box rootBox) {
		//#debug
		System.out.println("BoxRenderer.render():starting.");
		ArrayList itemStack = new ArrayList();
		int lastItemIndex;
		BoxContainer parentItem;
		Item item;
		BoxContainer rootBoxContainer = new BoxContainer(rootBox);
		viewPort.add(rootBoxContainer);
		// Used to traverse the tree hierarchy in depth-first order.
		itemStack.add(rootBoxContainer);
		Box parentBox;
		Box childBox;
		while(true) {
			if(itemStack.size() == 0) {
				break;
			}
			lastItemIndex = itemStack.size()-1;
			parentItem = (BoxContainer)itemStack.remove(lastItemIndex);
			parentBox = parentItem.getBox();
			int numberOfChildren = parentBox.children.size();
			for (int i = 0; i < numberOfChildren; i++) {
				childBox = (Box)parentBox.children.get(i);
				// TODO: The information about the contentType should come from the DomNode, not from the style.
				CssElement cssElement = childBox.correspondingNode;
				// It will be a container if it has children or is not a specialied item but like an empty div.
				if(childBox.children.size() != 0 || cssElement.getClass().equals(CssElement.class)) {
					item = new BoxContainer(childBox);
					itemStack.add(item);
					parentItem.add(item);
					continue;
				}
				if(cssElement instanceof TextCssElement) {
					String text = (String) cssElement.getContent();
					//#style dummyStringItem
					item = new StringItem(null,text);
					parentItem.add(item);
					//#debug
					System.out.println("BoxRenderer.createStringItem():for '"+text+"' from box:"+childBox);
					continue;
				}
				if(cssElement instanceof ImgCssElement) {
					Image image = ((ImgCssElement) cssElement).getImage();
					item = new ImageItem(null,image,Item.LAYOUT_TOP|Item.LAYOUT_LEFT,null);
					parentItem.add(item);
					continue;
				}
			}
		}
		setState(Renderer.STATE_READY);
		return;
	}

	public int getState() {
		return this.state;
	}
	
	private void setState(int state) {
		this.state = state;
		
		for (int index = 0; index < this.rendererListeners.size(); index++) {
			RendererListener listener = this.rendererListeners.get(index);
			listener.onRenderStateChange(state);
		}
	}
}
