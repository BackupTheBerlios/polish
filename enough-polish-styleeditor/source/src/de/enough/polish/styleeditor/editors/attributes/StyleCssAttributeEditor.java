/*
 * Created on Apr 29, 2007 at 11:40:15 AM.
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
package de.enough.polish.styleeditor.editors.attributes;

import java.util.Arrays;

import de.enough.polish.resources.StyleProvider;
import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.ui.Style;

/**
 * <p>Edits style attributes</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StyleCssAttributeEditor extends CssAttributeEditor {
	
	protected StyleProvider value;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#getValueAsObject()
	 */
	public Object getValueAsObject() {
		if (this.value == null) {
			return null;
		}
		return this.value.getStyle();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#getValue()
	 */
	public CssAttributeValue getValue() {
		return new CssAttributeValue( this.attribute, getValueAsObject(), getValueAsString());
	}



	/**
	 * @return
	 */
	private String getValueAsString() {
		if (this.value == null) {
			return null;
		}
		return this.value.getName();
	}



	public void setStyle(String name) {
		if (name == null) {
			this.value = null;
		} else {
			this.value = getResourcesProvider().getStyle(name);
		}
		update();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if (value == null) {
			this.value = null;
		} else {
			Style style = (Style) value;
			this.value = getResourcesProvider().getStyle( style.name );
		}
	}
	
	public String[] getStyleNames() {
		StyleProvider[] styles = getResourcesProvider().getStyles();
		String[] styleNames = new String[ styles.length ];
		for (int i = 0; i < styleNames.length; i++) {
			StyleProvider style = styles[i];
			styleNames[i] = style.getName();
		}
		Arrays.sort( styleNames );
		return styleNames;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#instantiateDefault()
	 */
	protected Object instantiateDefault() {
		String defaultStyleName = this.attribute.getDefaultValue();
		StyleProvider styleProvider = getResourcesProvider().getStyle(defaultStyleName);
		if (styleProvider != null) {
			return styleProvider.getStyle();
		} else {
			return null;
		}
	}


	
	
	

}
