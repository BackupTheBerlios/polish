/*
 * Created on 09-Nov-2004 at 11:04:10.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;

/**
 * <p>Draws an expanding backround which will show a border when it is fully expanded.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BorderedRoundRectOpeningBackground extends Background {
	private int oldX;
	private int oldY;
	private boolean isAnimationRunning;
	private int currentHeight;
	private final int color;
	private final int startHeight;
	private final int steps;
	private int maxHeight;
	private final int arcWidth;
	private final int arcHeight;
	private final int borderColor; 

	/**
	 * Creates a new opening background.
	 * 
	 * @param color the color of the background.
	 * @param startHeight the start height, default is 1
	 * @param steps the number of pixels by which the background-height should be increased 
	 * 			at each animation-step, default is 4
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 * 
	 */
	public BorderedRoundRectOpeningBackground( int color, int startHeight, int steps, int arcWidth, int arcHeight, int borderColor, int borderWidth ) {
		super();
		this.color = color;
		this.startHeight = startHeight;
		this.steps = steps;
		this.arcHeight = arcHeight;
		this.arcWidth = arcWidth;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (x != this.oldX || y != this.oldY ) {
			this.oldX = x;
			this.oldY = y;
			this.currentHeight = this.startHeight;
			this.maxHeight = height;
			this.isAnimationRunning = true;
		} 
		if (this.isAnimationRunning) {
			int difference = height - this.currentHeight;
			height = this.currentHeight;
			y += difference / 2;	
		}
		g.setColor( this.color );
		g.fillRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		if (!this.isAnimationRunning) {
			// draw the border:
			g.setColor( this.borderColor );
			g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
			if (this.borderWidth > 1) {
				int border = this.borderWidth - 1;
				while ( border > 0) {
					g.drawRoundRect( x+border, y+border, width - 2*border, height - 2*border, this.arcWidth, this.arcHeight );
					border--;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate()
	 */
	public boolean animate() {
		if (this.isAnimationRunning) {
			this.currentHeight += this.steps;
			if (this.currentHeight >= this.maxHeight) {
				this.isAnimationRunning = false;
			}
			return true;
		} else {
			return false;
		}
	}

}
