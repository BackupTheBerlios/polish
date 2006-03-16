/*
 * Created on Dec 8, 2005 at 4:23:58 PM.
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.Device;
import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.Platform;
import de.enough.utils.PropertyModel;
import de.enough.utils.Status;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 8, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class NewProjectModel extends PropertyModel{
    
    public static final String ID_NEWPROJECTMODEL_PROJECT_NAME = "id.newprojectmodel.project.name";
    public static final String ID_NEWPROJECTMODEL_PROJECT_INSTANCE = "id.newprojectmodel.project.instance";
    public static final String ID_NEWPROJECTMODEL_PROJECT_TOCONVERT = "id.newprojectmodel.project.toconvert";
    public static final String ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT = "id.newprojectmodel.state.created.project";
    public static final String ID_NEWPROJECTMODEL_MEPOSEMODEL = "id.newprojectmodel.meposeModel";
    
    private MeposeModel meposeModel;
//    private boolean canFinish = false;
    
    private List targetDevices = new LinkedList();
    private List targetConfigurations = new LinkedList();
    private List targetPlatforms = new LinkedList();
    private IProject projectInstance;
    
    private boolean javaTabReached = false;

    //    private IProject projectToConvert;
//    private boolean mayFinish;
//    private String projectName;
//    private IProject newProject;
//    private boolean projectCreated = false;
    
    public NewProjectModel() {
        this(null);
    }
    
    public NewProjectModel(MeposeModel meposeModel) {
        if(meposeModel != null) {
            this.meposeModel = meposeModel;
            // Init this to avoid null pointer exceptions when setting a text field.
            setPropertyDefaultValue(ID_NEWPROJECTMODEL_PROJECT_NAME,"");
        }
        else {
            this.meposeModel = new MeposeModel();
        }
        
        setPropertyValue(ID_NEWPROJECTMODEL_MEPOSEMODEL,this.meposeModel);
        
        
        this.propertiesWithAtLeastOKStatus = new String[] {ID_NEWPROJECTMODEL_PROJECT_INSTANCE};
        checkModelStatus();
    }
    
    

    protected Status getStatusForPropertyAndValue(String property, Object value) {
        if(ID_NEWPROJECTMODEL_PROJECT_NAME.equals(property)){
            return processProjectNameChanged(value);
        }
        return Status.OK;
    }

    private Status processProjectNameChanged(Object value) {
        String projectName = (String)value;
        if(projectName == null || projectName.length() == 0) {
            return new Status(Status.TYPE_ERROR,"The project name must not be empty");
        }
        IResource resource = null;
        resource = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
        
        // We found a project.
        if(resource != null) {
            if(resource instanceof IProject) {
                setPropertyValue(ID_NEWPROJECTMODEL_PROJECT_TOCONVERT,resource);
                return new Status(Status.TYPE_WARNING,"Project exists and will be converted");
            }
            setPropertyValue(ID_NEWPROJECTMODEL_PROJECT_TOCONVERT,null);
            return new Status(Status.TYPE_ERROR,"Resource exists");
        }
        setPropertyValue(ID_NEWPROJECTMODEL_PROJECT_TOCONVERT,null);
        return new Status(Status.TYPE_OK);
    }

    protected void checkModelStatus() {
        if(this.meposeModel.getModelStatus().getType() != Status.TYPE_OK) {
            setModelStatus(Status.ERROR);
            return;
        }
        super.checkModelStatus();
    }
    
    /**
     * 
     * @return Never null.
     */
    public MeposeModel getMeposeModel() {
        return this.meposeModel;
    }

    public void setMeposeModel(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
    }
    
    public boolean canFinish() {
        DeviceTree deviceTree = this.meposeModel.getDeviceTree();
        if(deviceTree == null) {
            return false;
        }
        boolean hasTargetDevices = deviceTree.getSelectedDevices().length > 0;
        boolean hasProject = getProject() != null;
        return hasTargetDevices && hasProject && this.javaTabReached;
    }
    
    public IProject getProject() {
        return this.projectInstance;
//        return (IProject)getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
    }
    
    public void setTargetDevices(Device[] targetDevices) {
        if(targetDevices == null){
            logger.error("Parameter 'targetDevices' is null contrary to API.");
            return;
        }
        this.targetDevices.clear();
        this.targetDevices = new LinkedList(Arrays.asList(targetDevices));
    }
    
    /**
     * The initial target devices for the new project.
     * @return Never null.
     */
    public Device[] getTargetDevices() {
        return (Device[]) this.targetDevices.toArray(new Device[this.targetDevices.size()]);
    }
    
    public void setTargetConfiguration(Configuration[] targetConfigurations) {
        if(targetConfigurations == null){
            logger.error("Parameter 'targetConfigurations' is null contrary to API.");
            return;
        }
        this.targetConfigurations.clear();
        this.targetConfigurations = new LinkedList(Arrays.asList(targetConfigurations));
    }
    
    /**
     * The initial target configurations for the new project.
     * @return Never null.
     */
    public Configuration[] getTargetConfigurations() {
        return (Configuration[]) this.targetConfigurations.toArray(new Configuration[this.targetConfigurations.size()]);
    }
    
    public void setTargetPlatforms(Platform[] targetPlatforms) {
        if(targetPlatforms == null){
            logger.error("Parameter 'targetPlatforms' is null contrary to API.");
            return;
        }
        this.targetPlatforms.clear();
        this.targetPlatforms = new LinkedList(Arrays.asList(targetPlatforms));
    }
    
    /**
     * The initial target platforms for the new project.
     * @return Never null.
     */
    public Platform[] getTargetPlatforms() {
        return (Platform[]) this.targetPlatforms.toArray(new Platform[this.targetPlatforms.size()]);
    }
    
    public void addTargetPlatform(Platform newPlatform) {
        if(newPlatform == null){
            logger.error("Parameter 'newPlatform' is null contrary to API.");
            return;
        }
        this.targetPlatforms.add(newPlatform);
    }
    
    public void removeTargetPlatform(Platform deletedPlatform) {
        if(deletedPlatform == null){
            logger.error("Parameter 'deletedPlatform' is null contrary to API.");
            return;
        }
        this.targetPlatforms.remove(deletedPlatform);
    }
    
    public void addTargetConfiguration(Configuration newConfiguration) {
        if(newConfiguration == null) {
            logger.error("Parameter 'newConfiguration' is null contrary to API.");
            return;
        }
        this.targetConfigurations.add(newConfiguration);
    }
    
    public void removeTargetConfiguration(Configuration deletedConfiguration) {
        if(deletedConfiguration == null){
            logger.error("Parameter 'deletedConfiguration' is null contrary to API.");
            return;
        }
        this.targetConfigurations.remove(deletedConfiguration);
    }
    
    public void addTargetDevice(Device newDevice) {
        if(newDevice == null){
            logger.error("Parameter 'newDevice' is null contrary to API.");
            return;
        }
        this.targetDevices.add(newDevice);
    }

    public void removeTargetDevice(Device deletedDevice) {
        if(deletedDevice == null){
            logger.error("Parameter 'deletedDevice' is null contrary to API.");
            return;
        }
        this.targetDevices.remove(deletedDevice);
    }

    /**
     * 
     */
    public void removeAllTargetDevices() {
        this.targetDevices.clear();
    }

    /**
     * @param projectDescription
     */
    public void setProjectDescription(String projectDescription) {
        this.meposeModel.setProjectDescription(projectDescription);
    }

    public void setProjectInstance(IProject projectInstance) {
        this.projectInstance = projectInstance;
    }
    
    /**
     * 
     * @return Never null.
     */
    public String getProjectDescription() {
        return this.meposeModel.getProjectDescription();
    }

    public boolean isJavaTabReached() {
        return this.javaTabReached;
    }

    public void setJavaTabReached(boolean javaTabReached) {
        this.javaTabReached = javaTabReached;
    }
    
    
    
}
