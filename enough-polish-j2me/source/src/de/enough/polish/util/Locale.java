/*
 * Created on 09-Sep-2004 at 10:30:04.
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
package de.enough.polish.util;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>Is used for internationalisation.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class Locale {
	
	private static short[][] parameterOrders;
	private static String[][] values;
	
	// do not change the following line!
//$$IncludeLocaleDefinitionHere$$//
	
	//#ifdef false
	/** The ISO language code, e.g. "en" for English. */
	public static final String LANGUAGE = "en";
	/** 
	 * The ISO country code, e.g. "US" for USA 
	 * This is null when no country is defined in the current locale.
	 */
	public static final String COUNTRY = "US";
	/** The localized language name, e.g. "English" or "Deutsch" */
	public static final String DISPLAY_LANGUAGE = "English";
	/** 
	 * The localized country name, e.g. "United States" or "Deutschland".
	 * This is null when no country is defined in the current locale.
	 */
	public static final String DISPLAY_COUNTRY = "United States";
	/** 
	 * The symbol of the currency, e.g. "$" or "€".
	 * This is null when no country is defined in the current locale.
	 */
	public static final String CURRENCY_SYMBOL = "$";
	/** 
	 * The international three letter code of the currency, e.g. "USD" or "EUR".
	 * This is null when no country is defined in the current locale.
	 */
	public static final String CURRENCY_CODE = "USD";
	
	private Locale() {
		// no instantiation allowed
	}
	//#endif

	//#ifdef polish.i18n.hasDynamicTranslations
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.StartGame"
	 * @return the translation, e.g. "Start Game" or "Spiel starten"
	 */
	public static final String get( String key ) {
		// all simple translations are actually directly embedded into the source-code,
		// so this method does not need to be implemenented at all:
		return null;
	}
	//#endif
	
	//#ifdef polish.i18n.hasDynamicTranslations
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.WelcomeUser"
	 * @param parameter the parameter, e.g. "Peter"
	 * @return the translation, e.g. "Hello Peter!" or "Hallo Peter!"
	 * @throws NullPointer when the given parameter is null.
	 */
	public static final String get( String key, String parameter ) {
		// all simple translations are usually directly embedded into the source-code,
		// but there can be dynamic keys as well:
		return null;
	}
	//#endif
	
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.MatchTitle"
	 * @param parameters the parameters-array, this needs to be defined outside of the call.
	 *     e.g.
	 * <pre> 
	 * 		String[] params = new String[]{ "Peter", "Jordan" };
	 * 		String translation = Locale . get( "titles.boxing", params );
	 * <pre>
	 * @return the translation, e.g. "Peter vs Jordan" or "Peter gegen Jordan"
	 * @throws NullPointer when the given parameters are null or if one of the given parameters is null.
	 */
	//#ifdef false
	public static final String get( String key, String[] parameters ) {
	//#else
		//# public static final String get( int keyId, String[] parameters ) {
	//#endif
		//#ifdef false
			short keyId = 0;
		//#endif
		
		// Reshuffle the parameters:
		// CHECK: There could be more or less parameters than expected, e.g.
		// "hello {2}, you are a {0}"???
		// --> some parameters can be ignored
		final short[] reorder = parameterOrders[ keyId ];
		final String[] reorderedParameters;
		if (reorder != null) {
			reorderedParameters = new String[ reorder.length ];
			for (int i = 0; i < reorderedParameters.length; i++) {
				reorderedParameters[i] = parameters[ reorder[i] ];	
			}
		} else {
			reorderedParameters = parameters;
		}
		// Now merge the value with the reordered parameters:
		final String[] valueChunks = values[ keyId ];
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < reorderedParameters.length; i++) {
			String value = valueChunks[i];
			result.append( value )
				  .append( reorderedParameters[ i ]);
			
		}
		
		for (int i = reorderedParameters.length; i < valueChunks.length; i++) {
			result.append( valueChunks[i] );
		}
		
		// return result:
		return result.toString();
	}
	
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Date instance.
	 * 
	 * @param time the time in milliseconds after 1.1.1970 GMT.
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Date)
	 */
	public static String formatDate( long time ) {		
		return formatDate( new Date( time ) );
	}
	
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Calendar instance.
	 * 
	 * @param date the date
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Calendar)
	 */
	public static String formatDate( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return formatDate( calendar );
	}
	
	/**
	 * Formats the given calendar to the current locale's format.
	 * Use this method for best efficiency.
	 * 
	 * @param calendar the calendar which holds the date
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the calendar is null
	 */
	public static String formatDate( Calendar calendar ) {
		StringBuffer buffer = new StringBuffer(10);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get( Calendar.MONTH );
		int day = calendar.get( Calendar.DAY_OF_MONTH );
		//TODO rob set DateFormat variable in TranslationManager
		//#if polish.DateFormat == mdy
			if (month < 9) {
				buffer.append('0');
			}
			buffer.append( ++month )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			if (day < 10) {
				buffer.append( '0' );
			}
			buffer.append( day )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			buffer.append( year );
		//#elif polish.DateFormat == dmy
			if (day < 10) {
				buffer.append( '0' );
			}
			buffer.append( day )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			if (month < 9) {
				buffer.append('0');
			}
			buffer.append( ++month )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			buffer.append( year );
		//#else
			// default to YMD
			buffer.append( year )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			if (month < 9) {
				buffer.append('0');
			}
			buffer.append( ++month )
			//#if polish.DateFormatSeparator:defined
				//#= .append("${polish.DateFormatSeparator}");
			//#else
			        .append("-");
			//#endif
			if (day < 10) {
				buffer.append( '0' );
			}
			buffer.append( day );
		//#endif	
		return buffer.toString();
	}

	
}
