/*
 * Created on Feb 24, 2005 at 10:52:36 AM.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 24, 2005 - ricky creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DummyParent extends DummyElement {

	private List children;
	
	
	public DummyParent(){
		this.name = "DummyParent";
		this.children  = new ArrayList();
		this.parent = null;
	}
	
	public DummyParent(String name, DummyElement parent){
		this.name = (name != null) ? name : "DummyParent";
		this.children  = new ArrayList();
		this.parent = parent;
	}
	
	/**
	 * @return Returns the children.
	 */
	public List getChildren() {
		return this.children;
	}
	/**
	 * @param children The children to set.
	 */
	public void setChildren(List children) {
		this.children = children;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append(this.name);
		result.append(":[");
		Iterator iterator = this.children.iterator();
		DummyElement dummyElement = null;
		while(iterator.hasNext()){
			dummyElement = (DummyElement) iterator.next();
			result.append(dummyElement);
			result.append(",");
		}
		result.append("]");
		return result.toString();
	}
}
