/*
 * Created on 28-Jan-2004 at 23:28:45.
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
package de.enough.polish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
 * <p>Manages all known J2ME devices.</p>
 * <p>The devices are defined in the devices.xml file</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        28-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceManager {

	private Device[] devices;
	private final ArrayList devicesList;
	private final HashMap devicesByIdentifier;
	private final VendorManager vendorManager;
	private final DeviceGroupManager groupManager;
	private final LibraryManager libraryManager;

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param libraryManager the manager for device-specific APIs
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager, DeviceGroupManager groupManager, LibraryManager libraryManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.devicesByIdentifier = new HashMap();
		this.devicesList = new ArrayList();
		this.groupManager = groupManager;
		this.libraryManager = libraryManager;
		this.vendorManager = vendorManager;
		loadDevices( vendorManager, groupManager, libraryManager, devicesIS );
		devicesIS.close();
	}
	
	/**
	 * Loads the device definitions.
	 * 
	 * @param vendManager The manager of the device-manufacturers
	 * @param grManager The manager for device-groups.
	 * @param libManager the manager for API libraries
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	private void loadDevices( VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (devicesIS == null) {
			throw new BuildException("Unable to load devices.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( devicesIS );
		
		HashMap devicesMap = this.devicesByIdentifier;
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String identifierStr = definition.getChildTextTrim( "identifier");
			if (identifierStr == null) {
				throw new InvalidComponentException("Unable to initialise device. Every device needs to define either its [identifier] or its [name] and [vendor]. Check your [devices.xml].");
			}
			// one xml definition can contain several device-definitions,
			// e.g. <identifier>Nokia/3650, Nokia/5550</identifier>
			String[] identifiers = StringUtil.splitAndTrim(identifierStr,',');
			for (int i = 0; i < identifiers.length; i++) {
				String identifier = identifiers[i];
				if (devicesMap.get( identifier ) != null) {
					throw new InvalidComponentException("The device [" + identifier + "] has been defined twice in [devices.xml]. Please remove one of those definitions.");
				}
				String[] chunks = StringUtil.split( identifier, '/');
				if (chunks.length != 2) {
					throw new InvalidComponentException("The device [" + identifier + "] has an invalid [identifier] - every identifier needs to consists of the vendor and the name, e.g. \"Nokia/6600\". Please check you [devices.xml].");
				}
				String vendorName = chunks[0];
				String deviceName = chunks[1];
				Vendor vendor = vendManager.getVendor( vendorName );
				if (vendor == null) {
					throw new InvalidComponentException("Invalid device-specification in [devices.xml]: Please specify the vendor [" + vendorName + "] in the file [vendors.xml].");
				}
				Device device = new Device( definition, identifier, deviceName, vendor, grManager, libManager, this );
				devicesMap.put( identifier, device );
				this.devicesList.add( device );
			}
		}
		this.devices = (Device[]) this.devicesList.toArray( new Device[ this.devicesList.size()]);
	}

	/**
	 * Retrieves all found device definitions.
	 * 
	 * @return the device definitions found in the devices.xml file.
	 */
	public Device[] getDevices() {
		return this.devices;
	}

	/**
	 * Retrieves a single device.
	 * 
	 *@param identifier the identifier of the device, eg Nokia/6600
	 *@return the device or null when it is not known. 
	 */
	public Device getDevice(String identifier) {
		return (Device) this.devicesByIdentifier.get( identifier );
	}
	
	/**
	 * Retrieves all known vendors.
	 * 
	 * @return an array with all known vendors
	 */
	public Vendor[] getVendors() {
		return this.vendorManager.getVendors();		
	}

	/**
	 * Retrieves all vendors that used by the given devices.
	 * 
	 * @param filteredDevices the devices that are searched for vendors
	 * @return an array with all vendors of the given devices
	 */
	public Vendor[] getVendors( Device[] filteredDevices ) {
		HashMap list = new HashMap();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			Vendor vendor = (Vendor) device.parent;
			list.put( vendor.identifier, vendor );
		}
		Vendor[] vendors =  (Vendor[]) list.values().toArray( new Vendor[ list.size() ] );
		Arrays.sort( vendors );
		return vendors;
	}
	
	/**
	 * Gets all devices of the specified vendor.
	 * 
	 * @param vendor the vendor
	 * @return an array of device-definitions of that vendor.
	 */
	public Device[] getDevices( Vendor vendor ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < this.devices.length; i++) {
			Device device = this.devices[i];
			if ( device.parent == vendor ) {
				list.add( device );
			}
		}
		Device[] vendorDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( vendorDevices );
		return vendorDevices;
	}
	
	/**
	 * Gets all devices of the specified vendor.
	 * 
	 * @param filteredDevices the devices that are searched for vendors
	 * @param vendor the vendor
	 * @return an array of device-definitions of that vendor.
	 */
	public Device[] getDevices( Device[] filteredDevices, Vendor vendor ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( device.parent == vendor ) {
				list.add( device );
			}
		}
		Device[] vendorDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( vendorDevices );
		return vendorDevices;
	}

	/**
	 * @param vendManager
	 * @param deviceGroupManager
	 * @param libManager
	 * @param customDevices
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDevices(VendorManager vendManager, DeviceGroupManager deviceGroupManager, LibraryManager libManager, File customDevices ) 
	throws JDOMException, InvalidComponentException {
		if (customDevices.exists()) {
			try {
				loadDevices( vendManager, deviceGroupManager, libManager, new FileInputStream( customDevices ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-devices.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-devices.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "devices.xml", "custom-devices.xml" );
				throw new InvalidComponentException( message, e );
				
			}
		}
	}


}
