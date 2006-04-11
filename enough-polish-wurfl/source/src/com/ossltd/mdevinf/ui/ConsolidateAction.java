/*
 * Class name : ConsolidateAction
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 01-Feb-2006
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.ossltd.mdevinf.model.WURFLExport;

/**
 * This action responds to a request to consolidate the currently loaded
 * WURFL and patch data.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ConsolidateAction extends AbstractAction
{
    /** Stores a reference to the parent component (for dialog display). */
    private Component parent; 
    
    /** Default filename to be selected for this action. */
    private String filename;
    
    /** Flag indicating export of full WURFL or patch file. */
    private boolean fullExport;
    
    /**
     * Creates the action with the provided name.
     * @param parent the parent component to be used for dialogs.
     * @param actionName the name of the action (WURFL... or Patch file...)
     * @param filename the default filename to use (wurfl.xml or wurfl_patch.xml
     * respectively)
     * @param fullExport flag indicating whether the user wishes to consolidate
     * the entire data set into a single WURFL file or whether they just want
     * to consolidate multiple patch files into a single one.
     */
    public ConsolidateAction(Component parent,
                             String actionName,
                             String filename,
                             boolean fullExport)
    {
        super(actionName);
        this.filename = filename;
        this.fullExport = fullExport;
    }

    /**
     * Saves the currently defined search parameters with the name provided
     * by the user.
     * @param e the event that triggered this action
     */
    public void actionPerformed(ActionEvent e)
    {
        int goAhead = JOptionPane.NO_OPTION;
        
        if (fullExport)
        {
            goAhead = JOptionPane.showConfirmDialog(parent,
                                      "Are you sure that you wish to create " +
                                      "a new WURFL data file.  This option \n" +
                                      "is not recommended for the following" +
                                      " reasons:\n" +
                                      " - The original comments will not be" +
                                      " preserved\n" +
                                      " - The original device ordering may " +
                                      "not be preserved\n" +
                                      " - This option has not been fully " +
                                      "tested and the\n    resulting file has" +
                                      " not been fully verified",
                                      "Warning!",
                                      JOptionPane.YES_NO_OPTION,
                                      JOptionPane.WARNING_MESSAGE);
        }
        
        if ((!fullExport) || (goAhead == JOptionPane.YES_OPTION))
        {
            JFileChooser jfc = new JFileChooser("." + File.separator);
            jfc.setMultiSelectionEnabled(false);
            jfc.setSelectedFile(new File(filename));
            
            int result = jfc.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = jfc.getSelectedFile();
    
                boolean writeFile = true;
                if (selectedFile.exists())
                {
                    if (JOptionPane.showConfirmDialog(parent,
                                             "Overwrite the existing file?",
                                             "File exists",
                                             JOptionPane.YES_NO_OPTION) == 
                                                      JOptionPane.NO_OPTION)
                    {
                        writeFile = false;
                    }
                }
                
                if (writeFile)
                {
                    try
                    {
                        WURFLExport.getInstance().exportWURFL(selectedFile,
                                                              fullExport);
                    }
                    catch (IOException ioe)
                    {
                        JOptionPane.showMessageDialog(parent,
                                              "File could not be exported.",
                                              "Export error",
                                              JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
