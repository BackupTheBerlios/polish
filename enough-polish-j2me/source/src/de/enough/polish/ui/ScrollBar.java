/*
 * Created on 22-Feb-2006 at 19:09:37.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Realizes a scrollbar for any J2ME Polish screens.</p>
 * <p>
 *    The scrollbar needs to be activated using the &quot;polish.useScrollBar&quot;
 *    variable:
 *    <pre>
 *    &lt;variable name=&quot;polish.useScrollBar&quot; value=&quot;true&quot; /&gt;
 *    </pre>
 * </p>
 * <p>Design the scrollbar using the predefined &quot;scrollbar&quot; style. You
 *    can also specify a screen specific style by defining the &quot;scrollbar-style&quot; CSS attribute.
 *    <br />
 *    Scrollbars support following additional attributes (apart from the background etc):   
 * </p>
 * <ul>
 * 	<li><b>scrollbar-slider-width</b>: The width of the slider in pixels, is ignored when a slider-image is defined.</li>
 * 	<li><b>scrollbar-slider-color</b>: The color of the slider, is ignored when a slider-image is defined.</li>
 * 	<li><b>scrollbar-slider-image</b>: The image used for the slider.</li>
 * 	<li><b>scrollbar-slider-image-repeat</b>: Either &quot;true&quot; or &quot;false&quot;. When &quot;true&quot; is 
 * 							given, the slider image is repeated vertically, if necessary.</li>
 * 	<li><b>scrollbar-slider-mode</b>: The mode of the slider, either &quot;pixel&quot;
 * 							or &quot;item&quot;. In the &quot;pixel&quot; mode the slider represents the
 *                          position of the current selection relativ to the complete width and height.
 *                          In the &quot;item&quot; mode the index of currently selected item
 *                          is taken into account relative to the number of items. The default mode
 *                          is &quot;pixel&quot;.
 * </li>
 * 	<li><b>scrollbar-position</b>: either &quot;left&quot; or &quot;right&quot;.</li>
 * 	<li><b></b>: </li>
 * </ul>
 * <p>You can exchange the default implementation with the &quot;polish.classes.ScrollBar&quot;
 *    variable.
 * </p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        22-Feb-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScrollBar extends Item {
	
	private static final int MODE_ITEM = 1;
	protected int sliderColor;
	protected int sliderWidth = 2;
	//#if polish.css.scrollbar-slider-image
		protected Image sliderImage;
		//#if polish.css.scrollbar-slider-image-repeat
			protected boolean repeatSliderImage;
			protected int repeatSliderNumber;
		//#endif
	//#endif
	//#if polish.css.scrollbar-slider-mode
		protected boolean useItemMode;
	//#endif
	protected int sliderY;
	protected int sliderHeight;
	protected int scrollBarHeight;
	private boolean isVisible;

	/**
	 * Creates a new default scrollbar
	 */
	public ScrollBar() {
		super();
	}

	/**
	 * Creates a new styled scrollbar
	 * 
	 * @param style the style
	 */
	public ScrollBar(Style style) {
		super(style);
	}

	/**
	 * Initializes this scrollbar.
	 * @param screenWidth the width of the screen
	 * @param screenAvailableHeight the height available for the content within the screen
	 * @param screenContentHeight the height of the content area of the screen
	 * @param selectionStart the start of the selection relative to the screenContentHeight
	 * @param selectionHeight the height of the current selection
	 * @param focusedIndex the index of currently focused item
	 * @param numberOfItems the number of available items 
	 * @return the item width of the scroll bar.
	 */
	public int initScrollBar( int screenWidth, int screenAvailableHeight, int screenContentHeight, int selectionStart, int selectionHeight, int focusedIndex, int numberOfItems ) {
		//#debug
		System.out.println("initScrollBar( screenWidth=" + screenWidth + ", screenAvailableHeight=" + screenAvailableHeight + ", screenContentHeight=" + screenContentHeight + ", selectionStart=" + selectionStart + ", selectionHeight=" + selectionHeight + ", focusedIndex=" + focusedIndex + ", numberOfItems=" + numberOfItems + ")");
		
		if ( screenAvailableHeight >= screenContentHeight ) {
			this.isVisible = false;
			return 0;
		}
		this.isVisible = true;
		this.scrollBarHeight = screenAvailableHeight;
		//#if polish.css.scrollbar-slider-mode
			if ( this.useItemMode ) {
				int chunkPerSlider = (screenAvailableHeight << 8) / numberOfItems; 
				this.sliderY = (chunkPerSlider * focusedIndex) >>> 8;
				this.sliderHeight = chunkPerSlider >>> 8;
			} else {
		//#endif
				this.sliderY = (selectionStart * screenAvailableHeight) / screenContentHeight;
				this.sliderHeight = (selectionHeight * screenAvailableHeight) / screenContentHeight;
		//#if polish.css.scrollbar-slider-mode
			}
		//#endif
		//#if polish.css.scrollbar-slider-image && polish.css.scrollbar-slider-image-repeat
			if (this.repeatSliderImage && this.sliderImage != null ) {
				if (this.sliderHeight > this.sliderImage.getHeight()) {
					this.repeatSliderNumber = this.sliderHeight / this.sliderImage.getHeight(); 
				} else {
					this.repeatSliderNumber = 1;
				}
			}
		//#endif
		//#debug
		System.out.println("sliderY=" + this.sliderY + ", sliderHeight=" + this.sliderHeight);
		if (!this.isInitialised) {	
			init( screenWidth, screenWidth );
		}
		return this.itemWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		//#if polish.css.scrollbar-slider-image
			if (this.sliderImage != null) {
				this.contentWidth = this.sliderImage.getWidth();
			} else {
		//#endif
				this.contentWidth = this.sliderWidth;
		//#if polish.css.scrollbar-slider-image
			}
		//#endif
		this.contentHeight = this.scrollBarHeight - ( this.paddingTop + this.paddingBottom + this.marginTop + this.marginBottom );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (!this.isVisible) {
			return;
		}
		super.paint(x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) 
	{
		//#if polish.css.scrollbar-slider-image
			if (this.sliderImage != null) {
				//#if polish.css.scrollbar-slider-image-repeat
					int imageHeight = this.sliderImage.getHeight();
					y = this.sliderY;
					for (int i=this.repeatSliderNumber; --i >= 0; ) {
						g.drawImage(this.sliderImage, x, this.sliderY, Graphics.TOP | Graphics.LEFT );
						y += imageHeight;
					}
				//#else
					g.drawImage(this.sliderImage, x, this.sliderY, Graphics.TOP | Graphics.LEFT );
				//#endif
			} else {
		//#endif
				g.setColor( this.sliderColor );
				g.fillRect(x, y + this.sliderY, this.contentWidth, this.sliderHeight);
		//#if polish.css.scrollbar-slider-image
			}
		//#endif
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "scrollbar";
	}
	//#endif


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.scrollbar-slider-image
			String url = style.getProperty("scrollbar-slider-image");
			if (url != null) {
				try {
					this.sliderImage = StyleSheet.getImage(url, url, false);
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load scrollbar slider image " + url + e );
				}
			}
			//#if polish.css.scrollbar-slider-image-repeat
				Boolean repeatSliderImageBool = style.getBooleanProperty("scrollbar-slider-image-repeat");
				if (repeatSliderImageBool != null) {
					this.repeatSliderImage = repeatSliderImageBool.booleanValue();
				}
			//#endif
		//#endif
		//#if polish.css.scrollbar-slider-width
			Integer sliderWidthInt = style.getIntProperty("scrollbar-slider-width");
			if (sliderWidthInt != null) {
				this.sliderWidth = sliderWidthInt.intValue();
			}
		//#endif
		//#if polish.css.scrollbar-slider-color
			Integer sliderColorInt = style.getIntProperty("scrollbar-slider-color");
			if (sliderColorInt != null) {
				this.sliderColor = sliderColorInt.intValue();
			}
		//#endif
			
		//#if polish.css.scrollbar-slider-mode
			Integer sliderModeInt = style.getIntProperty("scrollbar-slider-mode");
			if (sliderModeInt != null) {
				this.useItemMode = ( sliderModeInt.intValue() ==  MODE_ITEM);
			}
		//#endif
	}

	
}
