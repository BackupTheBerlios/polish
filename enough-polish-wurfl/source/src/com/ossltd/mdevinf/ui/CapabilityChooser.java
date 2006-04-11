/*
 * Class name : CapabilityChooser
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 20-Dec-2005
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * The dialog displaying all available patch files and check boxes to indicate
 * which ones are active.
 * @author Jim McLachlan
 * @version 1.0
 */
public class CapabilityChooser extends JDialog
{
    /** Stores the tree containing the capability selections. */
    private JTree capabilityTree;
    
    /** Flag to indicate that the user changed the data (pressed OK). */
    private boolean changed;
    
    /**
     * Creates and populates the Patch Chooser dialog.
     * @param owner the parent of the dialog.
     * @param currentCaps the names of all capabilites currently being displayed
     */
    public CapabilityChooser(Frame owner, ArrayList<String> currentCaps)
    {
        super (owner, "Capability selection", true);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Please select or deselect the " +
                                   "capabilities from the list below:"));
        Hashtable<String, Group> groups = 
                                   WURFLInfo.getInstance().getGroups("generic");
        ArrayList<String> groupKeys = new ArrayList<String>(groups.keySet());
        Collections.sort(groupKeys);
        Group group = null;
        Hashtable<String, Capability> caps = null;
        ArrayList<String> capKeys = null;
        DefaultMutableTreeNode groupNode = null;
        CapabilityNode capNode = null;
        DefaultMutableTreeNode rootNode =
                                 new DefaultMutableTreeNode("All capabilities");
        
        for (String groupName : groupKeys)
        {
            group = groups.get(groupName);
            groupNode = new DefaultMutableTreeNode(groupName);
            caps = group.getCapabilities();
            capKeys = new ArrayList<String>(caps.keySet());
            Collections.sort(capKeys);
            for (String capName : capKeys)
            {
                capNode = new CapabilityNode(capName,
                                             currentCaps.contains(capName));
                groupNode.add(capNode);
            }
            
            rootNode.add(groupNode);
        }
        
        capabilityTree = new JTree(rootNode);
        capabilityTree.setCellRenderer(new CapabilityRenderer());
        capabilityTree.getSelectionModel().
                     setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        capabilityTree.setRootVisible(false);
        capabilityTree.addMouseListener(
                                     new NodeSelectionListener(capabilityTree));

        JPanel capabilityPanel = new JPanel(new BorderLayout());
        capabilityPanel.setBackground(Color.white);
        capabilityPanel.add(capabilityTree, BorderLayout.CENTER);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                changed = true;
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                changed = false;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(capabilityPanel), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        getContentPane().add(mainPanel);

        setSize(new Dimension(400, 600));
        
        setLocationRelativeTo(owner);
    }
    
    /**
     * Accessor
     * @return the user selections
     */
    public ArrayList<String> getSelections()
    {
        ArrayList<String> selections = new ArrayList<String>();
        selections.add("brand_name");
        selections.add("model_name");
        
        TreeModel model = capabilityTree.getModel();
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        int groupCount = root.getChildCount();
        DefaultMutableTreeNode groupNode = null;
        int capCount = 0;
        CapabilityNode capNode = null;
        
        for (int i = 0; i < groupCount; i++)
        {
            groupNode = (DefaultMutableTreeNode)root.getChildAt(i);
            capCount = groupNode.getChildCount();
            for (int j = 0; j < capCount; j++)
            {
                capNode = (CapabilityNode)groupNode.getChildAt(j);
                String text = (String)capNode.getUserObject();
                if ((capNode.isSelected()) && (!selections.contains(text)))
                {
                    selections.add((String)capNode.getUserObject());
                }
            }
        }
        
        return (selections);
    }

    /**
     * Accessor
     * @return the flag that indicates whether the user pressed OK or not
     */
    public boolean isChanged()
    {
        return (changed);
    }
    
    /**
     * Extends the <code>DefaultMutableTreeNode</code> class to include a
     * boolean <b>selected</b> attribute.
     * @author Jim McLachlan
     * @version 1.0
     */
    class CapabilityNode extends DefaultMutableTreeNode
    {
        /** A flag indicating whether this node has been selected. */
        private boolean selected;
        
        /**
         * Creates a CapabilityNode object with the provided text and default
         * values (not selected).
         * @param userObject the text (String) to include in the node 
         */
        public CapabilityNode(Object userObject)
        {
            this(userObject, false);
        }
        
        /**
         * Creates a CapabilityNode object with the provided values
         * @param userObject the text (String) to include in the node
         * @param selected a flag indicating whether this node should be marked
         * as selected or not
         */
        public CapabilityNode(Object userObject,
                              boolean selected)
        {
            super(userObject, false);
            this.selected = selected;
        }
        
        /**
         * Mutator
         * @param selected a flag indicating the selection status of this node
         */
        public void setSelected(boolean selected)
        {
            this.selected = selected;
        }
        
        /**
         * Accessor
         * @return the flag indicating the selection status of this node
         */
        public boolean isSelected()
        {
            return (selected);
        }
    }
    
    /**
     * Provides a renderer for tree nodes that includes a check box in addition
     * to the text.
     * @author Jim McLachlan
     * @version 1.0
     */
    class CapabilityRenderer extends JPanel implements TreeCellRenderer
    {
        /** Check box to mark the item selected or not. */
        private JCheckBox checkbox;
        
        /** The text label to display. */
        private TreeLabel label;
        
        /**
         * The default renderer to use if not displaying a
         * <code>Capability</code>.
         */
        private DefaultTreeCellRenderer defaultRenderer;
        
        /** Constructor to initialise the object. */
        public CapabilityRenderer()
        {
            setLayout(null);
            checkbox = new JCheckBox();
            label = new TreeLabel();
            add(checkbox);
            add(label);
            checkbox.setBackground(UIManager.getColor("Tree.textBackground"));
            
            defaultRenderer = new DefaultTreeCellRenderer();
        }
        
        /**
         * Overridden method to return the appropriate component for rendering
         * the node.
         * @param tree the tree in which the node appears
         * @param value the object stored for this node
         * @param isSelected a flag indicating whether this node is selected
         * @param expanded a flag indicating whether this node is expanded
         * @param leaf a flag indicating whether this node is a leaf or has
         * children
         * @param row the position of this node in the tree
         * @param hasFocus a flag indicating whether this node has the current
         * focus
         * @return Component the component used to render this node
         */
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean isSelected,
                                                      boolean expanded,
                                                      boolean leaf, 
                                                      int row, 
                                                      boolean hasFocus)
        {
            Component result = null;
            
            if (value instanceof CapabilityNode)
            {
                String stringValue = tree.convertValueToText(value,
                                                             isSelected,
                                                             expanded, 
                                                             leaf, 
                                                             row, 
                                                             hasFocus);
                setEnabled(tree.isEnabled());
                checkbox.setSelected(((CapabilityNode)value).isSelected());
                label.setFont(tree.getFont());
                String mappedName = null;
                
                if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
                {
                    mappedName = ConfigurationManager.getInstance().
                                        getCapabilityNameMap().get(stringValue);
                }
                
                if (mappedName != null)
                {
                    label.setText(stringValue + " (" + mappedName + ")");
                }
                else
                {
                    label.setText(stringValue);
                }
                label.setSelected(isSelected);
                label.setFocus(hasFocus);
                if (leaf)
                {
                    label.setIcon(UIManager.getIcon("Tree.leafIcon"));
                }
                else if (expanded)
                {
                    label.setIcon(UIManager.getIcon("Tree.openIcon"));
                }
                else 
                {
                    label.setIcon(UIManager.getIcon("Tree.closedIcon"));
                }
                
                result = this;
            }
            else
            {
                result = defaultRenderer.getTreeCellRendererComponent(tree,
                                                                     value,
                                                                     isSelected,
                                                                     expanded,
                                                                     leaf,
                                                                     row,
                                                                     hasFocus);
            }
            
            return (result);
        }
        
        /**
         * Accessor
         * @return the preferred size of the component to render
         */
        public Dimension getPreferredSize()
        {
            Dimension d_check = checkbox.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            
            Dimension result = null;
            
            if (d_check.height < d_label.height)
            {
                result = new Dimension(d_check.width + d_label.width,
                                       d_label.height);
            }
            else
            {
                result = new Dimension(d_check.width + d_label.width,
                                       d_check.height);
            }
            
            return (result);
        }
        
        /**
         * Performs the necessary calculations to position the items in the
         * rendered component.
         */
        public void doLayout()
        {
            Dimension d_check = checkbox.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height)
            {
                y_check = (d_label.height - d_check.height) / 2;
            }
            else
            {
                y_label = (d_check.height - d_label.height) / 2;
            }
            
            checkbox.setLocation(0, y_check);
            checkbox.setBounds(0, y_check, d_check.width, d_check.height);
            label.setLocation(d_check.width, y_label);
            label.setBounds(d_check.width, y_label,
                            d_label.width, d_label.height);
        }

        /**
         * Mutator
         * @param colour the colour to set the background
         */
        public void setBackground(Color colour)
        {
            if (colour instanceof ColorUIResource)
            {
                colour = null;
            }
            
            super.setBackground(colour);
        }

        /**
         * An inner class to manage the display of the rendered component.
         * @author Jim McLachlan
         * @version 1.0
         */
        class TreeLabel extends JLabel
        {
            /** A flag indicating whether this node is selected or not. */
            boolean isSelected;
            
            /** A flag indicating whether this node has the current focus. */
            boolean hasFocus;

            /**
             * Mutator
             * @param colour the colour to set the background
             */
            public void setBackground(Color colour)
            {
                if(colour instanceof ColorUIResource)
                {
                    colour = null;
                }
                
                super.setBackground(colour);
            }

            /**
             * Paints the component, highlighting where necessary for selection
             * or focus.
             * @param g the graphics context on which to paint the items.
             */
            public void paint(Graphics g)
            {
                String str;
                if ((str = getText()) != null)
                {
                    if (0 < str.length())
                    {
                        if (isSelected)
                        {
                            g.setColor(UIManager.
                                          getColor("Tree.selectionBackground"));
                        }
                        else 
                        {
                            g.setColor(UIManager.
                                               getColor("Tree.textBackground"));
                        }
                        
                        Dimension d = getPreferredSize();
                        int imageOffset = 0;
                        Icon currentI = getIcon();
                        if (currentI != null)
                        {
                            imageOffset = currentI.getIconWidth() +
                                          Math.max(0, getIconTextGap() - 1);
                        }
                        
                        g.fillRect(imageOffset, 0,
                                   d.width -1 - imageOffset, d.height);
                        if (hasFocus)
                        {
                            g.setColor(UIManager.
                                         getColor("Tree.selectionBorderColor"));
                            g.drawRect(imageOffset, 0,
                                       d.width -1 - imageOffset, d.height -1);
                        }
                    }
                }
                
                super.paint(g);
            }
            
            /**
             * Mutator
             * @return the preferred size of the component to render
             */
            public Dimension getPreferredSize()
            {
                Dimension retDimension = super.getPreferredSize();
                if (retDimension != null)
                {
                    retDimension = new Dimension(retDimension.width + 3,
                                                 retDimension.height);
                }
                
                return retDimension;
            }

            /**
             * Mutator
             * @param isSelected flag indicating whether this node is selected
             */
            public void setSelected(boolean isSelected)
            {
                this.isSelected = isSelected;
            }

            /**
             * Mutator
             * @param hasFocus flag indicating whether this component has the
             * current focus
             */
            public void setFocus(boolean hasFocus)
            {
                this.hasFocus = hasFocus;
            }
        }
    }
    
    /**
     * An extended MouseAdapter used to listen for mouse clicks on tree nodes. 
     * @author Jim McLachlan
     * @version 1.0
     */
    class NodeSelectionListener extends MouseAdapter
    {
        /** Reference to the tree. */
        private JTree tree;

        /**
         * Constructor that initialises the tree reference.
         * @param tree the tree to which the listener is being applied 
         */
        NodeSelectionListener(JTree tree)
        {
            this.tree = tree;
        }

        /**
         * Event handline method that responds to mouse clicks, changing the
         * selected node where appropriate.
         * @param e the MouseEvent that triggered the call to this method.
         */
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            
            if (path != null)
            {
                Object lastComponent = path.getLastPathComponent();
                if (lastComponent instanceof CapabilityNode)
                {
                    CapabilityNode node = (CapabilityNode)lastComponent;
                    boolean isSelected = ! (node.isSelected());
                    node.setSelected(isSelected);
                    
                    ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
                    if (row == 0)
                    {
                        tree.revalidate();
                        tree.repaint();
                    }
                }
            }
        }
    }
}
