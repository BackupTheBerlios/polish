//#condition polish.usePolishGui
/*
 * Created on Oct 27, 2004 at 7:03:40 PM.
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

import javax.microedition.lcdui.Graphics;

/**
 * <p>Is responsible for visual representation and interpretation of user-input.</p>
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        Oct 27, 2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class ContainerView {
	
	protected int contentWidth;
	protected int contentHeight;
	protected int focusedIndex;

	/**
	 * Creates a new view
	 */
	protected ContainerView() {
		super();
	}
	
	/**
	 * Initialises this item. 
	 * The implementation needs to calculate and set the contentWidth and 
	 * contentHeight fields. 
	 * 
	 * @param parent the Container which uses this view 
	 * @param firstLineWidth the maximum width of the first line 
	 * @param lineWidth the maximum width of any following lines
	 * @see #contentWidth
	 * @see #contentHeight
	 */
	protected abstract void initContent( Container parent, int firstLineWidth, int lineWidth );
		
	/**
	 * Paints the content of this container view.
	 * 
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 */
	protected abstract void paintContent( int x, int y, int leftBorder, int rightBorder, Graphics g );

	/**
	 * Interpretes the given user-input and retrieves the nexte item which should be focused.
	 * Please not that the focusIte()-method is not called as well. The
	 * view is responsible for updating its internal configuration here as well.
	 * 
	 * @param keyCode the code of the keyPressed-events
	 * @param gameAction the associated game-action to the given keyCode
	 * @return the next item which will be focused, null when there is
	 * 			no such element.
	 */
	protected abstract Item getNextItem( int keyCode, int gameAction );
	
	/**
	 * Focuses the item with the given index.
	 * The container will then set the style of the 
	 * retrieved item. The default implementation just
	 * sets the internal focusedIndex field. When this
	 * method is overwritten, please do call super.focusItem first
	 * or set the field "focusedIndex" yourself-
	 * 
	 * @param index the index of the item
	 * @param items the items which are included in this view.
	 */
	protected void focusItem( int index, Item[] items  ) {
		this.focusedIndex = index;
	}
	
	/**
	 * Sets the style for this view.
	 * The style can include additional parameters for the view.
	 * 
	 * @param style the style
	 */
	protected abstract void setStyle( Style style );
	
	/**
	 * Retrieves the next focusable item.
	 * This helper method can be called by view-implementations.
	 * The index of the item can be retrieved with the focusedIndex-field.#
	 * 
	 * @param items the available items
	 * @param foward true when a following item should be looked for,
	 *        false if a previous item should be looked for.
	 * @param steps the number of steps which should be used (e.g. 2 in a table with two columns)
	 * @param allowCircle true when either the first focusable or the last focusable element
	 *        should be returned when there is no focusable item in the given direction.
	 * @return either the next focusable item or null when there is no such element
	 */
	protected Item getNextFocusableItem( final Item[] items, final boolean forward, int steps, boolean allowCircle ) {
		int i = this.focusedIndex;
		boolean isInLoop;
		while  ( true ) {
			if (forward) {
				i += steps;
				isInLoop = i < items.length;
				if (!isInLoop) {
					if (steps > 1) {
						i = items.length - 1;
						isInLoop = true;
					} else if (allowCircle) {
						steps = 1;
						allowCircle = false;
						i = 0;
						isInLoop = true;
					}
				}
			} else {
				i -= steps;
				isInLoop = i >= 0;
				if (!isInLoop) {
					if (steps > 1) {
						i = 0;
						isInLoop = true;
					} else if (allowCircle) {
						steps = 1;
						allowCircle = false;
						i = items.length - 1;
						isInLoop = true;
					}
				}
			}
			if (isInLoop) {
				Item item = items[i];
				if (item.appearanceMode != Item.PLAIN) {
					this.focusedIndex = i;
					return item;
				}
			} else {
				break;
			}
		}
		return null;
	}

}
