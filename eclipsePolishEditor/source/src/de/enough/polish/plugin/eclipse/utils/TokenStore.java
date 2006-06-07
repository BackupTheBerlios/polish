/*
 * Created on Apr 6, 2005 at 2:45:43 PM.
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
package de.enough.polish.plugin.eclipse.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.IToken;

/**
 * <p>
 * All methods ensure that there parameters are not null.
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2005
 * </p>
 * 
 * <pre>
 * 
 *  history
 *         Apr 6, 2005 - ricky creation
 *  
 * </pre>
 * 
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class TokenStore {
    
    private Map colorKeyToTokenMapping;
    
    public TokenStore() {
        this.colorKeyToTokenMapping = new HashMap();
    }
    
    public void addToken(String colorKey, IToken token) {
        this.colorKeyToTokenMapping.put(colorKey,token);
    }
    
    public void removeToken(String colorKey) {
        this.colorKeyToTokenMapping.remove(colorKey);
    }
    
    public IToken getToken(String colorKey) {
        IToken result = (IToken)this.colorKeyToTokenMapping.get(colorKey);
        return result;
    }
    
    public boolean containsToken(String colorKey) {
        return this.colorKeyToTokenMapping.containsKey(colorKey);
    }
}

