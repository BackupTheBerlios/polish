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
package de.enough.polish.devices;

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

import de.enough.polish.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.Device;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.ant.requirements.VariableDefinedRequirement;
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
	private HashMap devicesByUserAgent;
	private final VendorManager vendorManager;
	private List devicesXmlList;
	private List currentDevicesXmlList;
	private ConfigurationManager configurationManager;
	private PlatformManager platformManager;
	private DeviceGroupManager groupManager;
	private LibraryManager libraryManager;
	private CapabilityManager capabilityManager;

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param platformManager
	 * @param configurationManager
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param libraryManager the manager for device-specific APIs
	 * @param capabilityManager
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( ConfigurationManager configurationManager, PlatformManager platformManager, VendorManager vendorManager, DeviceGroupManager groupManager, LibraryManager libraryManager, CapabilityManager capabilityManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.vendorManager = vendorManager;

		this.devicesByIdentifier = new HashMap();        
		this.devicesList = new ArrayList();
		loadDevices( configurationManager, platformManager, vendorManager, groupManager, libraryManager, capabilityManager, devicesIS );
		devicesIS.close();
	}
	
	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.devicesByIdentifier = new HashMap();
		this.devicesList = new ArrayList();
		this.vendorManager = vendorManager;
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
	protected void loadDevices(  ConfigurationManager configManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadDevices( null, configManager, platfManager, vendManager, grManager, libManager, capManager, devicesIS);
	}
	
	/**
	 * Loads the device definitions.
	 * @param identifierList a list of identifiers that should be loaded, when null all found devices are loaded
	 * @param vendManager The manager of the device-manufacturers
	 * @param grManager The manager for device-groups.
	 * @param libManager the manager for API libraries
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	protected void loadDevices(  List identifierList, ConfigurationManager configManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.configurationManager = configManager;
		this.platformManager = platfManager;
		this.groupManager = grManager;
		this.libraryManager = libManager;
		this.capabilityManager = capManager;
		
		if (devicesIS == null) {
			throw new BuildException("Unable to load devices.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( devicesIS );
		
		HashMap devicesMap = this.devicesByIdentifier;
		List xmlList = document.getRootElement().getChildren();
		if (identifierList != null) {
			// optimized loading: load only devices of the specified identifiers.
			// This drastically improves loadtime but produces overhead for cases
			// when a desired device contains a parent device that is not in the list.
			// To workaround this problem, the getDevice( String identifier ) method
			// can load devices as well, if required.
			if (this.devicesXmlList == null ) {
				// this contains the devices.xml list when only devices with identifiers should be loaded:
				this.devicesXmlList = xmlList;
			}
			this.currentDevicesXmlList = xmlList;
		}
		loadDevices(identifierList, configManager, platfManager, vendManager, grManager, libManager, capManager, devicesMap, xmlList);
		this.devices = (Device[]) this.devicesList.toArray( new Device[ this.devicesList.size()]);
	}

	/**
	 * Loads the requested devices from the given xmlList
	 * 
	 * @param identifierList the requested identifiers, if null all devices are loaded
	 * @param configuratioManager
	 * @param platfManager
	 * @param vendManager
	 * @param grManager
	 * @param libManager
	 * @param capManager
	 * @param devicesMap
	 * @param xmlList
	 * @throws InvalidComponentException
	 */
	private void loadDevices(List identifierList, ConfigurationManager configuratioManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, HashMap devicesMap, List xmlList) throws InvalidComponentException {
		String lastKnownWorkingDevice = null;
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String identifierStr = definition.getChildTextTrim( "identifier");
			if (identifierStr == null) {
				throw new InvalidComponentException("Unable to initialise device. Every device needs to define its <identifier> element. Check your \"devices.xml\" file. The last known correct definition was " + lastKnownWorkingDevice + ".");
			}
			lastKnownWorkingDevice = identifierStr;
			// one xml definition can contain several device-definitions,
			// e.g. <identifier>Nokia/3650, Nokia/5550</identifier>
			String[] identifiers = StringUtil.splitAndTrim(identifierStr,',');
			for (int i = 0; i < identifiers.length; i++) {
				String identifier = identifiers[i];
				if (identifierList != null) {
					boolean isRequiredIdentifier = identifierList.remove( identifier );
					if (!isRequiredIdentifier) {
						// skip the loading of devices which are not needed. When a device later onwards
						// refers to a parent device, this will be loaded in the getDevice( String identifier ) method.
						continue;
					}
//					System.out.println("found required identifier: " + identifier );
				}
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
				Device device = new Device( configuratioManager, platfManager, definition, identifier, deviceName, vendor, grManager, libManager, this, capManager );
				devicesMap.put( identifier, device );
				this.devicesList.add( device );
				if (identifierList != null && identifierList.size() == 0) {
//					System.out.println("done loading <identifier> devices");
					break;
				}

			}
		}
	}

	/**
	 * Retrieves all found device definitions.
	 * 
	 * @return the device definitions found in the devices.xml file.
	 */
	public Device[] getDevices() {
		if (this.devices == null) {
			this.devices = (Device[]) this.devicesList.toArray( new Device[ this.devicesList.size()]);
		}
		return this.devices;
	}

    public void addDevice(Device device) {
        Device[] newDevices = new Device[this.devices.length+1];
        System.arraycopy(this.devices,0,newDevices,0,this.devices.length);
        newDevices[this.devices.length] = device;
        this.devices = newDevices;
        this.devicesByIdentifier.put(device.getIdentifier(),device);
    }
    
	/**
	 * Retrieves a single device.
	 * 
	 *@param identifier the identifier of the device, eg Nokia/6600
	 *@return the device or null when it is not known.
	 */
	public Device getDevice(String identifier) {
		Device device = (Device) this.devicesByIdentifier.get( identifier );
		// when devices are only loaded for specific identifiers, 
		// J2ME Polish might need to load parent devices later onwards:
		if (device == null && this.devicesXmlList != null) {
			List identifiers = new ArrayList();
			identifiers.add(identifier);
			try {
				loadDevices(identifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, this.devicesByIdentifier, this.devicesXmlList);
				device = (Device) this.devicesByIdentifier.get( identifier );
				if (device == null && this.currentDevicesXmlList != this.devicesXmlList) {
					loadDevices(identifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, this.devicesByIdentifier, this.currentDevicesXmlList );
					device = (Device) this.devicesByIdentifier.get( identifier );
				}
			} catch (InvalidComponentException e) {
				e.printStackTrace();
			}
		}
		return device;
	}
    
    /**
     * This class tries to guess the device identifier by analyzing the fuzzy string
     * and searches for a vendor and a model. The algorithm is rudimentary but a start.
     * It checks if the fuzzy name is a regular identifer. After this the string is split into
     * a vendor and model component. The vendor part is matched against all vendor aliases.
     * @param fuzzyName
     * @return the device or null when it is not known.
     */
    public Device getDeviceByFuzzyName(String fuzzyName) {
        // Try the fuzzy name as normal identifier.
        Device device;
        device = getDevice(fuzzyName);
        if(device != null) {
            return device;
        }
        
        String vendorString;
        String modelString;
        
        String[] components = fuzzyName.split("/");
        if(components.length != 2) {
            return null;
        }
        vendorString = components[0];
        modelString = components[1];
        
        Vendor vendor = this.vendorManager.getVendor(vendorString);
        if(vendor == null) {
            return null;
        }
        device = getDevice(vendor.getIdentifier()+"/"+modelString);
        return device;
    }
	
    /**
     * Searches for a device with a given user agent. If it is not found the
     * user agent is shortend and matched again.
     * @param userAgent
     * @return the device corresponding to the given userAgent or null.
     */
    public Device getDeviceByUserAgent(String userAgent) {
        if (this.devicesByUserAgent == null) {
            initializeUserAgentMapping();
        }
        Object device = null;
//        device = this.devicesByUserAgent.get(userAgent);
        int userAgentLength = userAgent.length();
        String subString;
        for(int i = userAgentLength; i > 1; i--) {
            subString = userAgent.substring(0,i);
            device = this.devicesByUserAgent.get(subString);
            if(device != null) {
                break;
            }
        }
        return (Device)device;
    }
    
    private void initializeUserAgentMapping() {
        this.devicesByUserAgent = new HashMap(10000);
        String CAPABILITY_WAP_USER_AGENT = "wap.userAgent";
        Device[] allDevices = getDevices();

        Requirements requirements = new Requirements();
        requirements.addRequirement(new VariableDefinedRequirement(CAPABILITY_WAP_USER_AGENT));
        Device[] devicesWithUA = requirements.filterDevices(allDevices);
        
        Device cachedDevice;
        Device currentDevice;
        String shortendUserAgentId;
        String currentUserAgentAsString;
        
        for (int deviceIndex = 0; deviceIndex < devicesWithUA.length; deviceIndex++) {
            
            currentDevice = devicesWithUA[deviceIndex];
            currentUserAgentAsString = currentDevice.getCapability(CAPABILITY_WAP_USER_AGENT);
            
            if(currentUserAgentAsString == null) {
                // Should not happen as we filtered the devices list for this capability.
                continue;
            }
            String[] currentUserAgents = StringUtil.splitAndTrim(currentUserAgentAsString,'\1');
            
            for (int userAgentIndex = 0; userAgentIndex < currentUserAgents.length; userAgentIndex++) {
                
                String currentUserAgent = currentUserAgents[userAgentIndex];
                int lengthOfString = currentUserAgent.length();
                
                for (int postfixIndex = 0; postfixIndex < lengthOfString; postfixIndex++) {
                    shortendUserAgentId = currentUserAgent.substring(0,lengthOfString-postfixIndex);
                    cachedDevice = (Device)this.devicesByUserAgent.get(shortendUserAgentId);
                    if(cachedDevice != null) {
                        // We have already a device with this user agent prefix. Abort.
                        if(currentUserAgent.equals(shortendUserAgentId)) {
                            // Perfect matches override everything. This is a problem
                            // if several perfect matches are present.
                            this.devicesByUserAgent.put(currentUserAgent,currentDevice);
                        }
                        break;
                    }
                    this.devicesByUserAgent.put(shortendUserAgentId,currentDevice);
                }
            }
        }
    }

    public Device[] getVirtualDevices() {
		return getVirtualDevices( this.devices );
	}
	
	public Device[] getVirtualDevices( Device[] filteredDevices ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( device.isVirtual() ) {
				list.add( device );
			}
		}
		Device[] virtualDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( virtualDevices );
		return virtualDevices;
	}
	
	public Device[] getRealDevices() {
		return getRealDevices( getDevices() );
	}
	
	public Device[] getRealDevices( Device[] filteredDevices ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( !device.isVirtual() ) {
				list.add( device );
			}
		}
		Device[] realDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( realDevices );
		return realDevices;
	}

	public Device[] getDevices( Configuration[] supportedConfigurations, Platform[] supportedPlatforms ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, null );
	}
	
	public Device[] getDevices( Configuration[] supportedConfigurations, Platform[] supportedPlatforms, Library[] supportedLibraries ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, supportedLibraries );
	}
	
	/**
	 * Retrieves all devices with support for the specified platform.
	 * @param platform the platform that should be supported by the device
	 * @return a list of devices suporting that specific platform, can be empty
	 */
	public Device[] getDevices(Platform platform) {
		return getDevices(null, new Platform[]{platform});
	}
	
	/**
	 * Retrieves all devices with support for the specified configuration.
	 * @param configuration the configuration that should be supported by the device
	 * @return a list of devices suporting that specific configuration, can be empty
	 */
	public Device[] getDevices(Configuration configuration) {
		return getDevices( new Configuration[]{ configuration }, null);
	}
	
	public Device[] getDevices( Device[] filteredDevices, Configuration[] supportedConfigurations, Platform[] supportedPlatforms ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, null );
	}
	
	public Device[] getDevices( Device[] filteredDevices, Configuration[] supportedConfigurations, Platform[] supportedPlatforms, Library[] supportedLibraries ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			boolean addDevice = true;
			if (supportedConfigurations != null) { // at least one configuration needs to be suported
				addDevice = false;
				for (int j = 0; j < supportedConfigurations.length; j++) {
					Configuration configuration = supportedConfigurations[j];
					if (device.supportsConfiguration( configuration )) {
						addDevice = true;
						break;
					}
				}
			}
			if (addDevice && supportedPlatforms != null) { // at least one platform needs to be suported
				addDevice = false;
				for (int j = 0; j < supportedPlatforms.length; j++) {
					Platform platform = supportedPlatforms[j];
					if (device.supportsPlatform( platform )) {
						addDevice = true;
						break;
					}
				}
			}
			if (addDevice && supportedLibraries != null) { // all libraries need to be supported
				for (int j = 0; j < supportedLibraries.length; j++) {
					Library library = supportedLibraries[j];
					if (!device.supportsLibrary( library )) {
						addDevice = false;
						break;
					}
				}
			}
			if (addDevice) {
				list.add( device );
			}
		}
		Device[] platformDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( platformDevices );
		return platformDevices;
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
	 * @param configuratioManager
	 * @param platformManager
	 * @param vendManager
	 * @param deviceGroupManager
	 * @param libManager
	 * @param capabilityManager
	 * @param customDevices
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDevices( ConfigurationManager configuratioManager, PlatformManager platformManager, VendorManager vendManager, DeviceGroupManager deviceGroupManager, LibraryManager libManager, CapabilityManager capabilityManager, File customDevices ) 
	throws JDOMException, InvalidComponentException {
		loadCustomDevices(null, configuratioManager, platformManager, vendManager, deviceGroupManager, libManager, capabilityManager, customDevices);
	}

	/**
	 * @param identifierList
	 * @param configuratioManager
	 * @param platformManager
	 * @param vendManager
	 * @param deviceGroupManager
	 * @param libManager
	 * @param capabilityManager
	 * @param customDevices
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDevices( List identifierList, ConfigurationManager configuratioManager, PlatformManager platformManager, VendorManager vendManager, DeviceGroupManager deviceGroupManager, LibraryManager libManager, CapabilityManager capabilityManager, File customDevices ) 
	throws JDOMException, InvalidComponentException {
		if (customDevices.exists()) {
			try {
				loadDevices( identifierList, configuratioManager, platformManager, vendManager, deviceGroupManager, libManager, capabilityManager, new FileInputStream( customDevices ) );
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


	/**
	 * Clears all stored devices from memory.
	 */
	public void clear()
	{
		this.devicesXmlList = null;
		this.currentDevicesXmlList = null;
		this.devices = null;
		this.devicesList.clear();
		this.devicesByIdentifier.clear();
		if (this.devicesByUserAgent != null)
		{
			this.devicesByUserAgent.clear();
		}
	}


}
