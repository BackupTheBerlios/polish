/*
 * Class name : NewCapabilityDialog
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 10-Nov-2005
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Dialog containing the information that must be specified when creating a
 * new capability.
 * @author Jim McLachlan
 * @version 1.0
 */
public class NewCapabilityDialog extends JDialog
{
    /** Stores the name of the newly created capability. */
    private JTextField capability;
    
    /** Stores the default value of the newly created capability. */
    private JTextField defaultValue;
    
    /** Flag indicating whether the capability can be added to the group. */
    private boolean capabilityOK;
    
    /**
     * Creates and sets up the dialog.
     * @param owner the component on which the dialog should be centred.
     * @param groupName the name of the group into which the capability should
     * be added
     */
    public NewCapabilityDialog(Component owner, String groupName)
    {
        setTitle("New capability");
        setModal(true);
        capabilityOK = false;
        Component parent = owner;
        
        final JTextField group = new JTextField(groupName, 20);
        group.setEditable(false);
        capability = new JTextField(20);
        defaultValue = new JTextField(20);

        final JPanel capInfoPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 20, 0, 10);
        c.anchor = GridBagConstraints.LINE_START;
        capInfoPanel.add(new JLabel("Group name:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(10, 0, 0, 20);
        capInfoPanel.add(group, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 20, 0, 10);
        capInfoPanel.add(new JLabel("Capabilty name:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 20);
        capInfoPanel.add(capability, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 20, 10, 10);
        capInfoPanel.add(new JLabel("Default value:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 10, 20);
        capInfoPanel.add(defaultValue, c);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                capabilityOK = false;
                if (capability.getText().length() > 0)
                {
                    String inGroup = WURFLInfo.getInstance().
                                    getGroupForCapability(capability.getText());
                    if (inGroup == null)
                    {
                        capabilityOK = true;
                        dispose();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(
                                        capInfoPanel,
                                        "The capability alread exists in the " +
                                        inGroup + " group",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(
                                  capInfoPanel,
                                  "You MUST specify a name for the capability.",
                                  "Error",
                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                capabilityOK = false;
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel newCapPanel = new JPanel(new BorderLayout());
        newCapPanel.add(capInfoPanel, BorderLayout.CENTER);
        newCapPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(newCapPanel);
        
        pack();
        
        setLocationRelativeTo(parent);
    }
    
    /**
     * Accessor
     * @return a flag indicating whether this capability can be added to the
     * generic device
     */
    public boolean isCapabilityOK()
    {
        return (capabilityOK);
    }
    
    /**
     * Accessor
     * @return the name of the new capability
     */
    public String getCapability()
    {
        return (capability.getText());
    }
    
    /**
     * Accessor
     * @return the default value for the new capability
     */
    public String getDefaultValue()
    {
        return (defaultValue.getText());
    }
}
