//#condition polish.midp || polish.usePolishGui

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

//import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;


/**
 * <p>Is used for only displaying a single canvas for devices that flicker between screen changes.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2008</p>
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
	/** the master canvas that actually displays the canvas that should be shown */
	public static MasterCanvas instance;
	protected AccessibleCanvas currentCanvas;
	protected Displayable currentDisplayable;
	//#if tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInPaint
		//#define tmp.fullScreenInPaint
		private boolean isInFullScreenMode;
	//#endif
	
	
	private MasterCanvas() {
	}
	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	protected void hideNotify() {
		if (this.currentCanvas != null) { 
			this.currentCanvas.hideNotify();
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	protected void showNotify() {
		//#if polish.midp2 && tmp.fullScreen
			setFullScreenMode( true );
		//#endif
		if (this.currentCanvas != null) { 
			this.currentCanvas.showNotify();
		}
	}


	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyPressed(int)
	 */
	protected void keyPressed(int keyCode) {
		if (this.currentCanvas != null) { 
			this.currentCanvas.keyPressed( keyCode );
		}
	}
	
	//#if polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int,int)
	 */
	protected void pointerPressed(int x, int y) {
		if (this.currentCanvas != null) { 
			this.currentCanvas.pointerPressed( x, y );
		}
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyRepeated(int)
	 */
	protected void keyRepeated(int keyCode) {
		if (this.currentCanvas != null) { 
			this.currentCanvas.keyRepeated( keyCode );
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#keyReleased(int)
	 */
	protected void keyReleased(int keyCode) {
		this.currentCanvas.keyReleased( keyCode );
	}

	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts
	protected void sizeChanged(int width, int height) {
		if (this.currentCanvas != null) { 
			this.currentCanvas.sizeChanged( width, height );
		}
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
		if (this.currentCanvas != null) {	
			this.currentCanvas.paint( g );
		}
	}
	
	/**
	 * Sets the current screen/displayable.
	 * 
	 * @param display the display
	 * @param nextDisplayable the screen/displayable that should be shown
	 */
	public static void setCurrent( Display display, Displayable nextDisplayable ) {
		//#debug
		System.out.println("MasterCanvas: setCurrent " + nextDisplayable  + ", on display " + display );
		if (nextDisplayable == null) {
			display.setCurrent( null );
			return;
		}
		if (instance != null && instance.currentCanvas == nextDisplayable) {
			instance.repaint();
			return;
		}
		//if ( nextDisplayable == instance ) {
		//	display.setCurrent( nextDisplayable );
		//	return;
		//}
		if ( ! (nextDisplayable instanceof AccessibleCanvas) ) {
			if (instance != null && instance.currentCanvas != null) {
				if (instance.currentDisplayable != nextDisplayable ) {
					//#debug
					System.out.println("MasterCanvas: setting instance.currentCanvas to NULL!!!");
					instance.currentCanvas.hideNotify();
					instance.currentCanvas = null;
					instance.currentDisplayable = null;
				}
			}
			//#debug
			System.out.println("MasterCanvas: setting native next displayable " + nextDisplayable );
			display.setCurrent( nextDisplayable );
			return;
		}
		AccessibleCanvas oldCanvas = null;
		if ( instance == null ) {
			instance = new MasterCanvas();
		} else {
			oldCanvas = instance.currentCanvas;
		}
					
		AccessibleCanvas canvas = ( (AccessibleCanvas) nextDisplayable );
		//#if polish.usePolishGui
			if (nextDisplayable instanceof Alert && instance.currentDisplayable != nextDisplayable) {
				Alert alert = (Alert)nextDisplayable;
				if (alert.nextDisplayable == null) {
					alert.nextDisplayable = (instance.currentDisplayable != null ? instance.currentDisplayable : display.getCurrent() );
				}
			}
		//#endif
		canvas.showNotify();
		instance.currentCanvas = canvas;
		instance.currentDisplayable = nextDisplayable;
//		if (nextDisplayable instanceof Alert) {
//			System.out.println("MasterCanvas: setting Alert " + nextDisplayable + " as next screen!");
//		}
		
		if ( oldCanvas != null ) {
			oldCanvas.hideNotify();
		}

		//#if polish.css.repaint-previous-screen
			// de-register an old remaining J2ME Polish screen for situations, in
			// which an AccessibleCanvas which is not a Screen is shown next.
			// Otherwise the AnimationThread will continue to animate the old
			// screen.
			if (StyleSheet.currentScreen != nextDisplayable &&  (!(nextDisplayable instanceof Screen))  ) {
				StyleSheet.currentScreen = null;
			}
		//#endif
		if ( !instance.isShown() ) {
			display.setCurrent( instance );
		} else {
			instance.repaint();
		}
	}
	
	// not needed anymore, since display.setCurrent( Alert, Displayable ) is now mapped
	// during the preprocessing phase.
//	public static void setCurrent( Display display, Alert alert, Displayable nextDisplayable ) {
//		//#debug
//		System.out.println("MasterCanvas: setCurrent of " + nextDisplayable.getClass().getName() );
//		//if ( nextDisplayable == instance ) {
//		//	display.setCurrent( nextDisplayable );
//		//	return;
//		//}
//		alert.setNextDisplayable(display, nextDisplayable);
//		setCurrent( display, alert );
////		if ( ! (nextDisplayable instanceof AccessibleCanvas) ) {
////			if (instance != null && instance.currentCanvas != null) {
////				if (instance.currentDisplayable != nextDisplayable ) {
////					//#debug
////					System.out.println("MasterCanvas: setting instance.currentCanvas to NULL!!!");
////					instance.currentCanvas.hideNotify();
////					instance.currentCanvas = null;
////					instance.currentDisplayable = null;
////				}
////			}
////			alert.setNextDisplayable(display, nextDisplayable);
////			display.setCurrent( alert );
////			display.setCurrent( alert, nextDisplayable );
////			return;
////		}
////		if ( instance == null ) {
////			instance = new MasterCanvas();
////		} else if ( instance.currentCanvas != null ) {
////			instance.currentCanvas.hideNotify();
////		}
////		AccessibleCanvas canvas = ( (AccessibleCanvas) nextDisplayable );
////		instance.currentCanvas = canvas;
////		instance.currentDisplayable = nextDisplayable;
////		canvas.showNotify();
////		display.setCurrent( alert, instance );
//	}
	
	public static Displayable getCurrent( Display display ) {
		//#debug
		System.out.println("MasterCanvas: getCurrent");
		if (instance != null) {
			return instance.currentDisplayable;
		} else {
			return display.getCurrent();
		}
	}
	
	public static void repaintAccessibleCanvas( AccessibleCanvas canvas ) {
		if (canvas == null) {
			//#debug warn
			System.out.println("MasterCanvas: repaintAccessibleCanvas got [null] canvas." );
			return;
		}
		//#debug
		System.out.println("MasterCanvas: repaintAccessibleCanvas");
		if ( instance != null ) {
			instance.repaint();
		} else {
			((Canvas) canvas).repaint(); 
		}
	}

	public static void repaintCanvas( Canvas canvas ) {
		if (canvas == null) {
			//#debug warn
			System.out.println("MasterCanvas: repaintCanvas got [null] canvas." );
			return;
		}
		//#debug
		System.out.println("MasterCanvas: repaintCanvas " + canvas + ", MasterCanvas.instance != null: " + ( instance != null) + ", instance == canvas: " +  ( instance == null) );
		if ( !(canvas instanceof AccessibleCanvas) ) {
			canvas.repaint();
		} else if ( instance != null ) {
			instance.repaint();
		} else {
			//System.out.println("native canvas repaint");
			canvas.repaint();
		}
	}
	
	public static void repaintCanvas( Canvas canvas, int x, int y, int width, int height ) {
		if (canvas == null) {
			//#debug warn
			System.out.println("MasterCanvas: repaintCanvas got [null] canvas." );
			return;
		}
		//#debug
		System.out.println("MasterCanvas: repaintCanvas " + canvas + ", MasterCanvas.instance != null: " + ( instance != null) + ", instance == canvas: " +  ( instance == null) );
		if ( !(canvas instanceof AccessibleCanvas) ) {
			canvas.repaint( x, y, width, height );
		} else if ( instance != null ) {
			instance.repaint( x, y, width, height );
		} else {
			//System.out.println("native canvas repaint");
			canvas.repaint( x, y, width, height );
		}
	}
	
	public static boolean isAccessibleCanvasShown(AccessibleCanvas canvas) {
		//#debug
		System.out.println("MasterCanvas: isAccessibleCanvasShown");
		if ( instance != null ) {
			return (canvas == instance.currentCanvas);
		} else {
			return ((Canvas)canvas).isShown();
		}
		
	}

	public static boolean isCanvasShown(Canvas canvas) {
		//#debug
		System.out.println("MasterCanvas: isCanvasShown");
		if ( instance != null && instance.isShown() ) {
			return (canvas == instance.currentDisplayable);
		} else {
			return canvas.isShown();
		}
	}

	public static boolean isDisplayableShown(Displayable displayable) {
		//#debug
		System.out.println("MasterCanvas: isDisplayableShown");
		if ( instance != null && instance.isShown() ) {
			return (displayable == instance.currentDisplayable);
		} else {
			return displayable.isShown();
		}
	}

}
