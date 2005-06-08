/*
 * Created on 22-Apr-2005 at 18:54:42.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.exceptions.InvalidComponentException;


/**
 * <p>Manages the available extensions.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionManager {
	
	public static final String TYPE_PLUGIN = "plugin";
	public static final String TYPE_PROPERTY_FUNCTION = "propertyfunction";
	public static final String TYPE_PREPROCESSOR = "preprocessor";
	public static final String TYPE_POSTCOMPILER = "postcompiler";
	public static final String TYPE_OBFUSCATOR = "obfuscator";
	public static final String TYPE_RESOURCE_COPIER = "resourcecopier";
	public static final String TYPE_PACKAGER = "packager";
	public static final String TYPE_FINALIZER = "finalizer";
	public static final String TYPE_LOG_HANDLER = "loghandler";
	
	
	private final Map definitionsByType;
	private final Map extensionsByType;
	private final Map typesByName;
	private final List instantiatedExtensions;
	private final List instantiatedPlugins;
	private final Project antProject;
	
	private Extension[] activeExtensions;
	private final List autoStartExtensions;

	/**
	 * @param antProject
	 * @param is
	 * @throws IOException
	 * @throws JDOMException
	 * 
	 */
	public ExtensionManager( Project antProject, InputStream is ) throws JDOMException, IOException {
		super();
		this.antProject = antProject;
		this.definitionsByType = new HashMap();
		this.extensionsByType = new HashMap();
		this.typesByName = new HashMap();
		this.instantiatedExtensions = new ArrayList();
		this.instantiatedPlugins = new ArrayList();
		this.autoStartExtensions = new ArrayList();
		loadDefinitions( is );
		is.close();
	}
	
	/**
	 * Loads custom extensions, when there are any.
	 * 
	 * @param customExtensionsFile the file that contains custom extensions
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDefinitions(File customExtensionsFile ) 
	throws JDOMException, InvalidComponentException {
		if (customExtensionsFile.exists()) {
			try {
				loadDefinitions( new FileInputStream( customExtensionsFile ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-extensions.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-extensions.xml]: " + e.toString() );
				e.printStackTrace();
			}
		}
	}


	/**
	 * Loads the definitions from the given input stream.
	 * 
	 * @param is the input stream, usually from extensions.xml or custom-extensions.xml
	 * @throws JDOMException when the XML is not wellformed
	 * @throws IOException when the input stream could not be read
	 */
	private void loadDefinitions( InputStream is ) 
	throws JDOMException, IOException 
	{
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		// load type-definitions:
		List xmlList = document.getRootElement().getChildren("typedefinition");
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			ExtensionTypeDefinition type = new ExtensionTypeDefinition( element );
			this.typesByName.put( type.getName(), type );
		}
		
		// load the actual extension-definitions:
		xmlList = document.getRootElement().getChildren("extension");
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			try {
				ExtensionDefinition definition = new ExtensionDefinition( element, this.antProject, this );
				Map store = (Map) this.definitionsByType.get( definition.getType() );
				if ( store == null ) {
					store = new HashMap();
					this.definitionsByType.put( definition.getType(), store );
				}
				store.put( definition.getName(), definition );
				if ( definition.getAutoStartCondition() != null ) {
					this.autoStartExtensions.add( definition );
				}
			} catch (Exception e) {
				System.out.println("Unable to load extension [" + element.getChildTextTrim("class") + "]: " + e.toString() );
			}
		}
	}
	
	/**
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @return the extension, null when the type or the name is not known
	 * @param environment the environment settings
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension(String type, ExtensionSetting setting, Environment environment) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		String name = setting.getName();
		if (name == null) {
			name = setting.getClassName();
			if (name == null) {
				throw new IllegalArgumentException("Unable to load extension without name or class-setting. Please check your build.xml file.");
			}
		}
		return getExtension(type, name, setting, environment);
	}
	
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension( String type, String name, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		return getExtension( type, name, null, environment );
	}
	
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension( String type, String name, ExtensionSetting setting, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		Map store = (Map) this.extensionsByType.get( type );
		if (store != null) {
			Extension extension = (Extension) store.get( name );
			if (extension != null) {
				return extension;
			}
		}
		// this extension has not been instantiated so far,
		// so do it now:
		ExtensionDefinition definition = getDefinition( type, name );
		ExtensionTypeDefinition typeDefinition = getTypeDefinition( type );
		Extension extension = Extension.getInstance( typeDefinition, definition, setting, this.antProject, this, environment );
		if (store == null) {
			store = new HashMap();
			this.extensionsByType.put( type, store );
		}
		this.instantiatedExtensions.add( extension );
		store.put( name, extension );
		return extension;
	}
	
	/**
	 * @param type
	 * @return
	 */
	public ExtensionTypeDefinition getTypeDefinition(String type) {
		return (ExtensionTypeDefinition) this.typesByName.get( type );
	}

	/**
	 * Retrieves the defnition of the specified extension
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @return the definition of the extension
	 */
	public ExtensionDefinition getDefinition( String type, String name ) {
		Map store = (Map) this.definitionsByType.get( type );
		if ( store == null ) {
			return null;
		} else {
			return (ExtensionDefinition) store.get( name );
		}
	}
	
	public void registerExtension( String type, Extension extension ) {
		Map store = (Map) this.extensionsByType.get( type );
		if (store == null) {
			store = new HashMap();
			this.extensionsByType.put( type, store );
		}
		store.put( extension.toString(), extension );
		this.instantiatedExtensions.add( extension );
	}
	
	
	public void preInitialize( Device device, Locale locale ) {
		// call preInitialize on the registered plugins:
	}
	
	public void initialize( Device device, Locale locale, Environment environment ) {
		// find out active extensions:
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		ArrayList activeList = new ArrayList();
		for (Iterator iter = this.instantiatedExtensions.iterator(); iter.hasNext();) {
			Extension extension = (Extension) iter.next();
			ExtensionSetting setting = extension.getExtensionSetting();
			if ( setting == null || setting.isActive(evaluator, this.antProject) ) {
				activeList.add( extension );
				// call initialize on all active extensions:
				extension.initialize(device, locale, environment);
			}
		}
		this.activeExtensions = (Extension[]) activeList.toArray( new Extension[ activeList.size() ] );
	}
	
	public void postInitialize( Device device, Locale locale, Environment environment ) {
		// call preInitialize on the registered plugins:
	}
	
	public void postCompile( Device device, Locale locale, Environment environment ) {
		Extension[] extensions = getExtensions( TYPE_POSTCOMPILER, device, locale, environment );
		for (int i = 0; i < extensions.length; i++) {
			Extension extension = extensions[i];
			extension.execute( device, locale, environment );
		}
	}
	
	/**
	 * @param type
	 * @param device
	 * @param locale
	 * @param environment
	 * @return an array of extensions that should be started automatically
	 */
	private Extension[] getExtensions(String type, Device device, Locale locale, Environment environment) {
		ArrayList list = new ArrayList();
		for (Iterator iter = this.autoStartExtensions.iterator(); iter.hasNext();) {
			ExtensionDefinition definition = (ExtensionDefinition) iter.next();
			if ( type.equals( definition.getType()) && definition.isConditionFulfilled( environment )) {
				try {
					list.add( getExtension( type, definition.getName(), environment ) );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (Extension[]) list.toArray( new Extension[ list.size() ] );
	}

	public void preFinalize( Device device, Locale locale, Environment environment ) {
		// call preInitialize on the registered plugins:
	}
	
	public void finalize( Device device, Locale locale, Environment environment ) {
		// call initialize on all active extensions:
		for (int i = 0; i < this.activeExtensions.length; i++) {
			Extension extension = this.activeExtensions[i];
			extension.finalize(device, locale, environment);
		}
	}
	
	public void postFinalize( Device device, Locale locale, Environment environment ) {
		// call preInitialize on the registered plugins:
	}

	/**
	 * @param type
	 * @param environment
	 * @return
	 */
	public Extension getActiveExtension(String type, Environment environment) {
		Map store = (Map) this.extensionsByType.get( type );
		if (store == null) {
			return null;
		}
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		Object[] extensions = store.values().toArray();
		for (int i = 0; i < extensions.length; i++) {
			Extension extension = (Extension) extensions[i];
			ExtensionSetting setting = extension.getExtensionSetting();
			if (setting == null || setting.isActive(evaluator, this.antProject)) {
				return extension;
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @param settings
	 * @param environment
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public void registerExtensions(String type, ExtensionSetting[] settings, Environment environment) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		if (settings == null) {
			return;
		}
		for (int i = 0; i < settings.length; i++) {
			ExtensionSetting setting = settings[i];
			getExtension(type, setting, environment);
		}
	}

	
	

}
