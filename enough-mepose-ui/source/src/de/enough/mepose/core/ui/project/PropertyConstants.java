/*
 * Created on Dec 21, 2005 at 11:23:54 AM.
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
package de.enough.mepose.core.ui.project;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.internal.ui.util.CoreUtility;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.core.ui.plugin.UIPluginActivator;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 21, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public interface PropertyConstants {

    public static final QualifiedName QN_WTK_HOME = new QualifiedName(UIPluginActivator.class.getName(),MeposeModel.ID_WTK_HOME);
    public static final QualifiedName QN_POLISH_HOME = new QualifiedName(UIPluginActivator.class.getName(),MeposeModel.ID_POLISH_HOME);
    public static final QualifiedName QN_PLATFORMS_SUPPORTED = new QualifiedName(UIPluginActivator.class.getName(),MeposeModel.ID_PLATFORMS_SUPPORTED);
    public static final QualifiedName QN_DEVICES_SUPPORTED = new QualifiedName(UIPluginActivator.class.getName(),MeposeModel.ID_DEVICES_SUPPORTED);;
}

