/*
 * Created on 27-Oct-2005 at 00:46:30.
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

import java.util.List;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ant.emulator.DebuggerSetting;

/**
 * <p>Connects the emulator to a debugger.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Debugger extends Extension {

	/**
	 * Creates a new debugger. 
	 */
	public Debugger() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		int port = ((DebuggerSetting) this.extensionSetting).getPort();
		connectDebugger( port, device, locale, env );
	}

	/**
	 * Connects to a debugger interface.
	 * 
	 * @param port the port in which the emulator listens
	 * @param device the current device
	 * @param locale the current locale
	 * @param env the environment
	 */
	public abstract void connectDebugger(int port, Device device, Locale locale, Environment env);

	/**
	 * Adds the debugging settings to the arguments list.
	 * By default the UEI arguments -Xdebug and -Xrunjdwp arguments are added by calling debugger.addDebugArguments( List ).
	 * 
	 * @param argsList the arguments list
	 */
	public void addDebugArguments(List argsList) {
		argsList.add( "-Xdebug" );
		argsList.add( "-Xrunjdwp:address=" + ((DebuggerSetting) this.extensionSetting).getPort() );

	} 

}
