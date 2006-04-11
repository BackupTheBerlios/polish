/*
 * Class name : DevicePanelManager
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Description;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.MakeModelDetails;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the Device tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class DevicePanelManager
{
    /** Stores the background colour for overridden values. */
    private static final Color overriddenColour = new Color(0xFF, 0xFF, 0xAA);
    
    /** Stores the background colour for values from patch files. */
    private static final Color patchColour = new Color(0xAA, 0xFF, 0xAA);
    
    /** Stores the default background colour. */
    private static final Color defaultColour = (new JTextField()).
                                                                getBackground();
    
    /** Constant defining the text for the default combo box items. */
    private static final String CHOOSE_ONE_STRING = "-- Choose one --";
    
    /** Constant defining the maximum number of items in the device history. */
    private static final int MAX_HISTORY_SIZE = 20;

    /** Stores a reference to the singleton instance of this class. */
    private static DevicePanelManager instance = null;
    
    /** Stores a reference to the panel for the Device tab. */
    private JPanel devicePanel;
    
    /**
     * Reference to the <code>JComboBox</code> used to list the brand names
     * from the wurfl.xml file.
     */
    private JComboBox makeList;
    
    /**
     * Reference to the <code>JComboBox</code> displayed on the Device tab,
     * used to list the model names from the wurfl.xml file.  This is
     * re-populated each time the Device tab <code>makeList</code> is changed.
     */
    private JComboBox modelList;
    
    /**
     * Reference to the <code>JComboBox</code> displayed on the Device tab,
     * used to list the 20 most recently selected devices.
     */
    private JComboBox recentList;
    
    /**
     * Reference to the listener used for the recentList.
     */
    private RecentActionListener recentActionListener;
    
    /**
     * Reference to the <code>JTextField</code>s used to display the device
     * capabilities.
     * <p><i>The key into this <code>Hashtable</code> is the capability name.
     * eg. j2me_midp_2_0.</i></p>
     */
    private Hashtable<String, JTextField> deviceInfoFields;
    
    /** Popup menu for the Relationships tree. */
    private JPopupMenu devicePopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    /**
     * Creates the popup menu for the Relationships panel. 
     */
    private void createPopup()
    {
        devicePopupMenu = new JPopupMenu();
        
        if ((WURFLInfo.getInstance().getMDevInfWSIcon() == null))
        {
            JMenuItem editDevice = new JMenuItem(new EditAction());
            editDevice.setToolTipText("Edit the currently selected device");
            
            devicePopupMenu.add(editDevice);
            devicePopupMenu.addSeparator();
        }
        
        JMenuItem compDevice = new JMenuItem("Compare device");
        compDevice.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CompPanelManager.getInstance().addDevice();
            }
        });
        
        devicePopupMenu.add(compDevice);
    }

    /**
     * Create the JPanel for the specific device tab of the main window.
     */
    private DevicePanelManager()
    {
        createPopup();

        MouseAdapter popupMouseListener = new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    devicePopupMenu.show(e.getComponent(), popX, popY);
                }
            }
            
            public void mousePressed(MouseEvent e)
            {
                showPopup(e);
            }
            
            public void mouseReleased(MouseEvent e)
            {
                showPopup(e);
            }
        };
        
        devicePanel = new JPanel(new BorderLayout());
        
        // Start by filling in the makes combo box
        Vector<String> makes = WURFLInfo.getInstance().getMakes();
        Collections.sort(makes);
        makes.insertElementAt(CHOOSE_ONE_STRING, 0);
        makeList = new JComboBox(makes);
        makeList.addActionListener(new ActionListener(){
            private String previousMake = CHOOSE_ONE_STRING;
            
            public void actionPerformed(ActionEvent e)
            {
                if ((!makeList.getSelectedItem().equals(CHOOSE_ONE_STRING)) &&
                    (!previousMake.equals(makeList.getSelectedItem())))
                {
                    previousMake = (String)makeList.getSelectedItem();
                    Vector<String> models = WURFLInfo.getInstance().
                                  getModels((String)makeList.getSelectedItem());
                    Collections.sort(models);
                    models.insertElementAt(CHOOSE_ONE_STRING, 0);
                    modelList.removeAllItems();
                    for (String item : models)
                    {
                        modelList.addItem(item);
                    }
                }
            }
        });
        
        // The model list shouldn't be filled in yet.  Wait for "make" selection
        modelList = new JComboBox();
        modelList.addItem(CHOOSE_ONE_STRING);
        modelList.addActionListener(new ModelActionListener());
        
        // Show the 20 most recently selected devices
        recentList = new JComboBox();
        recentList.addItem(CHOOSE_ONE_STRING);
        recentActionListener = new RecentActionListener();
        prepareRecentSelections();

        // Titles for combo boxes
        JPanel makePanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        makePanel.add(new JLabel("Make:"));
        JPanel modelPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        modelPanel.add(new JLabel("Model:"));
        JPanel recentPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        recentPanel.add(new JLabel("Recent:"));
        JPanel makeListPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        makeListPanel.add(makeList);
        JPanel modelListPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        modelListPanel.add(modelList);
        JPanel recentListPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        recentListPanel.add(recentList);
        
        // Create the Hashtable for the device info fields, the keys can be
        //  created from the names of the tabs and capabilities
        deviceInfoFields = new Hashtable<String, JTextField>();
        
        // Capability panel
        JTabbedPane capabilityTabs = new JTabbedPane();
        Hashtable<String, Group> groups = WURFLInfo.getInstance().
                                                           getGroups("generic");
        Enumeration<String> groupNames = groups.keys();
        ArrayList<String> sortedNames = new ArrayList<String>();
        while (groupNames.hasMoreElements())
        {
            sortedNames.add(groupNames.nextElement());
        }
        Collections.sort(sortedNames);
        Hashtable<String, Capability> caps = null;
        for (String groupName : sortedNames)
        {
            caps = groups.get(groupName).getCapabilities();
            int numCaps = caps.size() + 1; // Add one to make column wrap work.
            
            JPanel capsPanel = new JPanel(new GridBagLayout());
            Enumeration<String> capsList = caps.keys();
            ArrayList<String> sortedCaps = new ArrayList<String>();
            while (capsList.hasMoreElements())
            {
                sortedCaps.add(capsList.nextElement());
            }
            Collections.sort(sortedCaps);
            
            GridBagConstraints c = new GridBagConstraints();
            int row = 0;
            int col = 0;
            for (String curKey : sortedCaps)
            {
                JPanel labelPanel = new JPanel(new 
                                               FlowLayout(FlowLayout.TRAILING));
                labelPanel.add(new JLabel(curKey));
                Description desc = WURFLInfo.getInstance().
                                                         getDescription(curKey);
                String tooltip = "Sorry, no description available.";
                if (desc != null)
                {
                    tooltip = desc.getHTMLFormattedDescription();
                }
                labelPanel.setToolTipText(tooltip);
                labelPanel.addMouseListener(popupMouseListener);
                c.gridy = row;
                c.gridx = col;
                c.anchor = GridBagConstraints.LINE_END;
                capsPanel.add(labelPanel, c);
                JPanel valPanel = new JPanel(new FlowLayout(FlowLayout.
                                                                      LEADING));
                JTextField capField = new JTextField("", 6);
                capField.setEditable(false);
                deviceInfoFields.put(curKey, capField);
                valPanel.add(deviceInfoFields.get(curKey));
                c.gridy = row;
                c.gridx = col + 1;
                capsPanel.add(valPanel, c);
                
                row++;
                if (row >= (numCaps >> 1))
                {
                    row = 0;
                    col = 3;
                }
            }

            capsPanel.addMouseListener(popupMouseListener);
            capabilityTabs.addTab(groupName, new JScrollPane(capsPanel));
        }

        JPanel makeSubPanel = new JPanel(new FlowLayout());
        makeSubPanel.add(makePanel);
        makeSubPanel.add(makeListPanel);
        
        JPanel modelSubPanel = new JPanel(new FlowLayout());
        modelSubPanel.add(modelPanel);
        modelSubPanel.add(modelListPanel);
        
        JPanel recentSubPanel = new JPanel(new FlowLayout());
        recentSubPanel.add(recentPanel);
        recentSubPanel.add(recentListPanel);
        
        JPanel makeModelSubPanel = new JPanel();
        makeModelSubPanel.setLayout(new BoxLayout(makeModelSubPanel, BoxLayout.LINE_AXIS));
        makeModelSubPanel.add(makeSubPanel);
        makeModelSubPanel.add(Box.createHorizontalGlue());
        makeModelSubPanel.add(modelSubPanel);
        makeModelSubPanel.add(Box.createHorizontalGlue());
        makeModelSubPanel.add(recentSubPanel);

        devicePanel.add(makeModelSubPanel, BorderLayout.NORTH);
        devicePanel.add(capabilityTabs, BorderLayout.CENTER);
    }
    
    /**
     * Sets up the recently selected device list from the configuration file.
     */
    public void prepareRecentSelections()
    {
        recentList.removeActionListener(recentActionListener);
        
        recentList.removeAllItems();
        
        recentList.addItem(CHOOSE_ONE_STRING);
        
        for (MakeModelDetails dev :
                          ConfigurationManager.getInstance().getDeviceHistory())
        {
            recentList.addItem(dev);
        }
        
        recentList.addActionListener(recentActionListener);
    }
    
    /**
     * Accessor
     * @return the reference to the panel for the Device tab
     */
    public JPanel getDevicePanel()
    {
        return (devicePanel);
    }
    
    /**
     * Mutator
     * @param make the item to set in the make list
     */
    public void setMake(String make)
    {
        makeList.setSelectedItem(make);
    }
    
    /**
     * Accessor
     * @return the currently selected item in the make list
     */
    public String getMake()
    {
        return ((String)makeList.getSelectedItem());
    }
    
    /**
     * Mutator
     * @param model the item to set in the model list
     */
    public void setModel(String model)
    {
        modelList.setSelectedItem(model);
    }
    
    /**
     * Accessor
     * @return the currently selected item in the model list
     */
    public String getModel()
    {
        return ((String)modelList.getSelectedItem());
    }
    
    /**
     * Update the contents of the device panel when a new device has been
     * selected.
     * @param caps the capabilities of the device
     */
    public void updateContents(Hashtable<String, Capability> caps)
    {
        String make = caps.get("brand_name").getValue();
        String model = caps.get("model_name").getValue();
        
        makeList.setSelectedItem(make);
        modelList.setSelectedItem(model);
        
        ArrayList<MakeModelDetails> curList =
                          ConfigurationManager.getInstance().getDeviceHistory();
        MakeModelDetails recent = new MakeModelDetails(make, model);
        
        if (curList.contains(recent))
        {
            curList.remove(recent);
        }

        while (curList.size() >= MAX_HISTORY_SIZE)
        {
            curList.remove(MAX_HISTORY_SIZE - 1);
        }

        curList.add(0, recent);
        
        ConfigurationManager.getInstance().saveConfig();
        
        prepareRecentSelections();
        
        recentList.setSelectedItem(recent);
        
        Enumeration<String> capKeys = caps.keys();
        String curKey = null;
        JTextField curField = null;
        Capability curCap = null;
        while (capKeys.hasMoreElements())
        {
            curKey = capKeys.nextElement();
            curField = deviceInfoFields.get(curKey);
            curCap = caps.get(curKey);

            if (curField != null)
            {
                if (curCap.isPatched())
                {
                    curField.setBackground(patchColour);
                }
                else if (curCap.isOverridden())
                {
                    curField.setBackground(overriddenColour);
                }
                else
                {
                    curField.setBackground(defaultColour);
                }
                curField.setText(curCap.getValue());
            }
        }
    }
    
    /**
     * Used to removed the singleton instance.
     * <p>This allows complete re-creation of the device panel when changes
     * have been made to the WURFL hierarchy.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static DevicePanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new DevicePanelManager();
        }
        
        return (instance);
    }

    /**
     * Listener for the modelList <code>JComboBox</code>
     * @author Jim McLachlan
     * @version 1.0
     */
    class ModelActionListener implements ActionListener
    {
        /**
         * Stores the last selected item to ensure that action only takes
         * place when the list item has been changed.
         */
        private String previousModel = CHOOSE_ONE_STRING;
        
        /**
         * Responds to a selection from the model list.
         * @param e not used
         */
        public void actionPerformed(ActionEvent e)
        {
            String make = (String)makeList.getSelectedItem();
            String model = (String)modelList.getSelectedItem();

            if ((model != null) &&
                (!model.equals(CHOOSE_ONE_STRING)) &&
                (!model.equals(previousModel)))
            {
                previousModel = model;
                
                Device device = WURFLInfo.getInstance().
                                      findDeviceForMakeModel(make, model, true);
                if (device != null)
                {
                    TabbedPaneManager.getInstance().deviceSelected(device);
                }
            }
        }
    }

    /**
     * Listener for the recentList <code>JComboBox</code>
     * @author Jim McLachlan
     * @version 1.0
     */
    class RecentActionListener implements ActionListener
    {
        /**
         * Stores the last selected item to ensure that action only takes
         * place when the list item has been changed.
         */
        private String previousModel = CHOOSE_ONE_STRING;
        
        /**
         * Responds to a selection from the recently selected device list.
         * @param e not used
         */
        public void actionPerformed(ActionEvent e)
        {
            Object selected = recentList.getSelectedItem();
            if (selected instanceof MakeModelDetails)
            {
                String make = ((MakeModelDetails)selected).getMake();
                String model = ((MakeModelDetails)selected).getModel();

                if ((model != null) &&
                    (!model.equals(CHOOSE_ONE_STRING)) &&
                    (!model.equals(previousModel)))
                {
                    previousModel = model;
                
                    Device device = WURFLInfo.getInstance().
                                      findDeviceForMakeModel(make, model, true);
                    if (device != null)
                    {
                        TabbedPaneManager.getInstance().deviceSelected(device);
                    }
                }
            }
        }
    }
}
