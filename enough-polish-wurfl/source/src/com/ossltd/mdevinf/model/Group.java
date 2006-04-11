/*
 * Class name : Group
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 19-Oct-2005
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
 * Stores a related set of device capabilities as extracted from the
 * &lt;group&gt; sections of the WURFL XML file.
 * @author Jim McLachlan
 * @version 1.0
 */
public class Group
{
    /** Stores the id of the group (eg. "j2me"). */
    private String groupID;
    
    /**
     * Stores the capabilites within this group.  The keys are the names of
     * the capabilities (eg. "j2me_midp_2_0") and the values correspond to the
     * capability (eg. "true"/"false" or numeric values).
     */  
    private Hashtable<String, Capability> capabilities;
    
    /**
     * Creates a new Group object with the provided ID and an empty
     * <code>Hashtable</code> to store the capabilities.
     * @param groupID the ID (name) of this group.
     */
    public Group(String groupID)
    {
        this.groupID = groupID;
        capabilities = new Hashtable<String, Capability>();
    }
    
    /**
     * Accessor
     * @return the groupID (name of the group).
     */
    public String getGroupID()
    {
        return (groupID);
    }
    
    /**
     * Accessor
     * @return the capabilities <code>Hashtable</code> for this group.
     */
    public Hashtable<String, Capability> getCapabilities()
    {
        return (capabilities);
    }
}
