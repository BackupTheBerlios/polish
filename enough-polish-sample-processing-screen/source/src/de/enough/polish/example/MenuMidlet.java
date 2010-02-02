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
import de.enough.polish.processing.ProcessingScreen;
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

	ProcessingScreen screen ;
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


                // Create a sample ProcessingContext
                ProcessingContext context = new ProcessingContextExample();

                // And attach it to a ProcessingScreen.
		//#style mainScreen
		this.screen = new ProcessingScreen("Hello", context);

                screen.setCommandListener(this);

                
	}

	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("setting display.");
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.screen );
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
		
	}

	private void quit() {
		notifyDestroyed();
	}



}
