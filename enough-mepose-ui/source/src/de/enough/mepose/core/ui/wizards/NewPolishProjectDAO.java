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
    private IJavaProject projectToConvert;
    private boolean shouldConvertProject;
    // Must not be null.
    private IPath polishHomePath;
    private boolean isBasicallyConfigured;
    private String projectName;
    private IProject project;
    private MeposeModel model;
    
    public NewPolishProjectDAO() {
        reset();
    }
    
    public void reset() {
        this.projectToConvert = null;
        this.shouldConvertProject = false;
        this.polishHomePath = new Path("");
        this.isBasicallyConfigured = false;
        this.projectName = "";
    }
    public IJavaProject getProjectToConvert() {
        return this.projectToConvert;
    }
    public void setProjectToConvert(IJavaProject projectToConvert) {
        this.projectToConvert = projectToConvert;
    }
    public boolean isShouldConvertProject() {
        return this.shouldConvertProject;
    }
    public void setShouldConvertProject(boolean shouldConvertProject) {
        this.shouldConvertProject = shouldConvertProject;
    }
    public IPath getPolishHomePath() {
        return this.polishHomePath;
    }
    public void setPolishHomePath(IPath polishHomePath) {
        this.polishHomePath = polishHomePath;
    }
    public boolean isBasicallyConfigured() {
        return this.isBasicallyConfigured;
    }
    public void setBasicallyConfigured(boolean isBasicallyConfigured) {
        this.isBasicallyConfigured = isBasicallyConfigured;
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

    public IProject getProject() {
        return this.project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }
    
}
