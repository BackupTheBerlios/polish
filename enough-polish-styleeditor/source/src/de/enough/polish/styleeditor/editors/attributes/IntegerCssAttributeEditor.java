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

import de.enough.polish.styleeditor.CssAttributeValue;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;

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
public class IntegerCssAttributeEditor extends CssAttributeEditor {
	
	protected Integer value;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#getValueAsObject()
	 */
	public Object getValueAsObject() {
		return this.value;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#getValue()
	 */
	public CssAttributeValue getValue() {
		return new CssAttributeValue( this.attribute, this.value, getValueAsString());
	}



	/**
	 * @return
	 */
	private String getValueAsString() {
		if (this.value == null) {
			return null;
		}
		String[] allowedValues = this.attribute.getAllowedValues();
		if (allowedValues == null) {
			return this.value.toString();
		} else {
			return allowedValues[ this.value.intValue() ];
		}
	}



	public void setInteger(Integer value) {
		this.value = value;
		update();
	}
	
	public void setIntegerAsAllowedValue( String value ) {
		String[] allowedValues = this.attribute.getAllowedValues();
		if (allowedValues == null) {
			setInteger( new Integer( Long.decode(value).intValue() ) );
		} else {
			for (int i = 0; i < allowedValues.length; i++) {
				String allowedValue = allowedValues[i];
				if (allowedValue.equals(value)) {
					setInteger( new Integer(i));
					break;
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		this.value = (Integer) value;
	}
	

}
