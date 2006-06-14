/*
 * Created on Jun 13, 2006 at 3:14:22 PM.
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


/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 13, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ImportElement extends JavaElement {

    private String importStatement;

    public ImportElement(String importStatement) {
        setImportStatement(importStatement);
    }
    
    public void setImportStatement(String importElement) {
        importElement = importElement.replaceAll(";","");
        importElement = importElement.trim();
        String importPrefix = "import ";
        if(importElement.startsWith(importPrefix)) {
            importElement = importElement.substring(importPrefix.length(),importElement.length());
            importElement = importElement.trim();
        }
        if(importElement.length() == 0) {
            throw new IllegalArgumentException("Parameter 'importElement' does not contain any text.");
        }
        this.importStatement = importElement;
    }

    public String print(Rectangle rectangle) {
        StringBuffer result = new StringBuffer();
        result.append(addSpace(rectangle.getX()));
        result.append("import ");
        result.append(this.importStatement);
        result.append(";");
        return result.toString();
    }
    
    
}
