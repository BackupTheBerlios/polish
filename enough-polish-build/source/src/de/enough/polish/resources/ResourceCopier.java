/*
 * Created on 04-Apr-2005 at 13:13:43.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.Extension;
import de.enough.polish.ant.build.ResourceCopierSetting;

/**
 * <p>Is responsible for copying all resources for a specific target device and locale to a directory.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ResourceCopier extends Extension {

	/**
	 * Creates a new copier.
	 */
	public ResourceCopier() {
		super();
	}
	
	/**
	 * Copies all resources for the target device and the target locale to the final resources directory.
	 * 
	 * @param device the current target device
	 * @param locale the current target locale, can be null
	 * @param resources an array of resources
	 * @param targetDir the target directory
	 * @throws IOException when a resource could not be copied.
	 */
	public abstract void copyResources( Device device, Locale locale, File[] resources, File targetDir )
	throws IOException;
	
	public static ResourceCopier getInstance( ResourceCopierSetting copierSetting, Project antProject ) {
		if (copierSetting == null) {
			return new DefaultResourceCopier();
		}
		try {
			Extension extension = Extension.getInstance( copierSetting, antProject ); 
			return (ResourceCopier) extension;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to initialize resource copier [" + copierSetting.getClassName() + "]: " + e.toString() );
		}
	}

}
