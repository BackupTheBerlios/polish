/*
 * Created on 21-Jun-2004 at 13:05:20.
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.util.AbbreviationsGenerator;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.TextUtil;

/**
 * <p>Makes some standard preprocessing like the determination whether the Ticker-class is used etc.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        21-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessor extends CustomPreprocessor {
	
	private boolean isTickerUsed;
	private File abbreviationsFile;
	private File tickerFile;
	private AbbreviationsGenerator abbreviationsGenerator;
	private boolean isPopupUsed;
	private File popupFile;

	/**
	 * Creates a new uninitialised PolishLineProcessor 
	 */
	public PolishPreprocessor() {
		super();
		this.abbreviationsGenerator = new AbbreviationsGenerator();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
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
			this.abbreviationsFile = new File( device.getBaseDir() + File.separatorChar 
					+ "abbreviations.txt" );
			HashMap abbreviations;
			if (this.abbreviationsFile.exists()) {
				try {
					abbreviations = FileUtil.readPropertiesFile( this.abbreviationsFile );
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to load abbreviations of style-attributes: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			} else {
				abbreviations = new HashMap();
			}
			this.abbreviationsGenerator.setAbbreviationsMap(abbreviations);
			this.preprocessor.getStyleSheet().setAttributesAbbreviations( abbreviations );
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
			FileUtil.writePropertiesFile( this.abbreviationsFile, this.abbreviationsGenerator.getAbbreviationsMap() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to write abbreviations of style-properties to [" + this.abbreviationsFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
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
			this.preprocessor.removeSymbol("polish.skipTicker");
			if (!this.tickerFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/Screen.java");
				this.preprocessor.addToPreprocessQueue("de\\enough\\polish\\ui\\Screen.java");
				try {
					this.tickerFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create Ticker indicator file [" + this.tickerFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}
		} else {
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/Screen.java");
			this.preprocessor.removeFromPreprocessQueue("de\\enough\\polish\\ui\\Screen.java");
			this.preprocessor.addSymbol("polish.skipTicker");
		}
		
		// indicate the usage of a POPUP item:
		if (this.isPopupUsed || this.popupFile.exists()) {
			this.preprocessor.addSymbol( "polish.usePopupItem" );
			if (!this.popupFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
				this.preprocessor.addToPreprocessQueue("de\\enough\\polish\\ui\\ChoiceGroup.java");
				try {
					this.popupFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create POPUP indicator file [" + this.popupFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}			
		} else {
			this.preprocessor.removeSymbol( "polish.usePopupItem" );
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
			this.preprocessor.removeFromPreprocessQueue("de\\enough\\polish\\ui\\ChoiceGroup.java");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		if (!this.isUsingPolishGui) {
			return;
		}
		while (lines.next() ) {
			// check for ticker:
			String line = lines.getCurrent();

			
			// check for style-property-usage:
			int startPos = line.indexOf("style.getProperty(");
			if (startPos != -1) {
				int endPos = line.indexOf( ')', startPos );
				if (endPos == -1) {
					throw new BuildException ("Invalid style-usage in class [" 
							+ className + "] at line [" + lines.getCurrentIndex()
							+ "]: style.getProperty( \"name\" ); always needs to be on a single line. "
							+ " This line is invalid: " + line );
				}
				
				String property = line.substring( startPos + "style.getProperty(".length(),
						endPos ).trim();
				if (property.charAt(0) != '"' || property.charAt( property.length() - 1) != '"') {
					throw new BuildException ("Invalid style-usage in class [" 
							+ className + "] at line [" + lines.getCurrentIndex()
							+ "]: style.getProperty( \"name\" ); always needs to use the property-name directly (not a variable). "
							+ " This line is invalid: " + line );
				}
				String key = property.substring( 1, property.length() - 1);
				String abbreviation = this.abbreviationsGenerator.getAbbreviation(
						key, this.preprocessor.hasSymbol("polish.css." + key) );
				// check if this property is used at all:
				if ( abbreviation == null ) {
					//System.out.println("skipping attribute [" + key + "]");
					continue;
				}
				line = TextUtil.replace( line, property, "\"" + abbreviation + "\"" );
				lines.setCurrent( line );
				continue;
			}
			
			if (this.isInJ2MEPolishPackage) {
				// skip the next checks, when the J2ME Polish package is preprocessed:
				continue;
			}
			
			startPos = line.indexOf(".getTicker"); 
			if ( startPos != -1) {
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				line = line.substring(0, startPos)
					+ ".getPolishTicker"
					+ line.substring( startPos + 10 );
				this.isTickerUsed = true;
				lines.setCurrent( line );
				continue;
			}
			startPos = line.indexOf(".setTicker");
			if ( startPos != -1) {
				int commentPos = line.indexOf("//");
				if (commentPos != -1 && commentPos < startPos) {
					continue;
				}
				line = line.substring(0, startPos)
					+ ".setPolishTicker"
					+ line.substring( startPos + 10 );
				this.isTickerUsed = true;
				lines.setCurrent( line );
				continue;
			}
			
			// check for Choice.POPUP:
			startPos = line.indexOf(".POPUP");
			if (startPos != -1) {
				this.isPopupUsed = true;
				continue;
			}
		}
	}
	

}
