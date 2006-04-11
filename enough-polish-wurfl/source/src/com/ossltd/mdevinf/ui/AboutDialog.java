/*
 * Class name : AboutDialog
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 17-Nov-2005
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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The dialog to display the WURFL author, maintainer, contributor information. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class AboutDialog extends JDialog
{
//  {{"<name>", "<e-mail>", "<homepage>"}};
    /** Stores the information about current supporters of the project. */
    private static final String[][] supporters = null;
    
    /**
     * Creates and populates the About... dialog.
     * @param owner the parent container for this dialog
     * @param aboutIcon the mDevInf (or mDevInf Web Start icon
     * @param version the current mDevInf version
     */
    public AboutDialog(Frame owner, ImageIcon aboutIcon, String version)
    {
        super(owner, "About...", true);
        JTabbedPane tabs = new JTabbedPane();

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(new JLabel(aboutIcon), BorderLayout.CENTER);
        
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vPanel.add(new JLabel("mDevInf - Version " + version));
        
        JPanel uPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        uPanel.add(new JLabel("A utility for viewing, searching and editing " +
                              "the WURFL database"));

        JPanel cPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cPanel.add(new JLabel("Copyright (c)2005-2006, Jim McLachlan - " +
                              "Objective Software Services Ltd."));
        
        JPanel hPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hPanel.add(new JLabel("(http://www.oss-ltd.com)"));
        
        JPanel verPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 4;
        verPanel.add(imgPanel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        verPanel.add(vPanel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 1;
        verPanel.add(uPanel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        verPanel.add(cPanel, c);

        c.gridx = 1;
        c.gridy = 3;
        c.gridheight = 1;
        verPanel.add(hPanel, c);
        
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        versionPanel.add(verPanel);

        JPanel sHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sHeader.add(new JLabel("Thank you to our supporters:"));
        final JTextArea sList = new JTextArea(8, 40);
        sList.setEditable(false);

        if (supporters != null)
        {
            for (int i = 0; i < supporters.length; i++)
            {
                sList.append(supporters[i][0]);
                sList.append("\n  ");
                sList.append(supporters[i][1]);
                sList.append("\n  ");
                sList.append(supporters[i][2]);
                sList.append("\n");
            }
        }
        else
        {
            sList.append("Be listed here as a supporter for as " +
                         "little as $5.00!");
        }

        JPanel supportersPanel = new JPanel(new BorderLayout());
        supportersPanel.add(sHeader, BorderLayout.NORTH);
        supportersPanel.add(new JScrollPane(sList), BorderLayout.CENTER);
        
        tabs.addTab("Version", versionPanel);
        tabs.addTab("Supporters", supportersPanel);
        
        tabs.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                Rectangle topOfList = new Rectangle(1, 1, 2, 2);
                sList.scrollRectToVisible(topOfList);
            }
        });

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        
        JPanel wurflInfoPanel = new JPanel(new BorderLayout());
        wurflInfoPanel.add(tabs, BorderLayout.CENTER);
        wurflInfoPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(wurflInfoPanel);
        
        pack();
        
        setLocationRelativeTo(owner);
    }
}
