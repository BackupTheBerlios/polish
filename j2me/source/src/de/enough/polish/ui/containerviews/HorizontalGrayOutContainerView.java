//#condition polish.usePolishGui && polish.midp2

package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
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
public class HorizontalGrayOutContainerView extends ContainerView {
	
	private int targetOffset;
	private int offset = -1;
	private boolean animateItems;
	
	private transient int[][] inactiveIcons = null;
	private boolean isInitialized;
	
	/**
	 * Creates a new view
	 */
	public HorizontalGrayOutContainerView() {
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
		int height = 0;
		
		int completeWidth = 0;
		Item[] items = parent.getItems();
		
		this.inactiveIcons = new int[items.length][];
		
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			
			int itemHeight = item.getItemHeight(lineWidth, lineWidth);
			int itemWidth = item.itemWidth;
			if (itemWidth == 0)  {
				this.inactiveIcons[i] = new int[0];
				continue;
			}
			int rgbData[] = UiAccess.getRgbData(item);
			convertToGrayScale(rgbData);
			this.inactiveIcons[i] = rgbData;
			
			if (itemHeight > height ) {
				height = itemHeight;
			}
			item.relativeX = completeWidth;
			item.relativeY = 0;
			completeWidth += itemWidth + this.paddingHorizontal;
			if (item.appearanceMode != Item.PLAIN) {
				this.appearanceMode = Item.INTERACTIVE;
			}
		}
		
		this.contentHeight = height;
		this.contentWidth = lineWidth;
						
		this.animateItems = true;
		this.isInitialized = true;
		
		if (this.focusedItem == null && items.length > 0) {
			Item item = items[0];
			this.focusItem(0, item, Canvas.RIGHT, item.getFocusedStyle());
		}
	}
	
	public Style focusItem(int index, Item item, int direction, Style focusedStyle) {
		if (this.isInitialized) {
			int focusOffset = (this.contentWidth >> 1) - (item.itemWidth >> 1);
			this.targetOffset = focusOffset;
			
			for(int i=0; i< index; i++)
			{
				this.targetOffset -= this.parentContainer.get(i).itemWidth;
			}
			
			if(this.offset == -1)
			{
				this.offset = this.targetOffset;
			}
			
			this.animateItems = true;
		}
		
		return super.focusItem(index, item, direction, focusedStyle);
	}
	
	public boolean animate() {
		boolean animated = super.animate();
		int delta; 
		
		if(this.animateItems)
		{
			delta = Math.abs(this.targetOffset - this.offset) / 3;
			
			if(delta < 1) {
				delta = 1;
			}
			
			if(this.targetOffset > this.offset)
			{
				this.offset += delta;
			}
			else if(this.targetOffset < this.offset)
			{
				this.offset -= delta;
			}
			else if(this.targetOffset == this.offset)
			{
				this.animateItems = false;
			}
			
			animated = true;
		}
		
		return animated;
	}

	protected void convertToGrayScale(int[] rgbData)
	{
		int color,red,green,blue,alpha;
		for(int i = 0;i < rgbData.length;i++){
			color = rgbData[i];			
			
			alpha = (0xFF000000 & color);
			red = (0x00FF & (color >>> 16));	
			green = (0x0000FF & (color >>> 8));
			blue = color & (0x000000FF );
			
			int brightness = ((red + green + blue) / 3 ) & 0x000000FF;
			color = (brightness << 0)
				|   (brightness << 8)
				|   (brightness << 16);
			color |= alpha;
			rgbData[i] = color;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int clipEndX = clipX + clipWidth;
		int xStart = x + this.offset; 
		int itemStartX;
		int itemEndX;
		for(int i = 0; i < myItems.length; i++)
		{
			Item item = myItems[i];
			itemStartX = xStart + item.relativeX;
			if (itemStartX < clipEndX) {
				itemEndX = itemStartX + item.itemWidth;
				if (itemEndX > clipX) {
					int[] rgbData = this.inactiveIcons[i];
					DrawUtil.drawRgb( rgbData, itemStartX, y, item.itemWidth, item.itemHeight, true, g );
				}
			}
		}
		
		if (this.focusedItem != null) {
			
			int focusOffset = x + (this.contentWidth >> 1) - (this.focusedItem.itemWidth >> 1);
			g.clipRect(focusOffset, y, this.focusedItem.itemWidth, this.contentHeight);
			
			for(int i = 0; i < myItems.length; i++)
			{
				Item item = myItems[i];
				itemStartX = xStart + item.relativeX;
				if (itemStartX < clipEndX) {
					itemEndX = itemStartX + item.itemWidth;
					if (itemEndX > clipX) {
						item.paint(itemStartX, y, itemStartX, itemStartX + item.itemWidth, g);
					}
				}
			}
			g.setClip( clipX, clipY, clipWidth, clipHeight );
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

	//#ifdef polish.hasPointerEvents
	public boolean handlePointerPressed(int x, int y) {
		return super.handlePointerPressed(x + this.offset, y);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerReleased(int x, int y) {
		return super.handlePointerReleased(x + this.offset, y);
	}	
	//#endif
	
}

