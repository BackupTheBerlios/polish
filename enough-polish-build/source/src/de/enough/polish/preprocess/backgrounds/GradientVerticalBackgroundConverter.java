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

import org.apache.tools.ant.BuildException;

import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;


/**
 * Converter for de.enough.polish.ui.backgrounds.GradientVerticalBackground.
 * @author Tim Muders
 *
 */
public class GradientVerticalBackgroundConverter extends BackgroundConverter {

	protected String createNewStatement(HashMap map, Style stlye,
			StyleSheet styleSheet) throws BuildException {
		String result = "new de.enough.polish.ui.backgrounds.GradientVerticalBackground(" 
			;
		String topColor = "0";
		String topColorStr = (String) map.get("top-color");
		if ( topColorStr != null ) {
			topColor = parseColor( topColorStr );			
		}
		String gradientColor = "0";
		String gradientColorStr = (String) map.get("bottom-color");
		if ( gradientColorStr != null ) {
			gradientColor = parseColor( gradientColorStr );			
		}
		int stroke = 0 ;
		String strokeStr =  (String)map.get("stroke");
		if ( strokeStr != null ) {
			stroke =  parseInt(strokeStr, strokeStr);			
		}
		
		result += topColor + ", "+gradientColor +","+stroke +")";
		
		return result;
	}


}
