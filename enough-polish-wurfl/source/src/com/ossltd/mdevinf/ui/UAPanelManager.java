/*
 * Class name : UAPanelManager
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 07-Nov-2005
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the User Agents tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class UAPanelManager
{
    /** Stores the reference to the singleton instance of this class. */
    private static UAPanelManager instance = null;
    
    /** Stores a reference to the User Agent <code>JPanel</code> */
    private JPanel uaPanel;
    
    /**
     * Reference to the input field on the User Agent tab.
     */
    private JTextField uaInputField;
    
    /**
     * Reference to user agent results list.
     */
    private JList userAgentResults;
    
    /** Popup menu for the Relationships tree. */
    private JPopupMenu uaPopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    /**
     * Creates the popup menu for the Relationships panel. 
     */
    private void createPopup()
    {
        if ((WURFLInfo.getInstance().getMDevInfWSIcon() == null))
        {
            JMenuItem editDevice = new JMenuItem(new EditAction());
            editDevice.setToolTipText("Edit the currently selected device");
            
            uaPopupMenu = new JPopupMenu();
            
            uaPopupMenu.add(editDevice);
        }
    }

    /**
     * Creates the User Agents panel.
     */
    private UAPanelManager()
    {
        createPopup();
        
        uaPanel = new JPanel(new BorderLayout());
        uaInputField = new JTextField(20);
        userAgentResults = new JList();
        userAgentResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        final JCheckBox realOnly = new JCheckBox("Real devices only?", true);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JButton findButton = new JButton("Find matches");
        findButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Vector<String> uaMatches = WURFLInfo.getInstance().
                                           findUAMatches(uaInputField.getText(),
                                                         realOnly.isSelected());
                Collections.sort(uaMatches);
                userAgentResults.setListData(uaMatches);
            }
        });
        inputPanel.add(new JLabel("Full or partial User Agent string:"));
        inputPanel.add(uaInputField);
        inputPanel.add(findButton);
        inputPanel.add(realOnly);
        
        userAgentResults.addListSelectionListener(new ListSelectionListener(){
            private String previousMatch = "";
            
            public void valueChanged(ListSelectionEvent e)
            {
                String selected = (String)userAgentResults.getSelectedValue();
                if ((selected != null) &&
                    (!selected.toString().equals(previousMatch)))
                {
                    previousMatch = selected;
                    Device device = WURFLInfo.getInstance().
                                                  findDeviceForUAProf(selected);
                    if (device != null)
                    {
                        TabbedPaneManager.getInstance().deviceSelected(device);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(
                                                     uaPanel,
                                                     "No match found for '" +
                                                     selected + "'",
                                                     "Error",
                                                     JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        userAgentResults.addMouseListener(new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    uaPopupMenu.show(e.getComponent(), popX, popY);
                }
            }
            
            public void mousePressed(MouseEvent e)
            {
                showPopup(e);
            }
            
            public void mouseReleased(MouseEvent e)
            {
                showPopup(e);
            }
        });
        
        uaPanel.add(inputPanel, BorderLayout.NORTH);
        uaPanel.add(new JScrollPane(userAgentResults), BorderLayout.CENTER);
    }
    
    /**
     * Accessor
     * @return a reference to the User Agents panel
     */
    public JPanel getUAPanel()
    {
        return (uaPanel);
    }
    
    /**
     * Removes all the items from the list of matched devices.
     */
    public void clearListData()
    {
        userAgentResults.setListData(new Vector());
    }

    /**
     * Updates the user agent input field when a device has been selected
     * @param device the device that has been selected
     */
    public void updateContents(Device device)
    {
        uaInputField.setText(device.getUserAgent());
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static UAPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new UAPanelManager();
        }
        
        return (instance);
    }
}
