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

/**
 * <p>Edits background settings of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BackgroundEditor extends ParameterizedStyleEditor {

	private Background background;
	private CssAttribute backgroundAttribute;
	private EditStyle backgroundEditStyle = new EditStyle();
	private String fromName;


	
	private void initBackgroundAttribute() {
		if (this.backgroundAttribute == null) {
			this.backgroundAttribute = getAttribute( "background" );
		}
	}
	
	public String[] getBackgroundNames() {
		initBackgroundAttribute();
		String[] names = getMappingNames( this.backgroundAttribute );
		return names;
	}
	
	/**
	 * @param backgroundName
	 */
	public void setBackground(String backgroundName) {
		initBackgroundAttribute();
		this.fromName = backgroundName;
		this.background = null;
		this.attributesEditor = null;
		this.backgroundEditStyle = new EditStyle();
		if (backgroundName == null) {
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		} else {
			this.background = (Background) instantiateDefault( this.backgroundAttribute, backgroundName );
			CssAttributesEditor editor = getAttributesEditor(backgroundName);
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
		return "background";
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		System.out.println(this + ".readStyle(), attributesEditor==null: " + (this.attributesEditor == null));
		this.background = style.getStyle().background;
		// now extract background attributes:
		if (this.background != null) {
			initBackgroundAttribute();
			getAttributesEditorByTo(this.background.getClass().getName(), this.backgroundAttribute );
			this.attributesEditor.readStyle( this.background );
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
		style.getStyle().background = this.background;
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
		initBackgroundAttribute();
		this.attributesEditor = getAttributesEditor(this.backgroundAttribute, backgroundName, this.backgroundEditStyle);
		this.attributesEditor.setStyle(this.backgroundEditStyle);
		return this.attributesEditor;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#update()
	 */
	protected void update() {
		if (this.backgroundAttribute != null && this.fromName != null && this.attributesEditor != null) {
			this.attributesEditor = getAttributesEditor(this.fromName);
			this.background = (Background) instantiate( this.backgroundAttribute, this.fromName, this.attributesEditor.getAttributeValues() );
		}
		super.update();
	}


	/**
	 * @return
	 */
	public String getBackgroundName() {
		if (this.background == null) {
			return null;
		} else {
			return getFromName( this.background.getClass(), this.backgroundAttribute );
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
