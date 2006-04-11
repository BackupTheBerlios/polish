/*
 * Class name : MatchedDevice
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 27-Oct-2005
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

/**
 * This class simply stores the make/model combination of strings to allow
 * them to be displayed as a single string, but also to allow them to be
 * used separately.
 * 
 * @author Jim McLachlan
 * @version 1.0
 */
public class MatchedDevice implements Comparable<MatchedDevice>
{
    /** The make (brand) name. */
    private String make;
    
    /** The model name. */
    private String model;
    
    /**
     * Stores the provided data
     * @param make the make (brand) of the device
     * @param model the model of the device
     */
    public MatchedDevice(String make, String model)
    {
        this.make = make;
        this.model = model;
    }
    
    /**
     * Accessor
     * @return the make (brand) of the device
     */
    public String getMake()
    {
        return (make);
    }
    
    /**
     * Accessor
     * @return the model of the device.
     */
    public String getModel()
    {
        return (model);
    }
    
    /**
     * Provides the <code>String</code> representation of this object.
     * @return <code>make &lt;space&gt; model</code>
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer(make);
        result.append(" ");
        result.append(model);
        
        return (result.toString());
    }

    /**
     * Required for <code>Collections.sort()</code>
     * @param o the object being compared
     * @return this object compared to the provided object.
     */
    public int compareTo(MatchedDevice o)
    {
        return (toString().compareTo(o.toString()));
    }
}
