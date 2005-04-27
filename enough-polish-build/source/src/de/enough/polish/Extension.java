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

import java.util.Locale;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

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
public abstract class Extension {

	protected ExtensionSetting extensionSetting;
	protected Project antProject;
	protected ExtensionManager extensionManager;
	protected ExtensionDefinition extensionDefinition;
	protected ExtensionTypeDefinition extensionTypeDefinition;

	/**
	 * Creates a new extension.
	 */
	public Extension() {
		super();
	}
	
	/**
	 * Initialises this extension.
	 * 
	 * @param typeDefinition the definition of the base type, can be null
	 * @param definition the extension definition taken from extensions.xml or custom-extensions.xml 
	 * @param setting the extension settings
	 * @param project the ant project
	 */
	protected void init(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Project project, ExtensionManager manager) {
		this.extensionDefinition = definition;
		this.extensionSetting = setting;
		this.antProject = project;
		this.extensionManager= manager;
		this.extensionTypeDefinition = typeDefinition;
	}
	
	public ExtensionSetting getExtensionSetting() {
		return this.extensionSetting;
	}

	public ExtensionDefinition getExtensionDefinition() {
		return this.extensionDefinition;
	}

	public Project getAntProject() {
		return this.antProject;
	}
	
	/**
	 * Initializes this extension for a new device or a new locale.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param environment the environment/configuration
	 */
	public void intialize( Device device, Locale locale, Environment environment ) {
		// default implementation does nothing
	}
	
	/**
	 * Finalizes  this extension for a the device and locale.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param environment the environment/configuration
	 */
	public void finalize( Device device, Locale locale, Environment environment ) {
		// default implementation does nothing
	}
	
	/**
	 * Executes this extension. Not all extension types are executed.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param environment the environment/configuration
	 * @throws BuildException when the execution failed
	 */
	public abstract void execute( Device device, Locale locale, Environment environment )
	throws BuildException;
	
	/**
	 * Retrieves a parameter value from either the setting, the definition or the type definition of this extension.
	 * 
	 * @param parameterName the name of the parameter
	 * @param environment the environment, can be null
	 * @return either the value or null of the parameter is not defined anywhere
	 */
	public String getParameterValue( String parameterName, Environment environment ) {
		if (this.extensionSetting != null) {
			Variable parameter = this.extensionSetting.getParameter(parameterName, environment);
			if (parameter != null) {
				return parameter.getValue();
			}
		}
		if (this.extensionDefinition != null) {
			String value = this.extensionDefinition.getParameterValue(parameterName);
			if (value != null) {
				return value;
			}
		}
		if (this.extensionTypeDefinition != null) {
			String value = this.extensionTypeDefinition.getParameterValue(parameterName);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	
	/*
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
	
	public static Extension getInstance( ExtensionDefinition definition, Project antProject, ExtensionManager manager ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		
		ClassLoader classLoader;
		String classPathStr = definition.getClassPath();
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
		String className = definition.getClassName();
		Class extensionClass = classLoader.loadClass( className );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( definition, antProject, manager );
		return extension;
	}
	*/

	/**
	 * Instantiates the specified exception.
	 * At least one of the given definition of setting parameters must not be null.
	 * 
	 * @param typeDefinition the definition of the type, can be null
	 * @param definition the definition taken from extensions.xml or custom-extensions.xml
	 * @param setting the configuration taken from build.xml
	 * @param antProject the Ant project
	 * @param manager the extension manager
	 * @param environment the environment settings
	 * @return the configured extension.
	 * @throws ClassNotFoundException when the class was not found 
	 * @throws InstantiationException when the class could not get instantiated
	 * @throws IllegalAccessException when the class could not be accessed
	 * @throws IllegalArgumentException when both definition and setting are null or when no class has been defined anywhere.
	 */
	public static Extension getInstance(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Project antProject, ExtensionManager manager, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		String extensionType;
		if (typeDefinition != null) {
			extensionType = typeDefinition.getName() + "-extension";
		} else {
			extensionType = "extension";
		}
		if (definition == null && setting == null) {
			throw new IllegalArgumentException("Cannot instantiate " + extensionType + " without definition and without configuration-setting!");
		}
		String className = getClassName( typeDefinition, definition, setting );
		String name = getName( definition, setting );
		if (className == null) {
			throw new IllegalArgumentException("Unable to instantiate " + extensionType + " [" + name + "]: no class has been defined in neither extensions.xml, custom-extensions.xml or the build.xml.");
		}
		if ( name == null ) {
			name = className;
		}
		Path classPath = getClassPath( antProject, typeDefinition, definition, setting, environment );
		ClassLoader classLoader;
		if (setting != null) {
			classLoader = setting.getClass().getClassLoader();
		} else {
			classLoader = definition.getClass().getClassLoader();
		}
		if (classPath != null) {
			classLoader = new AntClassLoader(
    			classLoader,
    			antProject,  
				classPath,
				true);
		}
		Class extensionClass = classLoader.loadClass( className );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( typeDefinition, definition, setting, antProject, manager );
		if (setting != null && setting.hasParameters()) {
			PopulateUtil.populate( extension, setting.getParameters(), antProject.getBaseDir() );
		}
		return extension;
	}

	/**
	 * @param definition
	 * @param setting
	 * @return
	 */
	private static String getName(ExtensionDefinition definition, ExtensionSetting setting) {
		String name = null;
		if (setting != null) {
			name = setting.getName();
		}
		if ( name == null && definition != null) {
			name = definition.getName();
		}
		return name;
	}

	/**
	 * @param typeDefinition
	 * @param definition
	 * @param setting
	 * @param environment
	 * @return
	 */
	private static Path getClassPath( Project antProject, ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Environment environment) {
		Path classPath = null;
		if (setting != null) {
			classPath = setting.getClassPath();
		}
		if (classPath == null && definition != null && definition.getClassPath() != null ) {
			classPath = new Path( antProject, environment.writeProperties( definition.getClassPath() ) );
		}
		if (classPath == null && typeDefinition != null && typeDefinition.getDefaultClassPath() != null ) {
			classPath = new Path( antProject, environment.writeProperties( typeDefinition.getDefaultClassPath() ) );
		}
		return classPath;
	}

	/**
	 * @param typeDefinition
	 * @param definition
	 * @param setting
	 * @return
	 */
	private static String getClassName(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting) {
		String className = null;
		if (setting != null) {
			className = setting.getClassName();
		}
		if (className == null && definition != null) {
			className = definition.getClassName();
		}
		if (className == null && typeDefinition != null) {
			className = typeDefinition.getDefaultClassName();
		}
		return className;
	}


}
