//#condition polish.usePolishGui
/*
 * Created on 08-Mar-2006 at 01:32:28.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

import java.util.Hashtable;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import de.enough.polish.ui.ChoiceTextField;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * <p>Provides a form for creating a new mail.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        08-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateMessageForm extends Form  {
		
	private final static String[] RECEIVERS = new String[] {
		"aaron@somewhere.com", "ajax@hype.com", "asynchron@performance.com", "best@j2mepolish.org",
		"beta@j2mepolish.org", "circus@ms.com", "doing@going.com", "info@enough.de", "j2mepolish@enough.de"		
	};
	private final static String[] SENDERS = new String[] {
		"auser@somewhere.net", "buser@other.org", "donkey@p2p.net", "gold@ironr.us", "j2me@polish.org"
	};
	private final static String DEFAULT_SENDER = "j2me@polish.org";
	         
	private final ChoiceTextField receiver;
	private final ChoiceTextField sender;
	private TextField text;

	/**
	 * Creates a new form for writing an email.
	 * 
	 * @param title the title of the frame
	 */
	public CreateMessageForm(String title) {
		//#if polish.usePolishGui
			//#style createMessageForm?
			//# this( title );
		//#else
			this( title, null );
		//#endif
	}
	
	
	//#if polish.usePolishGui
	/**
	 * Creates a new form for writing an email.
	 * 
	 * @param title the title of the frame
	 * @param style the style for this form, is applied using the #style preprocessing directive
	 */
	public CreateMessageForm(String title, Style style ) {
		//#if polish.usePolishGui
			//# super( title, style );
		//#else
			super( title );
		//#endif
		boolean allowFreeText = false;
		boolean appendSelectedChoice = true;
		String appendDelimiter = ";";
		//#style addressInput
		this.receiver = new ChoiceTextField( "to: " , null, 255, TextField.EMAILADDR, RECEIVERS, allowFreeText, appendSelectedChoice, appendDelimiter );
		append( this.receiver );
		allowFreeText = false;
		//#style addressInput
		this.sender = new ChoiceTextField( "from: ", null, 255, TextField.EMAILADDR, SENDERS, allowFreeText );
		append( this.sender );
		//#style input, addressInput
		this.text = new TextField( "message: ", null, 255, TextField.ANY );
		append( this.text );
	}
	//#endif
	
	/**
	 * Retrieves the reveiver for the email
	 * 
	 * @return the reveiver for the email
	 */
	public String getReceiver() {
		return this.receiver.getString();
	}
	
	/**
	 * Retrieves the sender for the email
	 * 
	 * @return the sender for the email
	 */
	public String getSender() {
		String address = this.sender.getString();
		if (address == null || address.length() == 0) {
			// when the user has not entered the ChoiceTextField it can still be empty,
			// in that case use the default sender instead:
			address = DEFAULT_SENDER;
		}
		return address;
	}


}
