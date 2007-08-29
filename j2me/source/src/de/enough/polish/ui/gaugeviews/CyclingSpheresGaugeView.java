//#condition polish.usePolishGui
/*
 * Created on Jan 31, 2007 at 3:01:30 PM.
 * 
 * Copyright (c) 2007 Andre Schmidt / Enough Software
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
package de.enough.polish.ui.gaugeviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Shows an animation of rotating arcs for visualizing an CONTINUOUS_RUNNING indefinite gauge.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 31, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CyclingSpheresGaugeView extends ItemView {
	int sphereColor = 0xFFFFFF;
	int sphereHighlightColor = 0x000000;
	
	int sphereCount = 8;
	int sphereHighlightCount = 3;
	
	int sphereHighlightIndex = 0;
	
	int sphereWidth = 10;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int lineWidth) {
		this.contentWidth = Math.max( lineWidth/4, 24 );
		this.contentHeight = this.contentWidth;
	}

	protected void setStyle(Style style) {
		super.setStyle(style);
		
		Color colorObj;
		Integer countObj;
		
		//#if polish.css.gauge-cycling-spheres-color
		colorObj = style.getColorProperty("gauge-cycling-spheres-color");
		if (colorObj != null) {
			this.sphereColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-spheres-count
		countObj = style.getIntProperty("gauge-cycling-spheres-count");
		if (countObj != null) {
			this.sphereCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-spheres-highlight-color
		colorObj = style.getColorProperty("gauge-cycling-spheres-highlight-color");
		if (colorObj != null) {
			this.sphereHighlightColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-spheres-highlight-count
		countObj = style.getIntProperty("gauge-cycling-spheres-highlight-count");
		if (countObj != null) {
			this.sphereHighlightCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-cycling-spheres-width
		countObj = style.getIntProperty("gauge-cycling-spheres-width");
		if (countObj != null) {
			this.sphereWidth= countObj.intValue();
		}
		//#endif
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	public boolean animate() {
		super.animate();
		
		this.sphereHighlightIndex++;
		this.sphereHighlightIndex = this.sphereHighlightIndex % this.sphereCount;
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		//#if !polish.hasFloatingPoint
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
		//#else
			int centerX 	= (this.contentWidth - this.sphereWidth) / 2  + x;
			int centerY 	= (this.contentWidth - this.sphereWidth) / 2  + y;
			double alpha 	= 0;
			double radius 	= (this.contentWidth - this.sphereWidth)/2d;
			int sphereX 	= 0;
			int sphereY 	= 0;
			
			for(int i=0; i < this.sphereCount; i++)
			{
				setSphereColor(g, i);
				
				alpha = (360d / this.sphereCount) * i;
				alpha = (alpha / 180) * Math.PI;
				
				sphereX = (int)(centerX + radius * Math.cos(alpha));
				sphereY = (int)(centerY - radius * Math.sin(alpha));
				
				g.fillArc( sphereX, sphereY, this.sphereWidth, this.sphereWidth, 0, 360);
			}
		//#endif
	}
	
	private void setSphereColor(Graphics g, int i)
	{
		int startIndex = this.sphereHighlightIndex;
		int endIndex = ((this.sphereHighlightIndex + this.sphereHighlightCount) % this.sphereCount) - 1;
		
		if(startIndex < endIndex)
		{
			if(i >= startIndex && i <= endIndex)
			{
				g.setColor( this.sphereHighlightColor );
				return;
			}
		}
		else
		{
			if(i >= startIndex || i <= endIndex)
			{
				g.setColor( this.sphereHighlightColor );
				return;
			}			
		}
		
		g.setColor( this.sphereColor );
	}
}
