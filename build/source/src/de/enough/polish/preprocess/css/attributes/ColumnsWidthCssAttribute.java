/*
 * Created on Apr 15, 2007 at 10:12:36 PM.
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
package de.enough.polish.preprocess.css.attributes;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.util.StringUtil;

/**
 * <p>The columns-width CSS attribute is a specialized one.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColumnsWidthCssAttribute extends CssAttribute {
	
	/**
	 * Creates a new instance
	 */
	public ColumnsWidthCssAttribute() {
		super();
	}

	/**
	 * Checks and transforms the given CSS value for this attribute.
	 * 
	 * @param value the attribute value
	 * @param environment the environment
	 * @return the transformed value or the same value if no transformation is required.
	 * @throws BuildException when a condition is not met or when the value contains conflicting values
	 */
	public String getValue(String value, Environment environment ) {
		// remove any spaces, e.g. "columns-width: 50, 100, *":
		value = StringUtil.replace( value, " ", "" );
		return '"' + value + '"';
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		if (sourceCode.length() > 2 && sourceCode.charAt(0) == '"' && sourceCode.charAt( sourceCode.length() - 1) == '"') {
			return sourceCode.substring( 1, sourceCode.length() - 1 );
		}
		return sourceCode;
	}
	
	
}
