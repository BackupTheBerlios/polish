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
 * <p>Paints an animated background filled with several ever-growing circles.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the background, needs to be "pulsating-circles".</li>
 * 	<li><b>first-color</b>: the first color of a circle.</li>
 * 	<li><b>second-color</b>: the second color of a circle.</li>
 * 	<li><b>min-diameter</b>: the minimum diameter of a circle.</li>
 * 	<li><b>max-diameter</b>: the minimum diameter of a circle.</li>
 * 	<li><b>circles-number</b>: the number of the circles.</li>
 * 	<li><b>step</b>: the number of pixels each circle should grow in every animation.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PulsatingCirclesBackground extends Background {
	
	private int startColor;
	private final int firstColor;
	private final int secondColor;
	private final short[] diameters;
	private final int preciseMinDiameter;
	private final int preciseMaxDiameter;
	private final int[] preciseDiameters; 
	private final int diameterGrowth;
	
	/**
	 * Creates a new pulsating-circle background.
	 * 
	 * @param firstColor the first color of circles
	 * @param secondColor the second color of circles
	 * @param minDiameter the minimum diameter
	 * @param maxDiameter the maximum diameter
	 * @param numberOfCircles the number of circles
	 * @param step the number of pixels which should be added in each round to each circle
	 */
	public PulsatingCirclesBackground( int firstColor, 
			int secondColor, 
			int minDiameter, 
			int maxDiameter,
			int numberOfCircles,
			int step) {
		super();
		this.startColor = firstColor;
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.preciseMinDiameter = minDiameter * 100;
		this.preciseMaxDiameter = maxDiameter * 100;
		this.diameters = new short[ numberOfCircles ];
		this.preciseDiameters = new int[ numberOfCircles ];
		int difference = ((maxDiameter - minDiameter) * 100) / numberOfCircles;
		while (numberOfCircles > 0) {
			int diameter = difference * numberOfCircles;
			numberOfCircles--;
			this.diameters[ numberOfCircles ] = (short) (diameter/100);
			this.preciseDiameters[ numberOfCircles ] = diameter;
		}
		this.diameterGrowth = step;
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
		int centerX = x + width / 2;
		int centerY = y + height / 2;
		int color = this.startColor;
		int i = this.diameters.length - 1;
		while (i >= 0) {
			int diameter = this.diameters[i];
			int offset = diameter/2;
			g.setColor( color );
			g.fillArc( centerX - offset, centerY - offset, diameter, diameter, 0, 360 );
			if (color == this.firstColor) {
				color = this.secondColor;
			} else {
				color = this.firstColor;
			}
			i--;
		}
	}

	/**
	 * Animates this background.
	 * 
	 * @return always true, since a redraw is needed after each animation.
	 */
	public boolean animate() {
		int i = this.preciseDiameters.length - 1;
		int diameter = this.preciseDiameters[ i ] + this.diameterGrowth;
		if (diameter > this.preciseMaxDiameter) {
			while ( i > 0 ) {
				diameter = this.preciseDiameters[ i -1 ] + this.diameterGrowth;
				this.preciseDiameters[i] = diameter;
				this.diameters[i] = (short)(diameter / 100);
				i--;
 			}
			this.preciseDiameters[0] = this.preciseMinDiameter;
			this.diameters[0] = (short)(this.preciseMinDiameter/100);
			if (this.startColor == this.firstColor) {
				this.startColor = this.secondColor;
			} else {
				this.startColor = this.firstColor;
			}			
		} else {
			while ( i >= 0 ) {
				diameter = this.preciseDiameters[ i ] + this.diameterGrowth;
				this.preciseDiameters[i] = diameter;
				this.diameters[i] = (short)(diameter / 100);
				i--;
 			}
		}
		return true;
	}
}
