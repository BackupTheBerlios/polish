/*
 * Created on 09-Sep-2004 at 17:48:58.
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

import java.util.Locale;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.TextUtil;

/**
 * <p>Stores the localization settings.</p>
 *
 * <p>copyright Enough Software 2004</p>
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
			String[] localeDefs = TextUtil.splitAndTrim( supportedLocalesStr, ',' );
			Locale[] locales = new Locale[ localeDefs.length ];
			for (int i = 0; i < localeDefs.length; i++) {
				String localeDefinition = localeDefs[i];
				int countryStart = localeDefinition.indexOf('_');
				if (countryStart == -1) {
					countryStart = localeDefinition.indexOf('-');
				}
				if (countryStart == -1) {
					locales[i] = new Locale( localeDefinition );
				} else {
					String language = localeDefinition.substring( 0, countryStart );
					String country = localeDefinition.substring( countryStart + 1 );
					locales[i] = new Locale( language, country );
				}
				
			}
			this.supportedLocales = locales;
		}
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

}
 