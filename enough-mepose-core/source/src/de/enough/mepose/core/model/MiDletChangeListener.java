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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

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

    public void resourceChanged(IResourceChangeEvent event) {
        IResource resource = event.getResource();
        if(resource == null) {
            return;
        }
        if( ! isMiDLet(resource)) {
            System.out.println("DEBUG:MiDletChangeListener.resourceChanged(...):no midlet found.");
            return;
        }
        
        System.out.println("DEBUG:MiDletChangeListener.resourceChanged(...):midlet found.");
    }

    private boolean isMiDLet(IResource resource) {
        IJavaElement javaElement = JavaCore.create(resource);
        if(javaElement == null) {
            return false;
        }
        if(javaElement.getElementType() != IJavaElement.COMPILATION_UNIT) {
            return false;
        }
        ICompilationUnit compilationUnit = (ICompilationUnit)javaElement;
        IType primaryType = compilationUnit.findPrimaryType();
        ITypeHierarchy supertypeHierarchy;
        try {
            supertypeHierarchy = primaryType.newSupertypeHierarchy(new NullProgressMonitor());
        } catch (JavaModelException exception) {
            return false;
        }
        IType[] allSuperclasses = supertypeHierarchy.getAllSuperclasses(primaryType);
        for (int i = 0; i < allSuperclasses.length; i++) {
            if("MiDlet".equals(allSuperclasses[i].getElementName())){
                return true;
            }
        }
        return false;
    }

}
