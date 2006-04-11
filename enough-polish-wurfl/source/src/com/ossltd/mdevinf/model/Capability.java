/*
 * Class name : Capability
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 07-Nov-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan  (jim@oss-ltd.com)
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
 * 
 */

package com.ossltd.mdevinf.model;

/**
 * Stores the capability value text and a flag to indicate whether it has
 * been overridden by a device and/or included as part of a patch file.
 * @author Jim McLachlan
 * @version 1.0
 */
public class Capability
{
    /** The value of this capability. */
    private String value;
    
    /** 
     * A flag indicating whether the capability has been overridden (ie. a
     * device has provided a specific value for the capability, overriding the
     * default provided for the root/generic device.
     */
    private boolean overridden;
    
    /**
     * A flag indicating whether this capability was included/overriden by
     * an entry in a patch file.
     */
    private boolean patched;
    
    /**
     * A flag indicating that this capability has been edited within this
     * session.
     */
    private boolean edited;
    
    /**
     * Creates the <code>Capability</code>.
     * @param value the value of the capability
     * @param overridden the flag indicating whether this capability has been
     * overridden.
     * @param patched the flag indicating whether this capability has been
     * updated from a patch file.
     */
    public Capability(String value, boolean overridden, boolean patched)
    {
        this.value = value;
        this.overridden = overridden;
        this.patched = patched;
        this.edited = false;
    }
    
    /**
     * Accessor
     * @return the value of this capability
     */
    public String getValue()
    {
        return (value);
    }
    
    /**
     * Mutator
     * @param value sets the new value for this capability
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    
    /**
     * Accessor
     * @return a flag indicating whether this capability has been overridden.
     */
    public boolean isOverridden()
    {
        return (overridden);
    }
    
    /**
     * Mutator
     * @param overridden sets the overridden status of this capability
     */
    public void setOverridden(boolean overridden)
    {
        this.overridden = overridden;
    }
    
    /**
     * Accessor
     * @return a flag indicating whether this capability was overridden in a
     * patch file.
     */
    public boolean isPatched()
    {
        return (patched);
    }
    
    /**
     * Mutator
     * @param patched sets the patched status of this capability
     */
    public void setPatched(boolean patched)
    {
        this.patched = patched;
    }
    
    /**
     * Accessor
     * @return the flag indicating whether this capability has been edited
     */
    public boolean isEdited()
    {
        return (edited);
    }
    
    /**
     * Mutator
     * @param edited a flag indicating whether this capability has been edited
     */
    public void setEdited(boolean edited)
    {
        this.edited = edited;
    }
}
