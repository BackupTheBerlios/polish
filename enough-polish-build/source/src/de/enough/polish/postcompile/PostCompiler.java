/*
 * Created on 24-Mar-2005 at 15:44:24.
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
package de.enough.polish.postcompile;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.build.PostCompilerSetting;
import de.enough.polish.util.PopulateUtil;

/**
 * <p>Is the base class for any custom post compilers.</p>
 * <p>
 * Custom post compilers can modify the bytecode of an application before the 
 * application is obfuscated and then preverified.
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class PostCompiler {

	protected PostCompilerSetting setting;
	protected Project project;

	/**
	 * Creates a new instance of a custom post compiler.
	 */
	protected PostCompiler() {
		super();
	}
	
	/**
	 * Sets the basic settings of this post compiler
	 * 
	 * @param postCompilerSetting the ant settings
	 * @param antProject the current ant project
	 */
	public void init( PostCompilerSetting postCompilerSetting, Project antProject ) {
		this.setting = postCompilerSetting;
		this.project = antProject;
	}
	
	/**
	 * Retrieves a new PostCompiler
	 *  
	 * @param postCompilerSetting the ant settings
	 * @param antProject the current ant project
	 * @return a new instance of a post compiler
	 * @throws BuildException when the class could not be loaded or initialized
	 */
	public static PostCompiler getInstance( PostCompilerSetting postCompilerSetting, Project antProject )
	throws BuildException
	{
		try {
			Class postCompilerClass = Class.forName( postCompilerSetting.getClassName() );
			PostCompiler postCompiler = (PostCompiler) postCompilerClass.newInstance();
			postCompiler.init(postCompilerSetting, antProject);
			if (postCompilerSetting.hasParameters()) {
				PopulateUtil.populate( postCompiler, postCompilerSetting.getParameters(), antProject.getBaseDir() );
			}
			return postCompiler;
		} catch (ClassNotFoundException e) {
			throw new BuildException("Unable to load post compiler class [" + postCompilerSetting.getClassName() + "]: " + e.toString(), e );
		} catch (InstantiationException e) {
			throw new BuildException("Unable to intialize post compiler class [" + postCompilerSetting.getClassName() + "]: " + e.toString(), e );
		} catch (IllegalAccessException e) {
			throw new BuildException("Unable to access post compiler class [" + postCompilerSetting.getClassName() + "]: " + e.toString(), e );
		}
	}
	
	/**
	 * Postcompiles the project for the given target device.
	 * 
	 * @param classesDir the directory that contains all compiled classes
	 * @param device the current target device
	 * @throws BuildException when post-compiling fails
	 */
	public abstract void postCompile( File classesDir, Device device )
	throws BuildException;
	
	
	/**
	 * Subclasses can override this method for setting a different bootclasspath for the current device.
	 * This method is called before the application is compiled.
	 * The default implementation returns the specified bootclasspath.
	 * When several postcompilers try to change this path it can result in complications.
	 * 
	 * @param device the current device
	 * @param bootClassPath the current bootclasspath
	 * @return the appropriate bootclasspath for the current device, usually the same that has been given.
	 */
	public String verifyBootClassPath( Device device, String bootClassPath ) {
		return bootClassPath;
	}
	
	/**
	 * Subclasses can override this method for setting a different classpath for the current device.
	 * This method is called before the application is compiled.
	 * The default implementation returns the specified classpath.
	 * 
	 * @param device the current device
	 * @param classPath the current classpath
	 * @return the appropriate classpath for the current device, usually the same that has been given.
	 */
	public String verifyClassPath( Device device, String classPath ) {
		return classPath;
	}

	
	/**
	 * Retrieves the settings for this post compiler.
	 * 
	 * @return the settings
	 */
	public PostCompilerSetting getSetting() {
		return this.setting;
	}

}
