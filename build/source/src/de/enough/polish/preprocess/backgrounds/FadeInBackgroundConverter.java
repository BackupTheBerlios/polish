/*
 * Created on 09-Mar-2004 at 21:01:21.
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
 * <p>Creates a fade-in background with one color.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FadeInBackgroundConverter extends BackgroundConverter {
	
	/**
	 * Creates a new empty fade-in background creator 
	 */
	public FadeInBackgroundConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap background, Style style, StyleSheet styleSheet) throws BuildException {
		boolean hasAlphaColor = isAlphaColor(this.color);
		if ( !hasAlphaColor ) {
			int col = Integer.parseInt( this.color );
			this.color = "" + (0xFF000000 | col);
		}
		boolean restartOnTime = false;
		String restartOnTimeStr = (String) background.get("restart-on-time");
		if (restartOnTimeStr != null) {
			restartOnTime = parseBoolean("restart-on-time", restartOnTimeStr);
		}
		boolean restartOnPositionChange = false;
		String restartOnPositionChangeStr = (String) background.get("restart-on-position-change" );
		if (restartOnPositionChangeStr != null) {
			restartOnPositionChange = parseBoolean("restart-on-position-change", restartOnPositionChangeStr);
		}
		
		return "new " + BACKGROUNDS_PACKAGE + "FadeInBackground( "	
			+ this.color + ", " 
			+ restartOnTime + ", " 
			+ restartOnPositionChange + ")";
	}
}
