/*
 * Class name : Person
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
 * This class stores information about a person involved with the WURFL project.
 * @author Jim McLachlan
 * @version 1.0
 */
public class Person
{
    /** The person's name. */
    private String name;
    
    /** The person's e-mail address. */
    private String email;
    
    /** The person's home page. */
    private String homepage;
    
    /**
     * Creates a <code>Person</code> object with the provided data.
     * @param name The person's name
     * @param email The person's e-mail address
     * @param homepage The person's home page
     */
    public Person(String name, String email, String homepage)
    {
        this.name = name;
        this.email = email;
        this.homepage = homepage;
    }
    
    /**
     * Accessor
     * @return the person's name
     */
    public String getName()
    {
        return (name);
    }
    
    /**
     * Accessor
     * @return the person's e-mail
     */
    public String getEmail()
    {
        return (email);
    }
    
    /**
     * Accessor
     * @return the person's homepage
     */
    public String getHomepage()
    {
        return (homepage);
    }
}
