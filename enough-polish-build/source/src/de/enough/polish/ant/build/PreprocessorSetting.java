/*
 * Created on 11-Jul-2004 at 17:43:41.
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
package de.enough.polish.ant.build;

import java.io.File;

import de.enough.polish.ant.Setting;

/**
 * <p>Is used for defining additional user-defined preprocessors.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        11-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PreprocessorSetting extends Setting {
	
	private String className;
	private File classPath;

	/**
	 * Creates a new empty preprocessor setting.
	 */
	public PreprocessorSetting() {
		// initialisation is done via the setter methods
	}
	
	public void setClass( String className ) {
		this.className = className;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public void setClassPath( File classPath ) {
		this.classPath = classPath;
	}
	
	public File getClassPath() {
		return this.classPath;
	}

}
