/*
 * Created on 05-Sep-2004 at 15:35:23.
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
package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.ConvertUtil;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.PropertyFileMap;

/**
 * <p>Is responsible for configuring and starting any WTK-based emulator.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

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
	 * Indicates whether this emulator supports a standard preferences file and the "-Xprefs"-parameter.
	 * Subclasses can override this method for adjustments.
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsPreferencesFile() {
		return true;
	}
	
	
	/**
	 * Indicates whether this emulator supports "-Xdomain"-parameter.
	 * Subclasses can override this method for adjustments.
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsSecurityDomain() {
		return true;
	}

	/**
	 * Indicates whether this emulator supports "-Xheapsize"-parameter.
	 * Subclasses can override this method for adjustments.
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsHeapsize() {
		return true;
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
	 * @param device the device
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
			Environment env, Project project,
			BooleanEvaluator evaluator, String wtkHome) 
	{
		// okay, now create the arguments:
		ArrayList argumentsList = new ArrayList();
		
		Variable[] parameters = getParameters(setting, project, evaluator, env);

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
					System.out.println("Info: Emulator [" + skinFile.getAbsolutePath() + "] not found.");
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
						System.out.println("Info: Emulator [" + skinFile.getAbsolutePath() + "] not found.");
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
		argumentsList.add( "-Xdescriptor:" + env.getVariable("polish.jadPath") );
		
		// add the -Xverbose-parameter:
		String trace = setting.getTrace();
		if (trace != null) {
			argumentsList.add("-Xverbose:" + trace );
		}
		
		// add the -Xprefs-parameter:
		if (supportsPreferencesFile()) {
			File preferencesFile = setting.getPreferences();
			boolean usingPreferencesFile = false;
			if (preferencesFile != null) {
				if (preferencesFile.exists()) {
					argumentsList.add("-Xprefs:" + preferencesFile.getAbsolutePath() );
					usingPreferencesFile = true;
				} else {
					System.err.println("Warning: unable to use preferences-file: the file [" + preferencesFile.getAbsolutePath() + "] does not exist.");
				}
			}
			if (getParameter("-Xprefs", parameters) != null) {
				usingPreferencesFile = true;
			}
			if (!usingPreferencesFile && setting.writePreferencesFile()) {
				File propertiesFile = getEmulatorPropertiesFile( env );
				PropertyFileMap emulatorPropertiesMap = new PropertyFileMap();
				if (propertiesFile != null && propertiesFile.exists()) {
					try {
						emulatorPropertiesMap.readFile( propertiesFile );
						//FileUtil.readPropertiesFile(propertiesFile, ':', '#', emulatorPropertiesMap, false );	
					} catch (IOException e) {
						e.printStackTrace();
						throw new BuildException("Unable to read the default emulator properties from [" + propertiesFile.getAbsolutePath() + "]: " + e.toString() + " - please make sure to use the WTK/2.2 or higher.", e  );
					}
				}
				
				
				addProperties(setting, emulatorPropertiesMap);
				preferencesFile = new File( device.getBaseDir() + File.separatorChar + "emulator.properties" );	
				try {
					emulatorPropertiesMap.writeFile( preferencesFile );
					//FileUtil.writePropertiesFile(preferences, emulatorPropertiesMap);
					argumentsList.add("-Xprefs:" + preferencesFile.getAbsolutePath() );
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Unable to set preferences-file for emulator: " + e.toString() );
				}
			} else if (setting.writePreferencesFile()) {
				System.out.println("Warning: unable to enable any profiler/monitor, since a preferences-file is used.");
			}
		} // the device supports the -Xprefs parameter.
		
		if (supportsSecurityDomain()) {
			// add the -Xdomain-parameter:
			String securityDomain = setting.getSecurityDomain();
			if (securityDomain != null) {
				argumentsList.add("-Xdomain:" + securityDomain );
			}
		}
		
		if (supportsHeapsize()) {
			// add the -Xheapsize-parameter:
			String heapSizeStr = device.getCapability("polish.HeapSize");
			if ((heapSizeStr != null) && !("dynamic".equals(heapSizeStr))) {
				long bytes = ConvertUtil.convertToBytes( heapSizeStr );
				argumentsList.add("-Xheapsize:" + bytes );
			}
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

	protected void addProperties(EmulatorSetting setting, PropertyFileMap emulatorPropertiesMap) {
		// now write a preferences-file:
		emulatorPropertiesMap.put( "kvem.memory.monitor.enable", "" + setting.enableMemoryMonitor() );
		emulatorPropertiesMap.put( "kvem.profiler.enable", "" + setting.enableProfiler() );
		boolean enableNetworkMonitor = setting.enableNetworkMonitor();
		emulatorPropertiesMap.put( "kvem.netmon.comm.enable", "" + enableNetworkMonitor );
		emulatorPropertiesMap.put( "kvem.netmon.datagram.enable", ""  + enableNetworkMonitor );
		emulatorPropertiesMap.put( "kvem.netmon.http.enable", "" + enableNetworkMonitor );
		emulatorPropertiesMap.put( "kvem.netmon.https.enable", "" + enableNetworkMonitor );
		emulatorPropertiesMap.put( "kvem.netmon.socket.enable", "" + enableNetworkMonitor );
		emulatorPropertiesMap.put( "kvem.netmon.ssl.enable", "" + enableNetworkMonitor );
		
		emulatorPropertiesMap.put( "bluetooth.connected.devices.max", "7" );
		emulatorPropertiesMap.put( "bluetooth.device.authentication", "on" );
		emulatorPropertiesMap.put( "bluetooth.device.authorization", "on" );
		emulatorPropertiesMap.put( "bluetooth.device.discovery.enable", "true" );
		emulatorPropertiesMap.put( "bluetooth.device.discovery.timeout", "10000" );
		emulatorPropertiesMap.put( "bluetooth.device.encryption", "on" );
		emulatorPropertiesMap.put( "bluetooth.device.friendlyName", "WirelessToolkit" );
		emulatorPropertiesMap.put( "bluetooth.enable", "true" );
		emulatorPropertiesMap.put( "bluetooth.l2cap.receiveMTU.max", "512" );
		emulatorPropertiesMap.put( "bluetooth.sd.attr.retrievable.max", "10" );
		emulatorPropertiesMap.put( "bluetooth.sd.trans.max", "5" );
		emulatorPropertiesMap.put( "file.extension", "jad" );
		emulatorPropertiesMap.put( "heap.size", "" );
		emulatorPropertiesMap.put( "http.proxyHost", "" );
		emulatorPropertiesMap.put( "http.proxyPort", "" );
		emulatorPropertiesMap.put( "http.version", "HTTP/1.1" );
		emulatorPropertiesMap.put( "https.proxyHost", "" );
		emulatorPropertiesMap.put( "https.proxyPort", "" );
		emulatorPropertiesMap.put( "irdaobex.discoveryTimeout", "10000" );
		emulatorPropertiesMap.put( "irdaobex.packetLength", "4096" );
		emulatorPropertiesMap.put( "jammode", "" );
		emulatorPropertiesMap.put( "kvem.api.exclude", "" );
		emulatorPropertiesMap.put( "kvem.device", "DefaultColorPhone" );
		emulatorPropertiesMap.put( "kvem.netmon.autoclose", "false" );
		emulatorPropertiesMap.put( "kvem.netmon.enable", "false" );
		emulatorPropertiesMap.put( "kvem.netmon.filter_file_name", "netmon_filter.dat" );
		emulatorPropertiesMap.put( "kvem.netmon.fixed_font_name", "Courier New" );
		emulatorPropertiesMap.put( "kvem.netmon.fixed_font_size", "12" );
		emulatorPropertiesMap.put( "kvem.netmon.variable_font_name", "Arial" );
		emulatorPropertiesMap.put( "kvem.netmon.variable_font_size", "14" );
		emulatorPropertiesMap.put( "kvem.profiler.outfile", "" );
		emulatorPropertiesMap.put( "kvem.profiler.showsystem", "false" );
		emulatorPropertiesMap.put( "kvem.trace.all", "false" );
		emulatorPropertiesMap.put( "kvem.trace.allocation", "false" );
		emulatorPropertiesMap.put( "kvem.trace.bytecodes", "false" );
		emulatorPropertiesMap.put( "kvem.trace.calls", "false" );
		emulatorPropertiesMap.put( "kvem.trace.calls.verbose", "false" );
		emulatorPropertiesMap.put( "kvem.trace.class", "false" );
		emulatorPropertiesMap.put( "kvem.trace.class.verbose", "false" );
		emulatorPropertiesMap.put( "kvem.trace.events", "false" );
		emulatorPropertiesMap.put( "kvem.trace.exceptions", "false" );
		emulatorPropertiesMap.put( "kvem.trace.frames", "false" );
		emulatorPropertiesMap.put( "kvem.trace.gc", "false" );
		emulatorPropertiesMap.put( "kvem.trace.gc.verbose", "false" );
		emulatorPropertiesMap.put( "kvem.trace.monitors", "false" );
		emulatorPropertiesMap.put( "kvem.trace.networking", "false" );
		emulatorPropertiesMap.put( "kvem.trace.stackchunks", "false" );
		emulatorPropertiesMap.put( "kvem.trace.stackmaps", "false" );
		emulatorPropertiesMap.put( "kvem.trace.threading", "false" );
		emulatorPropertiesMap.put( "kvem.trace.verifier", "false" );
		emulatorPropertiesMap.put( "mm.control.capture", "true" );
		emulatorPropertiesMap.put( "mm.control.midi", "true" );
		emulatorPropertiesMap.put( "mm.control.mixing", "true" );
		emulatorPropertiesMap.put( "mm.control.record", "true" );
		emulatorPropertiesMap.put( "mm.control.volume", "true" );
		emulatorPropertiesMap.put( "mm.format.midi", "true" );
		emulatorPropertiesMap.put( "mm.format.video", "true" );
		emulatorPropertiesMap.put( "mm.format.wav", "true" );
		emulatorPropertiesMap.put( "netspeed.bitpersecond", "1200" );
		emulatorPropertiesMap.put( "netspeed.enableSpeedEmulation", "false" );
		emulatorPropertiesMap.put( "prng.secure", "false" );
		emulatorPropertiesMap.put( "screen.graphicsLatency", "0" );
		emulatorPropertiesMap.put( "screen.refresh.mode", "" );
		emulatorPropertiesMap.put( "screen.refresh.rate", "30" );
		emulatorPropertiesMap.put( "security.domain", "untrusted" );
		emulatorPropertiesMap.put( "storage.root", "" );
		emulatorPropertiesMap.put( "storage.size", "" );
		emulatorPropertiesMap.put( "vmspeed.bytecodespermilli", "100" );
		emulatorPropertiesMap.put( "vmspeed.enableEmulation", "false" );
		emulatorPropertiesMap.put( "vmspeed.range", "100,1000" );
		emulatorPropertiesMap.put( "wma.client.phoneNumber", "" );
		emulatorPropertiesMap.put( "wma.server.deliveryDelayMS", "" );
		emulatorPropertiesMap.put( "wma.server.firstAssignedPhoneNumber", "+5550000" );
		emulatorPropertiesMap.put( "wma.server.percentFragmentLoss", "0" );
		emulatorPropertiesMap.put( "wma.smsc.phoneNumber", "+1234567890" );
	}

	/**
	 * Retrieves the file containing the default properties for the emulator.
	 * 
	 * @param env the environment that helps to resolve files (env.resolveFile(String)).
	 * @return by default ${wtk.home}/wtklib/emulator.properties is returned
	 */
	protected File getEmulatorPropertiesFile(Environment env) {
		return env.resolveFile("${wtk.home}/wtklib/emulator.properties");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#startEmulator()
	 */
	public Process startEmulator() throws IOException {
		System.out.println("Starting emulator [" + this.arguments[0] + "]...");
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec( this.arguments, this.environment, this.workingDirectory );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		return this.arguments;
	}

}
