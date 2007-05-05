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

import javax.microedition.lcdui.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.enough.polish.resources.ColorProvider;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.TextEditor;
import de.enough.polish.styleeditor.editors.attributes.ColorCssAttributeEditor;
import de.enough.polish.styleeditor.swing.SwingStylePartEditor;
import de.enough.polish.styleeditor.swing.components.ColorChooserComponent;
import de.enough.polish.styleeditor.swing.components.ColorChooserListener;
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
public class SwingTextEditor 
extends SwingStylePartEditor
implements ColorChooserListener
{
	private final JComboBox fontFace;
	private final JComboBox fontSize;
	private final JButton fontColorButton;
	private final JCheckBox fontStyleBold;
	private final JCheckBox fontStyleItalic;
	private final JCheckBox fontStyleUnderlined;

	private final JComboBox textEffect;
	private String[] textEffectNames;

	public SwingTextEditor() {
		this.fontFace = new JComboBox( new String[]{ "system", "proportional", "monospace" } );
		this.fontFace.addActionListener( this );
		this.fontSize = new JComboBox( new String[]{ "small", "medium", "large" } );
		this.fontSize.addActionListener( this );
		this.fontColorButton = new JButton("...");
		this.fontColorButton.addActionListener( this );
		this.fontStyleBold = new JCheckBox( "bold" );
		this.fontStyleBold.addActionListener( this );
		this.fontStyleItalic = new JCheckBox( "italic" );
		this.fontStyleItalic.addActionListener( this );
		this.fontStyleUnderlined = new JCheckBox( "underlined" );
		this.fontStyleUnderlined.addActionListener( this );
		this.textEffect = new JComboBox();
		this.textEffect.addItem( NO_SELECTION );
		this.textEffect.addActionListener( this );

		JPanel stylePanel = new JPanel( new GridLayout( 3, 1, 5, 2 ) );
		stylePanel.add( this.fontStyleBold );
		stylePanel.add( this.fontStyleItalic );
		stylePanel.add( this.fontStyleUnderlined );
		JPanel leftPanel = new JPanel( new GridLayout( 3, 2, 5, 2 ) );
		leftPanel.add( new JLabel("face:") );
		leftPanel.add( this.fontFace );
		leftPanel.add( new JLabel("size:") );
		leftPanel.add( this.fontSize );
		leftPanel.add( new JLabel("color:") );
		leftPanel.add( this.fontColorButton );
		JPanel basicPanel = new JPanel( new BorderLayout() );
		SwingUtil.setTitle( basicPanel, "basic");
		basicPanel.add( leftPanel, BorderLayout.CENTER );
		basicPanel.add( stylePanel, BorderLayout.EAST );
		
		JPanel effectPanel = new JPanel( new BorderLayout() );
		SwingUtil.setTitle( effectPanel, "effect");
		effectPanel.add( this.textEffect, BorderLayout.NORTH );
		effectPanel.add( new JScrollPane(this.attributesPanel), BorderLayout.CENTER );
		SwingUtil.setTitle( effectPanel, "text effect");
		
		add( basicPanel, BorderLayout.NORTH );
		add( effectPanel, BorderLayout.CENTER );

	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#setPartEditor(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public void setPartEditor(StylePartEditor partEditor) {
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		TextEditor editor = (TextEditor) partEditor;
		this.textEffectNames = editor.getTextEffectNames();
		for (int i = 0; i < this.textEffectNames.length; i++) {
			this.textEffect.addItem( this.textEffectNames[i] );
		}
		this.isIgnoreActionEvents = false;
	}


	private int getFontFace() {
		switch (this.fontFace.getSelectedIndex()) {
			case 0: return Font.FACE_SYSTEM;
			case 1: return Font.FACE_PROPORTIONAL;
			default: return Font.FACE_MONOSPACE;
		}
	}

	private int getFontSize() {
		switch (this.fontSize.getSelectedIndex()) {
			case 0: return Font.SIZE_SMALL;
			case 1: return Font.SIZE_MEDIUM;
			default: return Font.SIZE_LARGE;
		}
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		TextEditor editor = (TextEditor) stylePartEditor;
		Object source = event.getSource();
		if (source == this.fontColorButton) {
			ColorProvider colorProvider = ColorChooserComponent.showDialog(getName(), editor.getColorProvider(), editor.getResourcesProvider(), this, false );
			if (colorProvider != null) {
				this.fontColorButton.setBackground( new java.awt.Color( colorProvider.getColor().getColor() ) );
				editor.setColorProvider( colorProvider );
			}
		} else if (source == this.fontFace) {
			editor.setFace( getFontFace() );
		} else if (source == this.fontSize) {
			editor.setSize( getFontSize() );
		} else if (source == this.fontStyleBold) {
			editor.setStyleBold( this.fontStyleBold.isSelected() );
		} else if (source == this.fontStyleItalic) {
			editor.setStyleItalic( this.fontStyleItalic.isSelected() );
		} else if (source == this.fontStyleUnderlined) {
			editor.setStyleUnderlined( this.fontStyleUnderlined.isSelected() );
		} else if (source == this.textEffect) {
			String textEffectName = (String) this.textEffect.getItemAt( this.textEffect.getSelectedIndex() );
			if (NO_SELECTION.equals(textEffectName)) {
				textEffectName = null;
			}
			editor.setTextEffect( textEffectName );
		}	
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.components.ColorChooserListener#notifyColorUpdated(de.enough.polish.resources.ColorProvider)
	 */
	public void notifyColorUpdated(ColorProvider colorProvider) {
		TextEditor editor = (TextEditor) this.partEditor;
		editor.setColorProvider( colorProvider );
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#updateGui(de.enough.polish.styleeditor.StylePartEditor)
	 */
	protected void updateGui(StylePartEditor stylePartEditor) {
		TextEditor editor = (TextEditor) stylePartEditor;
		
		if (editor.isSizeSmall()) {
			this.fontSize.setSelectedIndex(0);
		} else if (editor.isSizeMedium()) {
			this.fontSize.setSelectedIndex(1);
		} else {
			this.fontSize.setSelectedIndex(2);
		}
		if (editor.isFaceSystem()) {
			this.fontFace.setSelectedIndex(0);
		} else if (editor.isFaceProportional()) {
			this.fontFace.setSelectedIndex(1);
		} else {
			this.fontFace.setSelectedIndex(2);
		}
		this.fontStyleBold.setSelected( editor.isStyleBold() );
		this.fontStyleItalic.setSelected( editor.isStyleItalic() );
		this.fontStyleUnderlined.setSelected( editor.isStyleUnderlined() );
		
		this.fontColorButton.setBackground(  editor.getColorForAwt() );
		
		String textEffectName = editor.getTextEffect();
		if (textEffectName == null) {
			if (this.textEffect.getSelectedIndex() != 0) {
				this.textEffect.setSelectedIndex(0);
			} 
		} else {
			for (int i = 0; i < this.textEffectNames.length; i++) {
				String name = this.textEffectNames[i];
				if (textEffectName.equals(name)) {
					this.textEffect.setSelectedIndex(i+1);
					break;
				}
			}
		}
	}

}
