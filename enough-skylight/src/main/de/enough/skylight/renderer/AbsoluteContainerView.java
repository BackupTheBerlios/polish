package de.enough.skylight.renderer;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;

public class AbsoluteContainerView extends ContainerView {

	/**
	 * Initializes this container view. 
	 * The implementation needs to calculate and set the contentWidth and 
	 * contentHeight fields. 
	 * The style of the focused item has already been set.
	 * When the contentWidth will be larger than the specified availWidth, the container view allows to scroll horizontally automatically using pointer events.
	 * 
	 * @param firstLineWidth the maximum width of the first line 
	 * @param availWidth the maximum width of any following lines
	 * @param parentContainerItem the Container which uses this view, use parent.getItems() for retrieving all items. 
	 *  
	 * @see #contentWidth
	 * @see #contentHeight
	 */
	protected void initContent( Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight ) {
		
//		Item[] myItems = this.parentContainer.getItems();
//		for (int i = 0; i < myItems.length; i++) {
//			Item item = myItems[i];
//			item.init(availWidth, availWidth, availHeight);
//		}
	}
	
	/**
	 * Paints the content of this container view.
	 * This method adjusts the x and y offsets and forwards the call to paintContent(Container, Item[], int, int, int, int, int, int, int, int, Graphics)
	 * 
	 * @param parent the parent item
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 * @see #paintContent(Container, Item[], int, int, int, int, int, int, int, int, Graphics)
	 */
	protected void paintContent( Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g ) {
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		Item[] myItems = this.parentContainer.getItems();
		
		for (int i = 0; i < myItems.length; i++) {
			if (i != this.focusedIndex) {
				Item item = myItems[i];
				//System.out.println("item " + i + " at " + item.relativeX + "/" + item.relativeY);
				int itemX = x + item.relativeX;
				int itemY = y + item.relativeY;
				leftBorder = itemX;
				rightBorder = itemX + item.itemWidth;
				paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
	}
}
