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

/**
 * <p>A time interval in milliseonds - sample value: 2s == 2000 ms == 2000.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TimeMsCssAttribute extends CssAttribute {
	
	
	/**
	 * Creates a new instance.
	 */
	public TimeMsCssAttribute() {
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
		try {
			long multiplier = 1;
			if (value.endsWith("ms")) {
				value = value.substring(0, value.length() - 2).trim();
			} else if (value.endsWith("min")) {
				value  = value.substring(0, value.length() - 3).trim();
				multiplier = 60 * 1000;
			} else if (value.endsWith("mins")) {
				value  = value.substring(0, value.length() - 4).trim();
				multiplier = 60 * 1000;
			} else if (value.endsWith("m")) {
				value  = value.substring(0, value.length() - 1).trim();
				multiplier = 60 * 1000;
			} else if (value.endsWith("sec")) {
				value  = value.substring(0, value.length() - 3).trim();
				multiplier = 1000;
			} else if (value.endsWith("h")) {
				value  = value.substring(0, value.length() - 1).trim();
				multiplier = 60 * 60 * 1000;
			} else if (value.endsWith("hour")) {
				value  = value.substring(0, value.length() - 4).trim();
				multiplier = 60 * 60 * 1000;
			} else if (value.endsWith("hours")) {
				value  = value.substring(0, value.length() - 5).trim();
				multiplier = 60 * 60 * 1000;
			} else if (value.endsWith("s")) {
				value  = value.substring(0, value.length() - 1).trim();
				multiplier = 1000;
			}
			long intValue;
			try {
				intValue = Long.parseLong( value ) * multiplier;
			} catch (NumberFormatException e) {
				String processedValue = environment.getProperty( "calculate(" + value + ")", true);
				intValue = Long.parseLong( processedValue );
			}
			if (this.isBaseAttribute ) {
				return "" + intValue;
			} else {
				return "new Long(" + intValue + ")";
			}
		} catch (NumberFormatException e) {
			throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an integer value. The value [" + value + "] cannot be accepted.");
		} catch (BuildException e) {
			throw new BuildException("Unable to parse integer value \"" + value + "\" of CSS attribute " + this.name + ": " + e.getMessage() );
		}
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String sourceCode) {
		if (this.isBaseAttribute) {
			return new Long( Long.parseLong(sourceCode));
		}
		return super.instantiateValue(sourceCode);
	}
	
	

}
