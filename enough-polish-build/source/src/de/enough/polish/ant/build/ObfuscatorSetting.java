/*
 * Created on 23-Feb-2004 at 14:24:04.
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
package de.enough.polish.ant.build;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

import de.enough.polish.ant.Setting;

/**
 * <p>Containts information about the obfuscator which should be used.</p>
 * <p>Can be used for a more detailed setting than just using the 
 * &lt;build&gt;attributes "obfuscator" and "obfuscate".</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        23-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ObfuscatorSetting 
extends Setting 
{
	
	private ArrayList keeps;
	private boolean enable;
	private String name;
	private String className;
	private File classPath;
	private boolean useDefaultPackage;
	private boolean renameMidlets;

	/**
	 * Creates a new empty obfuscator setting. 
	 */
	public ObfuscatorSetting() {
		this.keeps = new ArrayList();
	}
	
	public void addConfiguredKeep( Keep keep ) {
		if (keep.getClassName() == null) {
			throw new BuildException("The <keep> element needs to define the attribute [class]. Please check your <obfuscator> setting.");
		}
		this.keeps.add( keep );
	}
	
	public void addConfiguredPreserve( Keep keep ) {
		addConfiguredKeep(keep);
	}
	
	public void setEnable( boolean enable ) {
		this.enable = enable;
	}
	
	public boolean isEnabled() {
		return this.enable;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setClass( String className ) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public void setClassPath( File classPath ) {
		this.classPath = classPath;
	}
	
	public File getClassPath() {
		return this.classPath;
	}
	
	public boolean hasKeepDefinitions() {
		return (this.keeps.size() > 0);
	}
	
	/**
	 * Retrieves the names of classes which should not be obfuscated.
	 * 
	 * @return An array with the names of classes which should not be obfuscated.
	 */
	public String[] getPreserveClassNames() {
		Keep[] keepDefinitions = (Keep[]) this.keeps.toArray( new Keep[ this.keeps.size() ] );
		String[] preserves = new String[ keepDefinitions.length ];
		for (int i = 0; i < keepDefinitions.length; i++) {
			Keep keep = keepDefinitions[i];
			preserves[i] = keep.getClassName();
		}
		return preserves;
	}

	/**
	 * Checks the settings of this obfuscator.
	 * This method distinguishes between the different ProGuard-versions.
	 * 
	 * @param parent the parent <build> element.
	 */
	public void checkSettings(BuildSetting parent) {
		if ("ProGuard".equals( this.name )) {
			// check if ProGuard 3.x is available:
			try {
				Class.forName("proguard.ClassSpecification");
			} catch (ClassNotFoundException e) {
				// only ProGuard 2 is available:
				this.name = "ProGuard2";
			}
		}
	}

	/**
	 * Determines whether the MIDlet classes should be renamed to short "A", "B", "C" etc.
	 * 
	 * @return Returns true when the midlet-classes should be renamed.
	 */
	public boolean renameMidlets() {
		return this.renameMidlets;
	}
	
	/**
	 * Sets whether the midlet-classes should be renamed
	 * 
	 * @param renameMidlets true when the midlet-classes should be renamed.
	 */
	public void setRenameMidlets(boolean renameMidlets) {
		this.renameMidlets = renameMidlets;
	}
	
	/**
	 * Determines whether all classes should be moved into the default package ("").
	 * 
	 * @return true when all classes should be moved into the default package.
	 */
	public boolean useDefaultPackage() {
		return this.useDefaultPackage;
	}
	
	/**
	 * Sets whether all classes should be moved into the default package ("").
	 * 
	 * @param useDefaultPackage true when all classes should be moved into the default package.
	 */
	public void setUseDefaultPackage(boolean useDefaultPackage) {
		this.useDefaultPackage = useDefaultPackage;
	}
}
