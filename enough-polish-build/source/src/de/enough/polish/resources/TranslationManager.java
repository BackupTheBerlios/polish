/*
 * Created on 12-Sep-2004 at 13:10:19.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.enough.polish.Device;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;
import de.enough.polish.util.PropertyUtil;

/**
 * <p>Manages translations.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        12-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationManager {
	
	private final Map translations;
	private final Locale locale;
	private final Device device;
	private final LocalizationSetting localizationSetting;
	private final IntegerIdGenerator idGenerator;
	private long lastModificationTime;

	/**
	 * Creates a new manager for translations.
	 * 
	 * @param device the current device
	 * @param locale the current locale
	 * @param variables the currently available variables
	 * @param resourceDirs the directories containing resources for the given device
	 * @param localizationSetting the localization setting
	 * @throws IOException when resources could not be loaded
	 */
	public TranslationManager(Device device, Locale locale, Map variables, File[] resourceDirs, LocalizationSetting localizationSetting )
	throws IOException
	{
		this.localizationSetting = localizationSetting;
		this.locale = locale;
		this.device = device;
		this.translations = new HashMap();
		Map rawTranslations = loadRawTranslations(resourceDirs);
		// load IDs for variables:
		File idsFile = new File( device.getBaseDir() + File.separator + "LocaleIds_" + locale.toString() + ".txt");
		Map idsMap = new HashMap();
		if (idsFile.exists()) {
			FileUtil.readPropertiesFile(idsFile, '=', idsMap );
		}
		this.idGenerator = new IntegerIdGenerator( idsMap );
		processRawTranslations( rawTranslations, variables );
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
	 * @param device the current device
	 * @param locale the current locale
	 * @param variables the current available variables
	 * @param idGenerator the generator for IDs
	 * @return a map containing for each translation-key the corresponding Translation instance
	 */
	private void processRawTranslations(Map rawTranslations, Map variables ) {
		String[] keys = (String[]) rawTranslations.keySet().toArray( new String[ rawTranslations.size()] );
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = (String) rawTranslations.get( key );
			if (value.indexOf('$') != -1) {
				value = PropertyUtil.writeProperties(value, variables);
			}
			Translation translation = new Translation( key, value, this.idGenerator );
			this.translations.put( key, translation );
			// if the key starts with "polish.", add the variable:
			if (key.startsWith("polish.")) {
				variables.put( key, value );
			}
		}
	}

	/**
	 * Loads the raw translation-messages for the given device and locale.
	 * 
	 * @param device the current device
	 * @param locale the current locale, must not be null
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
		for (int i = 0; i < resourceDirs.length; i++) {
			File dir = resourceDirs[i];
			File messagesFile = new File( dir.getAbsolutePath() + messagesFileName );
			if (messagesFile.exists()) {
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

}
