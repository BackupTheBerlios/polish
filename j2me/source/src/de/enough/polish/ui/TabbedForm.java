/*
 * Created on 23-Jan-2005 at 18:46:50.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

/**
 * <p>Separates a form into several tabs.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabbedForm extends Form {
	
	private final TabBar tabBar;
	private final Container[] tabContainers;
	private int activeTabIndex;

	/**
	 * Creates a new tabbed form without a style.
	 * 
	 * @param title the title of the form.
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 */
	public TabbedForm(String title, String[] tabNames, Image[] tabImages ) {
		this( title, tabNames, tabImages, null );
	}

	/**
	 * Creates a new tabbed form.
	 *  
	 * @param title the title of the form.
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 * @param style the style of this tabbed form.
	 * @throws NullPointerException if tabNames is null
	 */
	public TabbedForm(String title, String[] tabNames, Image[] tabImages, Style style) {
		super(title, style );
		//#style tabbar, default
		this.tabBar = new TabBar( tabNames, tabImages );
		this.tabContainers = new Container[ tabNames.length ];
		this.tabContainers[0] = this.container;
		for (int i = 1; i < tabNames.length; i++) {
			Container tabContainer = new Container( null, true, style, 20, this.screenHeight );
			tabContainer.screen = this;
			this.tabContainers[i] = tabContainer;
		}
		setSubTitle(this.tabBar);
	}

	//#if false
	/**
	 * Adds the item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
	 */
	public void append( int tabIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif

	//#if false
	/**
	 * Changes the item of a tab.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int tabIndex, int itemIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif

	
	//#if false
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param item the item which should be removed.
	 */
	public void delete( int tabIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif
	
	
	//#if false
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener listener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a tabbed form.");
	}
	//#endif
	
	/**
	 * Adds the  item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
	 */
	public void append( int tabIndex, Item item ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.add(item);
	}


	/**
	 * Changes the item of a tab.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int tabIndex, int itemIndex, Item item ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.set(itemIndex, item);
	}

	
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param item the item which should be removed.
	 */
	public void delete( int tabIndex, Item item ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.remove( item );
	}
	
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item which should be removed.
	 */
	public void delete( int tabIndex, int itemIndex ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.remove( itemIndex );
	}
	
	/**
	 * Focuses the specified tab.
	 * 
	 * @param tabIndex the index of the tab, the first tab has the index 0.
	 */
	public void setActiveTab( int tabIndex ) {
		if (this.container.isInitialised) {
			this.container.defocus( this.container.style );
		}
		this.activeTabIndex = tabIndex;
		this.tabBar.setActiveTab(tabIndex);
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.setVerticalDimensions( 0, this.screenHeight - (this.titleHeight  + this.subTitleHeight + this.infoHeight ) );
		tabContainer.focus( tabContainer.style );
		this.container = tabContainer;
		if (isShown()) {
			repaint();
		}
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "tabbedform";
	}
	//#endif
	
	
	
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		Item focusedItem = this.container.focusedItem;
		if (focusedItem instanceof Container) {
			focusedItem = ((Container) focusedItem).focusedItem;
		}
		if (focusedItem != null && focusedItem.handleKeyPressed(keyCode, gameAction)) {
			return true;
		} else if (gameAction == Canvas.RIGHT && this.activeTabIndex < this.tabContainers.length -1 ) {
			setActiveTab( ++this.activeTabIndex );
			return true;
		} else if (gameAction == Canvas.LEFT && this.activeTabIndex > 0) {
			setActiveTab( --this.activeTabIndex );
			return true;
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}
	
	
	public void focus(Item item) {
		for (int i = 0; i < this.tabContainers.length; i++) {
			Container tabContainer = this.tabContainers[i];
			if ( tabContainer.itemsList.contains(item)) {
				if (i != this.activeTabIndex) {
					setActiveTab( i );
				}
				super.focus(item);
				return;
			}
		}
	}
}
