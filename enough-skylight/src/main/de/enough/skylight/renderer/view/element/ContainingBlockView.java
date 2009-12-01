package de.enough.skylight.renderer.view.element;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;

public class ContainingBlockView extends ContainerView
{
	
	Dimension contentX;
	
	ContainingBlock block;
	
	/**
	 * Creates a new view type
	 */
	public ContainingBlockView(ContainingBlock block)
	{
		this.block = block;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight)
	{
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		// now just adjust positions, so that the elements are layout according to their settings:
		Item[] items = this.parentContainer.getItems();
		initContent( items, firstLineWidth, availWidth, availHeight );
	}

	/**
	 * Initiates this view for the specified items
	 * @param items the items
	 * @param firstLineWidth available width for the first line
	 * @param availWidth available width for the view
	 * @param availHeight available height for the view
	 */
	public void initContent(Item[] items, int firstLineWidth, int availWidth,
			int availHeight) 
	{
		int x = 0;
		int y = 0;
		int currentRowHeight = 0;
		int currentRowStartIndex = 0;
		int maxRowWidth = 0;
		
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			
			Element element;
			try {
				element = (Element)item;
			}catch(ClassCastException e) {
				//#debug error
				System.out.println(item + "is not an element");
				return;
			}
			
			if (element.isDisplay(Element.Display.BLOCK_LEVEL) || (x + item.getContentWidth() > availWidth) ) 
			{
				if (currentRowHeight != 0) {
					lineBreak( items, currentRowStartIndex, i - 1, x, currentRowHeight, availWidth );
					y += currentRowHeight + this.paddingVertical;
					currentRowHeight = 0;
				}
				if (x > maxRowWidth) {
					maxRowWidth = x;
				}
				x = 0;
				currentRowStartIndex = i;
			}
			
			item.relativeX = x;
			item.relativeY = y;
			
			// item has probably expand layout, as the content width fits within this row:
			int itemWidth = item.getItemWidth( availWidth - x, availWidth - x, availHeight );
			int itemHeight = item.itemHeight;
			
			x += itemWidth;
			
			if (itemHeight > currentRowHeight) {
				currentRowHeight = itemHeight;
			}
			
			if (x >= availWidth || element.isDisplay(Element.Display.BLOCK_LEVEL) || (i == items.length -1)) {
				if (currentRowHeight != 0) {
					lineBreak( items, currentRowStartIndex, i, x, currentRowHeight, availWidth);
					y += currentRowHeight + this.paddingVertical;
					currentRowHeight = 0;
				}
				if (x > maxRowWidth) {
					maxRowWidth = x;
				}
				x = 0;
				currentRowStartIndex = i + 1;
			}
		}
		if (x != 0) {
			//#debug
			System.out.println("Midp2ContainerView: currentRowHeight=" + currentRowHeight + ", x=" + x + ", maxRowWidth=" + maxRowWidth);
			if (currentRowHeight != 0) {
				lineBreak( items, currentRowStartIndex, items.length-1, x, currentRowHeight, availWidth);
				y += currentRowHeight;
			}
			if (x > maxRowWidth) {
				maxRowWidth = x;
			}
		}
		
		this.contentHeight = y;
		
		// if this block is block level ...
		if(this.block.isDisplay(Element.Display.BLOCK_LEVEL)) {
			// expand width to available
			this.contentWidth = availWidth;
		// otherwise ...
		} else {
			// set width to maximum row size
			this.contentWidth = maxRowWidth;
		}
	}

	/**
	 * Adds a linebreak to the current list of items.
	 */
	private void lineBreak(Item[] items, int currentRowStartIndex, int currentRowEndIndex, int currentRowWidth, int currentRowHeight, int availWidth)
	{
		int diff = 0;
		if (this.isLayoutCenter) {
			diff = (availWidth - currentRowWidth) / 2;
		} else if (this.isLayoutRight) {
			diff = (availWidth - currentRowWidth);
		}
		for (int i=currentRowStartIndex; i <= currentRowEndIndex; i++) {
			Item item = items[i];
			int lo = item.getLayout();
			if ((lo & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				item.relativeY += (currentRowHeight - item.itemHeight) / 2;
			} else if ((lo & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				item.relativeY += (currentRowHeight - item.itemHeight);
			}
			item.relativeX += diff;
			if (i == currentRowEndIndex) {
				if  ((lo & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT)
				{
					item.relativeX = availWidth - item.itemWidth;
				}
			}
		}
	}
	
	public void paintContent(Item[] items, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		for (int i = 0; i < items.length; i++) {
			if (i != this.focusedIndex) {
				Item item = items[i];
				//System.out.println("item " + i + " at " + item.relativeX + "/" + item.relativeY);
				int itemX = x + item.relativeX;
				int itemY = y + item.relativeY;
				leftBorder = itemX;
				rightBorder = itemX + item.itemWidth;
				paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		Item focItem = this.focusedItem;
		if (focItem != null) {
			x += focItem.relativeX;
			paintItem(focItem, this.focusedIndex, x, y + focItem.relativeY, x, x + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
		}
	}
} 

