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
import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;

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
	public static final Command RETURN_COMMAND = new Command( "Return", Command.SCREEN, 1 );
	private static final ArrayList MESSAGES = new ArrayList( 100 );
	private static Displayable returnDisplayable;
	private static Display midletDisplay;
	private static javax.microedition.lcdui.TextBox textBox;
	//#if polish.log.handlers:defined
		private static LogHandler[] handlers;
		static {
			//#= handlers = new LogHandler[ ${ number( polish.log.handlers )} ];
			int i = 0;
			//#foreach handler in polish.log.handlers
				//#= handlers[i] = new ${ classname( handler )}();
				i++;
			//#next handler
		}
	//#endif
	
	/**
	 * Prints a message.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param exception the exception or just an ordinary object
	 */
	public static final void debug( String level, String className, int lineNumber, String message, Object exception ) {
		if (exception instanceof Throwable) {
			debug( level, className, lineNumber, message, (Throwable) exception );
		} else {
			debug( level, className, lineNumber, message + exception );
		}
	}

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the char value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, char value ) {
		debug( level, className, lineNumber, message + value );
	}

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the int value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, int value ) {
		debug( level, className, lineNumber, message + value );
	}
	
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the long value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, long value ) {
		debug( level, className, lineNumber, message + value );
	}

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the short value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, short value ) {
		debug( level, className, lineNumber, message + value );
	}
	
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the byte value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, byte value ) {
		debug( level, className, lineNumber, message + value );
	}
	
	//#ifdef polish.cldc1.1
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the float value
	 */
	//# public static final void debug( String level, String className, int lineNumber, String message, float value ) {
	//# 	debug( level, className, lineNumber, message + value );
	//# }
	//#endif

	//#ifdef polish.cldc1.1
	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the double value
	 */
	//# public static final void debug( String level, String className, int lineNumber, String message, double value ) {
	//# 	debug( level, className, lineNumber, message + value );
	//# }
	//#endif

	/**
	 * Prints a message.
	 * This method should not be used directly.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param value the boolean value
	 */
	public static final void debug( String level, String className, int lineNumber, String message, boolean value ) {
		debug( level, className, lineNumber, message + value );
	}
	
	/**
	 * Prints the message or adds the message to the internal message list.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 */
	public static final void debug( String level, String className, int lineNumber, String message ) {
		debug( level, className, lineNumber, message, null );
	}
	
	/**
	 * Logs the given exception.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param exception the exception which was catched.
	 */
	public static final void debug( String level, String className, int lineNumber, Throwable exception ) {
		debug( level, className, lineNumber, "Error", exception );
	}
	
	/**
	 * Prints a message.
	 * 
	 * @param level the log level, e.g. "debug"
	 * @param className the name of the class
	 * @param lineNumber the line numer of the log statement
	 * @param message the message.
	 * @param exception the exception
	 */
	public static final void debug( String level, String className, int lineNumber, String message, Throwable exception ) {
		String exceptionMessage = null;
		if (exception != null) {
			exceptionMessage = exception.toString();
		}
		LogEntry logEntry = new LogEntry( className, lineNumber, System.currentTimeMillis(), level, message, exceptionMessage );
		System.out.println( logEntry.toString() );
		if (exception != null) {
			exception.printStackTrace();
		}
		MESSAGES.add( logEntry );
		if (MESSAGES.size() > 98) {
			MESSAGES.remove( 0 );
		}
		if (Debug.textBox != null) {
			addMessages();
		}
		// try to store this log-entry:
		// add then entry to the interal list:
		//#if polish.log.handlers:defined
			if ( handlers != null ) {
				for (int i = 0; i < handlers.length; i++) {
					LogHandler handler = handlers[i];
					try {
						handler.handleLogEntry(logEntry);
					} catch (Exception e) {
						e.printStackTrace();
						LogEntry entry = new LogEntry( "de.enough.polish.log.LogHandler", -1, System.currentTimeMillis(), "error", "Unable to handle log entry", e.toString() );
						while (MESSAGES.size() > 5) {
							MESSAGES.remove( 0 );
						}
						MESSAGES.add( entry );
						//#if polish.showLogOnError && polish.usePolishGui 
							if (Debug.textBox == null) {
								showLog( StyleSheet.display );
							}
						//#endif
					}
				}
			}
		//#endif
		
		//#if polish.showLogOnError && polish.usePolishGui 
			if (exception != null && Debug.textBox == null) {
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
		LogEntry[] entries = (LogEntry[]) MESSAGES.toArray( new LogEntry[ MESSAGES.size() ] );
		StringItem[] items = new StringItem[ entries.length ];
		int index = entries.length - 1;
		for (int i = 0; i < items.length; i++) {
			LogEntry entry;
			if (reverseSort) {
				entry = entries[ index ];
				index--;
			} else {
				entry = entries[i];
			}
			items[i] = new StringItem( null, entry.toString() );
		}
		Form form = new Form( "Log", items );
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
		LogEntry[] entries = (LogEntry[]) MESSAGES.toArray( new LogEntry[ MESSAGES.size() ] );
		//#if polish.Debug.showLastMessageFirst != true
			for (int i = 0; i < entries.length; i++) {
				buffer.append( entries[i].toString())
				.append( '\n' );
			}
			if ( buffer.length() >= maxSize) {
				buffer.delete(0,  buffer.length() - maxSize  );
			}
		//#else
			int i = entries.length - 1; 
			while (buffer.length() < maxSize && i >= 0 ) {
				buffer.append( entries[i].toString() )
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


