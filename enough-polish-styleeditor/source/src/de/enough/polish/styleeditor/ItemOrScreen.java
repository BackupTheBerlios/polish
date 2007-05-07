/*
 * Created on Apr 23, 2007 at 4:58:38 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.styleeditor;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * <p>Provides the target or source of a style - either an item or a screen.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ItemOrScreen {
	private Item item;
	private Screen screen;
	
	public ItemOrScreen( Item item ) {
		if (item == null) {
			throw new IllegalArgumentException("item must not be null");
		}
		this.item = item;
	}
	
	public ItemOrScreen( Screen screen ) {
		if (screen == null) {
			throw new IllegalArgumentException("screen must not be null");
		}
		this.screen = screen;
	}

	public ItemOrScreen( Screen screen, Item item ) {
		if (item == null && screen == null) {
			throw new IllegalArgumentException( "either screen or item must not be null");
		}
		this.screen = screen;
		this.item = item;
	}

	
	/**
	 * Tests whether this given filter name matches this item or screen's class.
	 * 
	 * @param filter the filter, e.g. "StringItem" or "TabbedForm"
	 * @return true when the filter is applicable for this item or screen
	 */
	public boolean appliesTo( String filter ) {
		Class objectClass = this.item == null? this.screen.getClass() : this.item.getClass();
		boolean isApplicable = objectClass.getName().equalsIgnoreCase(filter);
		while (!isApplicable && objectClass.getSuperclass() != null) {
			objectClass = objectClass.getSuperclass();
			isApplicable = objectClass.getName().equalsIgnoreCase(filter);
		}
		return isApplicable;
	}

	/**
	 * @param style
	 */
	public void setStyle(Style style) {
		if (this.item != null) {
			this.item.setStyle(style);
		}
		if (this.screen != null) {
			this.screen.setStyle(style);
		}
	}

	/**
	 * 
	 */
	public void requestInit() {
		if (this.item != null) {
			this.item.requestInit();
			Screen itemScreen = this.item.getScreen();
			if (itemScreen != null) {
				//System.out.println("ItemOrScreen.requestInit() success: repaint called on item's screen. ");
				itemScreen.repaint();
//			} else {
//				System.out.println("ItemOrScreen.requestInit() failed: item has no attached screen. ");
			}
		}
		if (this.screen != null) {
//			System.out.println("ItemOrScreen.requestInit() success: repaint called on screen. ");
			this.screen.repaint();
		}
		
	}

	/**
	 * @return
	 */
	public Object getItemOrScreen() {
		if (this.item != null) {
			return this.item;
		} else {
			return this.screen;
		}
	}

}
