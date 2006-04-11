/*
 * Class name : SavedSearchManager
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.SearchDetails;

/**
 * The dialog displaying all available saved searches and check boxes to allow
 * selection of those to be deleted.
 * @author Jim McLachlan
 * @version 1.0
 */
public class SavedSearchManager extends JDialog
{
    /** Stores the user selections. */
    private Vector<JCheckBox> selections;
    
    /**
     * Creates and populates the Patch Chooser dialog.
     * @param owner the parent of the dialog.
     */
    public SavedSearchManager(Frame owner)
    {
        super (owner, "Saved search manager", true);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Select searches to be deleted:"));
        
        JPanel patchPanel = new JPanel();
        patchPanel.setBackground(Color.white);
        patchPanel.setLayout(new BoxLayout(patchPanel, BoxLayout.PAGE_AXIS));
        selections = new Vector<JCheckBox>();
        JCheckBox curSearch = null;
        
        for (SearchDetails detail :
                               ConfigurationManager.getInstance().getSearches())
        {
            curSearch = new JCheckBox(detail.getSearchName(), false);
            curSearch.setBackground(Color.white);
            patchPanel.add(curSearch);
            selections.add(curSearch);
        }
        
        JButton okButton = new JButton("Delete");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                boolean changed = false;
                
                ArrayList<SearchDetails> searches =
                               ConfigurationManager.getInstance().getSearches();
                ArrayList<SearchDetails> toRemove = 
                                                 new ArrayList<SearchDetails>();
                Iterator<SearchDetails> it = null;
                boolean found = false;
                SearchDetails currentSearch = null;
               
                for (JCheckBox selection : selections)
                {
                    if (selection.isSelected())
                    {
                        it = searches.iterator();
                        found = false;
                        while ((!found) && (it.hasNext()))
                        {
                            currentSearch = it.next();
                            if (currentSearch.getSearchName().
                                                    equals(selection.getText()))
                            {
                                toRemove.add(currentSearch);
                                found = true;
                            }
                        }

                        searches.removeAll(toRemove);
                        SearchPanelManager.getInstance().
                                                      prepareSavedSearchCombo();
                        changed = true;
                    }
                }
                
                if (changed)
                {
                    ConfigurationManager.getInstance().saveConfig();
                }
                
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
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
}
