/*
 * Created on 04-Sep-2004 at 18:49:18.
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
package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.Variable;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.preprocess.BooleanEvaluator;
import de.enough.polish.stacktrace.BinaryStackTrace;
import de.enough.polish.stacktrace.DecompilerNotInstalledException;
import de.enough.polish.stacktrace.StackTraceUtil;
import de.enough.polish.util.PropertyUtil;

/**
 * <p>Excutes an emulator.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        04-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Emulator extends Thread {

	private Device emulatedDevice;
	private EmulatorSetting emulatorSetting;
	private boolean isFinished = false;
	private File[] sourceDirs;
	private String preprocessedSourcePath;
	private String classPath;
	private Map environmentProperties;
	
	/**
	 * Creates a new emulator instance.
	 * The actual initialisation is done in the init-method.
	 * 
	 * @see #init(Device, EmulatorSetting, HashMap, Project, BooleanEvaluator, String)
	 */
	public Emulator() {
		// no initialisation done here
	}
	
	/**
	 * Starts the emulator for the given device.
	 *  
	 * @param setting the setting
	 * @param device the current device
	 * @param properties all Ant- and polish-properties for the parameter-values
	 * @param project the ant-project to which this emulator belongs to 
	 * @param evaluator a boolean evaluator for the parameter-conditions
	 * @param wtkHome the home directory of the wireless toolkit
	 * @return true when an emulator could be detected
	 */
	public abstract boolean init(Device device, EmulatorSetting setting, HashMap properties, Project project, BooleanEvaluator evaluator, String wtkHome );
	
	/**
	 * Starts the actual emulator-process.
	 * 
	 * @return the process itself, so that J2ME Polish can show the output of the process
	 * @throws IOException when the process could not be started
	 */
	public abstract Process startEmulator() throws IOException;
	
	/**
	 * Sets the minimum settings.
	 * 
	 * @param device the device which is emulated
	 * @param setting the settings for the emulator
	 * @param sourceDirs the directories containing the original source files.
	 * @param environmentProperties the J2ME Polish and Ant-properties
	 */
	private void setBasicSettings( Device device, EmulatorSetting setting, File[] sourceDirs, Map environmentProperties ) {
		this.emulatedDevice = device;
		this.emulatorSetting = setting;
		this.environmentProperties = environmentProperties;
		this.classPath = device.getClassesDir();
		this.preprocessedSourcePath = device.getSourceDir();
		this.sourceDirs = sourceDirs;
	}
	

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Process process = startEmulator();
			if (this.emulatorSetting.doWait()) {
				String info = this.emulatedDevice.getIdentifier() + ": ";
				LoggerThread errorLog = new LoggerThread( process.getErrorStream(), System.err, info );
				errorLog.start();
				LoggerThread outputLog = new LoggerThread( process.getInputStream(), System.out, info );
				outputLog.start();
				int result = process.waitFor();
				System.out.println("Emulator finished with result code [" + result + "]." );
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to start or run emulator: " + e.toString() );
		} catch (InterruptedException e) {
			// ignore...
		} finally {
			this.isFinished = true;
		}
	}
	
	/**
	 * Determines whether this emulator is finished.
	 * 
	 * @return true when this emulator is finished or when J2ME Polish should not wait for it.
	 */
	public boolean isFinished() {
		return this.isFinished;
	}
	
	/**
	 * Writes all properties for the given parameters.
	 * 
	 * @param variables the input parameters
	 * @param properties the map containing all properties
	 * @return an array of parameters with the written properties
	 */
	protected Variable[] writeProperties( Variable[] variables, Map properties ) {
		Variable[] newVars = new Variable[ variables.length ];
		for (int i = 0; i < variables.length; i++) {
			Variable variable = variables[i];
			
			String value = variable.getValue();
			if (value.indexOf('$') == -1) {
				// now property needed:
				newVars[i] = variable;
			} else {
				// the value contains a property:
				value = PropertyUtil.writeProperties( value, properties);
				Variable var = new Variable( variable.getName(), value );
				var.setIf( variable.getIfCondition() );
				var.setUnless( variable.getUnlessCondition() );
				var.setType( variable.getType() );
				newVars[i] = var;
				if (value.indexOf("${") != -1) {
					int startIndex = value.indexOf("${") + 2;
					int endIndex = value.indexOf('}', startIndex );
					String missingProperty = value.substring( startIndex, endIndex );
					System.out.println("Warning: the Ant-property [" + missingProperty + "] needs to be defined, so that the parameter [" + variable.getName() + "] can be set correctly. Current value is [" + value + "].");					
				}
			}
		}
		return newVars;
	}
	
	/**
	 * Gets the parameters for this emulator.
	 * 
	 * @param setting the emulator setting
	 * @param project the Ant project
	 * @param evalutor the boolean evaluator
	 * @param properties all Ant- and J2ME Polish-properties.
	 * @return an array of initialised parameters.
	 */
	protected Variable[] getParameters( EmulatorSetting setting, Project project, BooleanEvaluator evalutor, Map properties ) {
		Variable[] parameters = setting.getParameters();
		if (parameters.length == 0) {
			return parameters;
		}
		ArrayList parametersList = new ArrayList( parameters.length );
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (variable.isConditionFulfilled(evalutor, project)) {
				parametersList.add( variable );
			}
		}
		Variable[] params = (Variable[]) parametersList.toArray( new Variable[ parametersList.size() ] );
		return writeProperties( params, properties );
	}
	
	/**
	 * Gets a specific parameter
	 * 
	 * @param name the name of the parameter
	 * @param parameters all parameters
	 * @return the found parameter or null, when none was found
	 */
	protected Variable getParameter( String name, Variable[] parameters ) {
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (name.equals( variable.getName())) {
				return variable;
			}
		}
		return null;
	}

	/**
	 * Gets a specific parameter-value
	 * 
	 * @param name the name of the parameter
	 * @param parameters all parameters
	 * @return the found parameter-value or null, when none was found
	 */
	protected String getParameterValue( String name, Variable[] parameters ) {
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (name.equals( variable.getName())) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Starts the emulator for the given device.
	 *  
	 * @param setting the setting
	 * @param device the current device
	 * @param variables the variables for the parameter-values
	 * @param project the ant-project to which this emulator belongs to 
	 * @param evaluator a boolean evaluator for the parameter-conditions
	 * @param wtkHome the home directory of the wireless toolkit
	 * @param sourceDirs the directories containing the original source files.
	 * @return true when an emulator could be detected
	 */
	public static Emulator createEmulator(Device device, EmulatorSetting setting, Map variables, Project project, BooleanEvaluator evaluator, String wtkHome, File[] sourceDirs ) {
		String className = setting.getEmulatorClassName();
		if (className == null) {
			className = device.getCapability("polish.Emulator.Class");
			if (className == null) {
				Object executable = variables.get("polish.Emulator.Executable");
				Object arguments =  variables.get("polish.Emulator.Arguments");
				if (executable != null && arguments != null) {
					className = GenericEmulator.class.getName();
				} else {
					className = WtkEmulator.class.getName();
				}
			}
		}
		Class emulatorClass = null;
		try {
			emulatorClass = Class.forName( className );
		} catch (ClassNotFoundException e) {
			// okay, try to add this package in front if it:
			// System.out.println("did not find class " + className );
			try {
				emulatorClass = Class.forName( "de.enough.polish.emulator." + className );
			} catch (ClassNotFoundException e2) {
				System.err.println("Unable to load emulator-class [" + className + "]: " + e2.toString() );
				return null;
			}
		}
		Emulator emulator = null;
		try {
			emulator = (Emulator) emulatorClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to instantiate the emulator-class [" + className + "]: " + e.toString() );
			return null;
		}
		Hashtable antProperties = project.getProperties();
		HashMap capabilities = device.getCapabilities();
		HashMap properties = new HashMap( antProperties.size() + variables.size() + capabilities.size() );
		properties.putAll( antProperties );
		properties.putAll( capabilities );
		properties.putAll( variables );
		boolean okToStart = emulator.init(device, setting, properties, project, evaluator, wtkHome);
		if (!okToStart) {
			return null;
		}
		emulator.setBasicSettings(device, setting, sourceDirs, properties );
		emulator.start();
		return emulator;
	}
	
	class LoggerThread extends Thread {
		private final InputStream input;
		private final PrintStream output;
		private final String header;

		public LoggerThread( InputStream input, PrintStream output, String header ) {
			this.input = input;
			this.output = output;
			this.header = header;
		}
		
		public void run() {
			StringBuffer log = new StringBuffer( 300 );
			log.append(this.header);
			int startPos = this.header.length();
			int c;
			
			try {
				boolean decompilerInstalled = true;
				while ((c = this.input.read() ) != -1) {
					if (c == '\n') {
						String logMessage = log.toString();
						this.output.println( logMessage );
						log.delete( startPos,  log.length() );
						if (decompilerInstalled
								&& (logMessage.indexOf('+') != -1) 
								&& (logMessage.indexOf("at ") != -1)) {
							try {
								// this seems to be an error message like
								// "   at de.enough.polish.ClassName(+263)"
								// so try to use JAD for finding out the source code address:
								BinaryStackTrace stackTrace = StackTraceUtil.translateStackTrace(logMessage, Emulator.this.classPath, Emulator.this.preprocessedSourcePath, Emulator.this.sourceDirs, Emulator.this.environmentProperties);
								if (stackTrace != null) {
									boolean showDecompiledStackTrace = true;
									if (stackTrace.couldBeResolved()) {
										this.output.println( this.header + stackTrace.getSourceCodeMessage() );
										showDecompiledStackTrace = false;
									} 
									if (showDecompiledStackTrace || Emulator.this.emulatorSetting.showDecompiledStackTrace()){
										this.output.println( this.header + "Decompiled stack-trace: " + stackTrace.getDecompiledCodeSnippet() );
									}
								}
							} catch (DecompilerNotInstalledException e) {
								this.output.println("Unable to translate stacktrace: " + e.getMessage() );
								decompilerInstalled = false;
							}
						}
					}  else if (c != '\r') {
						log.append((char) c);
					}
				}
				this.input.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to log: " + e.toString() );
			}
		}
	}


}
