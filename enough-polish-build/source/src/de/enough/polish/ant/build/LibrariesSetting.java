/*
 * Created on 14-Apr-2005 at 22:11:22.
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
package de.enough.polish.ant.build;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

import de.enough.polish.ant.Setting;

/**
 * <p>A container for several <library> tags.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LibrariesSetting extends Setting {
	
	private int currentId;
	
	private final ArrayList libraries;

	/**
	 * Creates a new libraries setting
	 */
	public LibrariesSetting() {
		super();
		this.libraries = new ArrayList();
	}
	
	public void addConfiguredLibrary( LibrarySetting setting ) {
		if (setting.getDir() == null && setting.getFile() == null ) {
			throw new BuildException("Invalid <library>-element: you need to define either the \"file\" or the \"dir\" attribute of the <library>-element in your build.xml file.");
		}
		setting.setId( this.currentId );
		this.currentId++;
		this.libraries.add( setting );
	}
	
	public LibrarySetting[] getLibraries() {
		LibrarySetting[] libs = new LibrarySetting[ this.libraries.size() ];
		this.libraries.toArray( libs );
		return libs;
	}

	/**
	 * @param setting
	 */
	public void add(LibrariesSetting setting) {
		this.libraries.addAll( setting.libraries );
	}

}
