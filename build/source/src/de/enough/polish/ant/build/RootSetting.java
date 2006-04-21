/*
 * Created on 12-Apr-2006 at 16:03:13.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;

import java.io.File;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Environment;
import de.enough.polish.ant.Setting;

/**
 * <p>Adds a base directory for the J2ME Polish resource assembling step.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        12-Apr-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RootSetting extends Setting {
	
	

	private String dirDefinition;
	private File dir;

	/**
	 * Creates a new directory setting.
	 */
	public RootSetting() {
		super();
	}
	
	public RootSetting(File dir) {
		this.dir = dir;
	}

	public void setDir( String dirDefinition ) {
		if ("".equals( dirDefinition ) ) {
			throw new BuildException("Invalid <root> element, an empty <root> attribute has been defined. Please check your <root> element(s) in the build.xml script.");
		}
		this.dirDefinition = dirDefinition;
	}
	
	public String getDirDefinition() {
		if (this.dir != null) {
			return this.dir.getAbsolutePath();
		} else {
			return this.dirDefinition;
		}
	}
	
	public File resolveDir( Environment env ) {
		if (this.dir != null) {
			return this.dir;
		} else {
			return env.resolveFile(this.dirDefinition);
		}
	}

}
