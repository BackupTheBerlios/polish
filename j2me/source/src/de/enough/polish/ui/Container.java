//#condition polish.usePolishGui
/*
 * Created on 01-Mar-2004 at 09:45:32.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;

/**
 * <p>Contains a number of items.</p>
 * <p>Main purpose is to manage all items of a Form or similiar canvases.</p>
 * <p>Containers support following CSS attributes:
 * <ul>
 * 		<li><b>focused</b>: The name of the focused style, e.g. "style( funnyFocused );"
 * 				</li>
 * 		<li><b>columns</b>: The number of columns. If defined a table will be drawn.</li>
 * 		<li><b>columns-width</b>: The width of the columns. "equals" for an equal width
 * 				of each column, "normal" for a column width which depends on
 * 			    the items. One can also specify the used widths directly with
 * 				a comma separated list of integers, e.g.
 * 				<pre>
 * 					columns: 2;
 * 					columns-width: 15,5;
 * 				</pre>
 * 				</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Container extends Item {
	//#if polish.css.columns || polish.useTable
		//#define tmp.useTable
	//#endif
	
	private static final int NO_COLUMNS = 0;
	private static final int EQUAL_WIDTH_COLUMNS = 1;
	private static final int NORMAL_WIDTH_COLUMNS = 2;
	private static final int STATIC_WIDTH_COLUMNS = 3;
	
	protected ArrayList itemsList;
	protected Item[] items;
	protected boolean autoFocusEnabled;
	protected int autoFocusIndex;
	protected Style focusedStyle;
	protected Style itemStyle;
	protected Item focusedItem;
	protected int focusedIndex = -1;
	private int columnsSetting = NO_COLUMNS;
	private int numberOfColumns;
	private int[] columnsWidths;
	private int[] rowsHeights;
	private int numberOfRows;
	private boolean enableScrolling;
	int yTop;
	int yBottom;
	protected int yOffset;
	private int focusedTopMargin;
	//#ifdef polish.css.view-type
		private ContainerView view;
	//#endif
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public Container( boolean focusFirstElement ) {
		this( null, focusFirstElement, null, -1, -1 );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public Container(boolean focusFirstElement, Style style) {
		this( null, focusFirstElement, style, -1, -1 );
	}

	/**
	 * Creates a new empty container.
	 * 
	 * @param label the label of this container
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 * @param yTop the vertical start of the screen - used for scrolling. -1 when not set.
	 * @param yBottom the vertical end of the scren - used for scrolling -1 when not set.
	 * @see #setVerticalDimensions(int, int ) 
	 */
	public Container(String label, boolean focusFirstElement, Style style, int yTop, int yBottom ) {
		super( label, LAYOUT_DEFAULT, INTERACTIVE, style );
		this.itemsList = new ArrayList();
		this.autoFocusEnabled = focusFirstElement;
		if (this.focusedStyle == null) {
			Style focStyle = StyleSheet.focusedStyle;
			this.focusedStyle = focStyle;
			this.focusedTopMargin = focStyle.marginTop + focStyle.paddingTop;
			if (focStyle.border != null) {
				this.focusedTopMargin += focStyle.border.borderWidth;
			} else if (focStyle.background != null) {
				this.focusedTopMargin += focStyle.background.borderWidth;
			}
		}
		this.layout |= Item.LAYOUT_NEWLINE_BEFORE;
		setVerticalDimensions(yTop, yBottom);
	}
	
	/**
	 * Sets the scrolling parameter for this container.
	 * 
	 * @param yTop the start of the screen for this container, -1 when scrolling should not be done.
	 * @param yBottom the end of the screen for this container, -1 when scrolling should not be done.
	 */
	public void setVerticalDimensions( int yTop, int yBottom ) {
		this.yTop = yTop + this.marginTop;
		this.yBottom = yBottom - this.marginBottom;
		this.enableScrolling = (yTop != -1);
	}
	
	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item ) {
		item.yTopPos = item.yBottomPos = 0;
		item.internalX = -9999;
		item.parent = this;
		this.itemsList.add( item );
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
	}

	/**
	 * Inserts the given item at the defined position.
	 * Any following elements are shifted one position to the back.
	 * 
	 * @param index the position at which the element should be inserted, 
	 * 					 use 0 when the element should be inserted in the front of this list.
	 * @param item the item which should be inserted
	 * @throws IllegalArgumentException when the given item is null
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public void add( int index, Item item ) {
		item.yTopPos = item.yBottomPos = 0;
		item.internalX = -9999;
		item.parent = this;
		this.itemsList.add( index, item );
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
	}
	
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item ) {
		//#debug
		System.out.println("Container: setting item " + index );
		item.parent = this;
		if (index == this.focusedIndex) {
			this.itemStyle = item.focus( this.focusedStyle );
		}
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
		return (Item) this.itemsList.set( index, item );
	}
	
	/**
	 * Returns the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item get( int index ) {
		return (Item) this.itemsList.get( index );
	}
	
	/**
	 * Removes the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item remove( int index ) {
		//#debug
		System.out.println("Container: removing item " + index );
		Item removedItem = (Item) this.itemsList.remove(index);
		// adjust y-positions of following items:
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.internalX = -9999;
			item.yTopPos = item.yBottomPos = 0;
			/*
			 int removedItemHeight = removedItem.itemHeight;
			if (item.yTopPos != item.yBottomPos) {
				item.yTopPos -= removedItemHeight;
				item.yBottomPos -= removedItemHeight;
			}*/
		}
		// check if the currenlty focused item has been removed:
		if (index == this.focusedIndex) {
			// focus the first possible item:
			boolean focusSet = false;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.appearanceMode != PLAIN) {
					focus( i, item );
					focusSet = true;
					break;
				}
			}
			if (!focusSet) {
				this.autoFocusEnabled = true;
			}
		} else if (index < this.focusedIndex) {
			this.focusedIndex--;
		}
		this.yOffset = 0;
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
		return removedItem;
	}
	
	/**
	 * Removes the given item.
	 * 
	 * @param item the item which should be removed.
	 * @return true when the item was found in this list.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public boolean remove( Item item ) {
		int index = this.itemsList.indexOf(item);
		if (index != -1) {
			remove( index );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all items from this container.
	 */
	public void clear() {
		this.itemsList.clear();
		this.items = new Item[0];
		if (this.focusedIndex != -1) {
			this.autoFocusEnabled = true;
			this.focusedIndex = -1;
		}
		this.yOffset = 0;
		if (this.isInitialised) {
			this.isInitialised = false;
			//this.yBottom = this.yTop = 0;
			repaint();
		}
	}
	
	/**
	 * Retrieves the number of items stored in this container.
	 * 
	 * @return The number of items stored in this container.
	 */
	public int size() {
		return this.itemsList.size();
	}
	
	/**
	 * Retrieves all items which this container holds.
	 * The items might not have been intialised.
	 * 
	 * @return an array of all items.
	 */
	public Item[] getItems() {
		if (!this.isInitialised) {
			return (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		} else {		
			return this.items;
		}
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item. The first item has the index 0. 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focusable.
	 */
	public boolean focus(int index) {
		Item item = (Item) this.itemsList.get(index );
		if (item.appearanceMode != Item.PLAIN) {
			focus( index, item );			
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the focus to the given item.
	 * 
	 * @param index the position
	 * @param item the item which should be focused
	 */
	public void focus( int index, Item item ) {
		//#debug
		System.out.println("Container: Focusing item " + index );
		
		if (this.autoFocusEnabled  && !this.isInitialised) {
			// setting the index for automatically focusing the appropriate item
			// during the initialisation:
			//#debug
			System.out.println("Container: Setting autofocus-index to " + index );
			this.autoFocusIndex = index;
			return;
		}
		
		if (index == this.focusedIndex && item.isFocused) {
			//#debug
			System.out.println("Container: ignoring focusing of item " + index );
			// ignore the focusing of the same element:
			return;
		}
		// first defocus the last focused item:
		if (this.focusedItem != null) {
			if (this.itemStyle != null) {
				this.focusedItem.defocus(this.itemStyle);
			} else {
				//#debug error
				System.out.println("Container: Unable to defocus item - no previous style found.");
				this.focusedItem.defocus( StyleSheet.defaultStyle );
			}
		}
		
		// save style of the to be focused item and focus the item:
		this.itemStyle = item.focus( this.focusedStyle );
		//#ifdef polish.debug.error
			if (this.itemStyle == null) {
				//#debug error 
				System.out.println("Container: Unable to retrieve style of item " + item.getClass().getName() );
			}
		//#endif
		
		this.focusedIndex = index;
		this.focusedItem = item;
		if (this.yTopPos != this.yBottomPos) {
			// this container has been painted already,
			// so the dimensions are known.
			if (item.internalX != -9999) {
				this.internalX =  item.contentX - this.contentX + item.internalX;
				this.internalWidth = item.internalWidth;
				this.internalY = item.contentY - this.contentY + item.internalY;
				this.internalHeight = item.internalHeight;
			} else {
				this.internalX = item.xLeftPos - this.contentX;
				this.internalWidth = item.itemWidth;
				this.internalY = item.yTopPos - this.contentY;
				this.internalHeight = item.itemHeight;
			}
			if (this.enableScrolling) {	
				// Now adjust the scrolling:
				int itemYTop = item.yTopPos;
				int itemYBottom = item.yBottomPos;
				int difference = 0;
				if (itemYTop == itemYBottom) {
					//#debug
					System.out.println("Container: unable to auto-scroll, item.yBottomPos == item.yTopPos");
				} else if (itemYBottom > this.yBottom) {
					// this item is too low:
					difference = this.yBottom - itemYBottom; 
					//System.out.println("item too low: difference: " + difference + "  itemYBottom=" + itemYBottom + "  container.yBottom=" + this.yBottom );
					if ( itemYTop + difference < this.yTop) {
						difference = this.yTop - itemYTop + 10; // additional pixels for adjusting the focused style above:
						//System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  container.yTop=" + this.yTop );
					}
				} else if (itemYTop < this.yTop) {
					// this item is too high:
					//#if tmp.useTable
						if ((index == 0) || (index < this.numberOfColumns) ) {
					//#else
						//# if (index == 0) {
					//#endif
						// scroll to the very top:
						difference = -1 * this.yOffset;
					} else {
						difference = this.yTop - itemYTop + this.focusedTopMargin;
					}
					//System.out.println("item too high: difference: " + difference + "  itemYTop=" + itemYTop + "  container.yTop=" + this.yTop  );
				}
				//#debug
				System.out.println("Container-focus:: difference: " + difference + "  container.yOffset=" + this.yOffset + "  internalY: " + (item.internalY) + " bis " + (item.internalY + item.internalHeight ) + "  contentY:" + this.contentY + "  top:" + this.yTop + " bottom:" + this.yBottom );
				this.yOffset += difference;
			}
		}
		this.isInitialised = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		//#debug
		System.out.println("Container: intialising content for " + getClass().getName() + ": autofocus=" + this.autoFocusEnabled);
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
		if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length) {
			this.autoFocusIndex = 0;
		}
		//#ifdef polish.css.view-type
			if (this.view != null) {
				if (this.autoFocusEnabled) {
					//#debug
					System.out.println("Container/View: autofocusing element " + this.autoFocusIndex);
					this.autoFocusEnabled = false;
					if (this.autoFocusIndex < myItems.length ) {
						Item item = myItems [ this.autoFocusIndex ];
						if (item.appearanceMode != Item.PLAIN) {
							focus( this.autoFocusIndex, item );		
						}
					}
				}
				this.view.initContent(this, firstLineWidth, lineWidth);
				this.contentWidth = this.view.contentWidth;
				this.contentHeight = this.view.contentHeight;
				this.appearanceMode = this.view.appearanceMode;
				return;
			}
		//#endif
			
		//#ifdef tmp.useTable
		if (this.columnsSetting == NO_COLUMNS || myItems.length <= 1) {
		//#endif
			int myContentWidth = 0;
			int myContentHeight = 0;
			boolean hasFocusableItem = false;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				//System.out.println("initalising " + item.getClass().getName() + ":" + i);
				int width = item.getItemWidth( firstLineWidth, lineWidth );
				int height = item.getItemHeight( firstLineWidth, lineWidth );
				// now the item should have a style, so it can be safely focused
				// without loosing the style information:
				if (item.appearanceMode != PLAIN) {
					hasFocusableItem = true;
				}
				if (this.autoFocusEnabled  && (i >= this.autoFocusIndex ) && (item.appearanceMode != Item.PLAIN)) {
					//#debug
					System.out.println("Container: autofocusing element " + i);
					this.autoFocusEnabled = false;
					focus( i, item );
					height = item.getItemHeight( firstLineWidth, lineWidth );
					width = item.getItemWidth( firstLineWidth, lineWidth );
				}
				if (width > myContentWidth) {
					myContentWidth = width; 
				}
				myContentHeight += height + this.paddingVertical;
			}
			if (!hasFocusableItem) {
				this.appearanceMode = PLAIN;
			} else {
				this.appearanceMode = INTERACTIVE;
			}
		
			this.contentHeight = myContentHeight;
			this.contentWidth = myContentWidth;
			return;
		//#ifdef tmp.useTable
		}
		//#endif
		
		//#ifdef tmp.useTable
			// columns are used
			boolean isNormalWidthColumns = (this.columnsSetting == NORMAL_WIDTH_COLUMNS);
			if (this.columnsSetting != STATIC_WIDTH_COLUMNS) {
				int availableColumnWidth;
				if (isNormalWidthColumns) {
					// this.columnsSetting == NORMAL_WIDTH_COLUMNS
					// each column should use as much space as it can use
					// without ousting the other columns
					// (the calculation will be finished below)
					availableColumnWidth = lineWidth - ((this.numberOfColumns -1) * this.paddingHorizontal);
				} else {
					// each column should take an equal share
					availableColumnWidth = 
						(lineWidth - ((this.numberOfColumns -1) * this.paddingHorizontal))
						/ this.numberOfColumns;
				}
				//System.out.println("available column width: " + availableColumnWidth );
				this.columnsWidths = new int[ this.numberOfColumns ];
				for (int i = 0; i < this.numberOfColumns; i++) {
					this.columnsWidths[i] = availableColumnWidth;
				}
			} else {
				int restWidth = 0;
				int dynamicColumnIndex = -1;
				for (int i = 0; i < this.numberOfColumns; i++) {
					int w = this.columnsWidths[i];
					if (w == -1) {
						dynamicColumnIndex = i;
					} else {
						restWidth += w;
					}
				}
				if (dynamicColumnIndex != -1) {
					this.columnsWidths[ dynamicColumnIndex ] = lineWidth - restWidth;
				}
			}
			this.numberOfRows = (myItems.length / this.numberOfColumns) + (myItems.length % 2);
			this.rowsHeights = new int[ this.numberOfRows ];
			int maxRowHeight = 0;
			int columnIndex = 0;
			int rowIndex = 0;
			int[] maxColumnWidths = null;
			if (isNormalWidthColumns) {
				maxColumnWidths = new int[ this.numberOfColumns ];
			}
			int maxWidth = 0; // important for "equal" columns-width
			int myContentHeight = 0;
			//System.out.println("starting init of " + myItems.length + " container items.");
			for (int i=0; i< myItems.length; i++) {
				Item item = myItems[i];
				int availableWidth = this.columnsWidths[columnIndex];
				//System.out.println("available with: " + availableWidth);
				int width = item.getItemWidth( availableWidth, availableWidth );
				//System.out.println("got item width");
				int height = item.getItemHeight( availableWidth, availableWidth );
				
				// now the item should have a style, so it can be safely focused
				// without loosing the style information:
				if (this.autoFocusEnabled  && (i >= this.autoFocusIndex ) && (item.appearanceMode != Item.PLAIN)) {
					//#debug
					System.out.println("Container: Autofocusing item " + i );
					this.autoFocusEnabled = false;
					focus( i, item );
					height = item.getItemHeight( availableWidth, availableWidth );
					width = item.getItemWidth( availableWidth, availableWidth );
				}
				
				if (height > maxRowHeight) {
					maxRowHeight = height;
				}
				if (isNormalWidthColumns && width > maxColumnWidths[columnIndex ]) {
					maxColumnWidths[ columnIndex ] = width;
				}
				if (width > maxWidth ) {
					maxWidth = width;
				}
				columnIndex++;
				if (columnIndex == this.numberOfColumns) {
					//System.out.println("starting new row: rowIndex=" + rowIndex + "  numberOfRows: " + numberOfRows);
					columnIndex = 0;
					this.rowsHeights[rowIndex] = maxRowHeight;
					myContentHeight += maxRowHeight + this.paddingVertical;
					maxRowHeight = 0;
					rowIndex++;
				}
			} // for each item
			// now save the worked out dimensions:
			if (isNormalWidthColumns) {
				// Each column should use up as much space as 
				// needed in the "normal" columns-width mode.
				// Each column which takes less than available 
				// the available-row-width / number-of-columns
				// can keep, but the others might need to be adjusted,
				// in case the complete width of the table is wider
				// than the allowed width.
				
				int availableRowWidth = lineWidth - ((this.numberOfColumns -1) * this.paddingHorizontal);
				int availableColumnWidth = availableRowWidth / this.numberOfColumns;
				int usedUpWidth = 0;
				int leftColumns = this.numberOfColumns;
				int completeWidth = 0;
				for (int i = 0; i < maxColumnWidths.length; i++) {
					int maxColumnWidth = maxColumnWidths[i];
					if (maxColumnWidth <= availableColumnWidth) {
						usedUpWidth += maxColumnWidth;
						leftColumns--;
					}
					completeWidth += maxColumnWidth;
				}
				if (completeWidth <= availableRowWidth) {
					// okay, the table is fine just how it is
					this.columnsWidths = maxColumnWidths;
				} else {
					// okay, some columns need to be adjusted:
					// re-initialise the table:
					int leftAvailableColumnWidth = (availableRowWidth - usedUpWidth) / leftColumns;
					int[] newMaxColumnWidths = new int[ this.numberOfColumns ];
					myContentHeight = 0;
					columnIndex = 0;
					rowIndex = 0;
					maxRowHeight = 0;
					maxWidth = 0;
					//System.out.println("starting init of " + myItems.length + " container items.");
					for (int i = 0; i < myItems.length; i++) {
						Item item = myItems[i];
						int width = item.itemWidth;
						int height = item.itemHeight;
						if (maxColumnWidths[ columnIndex ] <= availableColumnWidth) {
							newMaxColumnWidths[ columnIndex ] = maxColumnWidths[ columnIndex ];
						} else {
							// re-initialise this item,
							// if it is wider than the left-available-column-width
							if ( width > leftAvailableColumnWidth ) {
								item.isInitialised = false;
								width = item.getItemWidth( leftAvailableColumnWidth, leftAvailableColumnWidth );
								height = item.getItemHeight( leftAvailableColumnWidth, leftAvailableColumnWidth );
							}
							if (width > newMaxColumnWidths[ columnIndex ]) {
								newMaxColumnWidths[ columnIndex ] = width;
							}
						}
						if (height > maxRowHeight) {
							maxRowHeight = height;
						}
						columnIndex++;
						if (columnIndex == this.numberOfColumns) {
							//System.out.println("starting new row: rowIndex=" + rowIndex + "  numberOfRows: " + numberOfRows);
							columnIndex = 0;
							this.rowsHeights[rowIndex] = maxRowHeight;
							myContentHeight += maxRowHeight + this.paddingVertical;
							maxRowHeight = 0;
							rowIndex++;
						}
					} // for each item		
					this.columnsWidths = newMaxColumnWidths;
				}
			} else if (this.columnsSetting == EQUAL_WIDTH_COLUMNS) {
				// Use the maximum used column-width for each column,
				// unless this table should be expanded, in which
				// case the above set widths  will be used instead.
				if (!this.isLayoutExpand) {
					for (int i = 0; i < this.columnsWidths.length; i++) {
						this.columnsWidths[i] = maxWidth;
					}
				}
			} // otherwise the column widths are defined statically.
			// set content height & width:
			int myContentWidth = 0;
			for (int i = 0; i < this.columnsWidths.length; i++) {
				myContentWidth += this.columnsWidths[i] + this.paddingHorizontal;
			}
			this.contentWidth = myContentWidth;
			this.contentHeight = myContentHeight;
		//#endif
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintItem(int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		// adjust vertical start for scrolling:
		y += this.yOffset;
		//#ifdef polish.css.view-type
			if (this.view != null) {
				this.view.paintContent(x, y, leftBorder, rightBorder, g);
				return;
			}
		//#endif
		Item[] myItems;
		synchronized (this.itemsList) {
			myItems = this.items;
		}
		//#ifdef tmp.useTable
		if (this.columnsSetting == NO_COLUMNS || myItems.length == 1) {
		//#endif
			if (!(this.isLayoutCenter || this.isLayoutRight)) {
				// adjust the right border:
				rightBorder = leftBorder + this.contentWidth;
			}
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
				// since after every item a line break will be done.
				item.paint(x, y, leftBorder, rightBorder, g);
				y += item.itemHeight + this.paddingVertical;
			}
		//#ifdef tmp.useTable
		} else {
			x = leftBorder;
			int columnIndex = 0;
			int rowIndex = 0;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				int columnWidth = this.columnsWidths[ columnIndex ];
				item.paint(x, y, x, x + columnWidth, g);
				x += columnWidth + this.paddingHorizontal;
				columnIndex++;
				if (columnIndex == this.numberOfColumns) {
					columnIndex = 0;
					y += this.rowsHeights[ rowIndex ] + this.paddingVertical;
					x = leftBorder;
					rowIndex++;
				}
			}
		}
		//#endif
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "container";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		if (this.focusedItem != null) {
			Item item = this.focusedItem;
			if ( item.handleKeyPressed(keyCode, gameAction) ) {
				if (this.enableScrolling && item.internalX != -9999) {
					if ( item.contentY + item.internalY + item.internalHeight > this.yBottom) {
						this.yOffset -= ( item.contentY + item.internalY + item.internalHeight - this.yBottom );
					} else if ( item.contentY + item.internalY < this.yTop ) {
						this.yOffset += ( this.yTop - (item.contentY + item.internalY  )); 
					}
				}
				return true;
			}	
		}
		// now allow a navigation within the container:
		//#ifdef polish.css.view-type
			if (this.view != null) {
				Item next = this.view.getNextItem(keyCode, gameAction);
				if (next != null) {
					focus( this.view.focusedIndex, next );
					return true;
				} else if (this.enableScrolling) {
					if (gameAction == Canvas.UP && this.yOffset < 0 ) {
						this.yOffset += 10;
						if (this.yOffset > 0 ) {
							this.yOffset = 0;
						}
						return true;
					}
					if (gameAction == Canvas.DOWN
							&& (this.itemHeight + this.yOffset > (this.yBottom - this.yTop)) ) 
					{
						this.yOffset -= 10;
						return true;
					}
				}
				return false;
			}
		//#endif
		boolean processed = false;
		if ( (gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) 
				|| (gameAction == Canvas.DOWN  && keyCode != Canvas.KEY_NUM8)) {
			if (gameAction == Canvas.DOWN && this.columnsSetting != NO_COLUMNS) {
				int currentRow = this.focusedIndex / this.numberOfColumns;
				if (currentRow < this.numberOfRows - 1) {
					processed = shiftFocus( true, this.numberOfColumns - 1 );
				}
			}
			if (!processed) {
				processed = shiftFocus( true, 0 );
			}
			if ((!processed) && this.enableScrolling 
					&&  (this.yBottomPos + this.yOffset > this.yBottom)) {
				// scroll downwards:
				this.yOffset -= 10;
				processed = true;
				//System.out.println("yBottomPos: " + this.yBottomPos + "  yBottom: " + this.yBottom );
			}
		} else if ( (gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) 
				|| (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) ) {
			if (gameAction == Canvas.UP && this.columnsSetting != NO_COLUMNS) {
				int currentRow = this.focusedIndex / this.numberOfColumns;
				if (currentRow > 0) {
					processed = shiftFocus( false,  -(this.numberOfColumns -1 ));
				}
			}
			if (!processed) {
				processed = shiftFocus( false, 0 );
			}
			if ((!processed) && this.enableScrolling && (this.yOffset < 0)) {
				// scroll upwards:
				this.yOffset += 10;
				if (this.yOffset > 0) {
					this.yOffset = 0;
				}
				processed = true;
			}
		}
		return processed;
	}

	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started
	 * @return true when the focus could be moved to either the next or the previous item.
	 */
	private boolean shiftFocus(boolean forwardFocus, int steps ) {
		if ( this.items == null ) {
			return false;
		}
		int i = this.focusedIndex + steps;
		if (i > this.items.length) {
			i = this.items.length - 2;
		}
		if (i < 0) {
			i = 1;
		}
		Item item = null;
		boolean allowCycle = this.enableScrolling;
		if (allowCycle) {
			if (forwardFocus) {
				// when you scroll to the bottom and
				// there is still space, do
				// scroll first before cycling to the
				// first item:
				allowCycle = (this.yOffset + this.itemHeight <= this.yBottom);
			} else {
				// when you scroll to the top and
				// there is still space, do
				// scroll first before cycling to the
				// last item:
				allowCycle = (this.yOffset == 0);
			}
		}
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= this.items.length) {
					if (allowCycle) {
						allowCycle = false;
						i = 0;
					} else {
						break;
					}
				}
			} else {
				i--;
				if (i < 0) {
					if (allowCycle) {
						allowCycle = false;
						i = this.items.length - 1;
					} else {
						break;
					}
				}
			}
			item = this.items[i];
			if (item.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (item == null || item.appearanceMode == Item.PLAIN) {
			return false;
		}
		focus(i, item);
		return true;
	}

	/**
	 * Retrieves the index of the item which is currently focused.
	 * 
	 * @return the index of the focused item, -1 when none is focused.
	 */
	public int getFocusedIndex() {
		return this.focusedIndex;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		setStyle(style, false);
	}
	
	/**
	 * Sets the style of this container.
	 * 
	 * @param style the style
	 * @param ignoreBackground when true is given, the background and border-settings
	 * 		  will be ignored.
	 */
	public void setStyle( Style style, boolean ignoreBackground) {
		super.setStyle(style);
		if (ignoreBackground) {
			this.background = null;
			this.border = null;
			this.borderWidth = 0;
		}
		//#ifdef polish.css.focused-style
			Style focused = (Style) style.getObjectProperty("focused-style");
			if (focused != null) {
				this.focusedStyle = focused;
				this.focusedTopMargin = focused.marginTop + focused.paddingTop;
				if (focused.border != null) {
					this.focusedTopMargin += focused.border.borderWidth;
				} else if (focused.background != null) {
					this.focusedTopMargin += focused.background.borderWidth;
				}
			}
		//#endif
		this.columnsSetting = NO_COLUMNS;
		//#ifdef polish.css.columns
			Integer columns = style.getIntProperty("columns");
			if (columns != null) {
				this.numberOfColumns = columns.intValue();
				this.columnsSetting = NORMAL_WIDTH_COLUMNS;
				//#ifdef polish.css.columns-width
				String width = style.getProperty("columns-width");
				if (width != null) {
					if ("equal".equals(width)) {
						this.columnsSetting = EQUAL_WIDTH_COLUMNS;
					} else if ("normal".equals(width)) {
						//this.columnsSetting = NORMAL_WIDTH_COLUMNS;
					} else {
						// these are pixel settings.
						String[] widths = TextUtil.split( width, ',');
						if (widths.length != this.numberOfColumns) {
							// this is an invalid setting!
							this.columnsSetting = NORMAL_WIDTH_COLUMNS;
							//#debug warn
							System.out.println("Container: Invalid [columns-width] setting: [" + width + "], the number of widths needs to be the same as with [columns] specified.");
						} else {
							this.columnsSetting = STATIC_WIDTH_COLUMNS;
							this.columnsWidths = new int[ this.numberOfColumns ];
							//#ifdef polish.css.columns-width.star
								int combinedWidth = 0;
								int starIndex = -1;
							//#endif
							for (int i = 0; i < widths.length; i++) {
								//#ifdef polish.css.columns-width.star
									String widthStr = widths[i];
									if ("*".equals( widthStr )) {
										starIndex = i;
									} else {
										int w = Integer.parseInt( widthStr );
										combinedWidth += w;
										this.columnsWidths[i] = w;
									}
								//#else
									this.columnsWidths[i] = Integer.parseInt( widths[i] );
								//#endif
							}
							//#ifdef polish.css.columns-width.star
								if (starIndex != -1) {
									Screen myScreen = getScreen();
									if (myScreen != null) {
										this.columnsWidths[starIndex] = 
											myScreen.getWidth() - combinedWidth;
									} else {
										//#debug warn
										System.out.println("Container: Unable to process '*'-columns-width");
									}
								}
							//#endif
							this.columnsSetting = STATIC_WIDTH_COLUMNS;
						}					
					}
				}
				//#endif
				//TODO rob allow definition of the "fill-policy"
			}
		//#endif
		//#ifdef polish.css.view-type
			ContainerView viewType = (ContainerView) style.getObjectProperty("view-type");
			if (viewType != null) {
				try {
					if (viewType.parentContainer != null) {
						viewType = (ContainerView) viewType.getClass().newInstance();
					}
					viewType.parentContainer = this;
					viewType.focusFirstElement = this.autoFocusEnabled;
					viewType.setStyle(style);
				} catch (Exception e) {
					//#debug error
					System.out.println("Container: Unable to init view-type " + e );
				}
			}
			this.view = viewType;
		//#endif
	}

	/**
	 * Parses the given URL and includes the index of the item, when there is an "%INDEX%" within the given url.
	 * @param url the resource URL which might include the substring "%INDEX%"
	 * @param item the item to which the URL belongs to. The item must be 
	 * 		  included in this container.
	 * @return the URL in which the %INDEX% is substituted by the index of the
	 * 		   item in this container. The url "icon%INDEX%.png" is resolved
	 * 		   to "icon1.png" when the item is the second item in this container.
	 * @throws NullPointerException when the given url or item is null
	 */
	public String parseIndexUrl(String url, Item item) {
		int pos = url.indexOf("%INDEX%");
		if (pos != -1) {
			int index = this.itemsList.indexOf( item );
			//TODO rob check if valid, when url ends with %INDEX%
			url = url.substring(0, pos) + index + url.substring( pos + 7 );
		}
		return url;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style)
	 */
	protected Style focus(Style focusstyle) {
		if ( this.itemsList.size() == 0) {
			return super.focus( this.focusedStyle );
		} else {
			this.isFocused = true;
			if (this.focusedIndex == -1) {
				// focus the first interactive item...
				Item[] myItems = getItems();
				for (int i = 0; i < myItems.length; i++) {
					Item item = myItems[i];
					if (item.appearanceMode != PLAIN) {
						this.focusedIndex = i;
						break;
					}
				}
				if (this.focusedIndex == -1) {
					// this container has only non-focusable items!
					return super.focus( this.focusedStyle );
				}
			}
			Item item = (Item) this.itemsList.get( this.focusedIndex );
			focus( this.focusedIndex, item );
			this.isFocused = true;
			if (item.commands == null && this.commands != null) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.setItemCommands(this);
				}
			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				if (this.label != null) {
					Style labStyle = (Style) focusstyle.getObjectProperty("label-style");
					if (labStyle != null) {
						this.labelStyle = this.label.style;
						this.label.setStyle( labStyle );
					}
				}
			//#endif
			return item.style;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		if ( this.itemsList.size() == 0 || this.focusedIndex == -1) {
			super.defocus( originalStyle );
		} else {
			this.isFocused = false;
			Item item = (Item) this.itemsList.get( this.focusedIndex );
			item.defocus( this.itemStyle );
			this.isFocused = false;
			// now remove any commands which are associated with this item:
			if (item.commands == null && this.commands != null) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.removeItemCommands(this);
				}
			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				if (this.label != null && this.label.style != this.labelStyle) {
					this.label.setStyle( this.labelStyle );
				}
			//#endif
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		// just animate the currently focused item:
		if (this.focusedItem != null) {
			boolean animated = this.focusedItem.animate();
			/*
			if (this.focusedItem.background != null) {
				animated = animated | this.focusedItem.background.animate();
			}
			*/
			//#ifdef polish.css.view-type
				if ( this.view != null ) {
					animated = animated | this.view.animate();
				}
			//#endif
			return animated;
		} else {
			//#ifdef polish.css.view-type
				if ( this.view != null ) {
					return this.view.animate();
				}
			//#endif
			return false;
		}
	}
	
	/**
	 * Called by the system to notify the item that it is now at least
	 * partially visible, when it previously had been completely invisible.
	 * The item may receive <code>paint()</code> calls after
	 * <code>showNotify()</code> has been called.
	 * 
	 * <p>The container implementation calls showNotify() on the embedded items.</p>
	 */
	protected void showNotify()
	{
		//#ifdef polish.css.view-type
			if (this.view != null) {
				this.view.showNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.showNotify();
		}
	}

	/**
	 * Called by the system to notify the item that it is now completely
	 * invisible, when it previously had been at least partially visible.  No
	 * further <code>paint()</code> calls will be made on this item
	 * until after a <code>showNotify()</code> has been called again.
	 * 
	 * <p>The container implementation calls hideNotify() on the embedded items.</p>
	 */
	protected void hideNotify()
	{
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.hideNotify();
		}
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		if (y < this.yTopPos || y > this.yBottomPos 
			|| x < this.xLeftPos || x > this.xRightPos) {
			return false;
		}
		// an item within this container was selected:
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (y < item.yTopPos  || y > item.yBottomPos || x < item.xLeftPos || x > item.xRightPos) {
				// this item is not in the range:
				continue;
			}
			// the pressed item has been found:
			if ((item.appearanceMode != Item.PLAIN) && (i != this.focusedIndex)) {
				// only focus the item when it has not been focused already:
				focus(i, item);
				// let the item also handle the pointer-pressing event:
				item.handlePointerPressed( x , y );
				/*
				if (!item.handlePointerPressed( x , y )) {
					// simulate a FIRE keypress event:
					//handleKeyPressed( -1, Canvas.FIRE );
				}*/
				return true;			
			} else {
				return item.handlePointerPressed( x , y );
						//|| item.handleKeyPressed( -1, Canvas.FIRE ) );
			}
		}
		return false;
	}
	//#endif

//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
