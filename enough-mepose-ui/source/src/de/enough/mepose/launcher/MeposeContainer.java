/*
 * Created on Aug 18, 2006 at 3:38:52 PM.
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
package de.enough.mepose.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Aug 18, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeContainer implements ISourceContainer {

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#dispose()
     */
    public void dispose() {
        // TODO rickyn implement dispose

    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#findSourceElements(java.lang.String)
     */
    public Object[] findSourceElements(String name) throws CoreException {
        // TODO rickyn implement findSourceElements
        return null;
    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getName()
     */
    public String getName() {
        // TODO rickyn implement getName
        return null;
    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getSourceContainers()
     */
    public ISourceContainer[] getSourceContainers() throws CoreException {
        // TODO rickyn implement getSourceContainers
        return null;
    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#getType()
     */
    public ISourceContainerType getType() {
        // TODO rickyn implement getType
        return null;
    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#init(org.eclipse.debug.core.sourcelookup.ISourceLookupDirector)
     */
    public void init(ISourceLookupDirector director) {
        // TODO rickyn implement init

    }

    /*
     * @see org.eclipse.debug.core.sourcelookup.ISourceContainer#isComposite()
     */
    public boolean isComposite() {
        // TODO rickyn implement isComposite
        return false;
    }

    /*
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // TODO rickyn implement getAdapter
        return null;
    }

}
