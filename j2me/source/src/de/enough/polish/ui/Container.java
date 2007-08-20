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
 * <p>Containers support following additional CSS attributes:
 * </p>
 * <ul>
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
 * </ul>
 * <p>Copyright Enough Software 2004 - 2007</p>

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
	
	/** constant for normal scrolling (0) */
	public static final int SCROLL_DEFAULT = 0;
	/** constant for smooth scrolling (1) */
	public static final int SCROLL_SMOOTH = 1;
	
	protected ArrayList itemsList;
	protected Item[] items;
	protected boolean autoFocusEnabled;
	protected int autoFocusIndex;
	protected Style itemStyle;
	protected Item focusedItem;
	/** the index of the currently focused item - please use only for reading, not for setting, unless you know what you are doing */
	public int focusedIndex = -1;
	protected boolean enableScrolling;
	//#if polish.Container.allowCycling != false
		public boolean allowCycling = true;
	//#endif
	protected int yOffset;
	protected int targetYOffset;
	private int focusedTopMargin;
	//#if polish.css.view-type || polish.css.columns
		//#define tmp.supportViewType 
		protected ContainerView containerView;
	//#endif
	//#ifdef polish.css.scroll-mode
		protected boolean scrollSmooth = true;
	//#endif
	//#if polish.css.focus-container
		private boolean isFocusContainer;
		private Style oldContainerStyle;
	//#endif
	private boolean isScrollRequired;
	/** The height available for scrolling, ignore when set to -1 */
	protected int availableHeight = -1;

	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public Container( boolean focusFirstElement ) {
		this( null, focusFirstElement, null, -1 );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public Container(boolean focusFirstElement, Style style) {
		this( null, focusFirstElement, style, -1  );
	}

	/**
	 * Creates a new empty container.
	 * 
	 * @param label the label of this container
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 * @param height the vertical space available for this container, set to -1 when scrolling should not be activated
	 * @see #setScrollHeight( int ) 
	 */
	public Container(String label, boolean focusFirstElement, Style style, int height ) {
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
		setScrollHeight( height );
	}
	
	/**
	 * Sets the height available for scrolling of this item.
	 * 
	 * @param height available height for this item including label, padding, margin and border, -1 when scrolling should not be done.
	 */
	public void setScrollHeight( int height ) {
		this.availableHeight = height;
		this.enableScrolling = (height != -1);
		Item item = this.focusedItem;
		if (this.isScrollRequired && this.enableScrolling && item != null) {
			//#debug
			System.out.println("setScrollHeight(): scroll is required - scrolling to y=" + item.relativeY + ", height=" + height);
			scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight );
			this.isScrollRequired = false;
		}
	}
	
	/**
	 * Returns the available height for scrolling eiter from this container or from it's parent container.
	 * Note that the height available for this container might differ from the returned value.
	 * 
	 * @return the available vertical space or -1 when it is not known.
	 */
	public int getScrollHeight() {
		if (this.availableHeight == -1 && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollHeight();
		} else {
			return this.availableHeight;
		}
	}
	
	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item ) {
		item.relativeY =  0;
		item.internalX = -9999;
		item.parent = this;
		this.itemsList.add( item );
		this.isInitialized = false;
		repaint();
	}
	

	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @param itemStyle the style for the item
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item, Style itemStyle ) {
		add( item );
		if (itemStyle != null) {
			item.setStyle( itemStyle );
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
		item.relativeY = 0;
		item.internalX = -9999;
		item.parent = this;
		this.itemsList.add( index, item );
		if (index <= this.focusedIndex) {
			this.focusedIndex++;
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = this.focusedIndex;
				}
			//#endif
		}
		this.isInitialized = false;
		repaint();
	}
	
	//#if polish.LibraryBuild
	public void add( javax.microedition.lcdui.Item item ) {
		// ignore
	}
	public void add( int index, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif
	
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item ) {
		return set( index, item, null );
	}
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @param itemStyle the new style for the item
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item, Style itemStyle ) {
		//#debug
		System.out.println("Container: setting item " + index + " " + item.toString() );
		if (itemStyle != null) {
			item.setStyle(itemStyle);
		}
		item.parent = this;
		Item last = (Item) this.itemsList.set( index, item );
		if (index == this.focusedIndex) {
			last.defocus(this.itemStyle);
			if ( item.appearanceMode != PLAIN ) {
				//this.itemStyle = item.focus( this.focusedStyle, 0 );
				if (this.isFocused) {
					focus( index, item, 0 );
				} else {
					this.focusedItem = item;
				}
			} else {
				focus( -1 );
			}
		}
		if (this.items != null) {
			this.items[index] = item;
		}
		this.isInitialized = false;
		//#if polish.supportInvisibleItems
			if (!this.isInvisible) {
				repaint();
			}
		//#else
			repaint();
		//#endif
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
		this.items = null;
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		int removedItemHeight = removedItem.itemHeight + this.paddingVertical;
		//#if tmp.supportViewType
			if (this.containerView == null) {
		//#endif
				for (int i = index; i < myItems.length; i++) {
					Item item = myItems[i];
					item.relativeY -= removedItemHeight;
				}
		//#if tmp.supportViewType
			}
		//#endif
		// check if the currenlty focused item has been removed:
		if (index == this.focusedIndex) {
			this.focusedItem = null;
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			// remove any item commands:
			Screen scr = getScreen();
			if (scr != null) {
				scr.removeItemCommands(removedItem);
			}
			// focus the first possible item:
			if (index >= myItems.length) {
				index = myItems.length - 1;
			}
			if (index != -1) { 
				Item item = myItems[ index ];
				if (item.appearanceMode != PLAIN) {
					focus( index, item, Canvas.DOWN );
				} else {
					focusClosestItem(index, myItems);
				}
			}
		} else if (index < this.focusedIndex) {
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex--;
				} else {
			//#endif
					int offset = getScrollYOffset() + removedItemHeight;
					//System.out.println("new container offset: from " + this.yOffset + " to " + (offset > 0 ? 0 : offset));
					setScrollYOffset( offset > 0 ? 0 : offset, false );
			//#if tmp.supportViewType
				}
			//#endif
			this.focusedIndex--;
		}
		this.isInitialized = false;
		repaint();
		return removedItem;
	}
	
	/**
	 * Focuses the next focussable item starting at the specified index + 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItemAbove( int index) {
		Item[] myItems = getItems();
		Item newFocusedItem = null;
		int newFocusedIndex = -1;
		for (int i = index -1; i >= 0; i--) {
			Item item = myItems[i];
			if (item.appearanceMode != PLAIN) {
				newFocusedIndex = i;
				newFocusedItem = item;
				break;
			}
		}
		if (newFocusedItem == null) {
			for (int i = index + 1; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.appearanceMode != PLAIN) {
					newFocusedIndex = i;
					newFocusedItem = item;
					break;
				}
			}			
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (newFocusedIndex < index) {
				direction = Canvas.UP;
			}
			focus( newFocusedIndex, newFocusedItem, direction );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
	}

	/**
	 * Focuses the next focussable item starting at the specified index +/- 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItem( int index) {
		return focusClosestItem( index, getItems() );
	}

	/**
	 * Focuses the next focussable item starting at the specified index +/- 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @param myItems the items that should be used for the search
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	protected boolean focusClosestItem( int index, Item[] myItems ) {
		int i = 1;
		Item newFocusedItem = null;
		Item item;
		boolean continueFocus = true;
		while (continueFocus) {
			continueFocus = false;
			int testIndex = index + i;
			if (testIndex < myItems.length) {
				item = myItems[ testIndex ];
				if (item.appearanceMode != Item.PLAIN) {
					newFocusedItem = item;
					i = testIndex;
					break;
				}
				continueFocus = true;
			}
			testIndex = index - i;
			if (testIndex >= 0) {
				item = myItems[ testIndex ];
				if (item.appearanceMode != Item.PLAIN) {
					i = testIndex;
					newFocusedItem = item;
					break;
				}
				continueFocus = true;
			}
			i++;
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (i < index) {
				direction = Canvas.UP;
			}
			focus( i, newFocusedItem, direction );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
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
		}
		this.yOffset = 0;
		this.targetYOffset = 0;
		if (this.internalX != -9999) {
			this.internalX = -9999;
			this.internalY = 0;
			// adjust scrolling:
			if ( this.isFocused && this.parent instanceof Container ) {
				Container parentContainer = (Container) this.parent;
				int scrollOffset = - parentContainer.getScrollYOffset();
				if (scrollOffset > this.relativeY) {
					int diff = scrollOffset - this.relativeY;
					parentContainer.setScrollYOffset( diff - scrollOffset,  false );
				}
			}
		}
		if (this.isInitialized) {
			this.isInitialized = false;
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
	 * @return an array of all items, can be empty but not null.
	 */
	public Item[] getItems() {
		if (!this.isInitialized || this.items == null) {
			this.items = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		}
		return this.items;
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item. The first item has the index 0, 
	 * 		when -1 is given, the focus will be removed altogether 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focusable.
	 */
	public boolean focus(int index) {
		if (index == -1) {
			this.focusedIndex = -1;
			Item item = this.focusedItem; 
			if (item != null && this.itemStyle != null && item.isFocused) {
				item.defocus( this.itemStyle );
			}
			this.focusedItem = null;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			return true;
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
		System.out.println("Container (" + this + "): Focusing item " + index + " (" + item + ")" );
		
		//#if polish.blackberry
        	//# getScreen().setFocus( item );
		//#endif
		
		
		if (this.autoFocusEnabled  && !this.isInitialized) {
			// setting the index for automatically focusing the appropriate item
			// during the initialisation:
			//#debug
			System.out.println("Container: Setting autofocus-index to " + index );
			this.autoFocusIndex = index;
			//this.isFirstPaint = true;
			return;
		}
		
		if (index == this.focusedIndex && item.isFocused && item == this.focusedItem) {
			//#debug
			System.out.println("Container: ignoring focusing of item " + index );
			// ignore the focusing of the same element:
			return;
		}
		// indicating if either the former focusedItem or the new focusedItem has changed it's size or it's layout by losing/gaining the focus, 
		// of course this can only work if this container is already initialized:
		boolean isReinitializationRequired = false;
		// first defocus the last focused item:
		if (this.focusedItem != null) {
			Item fItem = this.focusedItem;
			int wBefore = fItem.itemWidth;
			int hBefore = fItem.itemHeight;
			int layoutBefore = fItem.layout;
			if (this.itemStyle != null) {
				fItem.defocus(this.itemStyle);
			} else {
				//#debug error
				System.out.println("Container: Unable to defocus item - no previous style found.");
				fItem.defocus( StyleSheet.defaultStyle );
			}
			if (this.isInitialized) {
				int wAfter = fItem.getItemWidth( this.contentWidth, this.contentWidth );
				int hAfter = fItem.itemHeight;
				int layoutAfter = fItem.layout;
				if (wAfter != wBefore || hAfter != hBefore || layoutAfter != layoutBefore ) {
					isReinitializationRequired = true;
					fItem.isInitialized = false; // could be that a container view poses restrictions on the possible size, i.e. within a table
				}
			}
		}
		int wBefore = item.itemWidth;
		int hBefore = item.itemHeight;
		int layoutBefore = item.layout;
		//#if tmp.supportViewType
			if ( this.containerView != null ) {
				this.itemStyle =  this.containerView.focusItem( index, item, direction, this.focusedStyle );
			} else {
		//#endif
				this.itemStyle = item.focus( this.focusedStyle, direction );
		//#if tmp.supportViewType
			} 
		//#endif
		//#ifdef polish.debug.error
			if (this.itemStyle == null) {
				//#debug error 
				System.out.println("Container: Unable to retrieve style of item " + item.getClass().getName() );
			}
		//#endif
		boolean isDownwards = (direction == Canvas.DOWN) || (direction == Canvas.RIGHT) || (direction == 0 &&  index > this.focusedIndex);
		int previousIndex = this.focusedIndex; // need to determine whether the user has scrolled from the bottom to the top
		this.focusedIndex = index;
		this.focusedItem = item;
		if  (this.isInitialized) {
			// this container has been initialised already,
			// so the dimensions are known.
			int wAfter = item.getItemWidth( this.contentWidth, this.contentWidth );
			int hAfter = item.itemHeight;
			int layoutAfter = item.layout;
			if (wAfter != wBefore || hAfter != hBefore || layoutAfter != layoutBefore ) {
				isReinitializationRequired = true;
				item.isInitialized = false; // could be that a container view poses restrictions on the possible size, i.e. within a table
			}

			if (item.internalX != -9999) {
				this.internalX =  item.relativeX + item.contentX + item.internalX;
				this.internalY = item.relativeY + item.contentY + item.internalY;
				this.internalWidth = item.internalWidth;
				this.internalHeight = item.internalHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): internal area found in item " + item + ": setting internalY=" + this.internalY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight + ", item.isInitialized=" + item.isInitialized + ", item.isStyleInitialized=" + item.isStyleInitialised);
			} else {
				this.internalX = item.relativeX;
				this.internalY = item.relativeY;
				this.internalWidth = item.itemWidth;
				this.internalHeight = item.itemHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): NO internal area found in item " + item + ": setting internalY=" + this.internalY + ", internalHeight=" + this.internalHeight + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight + ", this.availableHeight=" + this.availableHeight);
			}
			if (this.enableScrolling) {	
				// Now adjust the scrolling:			
				Item nextItem;
				if ( isDownwards && index < this.itemsList.size() - 1 ) {
					nextItem = (Item) this.itemsList.get( index + 1 );
					//#debug
					System.out.println("Focusing downwards, nextItem.relativY = [" + nextItem.relativeY + "], focusedItem.relativeY=[" + item.relativeY + "], this.yOffset=" + this.yOffset + ", this.targetYOffset=" + this.targetYOffset);
				} else if ( !isDownwards && index > 0 ) {
					nextItem = (Item) this.itemsList.get( index - 1 );
					//#debug
					System.out.println("Focusing upwards, nextItem.yTopPos = " + nextItem.relativeY + ", focusedItem.relativeY=" + item.relativeY );
				} else {
					//#debug
					System.out.println("Focusing last or first item.");
					nextItem = item;
				}

				
				if ( (index == 0) || (isDownwards && (index < previousIndex) || (previousIndex == -1)) ) {
					// either the first item or the first selectable item has been focused, so scroll to the very top:
					//#ifdef polish.css.scroll-mode
						if (!this.scrollSmooth) {
							this.yOffset = 0;
						} else {
					//#endif
							this.targetYOffset = 0;
					//#ifdef polish.css.scroll-mode
						}
					//#endif
				} else {
					int itemYTop = isDownwards ? item.relativeY : nextItem.relativeY;
					int itemYBottom = isDownwards ? nextItem.relativeY + nextItem.itemHeight : item.relativeY + item.itemHeight;
					scroll( direction, this.relativeX, itemYTop, item.internalWidth, itemYBottom - itemYTop );
				}
			}
		} else if (this.enableScrolling) {
			//#debug
			System.out.println("focus: postpone scrolling to initContent() for " + this + ", item " + item);
			this.isScrollRequired = true;
		}
		if (this.isInitialized) {
			this.isInitialized = !isReinitializationRequired;
		}
	}
	
	/**
	 * Scrolls this container so that the (internal) area of the given item is best seen.
	 * This is used when a GUI even has been consumed by the currently focused item.
	 * The call is fowarded to scroll( direction, x, y, w, h ).
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param item the item for which the scrolling should be adjusted
	 */
	public void scroll(int direction, Item item) {
		//#debug
		System.out.println("scroll: scrolling for item " + item  + ", item.internalX=" + item.internalX +", relativeInternalY=" + ( item.relativeY + item.contentY + item.internalY ));
		if (item.internalX != -9999 
				&& ( (item.itemHeight > getScrollHeight()) || ((item.internalY + item.internalHeight) > item.contentHeight ) ) ) 
		{
			// use internal position of item for scrolling:
			int relativeInternalX = item.relativeX + item.contentX + item.internalX;
			int relativeInternalY = item.relativeY + item.contentY + item.internalY;
			scroll(  direction, relativeInternalX, relativeInternalY, item.internalWidth, item.internalHeight );
		} else {
			// use item dimensions for scrolling:
			scroll(  direction, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight );			
		}
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is forwarded to a parent container when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 */
	protected void scroll( int direction, int x, int y, int width, int height ) {
		if (!this.enableScrolling) {
			if (this.parent instanceof Container) {
				x += this.contentX + this.relativeX;
				y += this.contentY + this.relativeY;
				//#debug
				System.out.println("Forwarding scroll request to parent now with y=" + y);
				((Container)this.parent).scroll(direction, x, y, width, height );
			}
			return;
		}
		//#debug
		System.out.println("scroll: direction=" + direction + ", y=" + y + ", availableHeight=" + this.availableHeight +  ", height=" +  height + ", focusedIndex=" + this.focusedIndex + ", yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset );
		// assume scrolling down when the direction is not known:
		boolean isDownwards = (direction == Canvas.DOWN || direction == Canvas.RIGHT ||  direction == 0);
		boolean isUpwards = (direction == Canvas.UP );
		
		int currentYOffset = this.targetYOffset; // yOffset starts at 0 and grows to -contentHeight + lastItem.itemHeight
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				currentYOffset = this.yOffset;
			}
		//#endif

		int verticalSpace = this.availableHeight - (this.contentY + this.marginBottom + this.paddingBottom + this.borderWidth); // the available height for this container
		if ( height == 0 || !this.enableScrolling) {
			return;
		} else if ( y + height + currentYOffset > verticalSpace ) {
			// the area is too low, so scroll down (= increase the negative yOffset):
			currentYOffset += verticalSpace - (y + height + currentYOffset);
			//#debug
			System.out.println("scroll: item too low: verticalSpace=" + verticalSpace + "  y=" + y + ", height=" + height + ", yOffset=" + currentYOffset);
			// check if the top of the area is still visible when scrolling downwards:
			if ( isDownwards && y + currentYOffset < 0 ) {
				currentYOffset -= (y + currentYOffset);
			}
		} else if ( y + currentYOffset < 0 ) {
			// area is too high, so scroll up (= decrease the negative yOffset):
			currentYOffset -=  y + currentYOffset; 
			//#debug
			System.out.println("scroll: item too high: , y=" + y + ", target=" + currentYOffset ); //+ ", focusedTopMargin=" + this.focusedTopMargin );
			// check if the bottom of the area is still visible when scrolling upwards:
			if (isUpwards && y + height + currentYOffset > verticalSpace ) {
				currentYOffset += verticalSpace - (y + height + currentYOffset);
			}

		} else {
			//#debug
			System.out.println("scroll: do nothing");
			return;
		}
				
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				this.yOffset = currentYOffset;
			} else {
		//#endif
				this.targetYOffset = currentYOffset;
				//#debug
				System.out.println("scroll: adjusting targetYOffset to " + this.targetYOffset + ", y=" + y);
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
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
		if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
			this.autoFocusIndex = 0;
		}
		//#if tmp.supportViewType
			if (this.containerView != null) {
				// additional initialization is necessary when a view is used for this container:
				boolean requireScrolling = this.isScrollRequired;
				synchronized (this) {
					if (this.autoFocusEnabled) {
						//#debug
						System.out.println("Container/View: autofocusing element " + this.autoFocusIndex);
						if (this.autoFocusIndex >= 0 ) {
							for (int i = this.autoFocusIndex; i < myItems.length; i++) {
								Item item = myItems[i];
								if (item.appearanceMode != Item.PLAIN) {
									// make sure that the item has applied it's own style first:
									item.getItemHeight( firstLineWidth, lineWidth );
									// now focus the item:
									this.autoFocusEnabled = false;
									requireScrolling = (this.autoFocusIndex != 0);
									focus( i, item, 0 );
									this.isScrollRequired = this.isScrollRequired && requireScrolling; // override setting in focus()
									this.containerView.focusedIndex = i;
									this.containerView.focusedItem = item;
									//System.out.println("autofocus: found item " + i );
									break;
								}							
							}
						} else {
							this.autoFocusEnabled = false;
						}
					}
				}
				
				this.containerView.initContent( this, firstLineWidth, lineWidth);
				this.appearanceMode = this.containerView.appearanceMode;
				this.contentWidth = this.containerView.contentWidth;
				this.contentHeight = this.containerView.contentHeight;
				
				if (requireScrolling && this.enableScrolling && this.focusedItem != null) {
					//#debug
					System.out.println("initContent(): scrolling autofocused or scroll-required item for view");
					Item item = this.focusedItem;
					scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight );
				}
				return;
			}
		//#endif
	
		boolean isLayoutShrink = (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK;
		boolean hasFocusableItem = false;
		int myContentStartX = Integer.MAX_VALUE;
		int myContentEndX = Integer.MIN_VALUE;
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			//System.out.println("initalising " + item.getClass().getName() + ":" + i);
			int width = item.getItemWidth( lineWidth, lineWidth );
			int height = item.itemHeight; // no need to call getItemHeight() since the item is now initialised...
			// now the item should have a style, so it can be safely focused
			// without loosing the style information:
			//String toString = item.toString();
			//System.out.println("init of item " + i + ": height=" + height + " of item " + toString.substring( 19, Math.min(120, toString.length() )  ));
			//if (item.isInvisible && height != 0) {
			//	System.out.println("*** item.height != 0 even though it is INVISIBLE - isInitialized=" + item.isInitialized );
			//}
			if (item.appearanceMode != PLAIN) {
				hasFocusableItem = true;
			}
			if (this.autoFocusEnabled  && (i >= this.autoFocusIndex ) && (item.appearanceMode != Item.PLAIN)) {
				this.autoFocusEnabled = false;
				focus( i, item, 0 );
				this.isScrollRequired = (this.isScrollRequired || hasFocusableItem) && (this.autoFocusIndex != 0); // override setting in focus()
				height = item.getItemHeight(lineWidth, lineWidth);
				if (!isLayoutShrink) {
					width = item.itemWidth;  // no need to call getItemWidth() since the item is now initialised...
				} else {
					width = 0;
				}
				if (this.enableScrolling && this.autoFocusIndex != 0) {
					//#debug
					System.out.println("initContent(): scrolling autofocused item, autofocus-index=" + this.autoFocusIndex + ", i=" + i  );
					scroll( 0, 0, myContentHeight, width, height );
				}
			} else if (i == this.focusedIndex) {
				if (isLayoutShrink) {
					width = 0;
				}
				if (this.isScrollRequired) {
					//#debug
					System.out.println("initContent(): scroll is required - scrolling to y=" + myContentHeight + ", height=" + height);
					scroll( 0, 0, myContentHeight, width, height );
					this.isScrollRequired = false;
//				} else if (item.internalX != -9999 ) {
//					// ensure that lines of textfields etc are within the visible area:
//					scroll(0, item );
				}
			} 
			if (width > myContentWidth) {
				myContentWidth = width; 
			}
			item.relativeY = myContentHeight;
			if  ( (item.layout & LAYOUT_CENTER) == LAYOUT_CENTER) {
				item.relativeX = (lineWidth - width) / 2;
			} else if ( (item.layout & LAYOUT_RIGHT) == LAYOUT_RIGHT) {
				item.relativeX = (lineWidth - width);
			} else {
				item.relativeX = 0;
			}
			if (item.relativeX < myContentStartX ) {
				myContentStartX = item.relativeX;
			}
			if (item.relativeX + width > myContentEndX ) {
				myContentEndX = item.relativeX + width;
			}
			myContentHeight += height != 0 ? height + this.paddingVertical : 0;
			//System.out.println("item.yTopPos=" + item.yTopPos);
		} // cycling through all items
		
		if (myContentEndX - myContentStartX > myContentWidth) {
			// this can happen when there are different layouts like left and right within the same container:
			myContentWidth = myContentEndX - myContentStartX;
		}
		if (!hasFocusableItem) {
			this.appearanceMode = PLAIN;
		} else {
			this.appearanceMode = INTERACTIVE;
			if (this.focusedItem != null) {
				Item item = this.focusedItem;
				if (item.internalX != -9999) {
					this.internalX =  item.relativeX + item.contentX + item.internalX;
					this.internalY = item.relativeY + item.contentY + item.internalY;
					this.internalWidth = item.internalWidth;
					this.internalHeight = item.internalHeight;
					//#debug
					System.out.println("Container (" + getClass().getName() + "): internal area found in item " + item + ": setting internalY=" + this.internalY + ", item.relativeY=" + item.relativeY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight + ", item.isInitialized=" + item.isInitialized);
				} else {
					this.internalX = item.relativeX;
					this.internalY = item.relativeY;
					this.internalWidth = item.itemWidth;
					this.internalHeight = item.itemHeight;
					//#debug
					System.out.println("Container (" + getClass().getName() + "): NO internal area found in item " + item + ": setting internalY=" + this.internalY + ", internalHeight=" + this.internalHeight + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight + ", this.availableHeight=" + this.availableHeight);
				}
				if (isLayoutShrink) {
					//System.out.println("container has shrinking layout and contains focuse item " + item);
					item.isInitialized = false;
					boolean doExpand = item.isLayoutExpand;
					int width;
					if (doExpand) {
						item.isLayoutExpand = false;
						width = item.getItemWidth( lineWidth, lineWidth );
						item.isInitialized = false;
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

		}
		this.contentHeight = myContentHeight;
		this.contentWidth = myContentWidth;	
	}
	
	int getContentScrollHeight() {
		return getScrollHeight() - (this.contentX + this.borderWidth + this.paddingBottom + this.marginBottom ); 
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		// adjust vertical start for scrolling:
		//#if polish.debug.debug
			if (this.enableScrolling) {
//				g.setColor( 0xFFFF00 );
//				g.drawLine( leftBorder, y, rightBorder, y + getContentScrollHeight() );
//				g.drawLine( rightBorder, y, leftBorder, y  + + getContentScrollHeight() );
//				g.drawString( "" + this.availableHeight, x, y, Graphics.TOP | Graphics.LEFT );
				//#debug 
				System.out.println("Container: drawing " + getClass().getName() + " with yOffset=" + this.yOffset );
			}
		//#endif
		boolean setClipping = ( this.enableScrolling && this.itemHeight > this.availableHeight) ; //( this.yOffset != 0 && (this.marginTop != 0 || this.paddingTop != 0) );
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (setClipping) {
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			//g.clipRect(clipX, y - this.paddingTop, clipWidth, clipHeight - ((y - this.paddingTop) - clipY) );
			g.clipRect(clipX, y, clipWidth, clipHeight - (y - clipY) );
		}
		//x = leftBorder;
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				//#debug
				System.out.println("forwarding paint call to " + this.containerView );
				this.containerView.paintContent( this, x, y, leftBorder, rightBorder, g);
				if (setClipping) {
					g.setClip(clipX, clipY, clipWidth, clipHeight);
				}
			} else {
		//#endif
			Item[] myItems;
			//synchronized (this.itemsList) {
				myItems = this.items;
			//}
//			if (!(this.isLayoutCenter || this.isLayoutRight)) {
//				// adjust the right border:
//				rightBorder = leftBorder + this.contentWidth;
//			}
			int focusedX = x;
			int focusedY = 0;
			int focusedRightBorder = rightBorder;
			int startY = g.getClipY();
			int endY = startY + g.getClipHeight();
			Item focItem = this.focusedItem;
			int focIndex = this.focusedIndex;
			//int originalY = y;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
				// since after every item a line break will be done. Use view-type: midp2; to place several items into a single row.
				if (i == focIndex) {
					//System.out.println("paint: FOCUSED ITEM=" + item);
					focusedY = y;
					item.getItemHeight( rightBorder - x, rightBorder - leftBorder );
				} else if ( y + item.itemHeight >= startY && y < endY ){
					// the currently focused item is painted last
					item.paint(x, y, leftBorder, rightBorder, g);
//					System.out.println("painting item at " + x + ", " + y + " - x+item.relativeX=" + (x+ item.relativeX) + ", y+item.relativeY=" + (y + item.relativeY) );
//				} else {
//					System.out.println("skipping " + item);
				}
//				if (this.parent == null || ((Container)this.parent).size() == 1) {
//					System.out.println( i + "=" + item.itemHeight);
//				}
				if (item.itemHeight != 0) {
					y += item.itemHeight + this.paddingVertical;
				}
			}
			boolean paintFocusedItemOutside = false;
			if (focItem != null) {
				paintFocusedItemOutside = (focItem.internalX != -9999);
				if (!paintFocusedItemOutside) {
					focItem.paint(focusedX, focusedY, focusedX, focusedRightBorder, g);
				}
			}
	
			if (setClipping) {
				g.setClip(clipX, clipY, clipWidth, clipHeight);
			}
			
			// paint the currently focused item outside of the clipping area when it has an internal area. This is 
			// for example useful for popup items that extend the actual container area.
			if (paintFocusedItemOutside) {
				//System.out.println("Painting focusedItem " + this.focusedItem + " with width=" + this.focusedItem.itemWidth + " and with increased colwidth of " + (focusedRightBorder - focusedX)  );
				focItem.paint(focusedX, focusedY, focusedX, focusedRightBorder, g);
			}
		//#ifdef tmp.supportViewType
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
			if (!item.isInitialized) {
				item.init( this.contentWidth, this.contentWidth );
			}
			if ( item.handleKeyPressed(keyCode, gameAction) ) {
				if (item.internalX != -9999) {
					if (this.enableScrolling) {
						scroll(gameAction, item);
					} else  {
						if (item.itemHeight > getScrollHeight()) {
							// adjust internal settings for root container:
							this.internalX = item.relativeX + item.contentX + item.internalX;
							this.internalY = item.relativeY + item.contentY + item.internalY;
							this.internalWidth = item.internalWidth;
							this.internalHeight = item.internalHeight;
							//#debug
							System.out.println("Adjusted internal area by internal area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
						} else {
							this.internalHeight = item.relativeX;
							this.internalY = item.relativeY;
							this.internalWidth = item.itemWidth;
							this.internalHeight = item.itemHeight;
							//#debug
							System.out.println("Adjusted internal area by full area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
						}
					}
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyPressed consumed by item " + item.getClass().getName() + "/" + item );
				
				return true;
			}
		}
		
		return handleNavigate(keyCode, gameAction);
	}

	/**
	 * Handles a keyPressed or keyRepeated event for navigating in the container.
	 *  
	 * @param keyCode the code of the keypress/keyrepeat event
	 * @param gameAction the associated game action 
	 * @return true when the key was handled
	 */
	protected boolean handleNavigate(int keyCode, int gameAction) {
		// now allow a navigation within the container:
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				boolean handled = this.containerView.handleKeyPressed(keyCode, gameAction);
				if (handled) {
					return true;
				}
				if (this.enableScrolling) {					
					if (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2 && this.targetYOffset < 0 ) {
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
					if (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8
							&& (this.itemHeight + this.targetYOffset > (this.availableHeight)) ) 
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
		if (
			//#if polish.blackberry && !polish.hasTrackballEvents
				(gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) ||
			//#endif
			   (gameAction == Canvas.DOWN   && keyCode != Canvas.KEY_NUM8)) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& ( (offset + this.focusedItem.relativeY + this.focusedItem.itemHeight > this.availableHeight)
					     || ( this.focusedItem.internalX != -9999
						    && offset + this.focusedItem.relativeY + this.focusedItem.contentY + this.focusedItem.internalY + this.focusedItem.internalHeight > this.availableHeight)) 
				       )
			{
//				if (gameAction == Canvas.RIGHT) {
//					return false;
//				}
				// keep the focus do scroll downwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling down: keeping focus, focusedIndex=" + this.focusedIndex );
			} else {
				processed = shiftFocus( true, 0 );
			}
			//#debug
			System.out.println("Container(" + this + "): forward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) && this.enableScrolling 
					&&  ( (this.focusedItem != null && offset + this.focusedItem.relativeY + this.focusedItem.itemHeight > this.availableHeight)
						|| offset + this.itemHeight > this.availableHeight)) {
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
		} else if ( 
				//#if polish.blackberry && !polish.hasTrackballEvents
					(gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) ||
				//#endif
				    (gameAction == Canvas.UP    && keyCode != Canvas.KEY_NUM2) ) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& offset + this.focusedItem.relativeY < 0 ) // this.focusedItem.yTopPos < this.yTop ) 
			{
//				if (gameAction == Canvas.LEFT) {
//					return false;
//				}
				// keep the focus do scroll upwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling up: keeping focus, focusedIndex=" + this.focusedIndex + ", focusedItem.yTopPos=" + this.focusedItem.relativeY + ", this.availableHeight=" + this.availableHeight + ", targetYOffset=" + this.targetYOffset);
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
					scroll(gameAction, item);
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
					scroll(gameAction, item);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyRepeated consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return handleNavigate(keyCode, gameAction);
		// note: in previous versions a keyRepeat event was just re-asigned to a keyPressed event. However, this resulted
		// in non-logical behavior when an item wants to ignore keyRepeat events and only press "real" keyPressed events.
		// So now events are ignored by containers when they are ignored by their currently focused item...
		//return super.handleKeyRepeated(keyCode, gameAction);
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
							allowCycle = (this.yOffset + this.itemHeight <= this.availableHeight);
							// #debug
							// System.out.println("allowCycle-calculation ( forward non-smoothScroll): yOffset=" + this.yOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.yOffset + this.itemHeight) + ", height=" + this.height);
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
							allowCycle = (this.targetYOffset + this.itemHeight <= this.availableHeight + 1);
							// #debug
							//System.out.println("allowCycle-calculation ( forward non-smoothScroll): targetYOffset=" + this.targetYOffset + ", contentHeight=" + this.contentHeight + " (together="+ (this.targetYOffset + this.contentHeight) + ", targetYOfset+itemHeight=" + (this.targetYOffset + this.itemHeight) + ", availableHeight=" + this.availableHeight );
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
				System.out.println("shiftFocus of " + this + ": allowCycle(local)=" + allowCycle + ", allowCycle(global)=" + this.allowCycling + ", isFoward=" + forwardFocus + ", enableScrolling=" + this.enableScrolling + ", targetYOffset=" + this.targetYOffset + ", yOffset=" + this.yOffset + ", focusedIndex=" + this.focusedIndex + ", start=" + i );
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
//			ContainerView viewType =  (ContainerView) style.getObjectProperty("view-type");
//			if (this instanceof ChoiceGroup) {
//				System.out.println("SET.STYLE / CHOICEGROUP: found view-type (1): " + (viewType != null) + " for " + this);
//			}
			if (this.view != null && this.view instanceof ContainerView) {
				ContainerView viewType = (ContainerView) this.view; // (ContainerView) style.getObjectProperty("view-type");
				this.containerView = viewType;
				this.view = null; // set to null so that this container can control the view completely. This is necessary for scrolling, for example.
				viewType.parentContainer = this;
				viewType.focusFirstElement = this.autoFocusEnabled;
				//#if polish.Container.allowCycling != false
					viewType.allowCycling = this.allowCycling;
				//#else
					viewType.allowCycling = false;
				//#endif
			} else if (!this.preserveViewType) {
				this.containerView = null;
			}
		//#endif
		//#ifdef polish.css.columns
			if (this.containerView == null) {
				Integer columns = style.getIntProperty("columns");
				if (columns != null) {
					if (columns.intValue() > 1) {
						//System.out.println("Container: Using default container view for displaying table");
						this.containerView = new ContainerView();  
						this.containerView.parentContainer = this;
						this.containerView.focusFirstElement = this.autoFocusEnabled;
						//#if polish.Container.allowCycling != false
							this.containerView.allowCycling = this.allowCycling;
						//#else
							this.containerView.allowCycling = false;
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
		//#if polish.css.focus-container
			Boolean focusContainerBool = style.getBooleanProperty("focus-container");
			if (focusContainerBool != null) {
				this.isFocusContainer = focusContainerBool.booleanValue();
			}
		//#endif
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.setStyle(style);
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
			//#if polish.css.focus-container
				if (this.isFocusContainer) {
					this.oldContainerStyle = super.focus(focusstyle, direction);
				}
			//#endif
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focus(focusstyle, direction);
					//this.isInitialised = false; not required
				}
			//#endif
			this.isFocused = true;
			int newFocusIndex = this.focusedIndex;
			//if (this.focusedIndex == -1) {
			//#if tmp.supportViewType
				if ( this.containerView == null || this.containerView.allowsAutoTraversal ) {
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
			//#if polish.css.focus-container
				if (this.isFocusContainer && this.oldContainerStyle != null) {
					super.defocus( this.oldContainerStyle );
					this.oldContainerStyle = null;
				}
			//#endif
			this.isFocused = false;
			Item item = this.focusedItem; //(Item) this.itemsList.get( this.focusedIndex );
			item.defocus( this.itemStyle );
			//#ifdef tmp.supportViewType
				if (this.containerView != null && originalStyle != null) {
					this.containerView.defocus( originalStyle );
					this.isInitialized = false;
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
		boolean animated = super.animate();
		// scroll the container:
		int target = this.targetYOffset;
		int current = this.yOffset;
		//#if polish.css.scroll-mode
			if (this.scrollSmooth && target != current ) {
		//#else
			//# if (target != current) {	
		//#endif
			if (this.availableHeight != -1 && Math.abs(target - current) > this.availableHeight) {
				// maximally scroll one page:
				if (current < target) {
					current = target - this.availableHeight;
				} else {
					current = target + this.availableHeight;
				}
			}
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
		if (this.focusedItem != null) {
			animated |= this.focusedItem.animate();
		}
		//#ifdef tmp.supportViewType
			if ( this.containerView != null ) {
				animated |= this.containerView.animate();
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
			if (this.containerView != null) {
				this.containerView.showNotify();
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
			if (this.containerView != null) {
				this.containerView.hideNotify();
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
	protected boolean handlePointerPressed(int relX, int relY) {
		//#debug
		System.out.println("Container.handlePointerPressed(" + relX + ", " + relY + ") for " + this );
		//System.out.println("Container.handlePointerPressed( x=" + x + ", y=" + y + "): adjustedY=" + (y - (this.yOffset  + this.marginTop + this.paddingTop )) );
		// an item within this container was selected:
		relY -= this.yOffset;
		relX -= this.contentX;
		relY -= this.contentY;
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			boolean processed = item.handlePointerPressed(relX - item.relativeX, relY - item.relativeY );
			if (processed) {
				//#debug
				System.out.println("pointer event at " + relX + "," + relY + " consumed by focusedItem.");
				return true;
			}
		}
		if (!isInItemArea(relX, relY)) {
			//System.out.println("Container.handlePointerPressed(): out of range, xLeft=" + this.xLeftPos + ", xRight="  + this.xRightPos + ", contentHeight=" + this.contentHeight );
			return false;
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerPressed(relX,relY) ) {
					return true;
				}
			}
		//#endif
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			//#debug
			System.out.println("Container.handlePointerPressed(" + relX + "," + relY + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
			// only focus the item when it has not been focused already:
			focus(i, item, 0);
			// let the item also handle the pointer-pressing event:
			item.handlePointerPressed( itemRelX , itemRelY );
			return true;			
		}
		return false;
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAt(int, int)
	 */
	public Item getItemAt(int relX, int relY) {
		relY -= this.yOffset;
		relX -= this.contentX;
		relY -= this.contentY;
		Item item = this.focusedItem;
		if (item != null) {
			int itemRelX = relX - item.relativeX;
			int itemRelY = relY - item.relativeY;
//			if (this.label != null) {
//				System.out.println("itemRelY=" + itemRelY + " of item " + item + ", parent=" + this );
//			}
			if (item.isInItemArea(itemRelX, itemRelY)) {
				return item.getItemAt(itemRelX, itemRelY);
			}
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			if ( i == this.focusedIndex || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			return item.getItemAt(itemRelX, itemRelY);			
		}
		relY += this.yOffset;
		relX += this.contentX;
		relY += this.contentY;
		return super.getItemAt(relX, relY);
	}
	
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
			item.isInitialized = false;
		}
	}

	/**
	 * Retrieves the vertical scrolling offset of this item.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different.
	 */
	public int getScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollYOffset();
		}
		int offset = this.targetYOffset;
		//#ifdef polish.css.scroll-mode
			if (!this.scrollSmooth) {
				offset = this.yOffset;
			}
		//#endif
		return offset;
	}
	
	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 */
	public void setScrollYOffset( int offset) {
		setScrollYOffset( offset, false );
	}

	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 * @param smooth scroll to this new offset smooth if allowed
	 */
	public void setScrollYOffset( int offset, boolean smooth) {
		if (!this.enableScrolling && this.parent instanceof Container) {
			((Container)this.parent).setScrollYOffset(offset, smooth);
			return;
		}
		//#ifdef polish.css.scroll-mode
			if (!smooth || !this.scrollSmooth) {
				this.yOffset = offset;			
			}
		//#else
			if (!smooth) {
				this.yOffset = offset;
			}
		//#endif
		this.targetYOffset = offset;
	}

	/**
	 * Retrieves the index of the specified item.
	 * 
	 * @param item the item
	 * @return the index of the item; -1 when the item is not part of this container
	 */
	public int indexOf(Item item) {
		Object[] myItems = this.items != null ? this.items :  this.itemsList.getInternalArray();
		for (int i = 0; i < myItems.length; i++) {
			Object object = myItems[i];
			if (object == null) {
				break;
			}
			if (object == item) {
				return i;
			}
		}
		return -1;
	}
	
	//#if (polish.debug.error || polish.keepToString) && polish.debug.container.includeChildren
	/**
	 * Generates a String representation of this item.
	 * This method is only implemented when the logging framework is active or the preprocessing variable 
	 * "polish.keepToString" is set to true.
	 * @return a String representation of this item.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( super.toString() ).append( ": { ");
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			//#if polish.supportInvisibleItems
				if (item.isInvisible) {
					buffer.append( i ).append(":invis./plain:" + ( item.appearanceMode == PLAIN ) + "=[").append( item.toString() ).append("]");
				} else {
					buffer.append( i ).append("=").append( item.toString() );
				}
			//#else
				buffer.append( i ).append("=").append( item.toString() );
			//#endif
			if (i != myItems.length - 1 ) {
				buffer.append(", ");
			}
		}
		buffer.append( " }");
		return buffer.toString();
	}
	//#endif

	/**
	 * Sets a list of items for this container.
	 * 
	 * @param itemsList the list of items to set
	 */
	protected void setItemsList(ArrayList itemsList) {
		if (this.isFocused) {
			this.autoFocusEnabled = true;
		}
		if (this.enableScrolling) {
			setScrollYOffset(0, false);
		}
		this.itemsList = itemsList;
		this.items = null;
		requestInit();
	}


//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
