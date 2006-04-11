/*
 * Class name : PatchChooser
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 09-Dec-2005
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ossltd.mdevinf.ConfigurationManager;

/**
 * The dialog displaying all available patch files and check boxes to indicate
 * which ones are active.
 * @author Jim McLachlan
 * @version 1.0
 */
public class PatchChooser extends JDialog
{
    /** Stores the user selections. */
    private Vector<JCheckBox> selections;
    
    /** Flag to indicate that the user changed the data (pressed OK). */
    private boolean changed;
    
    /**
     * Creates and populates the Patch Chooser dialog.
     * @param owner the parent of the dialog.
     */
    public PatchChooser(Frame owner)
    {
        super (owner, "Patch file selection", true);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Please select or deselect patch files " +
                                   "from the list below:"));
        
        JPanel patchPanel = new JPanel();
        patchPanel.setBackground(Color.white);
        patchPanel.setLayout(new BoxLayout(patchPanel, BoxLayout.PAGE_AXIS));
        Hashtable<String, Boolean> patchFiles =
                          ConfigurationManager.getInstance().getPatchFileList();
        selections = new Vector<JCheckBox>();
        JCheckBox curFile = null;
        
        String filename = null;
        Enumeration<String> patchFileKeys = patchFiles.keys();
        while (patchFileKeys.hasMoreElements())
        {
            filename = patchFileKeys.nextElement();
            curFile = new JCheckBox(filename, patchFiles.get(filename));
            curFile.setBackground(Color.white);
            patchPanel.add(curFile);
            selections.add(curFile);
        }
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Hashtable<String, Boolean> patches =
                          ConfigurationManager.getInstance().getPatchFileList();
                for (JCheckBox selection : selections)
                {
                    patches.put(selection.getText(), selection.isSelected());
                }
                
                changed = true;
                
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                changed = false;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(patchPanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(mainPanel);

        setSize(new Dimension(400, 300));
        
        setLocationRelativeTo(owner);
    }
    
    /**
     * Accessor
     * @return the user selections
     */
    public Vector<JCheckBox> getSelections()
    {
        return (selections);
    }

    /**
     * Accessor
     * @return the flag that indicates whether the user pressed OK or not
     */
    public boolean isChanged()
    {
        return (changed);
    }
}
