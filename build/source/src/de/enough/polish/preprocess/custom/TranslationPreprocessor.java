/*
 * Created on 13-Sep-2004 at 14:47:47.
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
package de.enough.polish.preprocess.custom;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.resources.Translation;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.StringList;
import de.enough.polish.util.TextUtil;

/**
 * <p>Incorporates localized messages into the source code.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        13-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationPreprocessor extends CustomPreprocessor {

	private final static String STRING_PATTERN = "\\\"[\\w|-|_|:|\\.|\\s]+\\\"";
	private final static String LOCALE_GET_PATTERN_STR = "Locale\\.get\\s*\\(\\s*" + STRING_PATTERN + "(\\s*\\,\\s*(\\w+|" + STRING_PATTERN + "))?\\s*\\)"; 
	protected final static Pattern LOCALE_GET_PATTERN = Pattern.compile( LOCALE_GET_PATTERN_STR );
	
	private TranslationManager translationManager;
	
	/**
	 * Creates a new empty preprocessor for translations.
	 */
	public TranslationPreprocessor() {
		super();
	}
	
	/**
	 * Sets a new translation manager.
	 * @param translationManager the new translation manager.
	 */
	public void setTranslationManager( TranslationManager translationManager ) {
		this.translationManager = translationManager;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDeviceEnd(de.enough.polish.Device, boolean)
	 */
	public void notifyDeviceEnd(Device device, boolean usesPolishGui) {
		super.notifyDeviceEnd(device, usesPolishGui);
		try {
			// now save the found messages-IDs to the disk:
			this.translationManager.writeIdsMap();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to store IDs-map of translations: " + e.toString(), e );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		while (lines.next()) {
			String line = lines.getCurrent();
			int startIndex = line.indexOf("Locale.get");
			if (startIndex != -1) {
				Matcher matcher = LOCALE_GET_PATTERN.matcher( line );
				boolean matchFound = false;
				while (matcher.find()) {
					matchFound = true;
					String call = matcher.group();
					int keyStart = call.indexOf('"');
					int keyEnd = call.indexOf('"', keyStart + 1);
					String key = call.substring( keyStart + 1, keyEnd );
					Translation translation = this.translationManager.getTranslation(key);
					if (translation == null) {
						throw new BuildException( getErrorStart(className, lines) + "Found no translation for key [" + key + "].");
					}
					if (translation.isPlain()) {
						line = TextUtil.replace( line, call, translation.getQuotedValue() );
					} else {
						// there is at least one parameter:
						int parameterStart = call.indexOf(',');
						if (parameterStart == -1) {
							throw new BuildException( getErrorStart(className, lines) + "The translation for key [" + key + "] expects at least one parameter, but none is given. The tranlation is: [" + translation.getValue() + "].");
						}
						if (translation.hasOneParameter()) {
							String parameter = call.substring( parameterStart + 1, call.length() - 1).trim();
							line = TextUtil.replace( line, call, translation.getQuotedValue( parameter ) );
						} else {
							// replace String-key with integer-ID to save valueable size:
							String id = "" + translation.getId();
							String quotedKey = '"' + key + '"';
							String callReplacement = TextUtil.replace( call, quotedKey, id );
							line = TextUtil.replace( line, call, callReplacement );
						}
					}
				} // while matcher.find()
				if (matchFound) {
					lines.setCurrent( line );
				}
			}
		}
	}


}
