/*
 * Created on Sep 7, 2006 at 2:58:20 PM.
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
package de.enough.mepose.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.enough.mepose.core.model.DeviceDatabaseException;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.ui.MeposeUIPlugin;
import de.enough.polish.Device;
import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.DeviceTreeItem;
import de.enough.polish.devices.Platform;

/**
 * This manager has the following responsibilites:
 * <ul>
 * <li>Create a visual representation for choosing a device.
 * <li>Maintain a state of the process of choosing a device. First set defaults,
 * then let the values be changed. Then accept or cancel all changes.
 * <li>Map DeviceTreeItem an TreeItem objects and keep them consistent.
 * <li>Maintain and check the state of selected configurations, platforms and devices.
 * <li>Manage user actions like selecting/unselecting an item. The user can
 * select/unselect a platform,configuration and devices. Accept changes which stores
 * the results in the model. Cancel the changes and leave the model unchanged.
 * <li>Set library paths when the supported devices are selected and accepted.
 * <li>Maintain a current device in respect to the currently supported devices.
 * </ul>
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Sep 7, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ChooseDeviceManager {

    // GUI elements.
    
    private Composite main;

    private Tree configurationsTree;
    private Tree platformTree;
    private Tree possibleDevicesTree;
    private Label descriptionLabel;

    // Model elements.
    
    private MeposeModel model;
    private DeviceTree deviceTreeModel;

    private DeviceSelectionManager deviceSelectionManager;
    
    protected class DeviceSelectionManager implements PropertyChangeListener,DisposeListener,SelectionListener{
        
        private Map treeItemToDeviceTreeItemMap;
        
        public DeviceSelectionManager() {
            this.treeItemToDeviceTreeItemMap = new HashMap();
        }
        
        public void addTreeItem(TreeItem treeItem) {
            DeviceTreeItem deviceTreeItem = (DeviceTreeItem)treeItem.getData();
            deviceTreeItem.addSelectionListener(this);
            this.treeItemToDeviceTreeItemMap.put(deviceTreeItem,treeItem);
            treeItem.addDisposeListener(this);
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            DeviceTreeItem deviceTreeItem = (DeviceTreeItem)evt.getSource();
            TreeItem treeItem = (TreeItem)this.treeItemToDeviceTreeItemMap.get(deviceTreeItem);
            treeItem.setChecked(deviceTreeItem.isSelected());
        }

        public void widgetDisposed(DisposeEvent e) {
            TreeItem treeItem = (TreeItem)e.widget;
            this.treeItemToDeviceTreeItemMap.remove(treeItem.getData());
            DeviceTreeItem deviceTreeItem = (DeviceTreeItem)treeItem.getData();
            deviceTreeItem.removeSelectionListener(this);
        }

        public void widgetSelected(SelectionEvent e) {
            if(e.detail != SWT.CHECK) {
                return;
            }
            TreeItem treeItem = (TreeItem)e.item;
            DeviceTreeItem deviceTreeItem = (DeviceTreeItem)treeItem.getData();
            deviceTreeItem.setIsSelected(treeItem.getChecked());
            checkState();
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            // not needed.
        }
        public void reset() {
            Collection deviceTreeItems = this.treeItemToDeviceTreeItemMap.keySet();
            for (Iterator iterator = deviceTreeItems.iterator(); iterator.hasNext(); ) {
                DeviceTreeItem deviceTreeItem = (DeviceTreeItem) iterator.next();
                deviceTreeItem.removeSelectionListener(this);
            }
            this.treeItemToDeviceTreeItemMap.clear();
        }
    }
    
    protected class PlatfAndConfSelectionChangeListener extends SelectionAdapter{
        public void widgetSelected(SelectionEvent e) {
            fillDeviceTable(null);
        }
    }
    
    protected class DescriptionDumper implements SelectionListener{
        
        public void widgetSelected(SelectionEvent e) {
            TreeItem item = (TreeItem)e.item;
            Object data = item.getData();
            if(data instanceof DeviceTreeItem) {
                DeviceTreeItem deviceTreeItem = (DeviceTreeItem)item.getData();
                setDescription(deviceTreeItem.getDescription());
            }
            else if (data instanceof Configuration){
                Configuration configuration = (Configuration)item.getData();
                setDescription(configuration.getDescription());
            }
            else if (data instanceof Platform){
                Platform platform = (Platform)item.getData();
                setDescription(platform.getDescription());
            }
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            //
        }
    }

    protected class DeviceSelectionChangeListener extends SelectionAdapter{
        public void widgetSelected(SelectionEvent e) {
            if(e.detail != SWT.CHECK) {
                return;
            }
            TreeItem treeItem = (TreeItem)e.item;
            DeviceTreeItem deviceTreeItem = (DeviceTreeItem)treeItem.getData();
            deviceTreeItem.setIsSelected(treeItem.getChecked());
        }
    }
    
    
    // #########################################################################
    // CONSTRUCTOR
    
    
    public ChooseDeviceManager(Composite parent, MeposeModel model) {
        this.model = model;
        DeviceDatabase deviceDatabase;
        try {
            deviceDatabase = this.model.getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            MeposeUIPlugin.log("Could not get DeviceDatabase from MeposeModel");
            return;
        }
        this.deviceSelectionManager = new DeviceSelectionManager();
        this.main = createControls(parent);
        this.deviceTreeModel = new DeviceTree(deviceDatabase,getSelectedConfigurations(),getSelectedPlatforms());
        fillDeviceTable(this.model.getSupportedDevices());
    }

    
    // #########################################################################
    // MODEL
    
    protected Configuration[] getAllConfigurations() {
        DeviceDatabase deviceDatabase;
        try {
            deviceDatabase = this.model.getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            return new Configuration[0];
        }
        if(deviceDatabase == null) {
            return new Configuration[0];
        }
        return deviceDatabase.getConfigurationManager().getConfigurations();
    }
    
    protected Platform[] getAllPlatforms() {
        DeviceDatabase deviceDatabase = null;
        try {
            deviceDatabase = this.model.getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            return new Platform[0];
        }
        if(deviceDatabase == null) {
            return new Platform[0];
        }
        return deviceDatabase.getPlatformManager().getPlatforms();
    }
    
    
    /**
     * Its not nice that this method relies on GUI trees. There should be only a
     * listener which caches the results of the selection.
     * @return all selected platforms
     */
    protected Platform[] getSelectedPlatforms() {
        TreeItem[] childItems = this.platformTree.getItems();
        LinkedList list = new LinkedList();
        for (int i = 0; i < childItems.length; i++) {
            TreeItem childItem = childItems[i];
            if(childItem.getChecked()) {
                list.add(childItem);
            }
        }
        TreeItem[] checkedItems = (TreeItem[]) list.toArray(new TreeItem[list.size()]);
        int selectionCount = checkedItems.length;
        Platform[] platforms = new Platform[selectionCount];
        for (int i = 0; i < selectionCount; i++) {
            TreeItem item = checkedItems[i];
            platforms[i] = (Platform)item.getData();
        }
        return platforms;
    }
    
    protected Configuration[] getSelectedConfigurations() {
        TreeItem[] childItems = this.configurationsTree.getItems();
        LinkedList list = new LinkedList();
        for (int i = 0; i < childItems.length; i++) {
            TreeItem childItem = childItems[i];
            if(childItem.getChecked()) {
                list.add(childItem);
            }
        }
        TreeItem[] checkedItems = (TreeItem[]) list.toArray(new TreeItem[list.size()]);
        int selectionCount = checkedItems.length;
        Configuration[] configurations = new Configuration[selectionCount];
        for (int i = 0; i < selectionCount; i++) {
            TreeItem item = checkedItems[i];
            configurations[i] = (Configuration)item.getData();
        }
        return configurations;
    }
    
    protected Device[] getSelectedDevices() {
        return this.deviceTreeModel.getSelectedDevices();
    }
    
    protected boolean checkState() {
        Device[] selectedDevices = this.deviceTreeModel.getSelectedDevices();
        if(selectedDevices == null || selectedDevices.length == 0) {
            return false;
        }
        return true;
    }
    
    
    
    // #########################################################################
    // CONTROLLER
    
    // User control cycle management.
    
    public void performCancel() {
        this.deviceSelectionManager.reset();
    }
    
    public void performOk() {
        this.model.setSupportedPlatforms(getSelectedPlatforms());
        this.model.setSupportedConfigurations(getSelectedConfigurations());
        this.model.setSupportedDevices(getSelectedDevices());
        this.model.setDeviceTree(this.deviceTreeModel);
    }
    
    public void performReset() {
        this.deviceSelectionManager.reset();
    }
    
    
    // #########################################################################
    // VIEW

    
    public Control getControl() {
        return this.main;
    }
    
    protected void fillConfigurationTree() {
        TreeItem item;
        Configuration[] configurations;
        configurations = getAllConfigurations();
        for (int i = 0; i < configurations.length; i++) {
            String identifier = configurations[i].getIdentifier();
            item = new TreeItem(this.configurationsTree,SWT.NONE);
            item.setText(identifier);
            item.setData(configurations[i]);
            if(identifier.startsWith("CLDC/1.1")) {
                item.setChecked(true);
            }
        }
        this.configurationsTree.showSelection();
    }

    protected void fillPlatformTree() {
        TreeItem item;
        Platform[] platforms;
        platforms = getAllPlatforms();
        for (int i = 0; i < platforms.length; i++) {
            String identifier = platforms[i].getIdentifier();
            item = new TreeItem(this.platformTree,SWT.NONE);
            item.setText(identifier);
            item.setData(platforms[i]);
            if(identifier.startsWith("MIDP/2.0")) {
                item.setChecked(true);
            }
//            item.setExpanded(false);
        }
        
        this.platformTree.showSelection();
    }
    
    protected void fillDeviceTable(Device[] devices){
        
        this.deviceSelectionManager.reset();
        
        Configuration[] selectedConfigurations = getSelectedConfigurations();
        Platform[] selectedPlatforms = getSelectedPlatforms();
        this.deviceTreeModel.rebuild(selectedConfigurations,selectedPlatforms,devices);
        this.possibleDevicesTree.removeAll();
        DeviceTreeItem[] deviceTreeItems;
        deviceTreeItems = this.deviceTreeModel.getRootItems();
        
        LinkedList nodes = new LinkedList();
        TreeItem treeItem;
        for (int i = 0; i < deviceTreeItems.length; i++) {
            treeItem = new TreeItem(this.possibleDevicesTree,SWT.NONE);
            treeItem.setData(deviceTreeItems[i]);
            this.deviceSelectionManager.addTreeItem(treeItem);
            nodes.add(treeItem);
        }
        
        DeviceTreeItem deviceTreeItem;

        for (int i = 0; i < nodes.size();i++) {
            treeItem = (TreeItem) nodes.get(i);
            deviceTreeItem = (DeviceTreeItem)treeItem.getData();
            treeItem.setText(deviceTreeItem.toString());
            
            deviceTreeItems = deviceTreeItem.getChildren();
            for (int j = 0; j < deviceTreeItems.length; j++) {
                TreeItem childTreeItem = new TreeItem(treeItem,SWT.NONE);
                childTreeItem.setData(deviceTreeItems[j]);
                this.deviceSelectionManager.addTreeItem(childTreeItem);
                nodes.add(childTreeItem);
            }
        }
    }
    
    /**
     * @param parent
     * @return the complete Composite which contains all gui elements.
     */
    private Composite createControls(Composite parent) {
        Composite top = new Composite(parent,SWT.NONE);
        
        top.setLayout(new GridLayout(2,true));
        
        Group configGroup = new Group(top,SWT.NONE);
        configGroup.setText("Configurations");
        configGroup.setLayout(new GridLayout(1,false));
        configGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        PlatfAndConfSelectionChangeListener platformAndConfigurationChangeListener = new PlatfAndConfSelectionChangeListener();
        DescriptionDumper descriptionDumper = new DescriptionDumper();
        this.configurationsTree = new Tree(configGroup,SWT.CHECK);
        this.configurationsTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.configurationsTree.addSelectionListener(platformAndConfigurationChangeListener);
        this.configurationsTree.addSelectionListener(descriptionDumper);
        
        Group platformGroup = new Group(top,SWT.NONE);
        platformGroup.setText("Platforms");
        platformGroup.setLayout(new GridLayout(1,false));
        platformGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        this.platformTree = new Tree(platformGroup,SWT.CHECK);
        this.platformTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.platformTree.addSelectionListener(platformAndConfigurationChangeListener);
        this.platformTree.addSelectionListener(descriptionDumper);
        
        Group devicesGroup = new Group(top,SWT.NONE);
        devicesGroup.setText("Supported Devices");
        devicesGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
        devicesGroup.setLayout(new GridLayout(1,false));
        
        this.possibleDevicesTree = new Tree(devicesGroup,SWT.CHECK);
        this.possibleDevicesTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
//        DeviceSelectionChangeListener deviceSelectionChangeListener = new DeviceSelectionChangeListener();
//        this.possibleDevicesTree.addSelectionListener(deviceSelectionChangeListener);
        this.possibleDevicesTree.addSelectionListener(this.deviceSelectionManager);
        this.possibleDevicesTree.addSelectionListener(descriptionDumper);
        
        Group descriptionGroup = new Group(top,SWT.NONE);
        descriptionGroup.setText("Description");
        descriptionGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
        descriptionGroup.setLayout(new GridLayout(1,false));
        
        this.descriptionLabel = new Label(descriptionGroup,SWT.WRAP);
        this.descriptionLabel.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        fillPlatformTree();
        fillConfigurationTree();
        return top;
    }
    
    protected void setDescription(String description) {
        this.descriptionLabel.setText(description);
    }
    
}
