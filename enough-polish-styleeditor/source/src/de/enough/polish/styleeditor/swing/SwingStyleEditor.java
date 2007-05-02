/*
 * Created on Apr 23, 2007 at 9:59:11 PM.
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
package de.enough.polish.styleeditor.swing;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StyleEditorVisual;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.StylePartEditorVisual;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingStyleEditor extends JTabbedPane 
implements StyleEditorVisual
{
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorVisual#getPartEditorVisual(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public StylePartEditorVisual getPartEditorVisual(StylePartEditor partEditor) {
		try {
			String className =  partEditor.getClass().getName();
			className = className.substring( className.lastIndexOf('.') + 1 );
			Class editorClass = Class.forName( "de.enough.polish.styleeditor.swing.editors.Swing" +className );
			SwingStylePartEditor visual = (SwingStylePartEditor) editorClass.newInstance();
			addPartEditor( partEditor, visual );
			return visual;
		} catch (ClassNotFoundException e) {
			// TODO robertvirkus handle ClassNotFoundException
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO robertvirkus handle InstantiationException
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO robertvirkus handle IllegalAccessException
			e.printStackTrace();
		}
		// TODO robertvirkus implement getPartEditorVisual
		return null;
	}

	/**
	 * @param visual
	 */
	private void addPartEditor(StylePartEditor partEditor, SwingStylePartEditor visual) {
		int index = getTabCount();
		visual.setIndex(index);
		addTab( partEditor.getName(),  new JScrollPane( visual ) );	
		visual.setParent( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorVisual#setStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void setStyle(EditStyle style) {
		// TODO robertvirkus implement setStyle
		
	}

	/**
	 * @param name
	 * @param editor
	 */
	public void setPartEditorName(String name, SwingStylePartEditor editor) {
		int index = editor.getIndex();
		if (index != -1) {
			setTitleAt(index, name);
		}
		
	}

}
