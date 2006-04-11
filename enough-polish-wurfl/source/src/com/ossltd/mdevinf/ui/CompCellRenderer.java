/*
 * Class name : CompCellRenderer
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 15-Dec-2005
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

package com.ossltd.mdevinf.ui;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

import com.ossltd.mdevinf.model.Capability;

/**
 * Manages the display of the capability information in the comparison table
 * cells.
 * @author Jim McLachlan
 * @version 1.0
 */
public class CompCellRenderer extends DefaultTableCellRenderer
{
    /**
     * Constant defining the colour to use when indicating a capability is
     * overridden.
     */
    private static final Color OVERRIDDEN_GREEN = new Color(0x00, 0xcc, 0x00);
    
    /**
     * Default constructor, simply calls the superclass constructor.
     */
    public CompCellRenderer()
    {
        super();
    }
    
    /**
     * Mutator
     * @param value the capability object used to identify the background
     * colour and text for the cell
     */
    public void setValue(Object value)
    {
        Capability capability = (Capability)value;

        if (capability.isPatched())
        {
            setForeground(OVERRIDDEN_GREEN);
        }
        else if (capability.isOverridden())
        {
            setForeground(Color.black);
        }
        else
        {
            setForeground(Color.lightGray);
        }
        
        setText(capability.getValue());
    }
}
