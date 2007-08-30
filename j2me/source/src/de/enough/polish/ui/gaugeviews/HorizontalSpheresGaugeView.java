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
import de.enough.polish.ui.Gauge;
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
public class HorizontalSpheresGaugeView extends ItemView {
	int sphereCount = 8;
	int sphereColor = 0xFFFFFF;
	
	int sphereHighlightCount = 3;
	int sphereHighlightColor = 0xAAAAAA;
	int sphereHighlightCenterColor = 0x000000;
	
	int sphereHighlightIndex = 0;
	
	int sphereHighlightCenterIndex = -1;
	int sphereHighlightCenterSpan = -1;
	
	int sphereWidth = 10;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int lineWidth) {
		this.contentWidth = lineWidth;
		this.contentHeight = this.sphereWidth;
	}

	protected void setStyle(Style style) {
		super.setStyle(style);
		
		Color colorObj;
		Integer countObj;
		
		//#if polish.css.gauge-horizontal-spheres-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-color");
		if (colorObj != null) {
			this.sphereColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-count
		countObj = style.getIntProperty("gauge-horizontal-spheres-count");
		if (countObj != null) {
			this.sphereCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-highlight-color");
		if (colorObj != null) {
			this.sphereHighlightColor = colorObj.getColor();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-count
		countObj = style.getIntProperty("gauge-horizontal-spheres-highlight-count");
		if (countObj != null) {
			this.sphereHighlightCount = countObj.intValue();
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-highlight-center-color
		colorObj = style.getColorProperty("gauge-horizontal-spheres-highlight-center-color");
		if (colorObj != null) {
			this.sphereHighlightCenterColor = colorObj.getColor();
			
			if(this.sphereHighlightCount > 2)
			{
				this.sphereHighlightCenterIndex = 1;
				this.sphereHighlightCenterSpan = this.sphereHighlightCount - 2;
			}
		}
		//#endif
		
		//#if polish.css.gauge-horizontal-spheres-width
		countObj = style.getIntProperty("gauge-horizontal-spheres-width");
		if (countObj != null) {
			this.sphereWidth = countObj.intValue();
			this.contentHeight = this.sphereWidth;
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
		int stepX = this.contentWidth / this.sphereCount; 
		int offsetX = 0; 
			
		for(int i=0; i < this.sphereCount; i++)
		{
			setSphereColor(g, i);
			
			offsetX = stepX * i;
			
			g.fillArc( x + offsetX, y, this.sphereWidth, this.sphereWidth, 0, 360);
		}
	}
	
	private void setSphereColor(Graphics g, int i)
	{
		int startIndex = this.sphereHighlightIndex;
		int endIndex = ((this.sphereHighlightIndex + this.sphereHighlightCount - 1) % this.sphereCount);
				
		if(startIndex <= endIndex)
		{
			if(i >= startIndex && i <= endIndex)
			{
				if(this.sphereHighlightCenterIndex != -1)
				{
					if(setCenterSphereColor(startIndex, g, i))
						return;
				}
				
				g.setColor( this.sphereHighlightColor );
				return;
			}
		}
		else
		{
			if(i >= startIndex || i <= endIndex)
			{
				if(this.sphereHighlightCenterIndex != -1)
				{
					if(setCenterSphereColor(startIndex, g, i))
						return;
				}
				
				g.setColor( this.sphereHighlightColor );
				return;
			}			
		}
		
		g.setColor( this.sphereColor );
	}
	
	public boolean setCenterSphereColor(int startIndex, Graphics g, int i)
	{
		int centerStartIndex = (startIndex + this.sphereHighlightCenterIndex) % this.sphereCount;
		int centerEndIndex = (startIndex + this.sphereHighlightCenterIndex + (this.sphereHighlightCenterSpan - 1)) % this.sphereCount;
				
		if(centerStartIndex <= centerEndIndex)
		{
			if(i >= centerStartIndex && i <= centerEndIndex)
			{
				g.setColor( this.sphereHighlightCenterColor  );
				return true;
			}
		}
		else
		{
			if(i >= centerStartIndex || i <= centerEndIndex)
			{
				g.setColor( this.sphereHighlightCenterColor );
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
	
}
