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
public class Test extends MIDlet{

    protected void destroyApp(boolean arg0) {
        System.out.println("Destroying MIDlet.");
    }

    protected void pauseApp() {
        System.out.println("Stopping MIDlet.");
    }
    
    protected void startApp(){
    	//#style form
    	Form form = new Form("ListTest");
    	
    	UiAccess.init(form);
    	
    	//#style linebox
    	LineBox linebox = new LineBox(form.getScreenContentWidth());
    	
    	for (int i = 0; i < 10; i++) {
    		Item item;
    		
    		String text = "part " + i;
    		
    		//#style part
    		item = new StringItem(null,text);
    		
    		if(i % 3 == 0) {
    			int remainingSpace = linebox.getRemainingSpace();
    			System.out.println(linebox + " has remaining space : " + remainingSpace);
    			//#style lineboxInner
    			LineBox lineboxInner = new LineBox(remainingSpace);
    			if(lineboxInner.hasSpace(item)) {
    				System.out.println(lineboxInner + " has space for " + item);
    				lineboxInner.add(item);
    			} 
    			item = lineboxInner.getLine();
    		} 
    		
    		if(!linebox.hasSpace(item)) {
    			form.append(linebox.getLine());
    			//#style linebox
    			linebox = new LineBox(form.getScreenContentWidth());
    			System.out.println("new linebox " + linebox);
    		} 
    		
    		System.out.println("adding " + item + " to " + linebox);
    		linebox.add(item);
		}
    	
    	form.append(linebox.getLine());
    	
    	Display.getDisplay(this).setCurrent(form);
    }
}
