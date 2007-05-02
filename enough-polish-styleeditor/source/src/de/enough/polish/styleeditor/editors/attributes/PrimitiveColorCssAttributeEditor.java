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

import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.ui.Color;

/**
 * <p>Allows to edit integer baswed color CSS attributes.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PrimitiveColorCssAttributeEditor extends CssAttributeEditor {
	
	protected Integer value;

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#getValue()
	 */
	public Object getValue() {
		return this.value;
	}



	public void setColor(Color value) {
		if (value == null) {
			setColor( (Integer)null );
		} else {
			setColor( value.getInteger() );
		}
	}
	
	public void setColor(Integer value) {
		this.value = value;
		update();
	}
	
	public void setColor( java.awt.Color value) {
		if (value == null) {
			setColor( (Integer)null );
		} else {
			setColor( new Integer( value.getRGB() ) );
		}
	}
	
	public java.awt.Color getColorAsAwt() {
		if (this.value == null) {
			return null;
		}
		return new java.awt.Color( this.value.intValue() );
	}


	/**
	 * @param rgb
	 */
	public void setColor(int rgb) {
		setColor( new Integer( rgb ) );
	}
	
	
	
	public Integer getColor() {
		return this.value;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.editors.CssAttributeEditor#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		Integer color;
		if (value instanceof Color) {
			color = ((Color)value).getInteger();
		} else {
			color = (Integer) value;
		}
		this.value = color; 
	}
	
	
	



}
