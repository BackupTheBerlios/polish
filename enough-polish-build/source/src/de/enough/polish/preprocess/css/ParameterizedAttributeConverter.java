/*
 * Created on May 8, 2007 at 4:58:08 AM.
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
package de.enough.polish.preprocess.css;

import java.util.HashMap;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

/**
 * <p>Converts parameterized CSS attributes like backgrounds and borders without specific converter classes.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 8, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ParameterizedAttributeConverter extends Converter {
	
	public String createNewStatement( 
			CssAttribute attribute,
			ParameterizedCssMapping mapping,
			HashMap cssValues, 
			Environment environment )
	throws BuildException
	{
		StringBuffer buffer = new StringBuffer();
		
		String to = mapping.getTo();
		if (to == null) {
			throw new BuildException("Unable to generate source code for " + attribute.getName() + "-" + mapping.getFrom()+  ": no \"to\" attribute found. Specify this or the \"converter\" attribute within the custom-css-attributes.xml file.");
		}
		buffer.append( "new ").append(to).append('(');
		CssAttribute[] parameters = mapping.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			CssAttribute parameter = parameters[i];
			String paramName = parameter.getName();
			String paramValue = (String) cssValues.get(paramName);
			if (paramValue == null) {
				paramValue = parameter.getDefaultValue();
			}
			if (paramValue == null) {
				System.out.println("Parameters of " + attribute.getName() + ":");
				for (int j = 0; j < parameters.length; j++) {
					System.out.println( j + "=" + parameters[j] + "=" + parameters[j].getName() );
				}
				System.out.println("CSS values for parameter " + i + ": " + cssValues.get(paramName));
				throw new BuildException("Error: unable to get value for " + mapping.getFrom() + "-"  + paramName + " - please report this error to j2mepolish@enough.de along with your polish.css settings and possibly your custom-css-attributes.xml file.");
			}
			buffer.append( parameter.getValue(paramValue, environment));
			if (i != parameters.length -1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

}
