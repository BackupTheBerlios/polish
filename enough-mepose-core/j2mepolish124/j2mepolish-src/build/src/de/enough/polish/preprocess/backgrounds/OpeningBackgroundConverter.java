/*
 * Created on 04-Nov-2004 at 19:07:52.
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
 * <p>Constructs code for instantiating an opening background.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        04-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OpeningBackgroundConverter extends BackgroundConverter {

	/**
	 * Creates a new instance.
	 */
	public OpeningBackgroundConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style,
			StyleSheet styleSheet) throws BuildException 
	{
		String startHeightStr = (String) background.get("start-height");
		if (startHeightStr == null) {
			startHeightStr = "1";
		} else {
			// check the value:
			parseInt("start-height", startHeightStr);
		}
		String stepsStr = (String) background.get("steps");
		if (stepsStr == null) {
			stepsStr = "4";
		} else {
			// check the value:
			parseInt("steps", stepsStr);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("new ")
		  .append( BACKGROUNDS_PACKAGE )
		  .append( "OpeningBackground( " )
		  .append( this.color ).append(", ")
		  .append( startHeightStr ).append(", ")
		  .append( stepsStr ).append( " )");
		return buffer.toString();
	}

}
