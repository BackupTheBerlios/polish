//#condition polish.usePolishGui

/*
 * @(#)MIDP2LayoutView.java
 * Created on 6/02/2005
 * Copyright 2005 by Majitek International Pte. Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek International Pte Ltd.
 * Use is subject to license terms.
 */

package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.util.ArrayList;
import de.enough.polish.ui.*;

/**
 * <code>MIDP2LayoutView</code>.
 * <p>
 * 
 * @author Kin Wong
 */
public class MIDP2LayoutView extends ContainerView {
	static private int LAYOUT_HORIZONTAL = Item.LAYOUT_LEFT
			| Item.LAYOUT_CENTER | Item.LAYOUT_RIGHT;

	static private int LAYOUT_VERTICAL = Item.LAYOUT_TOP | Item.LAYOUT_VCENTER
			| Item.LAYOUT_BOTTOM;

	class RowItem {
		int x;
		int y;
		int width;
		int height;
		Item item;
	}
	private ArrayList allRows;
	private ArrayList currentRow;
	private int rowWidth;
	private int rowHeight;
	private Style style;

	/**
	 * Constructs an instance of <code>MIDP2LayoutView</code>.
	 */
	public MIDP2LayoutView() {
		// no initialisation necessary
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container,
	 *      int, int)
	 */
	protected void initContent(Container parent, int firstLineWidth,
			int lineWidth) {
		Item[] myItems = parent.getItems();
		this.contentHeight = this.contentWidth = this.rowWidth = this.rowHeight = 0;
		this.currentRow = new ArrayList();
		this.allRows = new ArrayList();

		boolean hasFocusableItem = false;
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (item.appearanceMode != Item.PLAIN) {
				hasFocusableItem = true;
			}
			appendItemToRow(i, item, firstLineWidth, lineWidth);
		}
		// Make the remaining items a final line
		rowBreak(firstLineWidth);
		if (!hasFocusableItem) {
			this.appearanceMode = Item.PLAIN;
		} else {
			this.appearanceMode = Item.INTERACTIVE;
		}
	}

	protected void setStyle(Style style) {
		super.setStyle(style);
		this.style = style;
	}

	void appendItemToRow(int index, Item item, int firstLineWidth, int lineWidth) {
		if (this.focusFirstElement && (item.appearanceMode != Item.PLAIN)) {
			focusItem(index, item);
			this.focusFirstElement = false;
		}
		int width = item.getItemWidth(firstLineWidth, lineWidth);
		int height = item.getItemHeight(firstLineWidth, lineWidth);
		int layout = item.getLayout();

		if ((Item.LAYOUT_NEWLINE_BEFORE == (layout & Item.LAYOUT_NEWLINE_BEFORE))
				|| (this.rowWidth + this.paddingHorizontal + width > lineWidth)) {
			// Break if the NEWLINE_BEFORE is specified or not enough
			// room in the current row
			rowBreak(lineWidth);
		}

		this.rowWidth += width;
		if (this.currentRow.size() == 0) {
			this.rowHeight = height;
		} else {
			if (this.rowHeight < height)
				this.rowHeight = height;
			this.rowWidth += this.paddingHorizontal;
		}

		RowItem rowItem = new RowItem();
		rowItem.width = width;
		rowItem.height = height;
		rowItem.item = item;
		this.currentRow.add(rowItem);

		if (Item.LAYOUT_NEWLINE_AFTER == (layout & Item.LAYOUT_NEWLINE_AFTER)) {
			rowBreak(lineWidth);
		}
	}

	private void rowBreak(int lineWidth) {
		if (this.currentRow.size() == 0) {
			return; // Current row is empty
		}
		/**
		 * Horizontal Layout Starts Here!
		 */
		// Take away all the horizontal paddings
		int remainingWidth = lineWidth
				- ((this.currentRow.size() - 1) * this.paddingHorizontal);

		RowItem[] requiredExpanded = null;
		int requiredExpandedIndex = 0;
		for (int i = 0; i < this.currentRow.size(); i++) {
			RowItem rowItem = (RowItem) this.currentRow.get(i);
			remainingWidth -= rowItem.width;
			if (Item.LAYOUT_EXPAND == (rowItem.item.getLayout() & Item.LAYOUT_EXPAND)) {
				if (requiredExpanded == null) {
					requiredExpanded = new RowItem[this.currentRow.size() - i];
				}
				requiredExpanded[requiredExpandedIndex++] = rowItem;
			}
		}
		// Distribute the remaining width to the item that requires expanding
		if (requiredExpanded != null) {
			int expansion = remainingWidth / requiredExpandedIndex;
			remainingWidth = remainingWidth % requiredExpandedIndex;
			for (int i = 0; i < requiredExpandedIndex; i++) {
				RowItem rowItem = requiredExpanded[i];
				rowItem.width += expansion;
				if (i == 0) {
					// The first item get all the rounding
					//rowItem.width += remainingWidth;
				}
			}
		}

		// Horizontal Positioning determined by the first item in a row
		RowItem rowItem = (RowItem) this.currentRow.get(0);
		int rowHorizontalLayout = (rowItem.item.getLayout() & LAYOUT_HORIZONTAL);
		if (requiredExpanded != null)
			rowHorizontalLayout = Item.LAYOUT_LEFT;

		int x = 0;
		switch (rowHorizontalLayout) {
			case Item.LAYOUT_CENTER :
				x = (remainingWidth >> 1);
				break;

			case Item.LAYOUT_RIGHT :
				x = remainingWidth;
				break;
		}

		for (int i = 0; i < this.currentRow.size(); i++) {
			rowItem = (RowItem) this.currentRow.get(i);
			rowItem.x = x;
			x += rowItem.width + this.paddingHorizontal; // Next Item

			/**
			 * Vertical Layout starts here
			 */
			int layout = rowItem.item.getLayout();
			rowItem.y = this.contentHeight;
			if (Item.LAYOUT_VEXPAND == (layout & Item.LAYOUT_VEXPAND)) {
				// Vertical expansion is required, ignore all other
				rowItem.height = this.rowHeight;
			} else {
				layout = (layout & LAYOUT_VERTICAL);
				switch (layout) {
					case Item.LAYOUT_BOTTOM :
						rowItem.y += this.rowHeight - rowItem.height;
						break;

					case Item.LAYOUT_VCENTER :
						rowItem.y += ((this.rowHeight - rowItem.height) >> 1);
						break;
				}
			}
		}

		if (this.allRows.size() == 0) {
			// Adding first row
			this.contentHeight += this.rowHeight;
		} else {
			this.contentHeight += this.paddingVertical + this.rowHeight;
		}

		// Get ready for next row
		this.allRows.add(this.currentRow);
		this.rowHeight = this.rowWidth = 0;
		this.currentRow = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int,
	 *      javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
				Graphics g) 
	{
		for (int i = 0; i < this.allRows.size(); i++) {
			ArrayList row = (ArrayList) this.allRows.get(i);
			for (int j = 0; j < row.size(); j++) {
				RowItem rowItem = (RowItem) row.get(j);
				int xItem = x + rowItem.x;
				rowItem.item
						.paint(xItem, y + rowItem.y, Math
								.max(leftBorder, xItem), Math.min(rightBorder,
								xItem + rowItem.width), g);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem(int keyCode, int gameAction) {
		Item item = null;
		Item[] items = this.parentContainer.getItems();
		if ((gameAction == Canvas.DOWN) || (gameAction == Canvas.RIGHT)) {
			item = getNextFocusableItem(items, true, 1, true);
		} else if ((gameAction == Canvas.UP) || (gameAction == Canvas.LEFT)) {
			item = getNextFocusableItem(items, false, 1, true);
		}

		if (item != null) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] == item) {
					focusItem(i, item);
					break;
				}
			}
		}
		return item;
	}
}

