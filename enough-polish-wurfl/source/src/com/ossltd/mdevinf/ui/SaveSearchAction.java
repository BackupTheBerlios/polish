/*
 * Class name : SaveSearchAction
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 14-Dec-2005
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

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.SearchDetails;

/**
 * This action responds to a request to save a user-defined search.
 * @author Jim McLachlan
 * @version 1.0
 */
public class SaveSearchAction extends AbstractAction
{
    /**
     * Creates the action with the name "Save search".
     */
    public SaveSearchAction()
    {
        super("Save search");
    }

    /**
     * Saves the currently defined search parameters with the name provided
     * by the user.
     * @param e the event that triggered this action
     */
    public void actionPerformed(ActionEvent e)
    {
        String searchName = "";
        ArrayList<String> searchNames = new ArrayList<String>();
        for (SearchDetails search : 
                               ConfigurationManager.getInstance().getSearches())
        {
            searchNames.add(search.getSearchName());
        }
        
        boolean keepAsking = true;
        
        while (keepAsking)
        {
            keepAsking = false;
            
            searchName = JOptionPane.showInputDialog(null,
                                                     "Please enter a description of this search (max. 30 chars.)",
                                                     searchName);
            
            if (searchName != null)
            {
                if ((searchName.length() == 0) ||
                    (searchName.length() > 30))
                {
                    keepAsking = true;
                }
                
                if (searchNames.contains(searchName))
                {
                    int overwriteResult = JOptionPane.showConfirmDialog(null,
                                               "Search name exists, overwrite?",
                                               "Warning",
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.WARNING_MESSAGE);
                    if (overwriteResult != JOptionPane.YES_OPTION)
                    {
                        keepAsking = true;
                    }
                }
            }
        }
        
        if (searchName != null)
        {
            SearchPanelManager.getInstance().saveSearchDetails(searchName);
        }
    }

}
