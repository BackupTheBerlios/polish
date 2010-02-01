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

import de.enough.polish.processing.ProcessingBackground;
import de.enough.polish.processing.ProcessingContext;
import de.enough.polish.processing.ProcessingItem;
import de.enough.polish.ui.IconItem;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import de.enough.polish.ui.Form;
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

	Form menuScreen;
	Command startGameCmd = new Command( Locale.get( "cmd.StartGame" ), Command.ITEM, 8 );
	Command quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	//#ifdef polish.debugEnabled
		Command showLogCmd = new Command( Locale.get("cmd.ShowLog"), Command.ITEM, 9 );
	//#endif
	Display display;

	public MenuMidlet() {
		super();
		//#debug
		System.out.println("starting MenuMidlet");
		//#ifdef title:defined
			//#= String title = "${ title }";
		//#else
			String title = "J2ME Polish";
		//#endif


		//#style mainScreen
		this.menuScreen = new Form("Hello");

                // Create a new Processing Context. We will use it for an Item.
                // The class ProcessingContextExample extends ProcessingContext
                ProcessingContext itemContext = new ProcessingContextExample();

                // Create a new ProcessingItem based on the above Context.
                // We will later add the item to the on-screen form.
                ProcessingItem item = new ProcessingItem(itemContext);

                // Create another processing context, this time to use as a background.
                // The class ProcessingBackgroundExample extends ProcessingContext
                ProcessingContext backgroundContext = new ProcessingBackgroundExample();

                // Create a new ProcessingBackground based on the new Context.
                ProcessingBackground background = new ProcessingBackground(backgroundContext);

                // Create a new IconItem
                IconItem iconItem = new IconItem("Chat",null);

                // Attach the ProcessingBackground to the IconItem
                iconItem.setBackground(background);

                // Append the IconItem to the form
                //#style mainItem
		this.menuScreen.append(iconItem);

                // Append the ProcessingItem to the form
                //#style mainItem
                this.menuScreen.append(item);

                // Create another icon item, this one without a ProcessingBackground.
                // For comparison purposes. :)
                IconItem noBgIconItem = new IconItem("Mail",null);

                // Append it to the form
                //#style mainItem
                this.menuScreen.append(noBgIconItem);
                
		//#style mainItem
                this.menuScreen.append("Settings");
		
		//#style mainItem
		this.menuScreen.append("Applications");

                
//		//#style mainItem
//		this.menuScreen.append("Help", null);
//		//#style mainItem
//		this.menuScreen.append("Quit", null);


		this.menuScreen.setCommandListener(this);
		this.menuScreen.addCommand( this.startGameCmd );
		this.menuScreen.addCommand( this.quitCmd );
                
		//#ifdef polish.debugEnabled
			this.menuScreen.addCommand( this.showLogCmd );
		//#endif

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
					Debug.showLog(this.display);
					return;
				}
			//#endif
			if (cmd == List.SELECT_COMMAND) {
				int selectedItem = this.menuScreen.getCurrentIndex();
				if (selectedItem == 5) { //quit has been selected;
					quit();
				} else {
					showAlert( this.menuScreen.get(selectedItem).getLabel() );
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
		Alert alert = new Alert( "Alert", message, null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert );
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
