/*
 * Created on Jun 13, 2006 at 3:01:46 PM.
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
package de.enough.encogen.java;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 13, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ClazzElement extends JavaElement {

    private PackageElement packageElement;
    private List importElements;
    private String clazzName;
    private List methodElements;
    private String parent;
    
    public ClazzElement() {
        this.importElements = new LinkedList();
        this.methodElements = new LinkedList();
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public void setPackageElement(PackageElement packageElement) {
        this.packageElement = packageElement;
    }
    
    public void addImportStatement(ImportElement importStatement) {
        if(importStatement == null) {
            throw new IllegalArgumentException("Parameter 'importStatement' is null.");
        }
        this.importElements.add(importStatement);
    }
    
    public void setClazzName(String name) {
        this.clazzName = name;
    }
    
    public void addMethod(MethodElement methodElement) {
        this.methodElements.add(methodElement);
    }
    
    public String print(Rectangle rectangle) {
        StringBuffer result = new StringBuffer();
        result.append(this.packageElement.print(rectangle));
        result.append(addEoL());
        for (Iterator iterator = this.importElements.iterator(); iterator.hasNext(); ) {
            ImportElement importStatement = (ImportElement) iterator.next();
            result.append(importStatement.print(rectangle));
            result.append(addEoL());
        }
        result.append(addSpace(rectangle.getX()));
        result.append("public class ");
        result.append(this.clazzName);
        
        if(this.parent != null) {
            result.append(" extends ");
            result.append(this.parent);
        }
        
        result.append("{");
        result.append(addEoL());
        rectangle.addToX(5);
        for (Iterator iterator = this.methodElements.iterator(); iterator.hasNext(); ) {
            MethodElement methodElement = (MethodElement) iterator.next();
            result.append(methodElement.print(rectangle));
            result.append(addEoL());
        }
        rectangle.addToX(-5);
        result.append(addSpace(rectangle.getX()));
        result.append("}");
        return result.toString();
    }
    
    
}
