//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:52:57.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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


import javax.microedition.lcdui.Displayable;

/**
 * <p>Is used to animate Screens, Backgrounds and Items.</p>
 * <p>
 * 	You can specify the animation interval in milliseconds in the variables section of
 *  your build.xml script.
 *  Example:
 *  <pre>
 *  <variables>
 *		<variable name="polish.animationInterval" value="200" />
 *	</variables>
 * 	</pre> 
 *  sets the interval to 200 ms. When not specified, the default interval
 *  of 100 ms will be used. 
 * </p>
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AnimationThread extends Thread
{
	
	//#ifdef polish.animationInterval:defined
		//#= public final static int ANIMATION_INTERVAL = ${polish.animationInterval};
	//#else
		public final static int ANIMATION_INTERVAL = 100;
	//#endif
		//#ifdef polish.sleepInterval:defined
		//#= private final static int SLEEP_INTERVAL = ${polish.sleepInterval};
	//#else
		private final static int SLEEP_INTERVAL = 300;
	//#endif
	protected static boolean releaseResourcesOnScreenChange;
	
	/**
	 * Creates a new animation thread.
	 */
	public AnimationThread() {
		//#if polish.cldc1.1 && polish.debug.error
			super("AnimationThread");
		//#else
			//# super();
		//#endif
		
		//#if polish.blackberry and false
			// # Application app = Application.getApplication();
			// # app.addKeyListener( this );
			// # app.addTrackwheelListener( this );
		//#endif
	}
	
	/**
	 * Animates the current screen.
	 */
	public void run() {
		long sleeptime = ANIMATION_INTERVAL;
//		int animationCounter = 0;
		while ( true ) {
			try {
				Thread.sleep(sleeptime);
				Screen screen = StyleSheet.currentScreen;
				if (screen != null ) {
					if (screen.animate()) {
						//System.out.println("AnimationThread: screen needs repainting");
						//#if polish.Bugs.displaySetCurrentFlickers
							if ( MasterCanvas.instance != null ) {
								MasterCanvas.instance.repaint();
							}
						//#else
							screen.repaint();					
						//#endif
						
						sleeptime = ANIMATION_INTERVAL;
//						animationCounter = 0;
//					} else {
//						animationCounter++;
//						if (animationCounter > 10) {
//							sleeptime = SLEEP_INTERVAL;
//							animationCounter = 0;
//						}
					}
					if (releaseResourcesOnScreenChange) {
						Displayable d = StyleSheet.display.getCurrent();
						if (d != screen) {
							StyleSheet.currentScreen = null;
						}
					}
				} else {
					if (releaseResourcesOnScreenChange) {
						StyleSheet.releaseResources();
						releaseResourcesOnScreenChange = false;
					}
					sleeptime = SLEEP_INTERVAL;
				}
			} catch (InterruptedException e) {
				// ignore
			} catch (Throwable e) {
				//#debug error
				System.out.println("unable to animate screen" + e );
				e.printStackTrace();
			}
		}
	}

}
