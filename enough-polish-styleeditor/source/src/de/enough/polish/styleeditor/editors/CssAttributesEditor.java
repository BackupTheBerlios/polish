/*
 * Created on Apr 29, 2007 at 4:31:26 AM.
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
package de.enough.polish.styleeditor.editors;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StyleEditor;
import de.enough.polish.styleeditor.StylePartEditor;

/**
 * <p>Edits various CSS attributes</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttributesEditor extends StylePartEditor {
	
	private CssAttribute[] cssAttributes;
	private CssAttributeEditor[] cssAttributeEditors;

	public CssAttributesEditor() {
	}

	public CssAttributesEditor( StyleEditor styleEditor ) {
		setStyleEditor(styleEditor);
	}
	
	public void setStyleEditor( StyleEditor styleEditor ) {
		this.styleEditor = styleEditor;
	}

	public void setCssAttributes( CssAttribute[] attributes ) {
		// for each attribute get the corresponding editor, e.g. "integer", "color", "mapping" etc 
		this.cssAttributes = attributes;
		if (attributes == null) {
			this.cssAttributeEditors = null;
		} else {
			CssAttributeEditor[] editors = new CssAttributeEditor[attributes.length];
			for (int i = 0; i < attributes.length; i++) {
				CssAttribute attribute = attributes[i];
				CssAttributeEditor editor = getEditor( attribute );
				if (editor != null) {
					editor.setParent(this);
					editor.setStyleEditor(this.styleEditor);
				}
				editors[i] = editor;
			}
			this.cssAttributeEditors = editors;
		}
		
	}
	
	public CssAttributeEditor[] getEditors() {
		return this.cssAttributeEditors;
	}

	/**
	 * @param attribute
	 * @return
	 */
	private CssAttributeEditor getEditor(CssAttribute attribute) {
		String className = attribute.getClass().getName();
		int lastDotPos = className.lastIndexOf('.');
		if (lastDotPos != -1) {
			className = className.substring(lastDotPos+1);
		}
		className = "de.enough.polish.styleeditor.editors.attributes." + className + "Editor";
		try {
			CssAttributeEditor editor = (CssAttributeEditor) Class.forName(className).newInstance();
			editor.setCssAttribute(attribute);
			return editor;
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Warning: unable to instantiate CssAttributeEditor " + className );
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "attributes";
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		CssAttributeEditor[] editors = this.cssAttributeEditors;
		if (editors != null) {
			for (int i = 0; i < editors.length; i++) {
				CssAttributeEditor editor = editors[i];
				if (editor != null) {
					editor.setStyle( style );
				}
			}
		}
	}
	
	public void readStyle( Object object ) {
		if (this.cssAttributes != null && this.cssAttributeEditors != null && object != null) {
			for (int i = 0; i < this.cssAttributes.length; i++) {
				CssAttributeEditor editor = this.cssAttributeEditors[i];
				if (editor != null) {
					editor.readStyle(object);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void writeStyle(EditStyle style) {
		
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#setStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void setStyle(EditStyle style) {
		System.out.println( this + ".setStyle(" + style + ")");
		super.setStyle(style);
	}

	public CssAttributeValue[] getAttributeValues() {
		CssAttributeEditor[] editors = this.cssAttributeEditors;
		CssAttributeValue[] values = new CssAttributeValue[ editors.length ];
		for (int i = 0; i < editors.length; i++) {
			CssAttributeEditor editor = editors[i];
			values[i] = editor.getValue();
		}
		return values;
	}

	public Object[] getAttributeValuesAsObjects() {
		CssAttributeEditor[] editors = this.cssAttributeEditors;
		Object[] values = new Object[ editors.length ];
		for (int i = 0; i < editors.length; i++) {
			CssAttributeEditor editor = editors[i];
			Object value = null;
			if (editor != null) {
				value = editor.getValueAsObject();
				//System.out.println("getAttributeValues: " + i + "=" + value);
			}
			if (value == null) {
				CssAttribute attribute = this.cssAttributes[i];
				value = attribute.instantiateDefault(getEnvironment());
			}
			values[i] = value;
		}
		return values;
	}

	/**
	 * @return
	 */
	public CssAttribute[] getAttributes() {
		return this.cssAttributes;
	}

}
