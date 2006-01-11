/*
 * Created on 15-May-2005 at 21:28:30.
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



/**
 * <p>Helps to transform images</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        15-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class ImageUtil {

	/**
	 * No instantiation is allowed
	 */
	private ImageUtil() {
		super();
	}
	
	/**
	 * Scales the rgb data and stores it into the given scaledRgbData array.
	 * 
	 * @param scaleFactor the factor by which the rgb data should be magnified in percent, e.g. 130
	 * @param width the width of the rgbData and the scaledRgbData (scanline width)
	 * @param height the height of the rgbData and the scaledRgbData (scanline width)
	 * @param rgbData the source rgbData
	 * @param scaledRgbData the target rgbData array, must have the same dimensions like the given rgbData
	 */
	public static void scale(int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
		int yStart = ((height - height * 100 / scaleFactor ) / 2) * width;
		int xStart = (width - width * 100 / scaleFactor ) / 2;
		for (int y = 0; y < height; y++) {
			int c1 = y * width;
			int c2 = yStart + (y * 100  / scaleFactor) * width;
			for (int x = 0; x < width; x++) {
				scaledRgbData[c1 + x] = rgbData[ c2 + xStart + x * 100/scaleFactor ];
			}
		}
	}

	/**
	 * Scales the rgb data and stores it into the given scaledRgbData array and adds an alpha semi transparency value at the same time.
	 * 
	 * @param opacity the alpha value, 255 (0xFF) means fully opaque, 0 fully transparent
	 * @param scaleFactor the factor by which the rgb data should be magnified in percent, e.g. 130
	 * @param width the width of the rgbData and the scaledRgbData (scanline width)
	 * @param height the height of the rgbData and the scaledRgbData (scanline width)
	 * @param rgbData the source rgbData
	 * @param scaledRgbData the target rgbData array, must have the same dimensions like the given rgbData
	 */
	public static void scale( int opacity, int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
		opacity = (opacity << 24) | 0xFFFFFF;
		
		int yStart = ((height - height * 100 / scaleFactor ) / 2) * width;
		int xStart = (width - width * 100 / scaleFactor ) / 2;
		for (int y = 0; y < height; y++) {
			int c1 = y * width;
			int c2 = yStart + (y * 100  / scaleFactor) * width;
			for (int x = 0; x < width; x++) {
				scaledRgbData[c1 + x] = rgbData[ c2 + xStart + x * 100/scaleFactor ] & opacity;
			}
		}
	}

	
	public static int[] scale(int scaledWidth, int scaledHeight, int scanlength, int sourceWidth, int sourceHeight, int[] rgbData) {
		int scaledRgbData[] = new int[scaledWidth * scaledHeight];
		for (int y = 0; y < scaledHeight; y++) {
			int c1 = y * scaledWidth;
			int c2 = (y * sourceHeight / scaledHeight) * scanlength;
			for (int x = 0; x < scaledWidth; x++) {
				scaledRgbData[c1 + x] = rgbData[c2 + x * sourceWidth / scaledWidth];
			}
		}
		return scaledRgbData;
	}
	

	public static final int[] scale(int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight){
		int[]newrgbData = new int[newWidth*newHeight];
		scale( rgbData, newWidth,newHeight, oldWidth, oldHeight, newrgbData);
		return newrgbData;
	}

	public static final void scale(int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight,int[] newrgbData){	
		int currentX = 0,currentY = 0;
		int oldLenght = rgbData.length;
		int newLength = newrgbData.length;	
		int targetArrayIndex;
		int verticalShrinkFactorPercent = ((newHeight*100) / oldHeight);
		int horizontalScaleFactorPercent = ((newWidth*100) / oldWidth);
		for(int i = 0; i < newLength;i++){
			currentX = (currentX + 1) % newWidth;
			if(currentX == 0){
				currentY++;	
			}				
			targetArrayIndex = ((currentX*100)/horizontalScaleFactorPercent)+(oldWidth * ((currentY*100)/verticalShrinkFactorPercent));
			if(targetArrayIndex >= oldLenght)targetArrayIndex = oldLenght-1;
			if(targetArrayIndex < 0)targetArrayIndex = 0;
			newrgbData[i] = rgbData[targetArrayIndex];
		}
	}

}
