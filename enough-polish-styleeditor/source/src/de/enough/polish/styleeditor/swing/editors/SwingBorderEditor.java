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
import de.enough.polish.styleeditor.editors.BorderEditor;
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
public class SwingBorderEditor 
extends SwingStylePartEditor
{

	private final JComboBox bordersComboBox;

	private String[] backgroundNames;

	public SwingBorderEditor() {

		this.bordersComboBox = new JComboBox();
		this.bordersComboBox.addItem( NO_SELECTION );
		this.bordersComboBox.addActionListener( this );

		
		JPanel effectPanel = new JPanel( new BorderLayout() );
		SwingUtil.setTitle( effectPanel, "border");
		effectPanel.add( this.bordersComboBox, BorderLayout.NORTH );
		
		add( effectPanel, BorderLayout.NORTH );
		add( new JScrollPane(this.attributesPanel), BorderLayout.CENTER );

	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#setPartEditor(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public void setPartEditor(StylePartEditor partEditor) {
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		BorderEditor editor = (BorderEditor) partEditor;
		this.backgroundNames = editor.getBorderNames();
		for (int i = 0; i < this.backgroundNames.length; i++) {
			this.bordersComboBox.addItem( this.backgroundNames[i] );
		}
		this.isIgnoreActionEvents = false;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		BorderEditor editor = (BorderEditor) this.partEditor;
		Object source = event.getSource();
		if (source == this.bordersComboBox) {
			String backgroundName = (String) this.bordersComboBox.getItemAt( this.bordersComboBox.getSelectedIndex() );
			if (NO_SELECTION.equals(backgroundName)) {
				backgroundName = null;
			}
			editor.setBorder( backgroundName );
		}
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		BorderEditor editor = (BorderEditor) stylePartEditor;
		String backgroundName = editor.getBorderName();
		if (backgroundName == null) {
			if (this.bordersComboBox.getSelectedIndex() != 0) {
				this.bordersComboBox.setSelectedIndex(0);
			}
		} else if (this.backgroundNames != null){
			for (int i=0; i<this.backgroundNames.length; i++) {
				String name = this.backgroundNames[i];
				if (backgroundName.equals(name)) {
					this.bordersComboBox.setSelectedIndex(i+1);
					break;
				}
			}
		}
	}
	
}
