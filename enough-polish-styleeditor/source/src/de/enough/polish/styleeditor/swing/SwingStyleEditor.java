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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import de.enough.polish.resources.StyleProvider;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.ItemOrScreen;
import de.enough.polish.styleeditor.StyleEditor;
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
public class SwingStyleEditor extends JPanel 
implements StyleEditorVisual
{
	
	private final JTabbedPane styleEditorVisual;
	private final JLabel idleLabel;
	private final StyleEditor styleEditor;
	private boolean isInStyleEditor;
	private ItemOrScreen currentItemOrScreen;
	
	public SwingStyleEditor( StyleEditor styleEditor ) {
		super( new BorderLayout() );
		this.styleEditor = styleEditor;
		this.idleLabel = new JLabel("Please select a screen or a screen's item to design it.");
		this.styleEditorVisual = new JTabbedPane();
		
		add( this.idleLabel, BorderLayout.CENTER );
	}

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
		int index = this.styleEditorVisual.getTabCount();
		visual.setIndex(index);
		this.styleEditorVisual.addTab( partEditor.getName(),  new JScrollPane( visual ) );	
		visual.setParent( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorVisual#setStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void editStyle(EditStyle style) {
		if (!this.isInStyleEditor) {
			//System.out.println("Entering style editing mode...");
			removeAll();
			add( this.styleEditorVisual );
			this.isInStyleEditor = true;
			SwingUtilities.updateComponentTreeUI( this );
		}
	}

	/**
	 * @param name
	 * @param editor
	 */
	public void setPartEditorName(String name, SwingStylePartEditor editor) {
		int index = editor.getIndex();
		if (index != -1) {
			this.styleEditorVisual.setTitleAt(index, name);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StyleEditorVisual#chooseOrCreateStyleFor(de.enough.polish.styleeditor.ItemOrScreen, de.enough.polish.resources.StyleProvider[])
	 */
	public void chooseOrCreateStyleFor(ItemOrScreen itemOrScreen, StyleProvider[] availableStyles) {
		this.currentItemOrScreen = itemOrScreen;
		this.isInStyleEditor = false;
		CreateOrSelectStyleFlow flow = new CreateOrSelectStyleFlow( this, this.styleEditor.getChooseOrCreateDescription( itemOrScreen ), availableStyles, this.styleEditor.getChooseOrCreateSuggestion( itemOrScreen ) );
		removeAll();
		add( flow, BorderLayout.CENTER );
		SwingUtilities.updateComponentTreeUI( this );
	}

	public void createOrEditStyle(String styleName ) {
		this.styleEditor.attachStyle(this.currentItemOrScreen, styleName);
	}

}
