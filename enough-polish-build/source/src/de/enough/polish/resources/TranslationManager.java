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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.resources;


import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.preprocess.Preprocessor;
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
	
	private final Map translations;
	private final Locale locale;
	private final Device device;
	private final LocalizationSetting localizationSetting;
	private final IntegerIdGenerator idGenerator;
	private final Preprocessor preprocessor;
	private long lastModificationTime;
	private final List severalValuesTranslations;

	/**
	 * Creates a new manager for translations.
	 * 
	 * @param device the current device
	 * @param locale the current locale
	 * @param preprocessor the preprocessor which manages variables and symbols.
	 * @param resourceDirs the directories containing resources for the given device
	 * @param localizationSetting the localization setting
	 * @throws IOException when resources could not be loaded
	 */
	public TranslationManager(Device device, Locale locale, Preprocessor preprocessor, File[] resourceDirs, LocalizationSetting localizationSetting )
	throws IOException
	{
		this.localizationSetting = localizationSetting;
		this.locale = locale;
		this.device = device;
		this.preprocessor = preprocessor;
		this.translations = new HashMap();
		this.severalValuesTranslations = new ArrayList();
		Map rawTranslations = loadRawTranslations(resourceDirs);
		// load IDs for variables:
		File idsFile = new File( device.getBaseDir() + File.separator + "LocaleIds_" + locale.toString() + ".txt");
		Map idsMap = new HashMap();
		if (idsFile.exists()) {
			FileUtil.readPropertiesFile(idsFile, '=', idsMap );
		}
		this.idGenerator = new IntegerIdGenerator( idsMap );
		processRawTranslations( rawTranslations );
		
		// now set DateFormat variables according to current locale:
		String dateFormat = preprocessor.getVariable("polish.DateFormat"); 
		if ( dateFormat != null) {
			// maybe the dateformat needs to be changed:
			if (dateFormat.length() == 2) {
				String separator = preprocessor.getVariable("polish.DateFormatSeparator");
				String emptyText = preprocessor.getVariable("polish.DateFormatEmptyText");
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
				preprocessor.addVariable( "polish.DateFormat", dateFormat );
				preprocessor.addVariable( "polish.DateFormatSeparator", separator );
				preprocessor.addVariable( "polish.DateFormatEmptyText", emptyText );
			}
		} else {
			String language = locale.getLanguage();
			if ("de".equals(language)) {
				preprocessor.addVariable( "polish.DateFormat", "dmy" );
				preprocessor.addVariable( "polish.DateFormatSeparator", "." );
				preprocessor.addVariable( "polish.DateFormatEmptyText", "TT.MM.JJJJ" );
			} else if ("fr".equals(language)) {
				preprocessor.addVariable( "polish.DateFormat", "dmy" );
				preprocessor.addVariable( "polish.DateFormatSeparator", "." );
				preprocessor.addVariable( "polish.DateFormatEmptyText", "JJ/MM/AAAA" );
			} else {
				String country = locale.getCountry();
				if ("US".equals(country)) {
					preprocessor.addVariable( "polish.DateFormat", "mdy" );
					preprocessor.addVariable( "polish.DateFormatSeparator", "-" );
					preprocessor.addVariable( "polish.DateFormatEmptyText", "MM-DD-YYYY" );
				}
			}
		}
	}
	
	/**
	 * Saves the IDs-map for the complex translations to disk.
	 * 
	 * @throws IOException when the file could not be written
	 */
	public void writeIdsMap()
	throws IOException
	{
		File idsFile = new File( this.device.getBaseDir() + File.separator + "LocaleIds_" + this.locale.toString() + ".txt");
		FileUtil.writePropertiesFile(idsFile, this.idGenerator.getIdsMap() );
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
		Map variables = this.preprocessor.getVariables();
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
				this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("var:")) {
				key = key.substring( "var:".length() );
				this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("variable:")) {
				key = key.substring( "variable:".length() );
				this.preprocessor.addVariable(key, value );
				variableFound = true;
			} else if (key.startsWith("MIDlet-")) {
				this.preprocessor.addVariable(key, value );
				variableFound = true;
			}
			if ( variableFound ) {
				// create final translation:
				Translation translation = new Translation( key, value, this.idGenerator );
				this.translations.put( key, translation );
				rawTranslations.remove( originalKey );
				if (translation.hasSeveralParameters()) {
					this.severalValuesTranslations.add( translation );
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
			Translation translation = new Translation( key, value, this.idGenerator );
			this.translations.put( key, translation );
			if (translation.hasSeveralParameters()) {
				this.severalValuesTranslations.add( translation );
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
				FileUtil.readPropertiesFile(messagesFile, '=', rawTranslations );
			}
			if (languageFileName != null) {
				messagesFile = new File( dirPath + languageFileName );
				if (messagesFile.exists()) {
					//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
					if (messagesFile.lastModified() > this.lastModificationTime) {
						this.lastModificationTime = messagesFile.lastModified();
					}
					FileUtil.readPropertiesFile(messagesFile, '=', rawTranslations );
				}
			}
			messagesFile = new File( dirPath + localeFileName );
			if (messagesFile.exists()) {
				//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
				if (messagesFile.lastModified() > this.lastModificationTime) {
					this.lastModificationTime = messagesFile.lastModified();
				}
				FileUtil.readPropertiesFile(messagesFile, '=', rawTranslations );
			}
		}
		return rawTranslations;
	}
	
	/**
	 * Retrieves a translation for the given key.
	 * 
	 * @param key the key , e.g. "labels.GameStart"
	 * @return the translation for the given key or null when the translation was not found.
	 */
	public Translation getTranslation( String key ) {
		return (Translation) this.translations.get( key );
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
				insertTranslationsWithSeveralValues( code );
				break;
			}
		}
		if (!insertionPointFound) {
			throw new BuildException("Unable to modify [de.enough.polish.util.Locale.java]: insertion point not found!");
		}
		// now include the static local specific fields:
		insertFields( code );
	}

	/**
	 * Inserts the actual code fragments into Locale.java
	 * The static variables String[][] values and in[][] parameterOrders are intialized.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 */
	private void insertTranslationsWithSeveralValues(StringList code) {
		ArrayList lines = new ArrayList();
		Translation[] complexTranslations = getTranslationsWithSeveralValues();
		int numberOfTranslations = complexTranslations.length;
		if (numberOfTranslations == 0) {
			// no need to init an empty array:
			return; 
		}
		lines.add("\tstatic {");
		lines.add("\t\tparameterOrders = new short[" + numberOfTranslations + "][];");
		lines.add("\t\tvalues = new String[" + numberOfTranslations + "][];");
		for (int i = 0; i < complexTranslations.length; i++) {
			Translation translation = complexTranslations[i];
			lines.add( getValueCode( translation ));
			int[] parameterOrder = translation.getParameterIndices();
			if (!isContinuous(parameterOrder)) {
	 			lines.add( getParameterOrderCode( translation, parameterOrder ) );
			}
		}
		lines.add("\t} // end of complex translations");
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
	 * @param translation
	 * @return
	 */
	private String getValueCode(Translation translation) {
		StringBuffer code = new StringBuffer();
		code.append( "\t\tvalues[" )
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
		return code.toString();
	}

	/**
	 * @param translation
	 * @return
	 */
	private String getParameterOrderCode(Translation translation, int[] indices) {
		StringBuffer code = new StringBuffer();
		code.append( "\t\tparameterOrders[" )
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

	private Translation[] getTranslationsWithSeveralValues() {
		ArrayList list = new ArrayList( this.severalValuesTranslations.size() );
		for (Iterator iter = this.severalValuesTranslations.iterator(); iter.hasNext();) {
			Translation translation = (Translation) iter.next();
			if (translation.getId() != -1) {
				list.add( translation );
			}
		}
		Translation[] complexTranslations = (Translation[]) list.toArray( new Translation[ list.size() ]);
		Arrays.sort( complexTranslations, this );
		return complexTranslations;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Translation t1 = (Translation) o1;
		Translation t2 = (Translation) o2;
		return t1.getId() - t2.getId();
	}

	/**
	 * Inserts the static fields of the Locale class.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 */
	private void insertFields(StringList code) {
		code.insert( "\tpublic static final String LANGUAGE = \"" + this.locale.getLanguage() + "\";");
		code.insert( "\tpublic static final String DISPLAY_LANGUAGE = \"" + this.locale.getDisplayLanguage(this.locale) + "\";");
		String country = this.locale.getCountry();
		if (country.length() > 0) {
			code.insert( "\tpublic static final String COUNTRY = \"" + this.locale.getCountry() + "\";");
			code.insert( "\tpublic static final String DISPLAY_COUNTRY = \"" + this.locale.getDisplayCountry(this.locale) + "\";");
			NumberFormat format = NumberFormat.getCurrencyInstance(this.locale);
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
	}

}

