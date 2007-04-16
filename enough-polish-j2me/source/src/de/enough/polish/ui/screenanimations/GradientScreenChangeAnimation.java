//#condition polish.usePolishGui and polish.midp2
/*
 * Created on 24.08.2005 at 15:37:25.
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
import de.enough.polish.util.DrawUtil;

/**
 * <p>Fades the current screen's colors the the ones of the target screen. Quite similar to the fade animation.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		//#if polish.midp2
 * 			screen-change-animation: gradient;
 * 		//#endif
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GradientScreenChangeAnimation 
extends ScreenChangeAnimation 
{
	
	private int[] lastScreenRgb;
	private int[] nextScreenRgb;
	private int[] currentScreenRgb;
	private int steps = 5;
	private int currentStep;

	/**
	 * Creates a new animation.
	 */
	public GradientScreenChangeAnimation() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		if ( this.nextScreenRgb == null ) {
			this.nextScreenRgb = new int[ width * height ];
			this.currentScreenRgb = new int[ width * height ];
			this.lastScreenRgb = new int[ width * height ];
		}
		lstScreenImage.getRGB( this.lastScreenRgb, 0, width, 0, 0, width, height );
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		System.arraycopy( this.lastScreenRgb, 0, this.currentScreenRgb, 0, width * height);
		this.currentStep = 0;
		super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	
	
	public void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
		this.nextCanvasImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}

	//#if polish.hasPointerEvents
	public void pointerPressed(int x, int y) {
		super.pointerPressed(x, y);
		this.nextCanvasImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		if (this.currentStep < this.steps) {
			int lastColor;
			int targetColor;
			int permille = 1000 * this.currentStep / this.steps;
			for (int i = 0; i < this.currentScreenRgb.length; i++) {
				lastColor = this.lastScreenRgb[i];
				targetColor = this.nextScreenRgb[i];
				if (lastColor != targetColor) {
					this.currentScreenRgb[i] = DrawUtil.getGradientColor( lastColor, targetColor, permille );
				}
			}
			this.currentStep++;
			return true;
		} else {
			this.lastScreenRgb = null;
			this.currentScreenRgb = null;
			this.nextScreenRgb = null;
			this.currentStep = 0;
			return false;
		}
	
	}

	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		g.drawRGB(this.currentScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );		
		this.display.callSerially( this );
	}

}