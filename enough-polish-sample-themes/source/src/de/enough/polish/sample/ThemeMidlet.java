/*
 * Created on Mar 13, 2008 at 12:58:57 PM.
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
package de.enough.polish.sample;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

//#if polish.useThemes
import de.enough.polish.theme.ThemeController;
import de.enough.polish.theme.ThemeStream;
//#endif
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * 
 * <br>
 * Copyright Enough Software 2005-2008
 * 
 * <pre>
 *  history
 *         Mar 13, 2008 - rickyn creation
 * </pre>
 * 
 * @author Richard Nkrumah
 */
public class ThemeMidlet extends MIDlet implements CommandListener{

	Form form;

	static Command cmdExit;

	static {
		cmdExit = new Command("Exit", Command.EXIT, 0);
	}

	public ThemeMidlet() {
		this.form = new Form("Themes");

		this.form.addCommand(cmdExit);

		//#if polish.useThemes
		ThemeStream stream = null;

		try {
			//#= stream = new ThemeStream(this,"/${id}.theme");

			Style mainStyle = ThemeController.getStyle(stream, "main");
			
			this.form.setStyle(mainStyle);
			
			Image logoImage = ThemeController.getImage(stream, "logo");
			
			//#style logo
			this.form.append(logoImage);
			
		} catch (IOException e) {
			System.out.println("unable to load themes " + e);
		}

		//#endif
		
		this.form.setCommandListener(this);
	}

	protected void destroyApp(boolean arg0) {

	}

	protected void pauseApp() {

	}

	protected void startApp() {
		StyleSheet.display.setCurrent(this.form);
	}

	public void commandAction(Command cmd, Displayable disp) {
		if(cmd == cmdExit)
		{
			notifyDestroyed();
		}
	}

}
