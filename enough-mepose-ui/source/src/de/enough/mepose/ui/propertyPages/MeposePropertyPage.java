/*
 * Created on Sep 7, 2006 at 2:54:40 PM.
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
package de.enough.mepose.ui.propertyPages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.dialogs.PropertyPage;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Sep 7, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public abstract class MeposePropertyPage extends PropertyPage {

    protected MeposeModel getMeposeModel() {
        IAdaptable adaptable = getElement();
        IProject project = (IProject)adaptable.getAdapter(IProject.class);
        if(project == null) {
            return null;
        }
        MeposeModel meposeModel = MeposePlugin.getDefault().getMeposeModelManager().getModel(project);
        return meposeModel;
    }

}