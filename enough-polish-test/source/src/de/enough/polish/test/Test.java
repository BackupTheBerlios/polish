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

import java.util.Random;

import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.DebugHelper;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.UiAccess;

/**
 * 
 * <br>Copyright Enough Software 2005-2008
 * <pre>
 * history
 *        Mar 13, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class Test extends MIDlet implements Runnable{

    protected void destroyApp(boolean arg0) {
        System.out.println("Destroying MIDlet.");
    }

    protected void pauseApp() {
        System.out.println("Stopping MIDlet.");
    }
    
    TestProvider provider;
    
    String[] data;

    protected void startApp(){
    	
    	Form form = new Form("ListTest");
    	
    	this.provider = new TestProvider(UiAccess.getScreenContainer(form),form);
    	
    	Display.getDisplay(this).setCurrent(form);
    	
    	data = this.provider.getData();
    	
    	new Thread(this).start();
    }

	
	boolean begin;
	public void run() {
		while(true) {
			
			try {
			if(begin) {
				this.data[20] = "the entry";
				this.data[50] = "entry 50";
				
				begin = false;
			} else {
				this.data[20] = "entry 20";
				this.data[50] = "the entry";
				
				begin = true;
			}
			this.provider.update("the entry", true);
			
				Thread.sleep(10000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
    
    
}
