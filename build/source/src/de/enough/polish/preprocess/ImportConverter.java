/*
 * Created on 26-Feb-2004 at 15:03:21.
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
package de.enough.polish.preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import proguard.shrink.UsedClassFileFilter;

import de.enough.polish.Device;
import de.enough.polish.util.StringList;
import de.enough.polish.util.TextFileManager;
import de.enough.polish.util.TextUtil;

/**
 * <p>Converts the import-statements to allow the parallel usage of the polish-gui and the standard java-gui.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class ImportConverter {
	
	private final HashMap midp1ToPolish;
	private final HashMap midp2ToPolish;
	private final HashMap polishToMidp1;
	private final HashMap polishToMidp2;
	private final HashMap siemensColorGameApiToPolish;
	private final HashMap polishToSiemensColorGameApi;
	private final String completeMidp1;
	private final String completeMidp2;
	private final String defaultPackageCompleteMidp1;
	private final String defaultPackageCompleteMidp2;
	private final HashMap defaultPackageSiemensColorGameApiToPolish;
	
	/**
	 * Creates a new import converter.
	 */
	public ImportConverter() {
		// init import statements to translate from the J2ME- to the polish-GUI:
		HashMap toPolish = new HashMap();
		toPolish.put( "javax.microedition.lcdui.Choice", "de.enough.polish.ui.Choice; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.ChoiceGroup", "de.enough.polish.ui.ChoiceGroup; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.CustomItem", "de.enough.polish.ui.CustomItem; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.DateField", "de.enough.polish.ui.DateField; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Form", "de.enough.polish.ui.Form; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Gauge", "de.enough.polish.ui.Gauge; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.ImageItem", "de.enough.polish.ui.ImageItem; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Item", "de.enough.polish.ui.Item; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.ItemCommandListener", "de.enough.polish.ui.ItemCommandListener; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.ItemStateListener", "de.enough.polish.ui.ItemStateListener; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.List", "de.enough.polish.ui.List; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Screen", "de.enough.polish.ui.Screen; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Spacer", "de.enough.polish.ui.Spacer; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.StringItem", "de.enough.polish.ui.StringItem; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Style", "de.enough.polish.ui.Style; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.TextBox", "de.enough.polish.ui.TextBox; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.TextField", "de.enough.polish.ui.TextField; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.Ticker", "de.enough.polish.ui.Ticker; import de.enough.polish.ui.StyleSheet");
		this.midp2ToPolish = toPolish;
		
		this.siemensColorGameApiToPolish = new HashMap( toPolish );
		this.siemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.GameCanvas", "com.siemens.mp.color_game.GameCanvas; import de.enough.polish.ui.StyleSheet" );
		this.siemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.Layer", "com.siemens.mp.color_game.Layer; import de.enough.polish.ui.StyleSheet" );
		this.siemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.LayerManager", "com.siemens.mp.color_game.LayerManager; import de.enough.polish.ui.StyleSheet" );
		this.siemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.TiledLayer", "com.siemens.mp.color_game.TiledLayer; import de.enough.polish.ui.StyleSheet" );
		this.siemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite; import de.enough.polish.ui.StyleSheet");

		this.defaultPackageSiemensColorGameApiToPolish = new HashMap( toPolish );
		this.defaultPackageSiemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.GameCanvas", "com.siemens.mp.color_game.GameCanvas" );
		this.defaultPackageSiemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.Layer", "com.siemens.mp.color_game.Layer" );
		this.defaultPackageSiemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.LayerManager", "com.siemens.mp.color_game.LayerManager" );
		this.defaultPackageSiemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.TiledLayer", "com.siemens.mp.color_game.TiledLayer" );
		this.defaultPackageSiemensColorGameApiToPolish.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite");

		this.polishToSiemensColorGameApi = new HashMap();
		this.polishToSiemensColorGameApi.put( "javax.microedition.lcdui.game.GameCanvas", "com.siemens.mp.color_game.GameCanvas; import de.enough.polish.ui.StyleSheet" );
		this.polishToSiemensColorGameApi.put( "javax.microedition.lcdui.game.Layer", "com.siemens.mp.color_game.Layer; import de.enough.polish.ui.StyleSheet" );
		this.polishToSiemensColorGameApi.put( "javax.microedition.lcdui.game.LayerManager", "com.siemens.mp.color_game.LayerManager; import de.enough.polish.ui.StyleSheet" );
		this.polishToSiemensColorGameApi.put( "javax.microedition.lcdui.game.TiledLayer", "com.siemens.mp.color_game.TiledLayer; import de.enough.polish.ui.StyleSheet" );
		this.polishToSiemensColorGameApi.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite; import de.enough.polish.ui.StyleSheet");
		
		toPolish = new HashMap( toPolish );
		toPolish.put( "javax.microedition.lcdui.game.GameCanvas", "de.enough.polish.ui.game.GameCanvas; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.game.Layer", "de.enough.polish.ui.game.Layer; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.game.LayerManager", "de.enough.polish.ui.game.LayerManager; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.game.TiledLayer", "de.enough.polish.ui.game.TiledLayer; import de.enough.polish.ui.StyleSheet");
		toPolish.put( "javax.microedition.lcdui.game.*", "de.enough.polish.ui.game.*; import de.enough.polish.ui.StyleSheet");
		this.midp1ToPolish = toPolish;
		
		// that was peanuts now the tricky part comes:
		// when javax.microedition.lcdui.* is imported, there are still
		// some base classes which cannot be implemented by the polish framework,
		// these have to be inserted as well:
		this.defaultPackageCompleteMidp1 = 
				"javax.microedition.lcdui.CommandListener; " +
				"import javax.microedition.lcdui.Alert; " +
				"import javax.microedition.lcdui.AlertType; " + 
				"import javax.microedition.lcdui.Canvas; " + 
				"import javax.microedition.lcdui.Command; " +
				"import javax.microedition.lcdui.Display; " +
				"import javax.microedition.lcdui.Displayable; " + 
				"import javax.microedition.lcdui.Font; " +
				"import javax.microedition.lcdui.Graphics; " +
				"import javax.microedition.lcdui.Image";
		this.completeMidp1 = this.defaultPackageCompleteMidp1 +
				"; import de.enough.polish.ui.*";
		

		this.defaultPackageCompleteMidp2 = 
				"javax.microedition.lcdui.CommandListener; " +
				"import javax.microedition.lcdui.Alert; " +
				"import javax.microedition.lcdui.AlertType; " +
				"import javax.microedition.lcdui.Canvas; " + 
				"import javax.microedition.lcdui.Command; " +
				"import javax.microedition.lcdui.Display; " +
				"import javax.microedition.lcdui.Displayable; " +
				"import javax.microedition.lcdui.Font; " +
				"import javax.microedition.lcdui.Graphics; " +
				"import javax.microedition.lcdui.Image";
		this.completeMidp2 = this.defaultPackageCompleteMidp2 +
				"; import de.enough.polish.ui.*";
		
		// init import statements to translate from the polish- to the J2ME-GUI:
		HashMap toJavax = new HashMap();
		toJavax.put( "de.enough.polish.ui.*", "javax.microedition.lcdui.*" );
		// add all javaxToPolish-elements switched over: 
		Set set = toPolish.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			toJavax.put( toPolish.get(key), key );
		}
		this.polishToMidp2 = new HashMap( toJavax );
		// add game-API for MIDP/1.0 devices:
		toJavax.put( "javax.microedition.lcdui.game.GameCanvas", "de.enough.polish.ui.game.GameCanvas");
		toJavax.put( "javax.microedition.lcdui.game.Layer", "de.enough.polish.ui.game.Layer");
		toJavax.put( "javax.microedition.lcdui.game.LayerManager", "de.enough.polish.ui.game.LayerManager");
		toJavax.put( "javax.microedition.lcdui.game.Sprite", "de.enough.polish.ui.game.Sprite");
		toJavax.put( "javax.microedition.lcdui.game.TiledLayer", "de.enough.polish.ui.game.TiledLayer");
		toJavax.put( "javax.microedition.lcdui.game.*", "de.enough.polish.ui.game.*");
		this.polishToMidp1 = toJavax;
	}
	
	// this method is only available for testing purposes 
	protected boolean processImports( boolean usePolishGui, boolean isMidp1, StringList sourceCode ) {
		return processImports(usePolishGui, isMidp1, sourceCode, null, null );
	}
	
	/**
	 * Changes the import statements for the given file.
	 * 
	 * @param usePolishGui True when the polish-GUI should be used instead of the standard J2ME-GUI.
	 * @param isMidp1 True when the MIDP/1-standard is supported, false when the MIDP/2-standard is supported.
	 * @param sourceCode The source code
	 * @param device the current device\
	 * @param preprocessor the preprocessor with all variables and symbols
	 * @return True when the source code has been changed,
	 *         otherwise false is returned.
	 */
	public boolean processImports( boolean usePolishGui, boolean isMidp1, StringList sourceCode, Device device, Preprocessor preprocessor ) {
		boolean usePolishGameApi = false;
		boolean supportsSiemensColorGameApi = false;
		if (preprocessor != null) {
			usePolishGameApi = "true".equals(preprocessor.getVariable("polish.usePolishGameApi"));
			supportsSiemensColorGameApi = preprocessor.hasSymbol("polish.api.siemens-color-game-api");
		}
		boolean useDefaultPackage = preprocessor.useDefaultPackage();
		HashMap translations;
		if (usePolishGui) {
			if (isMidp1 || usePolishGameApi ) {
				if (supportsSiemensColorGameApi) {
					if (useDefaultPackage) {
						translations = this.defaultPackageSiemensColorGameApiToPolish;
					} else {
						translations = this.siemensColorGameApiToPolish;
					}
				} else {
					translations = this.midp1ToPolish;
				}
			} else {
				translations = this.midp2ToPolish;
			}
		} else {
			if (isMidp1 || usePolishGameApi ) {
				if (supportsSiemensColorGameApi) {
					translations = this.polishToSiemensColorGameApi;
				} else {
					translations = this.polishToMidp1;
				}
			} else {
				translations = this.polishToMidp2;
			}
		}
		translations = addDynamicTranslations( translations, usePolishGui, isMidp1, device, preprocessor, useDefaultPackage );
		
		//System.out.println("ImportConverter: useDefaultPackage=" + useDefaultPackage );
		TextFileManager textFileManager = preprocessor.getTextFileManager();
		
		// go through the code and search for import statements:
		boolean changed = false;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent().trim();
			if (line.startsWith("import ")) {
				String importContent = line.substring( 7, line.length() -1 ).trim();
				String replacement = null;
				if ( usePolishGui ) {
					/*
					if (!changed) {
						sourceCode.setCurrent( line + " import de.enough.polish.ui.StyleSheet;");
						changed = true;
					}
					*/
					// translate import statements from javax.microedition.lcdui to polish:
					replacement = (String) translations.get(importContent);
					if ( replacement == null 
							&& "javax.microedition.lcdui.*".equals( importContent) ) 
					{
						// check for the javax.microedition.lcdui.* import:
						// changed = true;
						// insert replacement:
						if (useDefaultPackage) {
							if (isMidp1) {
								//sourceCode.setCurrent( this.completeMidp1 );
								replacement = this.defaultPackageCompleteMidp1;
							} else {
								//sourceCode.setCurrent( this.completeMidp2 );
								replacement = this.defaultPackageCompleteMidp2;
							}
						} else {
							if (isMidp1) {
								//sourceCode.setCurrent( this.completeMidp1 );
								replacement = this.completeMidp1;
							} else {
								//sourceCode.setCurrent( this.completeMidp2 );
								replacement = this.completeMidp2;
							}
						}
					}
				} else {
					// translate import statements from polish to javax.microedition.lcdui:
					replacement = (String) translations.get(importContent);
					/*if ( replacement != null) {
						changed = true;
						sourceCode.setCurrent("import " + replacement + ";");
					}
					*/
				}
				if ( useDefaultPackage ) {
					if ( replacement != null ) {
						if ( textFileManager.containsImport(replacement) ) {
							sourceCode.setCurrent( "//" + line);
						} else {
							sourceCode.setCurrent("import " + replacement + ";");
						}
						changed = true;
					} else {
						if ( textFileManager.containsImport(importContent) ) {
							sourceCode.setCurrent( "//" + line);
							changed = true;
						}						
					}
					
				} else if ( replacement != null ) {
					changed = true;
					sourceCode.setCurrent("import " + replacement + ";");
				} else if ( usePolishGui && !changed && !useDefaultPackage ) {
					changed = true;
					sourceCode.setCurrent( line + " import de.enough.polish.ui.StyleSheet;");
				}
				
			} else if (line.startsWith("public class ")) {
				break;
			} else if (line.startsWith("protected class ")) {
				break;
			} else if (line.startsWith("class ")) {
				break;
			} else if (line.startsWith("public interface ")) {
				break;
			} else if (line.startsWith("protected interface ")) {
				break;
			} else if (line.startsWith("interface ")) {
				break;
			}
		}
		return changed;
	}

	/**
	 * Adds translations which depend on the current device. 
	 * An example is the inclusion of the WMAPI-wrapper-API, 
	 * which is only included on several conditions.
	 * 
	 * @param translations the basic translations
	 * @param usePolishGui True when the polish-GUI should be used instead of the standard J2ME-GUI.
	 * @param isMidp1 True when the MIDP/1-standard is supported, false when the MIDP/2-standard is supported.
	 * @param device the current device\
	 * @param preprocessor the preprocessor with all variables and symbols
	 * @return either the same translations map, when no changes are done, or
	 * 	 a new modified map.
	 */
	private HashMap addDynamicTranslations(HashMap translations, boolean usePolishGui, boolean isMidp1, Device device, Preprocessor preprocessor, boolean useDefaultPackage) {
		// by default do not change is applied:
		HashMap newTranslations = translations;
		// check for WMAPI-Wrapper:
		boolean useWMAPIWrapper = preprocessor.hasSymbol("polish.useWMAPIWrapper")
			&& !preprocessor.hasSymbol("polish.api.wmapi")
			&& preprocessor.hasSymbol("polish.supportsWMAPIWrapper");
		if (useWMAPIWrapper) {
			newTranslations = new HashMap( translations );
			if (useDefaultPackage) {
				newTranslations.put( "javax.wireless.messaging.*", "");
				newTranslations.put( "javax.wireless.messaging.Message", "");
				newTranslations.put( "javax.wireless.messaging.BinaryMessage", "");
				newTranslations.put( "javax.wireless.messaging.TextMessage", "");
				newTranslations.put( "javax.wireless.messaging.MessageConnection", "");
				newTranslations.put( "javax.wireless.messaging.MessageListener", "");
				newTranslations.put( "javax.wireless.messaging.MessageListener", "");
				newTranslations.put( "javax.microedition.io.Connector", "");
			} else {
				newTranslations.put( "javax.wireless.messaging.*", "de.enough.polish.messaging.*");
				newTranslations.put( "javax.wireless.messaging.Message", "de.enough.polish.messaging.Message");
				newTranslations.put( "javax.wireless.messaging.BinaryMessage", "de.enough.polish.messaging.BinaryMessage");
				newTranslations.put( "javax.wireless.messaging.TextMessage", "de.enough.polish.messaging.TextMessage");
				newTranslations.put( "javax.wireless.messaging.MessageConnection", "de.enough.polish.messaging.MessageConnection");
				newTranslations.put( "javax.wireless.messaging.MessageListener", "de.enough.polish.messaging.MessageListener");
				newTranslations.put( "javax.wireless.messaging.MessageListener", "de.enough.polish.messaging.MessageListener");
				newTranslations.put( "javax.microedition.io.Connector", "de.enough.polish.io.Connector");
			}
			// process javax.microedition.io.*:
			if (isMidp1) {
				if (useDefaultPackage) {
					newTranslations.put( "javax.microedition.io.*", "import javax.microedition.io.Connection; import javax.microedition.io.ContentConnection; import javax.microedition.io.Datagram; import javax.microedition.io.DatagramConnection; import javax.microedition.io.InputConnection; import javax.microedition.io.OutputConnection; import javax.microedition.io.StreamConnection; import javax.microedition.io.StreamConnectionNotifier; import javax.microedition.io.ConnectionNotFoundException; import javax.microedition.io.HttpConnection");
				} else {
					newTranslations.put( "javax.microedition.io.*", "de.enough.polish.io.Connector; import javax.microedition.io.Connection; import javax.microedition.io.ContentConnection; import javax.microedition.io.Datagram; import javax.microedition.io.DatagramConnection; import javax.microedition.io.InputConnection; import javax.microedition.io.OutputConnection; import javax.microedition.io.StreamConnection; import javax.microedition.io.StreamConnectionNotifier; import javax.microedition.io.ConnectionNotFoundException; import javax.microedition.io.HttpConnection");
				}
			} else {
				if (useDefaultPackage) {
					newTranslations.put( "javax.microedition.io.*", "import javax.microedition.io.Connection; import javax.microedition.io.ContentConnection; import javax.microedition.io.Datagram; import javax.microedition.io.DatagramConnection; import javax.microedition.io.InputConnection; import javax.microedition.io.OutputConnection; import javax.microedition.io.StreamConnection; import javax.microedition.io.StreamConnectionNotifier; import javax.microedition.io.ConnectionNotFoundException; import javax.microedition.io.HttpConnection; import javax.microedition.io.PushRegistry; import javax.microedition.io.UDPDatagramConnection; import javax.microedition.io.ServerSocketConnection; import javax.microedition.io.SocketConnection; import javax.microedition.io.SecurityInfo; import javax.microedition.io.SecureConnection; import javax.microedition.io.HttpsConnection; import javax.microedition.io.CommConnection");
				} else {
					newTranslations.put( "javax.microedition.io.*", "de.enough.polish.io.Connector; import javax.microedition.io.Connection; import javax.microedition.io.ContentConnection; import javax.microedition.io.Datagram; import javax.microedition.io.DatagramConnection; import javax.microedition.io.InputConnection; import javax.microedition.io.OutputConnection; import javax.microedition.io.StreamConnection; import javax.microedition.io.StreamConnectionNotifier; import javax.microedition.io.ConnectionNotFoundException; import javax.microedition.io.HttpConnection; import javax.microedition.io.PushRegistry; import javax.microedition.io.UDPDatagramConnection; import javax.microedition.io.ServerSocketConnection; import javax.microedition.io.SocketConnection; import javax.microedition.io.SecurityInfo; import javax.microedition.io.SecureConnection; import javax.microedition.io.HttpsConnection; import javax.microedition.io.CommConnection");
				}
			}			
		}
		return newTranslations;
	}

	/**
	 * Removes any usage of packages (as long as these exist within the source code).
	 * 
	 * @param list the source code
	 * @param textFileManager the manager of source code files
	 */
	public void removeDirectPackages(StringList list, TextFileManager textFileManager) {
		//System.out.println("removing direct packages....");
		String[] lines = list.getArray();
		/*
		for (int i = 0; i < lines.length; i++) {
			System.out.println( lines[i] );
		}
		System.out.println("=========================");
		*/
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = TextUtil.replace( line, "de.enough.polish.ui.borders.", "" );
			line = TextUtil.replace( line, "de.enough.polish.ui.backgrounds.", "" );
			line = TextUtil.replace( line, "de.enough.polish.ui.containerviews.", "" );
			lines[i] = line;
			String[] packageNames = getPackages( line, textFileManager );
			if (packageNames != null) {
				for (int j = 0; j < packageNames.length; j++) {
					String packageName = packageNames[j] + ".";
					for (int k = i; k < lines.length; k++) {
						line = lines[k];
						lines[k] = TextUtil.replace( line, packageName, "" );
					}
				}
			}			
		}
		/*
		System.out.println("=========================");
		System.out.println("=========================");
		*/
		list.setArray( lines );
	}

	/**
	 * Retrieves all words with dots in the given line.
	 * 
	 * @param line the input
	 * @param textFileManager the manager of source code files
	 * @return an array with words with dots
	 */
	private String[] getPackages(String line, TextFileManager textFileManager) {
		int dotIndex = line.indexOf('.');
		if (dotIndex == -1) {
			return null;
		}
		ArrayList packageNames = new ArrayList();
		char[] chars = line.toCharArray();
		while (dotIndex != -1 ) {
			int startIndex = dotIndex - 1;
			while ( startIndex > 0
					&& (Character.isJavaIdentifierPart( chars[startIndex ] )
						|| chars[startIndex] == '.' ))
			{
				startIndex--;
			}
			startIndex++;
			int endIndex = dotIndex + 1;
			int lastDotIndex = -1;
			//System.out.println("tail:");
			while ( endIndex < chars.length - 1 
				&& (Character.isJavaIdentifierPart( chars[endIndex ] )
						|| chars[endIndex] == '.' ) )
			{
				if ( chars[ endIndex ] == '.' ) {
					lastDotIndex = endIndex;
				}
				//System.out.print( chars[endIndex ] );
				endIndex++;
			}
			int length = lastDotIndex - startIndex;
			if (lastDotIndex == -1 || length < 2) {
				dotIndex = line.indexOf('.', dotIndex + 1);
				continue;
			}
			//System.out.println( "\ncomplete line=[" + line + "] - length=" + line.length() );
			String packageName = new String( chars, startIndex, length );
			//System.out.println("testing package [" + packageName + "]");
			boolean isPackage = textFileManager.containsPackage(packageName); 
			if ( !isPackage ) {
				// this could be a method or field as well:
				lastDotIndex = packageName.lastIndexOf('.');
				if ( lastDotIndex != -1 ) {
					packageName = packageName.substring(0, lastDotIndex);
					//System.out.println("testing package [" + packageName + "]");
					isPackage = textFileManager.containsPackage( packageName );
					/*
					if ( !isPackage ) {
						// this could be a method or field as well:
						lastDotIndex = packageName.lastIndexOf('.');
						if ( lastDotIndex != -1 ) {
							packageName = packageName.substring(0, lastDotIndex);
							//System.out.println("testing package [" + packageName + "]");
							isPackage = textFileManager.containsPackage( packageName );
						}
					}
					*/
				}
			}
			if (isPackage) {
				//System.out.println("adding package [" + packageName + "]");
				packageNames.add( packageName );
				dotIndex = line.indexOf('.', endIndex + 1);
			} else {
				dotIndex = line.indexOf('.', dotIndex + 1);
			}
		}
		return (String[]) packageNames.toArray( new String[ packageNames.size() ] );
	}
	
	
}
