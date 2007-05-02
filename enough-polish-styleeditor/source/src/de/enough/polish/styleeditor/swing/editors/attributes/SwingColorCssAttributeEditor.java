/*
 * Created on Apr 29, 2007 at 10:10:56 PM.
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
package de.enough.polish.styleeditor.swing.editors.attributes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.editors.attributes.ColorCssAttributeEditor;
import de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor;
import de.enough.polish.ui.Color;

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
public class SwingColorCssAttributeEditor 
extends SwingCssAttributeEditor
{
	
	private final JButton colorButton;

	public SwingColorCssAttributeEditor() {
		this.colorButton = new JButton("...");
		this.colorButton.addActionListener(this);
		setName( "color" );
	}
	
	public void setPartEditor( StylePartEditor partEditor ) {
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		CssAttributeEditor editor = (CssAttributeEditor) partEditor;
		CssAttribute attribute = editor.getAttribute();
		removeAll();
		JPanel panel = new JPanel( new GridLayout( 1, 2, 5, 2 ));
		setName( attribute.getName() );
		panel.add( new JLabel( attribute.getName() + ": ") );
		panel.add( this.colorButton );
		add( panel );
		this.isIgnoreActionEvents = false;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor#showValue(java.lang.Object)
	 */
	protected void showValue(Object value) {
		if (value == null) {
			this.colorButton.setBackground( null );
		} else if (value instanceof Color) {
			Color color = (Color) value;
			this.colorButton.setBackground( new java.awt.Color( color.getColor() ) );
		} else if (value instanceof Integer) {
			this.colorButton.setBackground( new java.awt.Color( ((Integer)value).intValue() ) );
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		ColorCssAttributeEditor editor = (ColorCssAttributeEditor) stylePartEditor;
		Object source = event.getSource();
		if (source == this.colorButton) {
			java.awt.Color newColor = JColorChooser.showDialog( this, getName(), this.colorButton.getBackground() );
			if (newColor != null) {
				this.colorButton.setBackground(newColor);
				editor.setColor( newColor );
			}
		}		
	}
	

}
