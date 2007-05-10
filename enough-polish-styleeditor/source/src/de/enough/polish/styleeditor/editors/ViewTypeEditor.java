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

import java.util.Arrays;

import javax.microedition.lcdui.Font;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.ItemOrScreen;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.TextEffect;

/**
 * <p>Edits the view-type of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ViewTypeEditor extends StylePartEditor {

	private CssAttribute viewTypeAttribute;
	private String viewTypeName;
	private CssAttributesEditor viewTypeEditor;
	private ItemView viewType;


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(EditStyle)
	 */
	public void writeStyle( EditStyle style ) {
		//System.out.println("View-Type-Editor: writeStyle()");
		if (this.viewTypeName == null) {
			style.removeAttribute( this.viewTypeAttribute );
		} else {
			style.addAttribute( new CssAttributeValue( this.viewTypeAttribute, this.viewType, this.viewTypeName ) );
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		initViewTypeAttribute();
		this.viewType = (ItemView) style.getStyle().getObjectProperty( this.viewTypeAttribute.getId() );
		this.viewTypeName = null;
		if (this.viewType == null) {
			this.viewTypeEditor = null;
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		} else {
			CssMapping mapping = this.viewTypeAttribute.getMappingByTo( this.viewType.getClass().getName() );
			if (mapping != null) {
				setViewType( mapping.getFrom(), false );
				this.viewTypeEditor.setStyle(style);
				if (this.visual != null) {
					this.visual.setCssAttributes( this.viewTypeEditor.getAttributes(),this.viewTypeEditor.getEditors() );
				}
			}
		}
	}
	
	private void initViewTypeAttribute() {
		if (this.viewTypeAttribute == null) {
			this.viewTypeAttribute = getAttribute( "view-type" );
		}
	}
	
	public String[] getViewTypeNames() {
		if (this.editStyle == null) {
			return new String[0];
		}
		ItemOrScreen itemOrScreen = this.editStyle.getItemOrScreen();
		if (itemOrScreen == null) {
			return new String[0];
		}
		Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
		if (itemOrScreenObj == null) {
			return new String[0];
		}
		initViewTypeAttribute();
		CssMapping[] mappings = this.viewTypeAttribute.getApplicableMappings( itemOrScreenObj.getClass() );
		String[] names = new String[ mappings.length ];
		for (int i = 0; i < names.length; i++) {
			CssMapping mapping = mappings[i];
			names[i] = mapping.getFrom();
		}
		Arrays.sort(names);
		return names;
	}
	
	public String getViewType() {
		return this.viewTypeName;
	}
	
	/**
	 * @param viewTypeName
	 */
	public void setViewType(String viewTypeName) {
		setViewType(viewTypeName, true);
	}
	public void setViewType(String viewTypeName, boolean update) {
		//System.out.println("setting view-type-name " + viewTypeName);
		// remove previous settings from style:
		if (this.viewTypeEditor != null) {
			this.editStyle.removeAttributes( this.viewTypeEditor.getAttributes() );
			if (this.visual != null) {
				this.visual.setCssAttributes(null, null);
			}
		}
		initViewTypeAttribute();
		this.viewTypeName = viewTypeName;
		if (viewTypeName == null) {
			this.viewTypeEditor = null;
			this.viewType = null;
		} else {
			this.viewTypeEditor = new CssAttributesEditor(this.styleEditor);
			CssMapping mapping = this.viewTypeAttribute.getMapping(viewTypeName);
			if (mapping != null) {
				try {
					Class effectClass = Class.forName( mapping.getToClassName() );
					this.viewType = (ItemView) effectClass.newInstance();
					CssAttribute[] attributes = getApplicableAttributes(effectClass);
					this.viewTypeEditor.setCssAttributes(attributes);
					this.viewTypeEditor.setStyle(this.editStyle);
					if (this.visual != null) {
						this.visual.setCssAttributes( attributes, this.viewTypeEditor.getEditors() );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (update) {
			update();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "view-type";
	}


	
	

}
