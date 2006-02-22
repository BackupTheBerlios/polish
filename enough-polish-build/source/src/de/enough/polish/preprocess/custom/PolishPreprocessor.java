/*
 * Created on 21-Jun-2004 at 13:05:20.
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
package de.enough.polish.preprocess.custom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.preprocess.CssAttribute;
import de.enough.polish.preprocess.CssAttributesManager;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Makes some standard preprocessing like the determination whether the Ticker-class is used etc.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        21-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessor extends CustomPreprocessor {
	
	private boolean isTickerUsed;
	private File stylePropertyIdsFile;
	private File tickerFile;
	private IntegerIdGenerator idGenerator;
	private boolean isPopupUsed;
	private File popupFile;
	protected static final String SET_TICKER_STR = "([\\.|\\s]|^)setTicker\\s*\\(.+\\)";
	protected static final String GET_TICKER_STR = "([\\.|\\s]|^)getTicker\\s*\\(\\s*\\)";
	protected static final Pattern SET_TICKER_PATTERN = Pattern.compile( SET_TICKER_STR );
	protected static final Pattern GET_TICKER_PATTERN = Pattern.compile( GET_TICKER_STR );
	
	protected static final String GET_GRAPHICS_STR = "([\\.|\\s]|^)getGraphics\\s*\\(\\s*\\)";
	protected static final Pattern GET_GRAPHICS_PATTERN = Pattern.compile( GET_GRAPHICS_STR );
	
	//protected static final String SET_CURRENT_ITEM_STR = "[\\w|\\.]+\\s*\\.\\s*setCurrentItem\\s*\\([\\w|\\.]+\\s*\\)";
	protected static final String JAVA_VAR_STR = "[\\w|\\.|_]+";
	protected static final String GET_DISPLAY_METHOD_STR = "Display\\s*\\.\\s*getDisplay\\s*\\(\\s*" + JAVA_VAR_STR + "\\s*\\)\\s*";
	protected static final String SET_CURRENT_ITEM_STR = "(" + JAVA_VAR_STR + "|" + GET_DISPLAY_METHOD_STR + ")\\s*\\.\\s*setCurrentItem\\s*\\(.+\\)";
	protected static final Pattern SET_CURRENT_ITEM_PATTERN = Pattern.compile( SET_CURRENT_ITEM_STR );
	
	protected static final String ALERT_CONSTRUCTOR = "new\\s+Alert\\s*\\(\\s*.+\\)" ;
	protected static final String SET_CURRENT_ALERT_DISPLAYABLE_STR = "[\\w|\\.]+\\s*\\.\\s*setCurrent\\s*\\(\\s*[\\w*|\\.]+\\s*,\\s*[\\w*|\\.]+\\s*\\)";
	protected static final Pattern SET_CURRENT_ALERT_DISPLAYABLE_PATTERN = Pattern.compile( SET_CURRENT_ALERT_DISPLAYABLE_STR );
	
	private CssAttributesManager cssAttributesManager;
	private boolean usesBlackBerry;

	/**
	 * Creates a new uninitialised PolishPreprocessor 
	 */
	public PolishPreprocessor() {
		super();
	}
	
	

	public void init(Preprocessor processor, PreprocessorSetting setting ) {
		super.init(processor, setting );
		this.idGenerator = new IntegerIdGenerator();
		this.cssAttributesManager = processor.getCssAttributesManager();
		//boolean allowAllCssAttributes = "true".equals( processor.getVariable("xxx.allowAllAttributes") );
		//System.out.println("allowing all CSS attributes: " + allowAllCssAttributes  + " ---> " + processor.getVariable("xxx.allowAllAttributes"));
		if (this.cssAttributesManager != null) {
			CssAttribute[] attributes = this.cssAttributesManager.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				CssAttribute attribute = attributes[i];
				int id = attribute.getId();
				String name = attribute.getName();
				if (id != -1) {
					this.idGenerator.addId( name, id );
					//System.out.println("Using ID [" + id + "] for CSS-attribute [" + attribute.getName() + "]");
				}
				/*
				if (allowAllCssAttributes) {
					processor.addSymbol( "polish.css" + name );
				}
				*/
			}
			// now register all attributes that have no ID assigned:
			for (int i = 0; i < attributes.length; i++) {
				CssAttribute attribute = attributes[i];
				int id = attribute.getId();
				String name = attribute.getName();
				if (id == -1) {
					this.idGenerator.getId( name, true );
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		Environment env = device.getEnvironment();
		this.usesBlackBerry = env.hasSymbol("polish.blackberry") && usesPolishGui;
		if (usesPolishGui) {
			// init ticker setting:
			this.tickerFile = new File( device.getBaseDir() + File.separatorChar 
					+ "TickerIndicator" );
			this.isTickerUsed = false;
			
			// init popup setting:
			this.popupFile = new File( device.getBaseDir() + File.separatorChar 
					+ "PopupIndicator" );
			this.isPopupUsed = false;
			
			// init abbreviations of style-properties:
			this.stylePropertyIdsFile = new File( device.getBaseDir() + File.separatorChar 
					+ "abbreviations.txt" );
			if (this.stylePropertyIdsFile.exists()) {
				//System.out.println("reading css attributes from " + this.stylePropertyIdsFile.getAbsolutePath() );
				try {
					HashMap idsByAttribute = FileUtil.readPropertiesFile( this.stylePropertyIdsFile );
					this.idGenerator.setIdsMap(idsByAttribute);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to load abbreviations of style-attributes: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}
			//System.out.println( "PolishPreprocessor: preprocessor == null: " + (this.preprocessor == null) );
			//System.out.println("styleSheet == null: "  + (this.preprocessor.getStyleSheet() == null) );
			//System.out.println("idGenerator == null: "  + (this.idGenerator == null ) );
			this.preprocessor.getStyleSheet().setAttributesIds( this.idGenerator.getIdsMap() );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDeviceEnd(de.enough.polish.Device, boolean)
	 */
	public void notifyDeviceEnd(Device device, boolean usesPolishGui) 
	{
		super.notifyDeviceEnd(device, usesPolishGui);
		if (!this.isUsingPolishGui) {
			return;
		}
		
		// write found abbreviations:
		try {
			FileUtil.writePropertiesFile( this.stylePropertyIdsFile, this.idGenerator.getIdsMap() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to write IDs of style-properties to [" + this.stylePropertyIdsFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyPolishPackageStart()
	 */
	public void notifyPolishPackageStart() {
		super.notifyPolishPackageStart();
		if (!this.isUsingPolishGui) {
			return;
		}
		
		// set settings for usage of Ticker:
		if (this.isTickerUsed || this.tickerFile.exists()) {
			this.environment.removeSymbol("polish.skipTicker");
			if (!this.tickerFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/Screen.java");
				try {
					this.tickerFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create Ticker indicator file [" + this.tickerFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}
		} else {
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/Screen.java");
			this.environment.addSymbol("polish.skipTicker");
		}
		
		// indicate the usage of a POPUP item:
		if (this.isPopupUsed || this.popupFile.exists()) {
			this.environment.addSymbol( "polish.usePopupItem" );
			if (!this.popupFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
				try {
					this.popupFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create POPUP indicator file [" + this.popupFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}			
		} else {
			this.environment.removeSymbol( "polish.usePopupItem" );
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		if (!this.isUsingPolishGui) {
			return;
		}
		//System.out.println("PolishPreprocessor: processing class " + className );
		while (lines.next() ) {
			// check for ticker:
			String line = lines.getCurrent();

			
			// check for style-property-usage:
			int startPos = -1;
			String methodName = "style.getProperty(";
			startPos = line.indexOf(methodName);
			if (startPos == -1) {
				methodName = "style.getIntProperty(";
				startPos = line.indexOf(methodName);
				if (startPos == -1) {
					methodName = "style.getBooleanProperty(";
					startPos = line.indexOf(methodName);
				}
				if (startPos == -1) {
					methodName = "style.getObjectProperty(";
					startPos = line.indexOf(methodName);
				}
			}
			if (startPos != -1) {
				int endPos = line.indexOf( ')', startPos );
				if (endPos == -1) {
					throw new BuildException ( getErrorStart(className, lines) + ": Invalid style-usage: "
							+ "style.getProperty( \"name\" ); always needs to be on a single line. "
							+ " This line is invalid: " + line );
				}
				
				String property = line.substring( startPos + methodName.length(),
						endPos ).trim();
				//System.out.println("last line: " + lines.getPrevious() + "\ncurrent=" + lines.getCurrent() + "\nnext = " + lines.getNext() );
				if (property.charAt(0) != '"' || property.charAt( property.length() - 1) != '"') {
					throw new BuildException (getErrorStart(className, lines) + ": Invalid style-usage: "
							+ "style.getProperty( \"name\" ); always needs to use the property-name directly (not a variable). "
							+ " This line is invalid: " + line );
				}
				String key = property.substring( 1, property.length() - 1);
				int id = this.idGenerator.getId(
						key, this.environment.hasSymbol("polish.css." + key) );
				// check if this property is used at all:
				if ( id == -1 ) {
					//System.out.println("skipping attribute [" + key + "]");
					continue;
				}
				//System.out.println("got id " + id + " for key " + key);
				line = StringUtil.replace( line, property, "" + id );
				//System.out.println("style: setting line[" + lines.getCurrentIndex() + " to = [" + line + "]");
				lines.setCurrent( line );
				continue;
			}
			
			if (this.isInJ2MEPolishPackage) {
				// skip the next checks, when the J2ME Polish package is preprocessed:
				continue;
			}
			
			startPos = line.indexOf("getTicker"); 
			if ( startPos != -1) {
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				Matcher matcher = GET_TICKER_PATTERN.matcher( line );
				boolean matchFound = false;
				while (matcher.find()) {
					matchFound = true;
					String group = matcher.group();
					String replacement = StringUtil.replace( group, "getTicker", "getPolishTicker");
					line = StringUtil.replace( line, group, replacement );
				}
				if (matchFound) {
					this.isTickerUsed = true;
					lines.setCurrent( line );					
				}
				continue;
			}
			startPos = line.indexOf("setTicker");
			if ( startPos != -1) {
				//System.out.println("setTicker found in line " + line );
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				Matcher matcher = SET_TICKER_PATTERN.matcher( line );
				boolean matchFound = false;
				while (matcher.find()) {
					matchFound = true;
					String group = matcher.group();
					String replacement = StringUtil.replace( group, "setTicker", "setPolishTicker");
					line = StringUtil.replace( line, group, replacement );
				}
				if (matchFound) {
					this.isTickerUsed = true;
					lines.setCurrent( line );
					//System.out.println( "line is now " + line );
				}
				continue;
			}
			
			// check for display.setCurrentItem:
			startPos = line.indexOf("setCurrentItem");
			if ( startPos != -1) {
				//System.out.println("setCurrentItem found in line " + line );
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				Matcher matcher = SET_CURRENT_ITEM_PATTERN.matcher( line );
				if (matcher.find()) {
					String group = matcher.group();
					//System.out.println("group = [" + group + "]");
					int parenthesisPos = group.indexOf('(', startPos );
					String displayVar = group.substring(0, parenthesisPos);
					int dotPos = displayVar.lastIndexOf('.');
					displayVar = displayVar.substring( 0, dotPos ).trim();
					String itemVar = group.substring( parenthesisPos + 1, group.length() -1 ).trim();
					String replacement = itemVar + ".show( " + displayVar + " )"; 
					//System.out.println("replacement = [" + replacement + "].");
					line = StringUtil.replace( line, group, replacement );
					//System.out.println("line = [" + line + "]");
					lines.setCurrent( line );
				}
				continue;
			}
			
			// check for display.setCurrent( Alert, Displayable ):
			startPos = line.indexOf("setCurrent");
			if ( startPos != -1) {
				//System.out.println("setCurrent found in line " + line );
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				Matcher matcher = SET_CURRENT_ALERT_DISPLAYABLE_PATTERN.matcher( line );
				if (matcher.find()) {
					String group = matcher.group();
					//System.out.println("group = [" + group + "]");
					int parenthesisPos = group.indexOf('(');
					String displayVar = group.substring(0, parenthesisPos);
					int dotPos = displayVar.lastIndexOf('.');
					displayVar = displayVar.substring( 0, dotPos ).trim();
					String alertDisplayableVars = group.substring( parenthesisPos + 1, group.length() -1 ).trim();
					//int commaPos = alertDisplayableVars.indexOf('.');
					String replacement = "Alert.setCurrent( " + displayVar + ", " + alertDisplayableVars  + " )"; 
					//System.out.println("replacement = [" + replacement + "].");
					line = StringUtil.replace( line, group, replacement );
					//System.out.println("line = [" + line + "]");
					lines.setCurrent( line );
				}
				continue;
			}			

			
			// check for Choice.POPUP:
			startPos = line.indexOf(".POPUP");
			if (startPos != -1) {
				this.isPopupUsed = true;
				continue;
			}
			
			// check for GameCanvase.getGraphics() on BlackBerry phones:
			if (this.usesBlackBerry) {
				startPos = line.indexOf("getGraphics"); 
				if ( startPos != -1) {
					int commentPos = line.indexOf("//");
					if (commentPos != -1 && commentPos < startPos) {
						continue;
					}
					Matcher matcher = GET_GRAPHICS_PATTERN.matcher( line );
					boolean matchFound = false;
					while (matcher.find()) {
						matchFound = true;
						String group = matcher.group();
						String replacement = StringUtil.replace( group, "getGraphics", "getPolishGraphics");
						line = StringUtil.replace( line, group, replacement );
					}
					if (matchFound) {
						this.isTickerUsed = true;
						lines.setCurrent( line );					
					}
					continue;
				}
			}

		}
	}
	

}
