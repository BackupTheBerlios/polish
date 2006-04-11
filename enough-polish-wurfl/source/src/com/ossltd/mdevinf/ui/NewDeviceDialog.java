/*
 * Class name : NewDeviceDialog
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 09-Nov-2005
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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Dialog containing the information that must be specified when creating a
 * new device.
 * @author Jim McLachlan
 * @version 1.0
 */
public class NewDeviceDialog extends JDialog
{
    /** A reference to the newly created device. */
    private Device device;
    
    /** Flag indicating whether the device can be added to the hierarchy. */
    private boolean deviceOK;
    
    /**
     * Creates and sets up the dialog.
     * @param owner the component on which the dialog should be centred.
     * @param fallbackId the device ID of the "parent".
     */
    public NewDeviceDialog(Component owner, String fallbackId)
    {
        setTitle("New device");
        setModal(true);
        deviceOK = false;
        Component parent = owner;
        
        final JTextField fallback = new JTextField(fallbackId, 20);
        fallback.setEditable(false);
        final JTextField userAgent = new JTextField(20);
        final JTextField deviceId = new JTextField(20);
        final JCheckBox actualDevice = new JCheckBox();

        final JPanel deviceInfoPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 20, 0, 10);
        c.anchor = GridBagConstraints.LINE_START;
        deviceInfoPanel.add(new JLabel("Fallback device:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(10, 0, 0, 20);
        deviceInfoPanel.add(fallback, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 20, 0, 10);
        deviceInfoPanel.add(new JLabel("User Agent:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 20);
        deviceInfoPanel.add(userAgent, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 20, 0, 10);
        deviceInfoPanel.add(new JLabel("Device ID:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 20);
        deviceInfoPanel.add(deviceId, c);
        
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 20, 10, 10);
        deviceInfoPanel.add(new JLabel("Actual device:"), c);
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 10, 20);
        deviceInfoPanel.add(actualDevice, c);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                deviceOK = false;
                if (userAgent.getText().length() > 0)
                {
                    if (deviceId.getText().length() > 0)
                    {
                        device = new Device(deviceId.getText(),
                                            fallback.getText(),
                                            userAgent.getText(),
                                            actualDevice.isSelected(),
                                            true);
                        String unique = WURFLInfo.getInstance().
                                                         deviceIsUnique(device);
                        if (unique.equals("OK"))
                        {
                            deviceOK = true;
                            dispose();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(
                                                     deviceInfoPanel,
                                                     unique,
                                                     "Error",
                                                     JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(
                                                 deviceInfoPanel,
                                                 "You MUST specify a device ID",
                                                 "Error",
                                                 JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(
                                                deviceInfoPanel,
                                                "You MUST specify a user agent",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                deviceOK = false;
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel newDevicePanel = new JPanel(new BorderLayout());
        newDevicePanel.add(deviceInfoPanel, BorderLayout.CENTER);
        newDevicePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(newDevicePanel);
        
        pack();
        
        setLocationRelativeTo(parent);
    }
    
    /**
     * Accessor
     * @return a flag indicating whether this device can be added to the
     * hierarchy.
     */
    public boolean isDeviceOK()
    {
        return (deviceOK);
    }
    
    /**
     * Accessor
     * @return the reference to the newly created device
     */
    public Device getDevice()
    {
        return (device);
    }
}
