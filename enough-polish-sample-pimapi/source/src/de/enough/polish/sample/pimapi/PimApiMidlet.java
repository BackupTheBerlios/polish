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
package de.enough.polish.sample.pimapi;

import java.util.Enumeration;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import de.enough.polish.util.ArrayList;

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
public class PimApiMidlet extends MIDlet{

	public class Message{
		private boolean error;
		private String testName;
		private String message;
		public Message(boolean error, String testName, String message) {
			this.error = error;
			this.testName = testName;
			this.message = message;
		}
		public String getMessage() {
			return this.message;
		}
		public String getTestName() {
			return this.testName;
		}
		public boolean isError() {
			return this.error;
		}
		@Override
		public String toString() {
			String status = "";
			if(this.error) {
				status = "ERROR:";
			}
			return status+this.message+"("+this.testName+")";
		}
	}
	
	private ArrayList<Message> messages = new ArrayList<Message>();
	private static Display display;

	public void run() {
		try {
			clearContacts();
			testRemoveContacts();
			testGetItems();
			testCreateContact();
			testGetItems();
		} catch(Exception exception) {
			addException("run","Somewhere something went south", exception);
		} finally {
			showResults();
		}
	}

	public void testCreateContact() {
		ContactList contactList = getContactList();
		Contact contact = contactList.createContact();
		
		int numberOfElements;
		numberOfElements = contactList.stringArraySize(Contact.NAME);
		String[] name = new String[numberOfElements];
		name[Contact.NAME_OTHER] = "Scrooge McDuck";
		contact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, name);

		numberOfElements = contactList.stringArraySize(Contact.ADDR);
		String[] address = new String[numberOfElements];
		address[Contact.ADDR_EXTRA] = "Duckburg";
		contact.addStringArray(Contact.ADDR, PIMItem.ATTR_NONE, address);
		contact.addString(Contact.TEL, Contact.ATTR_HOME, "+4999999999");
		contact.addString(Contact.TEL, Contact.ATTR_WORK, "+4911111111");
		try {
			contact.commit();
		} catch (PIMException e) {
			addException("createContact","Could not commit contact",e);
			return;
		}
	}

	public void testGetItems() {
		ContactList contactList = getContactList();
		Enumeration items;
		try {
			items = contactList.items();
		} catch (PIMException e) {
			addException("removeContacts","Could not get list of items from contactList", e);
			return;
		}
		while(items.hasMoreElements()) {
			PIMItem item = (PIMItem) items.nextElement();
			addData("getItems",item.toString());
		}
	}
	

	private void clearContacts() {
		ContactList contactList = getContactList();
		Enumeration items;
		try {
			items = contactList.items();
		} catch (PIMException e) {
			addException("removeContacts","Could not get list of items from contactList", e);
			return;
		}
		int i = 0;
		while(items.hasMoreElements()) {
			i++;
			Contact contact = (Contact) items.nextElement();
			try {
				contactList.removeContact(contact);
			} catch (PIMException e) {
				addException("clearContacts","Removing contact '"+i+"' failed", e);
			}
		}
	}
	
	public void testRemoveContacts() {
		clearContacts();
		ContactList	contactList = getContactList();
		Enumeration items;
		try {
			items = contactList.items();
		} catch (PIMException e) {
			addException("removeContacts","Could not get list of items from contactList", e);
			return;
		}
		if(items.hasMoreElements()) {
			addError("testRemoveContacts", "There are still contacts left");
		}
	}

	@Override
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		System.out.println("destroyApp:enter");
	}
	
	@Override
	protected void pauseApp() {
		System.out.println("pauseApp:enter");
	}

	@Override
	protected void startApp() throws MIDletStateChangeException {
		System.out.println("startApp:enter");
		if(display == null) {
			display = Display.getDisplay(this);
		}
		run();
	}

	private void addData(String testName, String messageText) {
		Message message = new Message(false, testName,messageText);
		this.messages.add(message);
	}
	
	private void addError(String testName, String messageText) {
		Message message = new Message(true, testName,messageText);
		this.messages.add(message);
	}
	
	private void addException(String testName, String failedSituation, Exception exception) {
		exception.printStackTrace();
		String exceptionName = exception.getClass().getName();
		String exceptionMessage = exception.getMessage();
		Message message = new Message(true, testName,exceptionName+":"+exceptionMessage);
		this.messages.add(message);
	}

	private ContactList getContactList() {
		PIM pim = PIM.getInstance();
		ContactList contactList;
		try {
			contactList = (ContactList)pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
		} catch (PIMException e) {
			addException("getContactList","Could not open Contact List", e);
			throw new RuntimeException();
		}
		return contactList;
	}
	
	private void showResults() {
		Form form = new Form("Test Results");
		int numberOfMessages = this.messages.size();
		for (int i = 0; i < numberOfMessages; i++) {
			Message message = this.messages.get(i);
			if(message == null) {
				continue;
			}
			String messageString = message.toString();
			form.append(messageString);
		}
		form.append("End of tests");
		this.messages.clear();
		display.setCurrent(form);
	}

}
