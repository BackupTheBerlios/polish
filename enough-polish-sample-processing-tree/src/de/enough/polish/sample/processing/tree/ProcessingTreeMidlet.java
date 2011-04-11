/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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

package de.enough.polish.sample.processing.tree;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.processing.ProcessingContext;
import de.enough.polish.processing.ProcessingScreen;

/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2011</p>
 * 
 * @author Ovidiu Iliescu
 */
public class ProcessingTreeMidlet extends MIDlet{

	ProcessingScreen screen ;
    Display display;

	public ProcessingTreeMidlet() {
		super();

        // Create a sample ProcessingContext
        ProcessingContext context = new ProcessingTreeContext();

        // And attach it to a ProcessingScreen.
		//#style mainScreen
		this.screen = new ProcessingScreen("Processing Tree", context);                
	}

	protected void startApp() throws MIDletStateChangeException {		
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.screen );		
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}

	private void quit() {
		notifyDestroyed();
	}

}
