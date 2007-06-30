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
 * <p>Paints a transition two screens for a nice effect.</p>
 * <p>Using a screen change animation is easy:
 *    <br />
 *    Use the <code>screen-change-animation</code> CSS attribute for specifying which
 *    animation you would like to have. You can also finetune some animations. Note
 *    that some animations have certain conditions like support of the MIDP 2.0 profile.
 * <pre>
 * screen-change-animation: left;
 * left-screen-change-animation-speed: 5;
 * </pre>
 * </p>
 * <p>You can easily implement your own screen change animations by following these
 *    steps:
 * </p>
 * <ol>
 * 	<li>Extend de.enough.polish.ui.ScreenChangeAnimation</li>
 *  <li>Implement the animate() method for doing the animation, use the fields lastCanvasImage 
 *      and nextCanvasImage for your manipulation</li>
 *  <li>Implement the paint() method and call &quot;this.display.callSerially( this );&quot; 
 *      at the end of the paint() method.</li>
 *  <li>Override the show() method if you need to get parameters from the style.</li>
 *  <li>In case you want to manipulate the RGB data, you also shoudl override the show() method 
 *      for getting the RGB values once. Consider to override the keyPressed(), keyReleased() and
 *      keyRepeated() methods as well. 
 *  </li>
 * </ol>
 * <p>You can now use your animation by specifying the <code>screen-change-animation</code> CSS attribute 
 *    accordingly:
 * <pre>
 * screen-change-animation: new com.company.ui.MyScreenChangeAnimation();
 * </pre>
 * </p>
 * <p>You can also ease the usage by registering your animation in ${polish.home}/custom-css-attributes:
 * <pre>
 * &lt;attribute name=&quot;screen-change-animation&quot;&gt;
 * 		&lt;mapping from=&quot;myanimation&quot; to=&quot;com.company.ui.MyScreenChangeAnimation()&quot; /&gt;
 * &lt;/attribute&gt;
 * </pre>
 * </p>
 * <p>Now your animation is easier to use:
 * <pre>
 * screen-change-animation: myanimation;
 * </pre>
 * </p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see #show(Style, Display, int, int, Image, Image, AccessibleCanvas, Displayable)
 * @see #animate()
 */
public abstract class ScreenChangeAnimation
//#if polish.midp2
	extends Canvas
//#elif polish.classes.fullscreen:defined
	//#= extends ${polish.classes.fullscreen}
//#else
	//#= extends Canvas 
//#endif
//#if polish.Bugs.displaySetCurrentFlickers && polish.useFullScreen
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

	/**
	 * Creates a new ScreenChangeAnimation.
	 * All subclasses need to implement the default constructor.
	 */
	public ScreenChangeAnimation() {
		// default constructor
	}
	
	/**
	 * Starts the animation.
	 * Please note that an animation can be re-used for several screens.
	 * 
	 * @param style the associated style.
	 * @param dsplay the display, which is used for setting this animation
	 * @param width the screen's width
	 * @param height the screen's height
	 * @param lstScreenImage an image of the last screen
	 * @param nxtScreenImage an image of the next screen
	 * @param nxtCanvas the next screen that should be displayed when this animation finishes (as an AccessibleCanvas)
	 * @param nxtDisplayable the next screen that should be displayed when this animation finishes (as a Displayable)
	 */
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
		//#if polish.Bugs.displaySetCurrentFlickers && polish.useFullScreen
			MasterCanvas.setCurrent( dsplay, this );
		//#else
			dsplay.setCurrent( this );
		//#endif
		
		//nxtScreen.showNotify();
		//Thread thread = new Thread( this );
		//thread.start();
	}
	
	/**
	 * Animates this animation.
	 * 
	 * @return true when the animation should continue, when false is returned the animation
	 *         will be stopped and the next screen will be shown instead.
	 */
	protected abstract boolean animate();
	
	public abstract void paint( Graphics g );

	
	
	//#if polish.hasPointerEvents
	/**
	 * Forwards pointer pressed events to the next screen.
	 * 
	 * @param x the horizontal coordinate of the clicked pixel
	 * @param y the vertical coordinate of the clicked pixel
	 */
	public void pointerPressed( int x, int y ) {
		AccessibleCanvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			next.pointerPressed( x, y );
			Graphics g = nextImage.getGraphics();
			next.paint( g );
		}
	}
	//#endif
	
	/**
	 * Notifies this animation that it will be shown shortly.
	 * The default implementation switches into fullscreen mode
	 */
	public void showNotify() {
		//#if polish.midp2 && !polish.Bugs.fullScreenInPaint
			setFullScreenMode(true);
		//#endif
	}
	
	/**
	 * Notifies this animation that it will be hidden shortly.
	 * This is ignored by the default implementation.
	 */
	public void hideNotify() {
		// ignore
	}
	
	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts 
	/**
	 * Notifies this animation that the screen space has been changed.
	 * This is ignored by the default implementation.
   * 
   * @param width the width
   * @param height the height
	 */
	public void sizeChanged( int width, int height ) {
		// ignore
	}
	//#endif

	/**
	 * Handles key repeat events.
	 * The default implementation forwards this event to the next screen
	 * and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 */
	public void keyRepeated( int keyCode ) {
		AccessibleCanvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			next.keyRepeated( keyCode );
			Graphics g = nextImage.getGraphics();
			next.paint( g );
		}
	}

	/**
	 * Handles key released events.
	 * The default implementation forwards this event to the next screen
	 * and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 */
	public void keyReleased( int keyCode ) {
		AccessibleCanvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			next.keyReleased( keyCode );
			Graphics g = nextImage.getGraphics();
			next.paint( g );
		}
	}

	/**
	 * Handles key pressed events.
	 * The default implementation forwards this event to the next screen
	 * and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 */
	public void keyPressed( int keyCode ) {
		AccessibleCanvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			next.keyPressed( keyCode );
			Graphics g = nextImage.getGraphics();
			next.paint( g );
		}
		//this.nextScreenImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	
	/**
	 * Runs this animation - subclasses need to ensure to call this.display.callSerially( this ); at the end of the paint method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (this.nextCanvas != null && animate()) {
			//#if polish.Bugs.displaySetCurrentFlickers && polish.useFullScreen
				MasterCanvas.instance.repaint();
			//#else
				repaint();
			//#endif
		} else if (this.nextCanvas != null) {
			//#debug
			System.out.println("ScreenChangeAnimation: setting next screen");
			this.lastCanvasImage = null;
			this.nextCanvasImage = null;
			this.nextCanvas = null;
			Display disp = this.display;
			this.display = null;
			Displayable next = this.nextDisplayable;
			this.nextDisplayable = null;
			System.gc();
			if (next != null) {
				//#if polish.Bugs.displaySetCurrentFlickers && polish.useFullScreen
					MasterCanvas.setCurrent( disp, next );
				//#else
					disp.setCurrent( next );
				//#endif
			}
		}
	}

}
