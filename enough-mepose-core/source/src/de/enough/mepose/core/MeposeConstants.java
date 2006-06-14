/*
 * Created on Dec 7, 2005 at 2:51:44 PM.
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
package de.enough.mepose.core;


/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 7, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public interface MeposeConstants
{
    public static final String ID_PLUGIN = "de.enough.mepose.core";
    
    public static final String PATH_BUILD_XML_TEMPLATE = "/templates/build.xml.template";
    public static final String ID_TEMPLATE_NAME = "build.xml.template";
    public static final String ID_NATURE_QUALIFIER = "polishNature";
    public static final String ID_NATURE = ID_PLUGIN + "." + ID_NATURE_QUALIFIER;
    public static final String ID_BUILDER_QUALIFIER = "polishBuilder";
    public static final String ID_BUILDER = ID_PLUGIN + "." + ID_BUILDER_QUALIFIER;
    public static final String ID_BUILD_LISTENERS_QUALIFIER = "buildListeners";
    public static final String ID_BUILD_LISTENERS = ID_PLUGIN + "." + ID_BUILD_LISTENERS_QUALIFIER;
    
    public static final String ID_WTK_HOME = "id.wtk.home";
    public static final String ID_NOKIA_HOME = "id.nokia.home";
    public static final String ID_SONY_HOME = "id.sony.home";
    public static final String ID_POLISH_HOME = "id.polish.home";
    public static final String ID_PROJECT_NAME = "id.project.name";

}