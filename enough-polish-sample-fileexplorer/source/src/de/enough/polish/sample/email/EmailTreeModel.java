//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TreeModel;
import de.enough.polish.util.ArrayList;

/**
 * @author robertvirkus
 *
 */
public class EmailTreeModel implements TreeModel {

	private IconItem itemInbox;
	private IconItem itemOutbox;
	private IconItem itemSent;

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#getChild(java.lang.Object, int)
	 */
	public void addChildren(Object parent, ArrayList list) {
		if (parent == this) {
			IconItem item;
			//#style mailbox
			item = new IconItem("Inbox", null);
			this.itemInbox = item;
			list.add( item );
			//#style mailbox
			item = new IconItem("Outbox", null);
			this.itemOutbox = item;
			list.add(item);
			//#style mailbox
			item = new IconItem("Sent", null);
			this.itemSent = item;
			list.add(item);
		} else if (parent == this.itemInbox) {
			IconItem item;
			//#style mailSummary
			item = new IconItem( "Bill Gates", null );
			list.add( item );
			//#style mailSummary
			item = new IconItem( "Stephen Hawkings", null );
			list.add( item );
			//#style mailSummary
			item = new IconItem( "David Byrne", null );
			list.add( item );
			//#style mailSummary
			item = new IconItem( "More...", null );
			list.add( item );			
		} else if (parent == this.itemOutbox) {
			IconItem item;
			//#style mailSummary
			item = new IconItem( "Enough Software", null );
			list.add( item );
		} else if (parent == this.itemSent) {
			IconItem item;
			//#style mailSummary
			item = new IconItem( "Steve Jobs", null );
			list.add( item );
		} else if (parent instanceof IconItem){
			String from = ((IconItem)parent).getText();
			String subject = null;
			String text = null;
			if ("Bill Gates".equals(from)) {
				subject = "What's next?";
				text ="After conquering the world, what's left?";
			} else if ("Stephen Hawkings".equals(from)) {
				subject = "Black Holes";
				text = "They are my favourite!";
			} else if ("David Byrne".equals(from)) {
				subject = "String Theory";
				text = "Or is it m-theory with multidimensional branes?!";
			} else if ("Enough Software".equals(from)) {
				subject = 	"J2ME Polish";
				text = "Powerful, Flexible, Extensible.";			
			} else if ("Steve Jobs".equals(from)) {
				subject = "iPhone 5G?";
				text = "Gimme that phone, please :-)  - and don't forget the Java support!";
			} else if ("More...".equals(from)) {
				IconItem item;
				//#style mailSummary
				item = new IconItem( "M...1", null );
				list.add( item );
				//#style mailSummary
				item = new IconItem( "M...2", null );
				list.add( item );
				//#style mailSummary
				item = new IconItem( "M...3", null );
				list.add( item );
				//#style mailSummary
				item = new IconItem( "M...4", null );
				list.add( item );
				return;
			} else if ("M...1".equals(from)) {
				subject = "First Subject";
				text = "First Text...!";
			} else if ("M...2".equals(from)) {
				subject = "Second Subject";
				text = "Second Text...!";
			} else if ("M...3".equals(from)) {
				subject = "Third Subject";
				text = "Third Text is a tiny little bit longer but that's ok...!";
			} else if ("M...4".equals(from)) {
				subject = "Fourth Subject";
				text = "Fourth Text...!";
			}
			StringItem item;
			//#style mailDetail
			item = new StringItem(  "Subject: ", subject );
			list.add(item);
			//#style mailDetail
			item = new StringItem(  "Text: ", text );
			list.add(item);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		if (node == this || node instanceof IconItem) {
			return false;
		} else {
			return true;
		}
	}

}
