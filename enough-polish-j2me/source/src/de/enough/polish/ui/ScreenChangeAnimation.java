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
import javax.microedition.lcdui.Displayable;
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
//#if polish.Bugs.displaySetCurrentFlickers
	implements Runnable, AccessibleCanvas
//#else
	//# implements Runnable	
//#endif
{
	protected Display display;
	protected AccessibleCanvas nextCanvas;
	protected Image lastCanvasImage;
	protected Image nextCanvasImage;
	protected int screenWidth;
	protected int screenHeight;
	//#if polish.Bugs.fullScreenInPaint
		protected boolean fullScreenModeSet;
	//#endif
	protected Displayable nextDisplayable;

	public ScreenChangeAnimation() {
		// default constructor
		//#if polish.midp2 && !polish.Bugs.fullScreenInPaint
			setFullScreenMode(true);
		//#endif
	}
	
	protected void show( Style style, Display dsplay, final int width, final int height, Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable ) {
		this.screenWidth = width;
		this.screenHeight = height;
		this.display = dsplay;
		this.nextCanvas = nxtCanvas;
		this.nextDisplayable = nxtDisplayable;
		this.lastCanvasImage = lstScreenImage;
		/*
		this.lastScreenRgb = new int[ width * height ];
		lstScreenImage.getRGB( this.lastScreenRgb, 0, width, 0, 0, width, height );
		*/
		this.nextCanvasImage = nxtScreenImage;
		/*
		this.nextScreenRgb = new int[ width * height ];
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		*/
		//#if polish.Bugs.displaySetCurrentFlickers
			MasterCanvas.setCurrent( dsplay, this );
		//#else
			dsplay.setCurrent( this );
		//#endif
		
		//nxtScreen.showNotify();
		//Thread thread = new Thread( this );
		//thread.start();
	}
	
	
	protected abstract boolean animate();

	
	
	//#if polish.hasPointerEvents
	public void pointerPressed( int x, int y ) {
		this.nextCanvas.pointerPressed( x, y );
		Graphics g = this.nextCanvasImage.getGraphics();
		this.nextCanvas.paint( g );
	}
	//#endif
	
	public void showNotify() {
		// ignore
	}
	
	public void hideNotify() {
		// ignore
	}
	
	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts 
	public void sizeChanged( int width, int height ) {
		// ignore
	}
	//#endif

	public void keyRepeated( int keyCode ) {
		keyPressed( keyCode );
	}

	public void keyReleased( int keyCode ) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	public void keyPressed( int keyCode ) {
		this.nextCanvas.keyPressed( keyCode );
		Graphics g = this.nextCanvasImage.getGraphics();
		this.nextCanvas.paint( g );
		//this.nextScreenImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (animate()) {
			//#if polish.Bugs.displaySetCurrentFlickers
				MasterCanvas.instance.repaint();
			//#else
				repaint();
			//#endif
		} else {
			//#debug
			System.out.println("ScreenChangeAnimation: setting next screen");
			//#if polish.Bugs.displaySetCurrentFlickers
				MasterCanvas.setCurrent( this.display, this.nextDisplayable );
			//#else
				this.display.setCurrent( this.nextDisplayable );
			//#endif
		}
	}

}
