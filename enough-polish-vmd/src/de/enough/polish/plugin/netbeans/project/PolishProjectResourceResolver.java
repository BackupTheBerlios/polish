/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 */

package de.enough.polish.plugin.netbeans.project;

import org.netbeans.modules.vmd.midp.components.ProjectResourceResolver;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author David Kaspar
 */
public class PolishProjectResourceResolver implements ProjectResourceResolver {

    public Collection<FileObject> getResourceRoots (Project project, String projectType) {
        if (! MidpDocumentSupport.PROJECT_TYPE_MIDP.equals (projectType))
            return null;
        FileObject resources = project.getProjectDirectory ().getFileObject ("resources"); // NOI18N
        if (resources == null)
            return null;
        FileObject[] roots = resources.getChildren ();
        ArrayList<FileObject> result = new ArrayList<FileObject> ();
        for (FileObject root : roots)
            if (root != null  &&  root.isFolder ())
                result.add (root);
        return result;
    }

}
