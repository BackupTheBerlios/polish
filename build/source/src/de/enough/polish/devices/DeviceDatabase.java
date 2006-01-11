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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;

import de.enough.polish.PolishProject;

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
    private File polishHome;
    private File apisHome;

	/**
	 * Creates a new device database.
	 * 
	 * @param properties configuration settings, needs to contain the wtk.home key
	 * @param polishHome the installation directory of J2ME Polish
	 * @param projectHome the project's directory
	 * @param apisHome the default import folder, can be null (in which case ${polish.home}/import is used)
	 * @param polishProject basic settings
	 * @param inputStreamsByFileName the configured input streams, can be null
	 * @param customFilesByFileName user-defined XLM configuration files, can be null
	 */
	public DeviceDatabase( Map properties, File polishHome, File projectHome, File apisHome, 
			PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) 
	{
		super();
		if (customFilesByFileName == null) {
			customFilesByFileName = new HashMap();
		}
		String wtkHomePath = (String) properties.get( "wtk.home" );
		if (wtkHomePath == null) {
			throw new BuildException("Unable to initialise device database - found no wtk.home property.");
		}
		File wtkHome = new File( wtkHomePath );
		
		try {			
			// load capability-definitions:
			InputStream is = getInputStream( "capabilities.xml", polishHome, inputStreamsByFileName ); 
			this.capabilityManager = new CapabilityManager( properties, is );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read capabilities.xml: " + e.getMessage(), e );
		}
		try {	
			// load libraries:
			InputStream is = getInputStream( "apis.xml", polishHome, inputStreamsByFileName );
			this.libraryManager = new LibraryManager( properties, apisHome, wtkHome, is );
			File file = (File) customFilesByFileName.get("custom-apis.xml");
			if ( file != null ) {
				this.libraryManager.loadCustomLibraries( file );
			}  else {
				// use default libraries:
				file = new File( polishHome, "custom-apis.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
				file = new File( projectHome, "custom-apis.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read apis.xml/custom-apis.xml: " + e.getMessage(), e );
		}
		try {	
			// load configurations:
			InputStream is = getInputStream("configurations.xml", polishHome, inputStreamsByFileName);
			this.configurationManager = new ConfigurationManager( this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-configurations.xml");
			if ( file != null ) {
				this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
			} else {
				// use default libraries:
				file = new File( polishHome, "custom-configurations.xml");
				if (file.exists()) {
					this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
				}
				file = new File( projectHome, "custom-configurations.xml");
				if (file.exists()) {
					this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read configurations.xml/custom-configurations.xml: " + e.getMessage(), e );
		}
		try {	
			// load platforms:
			InputStream is = getInputStream("platforms.xml", polishHome, inputStreamsByFileName);
			this.platformManager = new PlatformManager( this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-platforms.xml");
			if ( file != null ) {
				this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-platforms.xml");
				if (file.exists()) {
					this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
				}
				file = new File( projectHome, "custom-platforms.xml");
				if (file.exists()) {
					this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read platforms.xml/custom-platforms.xml: " + e.getMessage(), e );
		}
		try {	
			// load device groups:
			InputStream is = getInputStream("groups.xml", polishHome, inputStreamsByFileName);
			this.groupManager = new DeviceGroupManager( is, this.capabilityManager );
			File file = (File) customFilesByFileName.get("custom-groups.xml");
			if ( file != null ) {
				this.groupManager.loadCustomGroups( file, this.capabilityManager );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file, this.capabilityManager );
				}
				file = new File( projectHome, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read groups.xml/custom-groups.xml: " + e.getMessage(), e );
		}
		try {				
			// load vendors:
			InputStream is = getInputStream("vendors.xml", polishHome, inputStreamsByFileName);
			this.vendorManager = new VendorManager( polishProject,  is, this.capabilityManager, this.groupManager);
			File file = (File) customFilesByFileName.get("custom-vendors.xml");
			if ( file != null ) {
				this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
			} else {
				// use default vendors:
				file = new File( polishHome, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
				}
				file = new File( projectHome, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read vendors.xml/custom-vendors.xml: " + e.getMessage(), e );
		}
		try {				
			// load devices:
			InputStream is = getInputStream("devices.xml", polishHome, inputStreamsByFileName);
			this.deviceManager = new DeviceManager( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-devices.xml");
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
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read devices.xml/custom-devices.xml: " + e.getMessage(), e );
		}
        this.polishHome = polishHome;
        this.apisHome = apisHome;
	}

	
	
	/**
	 * Gets the input stream for the specified resource.
	 * 
	 * @param fileName the name of the resource
	 * @param polishHome the installation directory of J2ME Polish
	 * @param inputStreamsByFileName the map containing configured input streams, can be null
	 * @return the input stream or null when neither the stream is defined, nor the file can be found in polishHome
	 */
	private InputStream getInputStream(String fileName, File polishHome, Map inputStreamsByFileName) {
		InputStream is = null;
		if (inputStreamsByFileName != null)  {
			is = (InputStream) inputStreamsByFileName.get( fileName );
		}
		if (is == null) {
			File file = new File( polishHome, fileName );
			if ( file.exists() ) {
				try {
					return new FileInputStream( file );
				} catch (FileNotFoundException e) {
					// this should not happen, since we explicitely test this case
					e.printStackTrace();
				}
			}
		}
		return is;
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
	/**
	 * @return Returns the manager of platforms.
	 */
	public PlatformManager getPlatformManager() {
		return this.platformManager;
	}

	/**
	 * @return Returns the configurationManager.
	 */
	public ConfigurationManager getConfigurationManager() {
		return this.configurationManager;
	}

    public File getPolishHome() {
        return this.polishHome;
    }

    public File getApisHome() {
        return this.apisHome;
    }
    
    
    
}
