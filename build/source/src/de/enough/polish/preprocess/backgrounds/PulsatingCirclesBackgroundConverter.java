/*
 * Created on 17-Jul-2004 at 13:25:19.
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
 * along with Foobar; if not, write to the Free Software
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
 * <p>Converts CSS code into an instantiation of the PulsatingCirclesBackground.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type<b>: the type of the background, needs to be "pulsating-circles".</li>
 * 	<li><b>color<b>: the color of the background, defaults to "white".</li>
 * 	<li><b>min-diameter<b>: the minimum diameter of the circle.</li>
 * 	<li><b>max-diameter<b>: the minimum diameter of the circle.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PulsatingCirclesBackgroundConverter extends BackgroundConverter {

	/**
	 * Creates a new uninitialised background converter
	 */
	public PulsatingCirclesBackgroundConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(
			HashMap background, 
			Style style,
			StyleSheet styleSheet ) 
	throws BuildException {
		String firstColorStr = (String) background.get( "first-color");
		if (firstColorStr == null) {
			throw new BuildException("Invalid CSS: the pulsating-circles background needs the attribute [first-color].");
		}
		firstColorStr = parseColor(firstColorStr);
		String secondColorStr = (String) background.get( "second-color");
		if (secondColorStr == null) {
			throw new BuildException("Invalid CSS: the pulsating-circles background needs the attribute [second-color].");
		}
		secondColorStr = parseColor( secondColorStr );
		String minDiameterStr = (String) background.get( "min-diameter");
		if (minDiameterStr == null) {
			throw new BuildException("Invalid CSS: the pulsating-circles background needs the attribute [min-diameter].");
		}
		String maxDiameterStr  = (String) background.get( "max-diameter");
		if (maxDiameterStr == null) {
			throw new BuildException("Invalid CSS: the pulsating-circles background needs the attribute [max-diameter].");
		}
		String circlesNumberStr  = (String) background.get( "circles-number");
		if (circlesNumberStr == null) {
			throw new BuildException("Invalid CSS: the pulsating-circles background needs the attribute [circles-number].");
		}
		String stepStr = (String) background.get( "step");
		if (stepStr == null) {
			stepStr = "100";
		} else {
			float step = parseFloat("step", stepStr);
			if (step < 1) {
				throw new BuildException("Invalid CSS: the [step] attribute of the pulsating-circles background needs to be greater or equals 1.");
			}
			stepStr = "" + (int) (step * 100f);
		}
		// now check if these diameters have valid values:
		int minDiameter = parseInt("min-diameter", minDiameterStr);
		int maxDiameter = parseInt("max-diameter", maxDiameterStr);
		if (maxDiameter <= minDiameter ) {
			throw new BuildException("Invalid CSS: the [min-diameter] attribute of the pulsating-circles background needs to be smaller than the [max-diameter].");
		}
		if (minDiameter < 0 ) {
			throw new BuildException("Invalid CSS: the [min-diameter] attribute of the pulsating-circles background needs to be greater or equals 0.");
		}
		// check circles number:
		int circlesNumber = parseInt("circles-number", circlesNumberStr );
		if (circlesNumber < 2) {
			throw new BuildException("Invalid CSS: the [circles-number] attribute of the pulsating-circles background needs to be greater or equals 2.");
		}
		// okay, the min- and max-diameter parameters are okay:
		return "new " + BACKGROUNDS_PACKAGE + "PulsatingCirclesBackground( "
			+ firstColorStr + ", " + secondColorStr + ", "   
			+ minDiameterStr + ", " + maxDiameterStr + ", " 
			+ circlesNumberStr + ", " + stepStr
			+ ")";
	}

}
