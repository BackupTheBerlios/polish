/*
 * Created on 16-Jan-2004 at 12:17:12.
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
package de.enough.polish.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.PolishProject;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;
import de.enough.polish.util.TextFileManager;

/**
 * <p>Preprocesses source code.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Preprocessor {
	
	private static final int DIRECTIVE_FOUND = 1;
	/**
	 * A value indicating that the file has been changed.
	 * CHANGED has the value 2. 
	 */
	public static final int CHANGED = 2;
	/**
	 * A value indicating that the file has not been changed.
	 * NOT_CHANGED has the value 4. 
	 */
	public static final int NOT_CHANGED = 4;
	/**
	 * A value indicating that the file should be skipped altogether.
	 * This can be when e.g. a MIDP2-emulating class should not be
	 * copied to a MIDP2-system. 
	 * An example are the de.enough.polish.ui.game-classes.
	 * 
	 * SKIP_FILE has the value 8. 
	 */
	public static final int SKIP_FILE = 8;
	
	public static final Pattern DIRECTIVE_PATTERN = 
		Pattern.compile("\\s*(//#if\\s+|//#ifdef\\s+|//#ifndef\\s+|//#elif\\s+|//#elifdef\\s+|//#elifndef\\s+|//#else|//#endif|//#include\\s+|//#endinclude|//#style |//#debug|//#mdebug|//#enddebug|//#define\\s+|//#undefine\\s+|//#=\\s+|//#condition\\s+|//#message\\s+|//#todo\\s+|//#foreach\\s+|//#abort)");

	private DebugManager debugManager;
	private File destinationDir;
	/** holds all defined variables */
	//private HashMap variables;
	/** holds all defined symbols */
	//private HashMap symbols;
	private boolean backup;
	private boolean indent;
	boolean enableDebug;
	private String newExtension;
	private HashMap withinIfDirectives;
	private HashMap ignoreDirectives;
	private HashMap supportedDirectives;
	private int ifDirectiveCount;
	//private BooleanEvaluator booleanEvaluator;
	private StyleSheet styleSheet;
	private boolean usePolishGui;
	private CustomPreprocessor[] customPreprocessors;
	protected static final Pattern SYSTEM_PRINT_PATTERN = Pattern.compile(
				"System.(out|err).print(ln)?\\s*\\(" );
	private HashMap preprocessQueue;
	private boolean useDefaultPackage;
	private TextFileManager textFileManager;
	private CssAttributesManager cssAttributesManager;
	private final Environment environment;
	private final boolean replacePropertiesWithoutDirective;

	/**
	 * Creates a new Preprocessor - usually for a specific device or a device group.
	 * 
	 * @param project the project settings
	 * @param environment environment settings
	 * @param destinationDir the destination directory for the preprocessed files
	 * @param backup true when the found source files should be backuped
	 * @param indent true when comments should be intended
	 * @param replacePropertiesWithoutDirective true when ${name} properties should be replaced without using the //#= directive
	 * @param newExt the new extension for preprocessed files
	 */
	public Preprocessor(
			PolishProject project,
			Environment environment, File destinationDir,
			boolean backup,
			boolean indent,
			boolean replacePropertiesWithoutDirective,
			String newExt) 
	{
		this.replacePropertiesWithoutDirective = replacePropertiesWithoutDirective;
		this.debugManager = project.getDebugManager();
		this.enableDebug = project.isDebugEnabled();
		this.usePolishGui = project.usesPolishGui();
		this.backup = backup;
		this.indent = indent;
		this.newExtension = newExt;
		this.destinationDir = destinationDir;
		this.preprocessQueue = new HashMap();
		this.environment = environment;
		
		this.withinIfDirectives = new HashMap();
		this.withinIfDirectives.put( "elifdef", Boolean.TRUE );
		this.withinIfDirectives.put( "elifndef", Boolean.TRUE );
		this.withinIfDirectives.put( "else", Boolean.TRUE );
		this.withinIfDirectives.put( "elif", Boolean.TRUE );
		this.withinIfDirectives.put( "endinclude", Boolean.TRUE );
		this.withinIfDirectives.put( "endif", Boolean.TRUE );
		this.withinIfDirectives.put( "debug", Boolean.TRUE );
		this.withinIfDirectives.put( "mdebug", Boolean.TRUE );
		this.ignoreDirectives = new HashMap();
		this.ignoreDirectives.put( "endinclude", Boolean.TRUE );
		this.supportedDirectives = new HashMap();
		this.supportedDirectives.putAll( this.withinIfDirectives );
		this.supportedDirectives.putAll( this.ignoreDirectives );
		this.supportedDirectives.put( "if", Boolean.TRUE );
		this.supportedDirectives.put( "ifdef", Boolean.TRUE );
		this.supportedDirectives.put( "include", Boolean.TRUE );
		this.supportedDirectives.put( "define", Boolean.TRUE );
		this.supportedDirectives.put( "undefine", Boolean.TRUE );
		this.supportedDirectives.put( "message", Boolean.TRUE );
	}
	
	/**
	 * Sets the custom preprocessors which allow a finer grained control of the preprocessing phase.
	 *  
	 * @param customPreprocessors an array of custom preprocessors 
	 */
	public void setCustomPreprocessors( CustomPreprocessor[] customPreprocessors ) {
		this.customPreprocessors = customPreprocessors;	
	}

	/**
	 * Adds a custom preprocesor to this preprocessor.
	 * 
	 * @param preprocessor the additional preprocessor
	 */
	public void addCustomPreprocessor(CustomPreprocessor preprocessor) {
		//System.out.println("Adding custom preprocessor [" + preprocessor.getClass().getName() + "]");
		if (this.customPreprocessors == null) {
			this.customPreprocessors = new CustomPreprocessor[] { preprocessor };
			return;
		}
		CustomPreprocessor[] processors = new CustomPreprocessor[ this.customPreprocessors.length + 1];
		processors[ this.customPreprocessors.length] = preprocessor;
		System.arraycopy(this.customPreprocessors, 0, processors, 0, this.customPreprocessors.length);
		this.customPreprocessors = processors;												 
	}
	
	public void clearCustomPreprocessors() {
		//System.out.println("Clearing custom preprocessors");
		this.customPreprocessors = null;
	}

	/**
	 * Notifies the processor that from now on source code from the J2ME Polish package is processed.
	 * This will last until the notifyDevice(...)-method is called.
	 */
	public void notifyPolishPackageStart() {
		if (this.customPreprocessors != null) {
			for (int i = 0; i < this.customPreprocessors.length; i++) {
				CustomPreprocessor processor = this.customPreprocessors[i];
				processor.notifyPolishPackageStart();
			}
		}
	}
	
	
	/**
	 * Notifies this preprocessor about a new device for which code is preprocessed.
	 *  
	 * @param device the new device
	 * @param usesPolishGui true when the J2ME Polish GUI is used for the new device
	 */
	public void notifyDevice( Device device, boolean usesPolishGui ) {
		if (this.customPreprocessors != null) {
			for (int i = 0; i < this.customPreprocessors.length; i++) {
				CustomPreprocessor processor = this.customPreprocessors[i];
				processor.notifyDevice(device, usesPolishGui);
			}
		}
	}
	
	/**
	 * Notifies this preprocessor about a new locale for which code is preprocessed.
	 *  
	 * @param locale the new locale, can be null
	 */
	public void notifyLocale( Locale locale ) {
		if (this.customPreprocessors != null) {
			for (int i = 0; i < this.customPreprocessors.length; i++) {
				CustomPreprocessor processor = this.customPreprocessors[i];
				processor.notifyLocale( locale );
			}
		}
	}
	
	/**
	 * Notifies this preprocessor about the end of preprocessing for the given device.
	 * 
	 * @param device the new device
	 * @param usesPolishGui true when the J2ME Polish GUI is used for the new device
	 */
	public void notifyDeviceEnd(Device device, boolean usesPolishGui) {
		if (this.customPreprocessors != null) {
			for (int i = 0; i < this.customPreprocessors.length; i++) {
				CustomPreprocessor processor = this.customPreprocessors[i];
				processor.notifyDeviceEnd(device, usesPolishGui);
			}
		}
	}
	
	/**
	 * Sets the direcotry to which the preprocessed files should be copied to.
	 * 
	 * @param path The target path. 
	 */
	public void setTargetDir(String path) {
		this.destinationDir = new File( path );
		if (!this.destinationDir.exists()) {
			this.destinationDir.mkdirs();
		}
	}

	/**
	 * Sets the symbols. Any old settings will be discarded.
	 * 
	 * @param symbols All new symbols, defined in a HashMap.
	 * @throws BuildException when an invalid symbol is defined (currently only "false" is checked);
	 * @deprecated use Environment for such settings now
	 */
	public void setSymbols(HashMap symbols) {
		// check symbols:
		Set keySet = symbols.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String symbol = (String) iter.next();
			if ("false".equals(symbol)) {
				throw new BuildException("The symbol [false] must not be defined. Please check your settings in your build.xml, devices.xml, groups.xml and vendors.xml");
			}
		}
		this.environment.setSymbols(symbols);
		//this.symbols = symbols;
		//this.booleanEvaluator.setEnvironment(symbols, this.variables);
	}
	
	/**
	 * Turns the support for the J2ME Polish GUI on or off.
	 *  
	 * @param usePolishGui true when the GUI is supported, false otherwise
	 * @deprecated use environment add/removeSymbol now
	 */
	public void setUsePolishGui( boolean usePolishGui ) {
		this.usePolishGui = usePolishGui;
		if (usePolishGui) {
			this.environment.addSymbol("polish.usePolishGui");
		} else {
			this.environment.removeSymbol("polish.usePolishGui");
		}
	}
	
	/**
	 * Adds a single symbol to the list.
	 * 
	 * @param name The name of the symbol.
	 * @deprecated use environment.addSymbol() instead
	 */
	public void addSymbol( String name ) {
		this.environment.addSymbol( name );
	}

	/**
	 * Removes a symbol from the list of defined symbols.
	 * 
	 * @param name The name of the symbol.
	 * @deprecated use environment.removeSymbol() instead
	 */
	public void removeSymbol(String name) {
		this.environment.removeSymbol(name);
	}
	
	/**
	 * Sets the variables, any old settings will be lost.
	 * 
	 * @param variables the variables.
	 * @deprecated use Evironment for such settings now
	 */
	public void setVariables(HashMap variables) {
		this.environment.setVariables(  variables );
		//this.booleanEvaluator.setEnvironment(this.symbols, variables);
	}
	
	/**
	 * Adds a variable to the list of existing variables.
	 * When a variable with the given name already exists, it
	 * will be overwritten.
	 * 
	 * @param name The name of the variable.
	 * @param value The value of the variable.
	 * @deprecated use environment.addVariable() instead
	 */
	public void addVariable( String name, String value ) {
		this.environment.addVariable( name, value );
	}

	/**
	 * Removes a variable from this preprocessor
	 * 
	 * @param name the variable name
	 * @deprecated use environment.removeVariable() instead
	 */
	public void removeVariable(String name) {
		this.environment.removeVariable(name);
	}

	/**
	 * Adds all the variables to the existing variables.
	 * When a variable already exists, it will be overwritten.
	 * 
	 * @param additionalVars A map of additional variables.
	 * @deprecated use environment.addVariables() instead
	 */
	public void addVariables( Map additionalVars ) {
		this.environment.getVariables().putAll(additionalVars);
	}
	
	/**
	 * Retrieves the evaluator for boolean expressions
	 * 
	 * @return the boolean evaluator with the symbols and variables of the current device
	 * @deprecated use environment.getBooleanEvaluator() instead
	 * @see Environment#getBooleanEvaluator()
	 */
	public BooleanEvaluator getBooleanEvaluator(){
		return this.environment.getBooleanEvaluator();
	}
	
	/**
	 * Preprocesses the given file and saves it to the destination directory.
	 * 
	 * @param sourceDir the directory containing the source file
	 * @param fileName the name (and path)of the source file
	 * @return true when the file was preprocessed or changed
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when the file could not be read or written
	 * @throws BuildException when the preprocessing fails
	 */
	public boolean preprocess( File sourceDir, String fileName ) 
	throws FileNotFoundException, IOException, BuildException
	{
		this.ifDirectiveCount = 0;
		File sourceFile = new File( sourceDir.getAbsolutePath()  + "/" + fileName );
		String[] sourceLines = FileUtil.readTextFile( sourceFile );
		StringList lines = new StringList( sourceLines );
		// set source directory:
		this.environment.addVariable( "polish.source", sourceDir.getAbsolutePath() );
		String className = fileName.substring(0, fileName.indexOf('.'));
		className = StringUtil.replace( className, "/", "." );
		int result = preprocess( className, lines );
		if (result == SKIP_FILE) {
			return false;
		}
		boolean preprocessed = (result == CHANGED);
		if (this.newExtension != null) {
			// change the extension of the file:
			int dotPos = fileName.indexOf('.');
			if (dotPos != -1) {
				fileName = fileName.substring( 0, dotPos + 1 ) + this.newExtension;
			} else {
				fileName += "." + this.newExtension;
			}
		}
		File destinationFile = new File( this.destinationDir.getAbsolutePath() + "/" + fileName );
		if (preprocessed 
				|| ( !destinationFile.exists() ) 
				|| ( sourceFile.lastModified() > destinationFile.lastModified() ) 
				) 
		{
			// the file needs to be written
			if (this.backup && destinationFile.exists() ) {
				// create backup:
				destinationFile.renameTo( new File( destinationFile.getAbsolutePath() + ".bak") );
			}
			// save preprocessed file:
			FileUtil.writeTextFile( destinationFile, lines.getArray() );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Resets this preprocessor.
	 * The internal state is reset to allow new preprocessing of other files. 
	 */
	public void reset() {
		this.ifDirectiveCount = 0;
	}

	/**
	 * Preprocesses the given source code.
	 * 
	 * @param className the name of the source file.
	 * @param lines the source code. Changes are made directly in the code.
	 * @return true when changes were made.
	 * @throws BuildException when the preprocessing fails.
	 */
	public int preprocess(String className, StringList lines) 
	throws BuildException 
	{
		// clear the temporary variables and symbols:
		this.environment.clearTemporarySettings();
		
		// set debugging preprocessing symbols:
		if (this.debugManager != null) {
			String[] debuggingSymbols = this.debugManager.getDebuggingSymbols( className );
			for (int i = 0; i < debuggingSymbols.length; i++) {
				String symbol = debuggingSymbols[i];
				this.environment.addTemporarySymbol( symbol );
			}
		}
		// adding all normal variables and symbols to the temporary ones:
		/*
		this.temporarySymbols.putAll( this.symbols );
		this.temporaryVariables.putAll( this.variables );
		*/
		
		boolean changed = false;
		if (this.customPreprocessors != null) {
			for (int i = 0; i < this.customPreprocessors.length; i++) {
				CustomPreprocessor processor = this.customPreprocessors[i];
				processor.processClass(lines, className);
				lines.reset();
			}
			changed = changed || lines.hasChanged();
		}
		try {
			while (lines.next()) {
				String line = lines.getCurrent();
				if (line.indexOf('#') != -1) {
					// could be that there is a preprocess instruction:
					int result = processSingleLine( className, lines, line, line.trim() );
					if (result == CHANGED ) {
						changed = true;
					} else if (result == SKIP_FILE) {
						return SKIP_FILE;
					}
				} else if (this.replacePropertiesWithoutDirective && line.indexOf("${") != -1) {
					String newLine = this.environment.writeProperties(line);
					if (!newLine.equals(line)) {
						changed = true;
						lines.setCurrent( newLine );
					}
				} else if (this.useDefaultPackage){
					//line = line.trim();
					if (line.startsWith("package ")) {
						changed = true;
						lines.setCurrent( "//" + line );
					}
				}
			}
		} catch (BuildException e) {
			reset();
			throw e;
		}
		if (changed) {
			return CHANGED;
		} else {
			return NOT_CHANGED;
		}
	}
	
	/**
	 * Checks a single line for a preprocessing directive.
	 * 
	 * @param className the name of the source file
	 * @param lines the source code lines
	 * @param line the specific line
	 * @param trimmedLine the same line but without spaces at the beginning or end
	 * @return either NOT_PROCESSED when no first level directive was found, 
	 * 			CHANGED when a directive was found and changes were made or
	 * 			DIRECTIVE_FOUND when a valid first level directive was found but no changes were made 
	 * @throws BuildException when there was a syntax error in the directives
	 */
	protected int processSingleLine( String className, StringList lines, String line, String trimmedLine ) 
	throws BuildException 
	{
		if (!trimmedLine.startsWith("//#")) {
			// this is not a preprocesssing directive:
			if (this.replacePropertiesWithoutDirective && trimmedLine.indexOf("${") != -1) {
				String newLine = this.environment.writeProperties( line );
				//System.out.println("replaced property without //#=: " + newLine);
				if (!newLine.equals( line )) {
					lines.setCurrent(newLine);
					return CHANGED;
				}
			}
			return NOT_CHANGED;
		}
		int tabPos  = trimmedLine.indexOf('\t');
		int spacePos = trimmedLine.indexOf(' ');
		if (spacePos == -1) {
			spacePos = tabPos;
		} else if ( (tabPos != -1) && (tabPos < spacePos) ) {
			spacePos = tabPos;
		}
		
		if (spacePos == -1) { // directive has no argument
			// only #debug and #mdebug can have no arguments:
			if (trimmedLine.equals("//#debug")) {
				boolean changed = processDebug( null, lines, className );
				if (changed) {
					return CHANGED;
				} else {
					return DIRECTIVE_FOUND;
				}
			} else if (trimmedLine.equals("//#mdebug")) {
				boolean changed = processMdebug( null, lines, className );
				if (changed) {
					return CHANGED;
				} else {
					return DIRECTIVE_FOUND;
				}
			}
			// when the argument is within an if-branch, there might be other valid directives:
			return checkInvalidDirective( className, lines, line, trimmedLine.substring(3).trim(), null );
		} else if (this.ifDirectiveCount > 0 &&  spacePos == 3) {
			// this is just an outcommented line
			return NOT_CHANGED;
		}
		String command = trimmedLine.substring(3, spacePos);
		String argument = trimmedLine.substring( spacePos + 1 ).trim();
		boolean changed = false;
		if ("condition".equals(command)) {
			//System.out.println("Checking #condition " + argument);
			// a precondition must be fullfilled for this source file:
			if (! checkIfCondition(argument, className, lines)) {
				//System.out.println("#condition: " + argument + " is not defined.");
				return SKIP_FILE;
			}
		} else if ("ifdef".equals(command)) {
			changed = processIfdef( argument, lines, className );
		} else if ("ifndef".equals(command)) {
			changed = processIfndef( argument, lines, className );
		} else if ("if".equals(command)) {
			changed = processIf( argument, lines, className );
		} else if ("define".equals(command)) {
			// define never changes the source directly:
			processDefine( argument, lines, className );
		} else if ("undefine".equals(command)) {
			// undefine never changes the source directly:
			processUndefine( argument, lines, className );
		} else if ("=".equals(command)) {
			changed = processVariable( argument, lines, className );
		} else if ("include".equals(command)) {
			changed = processInclude( argument, lines, className );
		} else if ("style".equals( command) ) {
			changed = processStyle( argument, lines, className );
		} else if ("debug".equals( command) ) {
			changed = processDebug( argument, lines, className );
		} else if ("mdebug".equals( command) ) {
			changed = processMdebug( argument, lines, className );
		} else if ("message".equals( command) ) {
			changed = processMessage( argument, lines, className );
		} else if ("todo".equals( command) ) {
			changed = processTodo( argument, lines, className );
		} else if ("foreach".equals( command) ) {
			changed = processForeach( argument, lines, className );
		} else if ("abort".equals( command) ) {
			processAbort( argument, lines, className );
		} else {
			return checkInvalidDirective( className, lines, line, command, argument );
		}
		if (changed) {
			return CHANGED;
		} else {
			return DIRECTIVE_FOUND;
		}
	}
	

	/**
	 * Aborts the preprocessing.
	 * 
	 * @param argument
	 * @param lines
	 * @param className
	 */
	private void processAbort(String argument, StringList lines, String className) {
		String message = this.environment.writeProperties( argument );
		throw new BuildException(  className + " line " + (lines.getCurrentIndex() + 1) + ": #abort: " + message );
	}

	private int checkInvalidDirective( String className, StringList lines, String line, String command, String argument )
	throws BuildException
	{
		if ( (this.ifDirectiveCount > 0) && (this.withinIfDirectives.get( command ) != null) ) {
			return NOT_CHANGED;
		} else if ( this.ignoreDirectives.get( command ) != null ) {
			return DIRECTIVE_FOUND;
		} else {
			throw new BuildException(
					className + " line " + (lines.getCurrentIndex() + 1) 
					+ ": unable to process command [" + command 
					+ "] with argument [" + argument 
					+ "] in line [" + line + "]." );
		}
		
	}


	/**
	 * Processes the #ifdef command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when any lines were actually changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfdef(String argument, StringList lines, String className )
	throws BuildException
	{
		boolean conditionFulfilled = this.environment.hasSymbol( argument );
		if (!conditionFulfilled && argument.indexOf(' ') != -1) {
			throw new BuildException(
					className + " line " + (lines.getCurrentIndex() + 1) 
					+ ": unable to process #ifdef-directive with several arguments [" + argument 
					+ "]  - you probably want to use the #if-directive instead." );
		}
		return processIfVariations( conditionFulfilled, lines, className );
	}

	/**
	 * Processes the #ifndef command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className name of the file which is processed
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfndef(String argument, StringList lines, String className ) 
	throws BuildException
	{
		boolean conditionFulfilled = !this.environment.hasSymbol( argument );
		if (conditionFulfilled && argument.indexOf(' ') != -1) {
			throw new BuildException(
					className + " line " + (lines.getCurrentIndex() + 1) 
					+ ": unable to process #ifndef-directive with several arguments [" + argument 
					+ "] - you probably want to use the #if-directive instead." );
		}
		return processIfVariations( conditionFulfilled, lines, className );
	}
	
	/**
	 * Processes the #ifdef, #ifndef, #if, #else, #elifdef, #elifndef and #elif directives.
	 * 
	 * @param conditionFulfilled true when the ifdef or ifndef clause is true
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when any lines were actually changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfVariations(boolean conditionFulfilled, StringList lines, String className )
	throws BuildException
	{
		this.ifDirectiveCount++;
		int currentIfDirectiveCount = this.ifDirectiveCount;
		boolean endifFound = false;
		boolean elseFound = false;
		boolean conditionHasBeenFullfilled = conditionFulfilled;
		int commandStartLine = lines.getCurrentIndex();
		boolean processed = false;
		while (lines.next()) {
			String line = lines.getCurrent();
			String trimmedLine = line.trim();
			int result = NOT_CHANGED; 
			if (conditionFulfilled) {
				result = processSingleLine( className, lines, line, trimmedLine );
				conditionHasBeenFullfilled = true;
			}
			if (result == CHANGED ) {
				processed = true;
			} else if ( result == DIRECTIVE_FOUND ) {
				// another directive was found and processed, but no changes were made
			} else if ( trimmedLine.startsWith("//#ifdef ") ) {
				// we are currently in a branch which is not true (conditionFulfilled == false)
				this.ifDirectiveCount++;
			} else if ( trimmedLine.startsWith("//#if ") ) {
				// we are currently in a branch which is not true (conditionFulfilled == false)
				this.ifDirectiveCount++;
			} else if ( trimmedLine.startsWith("//#ifndef ") ) {
				// we are currently in a branch which is not true (conditionFulfilled == false)
				this.ifDirectiveCount++;
			} else if (trimmedLine.startsWith("//#else") 
					&& (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (conditionFulfilled || conditionHasBeenFullfilled) {
					conditionFulfilled = false;					
				} else {
					conditionFulfilled = true;
				}
				elseFound = true;
			} else if ( trimmedLine.startsWith("//#elifdef") 
					&&  (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifdef after #else branch.");
				}
				if (conditionHasBeenFullfilled) {
					conditionFulfilled = false;
				} else {
					String symbol = trimmedLine.substring( 10 ).trim();
					conditionFulfilled = hasSymbol( symbol );
					if (!conditionFulfilled && symbol.indexOf(' ') != -1) {
						throw new BuildException(
								className + " line " + (lines.getCurrentIndex() + 1) 
								+ ": unable to process #elifdef-directive with several arguments [" + symbol 
								+ "] in line [" + lines.getCurrent() + "]." );
					}
				}
			} else if (trimmedLine.startsWith("//#elifndef")
					&& (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifndef after #else branch.");
				}
				if (conditionHasBeenFullfilled) {
					conditionFulfilled = false;
				} else {
					String symbol = trimmedLine.substring( 11 ).trim();
					conditionFulfilled = !hasSymbol( symbol );
					if (conditionFulfilled && symbol.indexOf(' ') != -1) {
						throw new BuildException(
								className + " line " + (lines.getCurrentIndex() + 1) 
								+ ": unable to process #elifndef-directive with several arguments [" + symbol 
								+ "] in line [" + lines.getCurrent() + "]." );
					}
				}
			} else if (trimmedLine.startsWith("//#elif") 
					&& (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elif after #else branch.");
				}
				/*
				System.out.println("line: [" + line + "]");
				System.out.println("condition has been fullfilled: " + conditionHasBeenFullfilled);
				System.out.println("condition is fullfilled: " + conditionFulfilled );
				*/
				if (conditionHasBeenFullfilled) {
					conditionFulfilled = false;
				} else {
					String argument = trimmedLine.substring( 8 ).trim();
					conditionFulfilled = checkIfCondition( argument, className, lines );
				}
			} else if (trimmedLine.startsWith("//#endif")) {
				if (this.ifDirectiveCount == currentIfDirectiveCount ) {
					endifFound = true;
					break;
				} else {
					this.ifDirectiveCount--;
				}
			} else {
				// this line has to be either commented out or to be uncommented:
				boolean changed = false;
				if (conditionFulfilled) {
					changed = uncommentLine( line, lines );
				} else {
					changed = commentLine( line, trimmedLine, lines );
				}
				if (changed) {
					processed = true;
				}
			}
		} // loop until endif is found
		if (!endifFound) {
			throw new BuildException(className + " line " + (commandStartLine +1) 
					+": #ifdef is not terminated with #endif!" );
		}
		this.ifDirectiveCount--;
		return processed;
	}

	/**
	 * Checks whether an if-condition is true.
	 * 
	 * @param argument the if-expression e.g. "(symbol1 || symbol2) && symbol3"
	 * @param className the name of the file
	 * @param lines the list of source code
	 * @return true when the argument results in true
	 * @throws BuildException when there is an syntax error in the expression
	 */
	protected boolean checkIfCondition(String argument, String className, StringList lines ) 
	throws BuildException 
	{
		return this.environment.getBooleanEvaluator().evaluate( argument, className, lines.getCurrentIndex() + 1);
	}

	/**
	 * Comments a line, so it will not be compiled.
	 *  
	 * @param line the line
	 * @param trimmedLine the line without any spaces or tabs at the start
	 * @param lines the list were changes are written to
	 * @return true when the line were changed
	 */
	private boolean commentLine(String line, String trimmedLine, StringList lines ) {
		if (trimmedLine.startsWith("//#")) {
			return false;
		}
		String newLine;
		if (this.indent) {
			char[] lineChars = line.toCharArray();
			int insertPos = 0;
			for (int i = 0; i < lineChars.length; i++) {
				char c = lineChars[i];
				if ( c != ' ' && c != '\t') {
					insertPos = i;
					break;
				}
			}
			newLine = line.substring(0, insertPos ) + "//# " + line.substring( insertPos );
		} else {
			newLine =  "//# " + line;
		}
		lines.setCurrent( newLine );
		return true;
	}

	/**
	 * Removes the commenting of a line, so it will be compiled.
	 *  
	 * @param line the line
	 * @param lines the list of lines in which changes are saved
	 * @return true when the line was actually changed
	 */
	private boolean uncommentLine(String line, StringList lines ) {
		int commentPos = line.indexOf("//# ");
		if (commentPos == -1) {
			commentPos = line.indexOf("//#\t");
			if (commentPos == -1) {
				return false;
			}
		}
		String newLine = line.substring( 0, commentPos ) + line.substring( commentPos + 4);
		lines.setCurrent( newLine );
		return true;
	}

	/**
	 * Processes the #ifdef command.
	 * 
	 * @param argument the symbols which need to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIf(String argument, StringList lines, String className ) 
	throws BuildException
	{
		boolean conditionFulfilled = checkIfCondition( argument, className, lines );
		return processIfVariations( conditionFulfilled, lines, className );
	}

	/**
	 * Processes the #define command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @throws BuildException when the preprocessing fails
	 */
	private void processDefine(String argument, StringList lines, String className ) 
	throws BuildException
	{
		if (argument.equals("false")) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
					+ ": found invalid #define directive: the symbol [false] cannot be defined.");
		}
		int equalsIndex = argument.indexOf('=');
		if (equalsIndex != -1) {
			String name = argument.substring(0, equalsIndex).trim();
			String value = argument.substring( equalsIndex + 1).trim();
			this.environment.addTemporaryVariable(name, value);
		} else {
			this.environment.addTemporarySymbol( argument );
		}
	}

	/**
	 * Processes the #undefine command.
	 * 
	 * @param argument the symbol which should be undefined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @throws BuildException when the preprocessing fails
	 */
	private void processUndefine(String argument, StringList lines, String className ) 
	throws BuildException
	{
		if (argument.equals("true")) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
					+ ": found invalid #undefine directive: the symbol [true] cannot be defined.");
		}
		boolean success = this.environment.removeTemporarySymbol( argument );
		this.environment.removeSymbol( argument );
		if (!success) {
			// this is a variable
			this.environment.removeTemporaryVariable( argument );
			this.environment.removeVariable( argument );
		}
	}

	/**
	 * Processes the #= command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when the content has been changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processVariable(String argument, StringList lines, String className ) 
	throws BuildException
	{
		try {
			String line = this.environment.writeProperties( argument, true );
			lines.setCurrent( line );
			return true;
		} catch (IllegalArgumentException e) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to preprocess //#= in line [" + argument + "]: " + e.getMessage()  );
		}
	}

	/**
	 * Processes the #include command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made (always)
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processInclude(String argument, StringList lines, String className) 
	throws BuildException
	{
		String file = argument;
		try {
			file = this.environment.writeProperties( argument, true );
			String[] includes = FileUtil.readTextFile( file );
			lines.insert( includes );
			return true;
		} catch (IllegalArgumentException e) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getMessage()  );
		} catch (IOException e) {
			if (!argument.equals(file)) {
				argument += "] / [" + file;
			}
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Processes the #style command.
	 * 
	 * @param styleNames the name of the style(s)
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processStyle(String styleNames, StringList lines, String className) 
	throws BuildException
	{
		if (!this.usePolishGui) {
			return false;
		}
		int styleDirectiveLine = lines.getCurrentIndex() + 1;
		if (this.styleSheet == null) {
			throw new BuildException(
					className + " line " + styleDirectiveLine
					+ ": unable to process #style directive: no style-sheet found. Please create [resources/polish.css].");
		}
		// get the style-name:
		String[] styles = StringUtil.splitAndTrim(styleNames, ',');
		String style = null;
		for (int i = 0; i < styles.length; i++) {
			String name = styles[i].toLowerCase();
			if (name.charAt(0) == '.') {
				name = name.substring( 1 );
			}
			if (this.styleSheet.isDefined(name)) {
				style = name;
				break;
			}
		}
		if (style == null) {
			String message;
			if (styles.length == 1) {
				message = "the style [" + styleNames + "] is not defined. Please define the style in the appropriate polish.css file.";
			} else {
				message = "none of the styles [" + styleNames + "] is defined. Please define at least one of the styles in the appropriate polish.css file.";
			}
			throw new BuildException(
					className + " line " + styleDirectiveLine
					+ ": unable to process #style directive: " + message );
		}
		// when the #style directive is followed by a new operator, then
		// the defined style will be included as last argument in the new operator,
		// otherwise the defined style will be set as the current style in the stylesheet:
		String nextLine = null;
		if (lines.next()) {
			nextLine = lines.getCurrent();
			String trimmed = nextLine.trim(); 
			if ( trimmed.startsWith("//#=") ) {
				processSingleLine(className, lines, nextLine, trimmed);
				nextLine = lines.getCurrent();
			}
		}
		// get the statement which follows the #style-directive and
		// which is closed by a semicolon:
		if ( nextLine != null ) {
			uncommentLine( nextLine, lines );
			while ( nextLine.indexOf(';') == -1) {
				if (!lines.next()) {
					throw new BuildException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
							);
				}
				if ( containsDirective( nextLine) ) {
					throw new BuildException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
					);
				}
				nextLine = lines.getCurrent();				
				uncommentLine( nextLine, lines );
			}
			// get uncommented line:
			nextLine = lines.getCurrent();
			int parenthesisPos = nextLine.lastIndexOf(')');
			if ( parenthesisPos == -1 ) {
				throw new BuildException(
						className + " line " + styleDirectiveLine
						+ ": unable to process #style directive: the statement which follows the #style directive must be closed by a parenthesis and a semicolon on the same line: [);]. "  
				);
			}
			// append the style-parameter as the last argument:
			StringBuffer buffer = new StringBuffer();
			buffer.append( nextLine.substring(0, parenthesisPos ) );
			int openingParenthesisPos = nextLine.lastIndexOf('(');
			if (openingParenthesisPos != parenthesisPos -1 ) {
				buffer.append(", ");
			} 
			buffer.append( "StyleSheet." )
					.append( style )
					.append( "Style " )
					.append( nextLine.substring( parenthesisPos ) );
			lines.setCurrent( buffer.toString() );
		} else { // either there is no next line or the next line has no new operator
			lines.prev();
			lines.insert( "\tStyleSheet.currentStyle = StyleSheet." + style + "Style;"  );
		}
		// mark the style as beeing used:
		this.styleSheet.addUsedStyle( style );
		return true;
	}

	/**
	 * Processes the #message command.
	 * 
	 * @param argument the message which should be printed out
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processMessage(String argument, StringList lines, String className) 
	throws BuildException
	{
		argument = this.environment.writeProperties( argument);
		System.out.println("MESSAGE: " + argument );
		return false;
	}
	
	/**
	 * Processes the #todo command.
	 * 
	 * @param argument the message which should be printed out
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processTodo(String argument, StringList lines, String className) {
		argument = this.environment.writeProperties( argument );
		System.out.println("TODO: " + getErrorStart(className, lines) +  argument );
		return false;
	}
	
	/**
	 * Processes the #foreach command.
	 * Example:
	 * <pre>
	 * //#foreach format in polish.SoundFormat
	 *    //#= System.out.println( "Device supports the sound format: ${format}" );
	 * //#next format
	 * </pre>
	 * 
	 * @param argument the name of the loop-element and the variable which can have several values separated by commas.
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processForeach(String argument, StringList lines, String className) {
		final int inPos = argument.indexOf(" in ");
		if (inPos == -1) {
			throw new BuildException( getErrorStart(className, lines) + " invalid #foreach directive: keyword \" in \" not found in [" + argument + "].");
		}
		final String loopVarName = argument.substring(0, inPos).trim();
		final String varName = argument.substring( inPos + 4 ).trim();
		
		final String valueStr = this.environment.getVariable( varName );
		final String endToken = "//#next " + loopVarName;
		boolean changed = false;
		if (valueStr == null) {
			// okay, the variable is not defined at all,
			// so skip all lines until the relevant #next:
			while (lines.next()) {
				String line = lines.getCurrent();
				String trimmedLine = line.trim();
				if (endToken.equals(trimmedLine)) {
					break;
				}
				changed = commentLine(line, trimmedLine, lines);
			}
			return changed;
		}
		
		// the variable is defined, so the loop can be processed normally:
		final ArrayList innerLinesList = new ArrayList();
		while (lines.next()) {
			String line = lines.getCurrent();
			String trimmedLine = line.trim();
			if (endToken.equals(trimmedLine)) {
				break;
			}
			innerLinesList.add( line );
			commentLine(line, trimmedLine, lines);
		}
		int startIndex = lines.getCurrentIndex();
		int insertionIndex = startIndex;
		final String[] innerLines = (String[]) innerLinesList.toArray( new String[ innerLinesList.size() ] );
		final String[] values = StringUtil.splitAndTrim( valueStr, ',' );
		for (int i = 0; i < values.length; i++) {
			final String value = values[i];
			this.environment.addTemporaryVariable( loopVarName, value );
			final String[] copy = new String[ innerLines.length ];
			System.arraycopy( innerLines, 0, copy, 0, innerLines.length);
			for (int j = 0; j < copy.length; j++) {
				copy[j] = this.environment.writeProperties( copy[j] ); //PropertyUtil.writeProperties(copy[j], this.temporaryVariables );
			}
			lines.insert( copy );
			insertionIndex += copy.length;
			lines.setCurrentIndex( insertionIndex );
		}
		lines.setCurrentIndex( startIndex );
		return true;
	}	
	/**
	 * Processes the #debug command.
	 * 
	 * @param debugLevel the debug-level if defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processDebug(String debugLevel, StringList lines, String className) 
	throws BuildException
	{
		lines.next();
		String line = lines.getCurrent();
		if (!this.enableDebug) {
			return (commentLine( line, line.trim(), lines ));
		}
		if (debugLevel == null || "".equals(debugLevel)) {
			debugLevel = "debug";
		}
		if (this.debugManager.isDebugEnabled( className, debugLevel )) {
			/*
			boolean verboseDebug = this.debugManager.isVerbose();
			if (verboseDebug) {
				insertVerboseDebugInfo( lines, className );
			}
			return (verboseDebug | uncommentLine( line, lines ) | convertSystemOut( lines, debugLevel, className ));
			*/
			return (uncommentLine( line, lines ) | convertSystemOut( lines, debugLevel, className ));
		} else {
			return commentLine( line, line.trim(), lines );
		}
	}

	/**
	 * Processes the #mdebug command.
	 * 
	 * @param debugLevel the debug-level if defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processMdebug(String debugLevel, StringList lines, String className) 
	throws BuildException
	{
		boolean hasNext = lines.next();
		boolean debug = false;
		boolean changed = false;
		if (this.enableDebug) {
			if (debugLevel == null || "".equals(debugLevel)) {
				debugLevel = "debug";
			}
			debug = this.debugManager.isDebugEnabled( className, debugLevel );
		}
		int startLine = lines.getCurrentIndex();
		boolean endTagFound = false;
		/*
		boolean verboseDebug = (debug && this.debugManager.isVerbose());
		if (verboseDebug) {
			insertVerboseDebugInfo( lines, className );
		}
		*/
		while ( hasNext ) {
			String line = lines.getCurrent();
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("//#enddebug")) {
				endTagFound = true;
				break;
			}
			if (debug) {
				changed = changed | uncommentLine( line, lines ) | convertSystemOut( lines, debugLevel, className );
				
			} else {
				changed = changed | commentLine( line, trimmedLine, lines );
			}
			hasNext = lines.next();
		}
		if (! endTagFound ) {
			throw new BuildException(
					className + " line " + startLine
					+ ": missing #enddebug directive for multi-line debug directive #mdebug."
			);
		}
		//return (verboseDebug || changed);
		return (changed);
	}
	
	/**
	 * Converts a System.out.println etc to a Debug,debug(..)-call.
	 * Also channels an exception.printStackTrace() etc to a Debug.debug(...)-call. 
	 * 
	 * @param lines the current lines
	 * @return true when the current line was changed
	 */
	private boolean convertSystemOut(StringList lines, String debugLevel, String className ) {
		String debugCall;
		if (this.useDefaultPackage) {
			debugCall = "Debug.debug(";
		} else {
			debugCall = "de.enough.polish.util.Debug.debug(";
		}
		debugCall += "\"" + debugLevel + "\", \"" + className + "\", " + (lines.getCurrentIndex() + 1) + ", ";
		String line = lines.getCurrent();
		Matcher matcher = SYSTEM_PRINT_PATTERN.matcher( line );
		if (matcher.find()) {
			// the current line contains a system.out.println()
			String argument = line.substring( matcher.end() ).trim();
			int plusPos = argument.lastIndexOf('+');
			if ( plusPos != -1 && plusPos != argument.length() -1 ) {
				String firstArgument = argument.substring(0, plusPos ).trim();
				String secondArgument = argument.substring( plusPos + 1 ).trim();
				if ( (secondArgument.indexOf('"') != -1 && secondArgument.charAt(0) != '"')
						|| (secondArgument.indexOf(']') != -1 )) 
				{
					// the '+' was in the middle of a string, e.g. " bla + blubb "
					// or it was in something like "somearray[ index + 2 ]"
					line = debugCall + argument; 					
				} else {
					// okay, we can split up the argument:
					line = debugCall  
						+ firstArgument + ", " + secondArgument;
				}
			} else {
				line = debugCall + argument; 
			}
			lines.setCurrent( line );
			return true;
		}
		// now check if the line prints out a stacktrace:
		int stackTraceStart = line.indexOf(".printStackTrace()"); 
		if ( stackTraceStart != -1) {
			String exceptionVar = line.substring(0, stackTraceStart).trim();
			lines.setCurrent( debugCall + exceptionVar + ");");
			return true;
		}
		// the current line contained neither a system.out.println nor a e.printStackTrace():
		return false;
	}

	/**
	 * Inserts verbose debugging information (time, class-name and source-code line) instead of the //#debug-preprocessing statement.
	 * 
	 * @param lines the source code
	 * @param className the name of the class
	private void insertVerboseDebugInfo( StringList lines, String className ) {
		String debugVerbose;
		if (this.useDefaultPackage) {
			debugVerbose = "Debug.debug(System.currentTimeMillis() + "; 
		} else {
			debugVerbose = "de.enough.polish.util.Debug.debug(System.currentTimeMillis() + "; 
		}
		debugVerbose +=  "\" - " + className 
			+ " line " + (lines.getCurrentIndex() + 1 - lines.getNumberOfInsertedLines()) 
			+ "\" );";
		lines.prev();
		lines.setCurrent( debugVerbose );
		lines.next();
		//lines.insert( debugVerbose );
		//lines.next();
	}
	 */
	
	/**
	 * Checks if the given line contains a directive.
	 *  
	 * @param line the line which should be tested 
	 * @return true when the given line includes a preprocessing directive.
	 */
	public static final boolean containsDirective(String line) {
		Matcher matcher = DIRECTIVE_PATTERN.matcher( line );
		return matcher.find();
	}

	/**
	 * Sets the style sheet.
	 * 
	 * @param device the current device
	 * @param styleSheet the new style sheet
	 */
	public void setSyleSheet(StyleSheet styleSheet, Device device) {
		this.styleSheet = styleSheet;
		if (styleSheet == null) {
			this.environment.removeSymbol("polish.useDynamicStyles");
			this.environment.removeSymbol("polish.useBeforeStyle");
			this.environment.removeSymbol("polish.useAfterStyle");			
		} else {
			// the style sheet is not null:
			if (styleSheet.containsDynamicStyles()) {
				addSymbol("polish.useDynamicStyles");
			} else {
				this.environment.removeSymbol("polish.useDynamicStyles");
			}
			if (styleSheet.containsBeforeStyle()) {
				this.environment.addSymbol("polish.useBeforeStyle");
			} else {
				this.environment.removeSymbol("polish.useBeforeStyle");
			}
			if (styleSheet.containsAfterStyle()) {
				this.environment.addSymbol("polish.useAfterStyle");
			} else {
				this.environment.removeSymbol("polish.useAfterStyle");
			}
			// now set the CSS-symbols:
			this.environment.addSymbols( styleSheet.getCssPreprocessingSymbols( device ) );
		}
	}
	
	/**
	 * Retrieves the style sheet.
	 * 
	 * @return the style sheet.
	 */
	public StyleSheet getStyleSheet() {
		return this.styleSheet;
	}

	/**
	 * Determines whether the given symbol is defined.
	 * 
	 * @param symbol the symbol 
	 * @return true when the symbol is defined
	 * @deprecated use environment.hasSymbol() instead
	 */
	public boolean hasSymbol(String symbol) {
		return this.environment.hasSymbol( symbol);
	}

	/**
	 * Retrieves the value of a variable.
	 * 
	 * @param name the name of the variable
	 * @return the value of the variable
	 * @deprecated use environment.getVariable() instead
	 */
	public String getVariable(String name) {
		return this.environment.getVariable(name);
	}

	/**
	 * Determines whether the given file is in the preprocess queue.
	 * 
	 * @param fileName the name of the file, e.g. "de/enough/polish/ui/Screen.java"
	 * @return true when the given file is in the queue
	 */
	public boolean isInPreprocessQueue(String fileName) {
		return (this.preprocessQueue.get( fileName ) != null);
	}
	
	/**
	 * Adds the file to the preprocessing queue
	 * 
	 * @param fileName the name of the file, e.g. de/enough/polish/ui/Screen.java
	 */
	public void addToPreprocessQueue( String fileName ) {
		String fileNameWindows = fileName.replace( '/', '\\');
		this.preprocessQueue.put( fileNameWindows, Boolean.TRUE );
		String fileNameUnix = fileName.replace( '\\', '/');
		this.preprocessQueue.put( fileNameUnix, Boolean.TRUE );
	}
	
	/**
	 * Removes the file from the preprocessing queue
	 * 
	 * @param fileName the name of the file
	 */
	public void removeFromPreprocessQueue( String fileName ) {
		String fileNameWindows = fileName.replace( '/', '\\');
		this.preprocessQueue.remove( fileNameWindows );
		String fileNameUnix = fileName.replace( '\\', '/');
		this.preprocessQueue.remove( fileNameUnix );
	}

	/**
	 * Retrieves all defined variables.
	 * Changes take effect on the preprocessor.
	 * 
	 * @return all defined variables
	 * @deprecated use environment.getVariable() instead
	 */
	public Map getVariables() {
		return this.environment.getVariables();
	}
	
	/**
	 * Creates the start of an error message:
	 * 
	 * @param className the name of the current class
	 * @param lines the source code of that class
	 * @return a typical error-message start like "MyClass.java line [12]: " 
	 */
	protected String getErrorStart(String className, StringList lines) {
		return className + " line [" + (lines.getCurrentIndex() + 1) + "]: ";
	}

	/**
	 * Sets if all classes should be moved into the default package.
	 * 
	 * @param useDefaultPackage true when all classes should be moved
	 *        into the default package
	 */
	public void setUseDefaultPackage(boolean useDefaultPackage ) {
		this.useDefaultPackage = useDefaultPackage;
	}

	/**
	 * Determines if all classes should be moved into the default package.
	 * 
	 * @return true when all classes should be moved
	 *        into the default package
	 */
	public boolean useDefaultPackage() {
		return this.useDefaultPackage;
	}
	
	public void setTextFileManager( TextFileManager textFileManager ) {
		this.textFileManager = textFileManager;
	}
	
	public TextFileManager getTextFileManager() {
		return this.textFileManager;
	}

	/**
	 * @param manager
	 */
	public void setCssAttributesManager(CssAttributesManager manager) {
		this.cssAttributesManager = manager;
	}
	
	public CssAttributesManager getCssAttributesManager() {
		return this.cssAttributesManager;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

}
