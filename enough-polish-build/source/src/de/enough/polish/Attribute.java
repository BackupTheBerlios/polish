/*
 * Created on 29-Aug-2004 at 16:28:28.
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
package de.enough.polish;

import org.apache.tools.ant.BuildException;

import de.enough.polish.util.TextUtil;

/**
 * <p>Represents a JAD or MANIFEST attribute.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        29-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Attribute extends Variable {
	private static final String TARGET_JAD = "jad";
	private static final String TARGET_MANIFEST = "manifest"; 

	private boolean targetsJad = true;
	private boolean targetsManifest = true;

	/**
	 * Creates a new uninitalised attribute
	 */
	public Attribute() {
		super();
	}

	/**
	 * Creates a new initialised attribute.
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public Attribute(String name, String value) {
		super(name, value);
	}
	
	/**
	 * Sets the target for this attribute.
	 * 
	 * @param targetStr the target, either "jad" or "manifest" or both separated by comma.
	 */
	public void setTarget( String targetStr ) {
		this.targetsManifest = false;
		this.targetsJad = false;
		String[] targets = TextUtil.splitAndTrim( targetStr, ',' );
		for (int i = 0; i < targets.length; i++) {
			String target = targets[i];
			if (TARGET_JAD.equalsIgnoreCase( target )) {
				this.targetsJad = true;
			} else if (TARGET_MANIFEST.equalsIgnoreCase( target )) {
				this.targetsManifest = true;
			} else {
				throw new BuildException("The [target]-attribute of an <attribute>-element needs to bei either [jad] or [manifest] or both separated by comma. The target [" + targetStr + "] is invalid. Please correct the [build.xml] file.");
			}
		}
	}

	/**
	 * @return Returns true when this attribute should be added to the JAD.
	 */
	public boolean targetsJad() {
		return this.targetsJad;
	}
	
	/**
	 * @return Returns true when this attribute should be added to the MANIFEST.
	 */
	public boolean targetsManifest() {
		return this.targetsManifest;
	}
}
