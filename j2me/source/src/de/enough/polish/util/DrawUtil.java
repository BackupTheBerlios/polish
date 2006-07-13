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
			Object o = g; // this cast is needed, otherwise the compiler will complain
			              // that javax.microedition.lcdui.Graphics can never be casted
			              // to de.enough.polish.blackberry.ui.Graphics.
			if ( o instanceof de.enough.polish.blackberry.ui.Graphics) {
				net.rim.device.api.ui.Graphics graphics = ((de.enough.polish.blackberry.ui.Graphics) o).g;
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
	
	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multitplication nor devision).
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param steps the number of colors in the gradient, 
	 *        when 2 is given, the first one will be the startColor and the second one will the endColor.  
	 * @return an int array with the gradient.
	 */
	public static final int[] getGradient( int startColor, int endColor, int steps ) {
		int[] gradient = new int[ steps ];
		getGradient(startColor, endColor, gradient);
		return gradient;

	}

	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multitplication nor devision).
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param gradient the array in which the gradient colors are stored.  
	 */
	public static void getGradient(int startColor, int endColor, int[] gradient) {
		int steps = gradient.length;
		if (steps == 1) {
			gradient[0] = startColor;
			return;
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor  & 0x00000FF;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0x00FF;
		int endGreen = (endColor >>> 8) & 0x0000FF;
		int endBlue = endColor  & 0x00000FF;
		
		int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps-1);
		int stepRed = ((endRed -startRed) << 8) / (steps-1);
		int stepGreen = ((endGreen - startGreen) << 8) / (steps-1);
		int stepBlue = ((endBlue - startBlue) << 8) / (steps-1);
//		System.out.println("step red=" + Integer.toHexString(stepRed));
//		System.out.println("step green=" + Integer.toHexString(stepGreen));
//		System.out.println("step blue=" + Integer.toHexString(stepBlue));
		
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;
		
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
	}
	
	/**
	 * Retrieves the complementary color to the specified one.
	 * 
	 * @param color the original argb color
	 * @return the complementary color with the same alpha value
	 */
	public static int getComplementaryColor( int color ) {
		return  ( 0xFF000000 & color )
			| ((255 - (( 0x00FF0000 & color ) >> 16)) << 16)
			| ((255 - (( 0x0000FF00 & color ) >> 8)) << 8)
			| (255 - ( 0x000000FF & color ) );				
	}

	
	static int COLOR_BIT_MASK	= 0x000000FF;
	/**
	 * Performs a convolution of an image with a given matrix. 
	 * @param filterMatrix a matrix, which should have odd rows an colums (not neccessarily a square). The matrix is used for a 2-dimensional convolution. Negative values are possible.  
	 * @param brightness you can vary the brightness of the image measured in percent. Note that the algorithm tries to keep the original brightness as far as is possible.
	 * @param argbData the image (RGB+transparency)
	 * @param width 
	 * @param height
	 */
	public final static void applyFilter(byte[][] filterMatrix, int brightness, int[] argbData, int width, int height) {
		
		// check whether the matrix is ok
		if (filterMatrix.length % 2 !=1 || filterMatrix[0].length % 2 !=1 ){
			 throw new IllegalArgumentException();
		}
		
		int fhRadius=filterMatrix.length/2+1;
		int fwRadius=filterMatrix[0].length/2+1;
		int currentPixel=0;
		int newTran, newRed, newGreen, newBlue;
		
		// compute the bightness 
		int divisor=0;
		for (int fCol, fRow=0; fRow < filterMatrix.length; fRow++){
			for (fCol=0; fCol < filterMatrix[0].length; fCol++){
				divisor+=filterMatrix[fRow][fCol];
			}
		}
		// TODO: if (divisor==0), because of negativ matrixvalues
		if (divisor==0) {
			return; // no brightness
		}
		
		// copy the neccessary imagedata into a small buffer
		int[] tmpRect=new int[width*(filterMatrix.length)];
		System.arraycopy(argbData,0, tmpRect,0, width*(filterMatrix.length));
		
		for (int fCol, fRow, col, row=fhRadius-1; row+fhRadius<height+1; row++){
			for (col=fwRadius-1; col+fwRadius<width+1; col++){
				
				// perform the convolution
				newTran=0; newRed=0; newGreen=0; newBlue=0;
				
				for (fRow=0; fRow<filterMatrix.length; fRow++){
					
					for (fCol=0; fCol<filterMatrix[0].length;fCol++){

						// take the Data from the little buffer and skale the color 
						currentPixel = tmpRect[fRow*width+col+fCol-fwRadius+1];
						
						newTran	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 24) & COLOR_BIT_MASK);
						newRed	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 16) & COLOR_BIT_MASK);
						newGreen+= filterMatrix[fRow][fCol] * ((currentPixel >>> 8) & COLOR_BIT_MASK);
						newBlue	+= filterMatrix[fRow][fCol] * (currentPixel & COLOR_BIT_MASK);
						
					}
				}
				
				// calculate the color	
				newTran = newTran * brightness/100/divisor;
				newRed  = newRed  * brightness/100/divisor;
				newGreen= newGreen* brightness/100/divisor;
				newBlue = newBlue * brightness/100/divisor;
			
				newTran =Math.max(0,Math.min(255,newTran));
				newRed  =Math.max(0,Math.min(255,newRed));
				newGreen=Math.max(0,Math.min(255,newGreen));
				newBlue =Math.max(0,Math.min(255,newBlue));
				argbData[(row)*width+col]=(newTran<<24 | newRed<<16 | newGreen <<8 | newBlue);
				
			}
			
			// shift the buffer if we are not near the end
			if (row+fhRadius!=height) { 
				System.arraycopy(tmpRect,width, tmpRect,0, width*(filterMatrix.length-1));	// shift it back
				System.arraycopy(argbData,width*(row+fhRadius), tmpRect,width*(filterMatrix.length-1), width);	// add new data
			}
		}
		
	}
	
}
