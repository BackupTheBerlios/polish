/*
 * Class name : WURFLInfoDialog
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 27-Oct-2005
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
import java.util.Vector;

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

import com.ossltd.mdevinf.model.Person;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * The dialog to display the WURFL author, maintainer, contributor information. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class WURFLInfoDialog extends JDialog
{
    /**
     * Creates and populates the WURFL information dialog.
     * @param owner the parent container for this dialog
     * @param wurflIcon the WURFL icon
     */
    public WURFLInfoDialog(Frame owner, ImageIcon wurflIcon)
    {
        super(owner, "WURFL Information", true);
        JTabbedPane tabs = new JTabbedPane();

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(new JLabel(wurflIcon), BorderLayout.CENTER);
        
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vPanel.add(new JLabel("WURFL Version: "));
        vPanel.add(new JLabel(WURFLInfo.getInstance().getVersion()));
        
        JPanel dPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dPanel.add(new JLabel("Last Updated: "));
        dPanel.add(new JLabel(WURFLInfo.getInstance().getVDate()));
        
        JPanel uPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        uPanel.add(new JLabel("Official URL: "));
        uPanel.add(new JLabel(WURFLInfo.getInstance().getOfficialURL()));
        
        JPanel verPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        verPanel.add(imgPanel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        verPanel.add(vPanel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        verPanel.add(dPanel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        verPanel.add(uPanel, c);
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        versionPanel.add(verPanel);
        
        JPanel aHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aHeader.add(new JLabel("The authors of this version of WURFL are:"));
        final JTextArea aList = new JTextArea(8, 40);
        aList.setEditable(false);
        Vector<Person> authors = WURFLInfo.getInstance().getAuthors();
        for (Person author : authors)
        {
            aList.append(author.getName());
            aList.append("\n    e-mail: ");
            aList.append(author.getEmail());
            if (author.getHomepage().length() > 0)
            {
                aList.append("\n    Home page: ");
                aList.append(author.getHomepage());
            }
            aList.append("\n");
        }

        JPanel authorsPanel = new JPanel(new BorderLayout());
        authorsPanel.add(aHeader, BorderLayout.NORTH);
        authorsPanel.add(new JScrollPane(aList), BorderLayout.CENTER);
        
        JPanel mHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mHeader.add(
                   new JLabel("The maintainers of this version of WURFL are:"));
        final JTextArea mList = new JTextArea(8, 40);
        mList.setEditable(false);
        Vector<Person> maintainers = WURFLInfo.getInstance().getMaintainers();
        for (Person maintainer : maintainers)
        {
            mList.append(maintainer.getName());
            mList.append("\n    e-mail: ");
            mList.append(maintainer.getEmail());
            if (maintainer.getHomepage().length() > 0)
            {
                mList.append("\n    Home page: ");
                mList.append(maintainer.getHomepage());
            }
            mList.append("\n");
        }

        JPanel maintainersPanel = new JPanel(new BorderLayout());
        maintainersPanel.add(mHeader, BorderLayout.NORTH);
        maintainersPanel.add(new JScrollPane(mList), BorderLayout.CENTER);
        
        JPanel cHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cHeader.add(new JLabel(
                 "The following people contributed to this version of WURFL:"));
        final JTextArea cList = new JTextArea(8, 40);
        cList.setEditable(false);
        Vector<Person> contributors = WURFLInfo.getInstance().getContributors();
        for (Person contributor : contributors)
        {
            cList.append(contributor.getName());
            cList.append("\n    e-mail: ");
            cList.append(contributor.getEmail());
            if (contributor.getHomepage().length() > 0)
            {
                cList.append("\n    Home page: ");
                cList.append(contributor.getHomepage());
            }
            cList.append("\n");
        }

        JPanel contributorsPanel = new JPanel(new BorderLayout());
        contributorsPanel.add(cHeader, BorderLayout.NORTH);
        contributorsPanel.add(new JScrollPane(cList), BorderLayout.CENTER);
        
        JPanel sHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sHeader.add(new JLabel("A statement about WURFL:"));
        final JTextArea sList = new JTextArea(8, 50);
        sList.setEditable(false);
        sList.append(WURFLInfo.getInstance().getStatement());
        
        JPanel statementPanel = new JPanel(new BorderLayout());
        statementPanel.add(sHeader, BorderLayout.NORTH);
        statementPanel.add(new JScrollPane(sList), BorderLayout.CENTER);
        
        tabs.addTab("Version", versionPanel);
        tabs.addTab("Authors", authorsPanel);
        tabs.addTab("Maintainers", maintainersPanel);
        tabs.addTab("Contributors", contributorsPanel);
        tabs.addTab("Statement", statementPanel);
        
        tabs.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                Rectangle topOfList = new Rectangle(1, 1, 2, 2);
                aList.scrollRectToVisible(topOfList);
                mList.scrollRectToVisible(topOfList);
                cList.scrollRectToVisible(topOfList);
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
