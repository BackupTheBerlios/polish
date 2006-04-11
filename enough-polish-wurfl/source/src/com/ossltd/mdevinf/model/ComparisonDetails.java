/*
 * Class name : ComparisonDetails
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 14-Dec-2005
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
 * Stores the specifics for an individual capability comparison in a search.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ComparisonDetails
{
    /** The name of the capability being checked. */
    private String capability;
    
    /** The comparison type being used in the test. */
    private String comparison;
    
    /** The value being compared in the test. */
    private String value;
    
    public ComparisonDetails(String capability,
                             String comparison,
                             String value)
    {
        this.capability = capability;
        this.comparison = comparison;
        this.value = value;
    }

    /**
     * Accessor
     * @return the capability name
     */
    public String getCapability()
    {
        return (capability);
    }
    
    /**
     * Accessor
     * @return the comparison type
     */
    public String getComparison()
    {
        return (comparison);
    }
    
    /**
     * Accessor
     * @return the value for this comparison
     */
    public String getValue()
    {
        return (value);
    }
}
