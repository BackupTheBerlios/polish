/*
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
import java.util.HashMap;

import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.preprocess.BooleanEvaluator;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.TextUtil;

/**
 * <p>Launches an emulator from the command line.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        01.10.2004 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GenericEmulator extends Emulator {
	
	private String[] arguments;

	/**
	 * Creates a new emulator
	 */
	public GenericEmulator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device device, EmulatorSetting setting,
			HashMap properties, Project project, BooleanEvaluator evaluator,
			String wtkHome) 
	{
		String execStr = (String) properties.get("polish.Emulator.Executable");
		if (execStr == null) {
			System.err.println("Unable to launch emulator for [" + device.getIdentifier() + "]: Did not find the capability [Emulator.Executable] in the [devices.xml] file.");
			return false;
		}
		// setting of default values for ${siemens.home} and ${nokia.home}, when
		// these are not defined:
		Object siemensHome = properties.get("siemens.home");
		if ((siemensHome == null) && (File.separatorChar == '\\')) {
			String siemensHomePath = "C:\\siemens";
			if (new File(siemensHomePath).exists()) {
				properties.put("siemens.home", siemensHomePath );
			}
		}
		Object nokiaHome = properties.get("nokia.home");
		if (nokiaHome == null) {
			String nokiaHomePath;
			if (File.separatorChar == '\\') {
				nokiaHomePath = "C:\\Nokia";
			} else {
				nokiaHomePath = System.getProperty("user.home") + "/Nokia";
			}
			if (new File(nokiaHomePath).exists()) {
				properties.put("nokia.home", nokiaHomePath);
			}
		}
		execStr = PropertyUtil.writeProperties( execStr, properties );
		if (execStr.indexOf("${") != -1) {
			int propStart = execStr.indexOf("${");
			int propEnd = execStr.indexOf('}', propStart);
			String missingProperty = execStr.substring( propStart, propEnd + 1);
			System.err.println("Unable to launch emulator for [" + device.getIdentifier() + "]: Please define the needed property " + missingProperty + " in your build.xml file.");
			return false;
		}
		if (execStr.length() > 6) {
			File executable = new File( execStr );
			if (!executable.exists()) {
				System.err.println("Unable to launch emulator for [" + device.getIdentifier() + "]: Did not find the executable [" + execStr + "].");
				return false;
			}
		}
		
		String argsStr = (String) properties.get("polish.Emulator.Arguments");
		if (argsStr == null) {
			System.err.println("Unable to launch emulator for [" + device.getIdentifier() + "]: Did not find the capability [Emulator.Arguments] in the [devices.xml] file.");
			return false;
		}
		argsStr = PropertyUtil.writeProperties( argsStr, properties );
		if (argsStr.indexOf("${") != -1) {
			int propStart = argsStr.indexOf("${");
			int propEnd = argsStr.indexOf('}', propStart);
			String missingProperty = argsStr.substring( propStart, propEnd + 1);
			System.err.println("Unable to launch emulator for [" + device.getIdentifier() + "]: Please define the needed property " + missingProperty + " in your build.xml file.");
			return false;
		}
		String[] args = TextUtil.split( argsStr, ";;");
		
		this.arguments = new String[ args.length + 1];
		this.arguments[0] = execStr;
		System.arraycopy( args, 0, this.arguments, 1, args.length );
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#startEmulator()
	 */
	public Process startEmulator() throws IOException {
		System.out.println("Starting emulator [" + this.arguments[0] + "]...");
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec( this.arguments );
	}

}
