/*
 * Created on Apr 24, 2007 at 2:06:25 AM.
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.ViewTypeEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;
import de.enough.polish.util.SwingUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 24, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingViewTypeEditor 
extends SwingStylePartEditor
{
	private final JComboBox viewTypeComboBox;

	public SwingViewTypeEditor() {
		this.viewTypeComboBox = new JComboBox();
		this.viewTypeComboBox.addItem( NO_SELECTION );
		this.viewTypeComboBox.addActionListener( this );

		
		JPanel effectPanel = new JPanel( new BorderLayout() );
		effectPanel.add( this.viewTypeComboBox, BorderLayout.NORTH );
		effectPanel.add( new JScrollPane(this.attributesPanel), BorderLayout.CENTER );
		SwingUtil.setTitle( effectPanel, "view type");
		
		add( effectPanel, BorderLayout.NORTH );

	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#setPartEditor(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public void setPartEditor(StylePartEditor partEditor) {
		super.setPartEditor(partEditor);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		ViewTypeEditor editor = (ViewTypeEditor) stylePartEditor;
		Object source = event.getSource();
		if (source ==  this.viewTypeComboBox) {
			String textEffectName = (String) this.viewTypeComboBox.getItemAt( this.viewTypeComboBox.getSelectedIndex() );
			if (NO_SELECTION.equals(textEffectName)) {
				textEffectName = null;
			}
			editor.setViewType( textEffectName );
		}	
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		ViewTypeEditor editor = (ViewTypeEditor) stylePartEditor;
		
		String viewTypeName = editor.getViewType();
		String[] viewTypeNames = editor.getViewTypeNames();
		this.viewTypeComboBox.removeAllItems();
		this.viewTypeComboBox.addItem( NO_SELECTION );
		for (int i = 0; i < viewTypeNames.length; i++) {
			String name = viewTypeNames[i];
			this.viewTypeComboBox.addItem( name );
			if (name.equals(viewTypeName)) {
				this.viewTypeComboBox.setSelectedIndex( i + 1 );
			}
		}
		if (viewTypeName == null) {
			this.viewTypeComboBox.setSelectedIndex(0);
		}
	}

}
