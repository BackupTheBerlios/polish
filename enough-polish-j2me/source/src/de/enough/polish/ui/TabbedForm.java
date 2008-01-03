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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

/**
 * <p>Separates a form into several tabs.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2008</p>
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
	private TabbedFormListener tabbedFormListener;

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
		//#if polish.Container.allowCycling != false
			this.tabContainers[0].allowCycling = false;
		//#endif
		for (int i = 1; i < length; i++) {
			Container tabContainer = new Container( null, false, null, this.screenHeight );
			if (style != null) {
				tabContainer.setStyle( style, true );
			}
			tabContainer.screen = this;
			//#if polish.Container.allowCycling != false
				tabContainer.allowCycling = false;
			//#endif
			this.tabContainers[i] = tabContainer;
		}
		setSubTitle( this.tabBar );
	}

	//#if polish.LibraryBuild
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

	//#if polish.LibraryBuild
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

	
	//#if polish.LibraryBuild
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
	//#if polish.LibraryBuild
		public javax.microedition.lcdui.Item get(int tabIndex, int itemNum)
		{
			return null;
		}
	//#else
		//# public Item get(int tabIndex, int itemNum)
		//# {
		//#	Container tabContainer = this.tabContainers[ tabIndex ];
		//#	return tabContainer.get( itemNum );
		//# }
	//#endif

	
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
	 * Retrieves the number of tabs in this <code>TabbedForm</code>.
	 * 
	 * @return the number of tabs
	 */
	public int getTabCount() {
		return this.tabContainers.length;
	}

	/**
	 * Focuses the specified tab.
	 * 
	 * @param tabIndex the index of the tab, the first tab has the index 0.
	 */
	public void setActiveTab( int tabIndex ) {
		setActiveTab(tabIndex, true);
	}

	public void setActiveTab( int tabIndex, boolean focusTabBar ) {
		if (!notifyTabbedChangeRequested( this.activeTabIndex, tabIndex )) {
			return;
		}
		//#debug
		System.out.println("Activating tab [" + tabIndex + "].");
		if (this.container.isInitialized) {
			//System.out.println("defocus of container " + this.container);
			this.container.hideNotify();
			this.container.defocus( this.container.style );
			//#if polish.TabbedForm.releaseResourcesOnTabChange
				this.container.releaseResources();
			//#endif
		}
		int oldTabIndex = this.activeTabIndex;
		this.activeTabIndex = tabIndex;
		this.tabBar.setActiveTab(tabIndex);
		Container tabContainer = this.tabContainers[ tabIndex ];
		this.container = tabContainer;
		tabContainer.setScrollHeight( this.contentHeight );
		if (!tabContainer.isInitialized) {
			tabContainer.init( this.contentWidth, this.contentWidth );
		}
		if (
		//#if polish.TabbedForm.allowTabSelection
			!focusTabBar && 
		//#endif
			tabContainer.appearanceMode != Item.PLAIN) 
		{
				//#debug
				System.out.println("Focusing tab [" + tabIndex + "].");
				tabContainer.focus( tabContainer.style, 0 );
		        //#if polish.blackberry
			        //# setFocus( tabContainer );
			    //#endif
			}
		tabContainer.background = null;
		tabContainer.border = null;
		if (isShown()) {
			tabContainer.showNotify();
			repaint();
		}
		notifyTabbedChangeCompleted(oldTabIndex, this.activeTabIndex);
		//#if polish.TabbedForm.allowTabSelection
			if (focusTabBar) {
				tabContainer.defocus( tabContainer.style );
				//TODO check again when allowing tab-specific styles
				tabContainer.background = null;
				tabContainer.border = null;
				this.tabBar.focus( this.tabBar.getFocusedStyle(), 0);
			}
		//#endif
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
		//#if polish.TabbedForm.allowTabSelection
		if (this.tabBar.isFocused) {
			int nextTabIndex = this.activeTabIndex;
			//#if polish.css.tabbar-roundtrip
				if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6) {
					nextTabIndex = this.activeTabIndex + 1;
					if (nextTabIndex >= this.tabContainers.length) {
						nextTabIndex = 0;
					}
				} else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4) {
					nextTabIndex = this.activeTabIndex - 1;
					if (nextTabIndex < 0) {
						nextTabIndex = this.tabContainers.length - 1;
					}		
				}
			//#else
				if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6 && this.activeTabIndex < (this.tabContainers.length - 1)) {
					nextTabIndex = this.activeTabIndex + 1;
				}
				else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4 && this.activeTabIndex > 0) {
					nextTabIndex = this.activeTabIndex - 1;
				}
			//#endif
			else if (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8) {
				this.tabBar.defocus(this.tabBar.style);
				this.container.focus(this.container.style, Canvas.DOWN);
				return true;
			}
			//#if polish.css.tabbar-roundtrip
				else if (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) {
					this.tabBar.defocus(this.tabBar.style);
					this.container.focus(this.container.style, Canvas.UP);
					return true;
				}
			//#endif

			if (this.activeTabIndex != nextTabIndex) {
				setActiveTab(nextTabIndex, true);
				return true;
			}

			// Don't continue processing events when tabbar is focused.
			return false;
		}
		//#endif

		boolean handled = super.handleKeyPressed(keyCode, gameAction);

		//#if !polish.TabbedForm.allowTabSelection
			if (!handled) {
				int nextTabIndex = this.activeTabIndex;
				//#if polish.css.tabbar-roundtrip
					if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6) {
						nextTabIndex = this.activeTabIndex + 1;
						if (nextTabIndex >= this.tabContainers.length) {
							nextTabIndex = 0;
						}
					} else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4) {
						nextTabIndex = this.activeTabIndex - 1;
						if (nextTabIndex < 0) {
							nextTabIndex = this.tabContainers.length - 1;
						}		
					}
				//#else
					if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6 && this.activeTabIndex < (this.tabContainers.length - 1)) {
						nextTabIndex = this.activeTabIndex + 1;
					}
					else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4 && this.activeTabIndex > 0) {
						nextTabIndex = this.activeTabIndex - 1;
					}
				//#endif
				if (this.activeTabIndex != nextTabIndex) {
					setActiveTab(nextTabIndex, true);
					return true;
				}

			}
		//#else
			// Focus the tabbar when needed.
			if (!handled) {
				if (gameAction == Canvas.UP || gameAction == Canvas.DOWN) {
					this.container.defocus(this.container.style);
					this.tabBar.focus(this.tabBar.style, gameAction);
					return true;
				}
				//#if polish.blackberry and !polish.hasTrackballEvents
					else if (gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT) {
						this.container.defocus(this.container.style);
						this.tabBar.focus(this.tabBar.style, gameAction);
						return true;
					}
				//#endif
			}
		//#endif

		return handled;
	}
	
	
	public void focus(Item item) {
		if (item == this.tabBar) {
			item.focus( item.getFocusedStyle(), 0);
			// defocus current container:
			this.container.defocus( this.container.style );
			return;
		}
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
	
	
	/**
	 * Retrieves the index of the currently active tab.
	 * 
	 * @return the index of the currently active tab, 0 is the first tab. 
	 * @deprecated
	 * @see #getActiveTab()
	 */
	public int getSelectedTab() {
		return this.activeTabIndex;
	}
	
	/**
	 * Retrieves the index of the currently active tab.
	 * 
	 * @return the index of the currently active tab, 0 is the first tab. 
	 */
	public int getActiveTab() {
		return this.activeTabIndex;
	}


	/**
	 * Notifies the <code>TabbedFormListener</code> that a tab change was requested. Then
	 * <code>TabbedFormListener</code> can now allow or disallow the tab change.
	 * 
	 * @param oldTabIndex the index of the old tab
	 * @param newTabIndex the index of the new tab
	 * @return <code>true</code> if a tab change is okay, <code>false</code> otherwise
	 */
	public boolean notifyTabbedChangeRequested(int oldTabIndex, int newTabIndex) {
		if (this.tabbedFormListener != null) {
			return this.tabbedFormListener.notifyTabChangeRequested(oldTabIndex, newTabIndex);
		}

		return true;
	}

	/**
	 * Notifies the <code>TabbedFormListener</code> that a tab change is completed.
	 * 
	 * @param oldTabIndex the index of the old tab
	 * @param newTabIndex the index of the new tab
	 */
	public void notifyTabbedChangeCompleted(int oldTabIndex, int newTabIndex) {
		if (this.tabbedFormListener != null) {
			this.tabbedFormListener.notifyTabChangeCompleted(oldTabIndex, newTabIndex);
		}
	}

	/**
	 * Sets the <code>TabbedFormListener</code> to be notified when tab changes happen.
	 * 
	 * @param listener the listener that is notified whenever the user selects another tab,
	 */
	public void setTabbedFormListener( TabbedFormListener listener ) {
		this.tabbedFormListener = listener;
	}

	public Item getCurrentItem() {
		if (this.tabBar.isFocused) {
			return this.tabBar;
		}
		return super.getCurrentItem();
	}

}
