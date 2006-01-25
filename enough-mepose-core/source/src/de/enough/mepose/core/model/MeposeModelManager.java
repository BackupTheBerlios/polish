/*
 * Created on Jan 19, 2006 at 4:22:19 PM.
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
package de.enough.mepose.core.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.project.ProjectPersistence;


/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 19, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeModelManager {

    private Map modelByProject;
    private Map projectByModel;
    private IProject currentProject;
    private ProjectPersistence persistence;
    
    public class PropertyStorer implements PropertyChangeListener{
        private MeposeModel model;
        private ProjectPersistence persistence;
        private IProject project;
        
        public PropertyStorer(MeposeModel model,ProjectPersistence persistence,IProject project) {
            this.model = model;
            this.persistence = persistence;
            this.project = project;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                this.persistence.putMapInProject(this.model.getStoreableProperties(),this.project);
                System.out.println("DEBUG:PropertyStorer.propertyChange(...):enter.everything stored.");
            } catch (CoreException exception) {
                CorePlugin.log("Could not store model in project.exception:"+exception);
            }
        }
    }
    
    public MeposeModelManager() {
        this.modelByProject = new HashMap();
        this.projectByModel = new HashMap();
        this.persistence = new ProjectPersistence();
    }
    
    public MeposeModel getModel(IProject project) {
        if(project == null){
            throw new IllegalArgumentException("getModel(...):parameter 'project' is null contrary to API.");
        }
        return (MeposeModel)this.modelByProject.get(project);
    }
    
    public void addModel(IProject project, MeposeModel meposeModel) {
        if(project == null){
            throw new IllegalArgumentException("addModel(...):parameter 'project' is null contrary to API.");
        }
        this.modelByProject.put(project,meposeModel);
        this.projectByModel.put(meposeModel,project);
        try {
            this.persistence.putMapInProject(meposeModel.getStoreableProperties(),project);
        } catch (CoreException exception) {
            CorePlugin.log("Could not store properties in project."+exception.getMessage());
        }
        new PropertyStorer(meposeModel,this.persistence,project);
    }

    public void removeModel(IProject project) {
        if(project == null){
            throw new IllegalArgumentException("removeModel(...):parameter 'project' is null contrary to API.");
        }
        MeposeModel model = (MeposeModel)this.modelByProject.remove(project);
        if(model == null) {
            CorePlugin.log("No model registered for project.");
        }
        this.projectByModel.remove(model);
    }
    
    /**
     * 
     * @param project May be null.
     */
    public void setCurrentProject(IProject project) {
        this.currentProject = project;
    }
    
    public MeposeModel getCurrentMeposeModel() {
        if(this.currentProject == null) {
            return null;
        }
        return (MeposeModel)this.modelByProject.get(this.currentProject);
    }

    /**
     * @param project
     * @param map
     */
    public void addModel(IProject project, Map map) {
        if(project == null){
            throw new IllegalArgumentException("addModel(...):parameter 'project' is null contrary to API.");
        }
        if(map == null){
            throw new IllegalArgumentException("addModel(...):parameter 'map' is null contrary to API.");
        }
        MeposeModel meposeModel = new MeposeModel();
        meposeModel.restoreFromProperties(map);
        addModel(project,meposeModel);
    }
    
    public IProject getProject(MeposeModel model) {
        if(model == null) {
            return null;
        }
        return (IProject)this.projectByModel.get(model);
    }
}
