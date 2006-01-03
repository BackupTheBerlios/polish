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

/**
 * <p>Provides a list of items that can be used within a Form.</p>
 *
 * <p>Copyright Enough Software 2005</p>
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
	
	private int minContentWidth;
	private int minContentHeight;
	private int prefContentWidth;
	private int prefContentHeight;
	private final Container container;

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 */
	public ListItem(String label, int listType) {
		this( label, listType, null );
	}

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 * @param listType one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 * @param style the style
	 * @throws IllegalArgumentException if listType is not one of IMPLICIT, EXCLUSIVE, or MULTIPLE
	 */
	public ListItem(String label, int listType, Style style) {
		super(label);
		this.container = new Container( false );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
	 */
	protected int getMinContentWidth() {
		return this.minContentWidth;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
	 */
	protected int getMinContentHeight() {
		return this.minContentHeight;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
	 */
	protected int getPrefContentWidth(int maxHeight) {
		return this.prefContentWidth;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
	 */
	protected int getPrefContentHeight(int maxWidth) {
		return this.prefContentHeight;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	 */
	protected void paint(Graphics g, int w, int h) {
		this.container.paint( 0, 0, 0, this.prefContentWidth, g );
	}

}
