/*
 * Created on 19-Jun-2004 at 20:23:00.
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
package de.enough.polish.preprocess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.preprocess.custom.WrapperPreprocessor;
import de.enough.polish.util.PopulateUtil;
import de.enough.polish.util.StringList;

/**
 * <p>Processes single lines of source-code.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class CustomPreprocessor {

	protected Preprocessor preprocessor;
	protected BooleanEvaluator booleanEvaluator;
	protected boolean isUsingPolishGui;
	protected boolean isInJ2MEPolishPackage;
	protected Device currentDevice;
	protected StyleSheet currentStyleSheet;
	private ArrayList directives;

	/**
	 * Creates a new line-processor.
	 * The actual initialisation work is done in the init()-method.
	 * 
	 * @see #init(Preprocessor)
	 */
	public CustomPreprocessor() {
		// no initialisation work done
	}
	
	public void init( Preprocessor processor ) {
		this.preprocessor = processor;
		this.booleanEvaluator = processor.getBooleanEvaluator();
	}
	
	/**
	 * Notifies the processor that from now on source code from the J2ME Polish package is processed.
	 * This will last until the notifyDevice(...)-method is called.
	 */
	public void notifyPolishPackageStart() {
		this.isInJ2MEPolishPackage = true;
	}
		
	/**
	 * Notifies this processor about a new device for which code is preprocessed.
	 * The default implementation set the currentDevice, currentStyleSheet
	 * and isUsingPolishGui and resets the isInJ2MEPolishPackage instance variables.
	 *  
	 * @param device the new device
	 * @param usesPolishGui true when the J2ME Polish GUI is used for the new device
	 */
	public void notifyDevice( Device device, boolean usesPolishGui ) {
		this.currentDevice = device;
		this.isUsingPolishGui = usesPolishGui;
		this.currentStyleSheet = this.preprocessor.getStyleSheet();
		this.isInJ2MEPolishPackage = false;
	}
	
	/**
	 * Processes the given class.
	 * The default implementation searches for the registered directives
	 * and calls the appropriate methods upon findings.
	 * 
	 * @param lines the source code of the class
	 * @param className the name of the class
	 * @see #registerDirective(String)
	 */
	public void processClass( StringList lines, String className ) {
		if (this.directives == null) {
			return;
		}
		Directive[] myDirectives = (Directive[]) this.directives.toArray( new Directive[ this.directives.size()]);
		while (lines.next()) {
			String line = lines.getCurrent();
			for (int i = 0; i < myDirectives.length; i++) {
				Directive directive = myDirectives[i];
				if (line.indexOf( directive.directive ) != -1) {
					try {
						// a registered preprocessing directive has been found:
						// call the appropriate method:
						directive.method.invoke(this, new Object[]{ line, lines, className });
						// now check if the directive has been removed:
						if (line == lines.getCurrent()) {
							// the line has not been changed
							lines.setCurrent("// removed custom directive " + directive );
						}
						// break the for-loop:
						break;
					} catch (BuildException e) {
						throw e;
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof BuildException) {
							throw (BuildException) e.getCause();
						} else {
							e.printStackTrace();
							throw new BuildException("Unable to process directive [" 
									+ directive.directive + "] in line [" + line 
									+ "] of class [" + className + "] at line [" 
									+ (lines.getCurrentIndex() + 1) + "]: " + e.toString() );
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new BuildException("Unable to process directive [" 
								+ directive.directive + "] in line [" + line 
								+ "] of class [" + className + "] at line [" 
								+ (lines.getCurrentIndex() + 1) + "]: " + e.toString() );
					}
				}
			}
		}
	}
	
	/**
	 * Loads a line processor subclass.
	 * 
	 * @param setting the definition of the line processor 
	 * @param preprocessor the preprocessor
	 * @param project the Ant project
	 * @return the initialised line processor
	 * @throws BuildException when the defined class could not be instantiated
	 */
	public static CustomPreprocessor getInstance( PreprocessorSetting setting, 
			Preprocessor preprocessor,
			Project project ) 
	throws BuildException
	{
		try {
			CustomPreprocessor lineProcessor = null;
			if (setting.getClassPath() != null) {
				lineProcessor = new WrapperPreprocessor( setting, preprocessor, project );
			} else {
				lineProcessor = (CustomPreprocessor) Class.forName( setting.getClassName() ).newInstance();
				if (setting.hasParameters()) {
					PopulateUtil.populate( lineProcessor, setting.getParameters(), project.getBaseDir() );
				}
			}
			lineProcessor.init( preprocessor );
			return lineProcessor;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to load preprocessor [" + setting.getClassName() + "]: " + e.toString() );
		}
	}
	
	/**
	 * Adds a directive which is searched for in the preprocessed source codes.
	 * Whenever the directive is found, the appropriate method process[directive-name] is
	 * called.
	 * When for example the preprocessing directive "//#hello" should be processed,
	 * the subclass needs to implement the method 
	 * processHello( String line, StringList lines, String className ).
	 * <pre>
	 * registerDirective("hello");
	 * // is the same like
	 * registerDirective("//#hello");
	 * </pre> 
	 *  
	 * @param directive the preprocessing directive which should be found.
	 *        The directive needs to contain at least 2 characters (apart from
	 * 		  the beginning "//#"). The "//#" beginning is added when not specified.
	 * @throws BuildException when the corresponding method could not be found.
	 */
	protected void registerDirective( String directive ) throws BuildException {
		String methodName = directive;
		if (directive.startsWith("//#")) {
			methodName = directive.substring(3);
		} else {
			directive = "//#" + directive;
		}
		methodName = "process" + Character.toUpperCase( methodName.charAt(0)) + methodName.substring( 1 );
		try {
			Method method = getClass().getMethod( methodName, new Class[]{ String.class, StringList.class, String.class } );
			if (this.directives == null) {
				this.directives = new ArrayList();
			}
			this.directives.add( new Directive( directive, method ));
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new BuildException("Unable to register directive [" + directive + "]: method [" + methodName + "] could not be accessed: " + e.toString(), e );
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new BuildException("Unable to register directive [" + directive + "]: method [" + methodName + "] could not be found: " + e.toString(), e );
		}
	}
	
	class Directive {
		String directive;
		Method method;
		public Directive( String directive, Method method ) {
			this.directive = directive;
			this.method = method;
		}
	}

}
