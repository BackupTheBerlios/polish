/*
 * Created on 26-June-2007 at 16:14:27.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.example;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.List;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

//#debug ovidiu
import de.enough.polish.benchmark.Benchmark;

import de.enough.polish.util.DeviceControl;
import de.enough.polish.util.Locale; 

//#ifdef polish.debugEnabled
import de.enough.polish.util.Debug;
//#endif
	
/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2007</p>

 * <pre>
 * history
 *        26-June-2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MenuMidlet extends MIDlet implements CommandListener {
	
	//#debug ovidiu
	//public static Timer myTimer = new SmartTimer(50);
	
	List menuScreen;
	Command startGameCmd = new Command( Locale.get( "cmd.StartGame" ), Command.ITEM, 8 );
	Command quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	Command showLogCmd = new Command( Locale.get("cmd.ShowLog"), Command.ITEM, 9 );

	Display display; 
	
	
	public MenuMidlet() {
		super(); 
		
		//#mdebug ovidiu
                System.out.println("OVIDIU");
		// Set the timer descriptions
		Benchmark.setSmartTimerDescription("0", "Time spent in item.paint()");
		Benchmark.setSmartTimerDescription("1", "No. of calls to item.paint()");
		Benchmark.setSmartTimerDescription("2", "Time spent in GaussianBlur.process()");
		Benchmark.setSmartTimerDescription("3", "No. of calls to GaussianBlur.process()");
		Benchmark.setSmartTimerDescription("4", "Time spent pixel crunching in GaussianBlur.process()");
		Benchmark.setSmartTimerDescription("5", "No. of calls to AnimationThread.run()");
		Benchmark.setSmartTimerDescription("6", "Time spent in AnimationThread.run()");
		Benchmark.setSmartTimerDescription("7", "Time spent in AnimationThread.run().animatable.animate()");
		Benchmark.setSmartTimerDescription("8", "No. of calls to AnimationThread.run().animatable.animate()");
		Benchmark.setSmartTimerDescription("9", "Time spent repainting stuff in AnimationThread.run()");
                Benchmark.setSmartTimerDescription("gs", "Time spent in Grayscale.process()");
                Benchmark.setSmartTimerDescription("gs-count", "No. of calls to grayscale.process()");

                // Set the timer interval
                Benchmark.setSmartTimerCheckInterval(15000);
		//#enddebug

                //#ifdef title:defined
			//#= String title = "${ title }";
		//#else
			String title = "J2ME Polish";
		//#endif
		//#style mainScreen
		this.menuScreen = new List(title, List.IMPLICIT);
		//#style mainItem
		this.menuScreen.append( "Chat", null);
		//#style mainItem
		this.menuScreen.append("Mail", null);
		//#style mainItem
		this.menuScreen.append("Settings", null);
		//#style mainItem
		this.menuScreen.append("Applications", null);
		//#style mainItem
		this.menuScreen.append("Help", null);
		//#style mainItem
		this.menuScreen.append("Quit", null);
		//#style mainItem
		this.menuScreen.append("Documents", null);
		
		this.menuScreen.setCommandListener(this);
		this.menuScreen.addCommand( this.startGameCmd ); 
		this.menuScreen.addCommand( this.quitCmd );
                this.menuScreen.addCommand( this.showLogCmd );
		
		// You can also use further localization features like the following: 
		//System.out.println("Today is " + Locale.formatDate( System.currentTimeMillis() ));
		
		//#debug
		System.out.println("initialisation done."); 
		

	}

	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("setting display.");
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.menuScreen );
		DeviceControl.lightOn();
		DeviceControl.vibrate(20);

		this.menuScreen.setFullScreenMode(true);
		//#debug
		System.out.println("sample application is up and running.");
	}

	protected void pauseApp() {
		DeviceControl.lightOff(); 
	}
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}
	
	public void commandAction(Command cmd, Displayable screen) {		
		if (screen == this.menuScreen) {
			//#ifdef polish.debugEnabled
				if (cmd == this.showLogCmd ) {

                                        //#debug ovidiu
					Benchmark.haltOutput();
					Debug.showLog(this.display);
					return;
				}
			//#endif
			if (cmd == List.SELECT_COMMAND) {
				int selectedItem = this.menuScreen.getSelectedIndex();
				if (selectedItem == 5) { //quit has been selected
					quit();
				} else {
				 
				}
			} else if (cmd == this.startGameCmd) {
				startGame();
			}
		} 
		if (cmd == this.quitCmd) {
			quit();
		}
	}
	
	/**
	 * @param string
	 */
	private void showAlert(String message) {
		//#style messageAlert
		
		/* Alert alert = new Alert( "Alert", message, null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert ); */
	}

	private void startGame() {
		Alert alert = null;
		//#style messageAlert
		//#= alert = new Alert( "Welcome", Locale.get( "messages.welcome", "${user.name}" ), null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert, this.menuScreen );
	}
	
	private void quit() {
		notifyDestroyed();
	}
	
	
}
