//#condition polish.usePolishGui

/*
 * Created on 22-Aug-2005 at 16:50:12.
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
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif

import de.enough.polish.ui.Border;

/**
 * <p>Paints a translucent shadow on MIDP 2.0 and Nokia-UI-API devices.</p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        22-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DropShadowBorder extends Border {
	
	public final static int BOTTOM_RIGHT = 0;
	public final static int TOP_RIGHT = 1;
	public final static int BOTTOM_LEFT = 2;
	public final static int TOP_LEFT = 3;
	
	private final int[] shadowColors;
	private final int orientation;
	private final int offset;

	public DropShadowBorder( int innerColor, int outerColor, int width, int offset, int orientation ) {
		super();
		this.borderWidth = width;
		this.offset = offset;
		this.orientation = orientation;
		this.shadowColors = new int[ width ];
		this.shadowColors[0] = innerColor;
		this.shadowColors[ width - 1 ] = outerColor;
		int innerAlpha = innerColor >>> 24;
		int innerRed = (innerColor >>> 16) & 0x00FF;
		int innerGreen = (innerColor >>> 8) & 0x0000FF;
		int innerBlue = innerColor  & 0x000000FF;
		int outerAlpha = outerColor >>> 24;
		int outerRed = (outerColor >>> 16) & 0x00FF;
		int outerGreen = (outerColor >>> 8) & 0x0000FF;
		int outerBlue = outerColor  & 0x000000FF;
		int alpha = ( outerAlpha - innerAlpha ) / width;
		int red = ( outerRed - innerRed ) / width;
		int green = ( outerGreen - innerGreen ) / width;
		int blue = ( outerBlue  - innerBlue ) / width;
		for ( int i = 1; i < width -1; i++ ) {
			innerAlpha += alpha;
			innerRed += red;
			innerGreen += green;
			innerBlue += blue;
			this.shadowColors[i] = (innerAlpha << 24) | (innerRed << 16) | (innerGreen << 8) | innerBlue;
		}
	}

	public void paint(int x, int y, int width, int height, Graphics g) {
		//#ifdef polish.api.nokia-ui
			int[] xPoints = new int[ 2 ];
			int[] yPoints = new int[ 2 ];
			int right = x + width;
			int bottom = y + height;
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			for (int i = 0; i < this.borderWidth; i++ ) {
				int col = this.shadowColors[i];
				switch ( this.orientation ) {
				case TOP_RIGHT:
					// right side:
					xPoints[0] = right + i;
					xPoints[1] = right + i;
					yPoints[0] = y - i + 1;
					yPoints[1] = y + height - this.borderWidth + i;
					dg.drawPolygon(xPoints, 0, yPoints, 0, 2, col );
					// top side:
					xPoints[0] = x + this.borderWidth - i;
					xPoints[1] = right + i ;
					yPoints[0] = y - i;
					yPoints[1] = y - i;
					dg.drawPolygon(xPoints, 0, yPoints, 0, 2, col );
					
					if (i == 0) {
						height -= this.offset;
						x += this.offset;
						//width -= this.borderWidth;
					} else {
						// draw left edge of top side:
						xPoints[0] = x + this.borderWidth - i;
						xPoints[1] = x + this.borderWidth - i;
						yPoints[0] = y;
						yPoints[1] = y - i + 1;
						dg.drawPolygon(xPoints, 0, yPoints, 0, 2, col );
						
						// draw bottom edge of right side:
						xPoints[0] = right + 1;
						xPoints[1] = right + i - 1;
						yPoints[0] = y + height - this.borderWidth + i;
						yPoints[1] = y + height - this.borderWidth + i;
						dg.drawPolygon(xPoints, 0, yPoints, 0, 2, col );
						
					}
					break;
				case BOTTOM_LEFT:
					g.drawLine( x - i, y, x - i, y + height );
					g.drawLine( x, y + height + i, x + width, y + height + i );
					break;
				case TOP_LEFT:
					g.drawLine( x - i, y, x - i, y + height );
					g.drawLine( x, y - i, x + width, y - i );
					break;
				default: 
					g.drawLine( x + width + i, y, x + width + i, y + height );
					g.drawLine( x, y + height + i, x + width, y + height + i );
				}
			}
		//#elifdef polish.midp2
			// on the SE K700 for example the translated origin of the graphics 
			// does not seem to used. Instead the real origin is used:
			//#ifdef polish.Bugs.drawRgbOrigin
				x += g.getTranslateX();
				y += g.getTranslateY();
			//#endif
		//#endif
				
			// check if the buffer needs to be re-created:
			
			//#if polish.Bugs.drawRgbNeedsFullBuffer
//				if (width != this.lastWidth || height != this.lastHeight) {
//					this.lastWidth = width;
//					this.lastHeight = height;
//					int[] newBuffer = new int[ width * height ];
//					for (int i = newBuffer.length; --i >= 0; ) {
//						newBuffer[i] = this.currentColor;
//					}
//					this.buffer = newBuffer;
//					if (this.restartOnPositionChange) {
//						this.animationRunning = true;
//						this.currentColor = 0x00FFFFFF & this.targetArgbColor;
//						return;
//					}
//				}
//			//#else
//				if (width != this.lastWidth) {
//					this.lastWidth = width;
//					int[] newBuffer = new int[ width ];
//					for (int i = width; --i >= 0;) {
//						newBuffer[i] = this.currentColor;
//					}
//					this.buffer = newBuffer;
//					if (this.restartOnPositionChange) {
//						this.animationRunning = true;
//						this.currentColor = 0x00FFFFFF & this.targetArgbColor;
//						return;
//					}
//				}
			//#endif		
	}

}
