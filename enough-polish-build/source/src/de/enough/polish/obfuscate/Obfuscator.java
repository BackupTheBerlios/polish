/*
 * Created on 22-Feb-2004 at 12:16:08.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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

import de.enough.polish.Device;
import de.enough.polish.LibraryManager;
import de.enough.polish.ant.build.ObfuscatorSetting;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * <p>An obfuscator is used to obfuscate and shrink the code.</p>
 * <p>This class is used to handle any external obfuscator.</p>
 * 
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Obfuscator {
	
	protected Project project;
	protected LibraryManager libraryManager;
	protected File libDir;

	/**
	 * Creates a new obfuscator.
	 */
	public Obfuscator() {
		super();
	}
	
	/**
	 * Obfuscates a jar-file for the given device.
	 * 
	 * @param device The J2ME device
	 * @param sourceFile The jar-file containing the projects classes
	 * @param targetFile The file to which the obfuscated classes should be copied to 
	 * @param preserve All names of classes which should be preserved,
	 *                 that means not renamed or removed.
	 * @param bootClassPath A path to the library containing either the MIDP1.0 or MIDP2.0 environment.
	 * @throws BuildException when the obfuscation failed
	 */
	public abstract void obfuscate( Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath )
	throws BuildException;
	
	/**
	 * Instantiates an obfuscator.
	 * 
	 * @param preferredObfuscator The name of the preferred obfuscator.
	 *        When null is given and no className is specified, 
	 * 		  the GPL ProGuard will be used.
	 * @param setting The obfuscator setting 
	 * @param project the Ant-project to which the obfuscator belongs to
	 * @param libDir the main library directory
	 * @param libraryManager the api manager 
	 * @return The concrete obfuscator.
	 * @throws BuildException when the preferred obfuscator or none obfuscator at all
	 *         could be instantiated.
	 */
	public static final Obfuscator getInstance( ObfuscatorSetting setting, Project project, File libDir, LibraryManager libraryManager )
	throws BuildException
	{
		if (setting.getClassName() != null) {
			try {
				if (setting.getClassPath() == null) {
					Class obfuscatorClass = Class.forName( setting.getClassName() );
					Obfuscator obfuscator = (Obfuscator) obfuscatorClass.newInstance();
					obfuscator.init( project, libDir, libraryManager );
					return obfuscator;
				} else {
					WrapperObfuscator obfuscator = new WrapperObfuscator( setting, project, libDir, libraryManager );
					return obfuscator;
				}
			} catch (BuildException e) {
				throw e;
			} catch (Exception e) {
				throw new BuildException( "Unable to load obfuscator [" + setting.getClassName() + "]: " + e.getMessage(), e );
			}
		}
		if (setting.getName() != null) {
			try {
				String obfuscatorName = setting.getName().substring(0, 1).toUpperCase()
							        + setting.getName().substring( 1 );
				Class obfuscatorClass = Class.forName( 
							"de.enough.polish.obfuscate." 
							+ obfuscatorName + "Obfuscator");
				Obfuscator obfuscator = (Obfuscator) obfuscatorClass.newInstance();
				obfuscator.init( project, libDir, libraryManager );
				return obfuscator;
			} catch (ClassNotFoundException e) {
				throw new BuildException( "Unable to load obfuscator [" + setting.getName() + "]: " + e.getMessage(), e );
			} catch (InstantiationException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + setting.getName() + "]: " + e.getMessage(), e );
			} catch (IllegalAccessException e) {
				throw new BuildException( "Unable to instantiate obfuscator [" + setting.getName() + "]: " + e.getMessage(), e );
			}
		}
		// check for default obfuscators:
		try {
			Class.forName("proguard.ProGuard");
			// okay, proguard found:
			Obfuscator obfuscator = new ProGuardObfuscator();
			obfuscator.init( project, libDir, libraryManager );
			return obfuscator;
		} catch (ClassNotFoundException e) {
			// check for next obfuscator:
		}
		return null;
	}

	/**
	 * Initialises this obfuscator.
	 * The protected field project, libDir and libraryManager are set in the default implementation.
	 * 
	 * @param proj the Ant-project to which the obfuscator belongs to
	 * @param lbDir the main library directory
	 * @param lbManager the api manager 
	 */
	private void init(Project proj, File lbDir, LibraryManager lbManager) {
		this.project = proj;
		this.libDir = lbDir;
		this.libraryManager = lbManager;
	}
}
