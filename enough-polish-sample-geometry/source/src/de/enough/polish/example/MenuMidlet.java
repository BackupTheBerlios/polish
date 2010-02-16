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
import javax.microedition.lcdui.Display;
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
    
    Display display;

    public MenuMidlet() {
            super();
    }

    protected void startApp() throws MIDletStateChangeException {

            TestCanvas c = new TestCanvas(true);
            this.display = Display.getDisplay(this);
            this.display.setCurrent( c ) ;
            c.runTest();
    }


    protected void pauseApp() {
        // Do nothing
    }


    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        // Do nothing
    }

    public void commandAction(Command c, Displayable d) {
        // Do nothing
    }
	
	
}
