/*
 * Class name : RelPanelManager
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.WURFLInfo;
import com.ossltd.mdevinf.model.WURFLTree;

/**
 * Manages the layout and updates to the Relationships tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class RelPanelManager
{
    /** Stores a reference to the singleton instance of this class. */
    private static RelPanelManager instance = null;
    
    /** Stores a reference to the Relationships <code>JPanel</code>. */
    private JPanel relPanel;
    
    /** Stores the reference to the tree used to display the devices. */
    private JTree wurflTree;
    
    /** Popup menu for the Relationships tree. */
    private JPopupMenu relationshipPopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    // The following two routines were sourced from The Java Almanac:
    //   http://javaalmanac.com/egs/javax.swing.tree/ExpandAll.html
    // I didn't like the coding style, so I've changed it to match the rest
    //  of this source code.
    
    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    /**
     * Initiates the recursive expansion or collapsing of nodes in the provided
     * tree.
     * @param tree the tree to expand or collapse
     * @param expand a flag indicating whether the nodes should be expanded
     * (<code>true</code>>) or collapsed (<code>false</code>)
     */
    public void expandAll(JTree tree, boolean expand)
    {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
    
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }
    
    /**
     * Recursively called to expand or collapse of nodes in the provided tree.
     * @param tree the tree to expand or collapse
     * @param parent the node whose children should be expanded or collapsed.
     * @param expand a flag indicating whether the nodes should be expanded
     * (<code>true</code>>) or collapsed (<code>false</code>)
     */
    private void expandAll(JTree tree, TreePath parent, boolean expand)
    {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        Enumeration<?> nodeList = node.children();
        if (nodeList != null)
        {
            while (nodeList.hasMoreElements())
            {
                TreeNode n = (TreeNode)nodeList.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
    
        // Expansion or collapse must be done bottom-up
        if (expand)
        {
            tree.expandPath(parent);
        }
        else
        {
            // Don't expand or collapse the root node.
            if (parent.getPath().length > 1)
            {
                tree.collapsePath(parent);
            }
        }
    }

    /**
     * Creates the popup menu for the Relationships panel. 
     */
    private void createPopup()
    {
        JMenuItem showOverrides = new JMenuItem(new OverrideAction());
        JMenuItem expandAll = new JMenuItem("Expand All Relationships");
        expandAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                expandAll(true);
            }
        });
        
        JMenuItem collapseAll = new JMenuItem("Collapse All Relationships");
        collapseAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                expandAll(false);
            }
        });
        
        JMenuItem compDevice = new JMenuItem("Compare device");
        compDevice.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CompPanelManager.getInstance().addDevice();
            }
        });
        
        JMenuItem editDevice = null;
        JMenuItem createDevice = null;
        
        boolean runningLocally =
                           (WURFLInfo.getInstance().getMDevInfWSIcon() == null);
        if (runningLocally)
        {
            editDevice = new JMenuItem(new EditAction());
            editDevice.setToolTipText("Edit the currently selected device");
            createDevice = new JMenuItem(new ExtendAction());
            createDevice.setToolTipText("Create a device using the current " +
                                        "as a template");
        }
        
        relationshipPopupMenu = new JPopupMenu();
        
        relationshipPopupMenu.add(showOverrides);
        relationshipPopupMenu.addSeparator();
        relationshipPopupMenu.add(expandAll);
        relationshipPopupMenu.add(collapseAll);
        
        if (runningLocally)
        {
            relationshipPopupMenu.addSeparator();
            relationshipPopupMenu.add(editDevice);
            relationshipPopupMenu.add(createDevice);
        }
        
        relationshipPopupMenu.addSeparator();
        relationshipPopupMenu.add(compDevice);
    }

    /**
     * Creates the Relationships panel.
     */
    private RelPanelManager()
    {
        relPanel = new JPanel(new BorderLayout());
        wurflTree = new JTree(WURFLTree.getInstance().getRoot());
        wurflTree.getSelectionModel().
                     setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        wurflTree.setExpandsSelectedPaths(true);
        
        ImageIcon wurfl16 = null;
        ImageIcon deviceIcon = null;
        ImageIcon noDeviceIcon = null;
        
        java.net.URL imageURL = getClass().getResource("/wurfl16.png");
        if (imageURL != null)
        {
            wurfl16 = new ImageIcon(imageURL);
        }
        imageURL = getClass().getResource("/deviceIcon16.png");
        if (imageURL != null)
        {
            deviceIcon = new ImageIcon(imageURL);
        }
        imageURL = getClass().getResource("/noDeviceIcon16.png");
        if (imageURL != null)
        {
            noDeviceIcon = new ImageIcon(imageURL);
        }

        createPopup();

        wurflTree.setCellRenderer(new DeviceRenderer(wurflTree.getFont(),
                                                     wurfl16,
                                                     deviceIcon,
                                                     noDeviceIcon));
        
        wurflTree.addTreeSelectionListener(new TreeSelectionListener(){
            private DefaultMutableTreeNode previousNode = null;
            
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)wurflTree.
                                                 getLastSelectedPathComponent();
                if ((node != null) &&
                    (node != previousNode))
                {
                    Device device = (Device)node.getUserObject();
                    if (device != null)
                    {
                        TabbedPaneManager.getInstance().deviceSelected(device);
                    }
                }
            }
        });
        
        wurflTree.addMouseListener(new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    relationshipPopupMenu.show(e.getComponent(),
                                               popX,
                                               popY);
                    
                    TreePath closest = wurflTree.
                                          getClosestPathForLocation(popX, popY);
                    
                    if (closest != null)
                    {
                        wurflTree.setSelectionPath(closest);
                    }
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
        });
        
        relPanel.add(new JScrollPane(wurflTree), BorderLayout.CENTER);
    }

    /**
     * Accessor
     * @return a reference to the panel for the Relationships tab
     */
    public JPanel getRelationshipPanel()
    {
        return (relPanel);
    }
    
    /**
     * Sets the selected node on the relationships tree and ensures that the
     * node is visible.
     * @param path the node to be shown on the relationships tree
     */
    public void setTreePath(TreePath path)
    {
        wurflTree.setSelectionPath(path);
        wurflTree.scrollPathToVisible(path);
        TabbedPaneManager.getInstance().
                                markUpdated(TabbedPaneManager.relationship_tab);
    }
    
    /**
     * Expands or collapses all nodes in the relationships tree
     * @param expand flag indicating whether the nodes should be expanded
     * (<code>true</code>) or collapsed.
     */
    public void expandAll(boolean expand)
    {
        expandAll(wurflTree, expand);
    }
    
    /**
     * Find the provided device in the relationship hierarchy and set the path
     * to that device.
     * @param device the device to find in the hierarchy
     */
    public void updateContents(Device device)
    {
        TreePath path = WURFLTree.getInstance().getPath(device);
        
        if (path != null)
        {
            setTreePath(path);
        }
    }
    
    /**
     * Used to removed the singleton instance.
     * <p>This allows complete re-creation of the relationships panel when
     * changes have been made to the WURFL hierarchy.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor
     * @return an singleton instance of this class
     */
    public static RelPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new RelPanelManager();
        }
        
        return (instance);
    }

    /**
     * Action for use in the popup menu on the Relationships tab to allow the
     * user to display the overridden capabilities of a device.
     * @author Jim McLachlan
     * @version 1.0
     */
    class OverrideAction extends AbstractAction
    {
        /** Creates the action with the title "Show overridden capabilities". */
        public OverrideAction()
        {
            super ("Show overridden capabilities");
        }
        
        /**
         * Prepare and display the capabilities that have been overridden.
         * @param e not used
         */
        public void actionPerformed(ActionEvent e)
        {
            TreePath closest = wurflTree.getClosestPathForLocation(popX, popY);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                 closest.getLastPathComponent();
            if (node != null)
            {
                Device dev = (Device)node.getUserObject();
                Hashtable<String, Group> grps = dev.getGroups();
                JTextPane overriddenPane = new JTextPane();
                StyledDocument doc = overriddenPane.getStyledDocument();
                DocStyler.prepareStyledDocument(doc);
                Enumeration<String> grpKeys = grps.keys();
                Group curGrp = null;
                Hashtable<String, Capability> caps = null;
                Enumeration<String> capKeys = null;
                String curCapKey = null;
                
                while (grpKeys.hasMoreElements())
                {
                    curGrp = grps.get(grpKeys.nextElement());
                    caps = curGrp.getCapabilities();
                    try
                    {
                        doc.insertString(doc.getLength(),
                                         curGrp.getGroupID() + "\n",
                                         doc.getStyle("bold"));
                        capKeys = caps.keys();
                        while (capKeys.hasMoreElements())
                        {
                            curCapKey = capKeys.nextElement();
                            doc.insertString(doc.getLength(),
                                             "  " + curCapKey + " = " +
                                             caps.get(curCapKey).getValue() +
                                             "\n",
                                             doc.getStyle("boldBlue"));
                        }
                    }
                    catch (BadLocationException ble)
                    {
                        System.err.println("Error displaying capabilities: " +
                                           ble.toString());
                    }
                }

                overriddenPane.setPreferredSize(new Dimension(400, 200));
                JScrollPane scroller = new JScrollPane(overriddenPane);
                String title = dev.toString();
                if (title.length() < 2)
                {
                    title = dev.getDeviceID();
                }
                JOptionPane.showMessageDialog(relPanel,
                                              scroller,
                                              title,
                                              JOptionPane.
                                              INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Action used to add a new device, based on the currently selected one.
     * @author Jim McLachlan
     * @version 1.0
     */
    class ExtendAction extends AbstractAction
    {
        /**
         * Creates the action
         */
        public ExtendAction()
        {
            super("Extend device");
        }
        
        /**
         * Identifies the currently selected devices, shows the new device
         * dialog (based on the currently selected device), then populates the
         * editor tab appropriately.
         * @param e the event that triggered this action
         */
        public void actionPerformed(ActionEvent e)
        {
            TreePath closest = wurflTree.getClosestPathForLocation(popX, popY);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                 closest.getLastPathComponent();
            if (node != null)
            {
                Device device = (Device)node.getUserObject();
                String fallback = device.getDeviceID();
                // Get basic device details
                // UA string, ID, actual device
                NewDeviceDialog ndd = new NewDeviceDialog(relPanel, fallback);
                ndd.setVisible(true);
                
                // If continuing and details supplied
                if (ndd.isDeviceOK())
                {
                    TabbedPaneManager.getInstance().addEditorTab();
                    EditorPanelManager.getInstance().
                                                  setValuesFor(ndd.getDevice());
                    TabbedPaneManager.getInstance().
                                           setTab(TabbedPaneManager.editor_tab);
                }
            }
        }
    }
}
