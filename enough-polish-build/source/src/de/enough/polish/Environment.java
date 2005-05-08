/*
 * Created on 22-Apr-2005 at 16:57:20.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Project;

import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.propertyfunctions.PropertyFunction;
import de.enough.polish.util.StringUtil;

/**
 * <p>Contains all variables, settings, etc not only for the preprocessing, but also for the various other build phases.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Environment {
	
	private final static String PROPERTY_PATTERN_STR = "\\$\\{\\s*[\\w|\\.|\\-|,|\\(|\\)|\\s]+\\s*\\}";
	protected final static Pattern PROPERTY_PATTERN = Pattern.compile( PROPERTY_PATTERN_STR );
	private final static String FUNCTION_PATTERN_STR = "\\w+\\s*\\(\\s*[\\w|\\s|\\.|,]+\\s*\\)";
	protected final static Pattern FUNCTION_PATTERN = Pattern.compile( FUNCTION_PATTERN_STR );
	
	
	private final Map symbols;
	private final Map variables;
	/** holds all temporary defined variables */
	private final HashMap temporaryVariables;
	/** holds all temporary defined symbols */
	private final HashMap temporarySymbols;
	private final ExtensionManager extensionManager;
	private final BooleanEvaluator booleanEvaluator;
	private Locale locale;
	private Device device;
	private final Project antProject;
	private BuildSetting buildSetting;
	private LibraryManager libraryManager;
	

	/**
	 * Creates a new empty environment.
	 * 
	 * @param extensionsManager the manager for extensions
	 * @param antProject the project
	 */
	public Environment( ExtensionManager extensionsManager, Project antProject ) {
		super();
		this.antProject = antProject;
		this.symbols = new HashMap();
		this.variables = new HashMap();
		this.temporarySymbols = new HashMap();
		this.temporaryVariables = new HashMap();
		this.extensionManager = extensionsManager;
		this.booleanEvaluator = new BooleanEvaluator( this );
		this.variables.putAll( antProject.getProperties() );
	}
	
	public void initialize( Device newDevice, Locale newLocale ) {
		this.device = newDevice;
		this.locale = newLocale;
		this.symbols.clear();
		this.symbols.putAll( newDevice.getFeatures() );
		this.variables.clear();
		this.variables.putAll( newDevice.getCapabilities() );
		this.variables.putAll( this.antProject.getProperties() );
	}

	/**
	 * @param features
	 */
	public void setSymbols( Map features ) {
		this.symbols.putAll( features );
	}

	/**
	 * @param capabilities
	 */
	public void setVariables( Map capabilities ) {
		this.variables.putAll( capabilities );
	}
	
	public void clearTemporarySettings() {
		this.temporarySymbols.clear();
		this.temporaryVariables.clear();
	}
	

	/**
	 * @param name
	 * @param value
	 */
	public void addVariable(String name, String value) {
		name = name.toLowerCase();
		this.variables.put( name, value );
		this.symbols.put( name + ":defined", Boolean.TRUE );
		value = value.toLowerCase();
		String[] individualValues = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < individualValues.length; i++) {
			String individualValue = individualValues[i];
			this.symbols.put( name + "." + individualValue, Boolean.TRUE );
		}
	}
	
	public String removeVariable( String name ) {
		name = name.toLowerCase();
		String value = (String) this.variables.remove( name );
		String tempValue = (String) this.temporaryVariables.remove( name );
		if (value == null) {
			value = tempValue;
		}
		if (value != null) {
			this.symbols.remove( name + ":defined" );
			this.temporarySymbols.remove( name + ":defined" );
			String[] individualValues = StringUtil.splitAndTrim( value.toLowerCase(), ',' );
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = name + "." + individualValues[i];
				this.symbols.remove( individualValue );
				this.temporarySymbols.remove( individualValue );
			}
		}
		return value;
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public void setVariable(String name, String value) {
		removeVariable( name );
		addVariable( name, value );
	}

	
	public String removeTemporaryVariable( String name ) {
		name = name.toLowerCase();
		String value = (String) this.temporaryVariables.remove( name );
		if (value != null) {
			this.temporarySymbols.remove( name + ":defined" );
			String[] individualValues = StringUtil.splitAndTrim( value.toLowerCase(), ',' );
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = name + "." + individualValues[i];
				this.temporarySymbols.remove( individualValue );
			}
		}
		return value;
	}

	
	public String getVariable( String name ) {
		name = name.toLowerCase();
		String value = (String) this.variables.get( name );
		if (value == null) {
			value = (String) this.temporaryVariables.get( name );
		}
		return value;
	}
	
	public void addTemporaryVariable( String name, String value ) {
		name = name.toLowerCase();
		this.temporaryVariables.put( name, value );
		this.temporarySymbols.put( name + ":defined", Boolean.TRUE );
		value = value.toLowerCase();
		String[] individualValues = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < individualValues.length; i++) {
			String individualValue = individualValues[i];
			this.temporarySymbols.put( name + "." + individualValue, Boolean.TRUE );
		}
	}
	

	/**
	 * @param name
	 */
	public void addSymbol( String name ) {
		name = name.toLowerCase();
		this.symbols.put( name, Boolean.TRUE );
	}
	
	public boolean removeSymbol( String name ) {
		name = name.toLowerCase();
		return ((this.symbols.remove( name ) != null) 
		       | (this.temporarySymbols.remove( name ) != null) );
	}
	
	public boolean hasSymbol( String name ) {
		name = name.toLowerCase();
		//System.out.println( "Environment: hasSymbol(" + name + ") = " + ( this.symbols.get(name) != null ) );
		return ( this.symbols.get(name) != null 
				|| this.temporarySymbols.get( name ) != null );
	}
	
	public void addTemporarySymbol( String name ) {
		name = name.toLowerCase();
		this.temporarySymbols.put( name, Boolean.TRUE );
	}
	
	public boolean removeTemporarySymbol( String name ) {
		name = name.toLowerCase();
		return (this.temporarySymbols.remove( name ) != null);
	}
	
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined the full property-name is inserted instead (e.g. "${ property-name }").  
	 */
	public String writeProperties( String input ) {
		return writeProperties( input, false );
	}
	
	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @param needsToBeDefined true when an IllegalArgumentException should be thrown when
	 *              no value for a property was found.
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined (and needsToBeDefined is false),
	 *             the full property-name is inserted instead (e.g. "${ property-name }").  
	 * @throws IllegalArgumentException when a property-value was not found and needsToBeDefined is true.
	 */
	public String writeProperties( String input, boolean needsToBeDefined ) {	
		Matcher matcher = PROPERTY_PATTERN.matcher( input );
		boolean propertyFound = matcher.find();
		if (!propertyFound) {
			return input;
		}
		/*
		StringBuffer buffer = new StringBuffer();
		int startPos = 0;
		*/
		String lastGroup = null;
		while (propertyFound) {
			// append string til start of the pattern:
			// buffer.append( input.substring( startPos, matcher.start() ) );
			// startPos = matcher.end();
			// append property:
			String group = matcher.group(); // == ${ property.name }
											// or == ${ function( property.name ) }
											// or == ${ function( fix.value ) }
			if (group == lastGroup) {
				if (matcher.find()) {
					group = matcher.group();
				} else {
					break;
				}
			}
			lastGroup = group;
			
			String property = group.substring( 2, group.length() -1 ).trim(); // == property.name
			
			String value = getProperty( property, needsToBeDefined );
			if (value != null) {
				input = StringUtil.replace( input, group, value );
				matcher = PROPERTY_PATTERN.matcher( input );
			}
			propertyFound = matcher.find();
		} 
		/*
			// the property-name can also include a convert-function, e.g. bytes( polish.HeapSize )
			int functionStart = property.indexOf('(');
			if (functionStart != -1) {
				int functionEnd = property.indexOf(')', functionStart);
				if (functionEnd == -1) {
					throw new IllegalArgumentException("The function [" + property + "] needs a closing paranthesis in input [" + input + "].");
				}
				String functionName = property.substring(0, functionStart).trim();
				property = property.substring( functionStart + 1, functionEnd ).trim();
				String originalValue = getVariable( property );
				if (originalValue == null) {
					// when functions are used, fix values can be used, too: 
					originalValue = property;
				}
				
				Object intermediateValue = ConvertUtil.convert( originalValue, functionName, this.variables);
				value = ConvertUtil.toString(intermediateValue);
			} else {
				value = getVariable( property );
			}
			if (value == null) {
				if (needsToBeDefined) {
					throw new IllegalArgumentException("property " + group + " is not defined.");
				} else {
					value = group;
				}
			} else {
				if ( value.indexOf("${") != -1) {
					Matcher valueMatcher = PROPERTY_PATTERN.matcher( value );
					while ( valueMatcher.find() ) {
						String internalGroup = valueMatcher.group();
						String internalProperty = internalGroup.substring( 2, internalGroup.length() -1 ).trim(); // == property.name
						String internalValue = getVariable( internalProperty );
						if (internalValue != null) {
							value = StringUtil.replace( value, internalGroup, internalValue);
						} else if ( needsToBeDefined ) {
							throw new IllegalArgumentException("property " + internalGroup + " is not defined.");
						}
					}
					
				}
			}
			buffer.append( value );
			// look for another property:
			propertyFound = matcher.find();
		}
		// append tail:
		buffer.append( input.substring( startPos ) );
		return buffer.toString();
		*/ 
		return input;
	}

	/**
	 * @param property
	 * @param needsToBeDefined
	 * @return
	 */
	private String getProperty(String property, boolean needsToBeDefined) {
		if (property.indexOf('(') == -1) {
			// the property does not contain a property-function:
			String value = getVariable( property );
			if (value == null) {
				if (needsToBeDefined) {
					throw new IllegalArgumentException("The property [" + property + "] is not defined.");
				} else {
					return null;
				}
			} else {
				property = value;
			}
		} else {
			// the property contains a property-function:
			Matcher matcher = FUNCTION_PATTERN.matcher( property );
			while ( matcher.find() ) {
				String group = matcher.group(); // == function ( propertyname [, arg, arg, ...] )
				int propertyNameStart = group.indexOf('(');
				int propertyNameEnd = group.indexOf(',');
				boolean hasParameters = true;
				if (propertyNameEnd == -1 ) {
					propertyNameEnd = group.length() - 1;
					hasParameters = false;
				}
				String propertyName = group.substring( propertyNameStart + 1, propertyNameEnd ).trim();
				String propertyValue = getVariable( propertyName );
				if (propertyValue == null ) {
					if (needsToBeDefined) {
						throw new IllegalArgumentException("The property [" + propertyName + "] is not defined.");
					} else {
						propertyValue = propertyName;
					}
				}
				String[] parameters = null;
				if ( hasParameters ) {
					String parametersStr = property.substring( propertyNameEnd + 1, property.length() -1 ).trim();
					parameters = StringUtil.splitAndTrim( parametersStr, ',' );
				}
				String functionName = group.substring( 0, propertyNameStart ).trim();
				PropertyFunction function = null;
				try {
					function = (PropertyFunction) this.extensionManager.getExtension( ExtensionManager.TYPE_PROPERTY_FUNCTION, functionName, this );
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("The property function [" + functionName + "] could not be loaded. Please register it in custom-extensions.xml.");
				}
				if (function == null) {
					throw new IllegalArgumentException("The property function [" + functionName + "] is not known. Please register it in custom-extensions.xml.");
				}
				try {
					String replacement = function.process(propertyValue, parameters, this);
					property = StringUtil.replace( property, group, replacement );
					matcher = FUNCTION_PATTERN.matcher( property );
				} catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println("Unable to process function [" + functionName + "] on value [" + propertyValue + "]: " + e.toString() );
					throw e;
				}
			}
		}
		return property;
	}

	/**
	 * @param locale the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public BooleanEvaluator getBooleanEvaluator() {
		return this.booleanEvaluator;
	}

	/**
	 * @return
	 */
	public Map getVariables() {
		return this.variables;
	}

	/**
	 * @param additionalSymbols
	 */
	public void addSymbols(Map additionalSymbols) {
		this.symbols.putAll( additionalSymbols );
	}
	
	public Device getDevice() {
		return this.device;
	}

	/**
	 * @param vars
	 */
	public void addVariables(Map vars) {
		for (Iterator iter = vars.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = (String) vars.get( name );
			addVariable( name, value );
		}
	}

	/**
	 * @return
	 */
	public Project getProject() {
		return this.antProject;
	}

	/**
	 * Resolves the path to a file.
	 * 
	 * @param url the filepath that can contain properties such as ${polish.home}.
	 * @return the appropriate file. Please note that the file doesn't need to exist,
	 *         call file.exists() for determining that.
	 */
	public File resolveFile(String url) {
		url = writeProperties( url );
		File file = new File( url );
		if (!file.isAbsolute()) {
			file = new File( this.antProject.getBaseDir(), url );
		}
		return file;
	}

	/**
	 * @param setting
	 */
	public void setBuildSetting(BuildSetting setting) {
		this.buildSetting = setting;
	}
	
	public BuildSetting getBuildSetting() {
		return this.buildSetting;
	}
	
	public void setLibraryManager( LibraryManager manager ) {
		this.libraryManager = manager;
	}
	
	public LibraryManager getLibraryManager() {
		return this.libraryManager;
	}

	
}
