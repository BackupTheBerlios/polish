/*
 * Created on 10-Mar-2004 at 15:54:41.
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
package de.enough.polish.preprocess.borders;

import de.enough.polish.preprocess.*;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Creates the source code for a simple border.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SimpleBorderConverter extends BorderConverter {
	/**
	 * Creates a new simple border creator
	 */
	public SimpleBorderConverter() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(HashMap border, Style style, StyleSheet styleSheet) throws BuildException {
		String topWidthStr = (String) border.get("top-width");
		String bottomWidthStr = (String) border.get("bottom-width");
		String leftWidthStr = (String) border.get("left-width");
		String rightWidthStr = (String) border.get("right-width");
		
		if (topWidthStr == null && bottomWidthStr == null && leftWidthStr == null && rightWidthStr == null) {
			return "new " + BORDERS_PACKAGE + "SimpleBorder( " + this.color 
													+ ", " + this.width + ")";
		} else {
			this.width = (String) border.get("width");
			if (this.width == null) {
				this.width = "0";	// 0 is the default border width when at least one top/bottom/left/right border-width has been defined:
			} else {
				parseInt( "width", this.width );
			}
			if (topWidthStr == null) {
				topWidthStr = this.width;
			} else {
				parseInt("top-width", topWidthStr);
			}
			if (bottomWidthStr == null) {
				bottomWidthStr = this.width;
			} else {
				parseInt("bottom-width", bottomWidthStr);
			}
			if (leftWidthStr == null) {
				leftWidthStr = this.width;
			} else {
				parseInt("left-width", leftWidthStr);
			}
			if (rightWidthStr == null) {
				rightWidthStr = this.width;
			} else {
				parseInt("right-width", rightWidthStr);
			}
			return "new " + BORDERS_PACKAGE + "TopBottomLeftRightBorder( " + this.color 
				+ ", " + topWidthStr 
				+ ", " + bottomWidthStr
				+ ", " + leftWidthStr
				+ ", " + rightWidthStr	+ ")";
		}
	}
}
