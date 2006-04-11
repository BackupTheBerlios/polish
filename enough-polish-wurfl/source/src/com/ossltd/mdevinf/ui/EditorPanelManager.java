/*
 * Class name : EditorPanelManager
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 08-Nov-2005
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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Description;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.PatchManager;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the Editor tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class EditorPanelManager
{
    /** Stores the background colour for overridden values. */
    private static final Color overriddenColour = new Color(0xFF, 0xFF, 0xAA);
    
    /** Stores the background colour for values from patch files. */
    private static final Color patchColour = new Color(0xAA, 0xFF, 0xAA);
    
    /** Stores the default background colour. */
    private static final Color defaultColour = (new JTextField()).
                                                                getBackground();

    /** Stores a reference to the singleton instance of this class. */
    private static EditorPanelManager instance = null;

    /** Stores a reference to the editor panel. */
    private JPanel editorPanel;
    
    /** Stores a reference to the tabbed panes for the groups. */
    private JTabbedPane editTabs;
    
    /** Stores the references to the edit fields. */
    private Hashtable <String, EditField> editFields;
    
    /** Stores the reference to the device currently being edited. */
    private Device device;
    
    /** Popup menu for the Relationships tree. */
    private JPopupMenu editorPopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    /**
     * Creates the popup menu for the Relationships panel. 
     */
    private void createPopup()
    {
        JMenuItem createCapability = new JMenuItem("Create new capability");
        createCapability.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                String group = editTabs.getTitleAt(editTabs.getSelectedIndex());
                NewCapabilityDialog ncd = new NewCapabilityDialog(editorPanel,
                                                                  group);
                ncd.setVisible(true);
                
                if (ncd.isCapabilityOK())
                {
                    PatchManager.getInstance().
                                        addNewCapability(group,
                                                         ncd.getCapability(),
                                                         ncd.getDefaultValue());
                    WURFLInfo.getInstance().
                                           addCapability(group,
                                                         ncd.getCapability(),
                                                         ncd.getDefaultValue());
                    TabbedPaneManager.getInstance().removeEditorTab();
                    TabbedPaneManager.getInstance().addEditorTab();
                    EditorPanelManager.getInstance().setValuesFor(device);
                    TabbedPaneManager.getInstance().
                                           setTab(TabbedPaneManager.editor_tab);
                }
            }
        });
        
        editorPopupMenu = new JPopupMenu();
        
        editorPopupMenu.add(createCapability);
    }

    /**
     * Creates the Editor panel
     */
    private EditorPanelManager()
    {
        createPopup();
        MouseAdapter popupMouseListener = new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    JMenuItem menu = (JMenuItem)editorPopupMenu.getComponent(0);
                    menu.setText("Create new capability in " +
                              editTabs.getTitleAt(editTabs.getSelectedIndex()));
                    editorPopupMenu.show(e.getComponent(),
                                         popX,
                                         popY);
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
        
        editorPanel = new JPanel();
        
        editFields = new Hashtable<String, EditField>();
        
        editTabs = new JTabbedPane();
        
        Hashtable<String, Group> wurflGroups = WURFLInfo.getInstance().
                                                           getGroups("generic");
        Enumeration<String> groupNames = wurflGroups.keys();
        ArrayList<String> sortedNames = new ArrayList<String>();
        while (groupNames.hasMoreElements())
        {
            sortedNames.add(groupNames.nextElement());
        }

        Collections.sort(sortedNames);

        Hashtable<String, Capability> caps = null;
        Description desc = null;

        for (final String groupName : sortedNames)
        {
            caps = wurflGroups.get(groupName).getCapabilities();
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
                JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.
                                                                     TRAILING));
                labelPanel.add(new JLabel(curKey));
                desc = WURFLInfo.getInstance().getDescription(curKey);
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

                // If we don't know what type it is, it's better to allow
                //  alphanumeric input.
                EditField.field_type fieldType =
                                               EditField.field_type.alpha_field;
                if (desc != null)
                {
                    if (desc.getType().equals("true/false"))
                    {
                        fieldType = EditField.field_type.boolean_field;
                    }
                    else if (desc.getType().equals("integer"))
                    {
                        fieldType = EditField.field_type.numeric_field;
                    }
                }

                JPanel editPanel = new JPanel();
                editPanel.setLayout(new BoxLayout(editPanel,
                                                  BoxLayout.LINE_AXIS));
                EditField eField = new EditField(groupName, fieldType);
                editFields.put(curKey, eField);
                editPanel.add(eField.getValueField());
                
                c.gridy = row;
                c.gridx = col + 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                capsPanel.add(editPanel, c);
                
                row++;
                if (row >= (numCaps >> 1))
                {
                    row = 0;
                    col = 3;
                }
            }
            
            editTabs.addTab(groupName, new JScrollPane(capsPanel));
            
            capsPanel.addMouseListener(popupMouseListener);
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                JTextPane dataToCommit = new JTextPane();
                StyledDocument commitDoc = dataToCommit.getStyledDocument();
                DocStyler.prepareStyledDocument(commitDoc);
                Hashtable<String, Group> groups = 
                                                 new Hashtable<String, Group>();
                try
                {
                    commitDoc.insertString(
                              commitDoc.getLength(),
                              "The following capabilities have been updated:\n",
                              commitDoc.getStyle("boldBlue"));
                    int count = 0;
                    Enumeration<String> editKeys = editFields.keys();
                    String curKey = null;
                    EditField curField = null;
                    Capability cap = null;
                    Group curGroup = null;
                    while (editKeys.hasMoreElements())
                    {
                        curKey = editKeys.nextElement();
                        curField = editFields.get(curKey);
                        if (curField.isUpdated())
                        {
                            commitDoc.insertString(
                                               commitDoc.getLength(),
                                               curKey + " = " +
                                               curField.getValueAsText() +
                                               "\n",
                                               commitDoc.getStyle("boldGreen"));
                            
                            curGroup = groups.get(curField.getGroup());
                            if (curGroup == null)
                            {
                                curGroup = new Group(curField.getGroup());
                                groups.put(curField.getGroup(), curGroup);
                            }
                            
                            cap = curGroup.getCapabilities().get(curKey);
                            if (cap == null)
                            {
                                cap = new Capability(curField.getValueAsText(),
                                                     true, true);
                                curGroup.getCapabilities().put(curKey, cap);
                            }
                            else
                            {
                                cap.setValue(curField.getValueAsText());
                                cap.setOverridden(true);
                                cap.setPatched(true);
                                cap.setEdited(true);
                            }
                            
                            count++;
                        }
                    }
                    
                    if (count == 0)
                    {
                        commitDoc.insertString(commitDoc.getLength(),
                                               "None\n",
                                               commitDoc.getStyle("boldGreen"));
                    }
                }
                catch (BadLocationException ble)
                {
                    System.err.println("Couldn't display the commit edits" +
                                       ble.toString());
                }
                
                int result = JOptionPane.showConfirmDialog(
                                               editorPanel,
                                               new JScrollPane(dataToCommit),
                                               "Confirm changes",
                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION)
                {
                    PatchManager.getInstance().addCapabilities(device,
                                                               groups);
                    if (PatchManager.getInstance().outputPatchFile())
                    {
                        TabbedPaneManager.getInstance().removeEditorTab();
                        TabbedPaneManager.getInstance().updateAfterChanges();
                    }
                }
                else if (result == JOptionPane.NO_OPTION)
                {
                    TabbedPaneManager.getInstance().setTab(
                                                  TabbedPaneManager.device_tab);
                    TabbedPaneManager.getInstance().removeEditorTab();
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                TabbedPaneManager.getInstance().removeEditorTab();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(editTabs, BorderLayout.CENTER);
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Accessor
     * @return a reference to the Editor panel
     */
    public JPanel getEditorPanel()
    {
        return (editorPanel);
    }
    
    /**
     * Sets up the data in the editing tabs for the specified device.
     * @param device the <code>Device</code> to use to populate the tabs
     */
    public void setValuesFor(Device device)
    {
        this.device = device;
        Hashtable<String, Capability> caps =
                                            new Hashtable<String, Capability>();
        
        caps = WURFLInfo.getInstance().getAllCapabilitiesFor(device, caps);
        Enumeration<String> capKeys = caps.keys();
        String curKey = null;
        Capability curCap = null;
        EditField curField = null;
        JComponent comp = null;
        
        while (capKeys.hasMoreElements())
        {
            curKey = capKeys.nextElement();
            curCap = caps.get(curKey);
            curField = editFields.get(curKey);
            if (curField == null)
            {
                System.err.println("For the selected device, the capability " +
                                   curKey + " appears to be \"null\"");
            }
            else
            {
                curField.reset();
            }
            comp = curField.getValueField();

            if (curCap.isEdited())
            {
                comp.setBackground(EditField.updatedColour);
            }
            else if (curCap.isPatched())
            {
                comp.setBackground(patchColour);
            }
            else if (curCap.isOverridden())
            {
                comp.setBackground(overriddenColour);
            }
            else
            {
                comp.setBackground(defaultColour);
            }

            curField.setValue(curCap.getValue());
        }
    }
    
    /**
     * Used to removed the singleton instance.
     * <p>This allows complete re-creation of the editor panel for each
     * use.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor
     * @return the singeton instance of this class
     */
    public static EditorPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new EditorPanelManager();
        }
        
        return (instance);
    }
}
