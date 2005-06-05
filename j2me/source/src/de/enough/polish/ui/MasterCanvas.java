/*
 * Created on 03-Jun-2005 at 18:18:19.
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


/**
 * <p>Is used for only displaying a single canvas for devices that flicker between screen changes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        03-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MasterCanvas
//#if polish.useFullScreen && (polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts)  && (!polish.useMenuFullScreen || polish.hasCommandKeyEvents)
	//#define tmp.fullScreen
	//# extends Canvas
//#elif polish.useFullScreen && polish.classes.fullscreen:defined
	//#define tmp.fullScreen
	//#= extends ${polish.classes.fullscreen}
//#else
	extends Canvas
//#endif
{
	
	public static MasterCanvas instance;
	protected AccessibleCanvas currentCanvas;
	protected Displayable currentDisplayable;
	//#if tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInPaint
		//#define tmp.fullScreenInPaint
		private boolean isInFullScreenMode;
	//#endif
	
	
	private MasterCanvas() {
		//#if polish.midp2
			setFullScreenMode( true );
		//#endif
	}
	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	protected void hideNotify() {
		this.currentCanvas.hideNotify();
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keyCode) {
		this.currentCanvas.keyPressed( keyCode );
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
	 */
	protected void keyRepeated(int keyCode) {
		this.currentCanvas.keyRepeated( keyCode );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyReleased(int)
	 */
	protected void keyReleased(int keyCode) {
		this.currentCanvas.keyReleased( keyCode );
	}

	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts
	protected void sizeChanged(int width, int height) {
		this.currentCanvas.sizeChanged( width, height );
	}
	//#endif
		
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		//#if tmp.fullScreenInPaint
			if (!this.isInFullScreenMode) {
				setFullScreenMode(true);
				this.isInFullScreenMode = true;
			}
		//#endif
		this.currentCanvas.paint( g );
	}
	
	public static void setCurrent( Display display, Displayable nextDisplayable ) {
		if ( ! (nextDisplayable instanceof AccessibleCanvas) ) {
			if (instance != null && instance.currentCanvas != null) {
				instance.currentCanvas.hideNotify();
				instance.currentCanvas = null;
				instance.currentDisplayable = null;
			}
			display.setCurrent( nextDisplayable );
			return;
		}
		if ( instance == null ) {
			instance = new MasterCanvas();
		}
		if ( instance.currentCanvas != null ) {
			instance.currentCanvas.hideNotify();
		}
		AccessibleCanvas canvas = ( (AccessibleCanvas) nextDisplayable );
		instance.currentCanvas = canvas;
		instance.currentDisplayable = nextDisplayable;
		canvas.showNotify();
		if ( !instance.isShown() ) {
			display.setCurrent( instance );
		} else {
			instance.repaint();
		}
	}

}
