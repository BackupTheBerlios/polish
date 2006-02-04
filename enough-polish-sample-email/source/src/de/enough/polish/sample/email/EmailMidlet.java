/*
 * Created on 30-Jan-2006 at 03:27:38.
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
package de.enough.polish.sample.email;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.TreeItem;

/**
 * <p>Example for using the TreeItem.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EmailMidlet 
extends MIDlet
implements CommandListener, ItemStateListener
{

	/**
	 * Creates a new midlet.
	 */
	public EmailMidlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		//#style mailForm
		Form form = new Form("MobileMail");
		
//		ListItem list = new ListItem( null );
//		list.append("Write new mail", null);
//		list.append("Address book", null);
//		form.append( list );
		
		//#style mailTree
		TreeItem tree = new TreeItem( null );
		//#style mailbox
		Item node = tree.appendToRoot("Inbox", null);
		//#style mailSummary
		Item email = tree.appendToNode(node, "Bill Gates", null );
		//#style mailDetail
		Item detail = new StringItem( "Subject: ", "What's next?");
		tree.appendToNode( email, detail );
		//#style mailDetail
		detail = new StringItem( "Text: ", "After conquering the world, what's left?");
		tree.appendToNode( email, detail );
		//#style mailSummary
		email = tree.appendToNode(node, "Stephen Hawkings", null );
		//#style mailDetail
		detail = new StringItem( "Subject: ", "Black Holes");
		tree.appendToNode( email, detail );
		//#style mailDetail
		detail = new StringItem( "Text: ", "They are my favourite!");
		tree.appendToNode( email, detail );
		//#style mailSummary
		email = tree.appendToNode(node, "David Byrne", null );
		//#style mailDetail
		detail = new StringItem( "Subject: ", "String Theory");
		tree.appendToNode( email, detail );
		//#style mailDetail
		detail = new StringItem( "Text: ", "Or is m-theory with multidimensional branes?!");
		tree.appendToNode( email, detail );

		//#style mailbox
		node = tree.appendToRoot("Outbox", null);
		//#style mailSummary
		email = tree.appendToNode(node, "Dschingis Khan", null );
		//#style mailDetail
		detail = new StringItem( "Subject: ", "Rider");
		tree.appendToNode( email, detail );
		//#style mailDetail
		detail = new StringItem( "Text: ", "A rider in the night.");
		tree.appendToNode( email, detail );
		
		form.append( tree );
		form.setCommandListener( this );
		form.setItemStateListener( this );
		form.addCommand( new Command("Exit", Command.SCREEN, 1));
		
		Display display = Display.getDisplay( this );
		display.setCurrent( form );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// just keep on pausing
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// nothing to clean up
	}

	public void commandAction(Command arg0, Displayable arg1) {
		notifyDestroyed();
	}

	public void itemStateChanged(Item item) {
		System.out.println("ItemStateChanged " + item);
		// TODO enough implement itemStateChanged
		
	}

}
