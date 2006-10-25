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
	protected int targetYOffset;
	private int focusedTopMargin;
	//#if polish.css.view-type || polish.css.columns
		//#define tmp.supportViewType 
		protected ContainerView view;
	//#endif
	//#ifdef polish.css.scroll-mode
		protected boolean scrollSmooth = true;
	//#endif
	private boolean isScrollRequired;	
	//protected boolean isFirstPaint;

	
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
						0, 0x0, null, null, null, null, null, null
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
		this.enableScrolling = (yTop != -1 || yBottom != -1 );
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
			// remove any items:
			Screen scr = getScreen();
			if (scr != null) {
				scr.removeItemCommands(removedItem);
			}
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
				this.focusedItem = null;
				this.focusedIndex = -1;
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
			if (this.focusedItem != null) {
				if (this.focusedItem.commands != null) {
					Screen scr = getScreen();
					if (scr != null) {
						scr.removeItemCommands(this.focusedItem);
					}
				}
				if (this.itemStyle != null) {
					this.focusedItem.defocus(this.itemStyle);
				}
			}
			this.focusedItem = null;
			this.autoFocusEnabled = true;
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
			if (this.isFocused) {
				//# getScreen().setFocus( item );
			}
		//#endif
		
		
		if (this.autoFocusEnabled  && !this.isInitialised) {
			// setting the index for automatically focusing the appropriate item
			// during the initialisation:
			//#debug
			System.out.println("Container: Setting autofocus-index to " + index );
			this.autoFocusIndex = index;
			//this.isFirstPaint = true;
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
		boolean isDownwards = (direction == Canvas.DOWN) || (direction == Canvas.RIGHT) || (direction == 0 &&  index > this.focusedIndex);
		this.focusedIndex = index;
		this.focusedItem = item;
		//#if tmp.supportViewType
			if ( this.view != null ) {
				this.view.focusedIndex = index;
				this.view.focusedItem = item;
			}
		//#endif
		if  (this.isInitialised) { // (this.yTopPos != this.yBottomPos) {
			// this container has been initialised already,
			// so the dimensions are known.
			if (item.internalX != -9999) {
				this.internalX =  item.contentX - this.contentX + item.internalX;
				this.internalWidth = item.internalWidth;
				this.internalY = item.contentY - this.contentY + item.internalY;
				this.internalHeight = item.internalHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): setting internalY=" + this.internalY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight);
			} else {
				this.internalX = item.xLeftPos - this.contentX;
				this.internalWidth = item.itemWidth;
				this.internalY = item.yTopPos + this.yOffset; // (item.yTopPos - this.yOffset) - this.contentY;
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
					System.out.println("Focusing downwards, nextItem.y = [" + nextItem.yTopPos + "-" + nextItem.yBottomPos + "], focusedItem.y=[" + item.yTopPos + "-" + item.yBottomPos + "], this.yOffset=" + this.yOffset + ", this.targetYOffset=" + this.targetYOffset);
				} else if ( !isDownwards && index > 0 ) {
					nextItem = (Item) this.itemsList.get( index - 1 );
					//#debug
					System.out.println("Focusing upwards, nextItem.yTopPos = " + nextItem.yTopPos + ", focusedItem.yTopPos=" + item.yTopPos );
				} else {
					//#debug
					System.out.println("Focusing last or first item.");
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
//					//#if tmp.supportViewType
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
		} else if (this.enableScrolling) {
			//#debug
			System.out.println("focus: postpone scrolling to initContent()");
			this.isScrollRequired = true;
			
//		} else if (this.enableScrolling) {
//			this.isFirstPaint = true;
		}
		this.isInitialised = false;
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * 
	 * @param isDownwards true when the scrolling is downwards
	 * @param x the relative horizontal position of the area
	 * @param y the relative vertical position of the area
	 * @param width the width of the area
	 * @param height the height of the area
	 */
	protected void scroll( boolean isDownwards, int x, int y, int width, int height ) {
		//#debug
		System.out.println("scroll: isDownwards=" + isDownwards + ", y=" + y + ", Container.yTop=" + this.yTop +  ", height=" +  height + ", Container.yBottom=" + this.yBottom + ", focusedIndex=" + this.focusedIndex + ", yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset );
		y += this.paddingTop; // this.marginTop + this.paddingTop; marginTop is already used in setVerticalDimension!
		int difference = 0;
//		int index = this.focusedIndex;
		int target = this.targetYOffset;
		int current = this.yOffset;
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				target = current;
			}
		//#endif
		// calculate the absolute position from the relative one:
//		int absoluteY = y + current + this.yTop;
//		int absoluteYBottom = absoluteY + height;
//		int yOffsetDiff = 0;
//		//#ifdef polish.css.scroll-mode
//			if (this.scrollSmooth) {
//		//#endif
//				yOffsetDiff = target - current;
//		//#ifdef polish.css.scroll-mode
//			}
//		//#endif
		int verticalSpace = this.yBottom - this.yTop; // the available height for this container
		if ( height == 0 || !this.enableScrolling) {
			return;
		} else if ( y + height + target > verticalSpace ) {
			// the area is too low:
			difference = verticalSpace - (y + height + target);
			//#debug
			System.out.println("scroll: item too low: difference: " + difference + "  verticalSpace=" + verticalSpace + "  y=" + y + ", height=" + height + ", target=" + target);
			// check if the top of the area is still visible when scrolling downwards:
			if ( isDownwards && y + target + difference < 0 ) {
				difference -= y + target + difference;
			}
		} else if ( y + target < 0 ) {
			// area is too high:
			difference = - (y + target); 
			//#debug
			System.out.println("scroll: item too high: setting difference to " + difference + ", y=" + y + ", target=" + target ); //+ ", focusedTopMargin=" + this.focusedTopMargin );
			if (!isDownwards && y + height + target + difference > verticalSpace ) {
				difference += verticalSpace - (y + height + target + difference);
			}
//			
//		} else if (absoluteYBottom + yOffsetDiff > this.yBottom + 1) { //TODO +1 is a quick hack here
//			// this item is too low:
//			difference = this.yBottom - absoluteYBottom;
//			//#debug
//			System.out.println("scroll: item too low: difference: " + difference + "  absoluteYBottom=" + absoluteYBottom + "  container.yBottom=" + this.yBottom + ", yOffsetDiff=" + yOffsetDiff);
//			//if ( itemYTop + difference < this.yTop) {
//			if ( isDownwards && absoluteY + difference < this.yTop) {
//				//#debug
//				System.out.println("scroll: correcting: difference: " + difference + "  absoluteY=" + absoluteY + "  <  this.yTop=" +  this.yTop + "  to new difference=" + (this.yTop - y + 10) );
//				difference = this.yTop - absoluteY + 10; // additional pixels for adjusting the focused style above:
//			}
//			/*
//			if ( itemYTop + difference < this.internalY) {
//				//#debug
//				System.out.println("correcting: difference: " + difference + "  itemYTop=" + itemYTop + "  <  this.internalY=" +  this.internalY + "  to new difference=" + (this.internalY - itemYTop + 10) );
//				difference = this.internalY - itemYTop + 10; // additional pixels for adjusting the focused style above:
//			}
//			*/
//		} else if (y + yOffsetDiff < this.yTop) {
//			// this item is too high:
//			//#if tmp.supportViewType
//				//TODO when colspan is used, the index might be lower than anticipated
//				if ((index == 0) || ( this.view != null && (index < this.view.numberOfColumns) ) ) {
//			//#else
//				//# if (index == 0) {
//			//#endif
//				// scroll to the very top:
//				difference = -current;
//				//#debug
//				System.out.println("scroll: Scrolling to first item: setting difference to " + difference );
//			} else {
//				difference = this.yTop + yOffsetDiff - absoluteY; // + this.focusedTopMargin;
//				//#debug
//				System.out.println("scroll: item too high: setting difference to " + difference + ", absoluteY=" + absoluteY + ", yOffsetDiff=" + yOffsetDiff ); //+ ", focusedTopMargin=" + this.focusedTopMargin );
//			}
//			// re-adjust the scrolling in case we scroll up and the previous
//			// item is very large:
//			if ( !isDownwards && (absoluteYBottom + yOffsetDiff + difference > this.yBottom  )) {
//				difference = this.yBottom - (absoluteYBottom + yOffsetDiff);
//				//#debug
//				System.out.println("scroll: Scrolling upwards: adjusting difference to " + difference );
//			}
		} else {
			//#debug
			System.out.println("scroll: do nothing");
			return;
		}
				
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				this.yOffset = current + difference;
			} else {
		//#endif
				this.targetYOffset = target + difference;
				//#debug
				System.out.println("scroll: adjusting targetYOffset to " + this.targetYOffset + ", y=" + y);
//				if (this.focusedItem != null) {
//					this.focusedItem.backgroundYOffset = difference;
//				}
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
		int myContentWidth = 0;
		int myContentHeight = 0;
		try {
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
		if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
			this.autoFocusIndex = 0;
		}
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				boolean requireScrolling = this.isScrollRequired;
				if (this.autoFocusEnabled) {
					//#debug
					System.out.println("Container/View: autofocusing element " + this.autoFocusIndex);
					this.autoFocusEnabled = false;
					requireScrolling = true;
					if (this.autoFocusIndex < myItems.length  && this.autoFocusIndex >= 0 ) {
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
				if (requireScrolling && this.enableScrolling && this.focusedItem != null) {
					//#debug
					System.out.println("initContent(): scrolling autofocused or scroll-required item for view");
					Item item = this.focusedItem;
					scroll( true, item.xLeftPos, item.yTopPos, item.itemWidth, item.itemHeight );
				}

				return;
			}
		//#endif
			
		boolean isLayoutShrink = (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK;
		boolean hasFocusableItem = false;
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			//System.out.println("initalising " + item.getClass().getName() + ":" + i);
			int width = item.getItemWidth( lineWidth, lineWidth );
			int height = item.itemHeight; // no need to call getItemHeight() since the item is now initialised...
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
				height = item.getItemHeight(lineWidth, lineWidth);
				if (!isLayoutShrink) {
					width = item.itemWidth;  // no need to call getItemWidth() since the item is now initialised...
				} else {
					width = 0;
				}
				if (this.enableScrolling) {
					//#debug
					System.out.println("initContent(): scrolling autofocused item");
					scroll( true, 0, myContentHeight, width, height );
				}
			} else if (i == this.focusedIndex) {
				if (isLayoutShrink) {
					width = 0;
				}
				if (this.isScrollRequired) {
					//#debug
					System.out.println("initContent(): scroll is required.");
					scroll( true, 0, myContentHeight, width, height );
					this.isScrollRequired = false;
				}
			} 
			if (width > myContentWidth) {
				myContentWidth = width; 
			}
			item.yTopPos = myContentHeight;
			item.yBottomPos = myContentHeight + height;
			myContentHeight += height + this.paddingVertical;
			//System.out.println("item.yTopPos=" + item.yTopPos);
		}
		if (!hasFocusableItem) {
			this.appearanceMode = PLAIN;
		} else {
			this.appearanceMode = INTERACTIVE;
			if (isLayoutShrink && this.focusedItem != null) {
				Item item = this.focusedItem;
				//System.out.println("container has shrinking layout and contains focuse item " + item);
				item.isInitialised = false;
				boolean doExpand = item.isLayoutExpand;
				int width;
				if (doExpand) {
					item.isLayoutExpand = false;
					width = item.getItemWidth( lineWidth, lineWidth );
					item.isInitialised = false;
					item.isLayoutExpand = true;
				} else {
					width = item.itemWidth;
				}
				if (width > myContentWidth) {
					myContentWidth = width;
				}
				if ( this.minimumWidth != 0 && myContentWidth < this.minimumWidth ) {
					myContentWidth = this.minimumWidth;
				}
				//myContentHeight += item.getItemHeight( lineWidth, lineWidth );
			}
		}
		} catch (ArrayIndexOutOfBoundsException e) {
			//#debug error
			System.out.println("Unable to init container " + e );
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
			if (this.enableScrolling) {
				//#debug 
				System.out.println("Container: drawing " + getClass().getName() + " with yOffset=" + this.yOffset );
			}
		//#endif
		boolean setClipping = (this.yOffset != 0 && this.marginTop != 0);
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (setClipping) {
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			g.clipRect(clipX, y - this.paddingTop, clipWidth, clipHeight - ((y - this.paddingTop) - clipY) );
		}
		x = leftBorder;
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				this.view.paintContent(x, y, leftBorder, rightBorder, g);
			} else {
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
			Item focItem = this.focusedItem;
			int focIndex = this.focusedIndex;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
				// since after every item a line break will be done.
				if (i == focIndex) {
					focusedY = y;
					item.getItemHeight( rightBorder - x, rightBorder - leftBorder );
				} else {
					// the currently focused item is painted last
					item.paint(x, y, leftBorder, rightBorder, g);
				}
				y += item.itemHeight + this.paddingVertical;
			}
	
			
			// paint the currently focused item:
			if (focItem != null) {
				//System.out.println("Painting focusedItem " + this.focusedItem + " with width=" + this.focusedItem.itemWidth + " and with increased colwidth of " + (focusedRightBorder - focusedX)  );
				focItem.paint(focusedX, focusedY, focusedX, focusedRightBorder, g);
			}
		//#ifdef tmp.supportViewType
			}
		//#endif
		
		Item item = this.focusedItem;
		if (item != null) {
			if (item.internalX != -9999) {
				this.internalX =  item.contentX - this.contentX + item.internalX;
				this.internalWidth = item.internalWidth;
				this.internalY = item.contentY - this.contentY + item.internalY;
				this.internalHeight = item.internalHeight;
			} else {
				this.internalX = item.xLeftPos - this.contentX;
				this.internalWidth = item.itemWidth;
				this.internalY = item.yTopPos + this.yOffset; //(item.yTopPos - this.yOffset) - this.contentY;
				this.internalHeight = item.itemHeight;
			}
			// outcommented by rob - 2006-07-13 - now positions are determined in the initContent() method already
//			if (this.isFirstPaint) {
//				this.isFirstPaint = false;
//				if (this.enableScrolling) {
//					if ( this.contentY + this.internalY + this.internalHeight > this.yBottom ) {
//						//#debug
//						System.out.println("first paint, now scrolling...");
//						scroll( true, this.contentX, this.contentY + this.internalY, this.contentWidth, this.internalHeight + 3 ); // + 3 gives room for a focused border etc
//					}
//				}
//			}
		}

		if (setClipping) {
			g.setClip(clipX, clipY, clipWidth, clipHeight);
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
					adjustScrolling(item);
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
						// scroll the container view upwards without changing the focused item:
						//#if polish.Container.ScrollDelta:defined
							//#= this.targetYOffset += ${polish.Container.ScrollDelta};
						//#else
							this.targetYOffset += 30;
						//#endif
						if (this.targetYOffset > 0 ) {
							this.targetYOffset = 0;
						}
						//#if polish.css.scroll-mode
							if (!this.scrollSmooth) {
								this.yOffset = this.targetYOffset;
							}
						//#endif
						return true;
					}
					if (gameAction == Canvas.DOWN
							&& (this.itemHeight + this.targetYOffset > (this.yBottom - this.yTop)) ) 
					{
						// scroll the container view downwards without changing the focused item:
						//#if polish.Container.ScrollDelta:defined
							//#= this.targetYOffset -= ${polish.Container.ScrollDelta};
						//#else
							this.targetYOffset -= 30;
						//#endif
						//#if polish.css.scroll-mode
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
		int availableHeight = this.yBottom - this.yTop;
		int offset;
		//#if polish.css.scroll-mode
			if (this.scrollSmooth) {
		//#endif
				offset = this.targetYOffset;
		//#if polish.css.scroll-mode
			} else {
				offset = this.yOffset;
			}
		//#endif
		if ( (gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) 
				|| (gameAction == Canvas.DOWN  && keyCode != Canvas.KEY_NUM8)) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& offset + this.focusedItem.yBottomPos > availableHeight) 
			{
				if (gameAction == Canvas.RIGHT) {
					return false;
				}
				// keep the focus do scroll downwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling down: keeping focus, focusedIndex=" + this.focusedIndex );
			} else {
				processed = shiftFocus( true, 0 );
			}
			//#debug
			System.out.println("Container(" + this + "): forward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) && this.enableScrolling 
					&&  ( (this.focusedItem != null && offset + this.focusedItem.yBottomPos > availableHeight)
						|| offset + this.itemHeight > availableHeight)) {
				// scroll downwards:
				//#if polish.Container.ScrollDelta:defined
					//#= offset -= ${polish.Container.ScrollDelta};
				//#else
					offset -= 30;
				//#endif
				//#debug
				System.out.println("Down/Right: Reducing (target)YOffset to " + offset);	
				processed = true;
				//#if polish.css.scroll-mode
					if (this.scrollSmooth) {
				//#endif
						this.targetYOffset = offset;
				//#if polish.css.scroll-mode
					} else {
						this.yOffset = offset;
					}
				//#endif
			}
		} else if ( (gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) 
				|| (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) ) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& offset + this.focusedItem.yTopPos < 0 ) // this.focusedItem.yTopPos < this.yTop ) 
			{
				if (gameAction == Canvas.LEFT) {
					return false;
				}
				// keep the focus do scroll upwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling up: keeping focus, focusedIndex=" + this.focusedIndex + ", focusedItem.yTopPos=" + this.focusedItem.yTopPos + ", this.yTop=" + this.yTop + ", targetYOffset=" + this.targetYOffset);
			} else {
				processed = shiftFocus( false, 0 );
			}
			//#debug
			System.out.println("Container(" + this + "): upward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) && this.enableScrolling && (offset < 0)) {
				// scroll upwards:
				//#if polish.Container.ScrollDelta:defined
					//#= offset += ${polish.Container.ScrollDelta};
				//#else
					offset += 30;
				//#endif
				if (offset > 0) {
					offset = 0;
				}
				//#if polish.css.scroll-mode
					if (this.scrollSmooth) {
				//#endif
						this.targetYOffset = offset;
				//#if polish.css.scroll-mode
					} else {
						this.yOffset = offset;
					}
				//#endif

				//#debug
				System.out.println("Up/Left: Increasing (target)YOffset to " + offset);	
				processed = true;
			}
		}
		return processed;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		if (this.focusedItem != null) {
			Item item = this.focusedItem;
			if ( item.handleKeyReleased( keyCode, gameAction ) ) {
				if (this.enableScrolling && item.internalX != -9999) {
					adjustScrolling(item);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyReleased consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return super.handleKeyReleased(keyCode, gameAction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0) {
			return false;
		}
		if (this.focusedItem != null) {
			Item item = this.focusedItem;
			if ( item.handleKeyRepeated( keyCode, gameAction ) ) {
				if (this.enableScrolling && item.internalX != -9999) {
					adjustScrolling(item);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyRepeated consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return super.handleKeyRepeated(keyCode, gameAction);
	}

	/**
	 * Adjusts the scrolling for the given item.
	 * 
	 * @param item the item for which the scrolling should be adjusted
	 */
	private void adjustScrolling(Item item) {
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
			//#debug
			System.out.println("shiftFocus fails: this.items==null");
			return false;
		}
		//System.out.println("|");
		Item focItem = this.focusedItem;
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
							// #debug
							// System.out.println("allowCycle-calculation ( forward non-smoothScroll): targetYOffset=" + this.targetYOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.targetYOffset + this.itemHeight) + ", yBottom=" + this.yBottom);
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
							// #debug
							// System.out.println("allowCycle-calculation ( forward smoothScroll): targetYOffset=" + this.targetYOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.targetYOffset + this.itemHeight) + ", yBottom=" + this.yBottom);
						} else {
							// when you scroll to the top and
							// there is still space, do
							// scroll first before cycling to the
							// last item:
							allowCycle = (this.targetYOffset == 0) || (this.targetYOffset == 1);
						}
				//#if polish.css.scroll-mode
					}
				//#endif
			}
			//#if polish.Container.allowCycling != false
			//#debug
			System.out.println("shiftFocus of " + this + ": allowCycle(locale)=" + allowCycle + ", allowCycle(global)=" + this.allowCycling + ", isFoward=" + forwardFocus + ", enableScrolling=" + this.enableScrolling + ", targetYOffset=" + this.targetYOffset + ", yOffset=" + this.yOffset + ", focusedIndex=" + this.focusedIndex + ", start=" + i );
			//#endif
		//#endif
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= this.items.length) {
					//#if polish.Container.allowCycling != false
						if (allowCycle) {
							allowCycle = false;
							i = 0;
							//#debug
							System.out.println("allowCycle: Restarting at the beginning");
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
							//#debug
							System.out.println("allowCycle: Restarting at the end");
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
		if (item == null || item.appearanceMode == Item.PLAIN || item == focItem) {
			//#debug
			System.out.println("got original focused item: " + (item == focItem) + ", item==null:" + (item == null) + ", mode==PLAIN:" + (item == null ? false:(item.appearanceMode == PLAIN)) );
			
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
			this.marginTop = 0;
			this.marginBottom = 0;
			this.marginLeft = 0;
			this.marginRight = 0;
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
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				this.view.setStyle(style);
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
		//#if polish.css.include-label
			if ( this.includeLabel || this.itemsList.size() == 0) {
		//#else
			//# if ( this.itemsList.size() == 0) {
		//#endif
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
					//System.out.println("Container: direction UP with " + myItems.length + " items");
					for (int i = myItems.length; --i >= 0; ) {
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
					//System.out.println("Container: direction DOWN through view type " + this.view);
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
		//#if polish.css.include-label
			if ( this.includeLabel || this.itemsList.size() == 0 || this.focusedIndex == -1) {
		//#else
			//# if ( this.itemsList.size() == 0 || this.focusedIndex == -1) {
		//#endif
			super.defocus( originalStyle );
		} else {
			this.isFocused = false;
			Item item = this.focusedItem; //(Item) this.itemsList.get( this.focusedIndex );
			item.defocus( this.itemStyle );
			//#if tmp.supportViewType
//				if (this.view != null) {
//					//this.view.defocus( this.itemStyle );
//					this.isInitialised = false;
//				}
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
				Style tmpLabelStyle = null;
				if ( originalStyle != null) {
					tmpLabelStyle = (Style) originalStyle.getObjectProperty("label-style");
				}
				if (tmpLabelStyle == null) {
					tmpLabelStyle = StyleSheet.labelStyle;
				}
				if (this.label != null && tmpLabelStyle != null && this.label.style != tmpLabelStyle) {
					this.label.setStyle( tmpLabelStyle );
				}
			//#endif
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		boolean animated = false;
		// scroll the container:
		int target = this.targetYOffset;
		int current = this.yOffset;
		//#if polish.css.scroll-mode
			if (this.scrollSmooth && target != current ) {
		//#else
			//# if (target != current) {	
		//#endif
			int speed = (target - current) / 3;
			speed += target > current ? 1 : -1;
			current += speed;
			if ( ( speed > 0 && current > target) || (speed < 0 && current < target ) ) {
				current = target;
			}
			this.yOffset = current;
//			if (this.focusedItem != null && this.focusedItem.backgroundYOffset != 0) {
//				this.focusedItem.backgroundYOffset = (this.targetYOffset - this.yOffset);
//			}
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
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				this.view.hideNotify();
			}
		//#endif
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
		//System.out.println("Container.handlePointerPressed( x=" + x + ", y=" + y + "): adjustedY=" + (y - (this.yOffset  + this.marginTop + this.paddingTop )) );
		// an item within this container was selected:
		int labelHeight = 0;
		if (this.label != null) {
			labelHeight = this.label.itemHeight; // TODO: what if the label is on the same line???
		}
		y -= this.yOffset + this.marginTop + this.paddingTop + labelHeight;
//		int lastYPos = this.yBottomPos;
//		if ( myItems.length != 0) {
//			Item lastItem = myItems[ myItems.length - 1];
//			if ( lastItem.backgroundHeight > lastItem.itemHeight ) {
//				lastYPos += (lastItem.backgroundHeight - lastItem.itemHeight);
//			}
//		}
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			boolean processed = item.handlePointerPressed(x, y - item.yTopPos);
			if (processed) {
				return true;
			}
		}
		if (y < 0 || y > this.contentHeight 
			|| x < this.xLeftPos || x > this.xRightPos) {
			//System.out.println("Container.handlePointerPressed(): out of range, xLeft=" + this.xLeftPos + ", xRight="  + this.xRightPos + ", contentHeight=" + this.contentHeight );
			return false;
		}
		//#ifdef tmp.supportViewType
			if (this.view != null) {
				if ( this.view.handlePointerPressed(x,y) ) {
					return true;
				}
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			if (y < item.yTopPos  || y > item.yBottomPos || x < item.xLeftPos || x > item.xRightPos) {
				// check for internal positions (e.g. POPUP choice groups can be over this area):
				if ( item.backgroundHeight > item.itemHeight ) {
					if ( y > item.yTopPos + item.backgroundHeight && x > item.xLeftPos + item.backgroundWidth ) {
						//System.out.println("itemOutOfRange(" + i + "): yTop=" + item.yTopPos + ", bottom=" + item.yBottomPos + ", left=" + item.xLeftPos + ", right=" + item.xRightPos );
						continue;
					}
				} else {
					// this item is not in the range:
					//System.out.println("itemOutOfRange(" + i + "): yTop=" + item.yTopPos + ", bottom=" + item.yBottomPos + ", left=" + item.xLeftPos + ", right=" + item.xRightPos );
					continue;					
				}
			}
			// the pressed item has been found:
			//#debug
			System.out.println("Container.keyPressed(): found item " + i + "=" + item);
			if ((item.appearanceMode != Item.PLAIN) && (i != this.focusedIndex)) {
				// only focus the item when it has not been focused already:
				focus(i, item, 0);
				// let the item also handle the pointer-pressing event:
				item.handlePointerPressed( x , y  - item.yTopPos );
				/*
				if (!item.handlePointerPressed( x , y )) {
					// simulate a FIRE keypress event:
					//handleKeyPressed( -1, Canvas.FIRE );
				}*/
				return true;			
			// } else {
				// outcommented, because the focused item already has tried to handle the event above...
				//return item.handlePointerPressed( x , y );
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

	/**
	 * Requests the initialization of this container and all of its children items.
	 */
	public void requestFullInit() {
		requestInit();
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item) this.itemsList.get(i);
			item.isInitialised = false;
		}
	}

//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
