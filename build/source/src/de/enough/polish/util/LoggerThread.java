/*
 * Created on 05-Nov-2004 at 20:59:18.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * <p>Can be used to log the output of any stream, e.g. of an external process.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        05-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LoggerThread extends Thread {
	private final InputStream input;
	private final PrintStream output;
	private final String header;

	/**
	 * Creates a new logger thread.
	 * 
	 * @param input the input stream
	 * @param output the stream to which the input should be redirected
	 * @param header the info which should be added in front of each line 
	 */
	public LoggerThread( InputStream input, PrintStream output, String header ) {
		this.input = input;
		this.output = output;
		this.header = header;
	}
	
	public void run() {
		StringBuffer log = new StringBuffer( 300 );
		log.append(this.header);
		int startPos = this.header.length();
		int c;
		
		try {
			while ((c = this.input.read() ) != -1) {
				if (c == '\n') {
					String logMessage = log.toString();
					this.output.println( logMessage );
					log.delete( startPos,  log.length() );
				}  else if (c != '\r') {
					log.append((char) c);
				}
			}
			String logMessage = log.toString();
			this.output.println( logMessage );
			this.input.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to log: " + e.toString() );
		}
	}

	/**
	 * Redirects the streams of the given process to the normal input- and output-streams.
	 * 
	 * @param process the process
	 * @param header the info which should be added in front of each line 
	 */
	public static void log(Process process, String header) {
		// log normal output:
		InputStream in = process.getInputStream();
		LoggerThread thread = new LoggerThread( in, System.out, header );
		thread.start();
		// log error output:
		in = process.getErrorStream();
		thread = new LoggerThread( in, System.err, header );
		thread.start();
	}
}