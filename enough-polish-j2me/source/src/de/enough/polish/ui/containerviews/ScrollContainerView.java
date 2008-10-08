//#condition polish.usePolishGui
/*
 * Created on Sept 9, 2008 at 11:45:01 AM.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ScrollBar;
import de.enough.polish.ui.Style;

/**
 * <p>Allows to scroll any item within specified bounds</p>
 * <p>Usage:</p>
 * <pre>
 * .myItemStyle {
 *    view-type: scroll;
 *    scrollview-height: 50%;
 *    scrollview-scrollbar-style: .myScrollBarStyle;
 * }
 * </pre>
 * <p>
 * The height is relative to it's screen's available height.
 * </p>
 * <p><b>
 * Important: this view will is only available within J2ME Polish 2.0.5+. It will not be available in J2ME Polish 2.1 or higher,
 * as in J2ME Polish 2.1 this will be build in behavior.	
 * </b><p>
 * 
 * @author Robert Virkus
 *
 */
public class ScrollContainerView extends ContainerView {
	
	private int height = -50; //Integer.MIN_VALUE;
	private transient final ScrollBar scrollBar;
	private boolean showVerticalScrollBar;

	/**
	 * Creates a new scroll item view
	 */
	public ScrollContainerView() {
		//#style scrollbar?
		this.scrollBar = new ScrollBar();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item p, int firstLineWidth, int lineWidth) {
		Container parent = (Container)p;
		int sWidth = this.scrollBar.getItemWidth(firstLineWidth, lineWidth);
		lineWidth -= sWidth;
		firstLineWidth -= sWidth;
		super.initContent(p, firstLineWidth, lineWidth);
		this.showVerticalScrollBar = false;
		if (this.height != Integer.MIN_VALUE) {
			int availHeight = parent.getScreen() != null ? parent.getScreen().getAvailableHeight() : 300;
			int h = this.height >= 0 ? this.height : (this.height * availHeight) / -100;
			this.showVerticalScrollBar = (this.contentHeight > h);
			this.scrollBar.initScrollBar(lineWidth, h, this.contentHeight, parent.getScrollYOffset(), parent.getInternalY(), parent.getInternalHeight(), this.focusedIndex, parent.size());
			if (this.showVerticalScrollBar) {
				this.contentHeight = h;
				parent.setScrollHeight(h);
			}
			this.scrollBar.relativeX = lineWidth - sWidth;
		}
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item p, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		super.paintContent(p, x, y, leftBorder, rightBorder, g);
		if (!this.showVerticalScrollBar) {
			return;
		}
		Container parent = (Container)p;
		int realY = y - parent.getCurrentScrollYOffset();
		this.scrollBar.paint(x + this.scrollBar.relativeX, realY + this.scrollBar.relativeY, x + this.scrollBar.relativeX, rightBorder, g);

	}

	/**
	 * Specifies the available height
	 * @param height the height in pixels - negative values are treated as percentage values
	 */
	public void setHeight( int height ) {
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.scrollview-height
			Integer heightInt = style.getIntProperty("viewscroll-height");
			if (heightInt != null) {
				this.height = heightInt.intValue();
			}
		//#endif
		//#if polish.css.scrollview-scrollbar-style
			Style sbStyle = (Style) style.getObjectProperty("scrollview-scrollbar-style");
			if (sbStyle != null) {
				this.scrollBar.setStyle( sbStyle );
			}
		//#endif
	}
	
	
	
}
