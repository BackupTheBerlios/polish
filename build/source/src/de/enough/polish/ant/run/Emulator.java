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
package de.enough.polish.ant.run;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.preprocess.BooleanEvaluator;
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
public class Emulator extends Thread {

	private final Device device;
	private final String[] arguments;
	private final EmulatorSetting runSetting;
	private boolean isFinished = false;
	

	/**
	 * Creates a new Emulator instance
	 * @param device the device for which the emulator is started
	 * @param arguments any arguments
	 * @param runSetting any other settings
	 */
	public Emulator(final Device device, 
			final String[] arguments, final EmulatorSetting runSetting ) {
		super();
		this.device = device;
		this.arguments = arguments;
		this.runSetting = runSetting;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			System.out.println("Starting emulator [" + this.arguments[0] + "]...");
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec( this.arguments );
			if (this.runSetting.doWait()) {
				String info = this.device.getIdentifier() + ": ";
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
	 * Starts the emulator for the given device.
	 *  
	 * @param setting the setting
	 * @param device the current device
	 * @param infoProperties the info properties for the parameter-values
	 * @param project the ant-project to which this emulator belongs to 
	 * @param evaluator a boolean evaluator for the parameter-conditions
	 * @param wtkHome the home directory of the wireless toolkit
	 * @return true when an emulator could be detected
	 */
	public static Emulator createEmulator(Device device, EmulatorSetting setting, HashMap infoProperties, Project project, BooleanEvaluator evaluator, String wtkHome ) {
		String executable = setting.getExecutable();
		if (executable == null) {
			executable = device.getCapability("polish.Emulator");
		}
		boolean useDefaultEmulator = false;
		if (executable == null) {
			useDefaultEmulator = true;
			if (File.separatorChar == '/') {
				executable = wtkHome + "bin/emulator";
			} else {
				executable = wtkHome + "bin/emulator.exe";
			}
		} else {
			executable = PropertyUtil.writeProperties(executable, infoProperties, false );
			executable = PropertyUtil.writeProperties(executable, project.getProperties(), false );
			if (executable.indexOf("${") != -1) {
				int startIndex = executable.indexOf("${") + 2;
				int endIndex = executable.indexOf('}', startIndex );
				String missingProperty = executable.substring( startIndex, endIndex );
				System.out.println("Warning: the Ant-property [" + missingProperty + "] needs to be defined, so that the emulator [" + executable + "] can be started for the device [" + device.getIdentifier() + "].");
				return null;
			}
		}
		File execFile = new File( executable );
		if (!execFile.exists()) {
			System.out.println("Warning: unable to find the emulator [" + executable + "], now trying to start it as a command...");
		}
		// okay, now create the arguments:
		ArrayList argumentsList = new ArrayList();
		argumentsList.add( executable );
		
		if (useDefaultEmulator) {
			String xDevice = device.getCapability("polish.EmulatorSkin");
			if (xDevice != null) {
				// test if this emulator exists:
				File skinFile = new File(wtkHome + "wtklib/devices/" + xDevice );
				if (!skinFile.exists()) {
					System.out.println("Warning: unable  to start the emulator: the emulator-skin [" + xDevice + "] for the device [" + device.getIdentifier() + "] is not installed.");
					return null;
				}
				argumentsList.add( "-Xdevice:" + xDevice );
			} else {
				System.out.println("Warning: found no emulator-skin/Xdevice-definition for device [" + device.getIdentifier() + "], now using the default emulator.");
			}
			
			// add -Xdescriptor-parameter:
			argumentsList.add("-Xdescriptor:" + infoProperties.get("polish.jadPath") );
		}
		
		
		
		String[] arguments = (String[]) argumentsList.toArray(new String[argumentsList.size()]);
		Emulator emulator = new Emulator( device, arguments, setting );
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
				while ((c = this.input.read() ) != -1) {
					if (c == '\n') {
						this.output.println( log.toString() );
						log.delete( startPos,  log.length() );
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
