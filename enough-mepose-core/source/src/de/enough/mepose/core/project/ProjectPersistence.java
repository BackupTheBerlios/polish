/*
 * Created on Jan 23, 2006 at 4:05:38 PM.
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
package de.enough.mepose.core.project;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import de.enough.mepose.core.CorePlugin;
import de.enough.utils.Arrays;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 23, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ProjectPersistence {

    private static final String ID = CorePlugin.class.getName();
    public static final String CONTAINED_KEYS = "CONTAINED_KEYS";
    
//    public static final QualifiedName QN_WTK_HOME = new QualifiedName(ID,MeposeModel.ID_PATH_WTK_FILE);
//    public static final QualifiedName QN_POLISH_HOME = new QualifiedName(ID,MeposeModel.ID_PATH_POLISH_FILE);
//    public static final QualifiedName QN_PLATFORMS_SUPPORTED = new QualifiedName(ID,MeposeModel.ID_SUPPORTED_PLATFORMS);
//    public static final QualifiedName QN_DEVICES_SUPPORTED = new QualifiedName(ID,MeposeModel.ID_SUPPORTED_DEVICES);;
    
    public void putMapInProject(Map map, IProject resource) throws CoreException {
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String)map.get(key);
            resource.setPersistentProperty(new QualifiedName(ID,key),value);
        }
        String keysAsString = Arrays.arrayToString(map.keySet().toArray(new Object[map.keySet().size()]));
        resource.setPersistentProperty(new QualifiedName(ID,CONTAINED_KEYS),keysAsString);
    }
    
    public Map getMapFromProject(IProject project) throws CoreException {
       String keysAsString = project.getPersistentProperty(new QualifiedName(ID,CONTAINED_KEYS));
       Map p = new HashMap();
       String[] keys = keysAsString.split(",");
       for (int i = 0; i < keys.length; i++) {
           String key = keys[i];
        p.put(key,project.getPersistentProperty(new QualifiedName(ID,key)));
    }
       return p;
    }
}
