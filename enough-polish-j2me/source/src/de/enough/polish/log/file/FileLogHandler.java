//#condition polish.api.pdaapi

/*
 * Created on 05-Jul-2005 at 23:45:29.
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
package de.enough.polish.log.file;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;

public class FileLogHandler extends LogHandler {
	
	private PrintStream out;
	private final Object lock = new Object();

	public FileLogHandler() {
		super();
		// TODO enough implement FileLogHandler
	}

	public void handleLogEntry(LogEntry entry) throws Exception {
		if (this.out == null) {
			synchronized ( this.lock ) {
				String url = null;
				String root = null;
				Enumeration enumeration = FileSystemRegistry.listRoots();
				String roots = "";
				//#if polish.log.file.preferredRoot:defined
					while (enumeration.hasMoreElements()) {
						root = (String) enumeration.nextElement();
						roots += root + "; ";
						//#= if  ( root.startsWith( "${polish.log.file.preferredRoot}" )) {
							break;
						//#= }
					}
					
				//#else				
					root = (String) enumeration.nextElement();
				//#endif
				
				//#if polish.log.file.useUnqiueName == true
					url = "file:///" + root + "j2melog" + System.currentTimeMillis() + ".txt";
				//#elif polish.log.file.fileName:defined
					//#= url = "file:///" + root + "${polish.log.file.fileName}";
				//#else
					url = "file:///" + root + "j2melog.txt";
				//#endif
				FileConnection connection = null;
				connection = (FileConnection) Connector.open( url, Connector.READ_WRITE );
				if (!connection.exists()) {
					//System.out.println("Creating file...");
					connection.create();
				}
				//System.out.println("opening data output stream...");
				this.out = new PrintStream( connection.openOutputStream() );
				this.out.println("time\tlevel\tclass\tline\tmessage\terror");
				this.out.println( roots );
			}
		}
		StringBuffer buffer = new StringBuffer();					
		buffer.append( entry.time ).append('\t')
			.append( entry.level ).append('\t')
			.append( entry.className ).append('\t')
			.append( entry.lineNumber ).append('\t')
			.append( entry.message ).append('\t')
			.append( entry.exception );
		this.out.println( buffer.toString() );
		
		//this.out.close();
		//this.out = null;
	}

	public void exit() {
		super.exit();
		if (this.out != null) {
			this.out.close();
			this.out = null;
		}
	}
	
	

}
