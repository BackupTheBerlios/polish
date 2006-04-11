/*
 * Class name : WURFLTree
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 31-Oct-2005
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

package com.ossltd.mdevinf.model;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ossltd.mdevinf.MDevInfException;

/**
 * Manages the content of the WURFL device tree.
 * @author Jim McLachlan
 * @version 1.0
 */
public class WURFLTree
{
    /** Stores the singleton reference to this class. */
    private static WURFLTree instance = null;
    
    /** Stores the root node of the tree. */
    private DefaultMutableTreeNode root;
    
    /**
     *  A list of <code>Device</code>s whose fallback <code>Device</code> is
     * forward referenced and cannot be added to the tree until later.
     */
    private Vector<Device> orphans;

    /**
     * A recursive method used to find the location in the tree to which a
     * node should be added.
     * <p>When the current node's device ID matches the provided fallback, then
     * the current node must be the parent of the node to be added.</p>
     * @param curNode the node being checked to see if it is the parent
     * @param fallback the string that is used to identify the parent node
     * @return Either the identified parent node or <code>null</code>.
     */
    private DefaultMutableTreeNode findParent(DefaultMutableTreeNode curNode,
                                              String fallback)
    {
        DefaultMutableTreeNode result = null;
        
        Device dev = (Device)curNode.getUserObject();
        if (dev.getDeviceID().equals(fallback))
        {
            result = curNode;
        }
        else
        {
            int children = curNode.getChildCount();
            int curChild = 0;
            while ((curChild < children) && (result == null))
            {
                result = findParent(
                           (DefaultMutableTreeNode)curNode.getChildAt(curChild),
                           fallback);
                curChild++;
            }
        }
        
        return (result);
    }
    
    /**
     * Private constructor to preserve the singleton integrity.
     * <p>Initialises the <code>root</code> and <code>orphans</code> attributes.
     */
    private WURFLTree()
    {
        root = null;
        orphans = new Vector<Device>();
    }
    
    /**
     * Attempts to add the provided <code>Device</code> into the tree.
     * <p>If no parent can be found for the device, it is added to the
     * orphans list.  This list contains all the devices for whom a parent
     * node (fallback) could not be found.  This is usually because the parent
     * node is being forward-referenced (ie. the parent node hasn't been added
     * yet).</p>
     * <p>If the device is successfully added to the tree, but appears in the
     * <code>orphans</code> list, then it is removed from that list.</p>
     * @param device the <code>Device</code> to add to the tree.
     */
    public void addNode(Device device)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(device);

        if (device.getDeviceID().equals("generic"))
        {
            root = node;
        }
        else
        {
            if (root != null)
            {
                DefaultMutableTreeNode parent =
                                       findParent(root, device.getFallbackID());
                if (parent == null)
                {
                    orphans.add(device);
                }
                else
                {
                    parent.add(node);
                    orphans.remove(device);
                }
            }
            else
            {
                System.err.println("'generic' node not found!");
            }
        }
    }
    
    /**
     * Repeatedly attempts to add the orphan <code>Device</code>s to the tree
     * until either:
     * <ol>
     * <li>They are all added</li>
     * or
     * <li>No devices left in the list were successfully added.
     * </ol>
     * <p>If there are devices left in the list, an error is reported to the
     * standard error device.</p>
     * @throws MDevInfException if a problem occurs whilst fitting the devices
     * into the tree
     */
    public void manageOrphans() throws MDevInfException
    {
        int previousOrphansSize = -1;
        
        Vector<Device> orphansCopy = null;
        
        while ((orphans.size() > 0) &&
               (previousOrphansSize != orphans.size()))
        {
            previousOrphansSize = orphans.size();
            
            orphansCopy = new Vector<Device>();

            for (Device orphan : orphans)
            {
                orphansCopy.add(orphan);
            }
            
            orphans = new Vector<Device>();
            
            for (Device orphan : orphansCopy)
            {
                addNode(orphan);
            }
        }
        
        if (orphans.size() > 0)
        {
            StringBuffer error = new StringBuffer("Failed to load ");
            error.append(orphans.size());
            error.append(" devices.\n\nThis usually indicates that a device ");
            error.append("has an invalid 'fall_back' defined in ");
            error.append("the wurfl.xml file.\n\n");
            int toDisplay = 3;
            if (orphans.size() > 3)
            {
                error.append("The first three devices in the list are:\n");
            }
            else
            {
                error.append("The following devices could not be added:\n");
                toDisplay = orphans.size();
            }
            for (int i = 0; i < toDisplay; i++)
            {
                error.append("     Device ID: ");
                error.append(orphans.get(i).getDeviceID());
                error.append("\n");
            }
            throw (new MDevInfException(error.toString()));
        }
    }
    
    /**
     * Accessor
     * @return the root node of the tree
     */
    public DefaultMutableTreeNode getRoot()
    {
        return (root);
    }

    /**
     * Recursive method that identifies the nodes that form the path to node
     * containing the specified text.
     * @param toFind the device to find
     * @param curNode the node containing the child nodes that are currently
     * being searched.
     * @return the array of nodes that form the path to the target node.
     */
    public DefaultMutableTreeNode[] getPathTo(Device toFind,
                                              DefaultMutableTreeNode curNode)
    {
        DefaultMutableTreeNode[] result = null;

        int childCount = curNode.getChildCount();
        int curChild = 0;
        while((result == null) && (curChild < childCount))
        {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)curNode.
                                                           getChildAt(curChild);
            if (toFind.equals(childNode.getUserObject()))
            {
                TreeNode[] find = ((DefaultMutableTreeNode)curNode.
                                                getChildAt(curChild)).getPath();
                result = new DefaultMutableTreeNode[find.length];
                for (int i = 0; i < find.length; i++)
                {
                    result[i] = (DefaultMutableTreeNode)find[i];
                }
            }
            else
            {
                result = getPathTo(toFind,
                                   (DefaultMutableTreeNode)curNode.
                                                          getChildAt(curChild));
            }
            
            curChild++;
        }
        
        return (result);
    }
    
    /**
     * Calls the recursive method <code>getPathTo</code> to find the node
     * containing the provided string and returns the result as a
     * <code>TreePath</code>.
     * @param nodeToFind the device to find in the node 
     * @return The <code>TreePath</code> to the node.
     */
    public TreePath getPath(Device nodeToFind)
    {
        TreePath path = null;
        if (nodeToFind.getFallbackID().equals("root"))
        {
            path = new TreePath(root.getPath());
        }
        else
        {
            path = new TreePath(getPathTo(nodeToFind, root));
        }
        
        return (path);
    }
    
    /**
     * Accessor
     * @return the reference to the singleton instance of this class.
     */
    public static WURFLTree getInstance()
    {
        if (instance == null)
        {
            instance = new WURFLTree();
        }
        
        return (instance);
    }
}
