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
package de.enough.polish.jabber;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.im.Account;
import de.enough.polish.im.ID;
import de.enough.polish.im.Message;
import de.enough.polish.jabber.remote.JabberCallback;
import de.enough.polish.jabber.remote.JabberWorker;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextField;

/**
 * 
 * <br>Copyright Enough Software 2005-2008
 * <pre>
 * history
 *        Mar 13, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class JabberClient extends MIDlet implements CommandListener, JabberCallback{
	
	public final static Command cmdLogin = new Command("Login",Command.OK,0);
	public final static Command cmdSend = new Command("Send",Command.OK,1);
	public final static Command cmdExit = new Command("Exit",Command.EXIT,0);
	
	TextField field;
	
	Form main;
	
	Account account;
	
	ID target;
	
	JabberWorker worker;
	
	public JabberClient()
	{
		this.field = new TextField("Message","",255,TextField.ANY);
		
		this.main = new Form("JabberClient");
		
		this.main.append(this.field);
		
		this.main.addCommand(cmdLogin);
		this.main.addCommand(cmdSend);
		this.main.addCommand(cmdExit);

    	this.main.setCommandListener(this);
    	
    	this.account = new Account("andre","cheeba","other.site-online.name");
    	
    	this.target = new ID("test@other.site-online.name");
    	
    	this.worker = new JabberWorker(this.account,this);
	}

    protected void destroyApp(boolean arg0) {}

    protected void pauseApp() {}

    protected void startApp()
    {
    	StyleSheet.display.setCurrent(this.main);
    }

	public void commandAction(Command cmd, Displayable disp) {
		if(cmd == cmdLogin)
		{
			this.worker.login();
		}
		
		if(cmd == cmdSend)
		{
			this.worker.send(new Message(this.account,this.target,this.field.getString()));
		}
		
		if(cmd == cmdExit)
		{
			this.worker.logoff();
			notifyDestroyed();
		}
	}
	
	public void loginEvent(Exception exception) {
		if(exception == null)
		{
			System.out.println("Successfully logged in");
		}
		else
		{
			System.out.println("unable to login : " + exception);
		}
	}
	
	public void logoffEvent(Exception exception) {
		if(exception == null)
		{
			System.out.println("Successfully logged off");
		}
		else
		{
			System.out.println("unable to logoff : " + exception);
		}
	}

	public void idEvent(ID id) {
		//do stuff
	}

	public void messageEvent(Message message) {
		this.main.append(message.getMessage());
	}

}
