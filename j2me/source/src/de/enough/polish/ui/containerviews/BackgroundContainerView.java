//#condition polish.usePolishGui
/*
 * Created on Aug 21, 2006 at 1:10:30 PM.
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * <p>Base class for containerviews that want to paint the background and border of the parent container themselves.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Aug 21, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BackgroundContainerView extends ContainerView {
	
	protected Background background;
	protected Border border;
	protected int paddingLeft;
	protected int paddingRight;
	protected int paddingTop;
	protected int paddingBottom;
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.background != null) {
			animated |= this.background.animate();
		}
		return animated;
	}

	/**
	 * Paints the content as well as the background and the border of the parent container.
	 * 
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int width = rightBorder - leftBorder + this.paddingLeft + this.paddingRight;
		int height = this.contentHeight + this.paddingTop + this.paddingBottom;
		if (this.background != null) {
			this.background.paint( x - this.paddingLeft, y - this.paddingTop, width, height, g);
		}
		if (this.border != null) {
			this.border.paint(x - this.paddingLeft, y - this.paddingTop, width, height, g);
		}
		super.paintContent(parent, x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		this.paddingLeft = style.paddingLeft;
		this.paddingRight = style.paddingRight;
		this.paddingTop = style.paddingTop;
		this.paddingBottom = style.paddingBottom;
		Background bg = removeParentBackground();
		if (bg != null && bg != this.background) {
			this.background = bg;
		}
		Border bord = removeParentBorder();
		if (bord != null && bord != this.border ) {
			this.border = bord;
		}
	}

	
}
