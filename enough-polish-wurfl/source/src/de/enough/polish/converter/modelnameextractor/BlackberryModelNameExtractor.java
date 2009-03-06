/*
 * Created on Jun 11, 2008 at 2:09:59 PM.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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
package de.enough.polish.converter.modelnameextractor;

import java.util.LinkedList;
import java.util.List;

import de.enough.polish.converter.ModelNameExtractor;


public class BlackberryModelNameExtractor implements ModelNameExtractor{
    boolean first = true;
    public List<String> matchModel(String modelNameToMatch) {
        this.first = false;
        String modelName = modelNameToMatch;
        modelName = modelName.replaceAll("BlackBerry","");
        modelName = modelName.replaceAll("\\(.*\\)","");
        modelName = modelName.trim();
        List<String> models = new LinkedList<String>();
        models.add(modelName);
        return models;
    }
    public String getVendor(){
        return "BlackBerry";
    }
}