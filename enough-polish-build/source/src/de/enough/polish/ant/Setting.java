/*
 * Created on 16-Jul-2004 at 03:39:27.
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
package de.enough.polish.ant;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Variable;

/**
 * <p>A base class for settings which accept parameters.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        16-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Setting extends ConditionalElement {
	
	private ArrayList parameters;

	/**
	 * Creates a new empty parameter setting
	 */
	public Setting() {
		// initialisation is done in the subclass
	}
	
	/**
	 * Adds a parameter to this setting.
	 * 
	 * @param var the parameter with a [name] and a [value] attribute.
	 */
	public void addConfiguredParameter( Variable var ) {
		if (var.getName() == null) {
			throw new BuildException("Invalid parameter: please specify the attribute [name] for each <parameter> element.");
		}
		if (var.getValue() == null) {
			throw new BuildException("Invalid parameter: please specify the attribute [value] for each <parameter> element.");
		}
		if (this.parameters == null) {
			this.parameters = new ArrayList();
		}
		this.parameters.add( var );
	}
	
	/**
	 * Determines whether this setting has any parameters.
	 * 
	 * @return true when there are parameters for this setting.
	 */
	public boolean hasParameters() {
		return (this.parameters != null);
	}
	
	/**
	 * Retrieves the parameters of this setting.
	 * 
	 * @return an array of variable, can be empty but not null.
	 */
	public Variable[] getParameters() {
		if (this.parameters == null) {
			return new Variable[ 0 ];
		} else {
			return (Variable[]) this.parameters.toArray( new Variable[ this.parameters.size() ]);
		}
	}

}
