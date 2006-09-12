/*
 * Created on Dec 9, 2005 at 2:55:14 PM.
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
package de.enough.mepose.ui.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 9, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PlatformPage extends WizardPage {

    private NewProjectModel newProjectModel;
    private Tree deviceTree;
    protected Tree configTree;
    protected Tree platformTree;
    protected List selectedPlatformTreeItems = new LinkedList();
    protected List selectedConfigurationTreeItems = new LinkedList();
    
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
            updateButtons();
            
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            // not needed.
        }
    }
    
    protected class DescriptionDumper implements SelectionListener{
        
        private void setDescription(String description) {
            setDeviceDescription(description);
        }
        
//        private String trimString(String string) {
//            String temp = string;
//            temp = temp.replaceAll("\n|\r\n"," ");
//            temp = temp.replaceAll("\t+"," ");
//            temp = temp.replaceAll(" +"," ");
//            return temp;
//        }
        
        public void widgetSelected(SelectionEvent e) {
//            assert e.item instanceof TreeItem : "No TreeItem found.";
        
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
            else {
                setDescription("No description available. Would you like to add an entry to the device database?");
            }
            
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            //
        }
    }
    
    protected class DeviceTreeCreator implements SelectionListener{
        private List listToManage;
        private NewProjectModel newPolishModel;
        public DeviceTreeCreator(NewProjectModel newProjectModel,List listToManage) {
            this.newPolishModel = newProjectModel;
            this.listToManage = listToManage;
        }
        public void widgetSelected(SelectionEvent e) {
            if( ! (e.detail == SWT.CHECK)) {
                return;
            }
            TreeItem item = (TreeItem)e.item;
            if(item.getChecked()) {
                this.listToManage.add(item);
                addItemToProjectModel(item);
            }
            else {
                this.listToManage.remove(item);
                removeItemFromProjectModel(item);
            }
            updateDeviceTable();
            updateButtons();
        }
        private void addItemToProjectModel(TreeItem item) {
            Object data = item.getData();
            if(data instanceof Configuration) {
                this.newPolishModel.addTargetConfiguration((Configuration)data);
            }
            else if(data instanceof Platform) {
                this.newPolishModel.addTargetPlatform((Platform)data);
            }
        }
        private void removeItemFromProjectModel(TreeItem item) {
            Object data = item.getData();
            if(data instanceof Configuration) {
                this.newPolishModel.removeTargetConfiguration((Configuration)data);
            }
            else if(data instanceof Platform) {
                this.newPolishModel.removeTargetPlatform((Platform)data);
            }
//            else if(data instanceof DeviceTreeItem) {
//                DeviceTreeItem deviceTreeItem = (DeviceTreeItem)data;
//                if(deviceTreeItem.isDevice()) {
//                    this.newPolishModel.removeTargetDevice(deviceTreeItem.getDevice());
//                }
//            }
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            // not needed.
        }
    }
    
//    public static Logger logger = Logger.getLogger(PlatformPage.class);
    private DeviceTree deviceTreeModel;
    private Label descriptionLabel;
    private DeviceSelectionManager deviceSelectionManager;

    
    
    public PlatformPage(NewProjectModel newPolishProjectDAO) {
        super("Select Platform");
        this.newProjectModel = newPolishProjectDAO;
        setTitle("Select Target Devices");
        setDescription("Select the devices you want to support or choose platform and configuration categories");
    }

    protected void updateButtons() {
        getContainer().updateButtons();
    }
    

    public void createControl(Composite parent) {
        
        DescriptionDumper descriptionDumper = new DescriptionDumper();
        
        Composite topComposite = new Composite(parent,SWT.NONE);
        topComposite.setLayout(new GridLayout(2,true));
        
        Group configGroup = new Group(topComposite,SWT.NONE);
        configGroup.setText("Configurations");
        configGroup.setLayout(new GridLayout(1,false));
        configGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        this.configTree = new Tree(configGroup,SWT.CHECK);
        this.configTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        DeviceTreeCreator treeListener;
        treeListener = new DeviceTreeCreator(this.newProjectModel,this.selectedConfigurationTreeItems);
        this.configTree.addSelectionListener(treeListener);
        this.configTree.addSelectionListener(descriptionDumper);
        
        Group platformGroup = new Group(topComposite,SWT.NONE);
        platformGroup.setText("Platforms");
        platformGroup.setLayout(new GridLayout(1,false));
        platformGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        this.platformTree = new Tree(platformGroup,SWT.CHECK);
        this.platformTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        treeListener = new DeviceTreeCreator(this.newProjectModel,this.selectedPlatformTreeItems);
        this.platformTree.addSelectionListener(treeListener);
        this.platformTree.addSelectionListener(descriptionDumper);
        
        Group devicesGroup = new Group(topComposite,SWT.NONE);
        devicesGroup.setText("Devices");
        devicesGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
        devicesGroup.setLayout(new GridLayout(1,false));
        
        this.deviceTree = new Tree(devicesGroup,SWT.CHECK);
        this.deviceTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.deviceTree.addSelectionListener(descriptionDumper);
//        DeviceTreeUpdater deviceTreeUpdater = new DeviceTreeUpdater(this.newProjectModel);
//        this.deviceTree.addSelectionListener(deviceTreeUpdater);
        this.deviceSelectionManager = new DeviceSelectionManager();
        this.deviceTree.addSelectionListener(this.deviceSelectionManager);
        
        Group descriptionGroup = new Group(topComposite,SWT.NONE);
        descriptionGroup.setText("Description");
        descriptionGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
        descriptionGroup.setLayout(new GridLayout(1,false));
        
        this.descriptionLabel = new Label(descriptionGroup,SWT.WRAP);
        this.descriptionLabel.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        setControl(topComposite);
    }

    public void fillGUI() {
        DeviceDatabase deviceDatabase = null;
        try {
            deviceDatabase = this.newProjectModel.getMeposeModel().getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            // TODO rickyn handle DeviceDatabaseException
            exception.printStackTrace();
        }
        if(deviceDatabase == null) {
            MeposeUIPlugin.log("No device database found.");
            return;
        }
        
        fillConfigurationTree(deviceDatabase);
        fillPlatformTree(deviceDatabase);
        
//        this.platformTree.layout();
//        this.deviceTree.layout();
//        this.configTree.layout();
        
        this.deviceTreeModel = this.newProjectModel.getMeposeModel().getDeviceTree();
        if(this.deviceTreeModel == null) {
            throw new IllegalStateException("The directory of J2ME Polish is wrong.");
        }
        updateDeviceTable();
    }

    private void fillConfigurationTree(DeviceDatabase deviceDatabase) {
        if(deviceDatabase == null){
//            logger.error("Parameter 'deviceDatabase' is null contrary to API.");
            return;
        }
        
        TreeItem item;
        Configuration[] configurations;
        configurations = deviceDatabase.getConfigurationManager().getConfigurations();
        for (int i = 0; i < configurations.length; i++) {
            String identifier = configurations[i].getIdentifier();
            item = new TreeItem(this.configTree,SWT.NONE);
            item.setText(identifier);
            item.setData(configurations[i]);
            if(identifier.startsWith("CLDC/1.1")) {
                item.setChecked(true);
                this.selectedConfigurationTreeItems.add(item);
            }
        }
    }

    private void fillPlatformTree(DeviceDatabase deviceDatabase) {
        TreeItem item;
        Platform[] platforms;
        platforms = deviceDatabase.getPlatformManager().getPlatforms();
        for (int i = 0; i < platforms.length; i++) {
            String identifier = platforms[i].getIdentifier();
            item = new TreeItem(this.platformTree,SWT.NONE);
            item.setText(identifier);
            if(identifier.startsWith("MIDP/2.0")) {
                item.setChecked(true);
                this.selectedPlatformTreeItems.add(item);
                this.newProjectModel.addTargetPlatform(platforms[i]);
            }
            else {
                item.setChecked(false);
            }
            item.setData(platforms[i]);
            item.setExpanded(false);
        }
    }
    
    public boolean canFlipToNextPage() {
//        return this.selectedDeviceTreeItems.size() > 0;
        if(this.deviceTreeModel == null) {
            return false;
        }
        return this.deviceTreeModel.getSelectedDevices().length > 0;
    }



    protected void updateDeviceTable(){
        int numberOfTreeItems;
        numberOfTreeItems = this.selectedConfigurationTreeItems.size();
        Configuration[] configurationsArray = new Configuration[numberOfTreeItems];
        for (int i = 0; i < numberOfTreeItems; i++) {
            configurationsArray[i] = (Configuration)((TreeItem)this.selectedConfigurationTreeItems.get(i)).getData();
        }
        
        numberOfTreeItems = this.selectedPlatformTreeItems.size();
        Platform[] platformsArray = new Platform[numberOfTreeItems];
        for (int i = 0; i < numberOfTreeItems; i++) {
            platformsArray[i] = (Platform)((TreeItem)this.selectedPlatformTreeItems.get(i)).getData();
        }
        
        this.newProjectModel.getMeposeModel().setSupportedConfigurations(configurationsArray);
        this.newProjectModel.getMeposeModel().setSupportedPlatforms(platformsArray);
        
        this.deviceTreeModel.rebuild(configurationsArray,platformsArray);
        this.deviceTree.removeAll();
        this.newProjectModel.removeAllTargetDevices();
//        this.selectedDeviceTreeItems.clear();
        DeviceTreeItem[] deviceTreeItems;
        deviceTreeItems = this.deviceTreeModel.getRootItems();
        
        ArrayList nodes = new ArrayList();
        TreeItem treeItem;
        for (int i = 0; i < deviceTreeItems.length; i++) {
            treeItem = new TreeItem(this.deviceTree,SWT.NONE);
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
    
    protected void setDeviceDescription(String description) {
        if(description == null){
//            logger.error("Parameter 'description' is null contrary to API.");
            return;
        }
        this.descriptionLabel.setText(description);
    }



    public IWizardPage getNextPage() {
        
        
        IProject project = this.newProjectModel.getProject();
        IJavaProject javaProject = JavaCore.create(project);
        
        // Add the default source folder, polish style
        IPath sourcePath = new Path("/"+project.getName()+"/source/src");
        IPath[] inclusionPatterns = new Path[0];
        IPath[] exclusionPatterns = new Path[0];
        IClasspathAttribute[] classpathAttributes = new IClasspathAttribute[0];
        IPath specificOutputLocation = null;
        IClasspathEntry sourceEntry = JavaCore.newSourceEntry(sourcePath,inclusionPatterns,exclusionPatterns,specificOutputLocation,classpathAttributes);
        
        // Add the default classes folder, polish style
        IPath defaultOutputDir = new Path(project.getName()+"/bin/classes");
        
        // Add needed libraries.
        IClasspathEntry[] libraryEntries = getLibraryEntries();
        IClasspathEntry[] classpathEntries = new IClasspathEntry[libraryEntries.length+1];
        System.arraycopy(libraryEntries,0,classpathEntries,0,libraryEntries.length);
        // The last entry is the source entry.
        classpathEntries[libraryEntries.length] = sourceEntry;
        boolean defaulsOverrideExistingClasspath = true;
        
        MeposeModel meposeModel = this.newProjectModel.getMeposeModel();
        Device[] selectedDevices = this.deviceTreeModel.getSelectedDevices();
        meposeModel.setSupportedDevices(selectedDevices);
        meposeModel.setClasspath(classpathEntries);
        JavaCapabilityConfigurationPage nextPage = (JavaCapabilityConfigurationPage)super.getNextPage();
        nextPage.init(javaProject,defaultOutputDir,classpathEntries,defaulsOverrideExistingClasspath);
        this.newProjectModel.setJavaTabReached(true);
        return nextPage;
    }
    
    private IClasspathEntry[] getLibraryEntries() {
        File[] classpathFiles = this.deviceTreeModel.getClasspathForSelectedDevices();
        int numberOfClasspathEntriesForSelectedDevices = classpathFiles.length;
        int finalNumberOfClasspathEntries = numberOfClasspathEntriesForSelectedDevices+1;
        IClasspathEntry[] classpathEntries = new IClasspathEntry[finalNumberOfClasspathEntries];
        
        for (int i = 0; i < numberOfClasspathEntriesForSelectedDevices; i++) {
            IPath classpathEntryPath = new Path(classpathFiles[i].getAbsolutePath());
            IClasspathEntry classpathEntry = JavaCore.newLibraryEntry(classpathEntryPath,null,null);
            classpathEntries[i] = classpathEntry;
        }
        
        String polishClientJarPathString = this.newProjectModel.getMeposeModel().getPolishHome() + "/import/enough-j2mepolish-client.jar";
        
        IPath polishClientJarPath = new Path(polishClientJarPathString);
        IClasspathEntry polishClientJarEntry = JavaCore.newLibraryEntry(polishClientJarPath,null,null);
        classpathEntries[finalNumberOfClasspathEntries-1] = polishClientJarEntry;
        
        return classpathEntries;
    }
    

    
}
