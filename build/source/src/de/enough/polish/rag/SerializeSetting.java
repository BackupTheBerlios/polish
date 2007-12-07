/*
 * Created on 23-Jan-2003 at 08:09:52.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.rag;


import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Environment;

import java.util.ArrayList;

/**
 * <p>Represents the info-section of a polish project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SerializeSetting {
	
	private String regex;
	private String target;
	
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
