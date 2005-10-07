/*
 * Created on 12-Sep-2004 at 13:10:19.
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


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.StringList;

/**
 * <p>Manages translations.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        12-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationManager
implements Comparator
{
	public static final String ENVIRONMENT_KEY = "polish.TranslationManager";
	protected final Map translationsByKey;
	protected final Map preprocessingVariablesByKey;
	protected final Locale locale;
	protected final Device device;
	protected final LocalizationSetting localizationSetting;
	protected long lastModificationTime;
	protected final List multipleParametersTranslations;
	protected final boolean isDynamic;
	protected IntegerIdGenerator idGeneratorMultipleParameters;
	protected IntegerIdGenerator idGeneratorSingleParameter;
	protected IntegerIdGenerator idGeneratorPlain;
	protected ArrayList singleParameterTranslations;
	protected ArrayList plainTranslations;
	protected final Project project;
	private final Environment environment;

	/**
	 * Creates a new manager for translations.
	 * 
	 * @param project the current Ant project
	 * @param device the current device
	 * @param locale the current locale
	 * @param environment the environment
	 * @param resourceDirs the directories containing resources for the given device
	 * @param localizationSetting the localization setting
	 * @throws IOException when resources could not be loaded
	 */
	public TranslationManager(Project project, Device device, Locale locale, Environment environment, File[] resourceDirs, LocalizationSetting localizationSetting )
	throws IOException
	{
		this.project = project;
		this.localizationSetting = localizationSetting;
		this.isDynamic = (localizationSetting == null ? false : localizationSetting.isDynamic() );
		this.locale = locale;
		this.device = device;
		this.environment = environment;
		this.translationsByKey = new HashMap();
		this.preprocessingVariablesByKey = new HashMap();
		this.multipleParametersTranslations = new ArrayList();
		this.singleParameterTranslations = new ArrayList();
		this.plainTranslations = new ArrayList();
		Map rawTranslations = loadRawTranslations(resourceDirs);
		// load IDs for variables with multiple parameters:
		loadIdsMap();
		processRawTranslations( rawTranslations );
		
		// now set DateFormat variables according to current locale:
		String dateFormat = environment.getVariable("polish.DateFormat"); 
		if ( dateFormat != null) {
			// maybe the dateformat needs to be changed:
			if (dateFormat.length() == 2) {
				String separator = environment.getVariable("polish.DateFormatSeparator");
				String emptyText = environment.getVariable("polish.DateFormatEmptyText");
				if ("de".equals(dateFormat)) {
					dateFormat = "dmy";
					if (separator == null) {
						separator = ".";
					}
					if (emptyText == null) {
						emptyText = "TT.MM.JJJJ";
					}
				} else if ("fr".equals(dateFormat)) {
					dateFormat = "dmy";
					if (separator == null) {
						separator = "/";
					}
					if (emptyText == null) {
						emptyText = "JJ/MM/AAAA";
					}
				} else if ("us".equals(dateFormat)) {
					dateFormat = "mdy";
					if (separator == null) {
						separator = "-";
					}
					if (emptyText == null) {
						emptyText = "MM-DD-YYYY";
					}
				}
				environment.addVariable( "polish.DateFormat", dateFormat );
				environment.addVariable( "polish.DateFormatSeparator", separator );
				environment.addVariable( "polish.DateFormatEmptyText", emptyText );
			}
		} else {
			String language = locale.getLanguage();
			if ("de".equals(language)) {
				environment.addVariable( "polish.DateFormat", "dmy" );
				environment.addVariable( "polish.DateFormatSeparator", "." );
				environment.addVariable( "polish.DateFormatEmptyText", "TT.MM.JJJJ" );
			} else if ("fr".equals(language)) {
				environment.addVariable( "polish.DateFormat", "dmy" );
				environment.addVariable( "polish.DateFormatSeparator", "." );
				environment.addVariable( "polish.DateFormatEmptyText", "JJ/MM/AAAA" );
			} else {
				String country = locale.getCountry();
				if ("US".equals(country)) {
					environment.addVariable( "polish.DateFormat", "mdy" );
					environment.addVariable( "polish.DateFormatSeparator", "-" );
					environment.addVariable( "polish.DateFormatEmptyText", "MM-DD-YYYY" );
				}
			}
		}
		// register manager at the environment:
		environment.set( ENVIRONMENT_KEY, this );
	}

	/**
	 * Retrieves the file for storing IDs of translations with multiple parameters.
	 * 
	 * @return the file that stores those IDs
	 */
	protected File getMultipleParametersIdsFile() {
		return new File( this.device.getBaseDir() + File.separator + "LocaleIds_" + this.locale.toString() + ".txt");
	}

	/**
	 * Retrieves the file for storing IDs of translations with one parameter.
	 * 
	 * @return the file that stores those IDs
	 */
	protected File getSingleParameterIdsFile() {
		return new File( this.device.getBaseDir() + File.separator + "SingleLocaleIds_" + this.locale.toString() + ".txt");
	}

	/**
	 * Retrieves the file for storing IDs of translations with no parameters.
	 * 
	 * @return the file that stores those IDs
	 */
	protected File getPlainIdsFile() {
		return new File( this.device.getBaseDir() + File.separator + "PlainLocaleIds_" + this.locale.toString() + ".txt");
	}

	/**
	 * Saves the IDs-map for the complex translations to disk.
	 * When dynamic translations are used, also IDs of plain and single-parameter translations are saved, too.
	 * 
	 * @throws IOException when the file(s) could not be written
	 */
	public void writeIdsMap()
	throws IOException
	{
		File idsFile = getMultipleParametersIdsFile();
		FileUtil.writePropertiesFile(idsFile, this.idGeneratorMultipleParameters.getIdsMap() );
		if (this.isDynamic) {
			idsFile = getPlainIdsFile();
			FileUtil.writePropertiesFile(idsFile, this.idGeneratorPlain.getIdsMap() );
			idsFile = getSingleParameterIdsFile();
			FileUtil.writePropertiesFile(idsFile, this.idGeneratorSingleParameter.getIdsMap() );
		}
	}
	
	/**
	 * Loads the IDs-map for the complex translations to disk.
	 * When dynamic translations are used, also IDs of plain and single-parameter translations are loaded as well.
	 * 
	 * @throws IOException when the file(s) could not be read even though they exist
	 */
	public void loadIdsMap()
	throws IOException
	{
		File idsFile = getMultipleParametersIdsFile();
		Map idsMap = new HashMap();
		if (idsFile.exists()) {
			FileUtil.readPropertiesFile(idsFile, '=', idsMap );
		}
		this.idGeneratorMultipleParameters = new IntegerIdGenerator( idsMap );
		if (this.isDynamic) {
			// load IDs for variables with one parameter:
			idsFile = getSingleParameterIdsFile();
			idsMap = new HashMap();
			if (idsFile.exists()) {
				FileUtil.readPropertiesFile(idsFile, '=', idsMap );
			}
			this.idGeneratorSingleParameter = new IntegerIdGenerator( idsMap );
			// load IDs for variables with no parameters:
			idsFile = getPlainIdsFile();
			idsMap = new HashMap();
			if (idsFile.exists()) {
				FileUtil.readPropertiesFile(idsFile, '=', idsMap );
			}
			this.idGeneratorPlain = new IntegerIdGenerator( idsMap );
		}
	}
	
	
	/**
	 * Processes the raw translations: create Translation-instances and insert variable-values.
	 * 
	 * @param rawTranslations the translations
	 */
	private void processRawTranslations(Map rawTranslations ) {
		// the processing is actual done in two steps.
		// In the first step all variables are set,
		// in the second steps "ordinary" translations are processed.
		// This is necessary, so that variables which are defined or
		// changed in the messages-file can be used by other translations.
		Map variables = this.environment.getVariables();
		String[] keys = (String[]) rawTranslations.keySet().toArray( new String[ rawTranslations.size()] );
		// in the first round only set variables:
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String originalKey = key;
			String value = (String) rawTranslations.get( key );
			if (value.indexOf('$') != -1) {
				value = PropertyUtil.writeProperties(value, variables);
			}
			// process key: when it starts with "var:", "variable:" or "polish.",
			// then add it as a variable to the preprocessor:
			boolean variableFound = false;
			if (key.startsWith("polish.")) {
				//this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("var:")) {
				key = key.substring( "var:".length() );
				//this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("variable:")) {
				key = key.substring( "variable:".length() );
				//this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("MIDlet-")) {
				variableFound = true;
			}
			if ( variableFound ) {
				this.environment.addVariable(key, value );
				this.preprocessingVariablesByKey.put( key, value );
				// create final translation:
				Translation translation = new Translation( key, value, 
						false, null, null, null );
				this.translationsByKey.put( key, translation );
				rawTranslations.remove( originalKey );
				// the following can never be true, or could it?
				// every variable usually only has one single vlue without any i18n parameters
				if (translation.hasSeveralParameters()) {
					this.multipleParametersTranslations.add( translation );
				}
			}
		}		
		// in the second round set the actual translations as well:
		keys = (String[]) rawTranslations.keySet().toArray( new String[ rawTranslations.size()] );
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = (String) rawTranslations.get( key );
			if (value.indexOf('$') != -1) {
				value = PropertyUtil.writeProperties(value, variables);
			}
			// create final translation:
			Translation translation = new Translation( key, value, 
					this.isDynamic, this.idGeneratorPlain, this.idGeneratorSingleParameter, this.idGeneratorMultipleParameters );
			//Translation translation = new Translation( key, value, this.idGeneratorMultipleParameters );
			this.translationsByKey.put( key, translation );
			if (translation.hasSeveralParameters()) {
				this.multipleParametersTranslations.add( translation );
			} else if (translation.hasOneParameter()) {
				this.singleParameterTranslations.add( translation );
			} else {
				this.plainTranslations.add( translation );
			}
		}
	}

	/**
	 * Loads the raw translation-messages for the given device and locale.
	 * 
	 * @param resourceDirs the files containing the resources.
	 * @return a map containing all translations
	 * @throws IOException when a translations-file could not be loaded
	 */
	private Map loadRawTranslations( File[] resourceDirs ) throws IOException {
		//System.out.println("Loading translations for locale " + this.locale);
		// load the translations by following scheme:
		// first load the base-translations:
		// resources
		// resources/vendor
		// resources/group1..n
		// resources/vendor/device
		
		// Then load the locale specific resources:
		// resources/locale
		// resources/vendor/locale
		// resources/group1..n/locale
		// resources/vendor/device/locale
		
		Map rawTranslations = new HashMap();
		// load general resources:
		String messagesFileName = File.separator + this.localizationSetting.getMessagesFileName();
		String localeFileName;
		int splitPos = messagesFileName.lastIndexOf('.');
		if (splitPos != -1) {
			localeFileName = messagesFileName.substring(0, splitPos)
				+ "_"
				+ this.locale.toString()
				+ messagesFileName.substring(splitPos);
		} else {
			localeFileName = messagesFileName + this.locale.toString();
		}
		String languageFileName = null;
		if (this.locale.getCountry().length() > 0) {
			// okay, this locale has also a country defined,
			// so we need to look at the language-resources as well:
			if (splitPos != -1) {
				languageFileName = messagesFileName.substring(0, splitPos)
					+ "_"
					+ this.locale.getLanguage()
					+ messagesFileName.substring(splitPos);
			} else {
				languageFileName = messagesFileName + this.locale.getLanguage();
			}
		}
		for (int i = 0; i < resourceDirs.length; i++) {
			File dir = resourceDirs[i];
			String dirPath = dir.getAbsolutePath();
			File messagesFile = new File( dirPath + messagesFileName );
			if (messagesFile.exists()) {
				//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
				if (messagesFile.lastModified() > this.lastModificationTime) {
					this.lastModificationTime = messagesFile.lastModified();
				}
				//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
				readPropertiesFile(messagesFile, rawTranslations );
			}
			if (languageFileName != null) {
				messagesFile = new File( dirPath + languageFileName );
				if (messagesFile.exists()) {
					//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
					if (messagesFile.lastModified() > this.lastModificationTime) {
						this.lastModificationTime = messagesFile.lastModified();
					}
					//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
					readPropertiesFile(messagesFile, rawTranslations );
				}
			}
			messagesFile = new File( dirPath + localeFileName );
			if (messagesFile.exists()) {
				//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
				if (messagesFile.lastModified() > this.lastModificationTime) {
					this.lastModificationTime = messagesFile.lastModified();
				}
				//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
				readPropertiesFile(messagesFile, rawTranslations );
			}
		}
		return rawTranslations;
	}
	
	private void readPropertiesFile( File messagesFile, Map rawTranslations ) 
	throws FileNotFoundException, IOException 
	{
		if (this.isDynamic) {
			// use the java.util.Properties tool for resolving Unicode escape mechanism.
			// this is needed because we later store the loaded strings via DataOutputStream.writeUTF()
			// and the device would show \t instead of a tab and so on.
			Properties properties = new Properties();
			properties.load( new FileInputStream( messagesFile ) );
			rawTranslations.putAll( properties );
		} else {
			// just load the properties directly
			FileUtil.readPropertiesFile(messagesFile, '=', rawTranslations );
		}
	}
	
	/**
	 * Retrieves a translation for the given key.
	 * 
	 * @param key the key , e.g. "labels.GameStart"
	 * @return the translation for the given key or null when the translation was not found.
	 */
	public Translation getTranslation( String key ) {
		return (Translation) this.translationsByKey.get( key );
	}
	
	/**
	 * Gets the latest time when one of the basic messages-files has been modified.
	 *  
	 * @return a long containing the last modification time
	 */
	public long getLastModificationTime() {
		return this.lastModificationTime;
	}

	/**
	 * Inserts any required localization settings into the Locale.java class.
	 *  
	 * @param code the source code of Locale.java
	 */
	public void processLocaleCode(StringList code) {
		code.reset();
		boolean insertionPointFound = false;
		while (code.next()) {
			String line = code.getCurrent();
			if ("//$$IncludeLocaleDefinitionHere$$//".equals(line)) {
				insertionPointFound = true;
				// instantiate translations directly:
				insertMultipleParametersTranslations( code );
				break;
			}
		}
		if (!insertionPointFound) {
			throw new BuildException("Unable to modify [de.enough.polish.util.Locale.java]: insertion point not found!");
		}
		// now include the static local specific fields:
		insertFields( code, true );
	}

	/**
	 * Inserts the actual code fragments into Locale.java
	 * The static variables String[][] values and in[][] parameterOrders are intialized.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 */
	private void insertMultipleParametersTranslations(StringList code) {
		ArrayList lines = new ArrayList();
		Translation[] currentTranslations = getMultipleParametersTranslations();
		int numberOfTranslations = currentTranslations.length;
		if (numberOfTranslations == 0) {
			// no need to init an empty array:
			return; 
		}
		lines.add("\tstatic {");
		lines.add("\t\tmultipleParameterOrders = new short[" + numberOfTranslations + "][];");
		lines.add("\t\tmultipleParameterTranslations = new String[" + numberOfTranslations + "][];");
		for (int i = 0; i < currentTranslations.length; i++) {
			Translation translation = currentTranslations[i];
			lines.add( getMultipleParametersTranslationCode( translation ));
			int[] parameterOrder = translation.getParameterIndices();
			if (!isContinuous(parameterOrder)) {
	 			lines.add( getParameterOrderCode( translation, parameterOrder ) );
			}
		}
		/*
		 * dynamic translations are instantiated during the runtime by
		 * reading them out of a resource file
		if (this.isDynamic) {
			currentTranslations = getPlainTranslations();
			lines.add("\t\tplainTranslations = new String[" + currentTranslations.length + "];");
			for (int i = 0; i < currentTranslations.length; i++) {
				Translation translation = currentTranslations[i];
				lines.add( getTranslationCode( translation ));
			}
			currentTranslations = getSingleParameterTranslations();
			lines.add("\t\tsingleParameterTranslationsStart = new String[" + currentTranslations.length + "];");
			lines.add("\t\tsingleParameterTranslationsEnd = new String[" + currentTranslations.length + "];");
			for (int i = 0; i < currentTranslations.length; i++) {
				Translation translation = currentTranslations[i];
				lines.add( getTranslationCode( translation ));
			}			
		}
		*/
		lines.add("\t} // end of translations");
		String[] codeLines = (String[]) lines.toArray( new String[ lines.size() ] );
		code.insert(codeLines);
	}
	
	/**
	 * Determines whether the given integer-array is continuous.
	 * 
	 * @param parameterOrder the integer-array
	 * @return true only when the give values are {0, 1, 2, 3, .., n } 
	 */
	private boolean isContinuous(int[] parameterOrder) {
		for (int i = 0; i < parameterOrder.length; i++) {
			int j = parameterOrder[i];
			if (j != i) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Inserts the code necessary to instantiate one translation with multiple parameters.
	 * 
	 * @param translation the translation
	 * @return the string containing the necessary code
	 */
	private String getMultipleParametersTranslationCode(Translation translation) {
		StringBuffer code = new StringBuffer();
		if (!translation.hasSeveralParameters()) {
			throw new BuildException("Cannot create code for translation [" + translation.getKey() + "]: please report this error to j2mepolish@enough.de");
		}
		code.append( "\t\tmultipleParameterTranslations[" )
			.append( translation.getId() -1 )
			.append( "] = new String[] {" );
		String[] values = translation.getValueChunks();
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			code.append( '"' )
				.append( value )
				.append( '"' );
			if (i != values.length -1 ) {
				code.append(',');
			}
		}
		code.append("};");
		/*
		else if (translation.hasOneParameter()) {
			code.append( "\t\tsingleParameterTranslationsStart[" )
				.append( translation.getId() -1 )
				.append( "] = \"" ).append( translation.getOneValueStart() ).append("\";");
			code.append( "\t\tsingleParameterTranslationsEnd[" )
				.append( translation.getId() -1 )
				.append( "] = \"" ).append( translation.getOneValueStart() ).append("\";");
		} else {
			// translation has no parameters:
			code.append( "\t\tplainTranslations[" )
				.append( translation.getId() -1 )
				.append( "] = " ).append( translation.getQuotedValue() ).append(';');
		}
		*/
		return code.toString();
	}

	/**
	 * Gets the code for instantiating the order of the parameters for the specified translation
	 * 
	 * @param translation the translation
	 * @return the code for instantiating the order of the parameters for the specified translation
	 */
	private String getParameterOrderCode(Translation translation, int[] indices) {
		StringBuffer code = new StringBuffer();
		code.append( "\t\tmultipleParameterOrders[" )
			.append( translation.getId() -1 )
			.append( "] = new short[] {" );
		for (int i = 0; i < indices.length; i++) {
			int value = indices[i];
			code.append( value );
			if (i != indices.length -1 ) {
				code.append(',');
			}
		}
		code.append("};");
		return code.toString();
	}

	private Translation[] getMultipleParametersTranslations() {
		/*
		ArrayList list = new ArrayList( this.multipleParametersTranslations.size() );
		for (Iterator iter = this.multipleParametersTranslations.iterator(); iter.hasNext();) {
			Translation translation = (Translation) iter.next();
			if (translation.getId() != -1) {
				list.add( translation );
			}
		}
		*/
		Translation[] complexTranslations = (Translation[])  this.multipleParametersTranslations.toArray( new Translation[  this.multipleParametersTranslations.size() ]);
		Arrays.sort( complexTranslations, this );
		return complexTranslations;
	}
	
	private Translation[] getSingleParameterTranslations() {
		Translation[] myTranslations = (Translation[])  this.singleParameterTranslations.toArray( new Translation[  this.singleParameterTranslations.size() ]);
		Arrays.sort( myTranslations, this );
		return myTranslations;
	}

	private Translation[] getPlainTranslations() {
		Translation[] myTranslations = (Translation[])  this.plainTranslations.toArray( new Translation[  this.plainTranslations.size() ]);
		Arrays.sort( myTranslations, this );
		return myTranslations;
	}


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Translation t1 = (Translation) o1;
		Translation t2 = (Translation) o2;
		return t1.getId() - t2.getId();
	}

	public void insertDynamicFields(StringList code) {
		code.reset();
		boolean insertionPointFound = false;
		while (code.next()) {
			String line = code.getCurrent();
			if ("//$$IncludeLocaleDefinitionHere$$//".equals(line)) {
				insertionPointFound = true;
				break;
			}
		}
		if (!insertionPointFound) {
			throw new BuildException("Unable to modify [de.enough.polish.util.Locale.java]: insertion point not found!");
		}
		// now include the static local specific fields:
		insertFields( code, false );		
	}

	/**
	 * Inserts the static fields of the Locale class.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 */
	private void insertFields(StringList code, boolean isFinal) {
		if ( isFinal ) {
			code.insert( "\tpublic static final String LANGUAGE = \"" + this.locale.getLanguage() + "\";");
			code.insert( "\tpublic static final String DISPLAY_LANGUAGE = \"" + this.locale.getDisplayLanguage(this.locale) + "\";");
		} else {
			code.insert( "\tpublic static String LANGUAGE = \"" + this.locale.getLanguage() + "\";");
			code.insert( "\tpublic static String DISPLAY_LANGUAGE = \"" + this.locale.getDisplayLanguage(this.locale) + "\";");
		}
		String country = this.locale.getCountry();
		char minusSign = '-';
		char zeroDigit = '0';
		char decimalSeparator = '.';
		char monetaryDecimalSeparator = '.';
		char groupingSeparator = ',';
		char percent = '%';
		char permill = '\u2030';
		String infinity = "\u221e";
		
		NumberFormat format = NumberFormat.getCurrencyInstance(this.locale);
		try {
			DecimalFormat decimalFormat = (DecimalFormat) format;
			DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
			minusSign = symbols.getMinusSign();
			zeroDigit = symbols.getZeroDigit();
			decimalSeparator = symbols.getDecimalSeparator();
			monetaryDecimalSeparator = symbols.getMonetaryDecimalSeparator();
			groupingSeparator = symbols.getGroupingSeparator();
			percent = symbols.getPercent();
			permill = symbols.getPerMill();
			infinity = symbols.getInfinity();
		} catch (Exception e) {
			System.out.println("Warning: the locale [" + this.locale + "] does not support decimal symbols: " + e.toString() );
		}
		if (isFinal) {
			code.insert( "\tpublic static final char MINUS_SIGN = '" + minusSign + "';");
			code.insert( "\tpublic static final char ZERO_DIGIT = '" + zeroDigit + "';");
			code.insert( "\tpublic static final char DECIMAL_SEPARATOR = '" + decimalSeparator + "';");
			code.insert( "\tpublic static final char MONETARY_DECIMAL_SEPARATOR = '" + monetaryDecimalSeparator + "';");
			code.insert( "\tpublic static final char GROUPING_SEPARATOR = '" + groupingSeparator + "';");
			code.insert( "\tpublic static final char PERCENT = '" + percent + "';");
			code.insert( "\tpublic static final char PERMILL = '" + permill + "';");
			code.insert( "\tpublic static final String INFINITY = \"" + infinity + "\";");
			if (country.length() > 0) {
				code.insert( "\tpublic static final String COUNTRY = \"" + this.locale.getCountry() + "\";");
				code.insert( "\tpublic static final String DISPLAY_COUNTRY = \"" + this.locale.getDisplayCountry(this.locale) + "\";");
				Currency currency = format.getCurrency();
				String currencySymbol = currency.getSymbol(this.locale);
				String currencyCode = currency.getCurrencyCode();
				code.insert( "\tpublic static final String CURRENCY_SYMBOL = \"" + currencySymbol + "\";");
				code.insert( "\tpublic static final String CURRENCY_CODE = \"" + currencyCode + "\";");
			} else {
				// no country is defined:
				code.insert( "\tpublic static final String COUNTRY = null;");
				code.insert( "\tpublic static final String DISPLAY_COUNTRY = null;");
				code.insert( "\tpublic static final String CURRENCY_SYMBOL = null;");
				code.insert( "\tpublic static final String CURRENCY_CODE = null;");
			}
		} else {
			code.insert( "\tpublic static char MINUS_SIGN = '" + minusSign + "';");
			code.insert( "\tpublic static char ZERO_DIGIT = '" + zeroDigit + "';");
			code.insert( "\tpublic static char DECIMAL_SEPARATOR = '" + decimalSeparator + "';");
			code.insert( "\tpublic static char MONETARY_DECIMAL_SEPARATOR = '" + monetaryDecimalSeparator + "';");
			code.insert( "\tpublic static char GROUPING_SEPARATOR = '" + groupingSeparator + "';");
			code.insert( "\tpublic static char PERCENT = '" + percent + "';");
			code.insert( "\tpublic static char PERMILL = '" + permill + "';");
			code.insert( "\tpublic static String INFINITY = \"" + infinity + "\";");
			if (country.length() > 0) {
				code.insert( "\tpublic static String COUNTRY = \"" + this.locale.getCountry() + "\";");
				code.insert( "\tpublic static String DISPLAY_COUNTRY = \"" + this.locale.getDisplayCountry(this.locale) + "\";");
				Currency currency = format.getCurrency();
				String currencySymbol = currency.getSymbol(this.locale);
				String currencyCode = currency.getCurrencyCode();
				code.insert( "\tpublic static String CURRENCY_SYMBOL = \"" + currencySymbol + "\";");
				code.insert( "\tpublic static String CURRENCY_CODE = \"" + currencyCode + "\";");
			} else {
				// no country is defined:
				code.insert( "\tpublic static String COUNTRY = null;");
				code.insert( "\tpublic static String DISPLAY_COUNTRY = null;");
				code.insert( "\tpublic static String CURRENCY_SYMBOL = null;");
				code.insert( "\tpublic static String CURRENCY_CODE = null;");
			}
		}
	}
	
	/**
	 * Determines whether dynamic translations should be used.
	 * Such translations can be changed during runtime.
	 * 
	 * @return true when dynamic translations should be used.
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}

	/**
	 * Retrieves the default locale.
	 * This makes only sense when dynamic translations are used.
	 * 
	 * @return the default locale
	 */
	public Locale getDefaultLocale() {
		return this.localizationSetting.getDefaultLocale();
	}
	
	private void setIdsAndSort( Translation[] translations, TranslationManager manager ) {
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			Translation knownIdTranslation = manager.getTranslation( translation.getKey() );
			if (knownIdTranslation == null) {
				throw new BuildException("The translation [" + translation.getKey() +"] is not defined in the default locale. You need to define it in that locale as well when you want to use dynamic translations." );
			}
			int id = knownIdTranslation.getId();
			translation.setId( id );
		}		
		Arrays.sort( translations, this );
	}

	/**
	 * Saves all translations into a *.loc file.
	 * This is only possible when dynamic translations are used.
	 * 
	 * @param targetDir the target directory
	 * @param currentDevice the current device
	 * @param dynamicLocale the locale
	 * @param manager the manager that knows about the translations
	 * @throws IOException when a resource could not be copied
	 */
	public void saveTranslations(File targetDir, Device currentDevice, Locale dynamicLocale, TranslationManager manager)
	throws IOException
	{
		File file = new File( targetDir, dynamicLocale.toString() + ".loc" );
		//System.out.println("Writing translations to " + file.getAbsolutePath() );
		DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
		// plain translations:
		Translation[] translations = getPlainTranslations();
		setIdsAndSort( translations, manager );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			//System.out.println( i + "=" + translation.getValue() );
			out.writeUTF( translation.getValue() );
		}
		// translations with a single parameter:
		translations = getSingleParameterTranslations();
		setIdsAndSort( translations, manager );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			out.writeUTF( translation.getOneValueStart() );
			out.writeUTF( translation.getOneValueEnd() );
		}
		// translations with a multiple parameters:
		translations = getMultipleParametersTranslations();
		setIdsAndSort( translations, manager );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			try {
				int[] orders = translation.getParameterIndices();
				String[] chunks = translation.getValueChunks();
				if (orders.length != chunks.length) {
					throw new IllegalStateException("TranslationManager: unable to save translation file: orders.length != chunks.length, please report this error to j2mepolish@enough.org");
				}
				out.writeByte( chunks.length );
				for (int j = 0; j < chunks.length; j++) {
					String chunk = chunks[j];
					int order = orders[j]; 
					out.writeByte( order );
					out.writeUTF( chunk );
				}
				out.writeUTF( translation.getValue() );
			} catch (RuntimeException e) {
				System.err.println("Unable to process translation [" + translation.getKey() + "]: " + e.toString() );
				throw e;
			}
		}
		out.flush();
		out.close();
	}

	/**
	 * @return the locale associated with this manager
	 */
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * Retrieves all preprocessing variables that have been defined
	 * 
	 * @return all preprocessing variables that have been defined
	 */
	public Map getPreprocessingVariables() {
		return this.preprocessingVariablesByKey;
	}

}

