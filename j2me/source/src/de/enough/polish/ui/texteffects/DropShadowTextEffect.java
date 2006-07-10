//#condition polish.usePolishGui && polish.midp2
/*
 * Created on 10.07.2006 at 12:22:13.
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
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;
/**
 * @author Simon Schmitt
 *
 */
public class DropShadowTextEffect extends TextEffect {
	
	private final static int CLEAR_COLOR = 0xFF000123;
	
	private int startColor = 0xF0909090;
	private int endColor = 0x20909090;
	private int size=10;
	private int xOffset=1, yOffset=2;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		// set colors
		int[] gradient;
		if (this.size==1)
			gradient = new int[] {this.startColor};
		else
			gradient = DrawUtil.getGradient( this.startColor, this.endColor, this.size );

		// todo: change DrawUtil.getGradient, such that is supports a size of 1
		
		
		// calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		int iLeft=0, /*iRight=0,*/ iTop=0/*, iBottom=0*/; 
		
		// additional Margin for the image because of the shadow
		iLeft = this.size-this.xOffset<0 ? 0 : this.size-this.xOffset;//min0
		//iRight = this.size+this.xOffset<0 ? 0 : this.size+this.xOffset;
		iTop = this.size-this.yOffset<0 ? 0 : this.size-this.yOffset;
		//iBottom = this.size+this.xOffset<0 ? 0 : this.size+this.xOffset;
		
		// create Image, Graphics, ARGB-buffer
		Graphics bufferG;
		Image midp2ImageBuffer = Image.createImage( fWidth + this.size*2, fHeight + this.size*2); // iLeft+iRight=2*size??
		bufferG = midp2ImageBuffer.getGraphics();
		int[] localRgbBuffer = new int[ (fWidth + this.size*2) * (fHeight + this.size*2) ];
		
		// draw pseudo transparent Background
		bufferG.setColor( CLEAR_COLOR );
		bufferG.fillRect(0,0,fWidth + this.size*2, fHeight + this.size*2);
		
		// draw String on Graphics
		bufferG.setColor( 0xF0F0F0 );
		//bufferG.setColor( textColor );
		bufferG.drawString(text,iLeft,iTop, Graphics.LEFT | Graphics.TOP);
		
		// get RGB-Data from Image
		midp2ImageBuffer.getRGB(localRgbBuffer,0,fWidth + this.size*2, 0, 0, fWidth + this.size*2, fHeight + this.size*2);
		
		// transform RGB-Data
		for (int i=0; i<localRgbBuffer.length;i++){
			//	 perform Transparency
			if  (localRgbBuffer[i] == CLEAR_COLOR){
				localRgbBuffer[i] = 0x00000000;
			}
		}
		
		// walk over the text and look for non-transparent Pixels	
		for (int ix=-this.size+1; ix<this.size; ix++){
			for (int iy=-this.size+1; iy<this.size; iy++){
				//int gColor=gradient[ Math.max(Math.abs(ix),Math.abs(iy))];
				//int gColor=gradient[(Math.abs(ix)+Math.abs(iy))/2];

				// compute the color and draw all shadowPixels with offset (ix, iy)
				if ( Math.sqrt(ix*ix+iy*iy)<this.size) {
					int gColor = gradient[(int)  Math.sqrt(ix*ix+iy*iy) ];
				
					for (int col=iLeft,row; col<fWidth+iLeft; col++) { 
						for (row=iTop;row<fHeight+iTop-1;row++){
							
							// draw if an opaque pixel is found and the destination is less opaque then the shadow
							if (localRgbBuffer[row*(fWidth + this.size*2) + col]>>>24==0xFF 
									&& localRgbBuffer[(row+this.yOffset+iy)*(fWidth + this.size*2) + col+this.xOffset+ix]>>>24 < gColor>>>24)
							{
								localRgbBuffer[(row+this.yOffset+iy)*(fWidth + this.size*2) + col+this.xOffset+ix]=gColor;
							}
						}
					}
				}
			}
		}
		
		// draw RGB-Data
		g.drawRGB(localRgbBuffer,0,fWidth + this.size*2, startX+2*this.size-iLeft, startY, fWidth + this.size*2, fHeight + this.size*2, true);
	}

}
