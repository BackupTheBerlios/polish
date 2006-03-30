/*
 * Created on 09-Nov-2004 at 11:11:52.
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

import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Creates source code for instantiating a BorderedRoundRectOpeningBackground.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RoundRectOpeningBackgroundConverter extends BackgroundConverter {

	/**
	 * Creates a new converter.
	 */
	public RoundRectOpeningBackgroundConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style,
			StyleSheet styleSheet) 
	throws BuildException 
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
		String arc = (String) background.get("arc");
		if (arc != null) {
			parseInt( "arc", arc );
		} else {
			arc = "10";
		}
		String arcHeight = (String) background.get("arc-height");
		if (arcHeight != null) {
			parseInt( "arc-height", arc );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) background.get("arc-width");
		if (arcWidth != null) {
			parseInt( "arc-width", arc );
		} else {
			arcWidth = arc;
		}
		if (this.borderColor == null) {
			this.borderColor = "0x000000";
		}
		if (this.borderWidth == null) {
			this.borderWidth = "1";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("new ")
		  .append( BACKGROUNDS_PACKAGE )
		  .append( "BorderedRoundRectOpeningBackground( " )
		  .append( this.color ).append(", ")
		  .append( startHeightStr ).append(", ")
		  .append( stepsStr ).append(", ")
		  .append( arcWidth ).append(", ")
		  .append( arcHeight ).append(", ")
		  .append( this.borderColor ).append(", ")
		  .append( this.borderWidth )
		  .append( " )");
		return buffer.toString();
	}

}
