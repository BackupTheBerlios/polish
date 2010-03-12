package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Canvas;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.UiAccess;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.layout.LayoutAttributes;
import de.enough.skylight.renderer.node.CssElement;

public class ElementView extends ItemView  {
	
	protected void initContent(Item parent, int firstLineWidth, int availWidth,
			int availHeight) {
		UiAccess.initContent(parent, firstLineWidth, availWidth, availHeight);
		this.contentHeight = parent.getContentHeight();
		this.contentWidth = parent.getContentWidth();
	}

	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		UiAccess.paintContent(parent, x, y, leftBorder, rightBorder, g);
	}
	
	/*public boolean animate() {
		System.out.println("animate");
		return this.parentItem.animate();
	}

	protected void addFullRepaintRegion(Item item, ClippingRegion repaintRegion) {
		System.out.println("adding repaint region");
		repaintRegion.addRegion( 0, 
				0, 
				240,
				320 
		);
	}*/

	public boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = super.handleKeyReleased(keyCode, gameAction);
		
		if(gameAction == Canvas.FIRE) {
			CssElement element = LayoutAttributes.get(this.parentItem).getElement();
			if(element != null && element.isInteractive()) {
				Viewport viewport = element.getViewport();
				UserEvent event = new UserEvent();
				viewport.notifyUserEvent(element, event);
			}
		}
		
		return handled;
	}

}
