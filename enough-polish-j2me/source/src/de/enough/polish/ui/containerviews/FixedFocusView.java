//#condition polish.usePolishGui

/*
 * Created on May 12, 2006 at 8:29:03 PM.
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

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * <p>Leaves the currently focused element in one static/fixed position and only scrolls the other elements.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        May 12, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FixedFocusView extends ContainerView {
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item)
	 */
	protected void focusItem(int index, Item item) {
		super.focusItem(index, item);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int clipY = g.getClipY();
		int clipHeight = g.getClipHeight();
		int yOffset = 0;
		if (this.focusedItem != null) {
			int yTop = getItemYTopPos( this.focusedItem );
			int yBottom = getItemYBottomPos( this.focusedItem );
			
		}
		super.paintContent(x, y + this.yOffset, leftBorder, rightBorder, g);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Container parent, int firstLineWidth, int lineWidth) {
		super.initContent(parent, firstLineWidth, lineWidth);	
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO robertvirkus implement paintItem
		super.paintItem(item, index, x, y, leftBorder, rightBorder, g);
	}
	
	

}
