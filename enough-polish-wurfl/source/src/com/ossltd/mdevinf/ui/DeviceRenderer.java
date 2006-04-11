/*
 * Class name : DeviceRenderer
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 31-Oct-2005
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
import java.awt.Component;
import java.awt.Font;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * A class to render the device information in the Relationships tree.
 * <p>The root node is rendered with the <code>wurflIcon</code>.</p>
 * <p>Actual devices are rendered in blue and with the full colour
 * <code>deviceIcon</code>.</p>
 * <p>Actual devices that have been created or updated with a WURFL patch file
 * are rendered with green text.</p>
 * <p>Fallback devices are rendered in light gray with the diminished colour
 * <code>noDeviceIcon</code>.</p>
 * <p>Fallback devices that have been created or updated with a WURFL patch file
 * are rendered with dark green text.</p>
 * <p>The selected item is highlighted using an orange border.</p>
 * @author Jim McLachlan
 * @version 1.0
 */
public class DeviceRenderer extends DefaultTreeCellRenderer
{
    /** Constant defining the dark green colour. */
    private static final Color darkGreen = new Color(0x00, 0x66, 0x00);
    
    /** Constant defining the green colour. */
    private static final Color green = new Color(0x00, 0xcc, 0x00);
    
    /** Stores the default font. */
    private static Font defaultFont;
    
    /** Stores the bold font (based on the default font). */
    private static Font boldFont;
    
    /** Stores the WURFL icon for use at the tree root. */
    private ImageIcon wurflIcon;
    
    /** Stores the icon used to indicate an actual device. */
    private ImageIcon deviceIcon;
    
    /** Stores the icon used to indicate a fallback device. */
    private ImageIcon noDeviceIcon;
    
    /**
     * Configures the renderer.
     * @param defFont sets the default <code>Font</code>
     * @param wurflIcon the 16x16 WURFL icon for the tree root
     * @param deviceIcon the 16x16 device icon for actual devices
     * @param noDeviceIcon the 16x16 reduced colour icon for fallback devices
     */
    public DeviceRenderer(Font defFont,
                          ImageIcon wurflIcon,
                          ImageIcon deviceIcon,
                          ImageIcon noDeviceIcon)
    {
        defaultFont = defFont;
        boldFont = defaultFont.deriveFont(Font.BOLD);
        this.wurflIcon = wurflIcon;
        this.deviceIcon = deviceIcon;
        this.noDeviceIcon = noDeviceIcon;
    }
    
    /**
     * Sets up the nodes <code>JLabel</code> for rendering.
     * @param tree the WURFL tree
     * @param value the node to be rendered
     * @param sel a flag indicating whether this node is selected
     * @param expanded a flag indicating whether this node is expanded (unused)
     * @param leaf a flag indicating whether this node is a leaf (unused)
     * @param row the row in the tree model for this node (unused)
     * @param focus a flag indicating whether this node has focus (unused)
     * @return the rendered JLabel for display.
     */
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean focus)
    {
        setForeground(Color.lightGray);
        setFont(defaultFont);

        if (sel)
        {
            setBorder(BorderFactory.createLineBorder(Color.orange));
        }
        else
        {
            setBorder(null);
        }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

        Device dev = (Device)node.getUserObject();

        StringBuffer sb = new StringBuffer();
        
        if (dev != null)
        {
            Hashtable<String, Capability> props =
                                            new Hashtable<String, Capability>();
            props = WURFLInfo.getInstance().getAllCapabilitiesFor(dev, props);
            sb.append(props.get("brand_name").getValue());
            sb.append(" ");
            sb.append(props.get("model_name").getValue());
            
            if (dev.isActualDevice())
            {
                setIcon(deviceIcon);
                if (dev.isPatchedDevice())
                {
                    setFont(boldFont);
                    setForeground(green);
                }
                else
                {
                    setFont(boldFont);
                    setForeground(Color.blue);
                }
            }
            else
            {
                if (dev.getDeviceID().equals("generic"))
                {
                    setIcon(wurflIcon);
                }
                else
                {
                    setIcon(noDeviceIcon);
                }
                
                if (dev.isPatchedDevice())
                {
                    setForeground(darkGreen);
                }
                sb = new StringBuffer(dev.getDeviceID());
            }

            Hashtable<String, Group> grps = dev.getGroups();
            if ((grps != null) && (grps.size() > 0))
            {
                sb.append(" +");
            }
        }
        
        setText(sb.toString());
        
        return (this);
    }
}
