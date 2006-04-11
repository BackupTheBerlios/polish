/*
 * Class name : ValidationResult
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 11-Nov-2005
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
 * Stores the name and result descriptions of a WURFL validation test.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ValidationResult
{
    /** The name of the test. */
    private String testName;
    
    /** The result of the test. */
    private String result;
    
    /**
     * Creates a new ValidationResult with the provided test name and result
     * @param testName name of the test
     */
    public ValidationResult(String testName)
    {
        this.testName = testName;
        result = "OK";
    }
    
    /**
     * Accessor
     * @return the name of this test
     */
    public String getTestName()
    {
        return (testName);
    }
    
    /**
     * Mutator
     * @param result the result text for this test
     */
    public void setResult(String result)
    {
        this.result = result;
    }
    
    /**
     * Accessor
     * @return the result stored for this test
     */
    public String getResult()
    {
        return (result);
    }
}
