/*
 * Class name : TabbedPaneManager
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

import java.util.Hashtable;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout of the tabs on the main frame.
 * @author Jim McLachlan
 * @version 1.0
 */
public class TabbedPaneManager
{
    /** Constant specifying insert position of the Device tab. */
    public static final int device_tab = 0;
    
    /** Constant specifying insert position of the Search tab. */
    public static final int search_tab = 1;
    
    /** Constant specifying insert position of the Results tab. */
    public static final int results_tab = 2;
    
    /** Constant specifying insert position of the User Agent tab. */
    public static final int user_agent_tab = 3;
    
    /** Constant specifying insert position of the Relationship tab. */
    public static final int relationship_tab = 4;
    
    /** Constant specifying insert position of the Comparison tab. */
    public static final int comparison_tab = 5;
    
    /** Constant specifying insert position of the Editor tab. */
    public static final int editor_tab = 6;

    /**
     * Constant Strings defining titles of tabs.
     * <p>This array <b>must</b> have the correct number of elements, one
     * for each of the ..._tab constants.
     */
    private static final String[] titles = {"Device",
                                            "Search",
                                            "Results",
                                            "User Agents",
                                            "Relationships",
                                            "Comparison",
                                            "Editor"
                                           };
    
    /** Stores a reference to the singleton instance for this class. */
    private static TabbedPaneManager instance = null;
    
    /**
     * Stores the reference to the <code>JTabbedPane</code> that this class
     * manages.
     */
    private JTabbedPane tabbedPane;
    
    /**
     * Stores a reference to the last selected device.
     */
    private Device lastSelectedDevice;
    
    /**
     * Adds the tabs to the panel in the correct positions.
     */
    private void addTabs()
    {
        tabbedPane.insertTab(titles[device_tab],
                             null,
                             DevicePanelManager.getInstance().getDevicePanel(),
                             "Find out about a specific device",
                             device_tab);
        
        tabbedPane.insertTab(titles[search_tab],
                             null,
                             SearchPanelManager.getInstance().getSearchPanel(),
                             "Find all devices with specified capabilities",
                             TabbedPaneManager.search_tab);
        
        tabbedPane.insertTab(titles[results_tab],
                             null,
                             ResultsPanelManager.getInstance().
                                                              getResultsPanel(),
                             "Search results will be displayed here",
                             results_tab);
        
        tabbedPane.insertTab(titles[user_agent_tab],
                             null,
                             UAPanelManager.getInstance().getUAPanel(),
                             "User Agent Profile information",
                             user_agent_tab);
        
        tabbedPane.insertTab(titles[relationship_tab],
                             null,
                             RelPanelManager.getInstance().
                                                         getRelationshipPanel(),
                             "Device relationships in the WURFL hierarchy",
                             relationship_tab);
        
        tabbedPane.insertTab(titles[comparison_tab],
                             null,
                             CompPanelManager.getInstance().
                                                           getComparisonPanel(),
                             "Compare the capabilities of multiple devices",
                             comparison_tab);
        
        tabbedPane.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e)
            {
                int selected = tabbedPane.getSelectedIndex();
                tabbedPane.setTitleAt(selected, titles[selected]);
                
                tabbedPane.getSelectedComponent().repaint();
            }
        });
    }
    
    /**
     * Constructs the tabbed pane.
     */
    private TabbedPaneManager()
    {
        tabbedPane = new JTabbedPane();
        
        lastSelectedDevice = null;

        addTabs();
    }

    /**
     * Adds the asterisk (*) to the end of the specified tab if not already
     * present.
     * @param tab the tab whose title should be marked with the asterisk
     */
    public void markUpdated(int tab)
    {
        if (tab != getTab())
        {
            tabbedPane.setTitleAt(tab, titles[tab] + "*");
        }
    }
    
    /**
     * Accessor
     * @return a reference to the <code>JTabbedPane</code>
     */
    public JTabbedPane getTabbedPane()
    {
        return (tabbedPane);
    }
    
    /**
     * Mutator
     * @param index the index of the tab to select
     */
    public void setTab(int index)
    {
        tabbedPane.setSelectedIndex(index);
    }
    
    /**
     * Accessor
     * @return the index of the currently selected tab
     */
    public int getTab()
    {
        return (tabbedPane.getSelectedIndex());
    }

    /**
     * Adds the editor tab to the tabbed pane.
     */
    public void addEditorTab()
    {
        tabbedPane.insertTab("Editor",
                             null,
                             EditorPanelManager.getInstance().getEditorPanel(),
                             "Edit the values of the device in this tab",
                             editor_tab);
    }
    
    /**
     * Removes the editor tab from the tabbed pane.
     */
    public void removeEditorTab()
    {
        tabbedPane.remove(editor_tab);
        EditorPanelManager.destroy();
    }
    
    /**
     * Replace the device, search and relationships tab when a change has been
     * made to the WURFL hierarchy.
     */
    public void updateAfterChanges()
    {
        tabbedPane.removeChangeListener(tabbedPane.getChangeListeners()[0]);
        
        DevicePanelManager.destroy();
        SearchPanelManager.destroy();
        RelPanelManager.destroy();
        CompPanelManager.destroy();
        
        tabbedPane.removeAll();
        addTabs();  // This puts the change listener back.
        
        if (lastSelectedDevice != null)
        {
            deviceSelected(lastSelectedDevice);
        }
    }
    
    /**
     * Sets the currently selected device on all tabs.
     * @param device the device to display on all tabs
     */
    public void deviceSelected(Device device)
    {
        Hashtable<String, Capability> caps =
                                            new Hashtable<String, Capability>();
        caps = WURFLInfo.getInstance().getAllCapabilitiesFor(device, caps);
        
        DevicePanelManager.getInstance().updateContents(caps);
        markUpdated(device_tab);
        UAPanelManager.getInstance().updateContents(device);
        markUpdated(user_agent_tab);
        RelPanelManager.getInstance().updateContents(device);
        markUpdated(relationship_tab);
        
        lastSelectedDevice = device;
    }
    
    /**
     * Accessor
     * @return the last selected device
     */
    public Device getLastSelectedDevice()
    {
        return (lastSelectedDevice);
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static TabbedPaneManager getInstance()
    {
        if (instance == null)
        {
            instance = new TabbedPaneManager();
        }
        
        return (instance);
    }
}
