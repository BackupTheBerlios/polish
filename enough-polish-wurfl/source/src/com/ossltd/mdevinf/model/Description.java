/*
 * Class name : Description
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 22-Oct-2005
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
 * Stores the type (integer, true/false, string) and description of each
 * capability as defined at <a href="http://wurfl.sourceforge.net/help_doc.php">
 * http://wurfl.sourceforge.net/help_doc.php</a>.
 * @author Jim McLachlan
 * @version 1.0
 */
public class Description
{
    /** The capability type, one of "integer", "true/false" or "string". */
    private String type;
    
    /** The capability description. */
    private String description;

    /**
     * Creates a <code>Description</code> object with the provided information.
     * @param type the capability type
     * @param description the capability description
     */
    public Description(String type, String description)
    {
        this.type = type;
        this.description = description;
    }
    
    /**
     * Accessor
     * @return the capability type
     */
    public String getType()
    {
        return (type);
    }
    
    /**
     * Accessor
     * @return the capability description
     */
    public String getDescription()
    {
        return (description);
    }
    
    /**
     * Accessor
     * @return the description formatted in HTML for display in a tooltip label
     */
    public String getHTMLFormattedDescription()
    {
        StringBuffer sb = new StringBuffer("<html>");
        sb.append("<p>");
        if (description != null)
        {
            int i = 0;
            while (i < description.length())
            {
                sb.append(description.charAt(i));
                i++;
                
                if ((i % 41) == 40)
                {
                    int nextSpace = description.indexOf(' ', i);
                    if (nextSpace > -1)
                    {
                        while (i < nextSpace)
                        {
                            sb.append(description.charAt(i));
                            i++;
                        }
                    }
                    else
                    {
                        sb.append(description.substring(i));
                        i = description.length();
                    }
                    sb.append("<br>");
                }
            }
        }
        else
        {
            sb.append("<i>Sorry, no description available.</i>");
        }
        sb.append("</p>");
        sb.append("</html>");
        
        return (sb.toString());
    }
}
