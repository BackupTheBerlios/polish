/*
 * Created on 11-Jul-2004 at 21:13:02.
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.LibraryManager;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.util.PopulateUtil;

/**
 * <p>Loads and embeds an obfuscator with a different classpath.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WrapperObfuscator extends Obfuscator {
	
	private Object obfuscator;
	private Method obfuscateMethod;

	/**
	 * Creates a new wrapper for external obfuscators.
	 *  
	 * @param setting the setting of this obfuscator
	 * @param project the Ant project
	 * @param libraryDir the default directory for libraries
	 * @param libraryManager the mananger of libraries 
	 * @throws NoSuchMethodException when the wrapped obfuscator could not be loaded
	 * @throws SecurityException when the wrapped obfuscator could not be loaded
	 * @throws ClassNotFoundException when the wrapped obfuscator could not be loaded
	 * @throws InstantiationException when the wrapped obfuscator could not be loaded
	 * @throws IllegalAccessException when the wrapped obfuscator could not be loaded
	 * @throws InvocationTargetException when the wrapped obfuscator could not be loaded
	 * @throws IllegalArgumentException when the wrapped obfuscator could not be loaded
	 */
	public WrapperObfuscator( ObfuscatorSetting setting, Project project, File libraryDir, LibraryManager libraryManager ) 
	throws SecurityException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		super();
    	AntClassLoader antClassLoader = new AntClassLoader(
    			getClass().getClassLoader(),
    			project,  
				new Path( project ),
				true);
    	antClassLoader.addPathElement( setting.getClassPath().getAbsolutePath() );
    	Class obfuscatorClass = antClassLoader.loadClass( setting.getClassName() ); 
    	this.obfuscator = obfuscatorClass.newInstance();
    	// now init the line processor:
    	Method initMethod = obfuscatorClass.getMethod("init", new Class[]{ Project.class, File.class, LibraryManager.class  } );
    	initMethod.invoke( this.obfuscator, new Object[]{ project, libraryDir, libraryManager } );
    	// retrives processing method:
    	this.obfuscateMethod = obfuscatorClass.getMethod("obfuscate", new Class[]{ Device.class, File.class, File.class, String[].class, Path.class } );
    	// set parameters if there are any:
		if (setting.hasParameters()) {
			PopulateUtil.populate( this.obfuscator, setting.getParameters(), project.getBaseDir() );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) 
	throws BuildException {
		try {
			this.obfuscateMethod.invoke( this.obfuscator, new Object[]{ device, sourceFile, targetFile, preserve, bootClassPath } );
		} catch (BuildException e) {
			throw e;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			if (e.getCause() instanceof BuildException) {
				throw (BuildException) e.getCause();
			} else {
				throw new BuildException( "Unable to obfuscate: " + e.getCause(), e.getCause() );
			}
		} catch (Exception e) {
			throw new BuildException( "Unable to obfuscate: " + e, e );
		}
	}

}
