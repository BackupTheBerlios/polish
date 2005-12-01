//condition polish.midp

/*
 * Created on Nov 23, 2005 at 2:42:24 PM.
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
package de.enough.polish.util;

import javax.microedition.lcdui.Graphics;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif

/**
 * <p>Provides functions for</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Nov 23, 2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class DrawUtil {


	public final static void fillPolygon( int[] xValues, int[] yValues, int color, Graphics g ) {
		//#if polish.blackberry && polish.usePolishGui
			if ( ((Object)g) instanceof de.enough.polish.blackberry.ui.Graphics) {
				net.rim.device.api.ui.Graphics graphics = ((de.enough.polish.blackberry.ui.Graphics) ((Object)g)).g;
				graphics.setColor(color);
				graphics.drawFilledPath( xValues, yValues, null, null);
			}
		//#elif polish.api.nokia-ui
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			if ((color & 0xFF0000) == 0) {
				color |= 0xFF0000;
			}
			dg.fillPolygon(xValues, 0, yValues, 0, xValues.length, color );
		//#else
			// ... use default mechanishm
		//#endif
	}
	
	public final int[] getGradient( int startColor, int endColor, int steps ) {
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor  & 0x00000FF;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0x00FF;
		int endGreen = (endColor >>> 8) & 0x0000FF;
		int endBlue = endColor  & 0x00000FF;
		
		int stepAlpha = ((startAlpha - endAlpha) << 8) / steps;
		int stepRed = ((startRed - endRed) << 8) / steps;
		int stepGreen = ((startGreen - endGreen) << 8) / steps;
		int stepBlue = ((startBlue - endBlue) << 8) / steps;
		
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;
		
		int[] gradient = new int[ steps ];
		gradient[0] = startColor;
		for (int i = 1; i < steps; i++) {
			startAlpha += stepAlpha;
			startRed += stepRed;
			startGreen += stepGreen;
			startBlue += stepBlue;
			
			gradient[i] = (( startAlpha << 16) & 0xFF000000)
				| (( startRed << 8) & 0x00FF0000)
				| ( startGreen & 0x0000FF00)
				| ( startBlue >>> 8);
				//| (( startBlue >>> 8) & 0x000000FF);
		}
		return gradient;

	}
}
