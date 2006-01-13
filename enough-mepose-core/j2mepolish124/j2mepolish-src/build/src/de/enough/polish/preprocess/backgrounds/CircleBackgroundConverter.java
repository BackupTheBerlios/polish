/*
 * Created on 26-Jul-2004 at 14:17:43.
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
package de.enough.polish.preprocess.backgrounds;

import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.preprocess.BackgroundConverter;
import de.enough.polish.preprocess.Style;
import de.enough.polish.preprocess.StyleSheet;

/**
 * <p>Converts CSS-attribute into an instantiation of the CircleBackground.</p>
 * <ul>
 * 	<li><b>type<b>: the type of the background, needs to be "circle".</li>
 * 	<li><b>color<b>: the color of the background, defaults to "white".</li>
 * 	<li><b>diameter<b>: the diameter of the background, when defined
 * 	always a circle will be painted.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CircleBackgroundConverter extends BackgroundConverter {

	/**
	 * Creates a new uninitialised converter.
	 */
	public CircleBackgroundConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style,
			StyleSheet styleSheet) 
	throws BuildException 
	{
		String diameterStr = (String) background.get("diameter");
		if (diameterStr != null) {
			int diameter = parseInt( "diameter", diameterStr );
			if ( (diameter < 1) && (diameter != -1)) {
				throw new BuildException("Invalid CSS-code: the [diameter]-attribute of a circle-background needs to be a positive integer.");
			}
		} else {
			diameterStr = "-1";
		}
		return "new " + BACKGROUNDS_PACKAGE + "CircleBackground( " + this.color + 
			", " + diameterStr + ")";
	}

}
