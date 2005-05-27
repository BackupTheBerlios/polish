/*
 * Created on 23-May-2005 at 15:55:25.
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
package de.enough.polish.devices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.jdom.JDOMException;

import de.enough.polish.PolishProject;
import de.enough.polish.exceptions.InvalidComponentException;

/**
 * <p>Manages the complete device database.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceDatabase {

	private LibraryManager libraryManager;
	private DeviceManager deviceManager;
	private CapabilityManager capabilityManager;
	private DeviceGroupManager groupManager;
	private VendorManager vendorManager;
	private ConfigurationManager configurationManager;
	private PlatformManager platformManager;

	/**
	 * Creates a new device database.
	 * @param properties
	 * @param polishHome
	 * @param projectHome
	 * @param apisHome
	 * @param polishProject
	 * @param inputStreamsByFileName
	 * @param customFilesByFileName
	 */
	public DeviceDatabase( Map properties, File polishHome, File projectHome, File apisHome, 
			PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) 
	{
		super();
		try {
			// load capability-definitions:
			InputStream is = (InputStream) inputStreamsByFileName.get("capabilities.xml");
			this.capabilityManager = new CapabilityManager( properties, is );
			
			// load libraries:
			is = (InputStream) inputStreamsByFileName.get("apis.xml");
			String wtkHomePath = (String) properties.get( "wtk.home" );
			if (wtkHomePath == null) {
				throw new BuildException("Unable to initialise device database - found no wtk.home property.");
			}
			File wtkHome = new File( wtkHomePath );
			this.libraryManager = new LibraryManager( properties, apisHome, wtkHome, is );
			File file = (File) customFilesByFileName.get("custom-libraries.xml");
			if ( file != null ) {
				this.libraryManager.loadCustomLibraries( file );
			} else {
				// use default libraries:
				file = new File( polishHome, "custom-libraries.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
				file = new File( projectHome, "custom-libraries.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
			}
			
			// load configurations:
			is = (InputStream) inputStreamsByFileName.get("configurations.xml");
			this.configurationManager = new ConfigurationManager( this.capabilityManager, is );
			/*
			file = (File) customFilesByFileName.get("custom-configurations.xml");
			if ( file != null ) {
				this.configurationManager.loadCustomConfigurations( file );
			} else {
				// use default libraries:
				file = new File( polishHome, "custom-libraries.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
				file = new File( projectHome, "custom-libraries.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
			}
			*/
			// load platforms:
			is = (InputStream) inputStreamsByFileName.get("platforms.xml");
			this.platformManager = new PlatformManager( this.capabilityManager, is );
			
			
			// load vendors:
			is = (InputStream) inputStreamsByFileName.get("vendors.xml");
			this.vendorManager = new VendorManager( polishProject,  is, this.capabilityManager);
			file = (File) customFilesByFileName.get("custom-vendors.xml");
			if ( file != null ) {
				this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager );
				}
				file = new File( projectHome, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager );
				}
			}
			
			// load device groups:
			is = (InputStream) inputStreamsByFileName.get("groups.xml");
			this.groupManager = new DeviceGroupManager( is );
			file = (File) customFilesByFileName.get("custom-groups.xml");
			if ( file != null ) {
				this.groupManager.loadCustomGroups( file );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file );
				}
				file = new File( projectHome, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file );
				}
			}
			
			// load devices:
			is = (InputStream) inputStreamsByFileName.get("devices.xml");
			this.deviceManager = new DeviceManager( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, is );
			file = (File) customFilesByFileName.get("custom-devices.xml");
			if ( file != null ) {
				this.deviceManager.loadCustomDevices( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-devices.xml");
				if (file.exists()) {
					this.deviceManager.loadCustomDevices( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
				}
				file = new File( projectHome, "custom-devices.xml");
				if (file.exists()) {
					this.deviceManager.loadCustomDevices( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
				}
			}
			
		} catch (JDOMException e) {
			throw new BuildException("unable to create device database: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create device database: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create device database: " + e.getMessage(), e );
		}
	}

	
	
	/**
	 * @return Returns the capabilityManager.
	 */
	public CapabilityManager getCapabilityManager() {
		return this.capabilityManager;
	}
	/**
	 * @return Returns the deviceManager.
	 */
	public DeviceManager getDeviceManager() {
		return this.deviceManager;
	}
	/**
	 * @return Returns the groupManager.
	 */
	public DeviceGroupManager getGroupManager() {
		return this.groupManager;
	}
	/**
	 * @return Returns the libraryManager.
	 */
	public LibraryManager getLibraryManager() {
		return this.libraryManager;
	}
	/**
	 * @return Returns the vendorManager.
	 */
	public VendorManager getVendorManager() {
		return this.vendorManager;
	}
}
