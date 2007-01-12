/*
 * Created on 11-Sep-2004 at 10:32:23.
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
package de.enough.polish.resources;

import java.io.File;

import de.enough.polish.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.ant.ConditionalElement;

/**
 * <p>A fileset which can have additional conditions (if and unless attributes).</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourcesFileSet extends FileSet {
	
	private ConditionalElement condition = new ConditionalElement();
	private Environment environment;
	private String dirName; 
	private File resolvedDir;

	/**
	 * Creates a new empty file set.
	 */
	public ResourcesFileSet() {
		super();
	}

	/**
	 * Creates a new file set which inherits all settings from the given parent.
	 * 
	 * @param parent the parent file set.
	 */
	public ResourcesFileSet(FileSet parent) {
		super(parent);
	}
	
	public void setIf( String ifCondition ) {
		this.condition.setIf(ifCondition);
	}
	
	public void setUnless( String unlessCondition ) {
		this.condition.setUnless(unlessCondition);
	}
	
	public String getIf() {
		return this.condition.getIf();
	}

	public String getUnless() {
		return this.condition.getUnless();
	}
	
	public String getCondition() {
		return this.condition.getCondition();
	}

	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param antProject the Ant project into which this variable is embedded
	 * @param env the environment
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator, Project antProject, Environment env) {
		this.environment = env;
		this.resolvedDir = null;
		if (this.condition.isActive(evaluator, antProject)) {
			if (dirExists(antProject)) {
				return true;
			} else {
				System.err.println("Warning: resources <fileset> with dir=\"" + this.dirName + "\" points to the non-existing directory " + this.resolvedDir.getAbsolutePath() );
			}
		}
		return false;
	}	
	

	private boolean dirExists(Project antProject) {
		if (this.resolvedDir == null) {
			resolveDir(antProject);
		}
		return this.resolvedDir.exists();
	}

	private void resolveDir(Project antProject) {
		String realDirName = this.dirName;
		if (this.environment != null) {
			this.resolvedDir = this.environment.resolveFile( realDirName );
		} else if (antProject != null){
			this.resolvedDir = antProject.resolveFile( realDirName );
		} else {
			this.resolvedDir = new File( realDirName );
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.types.AbstractFileSet#setDir(java.io.File)
	 */
	public void setDir(File file) throws BuildException {
		//System.err.println("Warning: setDir( File ) called on ResourceFileSet: " + file.getPath() );
		this.dirName = file.getPath();
		if (this.dirName.indexOf('$') == -1 && !file.exists()) {
			throw new BuildException("A <fileset> \"dir\" attribute points to the non-existing location " + file.getAbsolutePath() + " - please correct this setting in your build.xml script." );
		}
	}


	/* (non-Javadoc)
	 * @see org.apache.tools.ant.types.AbstractFileSet#getDir(org.apache.tools.ant.Project)
	 */
	public File getDir(Project antProject) {
		if (this.resolvedDir == null) {
			resolveDir( antProject );
		}
		return this.resolvedDir;
	}
	
	public File getDir() {
		if (this.resolvedDir == null) {
			resolveDir( null );
		}
		return this.resolvedDir;
	}


	/* (non-Javadoc)
	 * @see org.apache.tools.ant.types.AbstractFileSet#getDirectoryScanner(org.apache.tools.ant.Project)
	 */
	public DirectoryScanner getDirectoryScanner(Project antProject) {
		if (this.resolvedDir == null) {
			resolveDir( antProject );
		}
		super.setDir( this.resolvedDir );
		return super.getDirectoryScanner( antProject );
	}
	
	public void setEnvironment( Environment env ) {
		this.environment = env;
	}

}
