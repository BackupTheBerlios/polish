/*
 * Created on Feb 23, 2005 at 2:44:50 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.parser;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssToken {

	private int offset;
	private int length;
	private String type; //e.g. NAME,COLON
	private String value;
	
	
	
	
	/**
	 * The value may be omitted because we have offset and length within the document. That would
	 * reduce redundancy.
	 * @param offset
	 * @param length
	 * @param type
	 * @param value
	 */
	public CssToken(int offset, int length, String type, String value) {
		super();
		this.offset = offset;
		this.length = length;
		this.type = (type !=null)? type: "";
		this.value = (value != null)? value: "";
	}
	
	/**
	 * In the first round we do not use positions.
	 * @param type
	 * @param value
	 */
	public CssToken(String type, String value){
		this(0,0,type,value);
	}
	
	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return this.length;
	}
	
	/**
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * @return Returns the offset.
	 */
	public int getOffset() {
		return this.offset;
	}
	/**
	 * @param offset The offset to set.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return this.value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append(this.offset);
		result.append(":");
		result.append(this.length);
		result.append(":");
		result.append(this.type);
		result.append(":");
		result.append("X");
		result.append(this.value);
		result.append("X");
		return result.toString();
	}
}
