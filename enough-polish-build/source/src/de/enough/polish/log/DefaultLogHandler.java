/*
 * Created on 26-Apr-2005 at 18:26:42.
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
package de.enough.polish.log;

import java.io.File;
import java.util.Locale;

import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionSetting;
import de.enough.polish.Variable;

/**
 * <p>The default log handler just sets preprocessing directives according to the given parameters.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultLogHandler extends LogHandler {
	
	private Variable[] parameters;


	/**
	 * Creates a new default log handler 
	 */
	public DefaultLogHandler() {
		super();
	}
	
	public void setParameters( Variable[] parameter, File baseDir ) {
		this.parameters = parameter;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#intialize(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void intialize(Device device, Locale locale, Environment environment) {
		super.intialize(device, locale, environment);
		ExtensionSetting setting = this.getExtensionSetting();
		if (setting == null) {
			System.out.println("Warning: default loghandler has not configuration setting and is unable to set up environment/preprocessing variables.");
			return;
		}
		String name = setting.getName();
		if (name == null) {
			System.out.println("Warning: default loghandler has not name and is unable to set up environment/preprocessing variables.");
			return;
		}
		String value = environment.getVariable( "polish.log.handlers" );
		if (value == null ) {
			environment.addVariable( "polish.log.handlers", getClientLogHandlerClass(environment) );
		} else {
			environment.addVariable( "polish.log.handlers", value + "," + getClientLogHandlerClass(environment) );
		}
		BooleanEvaluator evalutor = environment.getBooleanEvaluator();
		if (this.parameters != null) {
			Project project = environment.getProject();
			for (int i = 0; i < this.parameters.length; i++) {
				Variable parameter = this.parameters[i];
				if (parameter.isConditionFulfilled(evalutor, project)) {
					environment.addVariable( "polish.log." + name + "." + parameter.getName(), parameter.getValue() );
				}
			}
		}
	}

	/**
	 * Retrieves the name of the corresponding handler-class that runs on the J2ME client.
	 * The client class can be defined either with the "clientClass" parameter of the &lt;handler&gt;-element
	 * or by the "clientClass" setting in extensions.xml/custom-extensions.xml.
	 * 
	 * @return the client log handler class
	 * @throws IllegalStateException when the client log handler class is not defined anywhere
	 * @throws NullPointerException when the setting is null
	 */
	protected String getClientLogHandlerClass( Environment environment ) {
		String clientClass =  getParameterValue("clientClass", environment);
		if (clientClass == null) {
			throw new IllegalStateException("The loghandler [" + this.extensionSetting.getName() + "] has no clientClass-parameter defined. Please check your settings.");
		}
		return clientClass;
	}
}
