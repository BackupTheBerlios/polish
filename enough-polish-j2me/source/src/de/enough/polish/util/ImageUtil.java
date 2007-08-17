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
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        15-May-2005 - rob creation
 *        15-Aug-2007 - Simon hq-down scaling added
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders, tim.muders@enough.de
 * @author Simon Schmitt, simon.schmitt@enough.de
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
	 * @throws ArithmeticException when width of scaleFactor is 0
	 */
	public static void scale(int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
		if (scaleFactor < 100){
			int xStart = ((width*100) - (width*scaleFactor)) / 200;
			int yStart = ((height*100) - (height*scaleFactor)) / 200;
			for (int y=yStart; y < height-yStart; y++){
				for (int x=xStart; x < width-xStart; x++){
					int xTarget = (x*scaleFactor)/100  + xStart;
					int yTarget = (y*scaleFactor)/100 + yStart;            
					scaledRgbData[(yTarget*width) + xTarget] = rgbData[(y*width)+x];
				}
			}
			return;
		}
		int xStart = (width - width * 100 / scaleFactor ) / 2;
		int yStart = ((height - height * 100 / scaleFactor ) / 2) * width;
		for (int y = 0; y < height; y++) {
			int c1 = y * width;
			int c2 = yStart + (y * 100  / scaleFactor) * width;
			for (int x = 0; x < width; x++) {
				scaledRgbData[c1 + x] = rgbData[ c2 + xStart + x * 100/scaleFactor ];
			}
		}
	}
	
	/**
	 * Scales the image without filling gaps between single pixels.
	 * 
	 * @param factor the zoom factor in percent, must be greater than 100
	 * @param width the width of the target RGB
	 * @param height the height of the target RGB
	 * @param sourceRgb the source RGB data that has the original data
	 * @param targetRgb the target array for storing the scaled image
	 */
	public static void particleScale(int factor, int width, int height, int[] sourceRgb, int[] targetRgb) {
		for (int i = 0; i < targetRgb.length; i++) {
			targetRgb[i] = 0;
		}
		int centerX = width >> 1;
		int centerY = height >> 1;
		int distanceX = (width - (width * 100)/factor) >> 1;
		int startX = distanceX; 
		int endX = width - distanceX;
		int distanceY = (height - (height * 100)/factor) >> 1;
		int startY = distanceY;
		int endY = height - distanceY;
		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				distanceX = centerX - x;
				int targetX = centerX - ( distanceX * factor ) / 100;
				if (targetX < 0 || targetX >= width) {
					continue;
				}
				distanceY = centerY - y;
				int targetY = centerY - ( distanceY * factor ) / 100;
				if (targetY < 0 || targetY >= height) {
					continue;
				}
				int sourceIndex = y * width + x;
				int targetIndex =  targetY * width + targetX;
				targetRgb[ targetIndex ] = sourceRgb[ sourceIndex ];
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
	 * @throws ArithmeticException when width of scaleFactor is 0
	 */
	public static void scale( int opacity, int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
		opacity = (opacity << 24) | 0xFFFFFF;
		if (scaleFactor < 100){
			int xStart = ((width*100) - (width*scaleFactor)) / 200;
			int yStart = ((height*100) - (height*scaleFactor)) / 200;
//			for (int y=yStart; y < height-yStart; y++){
//				for (int x=xStart; x < width-xStart; x++){
			for (int y=0; y < height; y++){
				for (int x=0; x < width; x++){
					int xTarget = (x*scaleFactor)/100  + xStart;
					int yTarget = (y*scaleFactor)/100 + yStart;            
					scaledRgbData[(yTarget*width) + xTarget] = ( rgbData[(y*width)+x]  | 0xff000000 )  & opacity;
				}
			}
			return;
		}
		
		int yStart = ((height - height * 100 / scaleFactor ) / 2) * width;
		int xStart = (width - width * 100 / scaleFactor ) / 2;
		for (int y = 0; y < height; y++) {
			int c1 = y * width;
			int c2 = yStart + (y * 100  / scaleFactor) * width;
			for (int x = 0; x < width; x++) {
				scaledRgbData[c1 + x] = ( rgbData[ c2 + xStart + x * 100/scaleFactor ] | 0xff000000 )  & opacity;
			}
		}
	}

	
	/**
	 * Scales the rgb data and stores it into the given scaledRgbData array and adds an alpha semi transparency value at the same time.
	 * 
	 * @param scaledWidth the width of the scaled rgbData
	 * @param scaledHeight the height of the scaled rgbData
	 * @param scanlength the length of the given RGB data, usual the same as sourceWidth
	 * @param sourceWidth the width of the rgbData
	 * @param sourceHeight the height of the rgbData
	 * @param rgbData the source rgbData
	 * @return a new rgbData array that contains the scaled version
	 * @throws ArithmeticException when width of scaleFactor is 0
	 */
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

	//#if polish.hasFloatingPoint
	// TODO ohne float
	private static final int INTMAX=1<<10;//1<<15/MAX_SCALE;
	private static final int SCALE_THRESHOLD_SHIFT=3; // TODO dynamisch anpassen
	private static final boolean SKIP_FRACTIONS=false;
	private static final int FRACTION_SCALE=3;
	public static int[] scaleDownHq(int[] src, int srcWidth, int scaleFactor, int scaledWidth, int scaledHeight, int opacity){
		int[] dest;
		
		float scale; 
		
		// parse the scaling parameter
		int srcHeight = src.length/srcWidth;
		if (scaleFactor!=0){
			scale=(float)100/scaleFactor;
		} else if (scaledWidth!=0){
			scale=(float)srcWidth/scaledWidth;
		} else{
			scale=(float)srcHeight/scaledHeight;
		}
		if (scale==1){
			return src;
		}else if(scale<1){
			throw new IllegalArgumentException();
		} else if(scale>6){ // TODO max is probably sqrt(32)
			throw new IllegalArgumentException(" scale >6 may lead to internal overflows");
		}
		
		/*else if(1<<SCALE_THRESHOLD_SHIFT<= scale){
			throw new IllegalArgumentException(" down scalefactor is to high (>=8) ... use fastscale or set SKIP_FRACTIONS=false ");
		}*/
		
		System.out.println(" scaling requested with scale=" + scale);
		
		//TODO turn into parameter
		boolean doNotSkipFractions=true;//(scale<FRACTION_SCALE);
		
		// this prevents precision errors
		if(scaledHeight==0){
			scaledHeight=(int)(srcHeight/scale);
		}
		if (scaledHeight==0){
			scaledWidth=(int)(srcWidth/scale);
		}
		
		//TODO maybe without reallocation
		// prepare the destination
		dest = new int[scaledWidth*scaledHeight];
		
		float srcX=0, srcY=0;
		int destPointer=0;
		float yIntensityStart, yIntensityEnd;
		int[] tmp=new int[5];
		int[] intensityTrace=new int[1];
		
		int OVERFLOW_CHECK = (int) (INTMAX * scale*scale);
		
		// set all to green
		//opacity=255; // TODO delete this, check alpha mode again
		/*for (int i = 0; i < src.length; i++) {
			if(src[i]!=0){
				src[i]= 255<<24 | 255<<8;
			}
		}*/
		
		for (int scaledY = 0; scaledY < scaledHeight; scaledY++) {
			for (int scaledX = 0; scaledX < scaledWidth; scaledX++) {
				destPointer=scaledY*scaledWidth + scaledX;
				
				if(srcY<srcHeight && srcX<srcWidth){
					// normal
					//dest[scaledY*scaledWidth + scaledX]= src[(int)(srcY)*srcWidth  + (int)srcX];
					
						
					// calc the overlap of y
					yIntensityStart=1-(srcY-(int)srcY);
					// yIntensity=1; // in between
					yIntensityEnd=(srcY+scale-(int)(srcY+scale));
					if(yIntensityEnd==0){
						yIntensityEnd=1;
					}
					
					// sum them up
					tmp[0]=0;
					tmp[1]=0;
					tmp[2]=0;
					tmp[3]=0;
					tmp[4]=0;
					intensityTrace[0]=0;
					
					// start
					if(doNotSkipFractions || yIntensityStart==1){ // we need no fractions, if scale >3
						tmp=helpWithX(src,dest,tmp,srcWidth,srcX, srcY,scale,INTMAX*yIntensityStart,intensityTrace,OVERFLOW_CHECK,doNotSkipFractions);
					}
					// between
					int smallY;
					for (smallY = (int) srcY+1; smallY < srcY+scale-1; smallY++) {
						tmp=helpWithX(src,dest,tmp,srcWidth,srcX, smallY,scale,INTMAX*1,intensityTrace,OVERFLOW_CHECK,doNotSkipFractions);
					}
					//end
					
					if(doNotSkipFractions || yIntensityEnd==1){
						if (smallY<srcHeight){
							tmp=helpWithX(src,dest,tmp,srcWidth,srcX, smallY,scale,INTMAX*yIntensityEnd,intensityTrace,OVERFLOW_CHECK,doNotSkipFractions);
						} else {
							// TODO there is probably an easier way
							 //mic transparency in
							tmp=testMixPixelIn2(tmp, 0 ,(int)( INTMAX  *yIntensityEnd),intensityTrace,OVERFLOW_CHECK);
						}
					}
					
					
					if(intensityTrace[0]>OVERFLOW_CHECK){
						throw new IllegalArgumentException(" overflow");
					}

					// 				alpha=alpha/int			red=  redSum/(alpha*int)
					if(tmp[1]==0){
						dest[destPointer]=0;
					} else {
						dest[destPointer]=	(tmp[1]*opacity/(255*tmp[0]))<<24 |(tmp[2]/(tmp[1]))<<16 |  (tmp[3]/(tmp[1]))<<8 | (tmp[4]/(tmp[1]));
						
						if (tmp[3]/tmp[1]>255){
							
							throw new ArithmeticException(" error in color computation");
						}
					}
				
				} else {
					// not dangerous, but good to know while debugging
					throw new ArithmeticException (" out of area!");
				}
				
				srcX+=scale;
			}
			srcX=0;
			srcY+=scale;
		}
		
		return dest;
	}
	
	private static int[] helpWithX(int[] src, int[] dest, int[] tmp, int srcWidth, float srcX, float Y, float scale, float yIntenstiy,int [] intensityTrace, int OVERFLOW_CHECK, boolean doNotSkipFractions ){
		// 	TODO if intenstiy < .1 -> elegant abbrechen??
		// TODO sinnvolle Grenze festlegen (hängt von scale ab!!)
		// TODO es dürfen keine Lücken entstehen!!
		if (SKIP_FRACTIONS && yIntenstiy<OVERFLOW_CHECK>>SCALE_THRESHOLD_SHIFT){ // == /8 TODO evtl wäre 8 besser
			return tmp;
		}
		
		
		float xIntensityStart, xIntensityEnd;
		//int xIntensityStartI, xIntensityEndI;
		
		xIntensityStart=1-(srcX-(int)srcX);
		// xIntensity=1; // in between
		xIntensityEnd=(srcX+scale-(int)(srcX+scale));
		if(xIntensityEnd==0){
			xIntensityEnd=1;
		}
		
		int Y_srcWidth = (int)(Y)*srcWidth;
		// 	start
		if(doNotSkipFractions || xIntensityStart==1){
			tmp=testMixPixelIn2(tmp,src[Y_srcWidth  	+ (int)srcX],(int)( xIntensityStart *yIntenstiy ) ,intensityTrace,OVERFLOW_CHECK);
		}
		// between
		int smallX=0;
		for (smallX = (int) srcX+1; smallX < srcX+scale-1 ; smallX++) {
			tmp=testMixPixelIn2(tmp,src[Y_srcWidth  	+ smallX],(int)( yIntenstiy) ,intensityTrace,OVERFLOW_CHECK);
		}
		//end
		if(doNotSkipFractions || xIntensityEnd==1){
			if (smallX<srcWidth){
				tmp=testMixPixelIn2(tmp,src[Y_srcWidth  	+ smallX],(int)( xIntensityEnd  *yIntenstiy) ,intensityTrace,OVERFLOW_CHECK);
			} else {
				// mic transparency in
				tmp=testMixPixelIn2(tmp, 0 ,(int)( xIntensityEnd  *yIntenstiy),intensityTrace,OVERFLOW_CHECK);
			}
		}
		
		return tmp;
	}
	
	//#endif
	
	private static int[] testMixPixelIn2(int[] current, int add, int intensity,int[] intensityTrace,int OVERFLOW_CHECK){
		if(add==0){
			return current;
			
		}else if(SKIP_FRACTIONS && intensity<OVERFLOW_CHECK>>(SCALE_THRESHOLD_SHIFT<<1-1)){ // *8*8/2
			// TODO same as with the other one
			return current;
			
		} else {
			
			intensityTrace[0]+=intensity;
			int alpha=(add>>>24);
			current[0]+=intensity;
			
			current[1]+= 			( (add>>>24&255)*intensity);
			
			current[2]+= 	 	( (add>>>16&255)*intensity*alpha);
			current[3]+=  	( (add>>>8&255)*intensity*alpha);
			current[4]+=		( (add&255)*intensity*alpha);
			
			return current;
		}
	}
	
	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given RGB data image and uses the center as the reference point for the rotation.
	 * 
	 * @param image the RGB data image that is going to be rotated. Warning: the RGB data might get replaced
	 * @param angle the angle for the rotation in degrees (-360..0..360)
	 */
	public static void rotate( RgbImage image, int angle ) {
		rotate( image, angle, image.getWidth()/2, image.getHeight()/2 );
	}
	//#endif

	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given RGB data image.
	 * 
	 * @param image the RGB data image that is going to be rotated. Warning: the RGB data might get replaced
	 * @param angle the angle for the rotation in degrees (-360..0..360)
	 * @param referenceX the horizontal reference point for the rotation
	 * @param referenceY the vertical reference point for the rotation 
	 */
	public static void rotate(RgbImage image, int angle, int referenceX, int referenceY ) {
		int[] rgbData = image.getRgbData();
		int width = image.getWidth();
		int height = image.getHeight();
		double degreeCos = Math.cos(Math.PI*angle/180);
		double degreeSin = Math.sin(Math.PI*angle/180);
		int rotatedWidth = getRotatedWidth(angle, width, height, degreeCos, degreeSin);
		int rotatedHeight = getRotatedHeight(angle, width, height, degreeCos, degreeSin);
		int[] rotatedRgbData = new int[ rotatedWidth * rotatedHeight ];
		ImageUtil.rotate(rgbData, width, height, 
				referenceX, referenceY, 0x00FFFFFF,
				degreeCos, degreeSin, rotatedRgbData, rotatedWidth, rotatedHeight );
		image.setRgbData(rotatedRgbData, rotatedWidth);
		image.setWidth(rotatedWidth);
		image.setHeight(rotatedHeight);
	}
	//#endif
	
	
	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given rgb data and returns the rotated rgb data array.
	 * 
	 * @param argbArray the rgb data to be rotated.
	 * @param width the width of the rgb data.
	 * @param height the heigth of the rgb data.
	 * @param degree the degree value of the rotation.
	 * @param backgroundColor the ARGB color used for the background
	 * @return the rotated rgb data.
	 */
	public static final int[] rotate(int[]argbArray, int width, int height, int degree, int backgroundColor) {
		return rotate(argbArray, width, height, degree, width/2, height/2, backgroundColor);
	}
	//#endif

	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given rgb data and returns the rotated rgb data array.
	 * 
	 * @param sourceRgbData the rgb data to be rotated.
	 * @param width the width of the rgb data.
	 * @param height the heigth of the rgb data.
   * @param targetRgbData unused
	 * @param degree the degree value of the rotation.
	 * @param degreeCos unused
   * @param degreeSin unused
	 * @param backgroundColor the ARGB color used for the background
	 * @return the rotated rgb data.
	 */
	public static final int[] rotate(int[] sourceRgbData, int width, int height, int targetRgbData, int degree, double degreeCos, double degreeSin, int backgroundColor) {
		return rotate(sourceRgbData, width, height, degree, width/2, height/2, backgroundColor);
	}
	//#endif

	//#if polish.hasFloatingPoint
	/**
	 * Rotates the given rgb data and returns the rotated rgb data array.
	 * 
	 * @param sourceRgbData the rgb data to be rotated.
	 * @param width the width of the source rgb data.
	 * @param height the heigth of the source rgb data.
	 * @param degree the angle of the rotation.
	 * @param referenceX the x position for the center of rotation.
	 * @param referenceY the y position for the center of rotation.
	 * @param backgroundColor the ARGB color used for the background
	 * @return the rotated rgb data.
	 */
	public static final int[] rotate(int[] sourceRgbData,int width, int height,int degree,int referenceX,int referenceY,int backgroundColor){
		double degreeCos = Math.cos(Math.PI*degree/180);
		double degreeSin = Math.sin(Math.PI*degree/180);		
		int rotatedWidth = getRotatedWidth(degree,width,height,degreeCos,degreeSin);
		int rotatedHeight = getRotatedHeight(degree,width,height,degreeCos,degreeSin);
		int[] rotatedRGB = new int [rotatedHeight * rotatedWidth];
		rotate(sourceRgbData, width, height, referenceX, referenceY, backgroundColor, degreeCos, degreeSin, rotatedRGB, rotatedWidth, rotatedHeight);
		return rotatedRGB;
	}
	//#endif

	//#if polish.hasFloatingPoint
	/**
	 * Rotates the source RGB data and stores it within the target RGB data array.
	 * 
	 * @param sourceRgbData the rgb data to be rotated.
	 * @param width the width of the source rgb data.
	 * @param height the heigth of the source rgb data.
	 * @param referenceX the x position for the center of rotation.
	 * @param referenceY the y position for the center of rotation.
	 * @param backgroundColor the ARGB color used for the background
	 * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
	 * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
	 * @param rotatedRGB the RGB data array for storing the rotated pixel data
	 * @param rotatedWidth the width of the rotated rgb data
	 * @param rotatedHeight the height of the rotated rgb data
	 */
	public static void rotate(int[] sourceRgbData, int width, int height, int referenceX, int referenceY, int backgroundColor, double degreeCos, double degreeSin, int[] rotatedRGB, int rotatedWidth, int rotatedHeight) {
		int halfOfWidth = width/2;
		int halfOfHeigth = height/2;
		int refX,refY,newX,newY,sumXY;
		for(int x = 0; x < rotatedWidth; x++){
			for(int y = 0; y < rotatedHeight; y++){	
				refX = x - referenceX;
				refY = y - referenceY;
				newX = (int)(refX  * degreeCos + refY * degreeSin);
				newY = (int)(refY * degreeCos - refX  * degreeSin);
				newX += halfOfWidth;
				newY += halfOfHeigth;
				if( newX >= 0 && newX < width && newY >= 0 && newY < height ){
					sumXY  = newX + newY * width;
					rotatedRGB [x+y*rotatedWidth] = sourceRgbData[sumXY];
				} else {
					rotatedRGB [x+y*rotatedWidth] = backgroundColor;
				}
			}
		}
	}
	//#endif
	
	//#if polish.hasFloatingPoint
	/**
	 * Returns the new height for the given degree. The new heigth symbols the heigth for an rotated rgb data array with the same degree rotation.
	 * 
	 * @param degree the degree value of the rotation.
	 * @param width the width of the rgb source.
	 * @param heigth the heigth of the rgb source
	 * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
	 * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
	 * @return the new height of the rgb data.
	 */
	public static final int getRotatedHeight(int degree, int width, int heigth, double degreeCos, double degreeSin)
	{
		if(degree == -90 || degree == 90 || degree == 270 || degree == -270){
			return width;
		}
		else if(degree == 360 || degree == 180 || degree == 0){
			return heigth;
		}
		long pointY1 = MathUtil.round(0 * degreeSin + 0 * degreeCos);
		long pointY2 = MathUtil.round(width *degreeSin + 0 *degreeCos);
		long pointY3 = MathUtil.round(0 *degreeSin + heigth *degreeCos);
		long pointY4 = MathUtil.round(width *degreeSin + heigth *degreeCos);
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
	
	
	//#if polish.hasFloatingPoint
	/**
	 * Returns the new width for the given degree. The new width symbols the width for an rotated rgb data array with the same degree rotation.
	 * 
	 * @param degree the degree value of the rotation.
	 * @param width the width of the rgb source.
	 * @param heigth the heigth of the rgb source
	 * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
	 * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
	 * @return the new width of the rgb data.
	 */
	public static final int getRotatedWidth( int degree,int width,int heigth, double degreeCos, double degreeSin)
	{
		if(degree == -90 || degree == 90 || degree == 270 || degree == -270){
			return heigth;
		}
		else if(degree == 360 || degree == 180 || degree == 0){
			return width;
		}
		long pointX1 = 0; // MathUtil.round(0 * degreeCos - 0 * degreeSin);
		long pointX2 = MathUtil.round( width * degreeCos ); //MathUtil.round(width * degreeCos - 0 *degreeSin);
		long pointX3 = MathUtil.round( - heigth * degreeSin); // MathUtil.round(0 *degreeCos - heigth *degreeSin);
		long pointX4 = MathUtil.round( width * degreeCos - heigth * degreeSin );
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
	 * @param opacity the alpha value, 255 (0xFF) means fully opaque, 0 fully transparent
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 * @return the scaled rgb data.
	 */
	public static final int[] scale(int opacity, int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight){
		int[]newrgbData = new int[newWidth*newHeight];
		scale( opacity, rgbData, newWidth,newHeight, oldWidth, oldHeight, newrgbData);
		return newrgbData;
	}

	/**
	 * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original. Returns the scaled rgb data.
	 * 
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
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 * @param newRgbData the new rgbdata has to be initialised
	 */
	public static final void scale(int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight,int[] newRgbData){	
		int currentX = 0,currentY = 0;
		int oldLenght = rgbData.length;
		int newLength = newRgbData.length;	
		int targetArrayIndex;
		int verticalShrinkFactorPercent = ((newHeight*100) / oldHeight);
		int horizontalScaleFactorPercent = ((newWidth*100) / oldWidth);
		for(int i = 0; i < newLength;i++){
			currentX = (currentX + 1) % newWidth;
			if(currentX == 0){
				currentY++;	
			}				
			targetArrayIndex = ((currentX*100)/horizontalScaleFactorPercent)+(oldWidth * ((currentY*100)/verticalShrinkFactorPercent));
			if(targetArrayIndex >= oldLenght) {
				targetArrayIndex = oldLenght-1;
			}
			if(targetArrayIndex < 0) {
				targetArrayIndex = 0;
			}
			newRgbData[i] = rgbData[targetArrayIndex];
		}
	}
	
	/**
	 * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original.
	 * 
	 * @param opacity the alpha value, 255 (0xFF) means fully opaque, 0 fully transparent
	 * @param rgbData the original rgbdata
	 * @param newWidth the new width for the new rgbdata
	 * @param newHeight the new height for the new rgbdata
	 * @param oldWidth the width from the oiginal rgbdata
	 * @param oldHeight the height from the oiginal rgbdata
	 * @param newRgbData the new rgbdata has to be initialised
	 */
	public static final void scale(int opacity, int[]rgbData,int newWidth,int newHeight,int oldWidth, int oldHeight,int[] newRgbData){	
		int currentX = 0,currentY = 0;
		int oldLenght = rgbData.length;
		int newLength = newRgbData.length;	
		int targetArrayIndex;
		int verticalShrinkFactorPercent = ((newHeight*100) / oldHeight);
		int horizontalScaleFactorPercent = ((newWidth*100) / oldWidth);
		int alpha = (opacity << 24); // is now 0xtt000000
		for(int i = 0; i < newLength;i++){
			currentX = (currentX + 1) % newWidth;
			if(currentX == 0){
				currentY++;	
			}				
			targetArrayIndex = ((currentX*100)/horizontalScaleFactorPercent)+(oldWidth * ((currentY*100)/verticalShrinkFactorPercent));
			if(targetArrayIndex >= oldLenght) {
				targetArrayIndex = oldLenght-1;
			}
			if(targetArrayIndex < 0) {
				targetArrayIndex = 0;
			}
			if (opacity == 255) {
				newRgbData[i] = rgbData[targetArrayIndex];
			} else {
				int pixel = rgbData[targetArrayIndex];
				if ((pixel & 0xff000000) != 0) {
					pixel = (pixel & 0x00ffffff) | alpha;
				}
				newRgbData[i] = pixel;
			}
		}
	}
	

	/**
	 * Sets the specified transparency to the RGB data.
	 * 
	 * @param transparency the transparency between 0 (fully transparent) and 255 (fully opaque)
	 * @param data the RGB data
	 */
	public static void setTransparency(int transparency, int[] data) {
		transparency = (transparency << 24); // is now 0xtt000000
		for (int i = 0; i < data.length; i++) {
			data[i] = (data[i] & 0x00ffffff) | transparency;
		}
	}

	/**
	 * Sets the specified transparency to the RGB data, but only for pixels that are not full transparent already.
	 * 
	 * @param transparency the transparency between 0 (fully transparent) and 255 (fully opaque)
	 * @param data the RGB data
	 */
	public static void setTransparencyOnlyForOpaque(int transparency, int[] data) {
		transparency = (transparency << 24); // is now 0xtt000000
		for (int i = 0; i < data.length; i++) {
			int pixel = data[i];
			if ((pixel & 0xff000000) != 0) {
				data[i] = (pixel & 0x00ffffff) | transparency;
			}
		}
	}

}
