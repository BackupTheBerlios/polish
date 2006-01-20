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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;


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
    private IProject currentProject;

    public MeposeModelManager() {
        this.modelByProject = new HashMap();
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
    }

    public void removeModel(IProject project) {
        if(project == null){
            throw new IllegalArgumentException("removeModel(...):parameter 'project' is null contrary to API.");
        }
        this.modelByProject.remove(project);
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
    
}
