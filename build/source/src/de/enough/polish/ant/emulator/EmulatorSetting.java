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
package de.enough.polish.ant.emulator;

import java.io.File;

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
	private String className;
	private boolean wait = true;
	private String trace;
	private File preferences;
	private String securityDomain;
	private boolean writePreferencesFile;
	private boolean enableProfiler;
	private boolean enableMemoryMonitor;
	private boolean enableNetworkMonitor;
	
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

	/**
	 * Sets the classname of the emulator.
	 * This defaults to the WTK-emulator.
	 * This setting is usually not needed and will override
	 * any settings from the device database.
	 * 
	 * @param className the name of the de.enough.polish.ant.emulator.Emulator-subclass
	 * 					responsible for invoking the emulator.  
	 */
	public void setClass( String className ){
		this.className = className;
	}

	/**
	 * @return the name of the class responsible for invoking the emulator.
	 * @see #setClass( String )
	 */
	public String getEmulatorClassName() {
		return this.className;
	}
	
	/**
	 * Gets the preferences file
	 * 
	 * @return Returns the file containing the emulator-preferences.
	 */
	public File getPreferences() {
		return this.preferences;
	}
	
	/**
	 * Sets the file containing the emulator preferences.
	 * 
	 * @param preferences the file containing the emulator preferences.
	 */
	public void setPreferences(File preferences) {
		this.preferences = preferences;
	}
	
	/**
	 * @return Returns the trace.
	 */
	public String getTrace() {
		return this.trace;
	}
	
	/**
	 * Sets the trace-options.
	 * Valid trace-options are "class", "gc" or "all".
	 * 
	 * @param trace The trace-options in a comma-separated list
	 */
	public void setTrace(String trace) {
		this.trace = trace;
	}
	
	/**
	 * @return Returns the securityDomain.
	 */
	public String getSecurityDomain() {
		return this.securityDomain;
	}
	
	/**
	 * Sets the security domain.
	 * 
	 * @param securityDomain The security-domain: either "trusted", "untrusted", "minimum" or "maximum"
	 */
	public void setSecurityDomain(String securityDomain) {
		this.securityDomain = securityDomain;
	}
	
	/**
	 * @return Returns the enableMemoryMonitor.
	 */
	public boolean enableMemoryMonitor() {
		return this.enableMemoryMonitor;
	}
	
	/**
	 * @param enableMemoryMonitor The enableMemoryMonitor to set.
	 */
	public void setEnableMemoryMonitor(boolean enableMemoryMonitor) {
		this.enableMemoryMonitor = enableMemoryMonitor;
		this.writePreferencesFile = true;
	}
	
	/**
	 * @return Returns the enableProfiler.
	 */
	public boolean enableProfiler() {
		return this.enableProfiler;
	}
	/**
	 * @param enableProfiler The enableProfiler to set.
	 */
	public void setEnableProfiler(boolean enableProfiler) {
		this.enableProfiler = enableProfiler;
		this.writePreferencesFile = true;
	}
	
	public boolean enableNetworkMonitor() {
		return this.enableNetworkMonitor;
	}
	
	public void setEnableNetworkMonitor( boolean enable ) {
		this.enableNetworkMonitor = enable;
		this.writePreferencesFile = true;
	}
	
	/**
	 * @return Returns the writePreferencesFile.
	 */
	public boolean writePreferencesFile() {
		return this.writePreferencesFile;
	}

}
