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

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.editors.attributes.StringCssAttributeEditor;
import de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor;
import de.enough.polish.swing.IntegerTextField;

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
public class SwingStringCssAttributeEditor 
extends SwingCssAttributeEditor
{
	
	private final JTextField textField;

	public SwingStringCssAttributeEditor() {
		this.textField = new JTextField();
		this.textField.addActionListener(this);
	}
	
	public void setPartEditor( StylePartEditor partEditor ) {
		//System.out.println("setPartEditor " + partEditor );
		super.setPartEditor(partEditor);
		this.isIgnoreActionEvents = true;
		CssAttributeEditor editor = (CssAttributeEditor) partEditor;
		CssAttribute attribute = editor.getAttribute();
		String[] allowedValues = attribute.getAllowedValues();
		removeAll();
		JPanel panel = new JPanel( new GridLayout( 1, 2, 5, 2 ));
		panel.add( createLabel( editor )  );
		this.textField.setText( attribute.getDefaultValue() );
		panel.add( this.textField );
		add( panel );
		this.isIgnoreActionEvents = false;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.SwingStylePartEditor#handleActionPerformed(de.enough.polish.styleeditor.StylePartEditor, java.awt.event.ActionEvent)
	 */
	protected void handleActionPerformed(StylePartEditor stylePartEditor, ActionEvent event) {
		Object source = event.getSource();
		StringCssAttributeEditor editor = (StringCssAttributeEditor) stylePartEditor;
		if (source == this.textField) {
			editor.setString( this.textField.getText()  );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor#showValue(java.lang.Object)
	 */
	protected void showValue(CssAttributeValue attributeValue) {
		Object value = attributeValue.getValue();
		if (value == null) {
			this.textField.setText( null );
		} else {
			this.textField.setText( value.toString() );
		}
	}
	

}
