/*
 * Created on 05-Jan-2006 at 16:57:16.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.devices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.enough.polish.Device;

/**
 * <p>Maps a vendor, the "virtual category" or a device to the DeviceTree.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        05-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.devices.DeviceTree
 */
public class DeviceTreeItem {
	
	private boolean isSelected;
	private String name;
	private final Vendor vendor;
	private final Device device;
	private final List children;
    private DeviceTreeItem parentItem;
//    private LinkedList selectionListeners;
//    public interface SelectionListener{
//        //
//    }
    
	/**
	 * Creates a new tree item.
	 * 
	 * @param parentItem the parent of this item, can be null
	 * @param vendor the current vendor, can be null
	 * @param device the mapped device, can be null
	 */
	public DeviceTreeItem(DeviceTreeItem parentItem, Vendor vendor, Device device ) {
        this.parentItem = parentItem;
		this.children = new ArrayList();
		this.vendor = vendor;
		this.device = device;
		if (device != null) {
			if (vendor != null) {
				this.name = device.getName();
			} else {
				this.name = device.getIdentifier();
			}
		} else if (vendor != null ) {
			this.name = vendor.getIdentifier();
		} else {
			this.name = "Virtual";
		}
//        this.selectionListeners = new LinkedList();
	}
	
	public DeviceTreeItem[] getChildren() {
		return (DeviceTreeItem[]) this.children.toArray( new DeviceTreeItem[ this.children.size() ] );
	}
	
	public void setIsSelected( boolean selected ) {
        
        setIsSelectedOnItemOnly(selected);
        
		DeviceTreeItem[] childs = getChildren();
		for (int i = 0; i < childs.length; i++) {
			DeviceTreeItem item = childs[i];
			item.setIsSelectedOnItemAndChildren(selected);
		}
        
		// Uncheck the parent if we become unchecked as the parent can only be checked
        // if every child is checked. 
		if(selected == false) {
            DeviceTreeItem parent = getParentItem();
            if(parent != null) {
                parent.setIsSelectedOnItemAndParent(selected);
            }
        }
	}
	
    private void setIsSelectedOnItemAndParent(boolean selected) {
        setIsSelectedOnItemOnly(selected);
        DeviceTreeItem parent = getParentItem();
        if(parent != null) {
            parent.setIsSelectedOnItemAndParent(selected);
        }
    }
    
    private void setIsSelectedOnItemAndChildren(boolean selected) {
        setIsSelectedOnItemOnly(selected);
        DeviceTreeItem[] childrenTemp = getChildren();
        for (int i = 0; i < childrenTemp.length; i++) {
            DeviceTreeItem item = childrenTemp[i];
            item.setIsSelectedOnItemAndChildren(selected);
        }
    }
    
	/**
     * Marks this item as selected but does not alter the state of its children.
     * @param selected true if this item is selected, false otherwise.
     */
    private void setIsSelectedOnItemOnly(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
		return this.isSelected;
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean isVendor() {
		return this.device == null && this.vendor != null;
	}
	
	public boolean isDevice() {
		return this.device != null;
	}
	
	public Vendor getVendor() {
		return this.vendor;
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public String getDescription() {
		if (isVendor()) {
			return this.vendor.getDescription();
		} else if (isDevice()) {
			return this.device.getDescription();
		} else {
			return "Contains virtual devices that represent typical groups of devices.";
		}
	}
	
	protected void addChild( DeviceTreeItem child ) {
		this.children.add( child );
	}

    /**
     * Returns the parent item of this item.
     * @return the parent this item or null if is has none.
     */
    public DeviceTreeItem getParentItem() {
        return this.parentItem;
    }

    /**
     * Sets a new parent of this item.
     * @param parentItem the new parent of this item or null it it has none.
     */
    public void setParentItem(DeviceTreeItem parentItem) {
        this.parentItem = parentItem;
    }
	
//    private void fireSelectionChange() {
//        for (Iterator iterator = this.selectionListeners.iterator(); iterator.hasNext(); ) {
//            SelectionListener selectionListener = (SelectionListener) iterator.next();
//            selectionListener.
//        }
//    }
//	
//    // TODO: Use the generic EventManager.
//    public void addSelectionListener(SelectionListener selectionListener) {
//        this.selectionListeners.add(selectionListener);
//    }
//    
//    public void removeSelectionListener(SelectionListener selectionListener) {
//        this.selectionListeners.remove(selectionListener);
//    }

}
