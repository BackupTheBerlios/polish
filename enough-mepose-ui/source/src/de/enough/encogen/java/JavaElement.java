/*
 * Created on Jun 13, 2006 at 2:48:33 PM.
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
 * This is a abstract base class for all java elements which meight be generated.
 * This class could be futher improved by adding API to report warnings like
 * lowercase classnames or introducing Exceptions to signal error conditions like
 * missing parameters.
 * A lot of subclasses could be improved by specialising their parameters.
 * TODO: The design must be done upside down. The JavaElement must not have view logic
 * like the print method. It is only a model. A Formatter has a specialized method
 * for every JavaElement and will print it. The formatter may also know something
 * about color etc.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 13, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public abstract class JavaElement {

    public abstract String print(Rectangle rectangle);
    
    public String addSpace(int amount) {
        if(amount < 0) {
            throw new IllegalArgumentException("Parameter 'amount' is less than 0.");
        }
        StringBuffer result = new StringBuffer(amount);
        for(int i = 0; i < amount; i++) {
            result.append(" ");
        }
        return result.toString();
    }
    
    public String addEoL() {
        return "\n";
    }
}
