/*
 * Created on 20-Jan-2003 at 15:05:18.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * www.enough.de/j2mepolish for details.
 */
package de.enough.polish.util;

//#ifdef polish.useDebugGui
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
//#endif

/**
 * <p>Is used for debugging of information.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        20-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class Debug {
	//#ifdef polish.useDebugGui
	public static final Command RETURN_COMMAND = new Command( "return", Command.SCREEN, 1 );
	private static final ArrayList MESSAGES = new ArrayList( 100 );
	//#endif
	
	/**
	 * Prints a message with a time-stamp.
	 * 
	 * @param time the time in ms.
	 * @param message the message
	 */
	public static final void debug( long time, String message ) {
		debug( time + message );
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 */
	public static final void debug( String message ) {
		debug( message, null );
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 * @param exception the exception
	 */
	public static final void debug( String message, Throwable exception ) {
		//#ifndef polish.useDebugGui
		System.out.println( message );
		if (exception != null) {
			exception.printStackTrace();
		}
		//#else
		// add message to list:
		MESSAGES.add( message );
		if (MESSAGES.size() > 98) {
			MESSAGES.remove( 0 );
		}
		//#endif
	}
		
	//#ifdef polish.useDebugGui
	public static final Form getLogForm( boolean reverseSort ) {
		String[] messages = (String[]) MESSAGES.toArray( new String[ MESSAGES.size() ] );
		StringItem[] items = new StringItem[ messages.length ];
		int index = messages.length - 1;
		for (int i = 0; i < items.length; i++) {
			String message;
			if (reverseSort) {
				message = messages[ index ];
				index--;
			} else {
				message = messages[i];
			}
			items[i] = new StringItem( null, message );
		}
		Form form = new Form( "debug", items );
		form.addCommand(RETURN_COMMAND);
		return form;
	}
	//#endif
	
}


