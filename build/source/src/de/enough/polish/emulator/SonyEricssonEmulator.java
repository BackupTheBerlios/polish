/*
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
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.OutputFilter;

/**
 * <p>Starts Sony-Ericsson emulators.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09.10.2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SonyEricssonEmulator extends WtkEmulator {

	private String sonyEricssonHome;
	private String sonyWtkHome;

	/**
	 * Creates a new emulator instance
	 */
	public SonyEricssonEmulator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorSkin(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorSkin(String wtkHome, String xDevice  ) {
		this.sonyWtkHome = null;
		if (this.sonyEricssonHome == null ) {
			return super.getEmulatorSkin(wtkHome, xDevice);
		}
		File skinFolder = new File( this.sonyEricssonHome + "\\PC_Emulation\\WTK1\\wtklib\\devices\\" + xDevice );
		if (skinFolder.exists()) {
			this.sonyWtkHome = this.sonyEricssonHome + "\\PC_Emulation\\WTK1";
			return skinFolder;
		} else {
			skinFolder = new File( this.sonyEricssonHome + "\\PC_Emulation\\WTK2\\wtklib\\devices\\" + xDevice );
			if (skinFolder.exists()) {
				this.sonyWtkHome = this.sonyEricssonHome + "\\PC_Emulation\\WTK2";
				return skinFolder;
			}
		}
		// try to get the emulator from the default WTK:
		return super.getEmulatorSkin(wtkHome, xDevice);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorExcecutable(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorExcecutable(String wtkHome, String xDevice, Device device) {
		if (this.sonyEricssonHome == null || this.sonyWtkHome == null) {
			return super.getEmulatorExcecutable(wtkHome, xDevice, device);
		}
		
		String execPath = this.sonyWtkHome + "\\bin\\emulator.exe";
		this.workingDirectory = new File( this.sonyWtkHome + "\\bin" );
		return new File( execPath );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device device, EmulatorSetting setting,
			Environment properties, Project project, BooleanEvaluator evaluator,
			String wtkHome) 
	{
		String skin = device.getCapability("polish.Emulator.Skin");
		if (skin == null) {
			// add default-emulator name:
			device.addDirectCapability("polish.Emulator.Skin", "SonyEricsson_" + device.getName());
		}
		String sonyHomePath = properties.getVariable("sony-ericsson.home");
		if (sonyHomePath == null) {
			sonyHomePath = properties.getVariable("sonyericsson.home");
		}
		if (sonyHomePath == null) {
			if (File.separatorChar == '\\') {
				sonyHomePath = "C:\\SonyEricsson\\J2ME_SDK";
				File home = new File( sonyHomePath );
				if (!home.exists()) {
					sonyHomePath = null;
					// try to start emulators from the standard WTK directory.
				}
			}
		} else {
			File home = new File( sonyHomePath );
			if (!home.exists()) {
				System.err.println("Unable to start emulator for device [" + device.getIdentifier() + "]: Please adjust the ${nokia.home}-property in your build.xml. The path [" + sonyHomePath + "] does not exist.");
			}
		}
		this.sonyEricssonHome = sonyHomePath;
		return super.init(device, setting, properties, project, evaluator, wtkHome);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#exec(java.lang.String[], java.lang.String, boolean, de.enough.polish.util.OutputFilter, java.io.File)
	 */
	protected int exec(String[] args, String info, boolean wait, OutputFilter filter, File executionDir) throws IOException {
		int result = super.exec(args, info, wait, filter, executionDir);
		if ( result != 0 ) {
			// there was an error - new Sony Ericsson emulators require a "_emu" 
			// at the end of the device name - why? Who knows?
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.startsWith("-Xdevice:") ){
					args[i] = arg + "_emu";
					break;
				}
			}
			result = super.exec(args, info, wait, filter, executionDir);
		}
		return result;
	}

	

}
