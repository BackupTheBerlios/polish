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
	
	
	/**
	 * Rotates an RGBimage array.
	 * 
	 * @author Tim Muders
	 * @param argbArray
	 * @param width
	 * @param heigth
	 * @param rotation
	 * @param referenceX
	 * @param referenceY
	 * @return
	 */
	public static final int[]rotate(int[]argbArray,int width, int heigth,int rotation,int referenceX,int referenceY,int backgroundColor){
				double cosR = Math.cos(Math.PI*rotation/180);
				double sinR = Math.sin(Math.PI*rotation/180);		
				int newWidth = getNewWidth(rotation,width,heigth,cosR,sinR);
				int newHeigth = getNewHeigth(rotation,width,heigth,cosR,sinR);
				int []newRGB = new int [newHeigth * newWidth];
				int halfOfWidth = width/2;
				int halfOfHeigth = heigth/2;
				int refX,refY,newX,newY,sumXY;
				for(int x = 0; x < newWidth; x++){
					for(int y = 0; y < newHeigth; y++){	
						refX = x - referenceX;
						refY = y - referenceY;
						newX = (int)(refX  * cosR + refY * sinR);
						newY = (int)(refY * cosR - refX   * sinR);
						newX += halfOfWidth;
						newY += halfOfHeigth;
						if( newX >= 0 && newX < width && newY >= 0 && newY < heigth ){
							sumXY  = newX + newY * width;
							newRGB [x+y*newWidth] = argbArray[sumXY];
						}else{
							newRGB [x+y*newWidth] = backgroundColor;
						}
					}
				}
				return newRGB;
			}
	
	/**
	 * The new Heigth for an rotated image.
	 * 
	 * @author Tim Muders
	 * @param rotation
	 * @param width
	 * @param heigth
	 * @param cosR
	 * @param sinR
	 * @return
	 */
	public static final int getNewHeigth(int rotation,int width,int heigth,double cosR,double sinR)
	{
		if(rotation == -90 || rotation == 90 || rotation == 270 || rotation == -270){
			return width;
		}
		else if(rotation == 360 || rotation == 180 || rotation == 0){
			return heigth;
		}
		int pointY1 = MathUtil.round(0 * sinR + 0 * cosR);
		int pointY2 = MathUtil.round(width *sinR + 0 *cosR);
		int pointY3 = MathUtil.round(0 *sinR + heigth *cosR);
		int pointY4 = MathUtil.round(width *sinR + heigth *cosR);
		int minY = pointY1;
		if(pointY2 < minY){
			minY = pointY2;
		}
		if(pointY3 < minY){
			minY = pointY3;
		}
		if(pointY4 < minY){
			minY = pointY4;
		}
		int maxY = pointY1;
		if(pointY2 > maxY){
			maxY = pointY2;
		}
		if(pointY3 > maxY){
			maxY = pointY3;
		}
		if(pointY4 > maxY){
			maxY = pointY4;
		}
		return maxY - minY;
	}
	
	
	/**
	 * The New Width for an Rotated Image.
	 * 
	 * @author Tim Muders
	 * @param rotation
	 * @param width
	 * @param heigth
	 * @param cosR
	 * @param sinR
	 * @return
	 */
	public static final int getNewWidth(int rotation,int width,int heigth,double cosR,double sinR)
	{
		if(rotation == -90 || rotation == 90 || rotation == 270 || rotation == -270){
			return heigth;
		}
		else if(rotation == 360 || rotation == 180 || rotation == 0){
			return width;
		}
		int pointX1 = MathUtil.round(0 * cosR - 0 * sinR);
		int pointX2 = MathUtil.round(width *cosR - 0 *sinR);
		int pointX3 = MathUtil.round(0 *cosR - heigth *sinR);
		int pointX4 = MathUtil.round(width *cosR - heigth *sinR);
		int minX = pointX1;
		if(pointX2 < minX){
			minX = pointX2;
		}
		if(pointX3 < minX){
			minX = pointX3;
		}
		if(pointX4 < minX){
			minX = pointX4;
		}
		int maxX = pointX1;
		if(pointX2 > maxX){
			maxX = pointX2;
		}
		if(pointX3 > maxX){
			maxX = pointX3;
		}
		if(pointX4 > maxX){
			maxX = pointX4;
		}
		return maxX - minX;
	}
	
	
	/**
	 * Can scale the images unproportional in every new size you want 
	 * bigger or smaller.
	 * 
	 * @author Tim Muders
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 */
	public static final int[] scale(int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight){
		int[]newrgbData = new int[newWidth*newHeight];
		scale( rgbData, newWidth,newHeight, oldWidth, oldHeight, newrgbData);
		return newrgbData;
	}
	
	/**
	 * @author Tim Muders
	 * @param argbArray
	 * @param topStrechFactor
	 * @param bottomStrechFactor
	 * @param width
	 * @param heigth
	 * @return
	 */
	public static final int[] stretchVertical( int[] argbArray, int topStrechFactor, int bottomStrechFactor, int width, int heigth ){
		int newWidthTop = (width * topStrechFactor)/100;
		int newWidthBottom = (width * bottomStrechFactor)/100;
		int procentualScalingHeight ;
		int biggerWidth ;
		if(newWidthTop < newWidthBottom){
			procentualScalingHeight = (newWidthBottom - newWidthTop)/heigth;
			biggerWidth = newWidthBottom;
		}
		else{
			procentualScalingHeight = (newWidthTop - newWidthBottom)/heigth;
			biggerWidth = newWidthTop;
		}
		int[] newArgbArray = new int[biggerWidth * heigth];
		return stretchVertical( argbArray, newWidthTop, newWidthBottom,biggerWidth , width, heigth, procentualScalingHeight, newArgbArray );
	}
	
	/**
	 * @author Tim Muders
	 * @param argbArray
	 * @param newWidthTop
	 * @param newWidthBottom
	 * @param biggerWidth
	 * @param width
	 * @param heigth
	 * @param procentualScalingHeight
	 * @param newArgbArray
	 * @return
	 */
	public static final int[] stretchVertical( int[] argbArray, int newWidthTop, int newWidthBottom,int biggerWidth , int width, int heigth,int procentualScalingHeight, int[] newArgbArray ){
		if(procentualScalingHeight == 0)procentualScalingHeight++;
		int length = newArgbArray.length;
		int oldLength = argbArray.length;
		int insideCurrentY = 0;
		int insideCurrentX = 0, outsideCurrentX = 0;
		int sum1 = (biggerWidth-newWidthTop)/2;
		int sum2 = biggerWidth-((biggerWidth-newWidthTop)/2);
		for(int i = 0; i < length; i++){
	
			outsideCurrentX = (outsideCurrentX + 1) % biggerWidth;	
			if(outsideCurrentX == 0){
				if(newWidthTop < newWidthBottom){
					newWidthTop += procentualScalingHeight;
					sum1 = (biggerWidth-newWidthTop)/2;
					sum2 = biggerWidth-((biggerWidth-newWidthTop)/2);
				}
				else if(newWidthTop > newWidthBottom){
					newWidthTop -= procentualScalingHeight;
					sum1 = (biggerWidth-newWidthTop)/2;
					sum2 = biggerWidth-((biggerWidth-newWidthTop)/2);
				}
				insideCurrentY++;
				insideCurrentX=0;
			}
			if(outsideCurrentX >= sum1 && outsideCurrentX < sum2){
				insideCurrentX = (insideCurrentX + 1) % newWidthTop;
				newArgbArray[i] = argbArray[scaledPixel(oldLength,width,heigth,newWidthTop,heigth,insideCurrentX,insideCurrentY)];
			}
			else{
				newArgbArray[i] = 000000;
			}
		}
		return newArgbArray;
	}
	
	/**
	 * @author Tim Muders
	 * @param oldLength
	 * @param oldWidth
	 * @param oldHeigth
	 * @param newWidth
	 * @param newHeigth
	 * @param currentX
	 * @param currentY
	 * @return
	 */
	public static final int scaledPixel(int oldLength,int oldWidth,int oldHeigth, int newWidth, int newHeigth, int currentX, int currentY){
			int targetArrayIndex;
			int verticalShrinkFactorPercent = ((newHeigth*100) / oldHeigth);
			int horizontalScaleFactorPercent = ((newWidth*100) / oldWidth);				
			targetArrayIndex = ((currentX*100)/horizontalScaleFactorPercent)+(oldWidth * ((currentY*100)/verticalShrinkFactorPercent));
			if(targetArrayIndex >= oldLength)targetArrayIndex = oldLength-1;
			if(targetArrayIndex < 0)targetArrayIndex = 0;
			return targetArrayIndex;
	}
	
	/**
	 * @author Tim Muders
	 * @param argbArray
	 * @param leftStrechFactor
	 * @param rightStrechFactor
	 * @param width
	 * @param heigth
	 * @return
	 */
	public static final int[] stretchHorizontal( int[] argbArray, int leftStrechFactor, int rightStrechFactor, int width, int heigth ){
		int newHeigthLeft = (heigth * leftStrechFactor)/100;
		int newHeigthRight = (heigth * rightStrechFactor)/100;
		int procentualScalingWidth ;
		int biggerHeigth ;
		if(newHeigthLeft < newHeigthRight){
			procentualScalingWidth = (newHeigthRight - newHeigthLeft)/width;
			biggerHeigth = newHeigthRight;
		}
		else{
			procentualScalingWidth = (newHeigthLeft - newHeigthRight)/width;
			biggerHeigth = newHeigthLeft;
		}
		int[] newArgbArray = new int[biggerHeigth * width];
		return stretchHorizontal( argbArray, newHeigthLeft, newHeigthRight,biggerHeigth , width, heigth, procentualScalingWidth, newArgbArray );
	}

	/**
	 * @author Tim Muders
	 * @param argbArray
	 * @param newLeftHeigth
	 * @param newRigthHeigth
	 * @param biggerHeigth
	 * @param width
	 * @param heigth
	 * @param procentualScalingHeight
	 * @param newArgbArray
	 * @return
	 */
	public static final int[] stretchHorizontal( int[] argbArray, int newLeftHeigth, int newRigthHeigth,int biggerHeigth , int width, int heigth,int procentualScalingHeight, int[] newArgbArray ){
		if(procentualScalingHeight == 0)procentualScalingHeight++;
		int length = newArgbArray.length;
		int oldLength = argbArray.length;
		// x and y position int the new array
		int idX = 0, idY = 0;
		// x and y position of the old array
		int x = 0, y = 0;
		//position in the new array
		int whereIamAt = 0;
		// Heighth for goal
		int newHeigth = newLeftHeigth;
		//start Heigth to goal
		int startColumn = (biggerHeigth-newHeigth)/2;
		int endColumn = biggerHeigth-((biggerHeigth-newHeigth)/2);

		for(int i = 0; i < length; i++){
		
			if(startColumn <= idY && endColumn >= idY){
				newArgbArray[whereIamAt] = argbArray[scaledPixel(oldLength,width,heigth,width,newHeigth,x,y)];
				y = (y + 1) % newHeigth;
			}
			else{				
				newArgbArray[whereIamAt] = 000000;
			}
			idY = (idY + 1)% (biggerHeigth) ;
				whereIamAt = idX+(idY * width);
				if(idY == 0){
					idX++;
					x++;
					y = 0;
					if(newLeftHeigth < newRigthHeigth){
						newHeigth += procentualScalingHeight; 
					}
					else if(newLeftHeigth > newRigthHeigth){
						newHeigth -= procentualScalingHeight; 
					}
					startColumn = (biggerHeigth-newHeigth)/2;
					endColumn = biggerHeigth-((biggerHeigth-newHeigth)/2);
				}
		}
		return newArgbArray;
	}
	
	/**
	 * Can scale the images unproportional in every new size you want 
	 * bigger or smaller.
	 * 
	 * @author Tim Muders
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 * @param newrgbData the new rgbdata has to be initialised
	 */
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
