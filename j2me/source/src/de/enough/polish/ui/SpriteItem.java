//#condition polish.usePolishGui || polish.midp2
/*
 * Created on 15-Feb-2005 at 01:39:10.
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

import javax.microedition.lcdui.Graphics;
//#if polish.midp1 || (polish.usePolishGameApi == true)
	//# import de.enough.polish.ui.game.Sprite;
//#else
	import javax.microedition.lcdui.game.Sprite;
//#endif

/**
 * <p>Allows to use sprites within normal forms.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        15-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
	
public class SpriteItem
//#ifdef polish.usePolishGui
	//# extends CustomItem
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
{
	
	private final Sprite sprite;
	private final boolean animate;

	/**
	 * Creates a new sprite item.
	 * 
	 * @param label the label of this item
	 * @param sprite the sprite that should be painted
	 * @param animate true when this sprite should be animated
	 */
	public SpriteItem(String label, Sprite sprite, boolean animate ) {
		this( label, sprite, animate, null );
	}

	/**
	 * Creates a new sprite item.
	 * 
	 * @param label the label of this item
	 * @param sprite the sprite that should be painted
	 * @param animate true when this sprite should be animated
	 * @param style the CSS style
	 */
	public SpriteItem(String label, Sprite sprite, boolean animate, Style style) {
		//#ifdef polish.usePolishGui
			//# super(label, style);
		//#else
			super( label );
		//#endif
		this.sprite = sprite;
		this.animate = animate;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getMinContentWidth()
	 */
	protected int getMinContentWidth() {
		return this.sprite.getWidth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getMinContentHeight()
	 */
	protected int getMinContentHeight() {
		return this.sprite.getHeight();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getPrefContentWidth(int)
	 */
	protected int getPrefContentWidth(int height) {
		return this.sprite.getWidth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getPrefContentHeight(int)
	 */
	protected int getPrefContentHeight(int width) {
		return this.sprite.getHeight();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	 */
	protected void paint(Graphics g, int w, int h) {
		this.sprite.paint(g);
		if (this.animate) {
			this.sprite.nextFrame();
			repaint();
		}
	}

}
