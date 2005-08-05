/*
 * Created on 05-Aug-2005 at 14:33:58.
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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Converts SVG files during the build process.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        05-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders, tim@enough.de
 */
public class SvgConverterResourceCopier extends ResourceCopier {

	public SvgConverterResourceCopier() {
		super();
		// TODO enough implement SvgConverterResourceCopier
	}
	
	/**
	 * Determines whether the device supports the SVG API.
	 * 
	 * @param env the environment settings
	 * @return true when the device supports the SVG API
	 */
	protected boolean supportsSvgApi( Environment env ) {
		return env.hasSymbol( "polish.api.svg" );
	}
	
	/**
	 * Retrieves the maximum allowed icon size for the current device.
	 * 
	 * @param env the environment settings
	 * @return the maximum allowed size or null when the target device does not define the "IconSize" capability.
	 */
	protected Dimension getIconSize( Environment env ) {
		String iconSizesStr = env.getVariable("polish.IconSize");
		if (iconSizesStr == null) {
			return null;
		} else {
			String[] iconSizes = StringUtil.splitAndTrim( iconSizesStr, ',' );
			int maxWidth = 0;
			int maxHeight = 0;
			for (int i = 0; i < iconSizes.length; i++) {
				String sizeStr = iconSizes[i];
				String[] dimensions = StringUtil.splitAndTrim(sizeStr, 'x' );
				int width = Integer.parseInt( dimensions[0]);
				int height = Integer.parseInt( dimensions[1]);
				if (width > maxWidth || height > maxHeight ) {
					maxWidth = width;
					maxHeight = height;
				}
			}
			return new Dimension( maxWidth, maxHeight );
		}
	}

	/**
	 * Copies all resources for the target device and the target locale to the final resources directory, SVG files are converted on the fly.
	 * 
	 * @param device the current target device
	 * @param locale the current target locale, can be null
	 * @param resources an array of resources
	 * @param targetDir the target directory
	 * @throws IOException when a resource could not be copied.
	 */
	public void copyResources(Device device, Locale locale, File[] resources,
			File targetDir) 
	throws IOException 
	{
		Environment env = device.getEnvironment();
		boolean supportsSvgApi = supportsSvgApi( env );
		Dimension iconSize = getIconSize( env );
		
		ArrayList leftResourcesList = new ArrayList();
		for (int i = 0; i < resources.length; i++) {
			File file = resources[i];
			if ( file.getName().endsWith(".svg") || file.getName().endsWith(".SVG") ) {
				//TODO Tim: convert SVG
				
			} else {
				leftResourcesList.add( file );
			}
		}
		File[] leftResources = (File[]) leftResourcesList.toArray( new File[ leftResourcesList.size() ] );
		FileUtil.copy( leftResources, targetDir );
	}

}
