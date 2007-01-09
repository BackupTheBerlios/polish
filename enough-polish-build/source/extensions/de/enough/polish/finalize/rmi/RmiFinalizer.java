/*
 * Created on Dec 28, 2006 at 6:53:44 PM.
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
package de.enough.polish.finalize.rmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.JarUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RmiFinalizer extends Finalizer {

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device,
			Locale locale, Environment env) 
	{
		File sourceDir = (File) this.environment.get("rmi-classes-dir" );
	    List rmiClasses = (List) this.environment.get("rmi-classes" );
	    if (rmiClasses != null) {
			File targetJar = new File( device.getJarFile().getParentFile(), "rmi.jar" );
	    	System.out.println("packaging rmi classes to " + targetJar.getAbsolutePath() );
			File[] files = (File[]) rmiClasses.toArray( new File[ rmiClasses.size() ] );
//	    	File[] files = new File[ rmiClasses.size() + 1 ] ;
//	    	for (int i = 0; i < files.length - 1; i++) {
//				files[i] = (File) rmiClasses.get(i);
//			}
//	    	files[ files.length - 1 ] =  new File( this.environment.getProjectHome(), ".polishSettings/obfuscation-map.txt" );
			try {
				JarUtil.jar( files, sourceDir, targetJar, false );
				File obfuscationMapFile = new File( this.environment.getProjectHome(), ".polishSettings/obfuscation-map.txt" );
				if (obfuscationMapFile.exists()) {
					JarUtil.addToJar( obfuscationMapFile, targetJar, null, false );
				}
			} catch (IOException e) {
				BuildException be =  new BuildException("Unable to create " + targetJar.getAbsolutePath() + ": " + e.toString() );
				be.initCause( e );
				throw be;
			}
	    }
	}

}
