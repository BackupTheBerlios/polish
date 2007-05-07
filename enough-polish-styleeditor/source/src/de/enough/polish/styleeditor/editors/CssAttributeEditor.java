/*
 * Created on Apr 29, 2007 at 10:45:09 AM.
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
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.util.ReflectionUtil;

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
public abstract class CssAttributeEditor extends StylePartEditor {
	
	protected CssAttribute attribute;

	public CssAttributeEditor() {
		// create a new instance
	}
	
	public void setCssAttribute( CssAttribute attribute ) {
		this.attribute = attribute;
	}
	
	public abstract CssAttributeValue getValue();

	public abstract Object getValueAsObject();
	
	protected abstract void setValue(Object value);
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#createName()
	 */
	public String createName() {
		return (this.attribute == null ? "CSS attribute" : this.attribute.getName() );
	}
	
	public CssAttribute getAttribute() {
		return this.attribute;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void writeStyle(EditStyle style) {
		//System.out.println("CssAttributeEditor.writeStyle: EditStyle=" + style + ", attribute=" + attribute + ", Style=" + (style == null ? "null" : style.getStyle() ));
		
		style.addAttribute( getValue() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		//System.out.println(this + ".readStyle(" + style+ ")");
		Object value = style.getStyle().getObjectProperty( this.attribute.getId() );
		if (value == null) {
			value = instantiateDefault();
			//value = this.attribute.instantiateValue( this.attribute.getValue( this.attribute.getDefaultValue(), getEnvironment()) );
		}
		setValue( value );
	}

	/**
	 * @return
	 */
	protected Object instantiateDefault() {
		Object value;
		value = this.attribute.instantiateDefault( getEnvironment() );
		return value;
	}
	
	public void readStyle( Object styleSource ) {
		//System.out.println(this + ".readStyle(" + styleSource + ")");
		Object value = getFieldValue( styleSource );
		if (value == null) {
			value = instantiateDefault();
		}
		setValue( value );
	}
	

	/**
	 * @param styleSource
	 * @return
	 */
	private Object getFieldValue(Object styleSource) {
		String fieldName = this.attribute.getName();
		if (fieldName.indexOf('-') != -1) {
			StringBuffer buffer = new StringBuffer( fieldName.length() );
			char[] chars = fieldName.toCharArray();
			boolean nextUppercase = false;
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (!Character.isJavaIdentifierPart(c)) {
					nextUppercase = true;
				} else {
					if (nextUppercase) {
						c = Character.toUpperCase(c);
					}
					buffer.append(c);
					nextUppercase = false;
				}
			}
			fieldName = buffer.toString();
		}
		try {
			return ReflectionUtil.getFieldValue(styleSource, fieldName);
		} catch (Exception e) {
			System.out.println("Unable to extract field " + fieldName + " from " + styleSource );
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] getAllowedValues() {
		return this.attribute.getAllowedValues();
	}
	
	public String getDefaultValue() {
		return this.attribute.getDefaultValue();
	}

	
	
	
	
	


//	/* (non-Javadoc)
//	 * @see de.enough.polish.styleeditor.StylePartEditor#updateStyle()
//	 */
//	protected void updateStyle() {
//		Object value = getValue();
//		this.style.getStyle().addAttribute(this.attribute.getId(), value);
//	}

}
