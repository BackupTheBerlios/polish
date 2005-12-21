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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;

import de.enough.mepose.core.model.MeposeModel;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 8, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class NewPolishProjectDAO{
    
    // May be null.
    private IProject projectToConvert;
    private boolean shouldConvertProject;
    // Must not be null.
    private boolean mayFinish;
    private String projectName;
    private IProject newProject;
    private MeposeModel model;
    private boolean projectCreated = false;
    public NewPolishProjectDAO() {
        reset();
    }
    
    public void reset() {
        this.projectToConvert = null;
        this.shouldConvertProject = false;
        this.mayFinish = false;
        this.projectName = "";
    }
    public IProject getProjectToConvert() {
        return this.projectToConvert;
    }
    public void setProjectToConvert(IProject projectToConvert) {
        this.projectToConvert = projectToConvert;
    }
    public boolean isShouldConvertProject() {
        return this.shouldConvertProject;
    }
    public void setShouldConvertProject(boolean shouldConvertProject) {
        this.shouldConvertProject = shouldConvertProject;
    }
    public boolean isBasicallyConfigured() {
        return this.mayFinish;
    }
    public void setBasicallyConfigured(boolean isBasicallyConfigured) {
        this.mayFinish = isBasicallyConfigured;
    }
    public String getProjectName() {
        return this.projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public MeposeModel getModel() {
        return this.model;
    }

    public void setModel(MeposeModel model) {
        this.model = model;
    }

    public IProject getNewProject() {
        return this.newProject;
    }

    public void setNewProject(IProject project) {
        this.newProject = project;
    }
    
    public void setPolishHome(File polishHome) {
        this.model.setPolishHome(polishHome);
    }
    
    public void setWTKHome(File wtkHome) {
        this.model.setWTKHome(wtkHome);
    }
    
    public File getPolishHome() {
        return this.model.getPolishHome();
    }
    
    public File getWTKHome() {
        return this.model.getWTKHome();
    }

    public boolean isProjectCreated() {
        return this.projectCreated;
    }

    public void setProjectCreated(boolean projectCreated) {
        this.projectCreated = projectCreated;
    }
    
}
