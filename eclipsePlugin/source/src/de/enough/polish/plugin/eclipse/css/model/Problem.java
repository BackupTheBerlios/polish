/*
 * Created on Mar 2, 2005 at 12:35:30 PM.
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
package de.enough.polish.plugin.eclipse.css.model;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 2, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Problem {

	//TODO: This should be a IMarker or a JDT.IProblem or the like.
	
	private String message;
	private int startPos;
	private int length;
	
	
	
	/**
	 * @param message
	 * @param startPos
	 * @param length
	 */
	public Problem(String message, int startPos, int length) {
		this.message = message;
		this.startPos = startPos;
		this.length = length;
	}
	/**
	 * @return Returns the endPos.
	 */
	public int getlength() {
		return this.length;
	}
	/**
	 * @param length The endPos to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return this.message;
	}
	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return Returns the startPos.
	 */
	public int getStartPos() {
		return this.startPos;
	}
	/**
	 * @param startPos The startPos to set.
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.message);
		buffer.append(",");
		buffer.append(this.startPos);
		buffer.append(":");
		buffer.append(this.length);
		return buffer.toString();
	}
}
