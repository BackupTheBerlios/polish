/*
 * Created on 28-Apr-2005 at 16:33:34.
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
package de.enough.polish.jar;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Packages files using the kzip packager.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class KZipPackager 
extends Packager
implements OutputFilter
{


	/**
	 * Creates a new packager 
	 */
	public KZipPackager() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment environment) 
	throws IOException, BuildException 
	{
		File executable = environment.resolveFile("${polish.home}/bin/kzip.exe");
		if (!executable.exists()) {
			executable = environment.resolveFile("${kzip.home}/kzip.exe");
		}
		ArrayList arguments = new ArrayList();
		if ( File.separatorChar != '\\' ) {
			// use wine on Unix environments:
			arguments.add("wine");
			arguments.add("--");
		}
		if (executable.exists()) {
			arguments.add( executable.getAbsolutePath() );
		} else  {
			arguments.add( "kzip.exe" );
		}
		File tempFile = new File( "kziptemp.ZIP" );
		arguments.add( "kziptemp.ZIP" ); // target JAR file
		arguments.add("/y"); // overwrite existing files
		arguments.add("/r"); // recursive
		arguments.add( "*.*" ); // source directory
		System.out.println(arguments);
		int result = ProcessUtil.exec( arguments, "kzip: ", true, this, sourceDir );
		if (result != 0) {
			throw new BuildException("Unable to create [" + targetFile.getAbsolutePath() + "]: kzip returned [" + result + "].");
		}
		// now rename temporary file:
		tempFile.renameTo(targetFile);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		//if (message.indexOf("Adding") == -1) {
			output.println( message );
		//}
	}
	
}
