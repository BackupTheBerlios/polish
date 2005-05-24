/*
 * Created on 16-Feb-2004 at 19:09:20.
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.devices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages all known vendors.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PlatformManager {
	
	private final HashMap platformsByIdentifier;
	
	/**
	 * Creates a new platform manager.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./platforms.xml".
	 * @throws JDOMException when there are syntax errors in platforms.xml
	 * @throws IOException when platforms.xml could not be read
	 * @throws InvalidComponentException when a platforms definition has errors
	 */
	public PlatformManager( CapabilityManager capabilityManager, InputStream is ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.platformsByIdentifier = new HashMap();
		loadPlatforms( capabilityManager, is );
		is.close();
	}
	
	/**
	 * Loads all known platforms from the given file.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./platforms.xml".
	 * @throws JDOMException when there are syntax errors in platforms.xml
	 * @throws IOException when platforms.xml could not be read
	 * @throws InvalidComponentException when a platforms definition has errors
	 */
	private void loadPlatforms(CapabilityManager capabilityManager, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load platforms.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Platform platform = new Platform( deviceElement, capabilityManager );
			this.platformsByIdentifier.put( platform.getIdentifier(), platform );
		}
	}

	/**
	 * Retrieves the specified platform.
	 * 
	 * @param identifier The identifier of the platform, e.g. Nokia, Siemens, Motorola, etc.
	 * @return The platform or null of that platform has not been defined.
	 */
	public Platform getPlatform( String identifier ) {
		return (Platform) this.platformsByIdentifier.get( identifier );
	}

	/**
	 * Retrieves all known Platform.
	 * 
	 * @return an array with all known Platform
	 */
	public Platform[] getPlatforms() {
		return (Platform[]) this.platformsByIdentifier.values().toArray( new Platform[ this.platformsByIdentifier.size() ] );
	}

	/**
	 * Loads the custom-vendors.xml of the user from the current project.
	 * @param customPlatforms
	 * @param capabilityManager
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomPlatforms(File customPlatforms, CapabilityManager capabilityManager ) 
	throws JDOMException, InvalidComponentException {
		if (customPlatforms.exists()) {
			try {
				loadPlatforms( capabilityManager, new FileInputStream( customPlatforms ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-platforms.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-platforms.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "platforms.xml", "custom-platforms.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
	}
}
