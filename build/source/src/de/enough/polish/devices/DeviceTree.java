/*
 * Created on 05-Jan-2006 at 16:50:07.
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

import java.util.ArrayList;

import de.enough.polish.Device;

/**
 * <p>The DeviceTree structures the device database.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        05-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceTree {

	private final DeviceDatabase deviceDatabase;	
	private DeviceTreeItem[] deviceTreeItems;
	private DeviceTreeItem[] rootTreeItems;

	/**
	 * Creates a new DeviceTree.
	 * 
	 * @param deviceDatabase manager of the device db 
	 * @param supportedConfigurations all configurations that should be supported, can be null (in which case all configurations are supported)
	 * @param supportedPlatforms all platforms/profiles that should be supported, can be null (in which case all platforms/profiles are supported)
	 * 
	 */
	public DeviceTree( DeviceDatabase deviceDatabase, Configuration[] supportedConfigurations, Platform[] supportedPlatforms ) {
		super();
		this.deviceDatabase = deviceDatabase;
		rebuild( supportedConfigurations, supportedPlatforms );
	}

	/**
	 * Rebuilds this tree.
	 *  
	 * @param configurations all configurations that should be supported, can be null (in which case all configurations are supported)
	 * @param platforms all platforms/profiles that should be supported, can be null (in which case all platforms/profiles are supported)
	 */
	private void rebuild(Configuration[] configurations, Platform[] platforms) {
		Device[] devices = this.deviceDatabase.getDeviceManager().getDevices(configurations, platforms);
		DeviceTreeItem[] deviceItems = new DeviceTreeItem[ devices.length ];
		ArrayList rootItemsList = new ArrayList();
		DeviceTreeItem virtualTreeItem = new DeviceTreeItem( null, null );
		rootItemsList.add( virtualTreeItem );
		Vendor lastVendor = null;
		DeviceTreeItem lastVendorItem = null;
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			DeviceTreeItem item;
			if (device.isVirtual()) {
				item = new DeviceTreeItem( null, device );
				virtualTreeItem.addChild(item);
			} else {
				if (device.getVendor() != lastVendor) {
					lastVendor = device.getVendor();
					lastVendorItem = new DeviceTreeItem( lastVendor, null );
					rootItemsList.add( lastVendorItem );
				}
				item = new DeviceTreeItem( lastVendor, device );
				lastVendorItem.addChild( item );
			}
			deviceItems[i] = item;
		}
		this.deviceTreeItems = deviceItems;
		this.rootTreeItems = (DeviceTreeItem[]) rootItemsList.toArray( new DeviceTreeItem[ rootItemsList.size() ]);
	}
	
	public DeviceTreeItem[] getRootItems() {
		return this.rootTreeItems;
	}
	
	public Device[] getSelectedDevices() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < this.deviceTreeItems.length; i++) {
			DeviceTreeItem item = this.deviceTreeItems[i];
			if ( !item.isSelected() ) {
				list.add( item.getDevice() );
			}
		}
		Device[] selectedDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		//Arrays.sort( selectedDevices );
		return selectedDevices;
		
	}

}
