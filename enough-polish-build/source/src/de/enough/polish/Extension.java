/*
 * Created on 04-Apr-2005 at 14:55:10.
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
package de.enough.polish;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.jdom.Element;

import de.enough.polish.ant.ExtensionSetting;
import de.enough.polish.util.PopulateUtil;

/**
 * <p>Provides the common base for any extensions of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Extension {

	protected ExtensionSetting extensionSetting;
	protected Project antProject;
	private Element xmlConfiguration;
	private ExtensionManager extensionManager;
	private String type;
	private String name;

	/**
	 * Creates a new extension.
	 */
	public Extension() {
		super();
	}
	
	/**
	 * Initialises this extension.
	 * 
	 * @param setting the extension settings
	 * @param project the ant project
	 */
	protected void init(ExtensionSetting setting, Project project) {
		this.extensionSetting = setting;
		this.antProject = project;
	}
	
	/**
	 * Initialises this extension.
	 * 
	 * @param element the extension settings taken from extensions.xml or custom-extensions.xml 
	 * @param project the ant project
	 */
	protected void init(Element element, Project project, ExtensionManager manager) {
		this.xmlConfiguration = element;
		this.antProject = project;
		this.extensionManager= manager;
		this.type = element.getChildTextTrim( "type" );
		if (this.type == null) {
			throw new IllegalArgumentException("The extension has no type.");
		}
		this.name = element.getChildTextTrim( "name" );
		if (this.name == null) {
			throw new IllegalArgumentException("The extension has no name.");
		}
	}
	
	
	public ExtensionSetting getExtensionSetting() {
		return this.extensionSetting;
	}
	
	public Project getAntProject() {
		return this.antProject;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static Extension getInstance( ExtensionSetting setting, Project antProject ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		ClassLoader classLoader;
		Path classPath = setting.getClassPath();
		if (classPath == null) {
			classLoader = setting.getClass().getClassLoader();
		} else {
			classLoader = new AntClassLoader(
    			setting.getClass().getClassLoader(),
    			antProject,  
				classPath,
				true);
		}
		Class extensionClass = classLoader.loadClass( setting.getClassName() );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( setting, antProject );
		if (setting.hasParameters()) {
			PopulateUtil.populate( extension, setting.getParameters(), antProject.getBaseDir() );
		}
		return extension;
	}
	
	public static Extension getInstance( Element element, Project antProject, ExtensionManager manager ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		
		ClassLoader classLoader;
		String classPathStr = element.getChildTextTrim("classpath");
		if (classPathStr == null) {
			classLoader = Extension.class.getClassLoader();
		} else {
			Path classPath = new Path( antProject, classPathStr );
			classLoader = new AntClassLoader(
				Extension.class.getClassLoader(),
    			antProject,  
				classPath,
				true);
		}
		String className = element.getChildTextTrim("class");
		Class extensionClass = classLoader.loadClass( className );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( element, antProject, manager );
		return extension;
	}


}
