/*
 * Created on Jun 13, 2006 at 4:48:17 PM.
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
public class MethodElement extends JavaElement {

    private String name;
    private String returntype;
    private String modifier;
    private List statements;
    private String[] exceptions;
    private String[] parameters;


    public MethodElement(String name,String returntype,String[] parameters, String modifier,String[] exceptions) {
        this.name = name;
        this.returntype = returntype;
        this.modifier = modifier;
        this.statements = new LinkedList();
        this.exceptions = exceptions;
        this.parameters = parameters;
    }
    
    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }
    
    public String print(Rectangle rectangle) {
        StringBuffer result = new StringBuffer();
        result.append(addSpace(rectangle.getX()));
        result.append(this.modifier);
        result.append(" ");
        result.append(this.returntype);
        result.append(" ");
        result.append(this.name);
        result.append("(");
        if(this.parameters != null) {
            int parameterLength = this.parameters.length;
            for (int i = 0; i < parameterLength; i++) {
                result.append(this.parameters[i]);
                if(i < parameterLength-1) {
                    result.append(", ");
                }
            }
        }
        result.append(")");
        if(this.exceptions != null) {
            result.append(" throws ");
            for (int i = 0; i < this.exceptions.length; i++) {
                result.append(this.exceptions[i]);
                if(i < this.exceptions.length-1) {
                    result.append(",");
                }
            }
        }
        result.append("{");
        result.append(addEoL());
        rectangle.addToX(5);
        for (Iterator iterator = this.statements.iterator(); iterator.hasNext(); ) {
            Statement statement = (Statement) iterator.next();
            result.append(statement.print(rectangle));
            result.append(addEoL());
        }
        rectangle.addToX(-5);
        result.append(addSpace(rectangle.getX()));
        result.append("}");
        return result.toString();
    }

}
