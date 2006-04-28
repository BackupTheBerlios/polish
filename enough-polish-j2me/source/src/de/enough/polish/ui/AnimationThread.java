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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;


//#if polish.blackberry
	//# import net.rim.device.api.ui.Keypad;
	//# import net.rim.device.api.system.Application;
	//# import net.rim.device.api.system.KeyListener;
	//# import net.rim.device.api.system.TrackwheelListener;
//#endif

/**
 * <p>Is used to animate Backgrounds, Borders and Items.</p>
 * <p>
 * 	In either devices.xml, vendors.xml, groups.xml, device.xml or settings.xml
 *  the animation-interval can be specified.
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
//#if polish.blackberry
	// # implements KeyListener, TrackwheelListener
//#endif

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
	
	//#if polish.blackberry and false
	public boolean keyChar(char key, int status, int time) {
		Screen screen = StyleSheet.currentScreen;		
		if (screen == null || !screen.isShown()) {
			return false;
		}
		//#debug info
		System.out.println("keyChar");
		//#if (polish.TextField.useDirectInput == true)|| polish.css.textfield-direct-input
			Item item = screen.getCurrentItem();
			if (item instanceof TextField) {
				TextField field = (TextField) item;
				field.caretChar = key;
				field.insertCharacter();
				return true;
			}
		//#endif
		return false;
	}

	public boolean keyDown(int keyCode, int time) {
		Screen screen = StyleSheet.currentScreen;		
		if (screen == null || !screen.isShown() ) {
			return false;
		}
		//#debug info
		System.out.println("keyDown");
		//#if (polish.TextField.useDirectInput == true)|| polish.css.textfield-direct-input
			//#if polish.key.MoveLeftKey:defined
				//#= if ( keyCode == ${polish.key.MoveLeftKey} ) {
					screen.handleKeyPressed(keyCode,  Canvas.LEFT );
					//# return screen.keyPressedProcessed;
				//# }
			//#endif
			//#if polish.key.MoveRightKey:defined
				//#= if ( keyCode == ${polish.key.MoveRightKey} ) { 
					screen.handleKeyPressed(keyCode,  Canvas.RIGHT );
					//# return screen.keyPressedProcessed;
				//# }
			//#endif
			//#if polish.key.ReturnKey:defined
				//#= if ( keyCode == ${polish.key.ReturnKey} ) { 
					screen.keyPressed( keyCode );
					//# return screen.keyPressedProcessed;
				//# }
			//#endif
			//#if polish.key.ClearKey:defined
				//#= if ( keyCode != ${polish.key.ClearKey} ) { 
			//#endif
			//#if polish.key.DeleteKey:defined
				//#= if ( keyCode != ${polish.key.DeleteKey} ) { 
			//#endif
			Item item = screen.getCurrentItem();
			if (item instanceof TextField) {
				//#debug info
				System.out.println("keyDown aborted (TextField)");
				TextField field = (TextField) item;
				//#if false
					char c = 'A';
				//#else
					//# char c = Keypad.map( keyCode );
				//#endif
				if (field.inputMode == TextField.MODE_NUMBERS ) {
					if ( !Character.isDigit(c) ) {
						//# c = Keypad.getAltedChar( c );
						if ( !Character.isDigit(c)) {
							return false;
						}
					}
					//#debug info
					System.out.println("Adding numeric char [" + c + "]");
				}
				field.lastInputTime = time;
				synchronized ( field.lock ) {
					field.caretChar = c;
					field.insertCharacter();
				}
				return true;
			}
			//#if polish.key.DeleteKey:defined
				//# } 
			//#endif
			//#if polish.key.ClearKey:defined
				//# } 
			//#endif
		//#endif
		//screen.lastKeyCode = keyCode;
		screen.keyPressed( keyCode );
		return screen.keyPressedProcessed;
	}

	public boolean keyRepeat(int keyCode, int time) {
		return keyDown( keyCode, time );
	}

	public boolean keyStatus(int keyCode, int time) {
		// ignore
		return false;
	}

	public boolean keyUp(int keyCode, int time) {
		// ignore
		return false;
	}

	public boolean trackwheelClick(int status, int time) {
		Screen screen = StyleSheet.currentScreen;		
		if (screen == null || !screen.isShown()) {
			return false;
		}
		// invoke the menu
		//#ifdef polish.key.LeftSoftKey:defined
			//#= int key = ${polish.key.LeftSoftKey};
		//#else
			int key = -6;
		//#endif
		screen.keyPressed( key );
		return screen.keyPressedProcessed;
	}

	public boolean trackwheelRoll(int amount, int status, int time) {
		Screen screen = StyleSheet.currentScreen;		
		if (screen == null || !screen.isShown()) {
			return false;
		}
		if (amount < 0) {
			screen.keyPressed( screen.getKeyCode( Canvas.UP ) );
		} else {
			screen.keyPressed( screen.getKeyCode( Canvas.DOWN ) );
		}
		return screen.keyPressedProcessed;
	}

	public boolean trackwheelUnclick(int status, int time) {
		// TODO enough implement trackwheelUnclick
		return false;
	}
	//#endif

}
