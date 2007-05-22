/*
 * Created on May 9, 2007 at 4:04:40 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.styleeditor;

import de.enough.polish.resources.swing.ResourcesTree;
import de.enough.polish.runtime.Simulation;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 9, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultStyleEditorListener
implements StyleEditorListener
{
	
	private final ResourcesTree resourcesTree;
	private final Simulation simulation;

	public DefaultStyleEditorListener( ResourcesTree resourcesTree, Simulation simulation ) {
		this.resourcesTree = resourcesTree;
		this.simulation = simulation;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorListener#notifyStyleAttached(de.enough.polish.styleeditor.ItemOrScreen, de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleAttached(ItemOrScreen itemOrScreen, EditStyle style) {
	    this.resourcesTree.selectStyle(style);	
	    this.simulation.getCurrentDisplayable()._requestRepaint();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorListener#notifyStyleCreated(de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleCreated(EditStyle style) {
	    this.resourcesTree.selectStyle(style);	
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
	 */
	public void notifyStyleUpdated(EditStyle editStyle) {
		ItemOrScreen itemOrScreen = editStyle.getItemOrScreen();
		//System.out.println("notifyStyleUpdated");
		if (itemOrScreen != null) {
			itemOrScreen.setStyle( editStyle.getStyle() );
			itemOrScreen.requestInit();
		} else {
			System.out.println("notifyStyleUpdated(EditStyle style):  !!!!!!!style.getItemOrScreen() is NULL !!!!!!!!");
		}
		this.simulation.getCurrentDisplayable()._requestRepaint();
	}

}
