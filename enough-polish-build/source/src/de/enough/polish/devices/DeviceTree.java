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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.jdom.JDOMException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.PolishTask;

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
	public void rebuild(Configuration[] configurations, Platform[] platforms) {
		Device[] devices = this.deviceDatabase.getDeviceManager().getDevices(configurations, platforms);
		DeviceTreeItem[] deviceItems = new DeviceTreeItem[ devices.length ];
		ArrayList rootItemsList = new ArrayList();
        DeviceTreeItem virtualTreeItem = null;
		Vendor lastVendor = null;
		DeviceTreeItem lastVendorItem = null;
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			DeviceTreeItem item;
			if (device.isVirtual()) {
                if(virtualTreeItem == null) {
                    virtualTreeItem = new DeviceTreeItem( null, null, null );
                    rootItemsList.add( virtualTreeItem );
                }
				item = new DeviceTreeItem( virtualTreeItem, null, device );
				virtualTreeItem.addChild(item);
			} else {
				if (device.getVendor() != lastVendor) {
					lastVendor = device.getVendor();
					lastVendorItem = new DeviceTreeItem( null, lastVendor, null );
					rootItemsList.add( lastVendorItem );
				}
				item = new DeviceTreeItem( lastVendorItem, lastVendor, device );
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
			if ( item.isSelected() ) {
				list.add( item.getDevice() );
			}
		}
		Device[] selectedDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		//Arrays.sort( selectedDevices );
		return selectedDevices;
		
	}

    public File[] getClasspathForSelectedDevices() {
        Device[] selectedDevices = getSelectedDevices();
        Environment env = null;
        try {
            env  = new Environment(new ExtensionManager(new Project()),new Project(),new PolishTask());
        } catch (JDOMException exception) {
            throw new RuntimeException("Could not create an Environment."+exception);
        } catch (IOException exception) {
            throw new RuntimeException("Could not create an Environment."+exception);
        }
        env.set("polish.home", this.deviceDatabase.getPolishHome());
        env.set("polish.apidir", this.deviceDatabase.getApisHome());
        
        for (int i = 0; i < selectedDevices.length; i++) {
            Device selectedDevice = selectedDevices[i];
            selectedDevice.setEnvironment(env);
        }
        
        Set normalClasspathSet = new HashSet();
        Set bootClasspathSet = new HashSet();
        for (int i = 0; i < selectedDevices.length; i++) {
            Device device = selectedDevices[i];
            String[] normalClasspath = device.getClassPaths();
            String[] bootClasspath = device.getBootClassPaths();
            for (int j = 0; j < normalClasspath.length; j++) {
                normalClasspathSet.add(normalClasspath[j]);
            }
            for (int j = 0; j < bootClasspath.length; j++) {
                String bootClasspathEntry = bootClasspath[j];
                if(bootClasspathEntry != null) {
                    bootClasspathSet.add(bootClasspath[j]);
                }
            }
        }
        
        String[] normalClasspathArray = (String[]) normalClasspathSet.toArray(new String[normalClasspathSet.size()]);
        String[] bootClasspathArray = (String[]) bootClasspathSet.toArray(new String[bootClasspathSet.size()]);
        
        Arrays.sort(normalClasspathArray);
        Arrays.sort(bootClasspathArray);
        
        String[] fullClasspathArray = new String[normalClasspathArray.length+bootClasspathArray.length];
        
        System.arraycopy(bootClasspathArray,0,fullClasspathArray,0,bootClasspathArray.length);
        System.arraycopy(normalClasspathArray,0,fullClasspathArray,bootClasspathArray.length,normalClasspathArray.length);
        
        String [] filteredClasspathArray = filterPaths(fullClasspathArray);

        File[] files = new File[filteredClasspathArray.length];
        for (int i = 0; i < filteredClasspathArray.length; i++) {
            files[i] = new File(filteredClasspathArray[i]);
        }
        return files;
    }

    /**
     * Removes duplicate entires like MIDP-1.1, MIDP-2.0 and keeps the highest
     * version number (lexically) on the path.
     * @param paths Must be sorted and non null.
     * @return filtered classpath entries.
     */
    protected String[] filterPaths(String[] paths) {
        List resultList = new LinkedList();
        if(paths.length == 0) {
            return new String[0];
        }
        if(paths.length == 1) {
            return new String[] {paths[0]};
        }
        
        String oldPath = paths[0];
        String oldIdentifier = extractIdentifier(oldPath);
        
        for (int i = 1; i < paths.length; i++) {
            String path = paths[i];
            String identifier = extractIdentifier(path);
            
            if( ! identifier.equals(oldIdentifier)) {
                resultList.add(oldPath);
                oldIdentifier = identifier;
            }
            oldPath = path;
        }
        // The last of the platforms is always the highest. But the loop above does not catch this.
        resultList.add(paths[paths.length-1]);
        return (String[]) resultList.toArray(new String[resultList.size()]);
    }

    /**
     * @param path
     */
    private String extractIdentifier(String path) {
        String delimiter;
        String jarName = path.substring(path.lastIndexOf(File.separator),path.length()-1);
        if(jarName.indexOf("-") != -1) {
            delimiter = "-";
        }
        else {
            delimiter = ".";
        }
        
        return jarName.substring(0,jarName.indexOf(delimiter));
    }

}
