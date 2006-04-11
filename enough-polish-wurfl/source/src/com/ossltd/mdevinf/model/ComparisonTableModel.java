/*
 * Class name : ComparisonTableModel
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

package com.ossltd.mdevinf.model;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

import com.ossltd.mdevinf.ConfigurationManager;

/**
 * This class handles the data for the comparison table.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ComparisonTableModel extends AbstractTableModel
{
    /** Stores the names of the capabilities to compare. */
    private ArrayList<String> capabilities;
    
    /** Stores the capabilities of the devices being compared. */
    private ArrayList<Hashtable<String, Capability>> deviceData;
    
    /** Stores the device IDs of the compared devices. */
    private ArrayList<String> devices;
    
    /** Creates an instance of the model and initialises the model data. */
    public ComparisonTableModel()
    {
        super();
        clear();
    }
    
    /**
     * Removes all user selected capabilities and devices from the model and
     * re-initialises the model to containt the brand_name and model_name
     * capabilities.
     */
    public void clear()
    {
        capabilities = new ArrayList<String>();
        capabilities.add("brand_name");
        capabilities.add("model_name");
        
        deviceData = new ArrayList<Hashtable<String, Capability>>();
        
        devices = new ArrayList<String>();
        
        fireTableDataChanged();
    }
    
    /**
     * Accessor
     * @return the number of rows in the model
     */
    public int getRowCount()
    {
        int result = 0;
        
        if (capabilities != null)
        {
            result = capabilities.size();
        }
        
        return (result);
    }

    /**
     * Accessor
     * @return the number of columns in the model (number of devices + 1 for
     * the row headings)
     */
    public int getColumnCount()
    {
        int result = 1;
        
        if (deviceData != null)
        {
            result = deviceData.size() + 1;
        }
        
        return (result);
    }
    
    /**
     * Accessor
     * @param rowIndex the row position of the required object
     * @param colIndex the column position of the required object
     * @return a reference to the object stored at the specified position
     */
    public Object getValueAt(int rowIndex, int colIndex)
    {
        Object result = null;
        
        if ((colIndex >= 0) && (rowIndex >= 0) && 
            (colIndex <= getColumnCount()) && (rowIndex < getRowCount()))
        {
            if (colIndex == 0)
            {
                result = capabilities.get(rowIndex);
                if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
                {
                    Hashtable<String, String> nameMap =
                      ConfigurationManager.getInstance().getCapabilityNameMap();
                    if (nameMap.containsKey(result))
                    {
                        result = nameMap.get(result);
                    }
                }
            }
            else
            {
                result = deviceData.get(colIndex - 1).
                                                get(capabilities.get(rowIndex));
            }
        }

        return (result);
    }
    
    /**
     * Accessor
     * @param row the row displaying the capability
     * @return the actual WURFL name of the capability on the specified row
     */
    public String getCapabilityAt(int row)
    {
        return (capabilities.get(row));
    }
    
    /**
     * Don't allow any cells to be edited.
     * @param rowIndex the row position of the required object
     * @param colIndex the column position of the required object
     * @return <code>false</code>
     */
    public boolean isCellEditable(int rowIndex, int colIndex)
    {
        return (false);
    }
    
    /**
     * Adds a new row (capability) or column (device).
     * @param object the object to add.  This should be a <code>String</code>
     * for a new capability row or a <code>Device</code> for a new column
     * @param rowIndex the row position for the new object
     * @param colIndex the column position for the new object
     */
    public void setValueAt(Object object, int rowIndex, int colIndex)
    {
        if (colIndex == 0)
        {
            if (rowIndex < getRowCount())
            {
                capabilities.set(rowIndex, (String)object);
            }
            else
            {
                capabilities.add((String)object);
            }
        }
        else if (object instanceof Device)
        {
            Device dev = (Device)object;
            if (colIndex < getColumnCount())
            {
                deviceData.set(colIndex,
                               WURFLInfo.getInstance().
                               getAllCapabilitiesFor(dev,
                                                     deviceData.get(colIndex)));
            }
            else
            {
                Hashtable<String, Capability> caps =
                                            new Hashtable<String, Capability>();
                caps = WURFLInfo.getInstance().getAllCapabilitiesFor(dev, caps);
                deviceData.add(caps);
                devices.add(dev.getDeviceID());
            }
        }
        
        fireTableStructureChanged();
    }
    
    /**
     * Used to delete a capability (row) from the table
     * @param rowIndex the row to be deleted
     */
    public void deleteRow(int rowIndex)
    {
        if (rowIndex < getRowCount())
        {
            capabilities.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    /**
     * Used to delete a device (column) from the table
     * @param colIndex the column to be deleted
     */
    public void deleteColumn(int colIndex)
    {
        deviceData.remove(colIndex);
        devices.remove(colIndex);
    }
    
    /**
     * Returns the class of data displayed in the specified column.
     * @param colIndex the column being checked
     * @return the class to be used by the renderer
     */
    public Class<?> getColumnClass(int colIndex)
    {
        Class result = null;
        
        if (colIndex == 0)
        {
            result = String.class;
        }
        else
        {
            result = Capability.class;
        }
        
        return (result);
    }
    
    /**
     * Accessor
     * @return the currently displayed capabilites
     */
    public ArrayList<String> getCapabilities()
    {
        return (capabilities);
    }
    
    /**
     * Accessor
     * @return the device IDs of all devices currently in the model
     */
    public ArrayList<String> getDevices()
    {
        return (devices);
    }
}
