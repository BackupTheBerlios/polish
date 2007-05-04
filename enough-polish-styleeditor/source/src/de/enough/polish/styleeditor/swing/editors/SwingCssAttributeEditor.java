/*
 * Created on Apr 29, 2007 at 10:11:42 PM.
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
package de.enough.polish.styleeditor.swing.editors;

import java.awt.event.ActionEvent;

import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class SwingCssAttributeEditor 
extends SwingStylePartEditor 
{
	protected CssAttributeEditor attributeEditor;
	
	protected abstract void showValue( CssAttributeValue value );


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		CssAttributeEditor editor = (CssAttributeEditor) this.partEditor;
		this.attributeEditor = editor;
//		System.out.println("SwingCssAttributeEditor: Showing value " + editor.getValue() );
//		try {
//			throw new RuntimeException();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		showValue( editor.getValue() );
	}
	
	

}
