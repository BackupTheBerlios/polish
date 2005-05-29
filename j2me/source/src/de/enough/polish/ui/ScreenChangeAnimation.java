//#condition polish.usePolishGui

/*
 * Created on 27-May-2005 at 17:14:01.
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
package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Animates two screens for a nice effect.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ScreenChangeAnimation
//#if polish.midp2
	extends Canvas
//#elif polish.classes.fullscreen:defined
	//#= extends ${polish.classes.fullscreen}
//#else
	//#= extends Canvas 
//#endif
implements Runnable
{
	protected Display display;
	protected Screen nextScreen;
	protected Image lastScreenImage;
	protected Image nextScreenImage;
	protected int screenWidth;
	protected int screenHeight;
	//#if polish.Bugs.fullScreenInPaint
		protected boolean fullScreenModeSet;
	//#endif

	public ScreenChangeAnimation() {
		// default constructor
		//#if polish.midp2 && !polish.Bugs.fullScreenInPaint
			setFullScreenMode(true);
		//#endif
	}
	
	protected void show( Style style, Display dsplay, final int width, final int height, Image lstScreenImage, Image nxtScreenImage, Screen nxtScreen ) {
		this.screenWidth = width;
		this.screenHeight = height;
		this.display = dsplay;
		this.nextScreen = nxtScreen;
		this.lastScreenImage = lstScreenImage;
		/*
		this.lastScreenRgb = new int[ width * height ];
		lstScreenImage.getRGB( this.lastScreenRgb, 0, width, 0, 0, width, height );
		*/
		this.nextScreenImage = nxtScreenImage;
		/*
		this.nextScreenRgb = new int[ width * height ];
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		*/
		dsplay.setCurrent( this );
		//nxtScreen.showNotify();
		//Thread thread = new Thread( this );
		//thread.start();
	}
	
	protected abstract boolean animate();


	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed( int keyCode ) {
		this.nextScreen.keyPressed( keyCode );
		Graphics g = this.nextScreenImage.getGraphics();
		this.nextScreen.paint( g );
		//this.nextScreenImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (animate()) {
			repaint();
		} else {
			//#debug
			System.out.println("ScreenChangeAnimation: setting next screen");
			this.display.setCurrent( this.nextScreen );
		}
	}

}
