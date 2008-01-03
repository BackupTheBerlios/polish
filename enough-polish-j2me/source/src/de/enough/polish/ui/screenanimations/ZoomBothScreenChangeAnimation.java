//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 30-May-2005 at 01:14:36.
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
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.AccessibleCanvas;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Magnifies both the last screen and the next screen.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: zoomBoth;
 * 			zoomBoth-screen-change-animation-factor: 300; (260 is default)
 * 			zoomBoth-screen-change-animation-steps: 4; (6 is default)
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2008</p>
 * <pre>
 * history
 *        30-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ZoomBothScreenChangeAnimation extends ScreenChangeAnimation {
	private int outerScaleFactor = 260;
	private int innerScaleFactor = 460;
	private int steps = 6;
	private int currentStep;
	private int[] nextScreenRgb;
	private int[] nextScreenScaledRgb;
	private int[] lastScreenRgb;
	private int[] lastScreenScaledRgb;

	/**
	 * Creates a new animation 
	 */
	public ZoomBothScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		int size = width * height;
		if ( this.lastScreenRgb == null ) {
			this.lastScreenRgb = new int[ size ];
			this.lastScreenScaledRgb = new int[ size ];
			this.nextScreenRgb = new int[ size ];
			this.nextScreenScaledRgb = new int[ size ];			
		}
		lstScreenImage.getRGB( this.lastScreenRgb, 0, width, 0, 0, width, height );
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		
		System.arraycopy( this.lastScreenRgb, 0, this.lastScreenScaledRgb, 0,  width * height );
		//ImageUtil.scale( 200, this.scaleFactor, this.screenWidth, this.screenHeight, this.lastScreenRgb, this.scaledScreenRgb);
		super.show(style, dsplay, width, height, lstScreenImage,
				nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		try {
			//Thread.sleep( 500 );
		} catch (Exception e) {}
		this.currentStep++;
		if (this.currentStep >= this.steps) {
			this.currentStep = 0;
			this.lastScreenRgb = null;
			this.lastScreenScaledRgb = null;
			this.nextScreenRgb = null;
			this.nextScreenScaledRgb = null;
			return false;
		}
		int magnifyFactor = 100 + (this.outerScaleFactor - 100) * this.currentStep / this.steps;
		ImageUtil.scale(magnifyFactor, this.screenWidth, this.screenHeight, this.lastScreenRgb, this.lastScreenScaledRgb);
		//magnifyFactor = (100 + (this.innerScaleFactor - 100) * this.currentStep / this.steps * 100 ) / this.innerScaleFactor;
		magnifyFactor = ((100 + (this.innerScaleFactor - 100) * this.currentStep / this.steps) * 100) / this.innerScaleFactor;
		int opacity = (magnifyFactor * 255) / 100;
		ImageUtil.scale(opacity, magnifyFactor, this.screenWidth, this.screenHeight, this.nextScreenRgb, this.nextScreenScaledRgb);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.lastScreenScaledRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
		g.drawRGB(this.nextScreenScaledRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, true );
	}

}
