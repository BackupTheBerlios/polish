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
	
	//#if polish.cldc1.1
	/**
	 * Rotates the given rgb data and returns the rotated rgb data array.
	 * 
	 * @author Tim Muders
	 * @param argbArray the rgb data to be rotated.
	 * @param width the width of the rgb data.
	 * @param heigth the heigth of the rgb data.
	 * @param degree the degree value of the rotation.
	 * @param referenceX the x position for the center of rotation.
	 * @param referenceY the y position for the center of rotation.
	 * @param backgroundColor the ARGB color used for the background
	 * @return the rotated rgb data.
	 */
	public static final int[]rotate(int[]argbArray,int width, int heigth,int degree,int referenceX,int referenceY,int backgroundColor){
		double cosR = Math.cos(Math.PI*degree/180);
		double sinR = Math.sin(Math.PI*degree/180);		
		int newWidth = getNewWidth(degree,width,heigth,cosR,sinR);
		int newHeigth = getNewHeigth(degree,width,heigth,cosR,sinR);
		int[] newRGB = new int [newHeigth * newWidth];
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
	//#endif
	
	//#if polish.cldc1.1
	/**
	 * Returns the new heigth for the given degree. The new heigth symbols the heigth for an rotated rgb data array with the same degree rotation.
	 * 
	 * @author Tim Muders
	 * @param degree the degree value of the rotation.
	 * @param width the width of the rgb source.
	 * @param heigth the heigth of the rgb source
	 * @param cosR the cosine of the degree value.
	 * @param sinR the sine of the degree value.
	 * @return the new heigth of the rgb data.
	 */
	public static final int getNewHeigth(int degree,int width,int heigth,double cosR,double sinR)
	{
		if(degree == -90 || degree == 90 || degree == 270 || degree == -270){
			return width;
		}
		else if(degree == 360 || degree == 180 || degree == 0){
			return heigth;
		}
		long pointY1 = MathUtil.round(0 * sinR + 0 * cosR);
		long pointY2 = MathUtil.round(width *sinR + 0 *cosR);
		long pointY3 = MathUtil.round(0 *sinR + heigth *cosR);
		long pointY4 = MathUtil.round(width *sinR + heigth *cosR);
		long minY = pointY1;
		if(pointY2 < minY){
			minY = pointY2;
		}
		if(pointY3 < minY){
			minY = pointY3;
		}
		if(pointY4 < minY){
			minY = pointY4;
		}
		long maxY = pointY1;
		if(pointY2 > maxY){
			maxY = pointY2;
		}
		if(pointY3 > maxY){
			maxY = pointY3;
		}
		if(pointY4 > maxY){
			maxY = pointY4;
		}
		return (int) (maxY - minY);
	}
	//#endif
	
	
	//#if polish.cldc1.1
	/**
	 * Returns the new width for the given degree. The new width symbols the width for an rotated rgb data array with the same degree rotation.
	 * 
	 * @author Tim Muders
	 * @param degree the degree value of the rotation.
	 * @param width the width of the rgb source.
	 * @param heigth the heigth of the rgb source
	 * @param cosR the cosine of the degree value.
	 * @param sinR the sine of the degree value.
	 * @return the new width of the rgb data.
	 */
	public static final int getNewWidth(int degree,int width,int heigth,double cosR,double sinR)
	{
		if(degree == -90 || degree == 90 || degree == 270 || degree == -270){
			return heigth;
		}
		else if(degree == 360 || degree == 180 || degree == 0){
			return width;
		}
		long pointX1 = MathUtil.round(0 * cosR - 0 * sinR);
		long pointX2 = MathUtil.round(width *cosR - 0 *sinR);
		long pointX3 = MathUtil.round(0 *cosR - heigth *sinR);
		long pointX4 = MathUtil.round(width *cosR - heigth *sinR);
		long minX = pointX1;
		if(pointX2 < minX){
			minX = pointX2;
		}
		if(pointX3 < minX){
			minX = pointX3;
		}
		if(pointX4 < minX){
			minX = pointX4;
		}
		long maxX = pointX1;
		if(pointX2 > maxX){
			maxX = pointX2;
		}
		if(pointX3 > maxX){
			maxX = pointX3;
		}
		if(pointX4 > maxX){
			maxX = pointX4;
		}
		return (int) (maxX - minX);
	}
	//#endif
	
	
	/**
	 * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original. Returns the scaled rgb data.
	 * 
	 * @author Tim Muders
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 * @return the scaled rgb data.
	 */
	public static final int[] scale(int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight){
		int[]newrgbData = new int[newWidth*newHeight];
		scale( rgbData, newWidth,newHeight, oldWidth, oldHeight, newrgbData);
		return newrgbData;
	}
	
	/**
	 * Stretches the rgb data vertical to the given top and bottom stretch factor and returns the new rgb data array.
	 * 
	 * @author Tim Muders
	 * @param argbArray the rgb data to be stretched.
	 * @param topStrechFactor the stretch factor of the top.
	 * @param bottomStrechFactor the stretch factor of the bottom.
	 * @param width the source width of the rgb data.
	 * @param heigth the source heigth of the rgb data.
	 * @return stretched rgb data array.
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
	 * Stretches the rgb data vertical to the given top and bottom width and returns the new rgb data array.
	 *  
	 * 
	 * @author Tim Muders
	 * @param argbArray the rgb data to be stretched.
	 * @param newWidthTop the new top width of the rgb data.
	 * @param newWidthBottom the new bottom width of the rgb data.
	 * @param biggerWidth the bigger width of top and bottom width.
	 * @param width the source width of the rgb data.
	 * @param heigth the source heigth of the rgb data.
	 * @param procentualScalingHeight the procentual scaling heigth(biggerWidth - smallerWidth)/heigthOfTheOriginalImage).
	 * @param newArgbArray the new rgb data where the changes getting in.
	 * @return return filled the newArgbArray with stretched changes.
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
	 * Returns the one scaled Pixel for the given new heigth and width. 
	 * 
	 * @author Tim Muders
	 * @param oldLength length of the rgb data source.
	 * @param oldWidth the old width of the rgb data.
	 * @param oldHeigth the old heigth of the rgb data.
	 * @param newWidth the new width of the rgb data.
	 * @param newHeigth the new heigth of the rgb data.
	 * @param currentX the x position of the pixel to be scaled.
	 * @param currentY the y position of the pixel to be scaled.
	 * @return position of the scaled pixel in the old rgb data array.
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
	 * Stretches the rgb data horizontal to the given left and rigth stretch factor and returns the new rgb data array.
	 * 
	 * @author Tim Muders
	 * @param argbArray the rgb data to be stretched.
	 * @param leftStrechFactor the stretch factor of the left.
	 * @param rightStrechFactor the stretch factor of the rigth.
	 * @param width the source width of the rgb data.
	 * @param heigth the source heigth of the rgb data.
	 * @return stretched rgb data array.
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
	 * Stretches the rgb data horizontal to the given left and heigth width and returns the new rgb data array.
	 * 
	 * 
	 * @author Tim Muders
	 * @param argbArray the rgb data to be stretched.
	 * @param newLeftHeigth the new left heigth of the rgb data.
	 * @param newRigthHeigth the new rigth heigth of the rgb data.
	 * @param biggerHeigth the bigger heigth of left and rigth heigth.
	 * @param width the source width of the rgb data.
	 * @param heigth the source heigth of the rgb data.
	 * @param procentualScalingHeight the procentual scaling heigth(biggerHeigth - smallerSmaller)/widthOfTheOriginalImage).
	 * @param newArgbArray the new rgb data where the changes getting in.
	 * @return return the filled newArgbArray with stretched changes.
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
	 * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original.
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
