//#condition polish.usePolishGui
/*
 * Created on 16-Feb-2005 at 09:45:41.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Provides a list of items that can be used within a Form.</p>
 * <p>The list item behaves like a normal J2ME Polish container, so 
 *    you can specify view-types, columns, colspans, etc.
 * </p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ListItem 
//#ifdef polish.usePolishGui
	//# extends CustomItem
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
 
{
	
	private final Container container;
	//private int availableWidth;

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 */
	public ListItem(String label ) {
		this( label, null );
	}

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 * @param style the style
	 */
	public ListItem(String label, Style style) {
		super(label);
		this.container = new Container( false, style );
		//#if polish.usePolishGui
			//# this.container.parent = this;
		//#endif
		//#if polish.Container.allowCycling != false
			this.container.allowCycling = false;
		//#endif

	}
	
	//#if false
	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void append( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if false
	/**
	 * Inserts the specified item into this list.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 */
	public void insert( int position, javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if false
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
		return false;
	}
	//#endif
	
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param index the index of the item that should be removed
	 * @return the item that has been at the specified index
	 */
	//#if false
	public javax.microedition.lcdui.Item remove( int index ) {
		return null;
	//#else
		//# public Item remove( int index ) {
		//# return this.container.remove(index);
	//#endif
	}


	/**
	 * Appends the specified text and image to this list.
	 * 
	 * @param text the text
	 * @param image the image
	 */
	public void append( String text, Image image ) {
		append( text, image, null );
	}

	/**
	 * Appends the specified text and image to this list and provides it with the given style.
	 * 
	 * @param text the text
	 * @param image the image
	 * @param style the style
	 */
	public void append( String text, Image image, Style style ) {
		IconItem item = new IconItem( text, image, style );
		append( item );
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void append( Item item ) {
		this.container.add(item);
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 * @param style the style
	 */
	public void append( Item item, Style style ) {
		if (style != null) {
			item.setStyle( style );
		}
		this.container.add(item);
	}

	/**
	 * Inserts the specified item into this list.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 */
	public void insert( int position, Item item ) {
		this.container.add( position, item );
	}

	/**
	 * Inserts the specified item into this list and provides it with a style.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 * @param style the style
	 */
	public void insert( int position, Item item, Style style ) {
		if (style != null) {
			item.setStyle( style );
		}
		this.container.add( position, item );
	}

	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( Item item ) {
		return this.container.remove(item);
	}
	
	/**
	 * Clears this list.
	 */
	public void removeAll() {
		this.container.clear();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
	 */
	protected int getMinContentWidth() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
	 */
	protected int getMinContentHeight() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
	 */
	protected int getPrefContentWidth(int maxHeight) {
		// try to use the maximum available width:
		return Integer.MAX_VALUE;
		//return this.prefContentWidth;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
	 */
	protected int getPrefContentHeight(int maxWidth) {
		//this.availableWidth = maxWidth;
		return this.container.getItemHeight(maxWidth, maxWidth);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	 */
	protected void paint(Graphics g, int w, int h) {
		this.container.paint( 0, 0, 0, w, g );
	}

	protected void hideNotify() {
		this.container.hideNotify();
	}
	
	protected void showNotify() {
		this.container.showNotify();
	}


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		return this.container.handleKeyPressed(keyCode, gameAction);
	}

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y) {
		if (this.container.handlePointerPressed(x, y)) {
			invalidate();
		}
	}
	//#endif


	protected boolean traverse(int direction, int viewWidth, int viewHeight, int[] viewRect_inout) {
		boolean handled = (this.container.handleKeyPressed(0, direction));
		if (handled) {
			viewRect_inout[0] = this.container.internalX;
			viewRect_inout[1] = this.container.internalY;
			viewRect_inout[2] = this.container.internalWidth;
			viewRect_inout[3] = this.container.internalHeight;
		}
		return handled;
	}
	
	protected void traverseOut() {
		this.container.defocus(null);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusstyle, int direction ) {
		return this.container.focus(focusstyle, direction);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		this.container.defocus(originalStyle);
	}

}
