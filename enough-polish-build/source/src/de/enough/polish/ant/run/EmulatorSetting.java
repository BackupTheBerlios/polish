/*
 * Created on 04-Sep-2004 at 18:32:53.
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

import org.apache.tools.ant.Project;

import de.enough.polish.ant.Setting;

/**
 * <p>Includes the settings for running the emulator.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        04-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EmulatorSetting extends Setting {
	
	private String executable;
	private boolean wait = true;

	/**
	 * Creates an empty uninitialised run setting.
	 * 
	 * @param project the Ant project to which this setting belongs to.
	 */
	public EmulatorSetting( Project project ) {
		super();
	}
	
	

	/**
	 * Returns the command for launching the emulator.
	 * 
	 * @return Returns the executable.
	 */
	public String getExecutable() {
		return this.executable;
	}
	
	/**
	 * Sets the executable command for launching the emulator.
	 * This overrides any settings from the device database.
	 * 
	 * @param executable The executable to set.
	 */
	public void setExecutable(String executable) {
		this.executable = executable;
	}
	
	
	/**
	 * Determines whether J2ME Polish should wait for the end of execution of the emulator
	 * 
	 * @return Returns true when J2ME Polish should wait for the end of execution of the emulator.
	 */
	public boolean doWait() {
		return this.wait;
	}
	
	/**
	 * Sets the wait-status for running the emulator.
	 * Default value is true, so that J2ME Polish will show all emulator
	 * output. When there is more than one device selected, J2ME Polish will continue
	 * with the processing in the background, but will wait for the emulator to stop.
	 * 
	 * @param wait true when J2ME Polish should wait until the emulator is finished.
	 * 
	 */
	public void setWait(boolean wait) {
		this.wait = wait;
	}
}
