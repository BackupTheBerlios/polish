//#condition polish.usePolishGui
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.Image;

/**
 * <p>Separates a form into several tabs.</p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        23-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabbedForm extends Form {
	
	//#if polish.classes.TabBar:defined
		//#= private final ${polish.classes.TabBar} tabBar;
	//#else
		private final TabBar tabBar;
	//#endif
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
		//#if polish.classes.TabBar:defined
			//#style tabbar, default
			//#= this.tabBar = new ${polish.classes.TabBar} ( tabNames, tabImages );
		//#else
			//#style tabbar, default
			this.tabBar = new TabBar( tabNames, tabImages );
		//#endif
		int length;
		if (tabNames != null) {
			length = tabNames.length;
		} else {
			length = tabImages.length;
		}
		this.tabContainers = new Container[ length ];
		this.tabContainers[0] = this.container;
		for (int i = 1; i < length; i++) {
			Container tabContainer = new Container( null, true, null, 20, this.screenHeight );
			tabContainer.setStyle( style, true );
			tabContainer.screen = this;
			this.tabContainers[i] = tabContainer;
		}
		setSubTitle( this.tabBar );
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
	 * Adds the  item  to the first tab of this form.
	 * 
	 * @param item the item which should be added.
	 * @param itemStyle the style for that item
   * @return the assigned index of the Item within the specified tab
	 */
	public int append( Item item, Style itemStyle ) {
		return append( 0, item, itemStyle );
	}
	
	/**
	 * Adds the  item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
	 * @return the assigned index of the Item within the specified tab
	 */
	public int append( int tabIndex, Item item ) {
		return append( tabIndex, item, null );		
	}

	/**
	 * Adds the  item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
   * @param itemStyle the style for that item
	 * @return the assigned index of the Item within the specified tab
	 */
	public int append( int tabIndex, Item item, Style itemStyle ) {
		//#if polish.Container.allowCycling != false
			if (item instanceof Container) {
				((Container)item).allowCycling = false;
			}
		//#endif
		if (itemStyle != null) {
			item.setStyle( itemStyle );
		}
 		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.add(item);
		return tabContainer.size() - 1;
	}

	/**
	 * Changes the item of the first tab.
	 * 
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int itemIndex, Item item ) {
		set( 0, itemIndex, item );
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
		//#if polish.Container.allowCycling != false
			if (item instanceof Container) {
				((Container)item).allowCycling = false;
			}
		//#endif
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.set(itemIndex, item);
	}
	
	/**
	 * Gets the item at given position within the specified tab.  
	 * The contents of the
	 * <code>TabbedForm</code> are left unchanged.
	 * The <code>itemNum</code> parameter must be
	 * within the range <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemNum the index of item
	 * @return the item at the given position
	 * @throws IndexOutOfBoundsException - if itemNum is invalid
	 */
	public Item get(int tabIndex, int itemNum)
	{
		Container tabContainer = this.tabContainers[ tabIndex ];
		return tabContainer.get( itemNum );
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
		if (this.isShown() ) {
			repaint();
		}
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
		if (this.isShown() ) {
			repaint();
		}
	}

	/**
	 * Deletes the all items from the specified tab.
	 * 
	 * @param tabIndex the index of the tab from which all items should be removed,
	 *        the first tab has the index 0.
	 */
	public void deleteAll( int tabIndex ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		tabContainer.clear();
	}
	
	/**
	 * Retrieves the number of elements within the specified tab.
	 * 
	 * @param tabIndex the tab, the first tab has the index 0
	 * @return the number of elements within that tab
	 */
	public int size( int tabIndex ) {
		Container tabContainer = this.tabContainers[ tabIndex ];
		return tabContainer.size();
	}

	/**
	 * Focuses the specified tab.
	 * 
	 * @param tabIndex the index of the tab, the first tab has the index 0.
	 */
	public void setActiveTab( int tabIndex ) {
		//#debug
		System.out.println("Activating tab [" + tabIndex + "].");
		if (this.container.isInitialised) {
			//System.out.println("defocus of container " + this.container);
			this.container.hideNotify();
			this.container.defocus( this.container.style );
			//#if polish.TabbedForm.releaseResourcesOnTabChange
				this.container.releaseResources();
			//#endif
		}
		this.activeTabIndex = tabIndex;
		this.tabBar.setActiveTab(tabIndex);
		Container tabContainer = this.tabContainers[ tabIndex ];
		this.container = tabContainer;
		tabContainer.setVerticalDimensions( this.contentY, this.contentY + this.contentHeight );
		if (!tabContainer.isInitialised) {
			tabContainer.init( this.contentWidth, this.contentWidth );
		}
		if (tabContainer.appearanceMode != Item.PLAIN) {
			//#debug
			System.out.println("Focusing tab [" + tabIndex + "].");
			tabContainer.focus( tabContainer.style, 0 );
		}
        //#if polish.blackberry
	        else {
	            //# setFocus( tabContainer );
	        }
	    //#endif
		tabContainer.background = null;
		tabContainer.border = null;
		if (isShown()) {
			tabContainer.showNotify();
			repaint();
		}
	}
	
	/**
	 * Sets the image for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param image the image
	 */
	public void setTabImage( int tabIndex, Image image ) {
		this.tabBar.setImage( tabIndex, image );
	}
	
	/**
	 * Sets the text for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param text the text
	 */
	public void setText(int tabIndex, String text ) {
		this.tabBar.setText( tabIndex, text );	
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
//		if (focusedItem instanceof Container) {
//			focusedItem = ((Container) focusedItem).focusedItem;
//		}
		if (focusedItem != null && focusedItem.handleKeyPressed(keyCode, gameAction)) {
			if (focusedItem.internalX != -9999) {
				this.container.adjustScrolling( focusedItem );
			}

			return true;
		} else if ( this.tabBar.handleKeyPressed(keyCode, gameAction)) {
			setActiveTab( this.tabBar.getNextTab() );
			notifyScreenStateChanged();
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
	
	//#ifdef polish.hasPointerEvents
	/**
	 * Handles the pressing of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer pressing, it needs to 
	 * return true.
	 * The default implementation returns the result of calling the container's
	 *  handlePointerPressed-method
	 *  
	 * @param x the x position of the pointer pressing
	 * @param y the y position of the pointer pressing
	 * @return true when the pressing of the pointer was actually handled by this item.
	 */
	protected boolean handlePointerPressed( int x, int y ) {
		if ( y <= this.tabBar.yBottomPos && y >= this.tabBar.yTopPos 
				&& this.tabBar.handlePointerPressed(x, y)) 
		{
			if (this.tabBar.newActiveTabIndex == this.activeTabIndex) {
				return false;
			}
			setActiveTab( this.tabBar.newActiveTabIndex );
			notifyScreenStateChanged();
			return true;
		} else {
			return super.handlePointerPressed(x, y);
		}
	}
	//#endif
	
	/**
	 * Retrieves the index of the currently active tab.
	 * 
	 * @return the index of the currently active tab, 0 is the first tab. 
	 */
	public int getSelectedTab() {
		return this.activeTabIndex;
	}
	
	/**
	 * Sets the screen listener for this TabbedForm.
	 * 
	 * @param listener the listener that is notified whenever the user selects another tab,
	 */
	public void setScreenStateListener( ScreenStateListener listener ) {
		this.screenStateListener = listener;
	}

}
