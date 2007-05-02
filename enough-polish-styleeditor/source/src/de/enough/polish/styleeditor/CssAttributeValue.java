/*
 * Created on Apr 29, 2007 at 10:53:55 AM.
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
package de.enough.polish.styleeditor;


/**
 * <p>Contains a value or a reference to a value (like a background- or color-reference).</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttributeValue {
	
	private Object value;
	private CssAttributeReference reference;
	/**
	 * @return the reference
	 */
	public CssAttributeReference getReference() {
		return this.reference;
	}
	
	/**
	 * @param reference the reference to set
	 */
	public void setReference(CssAttributeReference reference) {
		this.reference = reference;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		if (this.reference != null) {
			return this.reference.getReferencedValue();
		}
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		if (value != null) {
			this.reference = null;
		}
		this.value = value;
	}
	
	

}
