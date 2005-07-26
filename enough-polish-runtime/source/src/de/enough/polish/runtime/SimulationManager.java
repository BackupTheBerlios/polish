/*
 * Created on 29-Jan-2005 at 13:50:45.
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
package de.enough.polish.runtime;

import de.enough.polish.Device;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.devices.CapabilityManager;
import de.enough.polish.devices.ConfigurationManager;
import de.enough.polish.devices.DeviceGroupManager;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.devices.PlatformManager;
import de.enough.polish.devices.Vendor;
import de.enough.polish.devices.VendorManager;
import de.enough.polish.exceptions.InvalidComponentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.microedition.midlet.MIDletStateChangeException;

import org.jdom.JDOMException;

/**
 * <p>Controls the simulation - loads the device database and initialises the MIDlets</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        29-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SimulationManager {
	
	private final File databaseDir;
	private final DeviceManager deviceManager;
	private Device[] devices;
	private boolean useFixKeys;
	private int clearKey;
	private int leftSoftKey;
	private int rightSoftKey;
	private int changeInputKey;

	/**
	 * @param configurationSettings
	 * @throws InvalidComponentException
	 * @throws IOException
	 * @throws JDOMException
	 * @throws FileNotFoundException
	 * 
	 */
	public SimulationManager( Map configurationSettings ) 
	throws FileNotFoundException, JDOMException, IOException, InvalidComponentException 
	{
		super();
		String databasePath = (String) configurationSettings.get("polish.home");
		if (databasePath != null) {
			File dir = new File( databasePath);
			if (dir.exists()) {
				this.databaseDir = dir;
			} else {
				this.databaseDir = new File( ".");
			}
		} else {
			this.databaseDir = new File( ".");
		}
		File wtkHome = new File( (String) configurationSettings.get("wtk.home") );
		//String preverifyHome = null;
		CapabilityManager capabilityManager = new CapabilityManager( null, open("capabilities.xml"));
		ConfigurationManager configurationManager = new ConfigurationManager( capabilityManager, open("configurations.xml"));
		PlatformManager platformManager = new PlatformManager( capabilityManager, open("platforms.xml"));
		VendorManager vendorManager = new VendorManager( null, open("vendors.xml"), capabilityManager);
		LibraryManager libraryManager = new LibraryManager(configurationSettings, new File( "import" ), wtkHome, open( "apis.xml" ) );
		DeviceGroupManager groupManager = new DeviceGroupManager( open("groups.xml"), capabilityManager ); 
		this.deviceManager = new DeviceManager( configurationManager, platformManager, vendorManager, groupManager, libraryManager, capabilityManager, open("devices.xml") );
		this.devices = this.deviceManager.getDevices();
	}
	
	/**
	 * Opens a file from the device database
	 * 
	 * @param fileName the name of the file which should be opened
	 * @return an input stream of the specified file
	 * @throws FileNotFoundException when the specified file was not found
	 */
	private InputStream open(String fileName) 
	throws FileNotFoundException 
	{
		File file = new File( this.databaseDir,  fileName );
		if (file.exists()) {
			return new FileInputStream( file );
		} else {
			return getClass().getClassLoader().getResourceAsStream(fileName);
		}
	}
	
	public Simulation loadSimulation( String deviceIdentifier, String midletClass ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, MIDletStateChangeException 
	{
		Device device = this.deviceManager.getDevice(deviceIdentifier);
		if (device == null) {
			throw new IllegalArgumentException("device [" + deviceIdentifier + "] is not known.");
		} 
		Simulation simulation = new Simulation( this, device );
		simulation.loadMIDlet(midletClass);
		if (this.useFixKeys) {
			simulation.setFixKeyCodes(this.clearKey, this.leftSoftKey, this.rightSoftKey, this.changeInputKey);
		}
		//System.out.println("SimulationManager: loaded simulation with canvas height=" + simulation.getCanvasHeight());
		return simulation;
	}

	/**
	 * Sets the device requirements for this simulation.
	 * 
	 * @param requirements the requirements
	 */
	public void setDeviceRequirements(Requirements requirements) {
		this.devices = requirements.filterDevices( this.deviceManager.getDevices() );
	}
	
	public Vendor[] getVendors() {
		return this.deviceManager.getVendors(this.devices);
	}
	
	public Device[] getDevices( Vendor vendor ) {
		return this.deviceManager.getDevices(this.devices, vendor);
	}
	
	public void setFixKeyCodes( int clearKey, int leftSoftKey, int rightSoftKey, int changeInputKey ) {
		this.useFixKeys = true;
		this.clearKey = clearKey;
		this.leftSoftKey = leftSoftKey;
		this.rightSoftKey = rightSoftKey;
		this.changeInputKey = changeInputKey;
	}

}
