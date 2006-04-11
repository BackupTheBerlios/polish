/*
 * Class name : Device
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 20-Oct-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan (jim@oss-ltd.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.ossltd.mdevinf.model;

import java.util.Hashtable;

/**
 * Stores the device name, fallback type and related Groups as extracted from
 * the &lt;device&gt; sections of the WURFL XML file.
 * @author Jim McLachlan
 * @version 1.0
 */
public class Device
{
    /** The ID (name) of the device (eg. "mot_mib22_generic"). */
    private String deviceID;
    
    /**
     * The ID of the device that contains any missing group/capability details
     * if they are not available in this device.
     */
    private String fallbackID;
    
    /** The UAProf string corresponding to that provided on a device request. */
    private String userAgent;
    
    /**
     * A flag indicating whether the device is an actual (real) device or is
     * a placeholder for further or missing information.
     */
    private boolean actualDevice;
    
    /** The collection of <code>Group</code> objects relating to this device. */
    private Hashtable<String, Group> groups;
    
    /** 
     * A flag to indicate whether this device was updated by or created from
     * a patch file
     */
    private boolean patchedDevice;
    
    /**
     * Creates a new <code>Device</code> object from the provided information
     * and with an empty <code>Hashtable</code> to store the group data.
     * @param deviceID the device ID (name)
     * @param fallbackID the fallback device ID (name)
     * @param userAgent the user agent for this device
     * @param actualDevice the flag indicating whether it is a real, physical
     * device
     * @param patchedDevice the flag indicating whether this device was
     * updated or created from a patch file
     */
    public Device(String deviceID,
                  String fallbackID,
                  String userAgent,
                  boolean actualDevice,
                  boolean patchedDevice)
    {
        this.deviceID = deviceID;
        this.fallbackID = fallbackID;
        this.userAgent = userAgent;
        this.actualDevice = actualDevice;
        this.patchedDevice = patchedDevice;
        groups = new Hashtable<String, Group>();
    }
    
    /**
     * Adds the provided group
     * @param group the <code>Group</code> object to be added to the device
     * details.
     */
    public void addGroup(Group group)
    {
        groups.put(group.getGroupID(), group);
    }
    
    /**
     * Accessor
     * @return the ID (name) of this device.
     */
    public String getDeviceID()
    {
        return (deviceID);
    }
    
    /**
     * Accessor
     * @return the fallback ID (name) for this device.
     */
    public String getFallbackID()
    {
        return (fallbackID);
    }
    
    /**
     * Accessor
     * @return the user agent string for this device.
     */
    public String getUserAgent()
    {
        return (userAgent);
    }
    
    /**
     * Accessor
     * @return the flag indicating whether this is an real, physical device or
     * just a placeholder for capability information.
     */
    public boolean isActualDevice()
    {
        return (actualDevice);
    }
    
    /**
     * Accessor
     * @return the flag indicating whether this device was created or updated
     * by a patch file.
     */
    public boolean isPatchedDevice()
    {
        return (patchedDevice);
    }
    
    /**
     * Mutator
     * @param patchedDevice to indicate that this device has been updated 
     * with a patch.
     */
    public void setPatchedDevice(boolean patchedDevice)
    {
        this.patchedDevice = patchedDevice;
    }
    
    /**
     * Accessor
     * @return The <code>Hashtable</code> containing the groups related to this
     * device.
     */
    public Hashtable<String, Group> getGroups()
    {
        return (groups);
    }
    
    /**
     * Accessor
     * @param groupID the group that is of interest
     * @return the capabilities for a specific group within this device. 
     */
    public Hashtable<String, Capability> getCapabilitiesForGroup(String groupID)
    {
        Hashtable<String, Capability> result = null;
        
        Group group = groups.get(groupID);
        if (group != null)
        {
            result = group.getCapabilities();
        }
        
        return (result);
    }
    
    /**
     * Provides the make and model of this device for display in the
     * Relationships tree and the associated information dialogs.
     * @return the brand_name and model_name of the device, separated by a
     * space.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        Hashtable<String, Capability> caps =
                                            new Hashtable<String, Capability>();
        caps = WURFLInfo.getInstance().getAllCapabilitiesFor(this, caps);
        if (caps != null)
        {
            result.append(caps.get("brand_name").getValue());
            result.append(" ");
            result.append(caps.get("model_name").getValue());
        }
        
        return (result.toString());
    }
    
    /**
     * Checks a device for equality based on the device ID and user agent.
     * The device ID alone cannot be used because of duplicates within the
     * WURFL file.
     * @param o the device to be compared to this one
     * @return <code>true</code> if both the device ID and userAgent strings
     * match, otherwise <code>false</code>
     */
    public boolean equals(Object o)
    {
        Device obj = (Device)o;
        
        return ((obj.deviceID.equals(deviceID)) &&
                (obj.userAgent.equals(userAgent)));
    }
}
