/*
 * Class name : EditAction
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 13-Dec-2005
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

import javax.swing.AbstractAction;

import com.ossltd.mdevinf.model.Device;

/**
 * Action used to set up the Editor tab to edit the currenly selected
 * device.
 * @author Jim McLachlan
 * @version 1.0
 */
public class EditAction extends AbstractAction
{

    /** 
     * Creates the Edit Action
     */
    public EditAction()
    {
        super("Edit device");
    }

    /**
     * Identifies the currently selected device and passes it to the editor
     * tab
     * @param e the event that triggered this action
     */
    public void actionPerformed(ActionEvent e)
    {
        Device curDev = TabbedPaneManager.getInstance().
                                                    getLastSelectedDevice();
        if (curDev != null)
        {
            TabbedPaneManager.getInstance().addEditorTab();
            EditorPanelManager.getInstance().setValuesFor(curDev);
            TabbedPaneManager.getInstance().
                                       setTab(TabbedPaneManager.editor_tab);
        }
    }
}
