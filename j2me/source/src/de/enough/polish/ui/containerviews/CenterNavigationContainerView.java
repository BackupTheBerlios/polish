//#condition polish.usePolishGui && polish.midp2

package de.enough.polish.ui.containerviews;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.DrawUtil;


/**
 * <p>Shows  the available items of a Container in a horizontal list.</p>
 * <p>Apply this view by specifying "view-type: horizontal;" in your polish.css file.</p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * @author Andre Schmidt
 */
public class CenterNavigationContainerView extends ContainerView {
	transient static ImageItem leftItem;
	transient static ImageItem rightItem;
	
	static
	{
		try {
			//#style leftArrow?
			leftItem = new ImageItem(null,Image.createImage("/arrow_left.png"),Item.LAYOUT_LEFT,null);
			
			//#style rightArrow?
			rightItem = new ImageItem(null,Image.createImage("/arrow_right.png"),Item.LAYOUT_RIGHT,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new view
	 */
	public CenterNavigationContainerView() {
		super();
		this.allowsAutoTraversal = false;
		this.isHorizontal = true;
		this.isVertical = false;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth,
			int lineWidth) 
	{
		Container parent = (Container) parentItm;
		
		int height = parent.get(0).getItemHeight(firstLineWidth, lineWidth);
		
		this.contentHeight = height;
		this.contentWidth = lineWidth;
	}
	
	boolean animateItems = false;
	
	public Style focusItem(int index, Item item, int direction, Style focusedStyle) {
		this.animateItems = true;
		return super.focusItem(index, item, direction, focusedStyle);
	}
	
	public boolean animate() {
		boolean animated = super.animate();
		
		if(this.animateItems)
		{
			System.out.println("animateItems");
			
			this.animateItems = false;
			
			animated = true;
		}
		
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int originalLeftBorder = leftBorder;
		int originalRightBorder = rightBorder;
		
		int width = rightBorder - leftBorder;

		int leftWidth = this.leftItem.getItemWidth(width, width);
		int rightWidth = this.rightItem.getItemWidth(width, width);
		
		leftBorder += leftWidth;
		rightBorder -= rightWidth;
		
		int center = width/2;
		
		int itemWidth = myItems[0].getItemWidth(width, width);
		
		int offset = (center - (this.focusedIndex * itemWidth)) - (itemWidth / 2);  
		
		for(int i = 0; i < myItems.length; i++)
		{
			Item item = myItems[i];
			
			int xOffset = x + offset;
			
			if(xOffset > leftBorder && (xOffset + itemWidth) < rightBorder)
			{
				item.paint(xOffset, y, leftBorder, rightBorder, g);
			}
			
			offset += itemWidth;
		}
		
		leftBorder = originalLeftBorder;
		rightBorder = originalRightBorder;
		
		if(myItems.length != 0)
		{
			if(this.focusedIndex != 0)
			{
				this.leftItem.paint(leftBorder, y, leftBorder, rightBorder, g);
			}
			
			if(this.focusedIndex != (myItems.length - 1))
			{
				this.rightItem.paint(rightBorder - rightWidth, y, leftBorder, rightBorder, g);	
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style)
	{
		//#if polish.midp1
			//# return false;
		//#else
			return super.isValid(parent, style);
		//#endif
	}	
	
	
}


