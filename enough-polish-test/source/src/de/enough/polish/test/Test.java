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
package de.enough.polish.test;

import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;

/**
 * 
 * <br>Copyright Enough Software 2005-2008
 * <pre>
 * history
 *        Mar 13, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class Test extends MIDlet {

    protected void destroyApp(boolean arg0) {
        System.out.println("Destroying MIDlet.");
    }

    protected void pauseApp() {
        System.out.println("Stopping MIDlet.");
    }

    protected void startApp(){
    	Form form = new Form("Test");
    	
    	// add testing here
    	
    	Display.getDisplay(this).setCurrent(form);
    }
}
