/*
 * Created on Aug 18, 2006 at 3:30:28 PM.
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
package de.enough.mepose.launcher;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;

import de.enough.mepose.core.model.MeposeModel;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Aug 18, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeSourceLocator extends JavaSourceLookupDirector implements
        ISourceLocator {

    private MeposeModel model;

    public MeposeSourceLocator(MeposeModel model) {
        this.model = model;
    }
    
    public MeposeSourceLocator() {
        //
    }
    
    public void setMeposeModel(MeposeModel model) {
        this.model = model;
    }
    
    public void initializeParticipants() {
        JavaSourceLookupParticipant javaSourceLookupParticipant = new JavaSourceLookupParticipant();
        addParticipants(new ISourceLookupParticipant[] {javaSourceLookupParticipant});
    }

    public synchronized ISourceContainer[] getSourceContainers() {
        System.out.println("DEBUG:MeposeSourceLocator.getSourceContainers(...):enter.");
        
        ISourceContainer[] defaultReturnValue;
        ISourceContainer[] sourceContainers = super.getSourceContainers();
        defaultReturnValue = sourceContainers;
//        MeposeModel model = MeposeUIPlugin.getDefault().getCurrentModel();
        if(this.model == null) {
            return defaultReturnValue;
        }
        
//        String projectPath = this.model.getProjectHome().getAbsolutePath();
//        IContainer[] locationContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(projectPath));
//        if(locationContainers == null || locationContainers.length == 0 || locationContainers[0] instanceof IProject) {
//            return defaultReturnValue;
//        }
//        IProject project = (IProject)locationContainers[0];
//        IJavaProject javaProject = JavaCore.create(project);
//        IClasspathEntry[] rawClasspath = null;
//        try {
//            rawClasspath = javaProject.getRawClasspath();
//        } catch (JavaModelException exception) {//
//        }
//        if(rawClasspath != null) {
//            for (int i = 0; i < rawClasspath.length; i++) {
//                IClasspathEntry classpathEntry = rawClasspath[i];
//                if(classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
//                    //
//                }
//            }
//        }
        
        //TODO: Needed for 
        ISourceContainer meposeContainer1 = new DirectorySourceContainer(new Path(this.model.getProjectHome()+"/source/src"),true);
        
        //FIXME: We need to get the source dir from the device.
//        ISourceContainer meposeContainer2 = new DirectorySourceContainer(new Path(this.model.getCurrentDevice().getSourceDir()),true);
        
        ISourceContainer[] newSourceContainers = new ISourceContainer[sourceContainers.length+2];
        System.arraycopy(sourceContainers,0,newSourceContainers,0,sourceContainers.length);
        newSourceContainers[sourceContainers.length] = meposeContainer1;
//        newSourceContainers[sourceContainers.length+1] = meposeContainer2;
        
        return newSourceContainers;
    }

    
}
