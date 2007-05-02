/*
 * Created on Apr 23, 2007 at 9:49:59 PM.
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
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;

/**
 * <p>Edits border settings of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BorderEditor extends ParameterizedStyleEditor {

	private Border border;
	private CssAttribute borderAttribute;
	private EditStyle borderEditStyle = new EditStyle();
	private String fromName;


	
	private void initBorderAttribute() {
		if (this.borderAttribute == null) {
			this.borderAttribute = getAttribute( "border" );
		}
	}
	
	public String[] getBorderNames() {
		initBorderAttribute();
		String[] names = getMappingNames( this.borderAttribute );
		return names;
	}
	
	/**
	 * @param borderName
	 */
	public void setBorder(String borderName) {
		initBorderAttribute();
		this.fromName = borderName;
		this.border = null;
		this.attributesEditor = null;
		this.borderEditStyle = new EditStyle();
		if (borderName == null) {
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		} else {
			this.border = (Border) instantiateDefault( this.borderAttribute, borderName );
			CssAttributesEditor editor = getAttributesEditor(borderName);
			if (this.visual != null && editor != null) {
				this.visual.setCssAttributes(editor.getAttributes(), editor.getEditors());
			}
		}
		update();
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "border";
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		System.out.println(this + ".readStyle(), attributesEditor==null: " + (this.attributesEditor == null));
		this.border = style.getStyle().border;
		// now extract background attributes:
		if (this.border != null) {
			initBorderAttribute();
			getAttributesEditorByTo(this.border.getClass().getName(), this.borderAttribute );
			this.attributesEditor.readStyle( this.border );
			if (this.visual != null) {
				this.visual.setCssAttributes(this.attributesEditor.getAttributes(), this.attributesEditor.getEditors());
			}
		} else { 
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void writeStyle(EditStyle style) {
		style.getStyle().border = this.border;
	}

	public CssAttributesEditor getAttributesEditorByTo(String className, CssAttribute attribute ) {
		CssMapping mapping = attribute.getMappingByTo(className);
		if (mapping == null) {
			return null;
		}
		return getAttributesEditor( mapping.getFrom() );
	}


	/**
	 * @param backgroundName
	 * @return
	 */
	public CssAttributesEditor getAttributesEditor(String backgroundName) {
		if (this.attributesEditor != null && this.attributesEditor.getName().equals(backgroundName)) {
			return this.attributesEditor;
		}
		System.out.println("creating attributes editor for background " + backgroundName );
		this.fromName = backgroundName;
		initBorderAttribute();
		this.attributesEditor = getAttributesEditor(this.borderAttribute, backgroundName, this.borderEditStyle);
		this.attributesEditor.setStyle(this.borderEditStyle);
		return this.attributesEditor;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#update()
	 */
	protected void update() {
		if (this.borderAttribute != null && this.fromName != null && this.attributesEditor != null) {
			this.attributesEditor = getAttributesEditor(this.fromName);
			this.border = (Border) instantiate( this.borderAttribute, this.fromName, this.attributesEditor.getAttributeValues() );
		}
		super.update();
	}


	/**
	 * @return
	 */
	public String getBorderName() {
		if (this.border == null) {
			return null;
		} else {
			return getFromName( this.border.getClass(), this.borderAttribute );
		}
	}


	protected String getFromName(Class instanceClass, CssAttribute attribute) {
		return getFromName( instanceClass.getName(), attribute );
	}


	/**
	 * @param name
	 * @param attribute
	 * @return
	 */
	protected String getFromName(String name, CssAttribute attribute) {
		CssMapping mapping = attribute.getMappingByTo( name );
		if (mapping != null) {
			System.out.println("Mapping to " + name + " is from " + mapping.getFrom() );
			return mapping.getFrom();
		}
		return null;
	}
	

}
