//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 27-May-2005 at 18:54:36.
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies the new screen.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScaleScreenChangeAnimation extends ScreenChangeAnimation {
	private int scaleFactor = 200;
	private int steps = 6;
	private int currentStep;
	private int[] nextScreenRgb;
	private boolean scaleDown;
	private int scaleWidth;
	private int scaleHeight;
	private int[] scaledScreenRgb;

	/**
	 * Creates a new animation 
	 */
	public ScaleScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, Screen nxtScreen) 
	{
		this.nextScreenRgb = new int[ width * height ];
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		this.scaledScreenRgb = this.nextScreenRgb;
		this.scaleWidth = width;
		this.scaleHeight = height;
		super.show(style, dsplay, width, height, lstScreenImage,
				nxtScreenImage, nxtScreen);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		int step = this.currentStep;
		if (this.scaleDown) {
			step--;
			if (step <= 0) {
				return false;
			}
		} else {
			step++;
			if (step > this.steps) {
				this.scaleDown = true;
				return true;
			}
		}
		this.currentStep = step;
		this.scaleWidth = this.screenWidth + ((this.screenWidth * this.scaleFactor * step) / (this.steps * 100));
		this.scaleHeight = this.screenHeight + ((this.screenHeight * this.scaleFactor * step) / (this.steps * 100));
		this.scaledScreenRgb = ImageUtil.scale(this.scaleWidth, this.scaleHeight, this.screenWidth, 
				this.screenWidth, this.screenHeight, this.nextScreenRgb);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		//#if polish.Bugs.fullScreenInPaint
			if (! this.fullScreenModeSet ) {
				setFullScreenMode(true);
				this.fullScreenModeSet = true;
			}
		//#endif
		/*
		int x = (this.screenWidth - this.scaleWidth) / 2;
		int y = (this.screenHeight - this.scaleHeight) / 2;
		if (x > 0) {
			g.setColor( 0xFFFFFF );
			g.fillRect( 0, 0, this.screenWidth, this.screenHeight );
		}
		g.drawRGB(this.scaledScreenRgb, 0, this.scaleWidth, x, y, this.scaleWidth, this.scaleHeight, false );
		*/
		g.drawRGB(this.scaledScreenRgb, 0, this.scaleWidth, 0, 0, this.scaleWidth, this.scaleHeight, false );
		
		this.display.callSerially( this );
	}

}
