//#condition polish.usePolishGui
/*
 * Created on 01-Mar-2004 at 09:45:32.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
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
 * <p>copyright Enough Software 2004</p>
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
	protected boolean focusFirstElement;
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
	private int yTop;
	private int yBottom;
	protected int yOffset;
	private int focusedTopMargin;
	
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
		this.focusFirstElement = focusFirstElement;
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
		this.isInitialised = false;
		item.parent = this;
		this.itemsList.add( item );
		if (this.isInitialised) {
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
		this.isInitialised = false;
		item.parent = this;
		this.itemsList.add( index, item );
		if (this.isInitialised) {
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
		this.isInitialised = false;
		item.parent = this;
		if (this.isInitialised) {
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
		Item removedItem = (Item) this.itemsList.remove(index);
		// check if the currenlty focused item has been removed:
		if (index == this.focusedIndex) {
			// focus the first possible item:
			boolean focusSet = false;
			Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.appearanceMode != PLAIN) {
					focus( i, item );
					focusSet = true;
					break;
				}
			}
			if (!focusSet) {
				this.focusFirstElement = true;
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
		this.items = null;
		if (this.focusedIndex != -1) {
			this.focusFirstElement = true;
			this.focusedIndex = -1;
		}
		this.yOffset = 0;
		if (this.isInitialised) {
			this.isInitialised = false;
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
	protected void focus( int index, Item item ) {
		// first defocus the last focused item:
		if (this.focusedItem != null) {
			this.focusedItem.defocus(this.itemStyle);
		}
		// save style of the to be focused item and focus the item:
		this.itemStyle = item.focus( this.focusedStyle );
		
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
				if (itemYBottom > this.yBottom) {
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
				//System.out.println("focus:: difference: " + difference + "  container.yOffset=" + this.yOffset + "  internalY: " + (item.internalY) + " bis " + (item.internalY + item.internalHeight ) + "  contentY:" + this.contentY + "  top:" + this.yTop + " bottom:" + this.yBottom );
				this.yOffset += difference;
			}
		}
		this.isInitialised = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
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
				if (this.focusFirstElement && (item.appearanceMode != Item.PLAIN)) {
					focus( i, item );
					height = item.getItemHeight( firstLineWidth, lineWidth );
					width = item.getItemWidth( firstLineWidth, lineWidth );
					this.focusFirstElement = false;
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
			if (this.columnsSetting != STATIC_WIDTH_COLUMNS) {
				int availableColumnWidth = (lineWidth 
								- ((this.numberOfColumns -1) + this.paddingHorizontal))
								/ this.numberOfColumns;
				//System.out.println("available column width: " + availableColumnWidth );
				this.columnsWidths = new int[ this.numberOfColumns ];
				for (int i = 0; i < this.numberOfColumns; i++) {
					this.columnsWidths[i] = availableColumnWidth;
				}
			}
			this.numberOfRows = (myItems.length / this.numberOfColumns) + (myItems.length % 2);
			this.rowsHeights = new int[ this.numberOfRows ];
			int maxRowHeight = 0;
			int columnIndex = 0;
			int rowIndex = 0;
			int[] maxColumnWidths = new int[ this.numberOfColumns ];
			boolean trackColumnWidths = (this.columnsSetting == NORMAL_WIDTH_COLUMNS);
			int maxWidth = 0;
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
				if (this.focusFirstElement && (item.appearanceMode != Item.PLAIN)) {
					focus( i, item );
					height = item.getItemHeight( availableWidth, availableWidth );
					width = item.getItemWidth( availableWidth, availableWidth );
					this.focusFirstElement = false;
				}
				
				if (height > maxRowHeight) {
					maxRowHeight = height;
				}
				if (trackColumnWidths && width > maxColumnWidths[columnIndex ]) {
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
			if (this.columnsSetting == NORMAL_WIDTH_COLUMNS) {
				this.columnsWidths = maxColumnWidths;
			} else if (this.columnsSetting == EQUAL_WIDTH_COLUMNS) {
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
		Item[] myItems = this.items;
		//#ifdef tmp.useTable
		if (this.columnsSetting == NO_COLUMNS || myItems.length == 1) {
		//#endif
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
		int i = this.focusedIndex + steps;
		if (i > this.items.length) {
			i = this.items.length - 2;
		}
		if (i < 0) {
			i = 1;
		}
		Item item = null;
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= this.items.length) {
					break;
				}
			} else {
				i--;
				if (i < 0) {
					break;
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
			String focused = style.getProperty("focused-style");
			if (focused != null) {
				Style focStyle = StyleSheet.getStyle( focused );
				if (focStyle != null) {
					this.focusedStyle = focStyle;
					this.focusedTopMargin = focStyle.marginTop + focStyle.paddingTop;
					if (focStyle.border != null) {
						this.focusedTopMargin += focStyle.border.borderWidth;
					} else if (focStyle.background != null) {
						this.focusedTopMargin += focStyle.background.borderWidth;
					}
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
							System.out.println("Invalid [columns-width] setting: [" + width + "], the number of widths needs to be the same as with [columns] specified.");
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
										System.out.println("Unable to process '*'-columns-width");
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
	protected Style focus(Style focusStyle) {
		if ( this.itemsList.size() == 0) {
			return super.focus( this.focusedStyle );
		} else {
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
			return item.style;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		if ( this.itemsList.size() == 0) {
			super.defocus( originalStyle );
		} else {
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
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		// just animate the currently focused item:
		if (this.focusedItem != null) {
			boolean animated = this.focusedItem.animate();
			if (this.focusedItem.background != null) {
				animated = animated | this.focusedItem.background.animate();
			}
			return animated;
		} else {
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


}
