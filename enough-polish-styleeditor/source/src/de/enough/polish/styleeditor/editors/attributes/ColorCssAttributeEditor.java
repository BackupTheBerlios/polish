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
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.ui.Color;

/**
 * <p>Allows to edit color CSS attributes.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColorCssAttributeEditor extends CssAttributeEditor {
	
	protected Color value;

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
		//TODO add support for dynamic colors and Item.transparent
		return new CssAttributeValue( this.attribute, this.value, getValueAsString() );
	}



	/**
	 * @return
	 */
	private String getValueAsString() {
		if (this.value == null) {
			return null;
		} else {
			return "#" + Integer.toHexString( this.value.getColor() );
		}
	}

	public void setColor(Color value) {
		this.value = value;
		update();
	}
	
	public void setColor(Integer value) {
		if (value == null) {
			setColor( (Color) null );
		} else {
			setColor( new Color( value.intValue() ) );
		}
	}
	
	public void setColor( java.awt.Color color) {
		if (color == null) {
			setColor( (Color) null );
		} else {
			setColor( new Color( color.getRGB() ) );
		}
	}
	
	public java.awt.Color getColorAsAwt() {
		if (this.value == null) {
			return null;
		}
		return new java.awt.Color( getColor().getColor() );
	}


	/**
	 * @param rgb
	 */
	public void setColor(int rgb) {
		setColor( new Color( rgb ) );
	}
	
	
	
	public Color getColor() {
		return this.value;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		Color color;
		if (value instanceof Integer) {
			color = new Color( ((Integer)value).intValue() );
		} else {
			color = (Color) value;
		}
		this.value = color; 
	}
	
	
	



}
