/*
 * Created on 05-Sep-2004 at 15:35:23.
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
package de.enough.polish.ant.emulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.Variable;
import de.enough.polish.preprocess.BooleanEvaluator;
import de.enough.polish.util.ConvertUtil;
import de.enough.polish.util.FileUtil;

/**
 * <p>Is responsible for configuring and starting any WTK-based emulator.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        05-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WtkEmulator extends Emulator {

	protected String[] arguments;
	protected boolean appendXdeviceArgument;
	protected String[] environment;
	protected File workingDirectory;
	
	/**
	 * Creates a new emulator
	 */
	public WtkEmulator() {
		super();
		this.appendXdeviceArgument = true;
	}
	
	/**
	 * Retrieves the folder which contains the emulator skin.
	 * 
	 * @param wtkHome the path to the Wireless Toolkit
	 * @param xDevice the name of the skin
	 * @return the file which points to the folder containing the skin
	 */
	protected File getEmulatorSkin( String wtkHome, String xDevice ) {
		return new File(wtkHome + "wtklib/devices/" + xDevice );
	}
	
	/**
	 * Retrieves the executable for the given device.
	 * 
	 * @param wtkHome the path to the Wireless Toolkit
	 * @param xDevice the name of the skin
	 * @return the file which points to the emulator-application
	 */
	protected File getEmulatorExcecutable( String wtkHome, String xDevice, Device device ) {
		String executable = null;
		if (File.separatorChar == '/') {
			executable = wtkHome + "bin/emulator";
		} else {
			executable = wtkHome + "bin/emulator.exe";
		}
		return new File( executable );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device device, EmulatorSetting setting,
			HashMap properties, Project project,
			BooleanEvaluator evaluator, String wtkHome) 
	{
		// okay, now create the arguments:
		ArrayList argumentsList = new ArrayList();
		
		Variable[] parameters = getParameters(setting, project, evaluator, properties);

		String xDevice = getParameterValue("-Xdevice", parameters );
		boolean xDeviceParameterGiven = true;
		if (xDevice == null) {
			xDeviceParameterGiven = false;
			xDevice = device.getCapability("polish.Emulator.Skin");
		}
		if (xDevice != null) {
			// test if this emulator exists:
			File skinFile = getEmulatorSkin(wtkHome, xDevice);
			if (!skinFile.exists()) {
				if (xDeviceParameterGiven) {
					System.out.println("Warning: unable  to start the emulator: the emulator-skin [" + xDevice + "] for the device [" + device.getIdentifier() + "] is not installed.");
					return false;
				} else {
					String originalSkin = xDevice;
					// check if there are other skins given:
					int skinNumber = 2;
					while (true) {
						xDevice = device.getCapability("polish.Emulator.Skin." + skinNumber);
						if (xDevice == null) {
							break;
						}
						skinNumber++;
						skinFile = getEmulatorSkin(wtkHome, xDevice);
						if (skinFile.exists()) {
							break;
						}
					}
					if (!skinFile.exists()) {
						System.out.println("Warning: unable  to start the emulator: the emulator-skin [" + originalSkin + "] for the device [" + device.getIdentifier() + "] is not installed.");
						return false;
					}
				}
			}
			// okay, skin-file exists:
		} else {
			System.out.println("Warning: found no emulator-skin or -Xdevice-parameter for device [" + device.getIdentifier() + "], now using the default emulator.");
		}
		
		// get emulator executable:
		File execFile = getEmulatorExcecutable(wtkHome, xDevice, device );
		String executable = execFile.getAbsolutePath();
		if (!execFile.exists()) {
			System.out.println("Warning: unable to find the emulator [" + executable + "].");
			return false;
		}
				
		argumentsList.add( executable );
		
		if (this.appendXdeviceArgument && (xDevice != null)) {
			argumentsList.add( "-Xdevice:" + xDevice );
		}
		
		// add -Xdescriptor-parameter:
		argumentsList.add("-Xdescriptor:" + properties.get("polish.jadPath") );
		
		// add the -Xverbose-parameter:
		String trace = setting.getTrace();
		if (trace != null) {
			argumentsList.add("-Xverbose:" + trace );
		}
		
		// add the -Xprefs-parameter:
		File preferences = setting.getPreferences();
		boolean usingPreferencesFile = false;
		if (preferences != null) {
			if (preferences.exists()) {
				argumentsList.add("-Xprefs:" + preferences.getAbsolutePath() );
				usingPreferencesFile = true;
			} else {
				System.err.println("Warning: unable to use preferences-file: the file [" + preferences.getAbsolutePath() + "] does not exist.");
			}
		}
		if (getParameter("-Xprefs", parameters) != null) {
			usingPreferencesFile = true;
		}
		if (!usingPreferencesFile && setting.writePreferencesFile()) {
			// now write a preferences-file:
			String[] lines = new String[ 8 ];
			lines[0] = "kvem.memory.monitor.enable:" + setting.enableMemoryMonitor();
			lines[1] = "kvem.profiler.enable:" + setting.enableProfiler();
			boolean enableNetworkMonitor = setting.enableNetworkMonitor();
			lines[2] = "kvem.netmon.comm.enable:" + enableNetworkMonitor;
			lines[3] = "kvem.netmon.datagram.enable:" + enableNetworkMonitor;
			lines[4] = "kvem.netmon.http.enable:" + enableNetworkMonitor;
			lines[5] = "kvem.netmon.https.enable:" + enableNetworkMonitor;
			lines[6] = "kvem.netmon.socket.enable:" + enableNetworkMonitor;
			lines[7] = "kvem.netmon.ssl.enable:" + enableNetworkMonitor;
			preferences = new File( device.getBaseDir() + File.separatorChar + "emulator.properties" );	
			try {
				FileUtil.writeTextFile(preferences, lines);
				argumentsList.add("-Xprefs:" + preferences.getAbsolutePath() );
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to set preferences-file for emulator: " + e.toString() );
			}
		} else if (setting.writePreferencesFile()) {
			System.out.println("Warning: unable to enable any profiler/monitor, since a preferences-file is used.");
		}
		
		// add the -Xdomain-parameter:
		String securityDomain = setting.getSecurityDomain();
		if (securityDomain != null) {
			argumentsList.add("-Xdomain:" + securityDomain );
		}
		
		// add the -Xheapsize-parameter:
		String heapSizeStr = device.getCapability("polish.HeapSize");
		if ((heapSizeStr != null) && !("dynamic".equals(heapSizeStr))) {
			long bytes = ConvertUtil.convertToBytes( heapSizeStr );
			argumentsList.add("-Xheapsize:" + bytes );
		}
		
		//now add other user-defined parameters:
		for (int i = 0; i < parameters.length; i++) {
			Variable parameter = parameters[i];
			String name = parameter.getName();
			if ( (xDeviceParameterGiven) &&  ("-xDevice".equals(parameter.getName()) ) ) {
				continue;
			}
			String value = parameter.getValue();
			if (value.length() > 0) {
				if ( ( name.charAt(0) == '-')  
						&& ((name.charAt(1) == 'X') || (name.charAt(1) == 'D')) ) {
					argumentsList.add( name + ":" + value );
				} else {
					argumentsList.add( name + " " + value );
				}
			} else {
				argumentsList.add( name );
			}
		}
		
		this.arguments = (String[]) argumentsList.toArray( new String[ argumentsList.size() ] );
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#startEmulator()
	 */
	public Process startEmulator() throws IOException {
		System.out.println("Starting emulator [" + this.arguments[0] + "]...");
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec( this.arguments, this.environment, this.workingDirectory );
	}

}
