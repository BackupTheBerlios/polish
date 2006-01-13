/*
 * Created on 09-Sep-2004 at 17:48:58.
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
package de.enough.polish.ant.build;

import java.util.Locale;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores the localization settings.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LocalizationSetting extends Setting {
	
	private String messages = "messages.txt";
	private boolean includeAllLocales;
	private Locale[] supportedLocales;
	private boolean dynamic;
	private Locale defaultLocale;
	private String preprocessorClassName;
	private String translationManagerClassName;


	/**
	 * Creates an empty setting
	 */
	public LocalizationSetting() {
		super();
	}
	
	/**
	 * Retrieves all supported locales.
	 * This will return null when all found locales should be included.
	 * 
	 * @return Returns the supported locales.
	 */
	public Locale[] getSupportedLocales() {
		return this.supportedLocales;
	}
	
	/**
	 * Sets the locales which should be supported during this build.
	 * 
	 * @param supportedLocalesStr The locales which should be supported or "*" for all found locales.
	 */
	public void setLocales(String supportedLocalesStr) {
		if ("*".equals(supportedLocalesStr)) {
			this.includeAllLocales = true;
		} else {
			String[] localeDefs = StringUtil.splitAndTrim( supportedLocalesStr, ',' );
			Locale[] locales = new Locale[ localeDefs.length ];
			for (int i = 0; i < localeDefs.length; i++) {
				String localeDefinition = localeDefs[i];
				locales[i] = getLocale( localeDefinition );				
			}
			this.supportedLocales = locales;
		}
	}
	
	/**
	 * Sets a single locale
	 * 
	 * @param locale the supported locale
	 */
	public void setLocale( String locale ) {
		setLocales(locale);
	}
	
	/**
	 * Determines whether all found locales should be included.
	 * 
	 * @return Returns true when all found locales should be included.
	 */
	public boolean includeAllLocales() {
		return this.includeAllLocales;
	}	
	
	/**
	 * Sets the name of messages-files.
	 * The default name is "messages.txt".
	 * 
	 * @param messages the name of files containing the messages.
	 */
	public void setMessages( String messages ) {
		this.messages = messages;
	}
	
	/**
	 * Retrieves the file-name containing the messages for localizations.
	 * 
	 * @return "messages.txt" or similar
	 */
	public String getMessagesFileName() {
		return this.messages;
	}
	
	/**
	 * Determines whether dynamic translations should be supported.
	 * Dynamic translations can be changed during runtime with the Locale.loadTranslations(...)-method.
	 * 
	 * @return true when dynamic translations should be supported.
	 */
	public boolean isDynamic() {
		return this.dynamic;
	}
	
	/**
	 * Defines whether dynamic translations should be supported.
	 * Dynamic translations can be changed during runtime with the Locale.loadTranslations(...)-method.
	 * 
	 * @param dynamic true when dynamic translations should be supported.
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	/**
	 * Parses the locale from the given definition
	 * 
	 * @param definition the definition, e.g. "de_DE"
	 * @return the corresponding locale
	 */
	private Locale getLocale( String definition ) {
		int countryStart = definition.indexOf('_');
		if (countryStart == -1) {
			countryStart = definition.indexOf('-');
		}
		if (countryStart == -1) {
			return new Locale( definition );
		} else {
			String language = definition.substring( 0, countryStart );
			String country = definition.substring( countryStart + 1 );
			return new Locale( language, country );
		}
	}
	
	/**
	 * Sets the default locale.
	 * This is the locale that is loaded upon start of the application by the de.enough.polish.util.Locale class.
	 * 
	 * @param locale the default locale, e.g. "en_US"
	 */
	public void setDefaultLocale( String locale ) {
		this.defaultLocale = getLocale( locale );
	}
	
	/**
	 * Retrieves the default locale.
	 * This call makes only sense when dynamic locales are used.
	 * 
	 * @return the default locale
	 */
	public Locale getDefaultLocale() {
		if (this.defaultLocale != null) {
			return this.defaultLocale;
		} else if (this.supportedLocales.length == 1){
			return this.supportedLocales[0];
		} else {
			Locale locale = Locale.getDefault();
			for (int i = 0; i < this.supportedLocales.length; i++) {
				Locale supportedLocale = this.supportedLocales[i];
				if (locale.equals( supportedLocale )) {
					return locale;
				}
			}
			return this.supportedLocales[0];
		}
	}

	/**
	 * Retrieves the name of the preprocessor class.
	 * 
	 * @return the name of the preprocessor class
	 */
	public String getPreprocessorClassName() {
		if (this.preprocessorClassName == null) {
			return "de.enough.polish.preprocess.custom.TranslationPreprocessor";
		} else {
			return this.preprocessorClassName;
		}
	}
	
	/**
	 * Sets the name of the preprocessor class.
	 * 
	 * @param preprocessorClassName the class name of the preprocessor
	 */
	public void setPreprocessor( String preprocessorClassName ) {
		this.preprocessorClassName = preprocessorClassName;
	}
	
	/**
	 * Retrieves the name of the translation manager class.
	 * 
	 * @return the name of the translation manager class
	 */
	public String getTranslationManagerClassName() {
		return this.translationManagerClassName;
	}
	
	/**
	 * Sets the name of the translation manager class.
	 * 
	 * @param translationManagerClassName the class name of the translation manager 
	 */
	public void setTranslationManager( String translationManagerClassName ) {
		this.translationManagerClassName = translationManagerClassName;
	}

}
 