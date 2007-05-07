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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.editors.attributes.IntegerCssAttributeEditor;
import de.enough.polish.styleeditor.editors.attributes.StyleCssAttributeEditor;
import de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor;
import de.enough.polish.swing.IntegerTextField;
import de.enough.polish.ui.Style;

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
public class SwingStyleCssAttributeEditor 
extends SwingCssAttributeEditor
{
	
	private final JComboBox styleNamesComboBox;
	private String[] styleNames;

	public SwingStyleCssAttributeEditor() {
		this.styleNamesComboBox = new JComboBox();
		this.styleNamesComboBox.addActionListener(this);
	}
	
	public void setPartEditor( StylePartEditor partEditor ) {
		//System.out.println("setPartEditor " + partEditor );
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		StyleCssAttributeEditor editor = (StyleCssAttributeEditor) partEditor;
		CssAttribute attribute = editor.getAttribute();
		this.styleNames = editor.getStyleNames();
		removeAll();
		this.styleNamesComboBox.removeAllItems();
		JPanel panel = new JPanel( new GridLayout( 1, 2, 5, 2 ));
		panel.add( new JLabel( attribute.getName() + ": ") );
		if (this.styleNames != null) {
			String defaultValue = attribute.getDefaultValue();
			if (defaultValue == null) {
				this.styleNamesComboBox.addItem(NO_SELECTION);
			}
			for (int i = 0; i < this.styleNames.length; i++) {
				String styleName = this.styleNames[i];
				//System.out.println( i + "/" + allowedValues.length + ": " + allowedValue );
				this.styleNamesComboBox.addItem(styleName);
			}
			panel.add( this.styleNamesComboBox );
		}
		add( panel );
		this.isIgnoreActionEvents = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		Object source = event.getSource();
		StyleCssAttributeEditor editor = (StyleCssAttributeEditor) stylePartEditor;
		if (source == this.styleNamesComboBox){
			int selectedIndex = this.styleNamesComboBox.getSelectedIndex();
			if (editor.getDefaultValue() == null) {
				selectedIndex--;
			}
			if (selectedIndex == -1) {
				editor.setStyle( (String)null);
			} else {
				editor.setStyle( (String)this.styleNamesComboBox.getItemAt(selectedIndex));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor#showValue(java.lang.Object)
	 */
	protected void showValue(CssAttributeValue attributeValue) {
		Object value = attributeValue.getValue();
		if (value == null) {
			if (this.styleNamesComboBox.getItemCount() > 0) {
				this.styleNamesComboBox.setSelectedIndex( 0 );
			}
		} else if (this.styleNames != null) {
			int selectedIndex = 0;
			String styleName =((Style)value).name;
			for (int i = 0; i < this.styleNames.length; i++) {
				String name = this.styleNames[i];
				if (name.equalsIgnoreCase(styleName)) {
					selectedIndex = i;
					break;
				}
			}
			if (this.attributeEditor.getAttribute().getDefaultValue() == null) {
				selectedIndex++;
			}
			this.styleNamesComboBox.setSelectedIndex(selectedIndex);
		}
	}
	

}
