/*
 * Created on 29-Aug-2004 at 16:28:28.
 * 
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish;

import java.io.IOException;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a JAD or MANIFEST attribute.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

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
		this.xmlElementName = "<attribute>";
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
		String[] targets = StringUtil.splitAndTrim( targetStr, ',' );
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
	
	/**
	 * Loads all attribute-definitions from the specified file.
	 * 
	 * @return an array of variable definitions found in the specified file.
	 */
	public Attribute[] loadAttributes() {
		try {
			HashMap map = FileUtil.readPropertiesFile(this.file, ':');
			Object[] keys = map.keySet().toArray();
			Attribute[] variables = new Attribute[ keys.length ];
			for (int i = 0; i < variables.length; i++) {
				String key = (String) keys[i];
				variables[i] = new Attribute( key, (String) map.get( key ) );
			}
			return variables;
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to load " + this.xmlElementName + "-file [" + this.file.getAbsolutePath() + "]:" + e.toString(), e );
		}
	}

	/**
	 * Defines if this attribute targets the manifest.
	 * 
	 * @param targets true when this attribute targets the manifest.
	 */
	public void setTargetsManifest( boolean targets ) {
		this.targetsManifest = targets;
	}

	/**
	 * Defines if this attribute targets the jad.
	 * 
	 * @param targets true when this attribute targets the jad.
	 */
	public void setTargetsJad( boolean targets ) {
		this.targetsJad = targets;
	}

}
