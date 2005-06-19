/*
 * Created on 23-Apr-2005 at 03:37:20.
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
package de.enough.polish.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>Contains one log entry.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LogEntry {
	
	public final String className;
	public final long time;
	public final String level;
	public final String message;
	public final int lineNumber;
	public final String exception;
	private byte[] data;

	/**
	 * Creates a new log entry,
	 * 
	 * @param className the name of the class
	 * @param lineNumber the line number within the class
	 * @param time the time of the log event
	 * @param level the level, e.g. "debug" or "info"
	 * @param message the message
	 * @param exception the exception message, if any
	 * 
	 */
	public LogEntry( String className, int lineNumber, long time, String level, String message, String exception ) {
		if (className == null) {
			className = "";
		}
		if ( level == null ) {
			level = "";
		}
		if ( message == null ) {
			message = "";
		}
		if ( exception == null ) {
			exception = "";
		}
		this.className = className;
		this.lineNumber = lineNumber;
		this.time = time;
		this.level = level;
		this.message = message;
		this.exception = exception;
	}
	
	/**
	 * Creates a new log entry out of the given byte array.
	 * 
	 * @param data the data of the entry
	 * @throws IOException when the data could not be read
	 */
	public LogEntry( byte[] data ) 
	throws IOException 
	{
		DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ) );
		this.time = in.readLong();
		this.level = in.readUTF();
		this.className = in.readUTF();
		this.lineNumber = in.readInt();
		this.message = in.readUTF();
		this.exception = in.readUTF();
	}
	
	/**
	 * Writes the data into a byte buffer.
	 * 
	 * @return a byte array containing the data
	 * @throws IOException when the data could not be written
	 */
	public byte[] toByteArray() 
	throws IOException 
	{
		if (this.data == null) {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream( byteOut );
			out.writeLong( this.time );
			out.writeUTF( this.level );
			out.writeUTF( this.className );
			out.writeInt( this.lineNumber );
			out.writeUTF( this.message );
			out.writeUTF(this.exception);
			this.data = byteOut.toByteArray();
		}
		return this.data;
	}
	
	/**
	 * Converts the entry into a log message.
	 * @return the String representation of this log entry
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[").append( this.level ).append("] "); 
		//#if polish.debugVerbose
			buffer.append( this.className ).append(" (").append( this.lineNumber ).append("): ");
		//#endif
		buffer.append( this.message );
		if (this.exception.length() > 0) {
			buffer.append("/").append( this.exception );
		}
		return buffer.toString();
	}

}
