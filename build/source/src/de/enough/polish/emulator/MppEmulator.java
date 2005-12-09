/*
 * Created on 07-Dec-2005 at 17:40:13.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;

/**
 * <p>Starts the mpowerplayer for emulation.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        07-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MppEmulator extends Emulator {

	private String mppPlayerPath;

	/**
	 * Creates a new emulator launcher.
	 */
	public MppEmulator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, de.enough.polish.Environment, org.apache.tools.ant.Project, de.enough.polish.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment env,
			Project project, BooleanEvaluator evaluator, String wtkHome) {
		String mppHome = env.getVariable("mpp.home");
		if (mppHome == null) {
			System.err.println("Warning: unable to launch the mpowerplayer emulator - please specify the \"mpp.home\" property in your build.xml script. \"mpp.home\" needs to point to the installation folder of the mpowerplayer SDK.");
			return false;
		}
		File playerJar = new File( mppHome + File.separatorChar + "player.jar" );
		if (!playerJar.exists()) {
			System.err.println("Warning: unable to launch the mpowerplayer emulator - the \"mpp.home\" property in your build.xml script points to the invcalid path [" + mppHome + "]. \"mpp.home\" needs to point to the installation folder of the mpowerplayer SDK (=the folder in which the \"player.jar\" is located).");
			return false;
		}
		this.mppPlayerPath = playerJar.getAbsolutePath();
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		return new String[] {
			"java",
			"-jar",
			this.mppPlayerPath,
			this.environment.getVariable("polish.jadPath")
		};
	}

}
