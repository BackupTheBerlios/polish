//#condition polish.usePolishGui
/*
 * Created on 17-Jul-2004 at 11:00:24.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;

/**
 * <p>Paints an animated circular background.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type<b>: the type of the background, needs to be "pulsating-circle".</li>
 * 	<li><b>color<b>: the color of the background, defaults to "white".</li>
 * 	<li><b>min-diameter<b>: the minimum diameter of the circle.</li>
 * 	<li><b>max-diameter<b>: the minimum diameter of the circle.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PulsatingCircleBackground extends Background {
	
	private final int color;
	private final int maxDiameter;
	private final int minDiameter;
	private int currentDiameter;
	private boolean isGrowing = true;
	
	/**
	 * Creates a new pulsating-circle background.
	 * 
	 * @param color the color of this background
	 * @param minDiameter the minimum diameter
	 * @param maxDiameter the maximum diameter
	 */
	public PulsatingCircleBackground( int color, int minDiameter, int maxDiameter ) {
		super();
		this.color = color;
		this.minDiameter = minDiameter;
		this.maxDiameter = maxDiameter;
		this.currentDiameter = minDiameter;
	}

	/**
	 * Renders the background to the screen.
	 * 
	 * @param x the x position of the background
	 * @param y the y position of the background
	 * @param width the width of the background
	 * @param height the height of the background
	 * @param g the Graphics instance for rendering this background
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		int centerX = x + width / 2;
		int centerY = y + height / 2;
		int offset = this.currentDiameter / 2;
		x = centerX - offset;
		y = centerY - offset;
		g.fillArc( x, y, this.currentDiameter, this.currentDiameter, 0, 360 );
	}

	/**
	 * Animates this background.
	 * 
	 * @return always true, since a redraw is needed after each animation.
	 */
	public boolean animate() {
		if (this.isGrowing) {
			this.currentDiameter++;
			if (this.currentDiameter == this.maxDiameter) {
				this.isGrowing = false;
			}
		} else {
			this.currentDiameter--;
			if (this.currentDiameter == this.minDiameter) {
				this.isGrowing = true;
			}
		}
		return true;
	}
}
