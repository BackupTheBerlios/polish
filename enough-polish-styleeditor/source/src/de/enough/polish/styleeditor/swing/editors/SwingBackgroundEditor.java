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
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.microedition.lcdui.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.BackgroundEditor;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.editors.CssAttributesEditor;
import de.enough.polish.styleeditor.editors.TextEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;
import de.enough.polish.util.StringUtil;
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
public class SwingBackgroundEditor 
extends SwingStylePartEditor
{
	private final JComboBox backgrounds;

	private String[] backgroundNames;

	public SwingBackgroundEditor() {

		this.backgrounds = new JComboBox();
		this.backgrounds.addItem( NO_SELECTION );
		this.backgrounds.addActionListener( this );

		
		JPanel effectPanel = new JPanel( new BorderLayout() );
		SwingUtil.setTitle( effectPanel, "background");
		effectPanel.add( this.backgrounds, BorderLayout.NORTH );
		
		add( effectPanel, BorderLayout.NORTH );
		add( new JScrollPane(this.attributesPanel), BorderLayout.CENTER );

	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#setPartEditor(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public void setPartEditor(StylePartEditor partEditor) {
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		BackgroundEditor editor = (BackgroundEditor) partEditor;
		this.backgroundNames = editor.getBackgroundNames();
		for (int i = 0; i < this.backgroundNames.length; i++) {
			this.backgrounds.addItem( this.backgroundNames[i] );
		}
		this.isIgnoreActionEvents = false;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		BackgroundEditor editor = (BackgroundEditor) this.partEditor;
		Object source = event.getSource();
		System.out.println("SwingBackgroundEditor.actionPerformed for " + source );
		if (source == this.backgrounds) {
			String backgroundName = (String) this.backgrounds.getItemAt( this.backgrounds.getSelectedIndex() );
			if (NO_SELECTION.equals(backgroundName)) {
				backgroundName = null;
			}
			editor.setBackground( backgroundName );
		}
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		BackgroundEditor editor = (BackgroundEditor) stylePartEditor;
		String backgroundName = editor.getBackgroundName();
		if (backgroundName == null) {
			if (this.backgrounds.getSelectedIndex() != 0) {
				this.backgrounds.setSelectedIndex(0);
			}
		} else if (this.backgroundNames != null){
			for (int i=0; i<this.backgroundNames.length; i++) {
				String name = this.backgroundNames[i];
				if (backgroundName.equals(name)) {
					this.backgrounds.setSelectedIndex(i+1);
					break;
				}
			}
		}
	}
	
}
