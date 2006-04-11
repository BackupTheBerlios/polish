/*
 * Class name : SearchDetails
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

import java.util.ArrayList;


/**
 * Stores the information relevant to a search:
 * <ul>
 * <li>The name of the search
 * <li>Whether the criteria are to be 'OR'ed or 'AND'ed together
 * <li>Whether default (non-overridden) values are to be ignored or included
 * <li>A list of search criteria (capability, comparator, value)
 * </ul> 
 * @author Jim McLachlan
 * @version 1.0
 */
public class SearchDetails
{
    /** Stores the name/description fo the search. */
    private String searchName;
    
    /** Stores the list if capabilities and comparisons for this search. */
    private ArrayList<ComparisonDetails> comparisonItems;
    
    /** Stores the and/or type. */
    private boolean orComparison;
    
    /** Stores the "ignore defaults" value. */
    private boolean ignoreDefaults;
    
    /**
     * Creates the search object.
     * @param searchName the name/description of the search
     * @param orComparison the flag indicating whether the search terms are 
     * "OR"'d or "AND"ed together
     * @param ignoreDefaults the flag indicating whether the search should or
     * should not ignore default values
     */
    public SearchDetails(String searchName,
                         boolean orComparison, 
                         boolean ignoreDefaults)
    {
        this.searchName = searchName;
        this.orComparison = orComparison;
        this.ignoreDefaults = ignoreDefaults;
        
        comparisonItems = new ArrayList<ComparisonDetails>();
    }
    
    /**
     * Accessor
     * @return the description of this search
     */
    public String getSearchName()
    {
        return (searchName);
    }
    
    /**
     * Accessor
     * @return the flag indicating whether the search terms are "OR"'d or
     * "AND"ed together
     */
    public boolean isOrComparison()
    {
        return (orComparison);
    }
    
    /**
     * Accessor
     * @return the flag indicating whether the search should or should not
     * ignore default values.
     */
    public boolean isIgnoreDefaults()
    {
        return (ignoreDefaults);
    }
    
    /**
     * Mutator
     * @param detail the comparison detail to be added to this search
     */
    public void addComparisonItem(ComparisonDetails detail)
    {
        comparisonItems.add(detail);
    }

    /**
     * Accessor
     * @return the list of comparison details in this search
     */
    public ArrayList<ComparisonDetails> getComparisonItems()
    {
        return (comparisonItems);
    }
    
    /**
     * Used to check object equality
     * @param o the object to compare
     * @return <code>true</code> if the search names match, otherwise, false
     */
    public boolean equals(Object o)
    {
        boolean result = false;

        result = ((SearchDetails)o).getSearchName().equals(searchName);
        
        return (result);
    }
}
