/*
 * Created on Feb 28, 2005 at 10:26:01 AM.
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
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StyleSection extends Section {

	private String parentStyle;
	
	public StyleSection(){
		super();
	}
	/**
	 * @return Returns the parentStyle.
	 */
	public String getParentStyle() {
		return this.parentStyle;
	}
	/**
	 * @param parentStyle The parentStyle to set.
	 */
	public void setParentStyle(String parentStyle) {
		this.parentStyle = parentStyle;
	}
	
	public void addSection(Section section){
		if(section == null){
			System.out.println("ERROR:StyleSection:addSection():Parameter section is null");
			return;
		}
		this.children.add(section);
		section.setParent(this);
	}
	
	
}
