/*
 * Created on Apr 23, 2007 at 4:08:51 PM.
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

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.ui.Style;

/**
 * <p>Interacts with the visualization of a style part editor</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface StylePartEditorVisual {
	
	public void notifyStyleUpdated();
	
	public void setPartEditor( StylePartEditor partEditor );

	/**
	 * @param attributes
	 * @param editors 
	 */
	public void setCssAttributes(CssAttribute[] attributes, CssAttributeEditor[] editors);

}
