/*
 * Created on 02-Nov-2004 at 15:15:54.
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
package de.enough.polish.jar;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.build.PackageSetting;
import de.enough.polish.preprocess.BooleanEvaluator;

/**
 * <p>Is responsible for packaging files.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Packager {
	
	protected PackageSetting setting;

	/**
	 * Creates a new packager
	 */
	protected Packager() {
		super();
	}
	
	public final static Packager getInstance( PackageSetting setting ) {
		if (setting == null) {
			return new DefaultPackager();
		} else if ( setting.getClassName() != null) {
			Class packagerClass;
			try {
				packagerClass = Class.forName( setting.getClassName() );
			} catch (ClassNotFoundException e) {
				try {
					packagerClass = Class.forName( "de.enough.polish.jar." + setting.getClassName() );
				} catch (ClassNotFoundException e2) {
					throw new BuildException("Unable to load packager-class [" + setting.getClassName() + "]: class not found.");
				} 
			}
			try {
				Packager packager = (Packager) packagerClass.newInstance();
				packager.init(setting);
				return packager;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Unable to initialise the packager: " + e.toString() );
			}
		} else if ( setting.getExecutable() != null) {
			if ( setting.getArguments() == null) {
				throw new BuildException("Please set the \"arguments\"-attribute of the <package>-element.");
			}
			return new ExternalPackager( setting );
		} else {
			return new DefaultPackager();
		}
	}
	
	/**
	 * Initialises this packager.
	 * The default implementation just sets the protected field "setting".
	 * 
	 * @param packageSetting the setting
	 */
	protected void init( PackageSetting packageSetting ) {
		this.setting = packageSetting;
	}
	
	/**
	 * Creates a jar file from all the contents in the given directory.
	 * 
	 * @param sourceDir the directory which contents should be jarred
	 * @param targetFile the target jar-file
	 * @param device the current device
	 * @param evalator the evaluator for conditional parameters
	 * @param variables the variables
	 * @param project the base Ant projects
	 * @throws IOException when the packaging fails
	 * @throws BuildException when the packaging fails for another reason
	 */
	public abstract void createPackage( File sourceDir, File targetFile, Device device, BooleanEvaluator evalator, Map variables, Project project)
	throws IOException, BuildException;

}
