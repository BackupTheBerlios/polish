//#condition polish.usePolishGui
/*
 * Created on 23-Jan-2005 at 19:04:14.
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
 * <p>Manages and paints the tabs of a tabbed form (or another Screen).</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabBar extends Item {

	private final ImageItem[] tabs;
	//#if true
		//# private final Style activeStyle;
		//# private final Style inactiveStyle;
	//#else
		private Style activeStyle;
		private Style inactiveStyle;
	//#endif
	
	private int activeTabIndex;
	//#ifdef polish.hasPointerEvents
	protected int newActiveTabIndex;
	//#endif
	private int xOffset;
	private int scrollArrowHeight = 10;
	private int scrollArrowPadding = 2;
	private int scrollArrowColor = 0xFFFFFF;

	/**
	 * Creates a new tab bar.
	 * 
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 */
	public TabBar(String[] tabNames, Image[] tabImages) {
		this( tabNames, tabImages, null );
	}

	/**
	 * Creates a new tab bar.
	 * 
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 * @param style the style of the bar
	 */
	public TabBar(String[] tabNames, Image[] tabImages, Style style) {
		super( null, 0, Item.BUTTON, style);
		if (tabImages == null) {
			tabImages = new Image[ tabNames.length ];
		}
		// getting styles:
		//#if true
			//#style activetab, tab, default
			//# this.activeStyle = ();
			//#style inactivetab, tab, default
			//# this.inactiveStyle = ();
		//#endif
		
		this.tabs = new ImageItem[ tabNames.length ];
		for (int i = 0; i < tabImages.length; i++) {
			String name = tabNames[i];
			Image image = tabImages[i];
			ImageItem tab = new ImageItem( null, image, 0, name, this.inactiveStyle );
			this.tabs[i] = tab;
		}

		this.tabs[0].style = this.activeStyle;
	}
	
	/**
	 * Changes the active/selected tab.
	 * 
	 * @param index the index of the active tab, the first tab has the index 0.
	 */
	public void setActiveTab( int index ) {
		// deactivating the old tab:
		this.tabs[ this.activeTabIndex ].setStyle(this.inactiveStyle);
		// activating the new tab:
		this.tabs[ index ].setStyle(this.activeStyle);
		this.activeTabIndex = index;
		this.isInitialised = false;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		int scrollerWidth = this.scrollArrowHeight + 2 * this.scrollArrowPadding; 
		int maxHeight = scrollerWidth;
		int completeWidth = 0;
		if (this.activeTabIndex == 0 || this.activeTabIndex == this.tabs.length -1) {
			// only one scroll indicator needs to be painted
			lineWidth -= maxHeight;
			completeWidth = maxHeight;
		} else {
			lineWidth -= 2 * maxHeight;
			completeWidth = 2 * maxHeight;
		}
		
		int activeTabXPos = 0;
		int activeTabWidth = 0;
		for (int i = 0; i < this.tabs.length; i++) {
			ImageItem tab = this.tabs[i];
			int tabHeight = tab.getItemHeight(firstLineWidth, lineWidth);
			if (tabHeight > maxHeight ) { 
				maxHeight = tabHeight;
			}
			if (i == this.activeTabIndex) {
				activeTabXPos = completeWidth;
				activeTabWidth = tab.itemWidth;
			}
			// I can use the itemWidth field, since I have called getItemHeight(..) above,
			// which initialises the tab.
			completeWidth += tab.itemWidth;
		}
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		if (this.activeTabIndex == 0) {
			this.xOffset = 0;
		} else if ( this.xOffset + activeTabXPos < 2 * scrollerWidth ) {
			this.xOffset = 2 * scrollerWidth - activeTabXPos;
		} else if ( this.xOffset + activeTabXPos + activeTabWidth > lineWidth ) {
			this.xOffset = (lineWidth - activeTabWidth) - (activeTabXPos - scrollerWidth); 
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) 
	{
		// draw scrolling indicators:
		g.setColor( this.scrollArrowColor );
		y += 2;
		if ( this.activeTabIndex > 0 ) {
			// draw left scrolling indicator:
			x += this.scrollArrowPadding;
			int halfWidth = this.scrollArrowHeight / 2;
			//#ifdef polish.midp2
				g.fillTriangle(x, y + halfWidth-1, x + this.scrollArrowHeight, y, x + this.scrollArrowHeight, y + this.scrollArrowHeight );
			//#else
				g.drawLine( x, y + halfWidth-1, x + this.scrollArrowHeight, y );
				g.drawLine( x + this.scrollArrowHeight, y, x + this.scrollArrowHeight, y + this.scrollArrowHeight);
				g.drawLine( x, y + halfWidth-1, x + this.scrollArrowHeight, y  + this.scrollArrowHeight );
			//#endif
			x += this.scrollArrowHeight + this.scrollArrowPadding;
		}
		if ( this.activeTabIndex < this.tabs.length - 1 ) {
			// draw right scrolling indicator:
			rightBorder -=  (this.scrollArrowHeight + this.scrollArrowPadding);
			int halfWidth = this.scrollArrowHeight / 2;
			//#ifdef polish.midp2
				g.fillTriangle(rightBorder, y, rightBorder, y  + this.scrollArrowHeight, rightBorder + this.scrollArrowHeight, y + halfWidth - 1 );
			//#else
				g.drawLine( rightBorder, y, rightBorder, y  + this.scrollArrowHeight );
				g.drawLine( rightBorder, y, rightBorder + this.scrollArrowHeight, y + halfWidth - 1);
				g.drawLine( rightBorder, y  + this.scrollArrowHeight, rightBorder + this.scrollArrowHeight, y + halfWidth - 1);
			//#endif
			rightBorder -=  this.scrollArrowPadding;
		}

		// draw the tabs:
		y -= 2;
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		g.setClip( x, y, rightBorder - x, clipHeight);
		x += this.xOffset;
		for (int i = 0; i < this.tabs.length; i++) {
			ImageItem tab = this.tabs[i];
			tab.paint( x, y, leftBorder, rightBorder, g );
			x += tab.itemWidth;
		}
		g.setClip( clipX, clipY, clipWidth, clipHeight);		
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "tabbar";
	}
	//#endif
	

	public void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.tabbar-scrolling-indicator-color
			Integer scrollColorInt = style.getIntProperty("tabbar-scrolling-indicator-color");
			if (scrollColorInt != null) {
				this.scrollArrowColor = scrollColorInt.intValue();
			}
		//#endif
	}
	
	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerPressed(int x, int y) {
		//System.out.println( "pointer-pressed: " + x + ", " + y);
		int scrollerWidth = this.scrollArrowHeight + 2 * this.scrollArrowPadding; 
		if ( this.activeTabIndex > 0 && x <= scrollerWidth ) {
			//System.out.println("left: x <= " + scrollerWidth );
			this.newActiveTabIndex = this.activeTabIndex - 1;
		} else if ( this.activeTabIndex < this.tabs.length -1 && x >= this.xRightPos - scrollerWidth) {
			//System.out.println("right: x >= " + (this.xRightPos - scrollerWidth) );
			this.newActiveTabIndex = this.activeTabIndex + 1;
		} else {
			int width = this.xOffset;
			for (int i = 0; i < this.tabs.length; i++) {
				ImageItem tab = this.tabs[i];
				if ( x >= width && x <= width + tab.itemWidth) {
					//System.out.println("i-1=" + i + ": x <= " + tab.xLeftPos + ", x <= " + tab.xRightPos );
					this.newActiveTabIndex = i;
					return true;
				}
				width += tab.itemWidth;
			}
			this.newActiveTabIndex = this.tabs.length - 1;
		}
		
		return true;
	}
	//#endif
	

}
