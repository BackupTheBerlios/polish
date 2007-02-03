/*
 * Created on 09.06.2006 at 15:46:23.
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.preprocess.backgrounds;

import java.util.HashMap;

import de.enough.polish.BuildException;

import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;


/**
 * Converter for de.enough.polish.ui.backgrounds.HorizontalStripesBackground.
 * @author Robert Virkus
 *
 */
public class HorizontalStripesBackgroundConverter extends BackgroundConverter {

	protected String createNewStatement(HashMap map, Style stlye,
			StyleSheet styleSheet) throws BuildException 
	{
		String firstTopColor = "0xFFFFFF";
		String topColorStr = (String) map.get("first-top-color");
		if ( topColorStr != null ) {
			firstTopColor = parseColor( topColorStr );			
		}
		String firstBottomColor = "0x000000";
		String firstBottomColorStr = (String) map.get("first-bottom-color");
		if ( firstBottomColorStr != null ) {
			firstBottomColor = parseColor( firstBottomColorStr );			
		}
		
		String secondTopColor = "0x000000";
		String secondTopColorStr = (String) map.get("second-top-color");
		if ( secondTopColorStr != null ) {
			secondTopColor = parseColor( secondTopColorStr );			
		}
		String secondBottomColor = "0xFFFFFF";
		String secondBottomColorStr = (String) map.get("second-bottom-color");
		if ( secondBottomColorStr != null ) {
			secondBottomColor = parseColor( secondBottomColorStr );			
		}

//		String stroke = "Graphics.SOLID";
//		String strokeStr =  (String)map.get("stroke");
//		if ( strokeStr != null ) {
//			stroke =  parseStroke( "stroke", strokeStr);			
//		}
//		int start = 0;
//		int end = 0;
//		boolean isPercent = false;
//		String startStr = (String) map.get("start"); 
//		if (startStr != null) {
//			if (startStr.charAt(startStr.length()-1) == '%') {
//				isPercent = true;
//				start = parseInt( "start", startStr.substring(0, startStr.length() - 1));
//			} else {
//				start = parseInt( "start", startStr);
//			}			
//		}
//		String endStr = (String) map.get("end"); 
//		if (endStr != null) {
//			if (endStr.charAt(endStr.length()-1) == '%') {
//				isPercent = true;
//				end = parseInt( "endStr", endStr.substring(0, endStr.length() - 1));
//			} else {
//				end = parseInt( "end", endStr);
//			}
//			
//		}

		String result = "new de.enough.polish.ui.backgrounds.HorizontalStripesBackground(" 
			+ firstTopColor + ", " + firstBottomColor + ","
			+ secondTopColor + ", " + secondBottomColor + ")";
		
		return result;
	}

}
