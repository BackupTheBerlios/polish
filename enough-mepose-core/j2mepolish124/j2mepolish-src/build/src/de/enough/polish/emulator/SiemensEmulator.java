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
import java.util.HashMap;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.preprocess.BooleanEvaluator;

/**
 * <p>Launches a Siemens emulator.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01.10.2004 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SiemensEmulator extends WtkEmulator {

	private String siemensHome;

	/**
	 * Creates a new emulator
	 */
	public SiemensEmulator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorSkin(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorSkin(String wtkHome, String xDevice) {
		return new File( this.siemensHome + "\\emulators\\" + xDevice );
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.WtkEmulator#supportsPreferencesFile()
	 */
	protected boolean supportsPreferencesFile() {
		return false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.WtkEmulator#supportsHeapsize()
	 */
	protected boolean supportsHeapsize() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.WtkEmulator#supportsSecurityDomain()
	 */
	protected boolean supportsSecurityDomain() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device device, EmulatorSetting setting,
			HashMap properties, Project project, BooleanEvaluator evaluator,
			String wtkHome) 
	{
		String siemensHomePath = (String) properties.get("siemens.home");
		if (siemensHomePath == null) {
			siemensHomePath = "C:\\siemens\\SMTK";
		}
		File executable = new File( siemensHomePath + "\\bin\\Emulator.exe");
		if (!executable.exists()) {
			System.err.println("Unable to start emulator for device [" + device.getIdentifier() + "]: unable to find Siemens-emulator [" + executable.getAbsolutePath() + "]. Consider to set the ${siemens.home} property in your build.xml");
			return false;
		}
		this.siemensHome = siemensHomePath;
		String skin = device.getCapability("polish.Emulator.Skin");
		if (skin == null) {
			device.addDirectCapability("polish.Emulator.Skin", device.getName());
		}
		if (super.init(device, setting, properties, project, evaluator, wtkHome)) {
			this.arguments[0] = executable.getAbsolutePath();
			return true;
		} else {
			return false;
		}
	}

}
