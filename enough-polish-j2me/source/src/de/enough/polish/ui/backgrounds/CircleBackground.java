//#condition polish.usePolishGui
/*
 * Created on 26-Jul-2004 at 14:13:59.
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
 * <p>Paints a circular or elliptical background.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the background, needs to be "circle".</li>
 * 	<li><b>color</b>: the color of the background, defaults to "white".</li>
 * 	<li><b>diameter</b>: the diameter of the background, when defined
 * 	always a circle will be painted.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CircleBackground extends Background {

	private final int color;
	private final int diameter;

	/**
	 * Creates a new circle background.
	 * 
	 * @param color the color of the background.
	 * @param diameter the diameter of the background, when -1 this will
	 *      be ignored, otherwise this will result in a centered circle
	 *      (instead of an ellipse).
	 */
	public CircleBackground( int color, int diameter ) {
		this.color = color;
		this.diameter = diameter;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.diameter != -1) {
			int centerX = x + width / 2;
			int centerY = y + height / 2;
			int offset = this.diameter / 2;
			x = centerX - offset;
			y = centerY - offset;		
			width = this.diameter;
			height = this.diameter;
		}
		g.setColor( this.color );
		g.fillArc(x, y, width, height, 0, 360 );	
	}

}
