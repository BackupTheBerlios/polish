/*
 * Created on Jun 1, 2005 at 3:29:44 PM.
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
package de.enough.polish.plugin.eclipse.core;

public class ObjectChangeEvent extends Event{
    
    private Object oldObject;
    private Object newObject;
    
    public ObjectChangeEvent(Object sender, Object oldObject, Object newObject) {
        super(sender);
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    public Object getNewObject() {
        return this.newObject;
    }

    public Object getOldObject() {
        return this.oldObject;
    }

    public void setNewObject(Object newObject) {
        this.newObject = newObject;
    }

    public void setOldObject(Object oldObject) {
        this.oldObject = oldObject;
    }
    
//    public ObjectChangeEvent(Object sender, Object changedObject,int typeOfChange) {
//        super(sender);
//        // We need a reference to the object which changed. null doesnt say anything.
//        if(changedObject == null){
//            throw new IllegalArgumentException("ERROR:ObjectChangeEvent.ObjectChangeEvent(...):Parameter 'changedObject' is null.");
//        }
//        this.changedObject = changedObject;
//        this.typeOfChange = typeOfChange;
//    }

    
 
   
}
