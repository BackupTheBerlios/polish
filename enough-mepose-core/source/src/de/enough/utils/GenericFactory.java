/*
 * Created on Jun 9, 2005 at 5:04:30 PM.
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
package de.enough.utils;

import java.util.HashMap;
import java.util.Map;

public class GenericFactory {

    private Map classStringToClassObjectMapping;
    
    public GenericFactory() {
        this.classStringToClassObjectMapping = new HashMap();
    }
    
    public Object getInstance(String nameOfInstance) {
        if(nameOfInstance == null){
            throw new IllegalArgumentException("ERROR:GenericFactory.getInstance(...):Parameter 'nameOfInstance' is null.");
        }
        Class classToInstantiate = (Class)this.classStringToClassObjectMapping.get(nameOfInstance);
        Object instance = null;
        try {
            instance = classToInstantiate.newInstance();
        } catch (Exception exception) {
            // Do nothing.
        }
        return instance;
    }
    
    public void registerClass(String nameOfInstance,Class classToRegister) {
        if(nameOfInstance == null){
            throw new IllegalArgumentException("ERROR:GenericFactory.registerClass(...):Parameter 'nameOfInstance' is null.");
        }
        if(classToRegister == null) {
            this.classStringToClassObjectMapping.remove(nameOfInstance);
        }
        else {
            this.classStringToClassObjectMapping.put(nameOfInstance,classToRegister);
        }
    }
    
    public boolean isNameRegistered(String nameOfInstance) {
        if(nameOfInstance == null){
            throw new IllegalArgumentException("ERROR:GenericFactory.isNameRegistered(...):Parameter 'nameOfInstance' is null.");
        }
        return this.classStringToClassObjectMapping.containsKey(nameOfInstance);
    }
    
}
