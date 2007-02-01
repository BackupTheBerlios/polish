//#condition polish.LibraryBuild
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
 * <p>Used for items that want to have the signature of CustomItems but in real extend the de.enough.polish.ui.Container.</p>
 * <p>Usage:
 * <pre>
 * public class MyContainer
 * //#if polish.LibraryBuild
 *               extends FakeContainerCustomItem
 * //#else
 *              //# extends StringItem
 * //#endif      
 * </pre>
 *

 * @see Container
 */
public class FakeContainerCustomItem extends FakeCustomItem {
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
	private boolean isScrollRequired;
	/** The height available for scrolling, ignore when set to -1 */
	protected int availableHeight = -1;	

	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public FakeContainerCustomItem( boolean focusFirstElement ) {
		this( null, focusFirstElement, null, -1  );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public FakeContainerCustomItem(boolean focusFirstElement, Style style) {
		this( null, focusFirstElement, style, -1  );
	}

	/**
	 * Creates a new empty container.
	 * 
	 * @param label the label of this container
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 * @param height the vertical space available for scrolling. -1 when not set.
	 * @see #setHeight( int ) 
	 */
	public FakeContainerCustomItem(String label, boolean focusFirstElement, Style style, int height ) {
		super( label, LAYOUT_DEFAULT, INTERACTIVE, style );
		this.itemsList = new ArrayList();
		this.autoFocusEnabled = focusFirstElement;
		Style focStyle = StyleSheet.focusedStyle;
		//#if polish.LibraryBuild
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
		setHeight( height );
	}
	
	/**
	 * Sets the height available for scrolling of this item.
	 * 
	 * @param height available height for this item including label, padding, margin and border, -1 when scrolling should not be done.
	 */
	public void setHeight( int height ) {
		this.availableHeight = height;
		this.enableScrolling = (height != -1);
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
//		item.parent = this;
		this.itemsList.add( item );
		if (this.isInitialized) {
			this.isInitialized = false;
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
		item.relativeY = 0;
		item.internalX = -9999;
//		item.parent = this;
		this.itemsList.add( index, item );
		if (index <= this.focusedIndex) {
			this.focusedIndex++;
		}
		if (this.isInitialized) {
			this.isInitialized = false;
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
//		item.parent = this;
		Item last = (Item) this.itemsList.set( index, item );
		if (index == this.focusedIndex) {
			last.defocus(this.itemStyle);
			if ( item.appearanceMode != PLAIN ) {
				this.itemStyle = item.focus( this.focusedStyle, 0 );
			}
		}
		if (this.isInitialized) {
			this.isInitialized = false;
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
			item.relativeY = 0;
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
		if (this.isInitialized) {
			this.isInitialized = false;
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
		if (this.internalX != -9999) {
			this.internalX = -9999;
			this.internalY = 0;
			if ( this.isFocused && this.parent instanceof Container ) {
//				((Container) this.parent).scroll( 0, this );
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
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
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
		
		
		if (this.autoFocusEnabled  && !this.isInitialized) {
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
		int previousIndex = this.focusedIndex; // need to determine whether the user has scrolled from the bottom to the top
		this.focusedIndex = index;
		this.focusedItem = item;
		//#if tmp.supportViewType
			if ( this.containerView != null ) {
				this.containerView.focusedIndex = index;
				this.containerView.focusedItem = item;
			}
		//#endif
		if  (this.isInitialized) {
			// this container has been initialised already,
			// so the dimensions are known.
			if (item.internalX != -9999) {
				this.internalX =  item.relativeX + item.contentX + item.internalX;
				this.internalWidth = item.internalWidth;
				this.internalY = item.relativeY + item.contentY + item.internalY;
				this.internalHeight = item.internalHeight;
				
				//#debug
				System.out.println("Container (" + getClass().getName() + "): setting internalY=" + this.internalY + ", item.contentY=" + item.contentY + ", this.contentY=" + this.contentY + ", item.internalY=" + item.internalY+ ", this.yOffset=" + this.yOffset + ", item.internalHeight=" + item.internalHeight);
			} else {
				this.internalX = item.relativeX;
				this.internalWidth = item.itemWidth;
				this.internalY = item.relativeY;
				this.internalHeight = item.itemHeight;
				//#debug
				System.out.println("Container (" + getClass().getName() + "): setting internalY=" + this.internalY + ", item.relativeY=" + item.relativeY + ", this.contentY=" + this.contentY + ", this.yOffset=" + this.yOffset + ", item.itemHeight=" + item.itemHeight);
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

				
				if ( (index == 0) || (isDownwards && index < previousIndex) ) {
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
			System.out.println("focus: postpone scrolling to initContent()");
			this.isScrollRequired = true;
			
		}
		this.isInitialized = false;
	}
	
	/**
	 * Scrolls this container so that the (internal) area of the given item is best seen.
	 * This is used when a GUI even has been consumed by the currently focused item.
	 * The call is fowarded to scroll( isDownwards, x, y, w, h ).
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param item the item for which the scrolling should be adjusted
	 */
	protected void scroll(int direction, Item item) {
		if (item.internalX != -9999) {
			int relativeInternalX = item.relativeX + item.contentX + item.internalX;
			int relativeInternalY = item.relativeY + item.contentY + item.internalY;
			scroll(  direction, relativeInternalX, relativeInternalY, item.internalWidth, item.internalHeight );
		} else {
			scroll(  direction, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight );			
		}
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is ignored when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 */
	protected void scroll( int direction, int x, int y, int width, int height ) {
		if (!this.enableScrolling) {
			return;
		}
		//#debug
		System.out.println("scroll: direction=" + direction + ", y=" + y + ", Container.height=" + this.availableHeight +  ", height=" +  height + ", focusedIndex=" + this.focusedIndex + ", yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset );
		
		// assume scrolling down when the direction is not known:
		boolean isDownwards = (direction == Canvas.DOWN  ||   direction == 0);
		
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
			if (!isDownwards && y + height + currentYOffset > verticalSpace ) {
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
		try {
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		this.items = myItems;
		if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
			this.autoFocusIndex = 0;
		}
		//#if tmp.supportViewType
			if (this.containerView != null) {
				// additional initialization is necessary when a view is used for this container:
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
							this.containerView.focusedIndex = this.autoFocusIndex;
							this.containerView.focusedItem = this.focusedItem;
						}
					}
				}
				
//				this.containerView.initContent( this, firstLineWidth, lineWidth);
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
					scroll( 0, 0, myContentHeight, width, height );
				}
			} else if (i == this.focusedIndex) {
				if (isLayoutShrink) {
					width = 0;
				}
				if (this.isScrollRequired) {
					//#debug
					System.out.println("initContent(): scroll is required.");
					scroll( 0, 0, myContentHeight, width, height );
					this.isScrollRequired = false;
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
		} catch (ArrayIndexOutOfBoundsException e) {
			//#debug error
			System.out.println("Unable to init container " + e );
		}
		this.contentHeight = myContentHeight;
		this.contentWidth = myContentWidth;	
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
				//#debug 
				System.out.println("Container: drawing " + getClass().getName() + " with yOffset=" + this.yOffset );
			}
		//#endif
		boolean setClipping = (this.yOffset != 0 && (this.marginTop != 0 || this.paddingTop != 0) );
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
		x = leftBorder;
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				//#debug
				System.out.println("fowarding paint call to " + this.containerView );
//				this.containerView.paintContent( this, x, y, leftBorder, rightBorder, g);
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
				// inherit the internal area of the focused item:
				this.internalX =  item.contentX + item.internalX;
				this.internalWidth = item.internalWidth;
				this.internalY = item.contentY+ item.internalY;
				this.internalHeight = item.internalHeight;
			} else {
				// use the item as my internal area:
				this.internalX = item.relativeX ;
				this.internalWidth = item.itemWidth;
				this.internalY = item.relativeY; //(item.yTopPos - this.yOffset) - this.contentY;
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
					scroll(gameAction, item);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyPressed consumed by item " + item.getClass().getName() + "/" + item );
				
				return true;
			}
		}
		// now allow a navigation within the container:
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				boolean handled = this.containerView.handleKeyPressed(keyCode, gameAction);
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
		if ( (gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) 
				|| (gameAction == Canvas.DOWN  && keyCode != Canvas.KEY_NUM8)) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& offset + this.focusedItem.relativeY + this.focusedItem.itemHeight > availableHeight) 
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
		} else if ( (gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) 
				|| (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) ) {
			if (this.focusedItem != null 
					&& this.enableScrolling
					&& offset + this.focusedItem.relativeY < 0 ) // this.focusedItem.yTopPos < this.yTop ) 
			{
				if (gameAction == Canvas.LEFT) {
					return false;
				}
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
		return false;
		// note: in previous versions a keyRepeat event was just re-asigned to a keyPressed event. However, this resulted
		// in non-logical behavior when an item wants to ignore keyRepeast events and only press "real" keyPressed events.
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
							allowCycle = (this.targetYOffset + this.itemHeight <= this.availableHeight);
							// #debug
							// System.out.println("allowCycle-calculation ( forward non-smoothScroll): targetYOffset=" + this.targetYOffset + ", contentHeight=" + this.contentHeight + " (together="+ (this.targetYOffset + this.contentHeight) + ", targetYOfset+itemHeight=" + (this.targetYOffset + this.itemHeight) + ", availableHeight=" + this.availableHeight );
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
			if (this.view != null) {
				ContainerView viewType = (ContainerView) this.view; // (ContainerView) style.getObjectProperty("view-type");
				this.containerView = viewType;
				this.view = null; // set to null so that this container can control the view completely. This is necessary for scrolling, for example.
//				viewType.parentContainer = this;
				viewType.focusFirstElement = this.autoFocusEnabled;
				//#if polish.Container.allowCycling != false
					viewType.allowCycling = this.allowCycling;
				//#else
					viewType.allowCycling = false;
				//#endif
			}
		//#endif
		//#ifdef polish.css.columns
			if (this.containerView == null) {
				Integer columns = style.getIntProperty("columns");
				if (columns != null) {
					if (columns.intValue() > 1) {
						//System.out.println("Container: Using default container view for displaying table");
						this.containerView = new ContainerView();  
//						this.containerView.parentContainer = this;
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
				if (this.containerView != null && false) {
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
//					scr.setItemCommands(this);
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
//					scr.removeItemCommands(this);
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
		if  (this.background != null) {
			animated |= this.background.animate();
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
	protected boolean handlePointerPressed(int x, int y) {
		//System.out.println("Container.handlePointerPressed( x=" + x + ", y=" + y + "): adjustedY=" + (y - (this.yOffset  + this.marginTop + this.paddingTop )) );
		// an item within this container was selected:
		y -= this.yOffset;
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			boolean processed = item.handlePointerPressed(x - (this.contentX + item.relativeX), y - ( this.contentY + item.relativeY) );
			if (processed) {
				//#debug
				System.out.println("pointer event at " + x + "," + y + " consumed by focusedItem.");
				return true;
			}
		}
		if (!isInContentArea(x, y + this.yOffset)) {
			//System.out.println("Container.handlePointerPressed(): out of range, xLeft=" + this.xLeftPos + ", xRight="  + this.xRightPos + ", contentHeight=" + this.contentHeight );
			return false;
		}
		x -= this.contentX;
		y -= this.contentY;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerPressed(x,y) ) {
					return true;
				}
			}
		//#endif
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = x - item.relativeX;
			itemRelY = y - item.relativeY;
			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			// #debug
			System.out.println("Container.handlePointerPressed(" + x + "," + y + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
			// only focus the item when it has not been focused already:
			focus(i, item, 0);
			// let the item also handle the pointer-pressing event:
			item.handlePointerPressed( itemRelX , itemRelY );
			return true;			
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
			item.isInitialized = false;
		}
	}

	/**
	 * Retrieves the vertical scrolling offset of this item.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different.
	 */
	public int getScrollYOffset() {
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
		this.targetYOffset = offset;
		this.yOffset = offset;
	}

}
