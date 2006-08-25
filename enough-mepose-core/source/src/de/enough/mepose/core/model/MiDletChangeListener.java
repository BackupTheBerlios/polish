/*
 * Created on May 15, 2006 at 1:51:09 PM.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.project.PolishNature;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        May 15, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MiDletChangeListener implements IResourceChangeListener{

    public class DeltaVisitor implements IResourceDeltaVisitor{

        public static final int ADDED = IResourceDelta.ADDED;
        public static final int REMOVED = IResourceDelta.REMOVED;
        public static final int CHANGED = IResourceDelta.CHANGED;
        
        public boolean visit(IResourceDelta delta) {
//            if(delta.getFlags() != 131072) {
            
            int flags = delta.getFlags();
            int kind = delta.getKind();
            
            boolean pass = false;
            
            if(kind == ADDED) {
                pass = true;
            }
            
            if(kind == REMOVED) {
                pass = true;
            }
            
            if(kind == CHANGED) {
                if(flags == 131072) {
                    pass = true;
                }
            }
            
            if(flags != 256 && flags != 0 && (kind != 2 && flags != 131072)) {
                return true;
            }
            
            if(!pass) {
                return true;
            }
            
            IResource res = delta.getResource();
            switch (kind) {
               case IResourceDelta.ADDED:
                  handleResource(res,ADDED);
                  break;
               case IResourceDelta.REMOVED:
                  handleResource(res,REMOVED);
                  break;
               case IResourceDelta.CHANGED:
                  handleResource(res,CHANGED);
                  break;
            }
            return true; // visit the children
        }

        private void handleResource(IResource resource,int action) {
            MidletItem midletItem = generateMiDLetFromResource(resource);
            if(midletItem == null) {
                return;
            }
            handleMiDlet(midletItem,action);
        }

        private void handleMiDlet(MidletItem midletItem,int action) {
            IProject project = midletItem.getResource().getProject();
            MeposeModel model = MeposePlugin.getDefault().getMeposeModelManager().getModel(project);
            if(model == null) {
                return;
            }
            IResource midletPropertiesResource = project.findMember("/midlet.properties");
            File midletPropertiesFile;
            if(midletPropertiesResource == null) {
                midletPropertiesFile = new File(project.getLocation().append("/midlet.properties").toOSString());
                try {
                    midletPropertiesFile.createNewFile();
                } catch (IOException exception) {
                    MeposePlugin.log("Could not create file /midlet.properties",exception);
                    return;
                }
                MeposePlugin.log("No midlet.properties file found.Creating:"+midletPropertiesFile.getAbsolutePath());
            }
            else {
                midletPropertiesFile = midletPropertiesResource.getRawLocation().toFile();
            }
            Set midlets = getMidletsFromPropertiesFile(midletPropertiesFile);
            switch(action) {
                case ADDED:
                case CHANGED:
                    midlets.add(midletItem);
                    removeMidletsFromList(project,midlets);
                    break;
                case REMOVED:
                    midlets.remove(midletItem);
                    break;
            }
            putMidletsIntoPropertiesFile(midletPropertiesFile,midlets);
            
//            The following exception occur:
//              org.eclipse.core.internal.resources.ResourceException: The resource tree is locked for modifications.
//            try {
//                midletPropertiesResource.touch(null);
//            } catch (CoreException exception) {
//                // TODO rickyn handle CoreException
//                exception.printStackTrace();
//            }
            
            
//            The following exception occur:
//          org.eclipse.core.internal.resources.ResourceException: The resource tree is locked for modifications.

//            try {
//                project.refreshLocal(1,new NullProgressMonitor());
//            } catch (CoreException exception) {
//                MeposePlugin.log("Could not refresh project",exception);
//                return;
//            }
            
        }
    }
    
    protected void removeMidletsFromList(IProject project, Set midlets) {
        IJavaProject javaProject = JavaCore.create(project);
        for (Iterator iterator = midlets.iterator(); iterator.hasNext(); ) {
            MidletItem midletItem = (MidletItem) iterator.next();
            IType type;
            try {
                type = javaProject.findType(midletItem.getClassName());
            } catch (JavaModelException exception) {
                MeposePlugin.log("Exception while searching for '"+midletItem.getClassName()+"'",exception);
                continue;
            }
            if(type == null) {
                // The midlet is no longer present. Remove it from the list.
                iterator.remove();
            }
        }
    }
    
    protected void putMidletsIntoPropertiesFile(File midletPropertiesFile,Set midlets){
        Properties properties = new Properties();
        int i = 1;
        for (Iterator iterator = midlets.iterator(); iterator.hasNext(); ) {
            MidletItem midletItem = (MidletItem) iterator.next();
            StringBuffer value = new StringBuffer();
            value.append(midletItem.getName());
            value.append(",");
            value.append(midletItem.getIconPath());
            value.append(",");
            value.append(midletItem.getClassName());
            properties.put("MIDlet-"+i,value.toString());
            i++;
        }
        
        //Remove midlet.properties file.
        if( ! properties.keys().hasMoreElements()) {
            if(midletPropertiesFile.exists()) {
                midletPropertiesFile.delete();
            }
            return;
        }
        
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(midletPropertiesFile);
        } catch (FileNotFoundException exception) {
            MeposePlugin.log("Could not open outputstream for /midlet.properties",exception);
            return;
        }
        try {
            properties.store(outputStream,"");
        } catch (IOException exception) {
            MeposePlugin.log("Could not store midlet information in /midlet.properties",exception);
            return;
        }
        try {
            outputStream.close();
        } catch (IOException exception) {
            MeposePlugin.log("Could not close outputStream for /midlet.properites",exception);
            return;
        }
    }
    
    protected Set getMidletsFromPropertiesFile(File propertyFile) {
        FileInputStream inputStream;
        Set result = new HashSet();
        try {
            inputStream = new FileInputStream(propertyFile);
        } catch (FileNotFoundException exception) {
            return result;
        }
        Properties properties;
        try {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException exception) {
            return result;
        }
        for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String)properties.get(key);
            String[] components = value.split(",");
            MidletItem midletItem = new MidletItem();
            midletItem.setKey(key);
            midletItem.setName(components[0]);
            midletItem.setIconPath(components[1]);
            midletItem.setClassName(components[2]);
            result.add(midletItem);
        }
        try {
            inputStream.close();
        } catch (IOException exception) {
            MeposePlugin.log("Could not close input stream for /midlet.properties",exception);
            return result;
        }
        return result;
    }
    
    public void resourceChanged(IResourceChangeEvent event) {
        boolean hasPolishNature = false;
        try {
            IResource resource = event.getResource();
            if(resource == null) {
                return;
            }
            IProject project = resource.getProject();
            if(project == null){
                return;
            }
            hasPolishNature = project.hasNature(PolishNature.ID);
        } catch (CoreException exception1) {
            return;
        }
        if(!hasPolishNature) {
            return;
        }
        switch (event.getType()) {
           case IResourceChangeEvent.POST_CHANGE:
              try {
                    event.getDelta().accept(new DeltaVisitor());
                } catch (CoreException exception) {
                    MeposePlugin.log(exception);
                    return;
                }
              break;
        }
    }

    protected MidletItem generateMiDLetFromResource(IResource resource) {
        IJavaElement javaElement = JavaCore.create(resource);
        if(javaElement == null) {
            return null;
        }
        if(javaElement.getElementType() != IJavaElement.COMPILATION_UNIT) {
            return null;
        }
        ICompilationUnit compilationUnit = (ICompilationUnit)javaElement;
        IType primaryType = compilationUnit.findPrimaryType();
        if(primaryType == null) {
            return null;
        }
        int flags;
        try {
            flags = primaryType.getFlags();
        } catch (JavaModelException exception) {
            MeposePlugin.log(exception);
            return null;
        }
        if(Flags.isAbstract(flags)) {
            return null;
        }
        ITypeHierarchy supertypeHierarchy;
        try {
            supertypeHierarchy = primaryType.newSupertypeHierarchy(new NullProgressMonitor());
        } catch (JavaModelException exception) {
            return null;
        }
        IType[] allSuperclasses = supertypeHierarchy.getAllSuperclasses(primaryType);
        for (int i = 0; i < allSuperclasses.length; i++) {
            if("MIDlet".equals(allSuperclasses[i].getElementName())){
                MidletItem midletItem = new MidletItem();
                midletItem.setResource(resource);
                midletItem.setClassName(primaryType.getFullyQualifiedName());
                midletItem.setName(primaryType.getElementName());
                return midletItem;
            }
        }
        return null;
    }

}
