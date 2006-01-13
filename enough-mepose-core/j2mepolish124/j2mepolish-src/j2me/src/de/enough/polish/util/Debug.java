/*
 * Created on 20-Jan-2003 at 15:05:18.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.util;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
//#if polish.showLogOnError && polish.usePolishGui
	import de.enough.polish.ui.StyleSheet;
//#endif

/**
 * <p>Is used for debugging of information.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        20-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class Debug
implements CommandListener
{
	public static final Command RETURN_COMMAND = new Command( "return", Command.SCREEN, 1 );
	private static final ArrayList MESSAGES = new ArrayList( 100 );
	private static Displayable returnDisplayable;
	private static Display midletDisplay;
	private static javax.microedition.lcdui.TextBox textBox;
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 * @param exception the exception or just an ordinary object
	 */
	public static final void debug( String message, Object exception ) {
		if (exception instanceof Throwable) {
			debug( message, (Throwable) exception );
		} else {
			debug( message + exception );
		}
	}

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the int value
	 */
	public static final void debug( String message, int value ) {
		debug( message + value );
	}
	
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the long value
	 */
	public static final void debug( String message, long value ) {
		debug( message + value );
	}

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the short value
	 */
	public static final void debug( String message, short value ) {
		debug( message + value );
	}
	
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the byte value
	 */
	public static final void debug( String message, byte value ) {
		debug( message + value );
	}
	
	//#ifdef polish.cldc1.1
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the float value
	 */
	//# public static final void debug( String message, float value ) {
	//# 	debug( message + value );
	//# }
	//#endif
	
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param message the message.
	 * @param value the boolean value
	 */
	public static final void debug( String message, boolean value ) {
		debug( message + value );
	}
	
	/**
	 * Prints the message or adds the message to the internal message list.
	 * 
	 * @param message the message.
	 */
	public static final void debug( String message ) {
		debug( message, null );
	}
	
	/**
	 * Logs the given exception.
	 * 
	 * @param exception the exception which was catched.
	 */
	public static final void debug( Throwable exception ) {
		debug( "Error", exception );
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param message the message.
	 * @param exception the exception
	 */
	public static final void debug( String message, Throwable exception ) {
		System.out.println( message );
		if (exception != null) {
			message += ": " + exception.toString();
			exception.printStackTrace();
		}
		MESSAGES.add( message );
		if (MESSAGES.size() > 98) {
			MESSAGES.remove( 0 );
		}
		if (Debug.textBox != null) {
			addMessages();
		}
		//#if polish.showLogOnError && polish.usePolishGui 
			if (exception != null) {
				showLog( StyleSheet.display );
			}
		//#endif
	}
		
	/**
	 * Retrieves a form with all the debugging messages.
	 * 
	 * @param reverseSort true when the last message should be shown first
	 * @param listener the command listener for the created form
	 * @return the form containing all the debugging messages so far.
	 * @throws NullPointerException when the listener is null
	 * @deprecated use showLogForm instead
	 * @see #showLog(Display)
	 */
	public static final Form getLogForm( boolean reverseSort, CommandListener listener ) {
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
		form.setCommandListener(listener);
		form.addCommand(RETURN_COMMAND);
		return form;
	}
	
	/**
	 * Shows the log with the current messages.
	 * When new messages are added, the log will be updated.
	 * The latest messages will be at the top.
	 * 
	 * @param display the display-variable for the current MIDlet.
	 */
	public static void showLog( Display display ) {
		if (returnDisplayable != null) {
			// the debug form is already shown!
			return;
		}
		if (display == null) {
			System.err.println("Unable to show log with null-Display.");
			return;
		}
		Debug.returnDisplayable = display.getCurrent();
		Debug.midletDisplay = display;
		Debug.textBox = new javax.microedition.lcdui.TextBox("Log", null, 4096, javax.microedition.lcdui.TextField.ANY );
		int maxSize = Debug.textBox.getMaxSize();
		Debug.textBox.setMaxSize( maxSize );
		addMessages();
		Debug.textBox.addCommand(RETURN_COMMAND);
		Debug.textBox.setCommandListener( new Debug() );
		display.setCurrent( Debug.textBox );
	}
	
	/**
	 * Adds all messages to the internal TextBox-Log.
	 *
	 */
	private static final void addMessages() {
		StringBuffer buffer = new StringBuffer();
		int maxSize = Debug.textBox.getMaxSize();
		String[] messages = (String[]) MESSAGES.toArray( new String[ MESSAGES.size() ] );
		//#if polish.Debug.showLastMessageFirst != true
			for (int i = 0; i < messages.length; i++) {
				buffer.append( messages[i])
				.append( '\n' );
			}
			if ( buffer.length() >= maxSize) {
				buffer.delete(0,  buffer.length() - maxSize  );
			}
		//#else
			int i = messages.length - 1; 
			while (buffer.length() < maxSize && i >= 0 ) {
				buffer.append( messages[i])
					.append( '\n' );
				i--;
			}
			if ( buffer.length() >= maxSize) {
				buffer.delete(maxSize - 1, buffer.length() );
			}
		//#endif
		Debug.textBox.setString( buffer.toString() );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable screen) {
		Debug.textBox = null;
		Display disp = Debug.midletDisplay;
		Debug.midletDisplay = null;
		Displayable returnDisp = Debug.returnDisplayable;
		Debug.returnDisplayable = null;
		disp.setCurrent( returnDisp );
	}
	
}


