/*
 * Created on 08-Apr-2006 at 19:19:50.
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
package de.enough.polish.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>Wraps the native2ascii tool of the Java SDK.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        08-Apr-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Native2Ascii {

	private Native2Ascii() {
		super();
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Usage: java de.enough.polish.util.Native2Ascii filepath [encoding]");
			return;
		}
		File file = new File( args[0] );
		String encoding = null;
		if (args.length > 1) {
			encoding = args[1];
		}
		InputStream in = translateToAscii( new FileInputStream( file ), encoding );
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println( line );
		}
		in.close();	
	}
	
	/**
	 * Translates the given input stream to ascii code
	 * @param in the input stream
	 * @param encoding the encoding, when null the default encoding is used
	 * @return the input stream that contains the ascii representation of the given input stream
	 * @throws IOException when the native2ascii tool could not be found.
	 */
	public static InputStream translateToAscii( InputStream in, String encoding ) 
	throws IOException 
	{
		String javaHomePath = System.getProperty("java.home");
		File javaHome = new File( javaHomePath );
		if (!javaHome.exists()) {
			throw new IOException("The system property java.home points to the invalid destination [" + javaHome.getAbsolutePath() + "].");
		}
		File executable;
		if ( File.separatorChar == '\\') { // windows
			executable = new File( javaHome, "bin\\native2ascii.exe");
		} else {
			executable = new File( javaHome, "bin/native2ascii");
		}
		if (!executable.exists()) {
			if ( javaHomePath.endsWith("jre") || javaHomePath.endsWith("JRE") ) {
				javaHomePath = javaHomePath.substring( 0, javaHomePath.length() - "/jre".length() );
				javaHome = new File( javaHomePath );
				if ( File.separatorChar == '\\') { // windows
					executable = new File( javaHome, "bin\\native2ascii.exe");
				} else {
					executable = new File( javaHome, "bin/native2ascii");
				}				
			}
			if (!executable.exists()) {
				throw new IOException("Unable to find the native2ascii tool at [" + executable.getAbsolutePath() + "].");
			}
		}
		String[] arguments;
		if (encoding == null) {
			arguments = new String[]{ executable.getAbsolutePath() };			
		} else {
			arguments = new String[]{
					executable.getAbsolutePath(),
					"-encoding",
					encoding
				};			
		}
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec( arguments );
		StreamWrapper wrapper = new StreamWrapper( in, process.getOutputStream() );
		wrapper.start();
		return process.getInputStream();
	}

}
