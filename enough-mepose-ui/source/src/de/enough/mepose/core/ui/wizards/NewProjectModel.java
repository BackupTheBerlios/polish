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
package de.enough.mepose.core.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import de.enough.mepose.core.model.MeposeModel;
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
        }
        else {
            this.meposeModel = new MeposeModel();
        }
        
        setPropertyDefaultValue(ID_NEWPROJECTMODEL_PROJECT_NAME,"");
        setPropertyValue(ID_NEWPROJECTMODEL_MEPOSEMODEL,this.meposeModel);
        
        this.propertiesLeastOK = new String[] {ID_NEWPROJECTMODEL_PROJECT_INSTANCE};
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
                return new Status(Status.TYPE_OK,"Project exists and will be converted");
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
    
//    /**
//     * 
//     * @return May be null.
//     */
//    public IProject getProjectToConvert() {
//        return this.projectToConvert;
//    }
//    
//    public void setProjectToConvert(IProject projectToConvert) {
//        this.projectToConvert = projectToConvert;
//    }
//    
//    public boolean isBasicallyConfigured() {
//        return this.mayFinish;
//    }
//    public void setBasicallyConfigured(boolean isBasicallyConfigured) {
//        this.mayFinish = isBasicallyConfigured;
//    }
    
//    /**
//     * 
//     * @return Never be null.
//     */
//    public String getProjectName() {
//        return this.projectName;
//    }
//    
//    public void setProjectName(String projectName) {
//        this.projectName = projectName;
//    }

    

//    /**
//     * 
//     * @return May be null.
//     */
//    public IProject getNewProject() {
//        return this.newProject;
//    }
//
//    public void setNewProject(IProject project) {
//        this.newProject = project;
//    }
    
//    public void setPolishHome(File polishHome) {
//        this.model.setPolishHome(polishHome);
//    }
//    
//    public void setWTKHome(File wtkHome) {
//        this.model.setWTKHome(wtkHome);
//    }
    
//    /**
//     * 
//     * @return Never null.
//     */
//    public File getPolishHome() {
//        return this.model.getPolishHome();
//    }
//    
//    /**
//     * 
//     * @return Never null.
//     */
//    public File getWTKHome() {
//        return this.model.getWTKHome();
//    }

//    public boolean isProjectCreated() {
//        return this.projectCreated;
//    }
//
//    public void setProjectCreated(boolean projectCreated) {
//        this.projectCreated = projectCreated;
//    }
    
    
    
    
}
