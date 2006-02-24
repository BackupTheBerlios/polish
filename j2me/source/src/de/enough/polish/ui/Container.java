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

/**
 * <p>Contains a number of items.</p>
 * <p>Main purpose is to manage all items of a Form or similiar canvases.</p>
 * <p>Containers support following CSS attributes:
 * </p>
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
 * 		<li><b>scroll-mode</b>: Either "smooth" (=default) or "normal".</li>
 * 		<li><b>view-type</b>: The view of this container.</li>
 * </ul>
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
	
	public static final int SCROLL_DEFAULT = 0;
	public static final int SCROLL_SMOOTH = 1;
	
	protected ArrayList itemsList;
	protected Item[] items;
	protected boolean autoFocusEnabled;
	protected int autoFocusIndex;
	protected Style itemStyle;
	protected Item focusedItem;
	public int focusedIndex = -1;
	protected boolean enableScrolling;
	//#if polish.Container.allowCycling != false
		public boolean allowCycling = true;
	//#endif
	int yTop;
	int yBottom;
	protected int yOffset;
	private int focusedTopMargin;
	//#if polish.css.view-type || polish.css.columns
		//#define tmp.supportViewType 
		protected ContainerView view;
	//#endif
	//#ifdef polish.css.scroll-mode
		protected boolean scrollSmooth = true;	
	//#endif
	protected int targetYOffset;

	
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
		Style focStyle = StyleSheet.focusedStyle;
		//#if false
			// this code is needed for the JUnit tests only:
			if (focStyle == null) {
				focStyle = new Style( 1, 1, 1, 1,
						0, 0, 0, 0, 0, 0,
						0, 0x0, null, null, null, null, null, null, null, null
				);
			}
		//#endif
		this.focusedStyle = focStyle;
		this.focusedTopMargin = focStyle.marginTop + focStyle.paddingTop;
		if (focStyle.border != null) {
			this.focusedTopMargin += focStyle.border.borderWidth;
		} 
		if (focStyle.background != null) {
			this.focusedTopMargin += focStyle.background.borderWidth;
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
		if (index <= this.focusedIndex) {
			this.focusedIndex++;
		}
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
		System.out.println("Container: setting item " + index + " " + item.toString() );
		item.parent = this;
		Item last = (Item) this.itemsList.set( index, item );
		if (index == this.focusedIndex) {
			last.defocus(this.itemStyle);
			if ( item.appearanceMode != PLAIN ) {
				this.itemStyle = item.focus( this.focusedStyle, 0 );
			}
		}
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
		return last;
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
		//#debug
		System.out.println("Container: removing item " + index + " " + removedItem.toString()  );
		Screen scr = getScreen();
		if (scr != null) {
			scr.removeItemCommands(removedItem);
		}
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
					focus( i, item, Canvas.DOWN );
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
		this.targetYOffset = 0;
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
			this.autoFocusEnabled = this.isFocused;
			//#if polish.Container.clearResetsFocus != false
				this.autoFocusIndex = 0;
			//#else
				this.autoFocusIndex = this.focusedIndex;
			//#endif			
			this.focusedIndex = -1;
			if (this.focusedItem != null && this.focusedItem.commands != null) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.removeItemCommands(this.focusedItem);
				}
			}
		}
		this.yOffset = 0;
		this.targetYOffset = 0;
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
	 * @param index the index of the item. The first item has the index 0, 
	 * 		when -1 is given, the focus will be removed altogether (remember to call defocus( Style ) first in that case). 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focusable.
	 */
	public boolean focus(int index) {
		if (index == -1) {
			this.focusedIndex = -1;
			this.focusedItem = null;
			//#ifdef tmp.supportViewType
				if (this.view != null) {
					this.view.focusedIndex = -1;
					this.view.focusedItem = null;
				}
			//#endif
			return false;
		}
		Item item = (Item) this.itemsList.get(index );
		if (item.appearanceMode != Item.PLAIN) {
			int direction = 0;
			if (this.isFocused) {
				if (this.focusedIndex == -1) {
					// nothing
				} else if (this.focusedIndex < index ) {
					direction = Canvas.DOWN;
				} else if (this.focusedIndex > index) {
					direction = Canvas.UP;
				}
			
			}
			focus( index, item, direction );			
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the focus to the given item.
	 * 
	 * @param index the position
	 * @param item the item which should be focused
	 * @param direction the direction, either Canvas.DOWN, Canvas.RIGHT, Canvas.UP, Canvas.LEFT or 0.
	 */
	public void focus( int index, Item item, int direction ) {
		//#debug
		System.out.println("Container (" + getClass().getName() + "): Focusing item " + index );
		
		//#if polish.blackberry
			//# getScreen().setFocus( item );
		//#endif
		
		
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
		
		this.itemStyle = item.focus( this.focusedStyle, direction );
		//#ifdef polish.debug.error
			if (this.itemStyle == null) {
				//#debug error 
				System.out.println("Container: Unable to retrieve style of item " + item.getClass().getName() );
			}
		//#endif
		boolean isDownwards = index > this.focusedIndex;
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
				//#debug
				System.out.println("Container (" + getClass().getName() + "): setting internalY=" + this.internalY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight);
				this.internalHeight = item.internalHeight;
			} else {
				this.internalX = item.xLeftPos - this.contentX;
				this.internalWidth = item.itemWidth;
				this.internalY = (item.yTopPos - this.yOffset) - this.contentY;
				this.internalHeight = item.itemHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): setting internalY=" + this.internalY + ", item.yTopPos=" + item.yTopPos + ", this.contentY=" + this.contentY + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight);
				
			}
			if (this.enableScrolling) {	
				// Now adjust the scrolling:
				
				Item nextItem;
				if ( isDownwards && index < this.itemsList.size() - 1 ) {
					nextItem = (Item) this.itemsList.get( index + 1 );
					//#debug
					System.err.println("Focusing downwards, nextItem.y = [" + nextItem.yTopPos + "-" + nextItem.yBottomPos + "], focusedItem.y=[" + item.yTopPos + "-" + item.yBottomPos + "], this.yOffset=" + this.yOffset + ", this.targetYOffset=" + this.targetYOffset);
				} else if ( !isDownwards && index > 0 ) {
					nextItem = (Item) this.itemsList.get( index - 1 );
					//#debug
					System.err.println("Focusing upwards, nextItem.yTopPos = " + nextItem.yTopPos + ", focusedItem.yTopPos=" + item.yTopPos );
				} else {
					//#debug
					System.err.println("Focusing last or first item.");
					nextItem = item;
				}

				
				int itemYTop = isDownwards ? item.yTopPos : nextItem.yTopPos;
				int itemYBottom = isDownwards ? nextItem.yBottomPos : item.yBottomPos;
				
//				if (itemYBottom - itemYTop > this.yTop - this.yBottom) {
//					if ( isDownwards ) {
//						itemYBottom = this.internalY + this.internalHeight;
//					} else {
//						itemYTop = this.internalY;
//					}
//				}
//				int difference = 0;
				scroll( isDownwards, this.xLeftPos, itemYTop, item.internalWidth, itemYBottom - itemYTop );
//				if (itemYTop == itemYBottom) {
//					//#debug
//					System.out.println("Container: unable to auto-scroll, item.yBottomPos == item.yTopPos");
//				} else if (itemYBottom > this.yBottom) {
//					// this item is too low:
//					difference = this.yBottom - itemYBottom;
//					// #debug
//					//System.out.println("item too low: difference: " + difference + "  itemYBottom=" + itemYBottom + "  container.yBottom=" + this.yBottom );
//					//if ( itemYTop + difference < this.yTop) {
//					if ( isDownwards && itemYTop + difference < this.yTop) {
//						// #debug
//						//System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  <  this.yTop=" +  this.yTop + "  to new difference=" + (this.yTop - itemYTop + 10) );
//						difference = this.yTop - itemYTop + 10; // additional pixels for adjusting the focused style above:
//					}
//					/*
//					if ( itemYTop + difference < this.internalY) {
//						//#debug
//						System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  <  this.internalY=" +  this.internalY + "  to new difference=" + (this.internalY - itemYTop + 10) );
//						difference = this.internalY - itemYTop + 10; // additional pixels for adjusting the focused style above:
//					}
//					*/
//				} else if (itemYTop < this.yTop) {
//					// this item is too high:
//					//#if tmp.useSupportViewType
//						//TODO when colpan is used, the index might need to be higher than anticipated
//						if ((index == 0) || ( this.view != null && (index < this.view.numberOfColumns) ) ) {
//					//#else
//						//# if (index == 0) {
//					//#endif
//						// scroll to the very top:
//						difference = -1 * this.yOffset;
//					} else {
//						difference = this.yTop - itemYTop + this.focusedTopMargin;
//					}
//					// re-adjust the scrolling in case we scroll up and the previous
//					// item is very large:
//					if ( !isDownwards && itemYBottom + difference > this.yBottom  ) {
//						difference = this.yBottom - itemYBottom;
//					}
//						
//					// #debug
//					//System.out.println("item too high: difference: " + difference + "  itemYTop=" + itemYTop + "  container.yTop=" + this.yTop  );
//				}
//				//#debug
//				System.out.println("Container (" + getClass().getName() + "): difference: " + difference + "  container.yOffset=" + this.yOffset + ", itemY=[" + itemYTop + "-" + itemYBottom + "],  item.internalY: " + (item.internalY) + " bis " + (item.internalY + item.internalHeight ) + "  contentY:" + this.contentY + "  top:" + this.yTop + " bottom:" + this.yBottom  + "   ---- , this.internalY=" +  this.internalY);
//						
//				//#if polish.css.scroll-mode
//					if (!this.scrollSmooth) {
//						this.yOffset += difference;
//					} else {
//				//#endif
//						this.targetYOffset = this.yOffset + difference;
//				//#if polish.css.scroll-mode
//					}
//				//#endif
			}
		}
		this.isInitialised = false;
	}
	
	protected void scroll( boolean isDownwards, int x, int itemYTop, int width, int height ) {
		//#debug
		System.out.println("scroll: isDownwards=" + isDownwards + ", itemYTop=" + itemYTop + ", Container.yTop=" + this.yTop +  ", itemYBottom=" +  (itemYTop + height) + ", Container.yBottom=" + this.yBottom );
		int difference = 0;
		int index = this.focusedIndex;
		int itemYBottom = itemYTop + height;
		if ( height == 0 || !this.enableScrolling) {
			return;
		} else if (itemYBottom > this.yBottom) {
			// this item is too low:
			difference = this.yBottom - itemYBottom;
			//#debug
			System.out.println("item too low: difference: " + difference + "  itemYBottom=" + itemYBottom + "  container.yBottom=" + this.yBottom );
			//if ( itemYTop + difference < this.yTop) {
			if ( isDownwards && itemYTop + difference < this.yTop) {
				//#debug
				System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  <  this.yTop=" +  this.yTop + "  to new difference=" + (this.yTop - itemYTop + 10) );
				difference = this.yTop - itemYTop + 10; // additional pixels for adjusting the focused style above:
			}
			/*
			if ( itemYTop + difference < this.internalY) {
				//#debug
				System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  <  this.internalY=" +  this.internalY + "  to new difference=" + (this.internalY - itemYTop + 10) );
				difference = this.internalY - itemYTop + 10; // additional pixels for adjusting the focused style above:
			}
			*/
		} else if (itemYTop < this.yTop) {
			// this item is too high:
			//#if tmp.useSupportViewType
				//TODO when colpan is used, the index might need to be higher than anticipated
				if ((index == 0) || ( this.view != null && (index < this.view.numberOfColumns) ) ) {
			//#else
				//# if (index == 0) {
			//#endif
				// scroll to the very top:
				difference = -1 * this.yOffset;
			} else {
				difference = this.yTop - itemYTop + this.focusedTopMargin;
			}
			// re-adjust the scrolling in case we scroll up and the previous
			// item is very large:
			if ( !isDownwards && itemYBottom + difference > this.yBottom  ) {
				difference = this.yBottom - itemYBottom;
			}
		}
				
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				this.yOffset += difference;
			} else {
		//#endif
				this.targetYOffset = this.yOffset + difference;
		//#if polish.css.scroll-mode
			}
		//#endif
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		//#debug
		System.out.println("Container: intialising content for " + this + ": autofocus=" + this.autoFocusEnabled);
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
		if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
			this.autoFocusIndex = 0;
		}
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				if (this.autoFocusEnabled) {
					//#debug
					System.out.println("Container/View: autofocusing element " + this.autoFocusIndex);
					this.autoFocusEnabled = false;
					if (this.autoFocusIndex < myItems.length ) {
						Item item = myItems [ this.autoFocusIndex ];
						if (item.appearanceMode != Item.PLAIN) {
							// make sure that the item has applied it's own style first:
							item.getItemHeight( firstLineWidth, lineWidth );
							// now focus the item:
							focus( this.autoFocusIndex, item, 0 );
							this.view.focusedIndex = this.autoFocusIndex;
							this.view.focusedItem = this.focusedItem;
						}
					}
				}
				this.view.initContent(this, firstLineWidth, lineWidth);
				this.contentWidth = this.view.contentWidth;
				this.contentHeight = this.view.contentHeight;
				this.appearanceMode = this.view.appearanceMode;
				//#todo remove workaround for container
				//TODO remove workaround for container
				if (this.focusedIndex == 0 && this.focusedItem != null) {
					this.internalX = 0;
					this.internalY = 0;
					this.internalWidth = this.focusedItem.itemWidth;
					this.internalHeight = this.focusedItem.itemHeight;
				}
				return;
			}
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
				focus( i, item, 0 );
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
		
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintItem(int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		// adjust vertical start for scrolling:
		//#if polish.debug.debug
			if (this.yOffset != 0) {
				//#debug
				System.out.println("Container: drawing " + getClass().getName() + " with yOffset=" + this.yOffset );
			}
		//#endif
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				this.view.paintContent(x, y, leftBorder, rightBorder, g);
				return;
			}
		//#endif
		Item[] myItems;
		//synchronized (this.itemsList) {
			myItems = this.items;
		//}
		int focusedX = x;
		int focusedY = 0;
		int focusedRightBorder = rightBorder;
		if (!(this.isLayoutCenter || this.isLayoutRight)) {
			// adjust the right border:
			rightBorder = leftBorder + this.contentWidth;
		}
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
			// since after every item a line break will be done.
			if (i == this.focusedIndex) {
				focusedY = y;
				item.getItemHeight( rightBorder - x, rightBorder - leftBorder );
			} else {
				// the currently focused item is painted last
				item.paint(x, y, leftBorder, rightBorder, g);
			}
			y += item.itemHeight + this.paddingVertical;
		}

		
		// paint the currently focused item:
		if (this.focusedItem != null) {
			//System.out.println("Painting focusedItem " + this.focusedItem + " with width=" + this.focusedItem.itemWidth + " and with increased colwidth of " + (focusedRightBorder - focusedX)  );
			this.focusedItem.paint(focusedX, focusedY, focusedX, focusedRightBorder, g);
		}
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
						//#if polish.css.scroll-mode
							if (!this.scrollSmooth) {
								this.yOffset -= ( item.contentY + item.internalY + item.internalHeight - this.yBottom );
							} else {
						//#endif
								this.targetYOffset -= ( item.contentY + item.internalY + item.internalHeight - this.yBottom );
						//#if polish.css.scroll-mode
							}
						//#endif
						//#debug
						System.out.println("Container (" + getClass().getName() + "): lowered yOffset to " + this.yOffset + "/" + this.targetYOffset  );
					} else if ( item.contentY + item.internalY < this.yTop ) {
						//#if polish.css.scroll-mode
							if (!this.scrollSmooth) {
								this.yOffset += ( this.yTop - (item.contentY + item.internalY  )); 
							} else {
						//#endif
								this.targetYOffset += ( this.yTop - (item.contentY + item.internalY  )); 
						//#if polish.css.scroll-mode
							}
						//#endif
						//#debug
						System.out.println("Container (" + getClass().getName() + "): increased yOffset to " + this.yOffset + "/" + this.targetYOffset + ", yTop=" + this.yTop + ", item.class=" + item.getClass().getName() + ", item.contentY=" + item.contentY + ", item.internalY=" + item.internalY );
					}
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyPressed consumed by item " + item.getClass().getName() + "/" + item );
				
				return true;
			}	
		}
		// now allow a navigation within the container:
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				boolean handled = this.view.handleKeyPressed(keyCode, gameAction);
				if (handled) {
					return true;
				}
//				Item next = this.view.getNextItem(keyCode, gameAction);
//				if (next != null) {
//					focus( this.view.focusedIndex, next, gameAction );
//					return true;
//				} else 
				if (this.enableScrolling) {
					
					if (gameAction == Canvas.UP && this.targetYOffset < 0 ) {
						//#if polish.Container.ScrollDelta:defined
							//#= this.targetYOffset += ${polish.Container.ScrollDelta};
						//#else
							this.targetYOffset += 30;
						//#endif
						if (this.targetYOffset > 0 ) {
							this.targetYOffset = 0;
						}
						//#if polish.scroll-mode
							if (!this.scrollSmooth) {
								this.yOffset = this.targetYOffset;
							}
						//#endif
						return true;
					}
					if (gameAction == Canvas.DOWN
							&& (this.itemHeight + this.targetYOffset > (this.yBottom - this.yTop)) ) 
					{
						//#if polish.Container.ScrollDelta:defined
							//#= this.targetYOffset -= ${polish.Container.ScrollDelta};
						//#else
							this.targetYOffset -= 30;
						//#endif
						//#if polish.scroll-mode
							if (!this.scrollSmooth) {
								this.yOffset = this.targetYOffset;
							}
						//#endif
						return true;
					}
				}
				return false;
			}
		//#endif
		boolean processed = false;
		if ( (gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) 
				|| (gameAction == Canvas.DOWN  && keyCode != Canvas.KEY_NUM8)) {
			processed = shiftFocus( true, 0 );
			//#debug
			System.out.println("Container(" + this + "): forward shift by one column succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) && this.enableScrolling 
					&&  (this.yBottomPos + this.targetYOffset > this.yBottom)) {
				// scroll downwards:
				//#if polish.Container.ScrollDelta:defined
					//#= this.targetYOffset -= ${polish.Container.ScrollDelta};
				//#else
					this.targetYOffset -= 30;
				//#endif
				//#debug
				System.out.println("Down/Right: Reducing targetYOffset to " + this.targetYOffset);	
				processed = true;
				//System.out.println("yBottomPos: " + this.yBottomPos + "  yBottom: " + this.yBottom );
				//#if polish.scroll-mode
					if (!this.scrollSmooth) {
						this.yOffset = this.targetYOffset;
					}
				//#endif
			}
		} else if ( (gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) 
				|| (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) ) {

			processed = shiftFocus( false, 0 );
			if ((!processed) && this.enableScrolling && (this.targetYOffset < 0)) {
				// scroll upwards:
				//#if polish.Container.ScrollDelta:defined
					//#= this.targetYOffset += ${polish.Container.ScrollDelta};
				//#else
					this.targetYOffset += 30;
				//#endif
				//#debug
				System.out.println("Up/Left: Increasing targetYOffset to " + this.targetYOffset);	
				if (this.targetYOffset > 0) {
					this.targetYOffset = 0;
				}
				processed = true;
				//#if polish.scroll-mode
					if (!this.scrollSmooth) {
						this.yOffset = this.targetYOffset;
					}
				//#endif
			}
		}
		return processed;
	}

	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @return true when the focus could be moved to either the next or the previous item.
	 */
	private boolean shiftFocus(boolean forwardFocus, int steps ) {
		if ( this.items == null ) {
			return false;
		}
		//#if polish.css.colspan
			int i = this.focusedIndex;
			if (steps != 0) {
				//System.out.println("ShiftFocus: steps=" + steps + ", forward=" + forwardFocus);
				int doneSteps = 0;
				steps = Math.abs( steps ) + 1;
				Item item = this.items[i];
				while( doneSteps <= steps) {
					doneSteps += item.colSpan;
					if (doneSteps >= steps) {
						//System.out.println("bailing out at too many steps: focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
						break;
					}
					if (forwardFocus) {
						i++;
						if (i == this.items.length - 1 ) {
							i = this.items.length - 2;
							break;
						} else if (i == this.items.length) {
							i = this.items.length - 1;
							break;
						}
					} else {
						i--; 
						if (i < 0) {
							i = 1;
							break;
						}
					}
					item = this.items[i];
					//System.out.println("focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
				}
				if (doneSteps >= steps && item.colSpan != 1) {
					if (forwardFocus) {
						i--;
						if (i < 0) {
							i = this.items.length - 1;
						}
						//System.out.println("forward: Adjusting startIndex to " + i );
					} else {
						i = (i + 1) % this.items.length;
						//System.out.println("backward: Adjusting startIndex to " + i );
					}
				}
			}
		//#else			
			//# int i = this.focusedIndex + steps;
			if (i > this.items.length) {
				i = this.items.length - 2;
			}
			if (i < 0) {
				i = 1;
			}
		//#endif
		Item item = null;
		//#if polish.Container.allowCycling != false
			boolean allowCycle = this.enableScrolling && this.allowCycling;
			if (allowCycle) {
				//#if polish.css.scroll-mode
					if (!this.scrollSmooth) {
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
					} else {
				//#endif
					if (forwardFocus) {
						// when you scroll to the bottom and
						// there is still space, do
						// scroll first before cycling to the
						// first item:
						allowCycle = (this.targetYOffset + this.itemHeight <= this.yBottom);
					} else {
						// when you scroll to the top and
						// there is still space, do
						// scroll first before cycling to the
						// last item:
						allowCycle = (this.targetYOffset == 0);
					}
				//#if polish.css.scroll-mode
					}
				//#endif
				//#debug
				System.out.println("shiftFocus: allowCycl=" + allowCycle + ", isFoward=" + forwardFocus + ", targetYOffset=" + this.targetYOffset + ", yOffset=" + this.yOffset );	
			}
		//#endif
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= this.items.length) {
					//#if polish.Container.allowCycling != false
						if (allowCycle) {
							allowCycle = false;
							i = 0;
						} else {
							break;
						}
					//#else
						break;
					//#endif
				}
			} else {
				i--;
				if (i < 0) {
					//#if polish.Container.allowCycling != false
						if (allowCycle) {
							allowCycle = false;
							i = this.items.length - 1;
						} else {
							break;
						}
					//#else
						break;
					//#endif
				}
			}
			item = this.items[i];
			if (item.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (item == null || item.appearanceMode == Item.PLAIN || item == this.focusedItem) {
			return false;
		}
		int direction = Canvas.UP;
		if (forwardFocus) {
			direction = Canvas.DOWN;
		}
		focus(i, item, direction );
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
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when there is no focusable item in this container.
	 */
	public Item getFocusedItem() {
		return this.focusedItem;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.debug.debug
		if (this.parent == null) {
			//#debug
			System.out.println("Container.setStyle without boolean parameter for container " + toString() );
		}
		//#endif
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
		//#if polish.css.focused-style
			this.focusedTopMargin = this.focusedStyle.marginTop + this.focusedStyle.paddingTop;
			if (this.focusedStyle.border != null) {
				this.focusedTopMargin += this.focusedStyle.border.borderWidth;
			}
			if (this.focusedStyle.background != null) {
				this.focusedTopMargin += this.focusedStyle.background.borderWidth;
			}
		//#endif
		
		//#ifdef polish.css.view-type
			ContainerView viewType = (ContainerView) style.getObjectProperty("view-type");
//			if (this instanceof ChoiceGroup) {
//				System.out.println("SET.STYLE / CHOICEGROUP: found view-type (1): " + (viewType != null) + " for " + this);
//			}
			if (viewType != null) {
				if (this.view != null && this.view.getClass() == viewType.getClass()) {
					this.view.focusFirstElement = this.autoFocusEnabled;
					this.view.setStyle(style);
					//System.out.println("SET.STYLE / CHOICEGROUP: found OLD view-type (2): " + viewType + " for " + this);
				} else {
					//System.out.println("SET.STYLE / CHOICEGROUP: found new view-type (2): " + viewType + " for " + this);
					try {
						if (viewType.parentContainer != null) {
							viewType = (ContainerView) viewType.getClass().newInstance();
						}
						viewType.parentContainer = this;
						viewType.focusFirstElement = this.autoFocusEnabled;
						//#if polish.Container.allowCycling != false
							viewType.allowCycling = this.allowCycling;
						//#else
							viewType.allowCycling = false;
						//#endif
						viewType.setStyle(style);
						this.view = viewType;
					} catch (Exception e) {
						//#debug error
						System.out.println("Container: Unable to init view-type " + e );
						viewType = null;
					}
				}
			}
		//#endif
		//#ifdef polish.css.columns
			if (this.view == null) {
				Integer columns = style.getIntProperty("columns");
				if (columns != null) {
					if (columns.intValue() > 1) {
						//System.out.println("Container: Using default container view for displaying table");
						this.view = new ContainerView();  
						this.view.parentContainer = this;
						this.view.focusFirstElement = this.autoFocusEnabled;
						//#if polish.Container.allowCycling != false
							this.view.allowCycling = this.allowCycling;
						//#else
							this.view.allowCycling = false;
						//#endif
						this.view.setStyle(style);
					}
				}
			}
		//#endif

		//#if polish.css.scroll-mode
			Integer scrollModeInt = style.getIntProperty("scroll-mode");
			if ( scrollModeInt != null ) {
				this.scrollSmooth = (scrollModeInt.intValue() == SCROLL_SMOOTH);
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
	/**
	 * Retrieves the position of the specified item.
	 * 
	 * @param item the item
	 * @return the position of the item, or -1 when it is not defined
	 */
	public int getPosition( Item item ) {
		return this.itemsList.indexOf( item );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusstyle, int direction ) {
		if ( this.itemsList.size() == 0) {
			return super.focus( this.focusedStyle, direction );
		} else {
			if (this.focusedStyle != null) {
				focusstyle = this.focusedStyle;
			}
			//#if tmp.supportViewType
				if (this.view != null) {
					this.view.focus(focusstyle, direction);
					//this.isInitialised = false; not required
				}
			//#endif
			this.isFocused = true;
			int newFocusIndex = this.focusedIndex;
			//if (this.focusedIndex == -1) {
			//#if tmp.supportViewType
				if (this.view != null && false) {
			//#endif
				Item[] myItems = getItems();
				// focus the first interactive item...
				if (direction == Canvas.UP || direction == Canvas.LEFT ) {
					//System.out.println("Container: direction UP");
					for (int i = myItems.length; --i > 0; ) {
						Item item = myItems[i];
						if (item.appearanceMode != PLAIN) {
							newFocusIndex = i;
							break;
						}
					}
				} else {
					//System.out.println("Container: direction DOWN");
					for (int i = 0; i < myItems.length; i++) {
						Item item = myItems[i];
						if (item.appearanceMode != PLAIN) {
							newFocusIndex = i;
							break;
						}
					}
				}
				this.focusedIndex = newFocusIndex;
				if (newFocusIndex == -1) {
					//System.out.println("DID NOT FIND SUITEABLE ITEM");
					// this container has only non-focusable items!
					return super.focus( this.focusedStyle, direction );
				}
			//}
			//#if tmp.supportViewType
				} else if (this.focusedIndex == -1) {
					Item[] myItems = getItems();
					//System.out.println("Container: direction DOWN through view type");
					for (int i = 0; i < myItems.length; i++) {
						Item item = myItems[i];
						if (item.appearanceMode != PLAIN) {
							newFocusIndex = i;
							break;
						}
					}
					this.focusedIndex = newFocusIndex;
					if (newFocusIndex == -1) {
						//System.out.println("DID NOT FIND SUITEABLE ITEM");
						// this container has only non-focusable items!
						return super.focus( this.focusedStyle, direction );
					}
				}
			//#endif
			Item item = (Item) this.itemsList.get( this.focusedIndex );
//			Style previousStyle = item.style;
//			if (previousStyle == null) {
//				previousStyle = StyleSheet.defaultStyle;
//			}
			focus( this.focusedIndex, item, direction );
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
			return this.style;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	public void defocus(Style originalStyle) {
		if ( this.itemsList.size() == 0 || this.focusedIndex == -1) {
			super.defocus( originalStyle );
		} else {
			this.isFocused = false;
			Item item = this.focusedItem; //(Item) this.itemsList.get( this.focusedIndex );
			item.defocus( this.itemStyle );
			//#if tmp.supportViewType
				if (this.view != null) {
					this.view.defocus( this.itemStyle );
					this.isInitialised = false;
				}
			//#endif
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
		boolean animated = false;
		//#if polish.css.scroll-mode
			if (this.scrollSmooth && this.targetYOffset != this.yOffset) {
		//#else
			//# if (this.targetYOffset != this.yOffset) {	
		//#endif
			int speed = (this.targetYOffset - this.yOffset) / 3;
			speed += this.targetYOffset > this.yOffset ? 1 : -1;
			this.yOffset += speed;
			if ( speed > 0 && this.yOffset > this.targetYOffset) {
				this.yOffset = this.targetYOffset;
			} else if (speed < 0 && this.yOffset < this.targetYOffset) {
				this.yOffset = this.targetYOffset;
			}
			// # debug
			//System.out.println("animate(): adjusting yOffset to " + this.yOffset );
			animated = true;
		}
		if  (this.background != null) {
			animated |= this.background.animate();
		}
		if (this.focusedItem != null) {
			animated |= this.focusedItem.animate();
		}
		//#ifdef tmp.supportViewType
			if ( this.view != null ) {
				animated |= this.view.animate();
			}
		//#endif
		return animated;
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
		if (this.style != null && !this.isStyleInitialised) {
			setStyle( this.style );
		}
		//#ifdef polish.useDynamicStyles
			else if (this.style == null) {
				initStyle();
			}
		//#else
			else if (this.style == null && !this.isStyleInitialised) {
				//#debug
				System.out.println("Setting default style for container " + this  );
				setStyle( StyleSheet.defaultStyle );
			}
		//#endif
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				this.view.showNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (item.style != null && !item.isStyleInitialised) {
				item.setStyle( item.style );
			}
			//#ifdef polish.useDynamicStyles
				else if (item.style == null) {
					initStyle();
				}
			//#else
				else if (item.style == null && !item.isStyleInitialised) {
					//#debug
					System.out.println("Setting default style for item " + item );
					item.setStyle( StyleSheet.defaultStyle );
				}
			//#endif
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
		// an item within this container was selected:
		Item[] myItems = getItems();
		int lastYPos = this.yBottomPos;
		if ( myItems.length != 0) {
			Item lastItem = myItems[ myItems.length - 1];
			if ( lastItem.backgroundHeight > lastItem.itemHeight ) {
				lastYPos += (lastItem.backgroundHeight - lastItem.itemHeight);
			}
		}
		if (y < this.yTopPos || y > lastYPos 
			|| x < this.xLeftPos || x > this.xRightPos) {
			return false;
		}
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				if ( this.view.handlePointerPressed(x,y) ) {
					return true;
				}
			}
		//#endif
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (y < item.yTopPos  || y > item.yBottomPos || x < item.xLeftPos || x > item.xRightPos) {
				// check for internal positions (e.g. POPUP choice groups can be over this area):
				if ( item.backgroundHeight > item.itemHeight ) {
					if ( y > item.yTopPos + item.backgroundHeight && x > item.xLeftPos + item.backgroundWidth ) {
						continue;
					}
				} else {
					// this item is not in the range:
					continue;					
				}
			}
			// the pressed item has been found:
			if ((item.appearanceMode != Item.PLAIN) && (i != this.focusedIndex)) {
				// only focus the item when it has not been focused already:
				focus(i, item, 0);
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

	/**
	 * Moves the focus away from the specified item.
	 * 
	 * @param item the item that currently has the focus
	 */
	public void requestDefocus( Item item ) {
		if (item == this.focusedItem) {
			boolean success = shiftFocus(true, 1);
			if (!success) {
				defocus(this.itemStyle);
			}
		}
	}

//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
