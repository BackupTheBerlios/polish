/*
 * Created on May 22, 2006 at 3:10:34 PM.
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

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        May 22, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MidletItem {

    private String name = "";
    private String key = "";
    private String className = "";
    private IResource resource;
    private String iconPath = "";

    public MidletItem() {
        //
    }
    
    public MidletItem(IResource resource) {
        if(resource == null){
            throw new IllegalArgumentException("MidletItem(...):parameter 'resource' is null contrary to API.");
        }
        
        this.name = resource.getName();
        this.resource = resource;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IResource getResource() {
        return this.resource;
    }

    public void setResource(IResource resource) {
        this.resource = resource;
    }
    
    public boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if( ! getClass().equals(other.getClass())) {
            return false;
        }
        if(this.className.equals(((MidletItem)other).getClassName())) {
            return true;
        }
        return false;
    }

    
    
    public int hashCode() {
        return this.className.hashCode();
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
 
    
}
