//#condition polish.usePolishGui

/*
 * Created on 27-May-2005 at 18:54:36.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Moves the new screen like two snapping doors from top and bottom that meet in the middle.</p>
 *
 * <p>Copyright (c) Enough Software 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VerticalDoorsScreenChangeAnimation extends ScreenChangeAnimation
{	
	private int currentY;
	private int speed = -1;

	/**
	 * Creates a new animation 
	 */
	public VerticalDoorsScreenChangeAnimation()
	{
		// Do nothing here.
	}

	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style)
	{
		super.setStyle(style);
		this.currentY = this.screenHeight / 2;

		//#if polish.css.vertical-doors-screen-change-animation-speed
			Integer speedInt = style.getIntProperty("vertical-doors-change-animation-speed");
			if (speedInt != null)
			{
				this.speed = speedInt.intValue();
			} else {
				this.speed = -1;
			}
		//#endif
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate()
	{
		int adjust;
		//#if polish.css.vertical-doors-screen-change-animation-speed
			if (this.speed != -1) {
				adjust = this.speed;
			} else {
		//#endif
				adjust = this.currentY / 3;
				if (adjust < 2) {
					adjust = 2;
				}
		//#if polish.css.vertical-doors-screen-change-animation-speed
			}
		//#endif
		if (this.currentY > 0)
		{
			this.currentY -= adjust;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g)
	{
		Image first;
		Image second;
		int height;
		if (this.isForwardAnimation) {
			first = this.lastCanvasImage;
			second = this.nextCanvasImage;
			height = (this.screenHeight/2) - this.currentY;
		} else {
			first = this.nextCanvasImage;
			second = this.lastCanvasImage;
			height = this.currentY;
		}

		g.drawImage(first, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.setClip(0, 0, this.screenWidth, height );
		g.drawImage(second, 0, height - (this.screenHeight/2), Graphics.TOP | Graphics.LEFT);
		g.setClip(0, this.screenHeight-height, this.screenWidth, height );
		g.drawImage(second, 0, (this.screenHeight/2) - height , Graphics.TOP | Graphics.LEFT);
	}
}
