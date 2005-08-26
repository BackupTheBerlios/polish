/*
 * Created on 24.08.2005 at 15:37:25.
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.AccessibleCanvas;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;

/**
 * @author Tim Muders
 *
 */
public class BwToColorScreenChangeAnimation 
extends ScreenChangeAnimation 
{
	private Image firstScreenImage;
	
	private int[] nextScreenRgb;
	private int[] currentScreenRgb;
	private int[] bufferedScreenRgb;
	private boolean firstStep;
	private int factor,count=0;
	/**
	 * 
	 */
	public BwToColorScreenChangeAnimation() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		System.out.print("show\n");
		if ( this.nextScreenRgb == null ) {
			this.nextScreenRgb = new int[ width * height ];
			this.currentScreenRgb = new int[ width * height ];
			this.bufferedScreenRgb = new int [width * height];
			if(this.firstScreenImage == null){
				System.out.print("fill image\n");
				this.firstScreenImage = this.nextCanvasImage;
			}
		}
		nxtScreenImage.getRGB( this.nextScreenRgb, 0, width, 0, 0, width, height );
		nxtScreenImage.getRGB( this.bufferedScreenRgb, 0, width, 0, 0, width, height );
		// TODO render a black and white version out of the nextScreenRgb array
		int color,red,green,blue;
		for(int i = 0;i < this.currentScreenRgb.length;i++){
			color = this.nextScreenRgb[i];			
			red = (0x00FF & (color >>> 16));	
			green = (0x0000FF & (color >>> 8));
			blue = color & (0x000000FF );
			int brightness = (red + green + blue) / 3;
			if ( brightness > 127 ) {
				this.currentScreenRgb[i] = 0xFFFFFF;
			} else {
				this.currentScreenRgb[i] = 0x000000;
			}
//			color = (blue + (green * 256) + (red * 65536));
	
		}
		this.firstStep = true;
		super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	
	
	public void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
		this.nextCanvasImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
//		if(this.firstScreenImage != null){
//			this.firstScreenImage.getRGB( this.bufferedScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
//		}
//		int color,red = 0,green = 0,blue = 0,nextRed,nextBlue,nextGreen,nextColor;
////		for(int i = 1; i <= 2;i++){
//			int id = 0;
//			while(id < this.nextScreenRgb.length){
//				
//				color = this.bufferedScreenRgb[id];
//				nextColor = this.nextScreenRgb[id];
//
//				if(this.bufferedScreenRgb[id] != this.nextScreenRgb[id]){
//					red = (0x00FF & (color >>> 16));	
//					green = (0x0000FF & (color >>> 8));
//					blue = color & (0x000000FF );
//					nextRed = (0x00FF & (nextColor >>> 16));
//					nextGreen = (0x0000FF & (nextColor >>> 8));
//					nextBlue = nextColor & (0x000000FF);
////					System.out.print("change pixel");
//					int brightness = (nextRed + nextGreen + nextBlue) / 3;
//					if ( brightness > 127 ) {
////						this.currentScreenRgb[id] = 0xFFFFFF;
//						red = 255;
//						green = 255;
//						blue = 255;
//					} else {
////						this.currentScreenRgb[id] = 0x0;
//						red = 0;
//						green = 0;
//						blue = 0;
//					}
//					for(int u = 0;u < this.count;u++){
//						if(red != nextRed){
//							red = this.colorValue(red,nextRed,-1);
//						}
//						if(green != nextGreen){
//							green = this.colorValue(green,nextGreen,-1);
//						}
//						if(blue != nextBlue){
//							blue = this.colorValue(blue,nextBlue,-1);
//						}
//					}
//					this.currentScreenRgb[id] = (red << 16) | (green << 8) | blue;				
//				}
//				id++;
//			}
////		}
//			System.out.print("id:"+id+"\n");
//			this.firstScreenImage = this.nextCanvasImage;
	}

	//#if polish.hasPointerEvents
	public void pointerPressed(int x, int y) {
		super.pointerPressed(x, y);
		this.nextCanvasImage.getRGB( this.nextScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	//#endif

	private int colorValue(int currentColor,int targetColor,int factor){
		int speed = 6;		
			if(currentColor < targetColor){
				int sum =    (targetColor-currentColor ) ;
				if(sum > speed)sum = sum / speed;				
				if(sum == 0)sum = 1;
				currentColor+= sum;
			}
			else{
//				currentColor = targetColor;
				int sum =  (currentColor-targetColor ) ;
				if(sum > speed)sum = sum / speed;
				if(sum == 0)sum = 1;
				currentColor-= sum;
			}		

		return currentColor;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		// TODO Auto-generated method stub
		boolean bolReturn = false;
		int color,nextColor,red,green,blue,nextRed,nextGreen,nextBlue;
		for(int i = 0;i < this.currentScreenRgb.length;i++){
			color = this.currentScreenRgb[i];
			nextColor = this.nextScreenRgb[i];
			red = (0x00FF & (color >>> 16));	
			green = (0x0000FF & (color >>> 8));
			blue = color & (0x000000FF );
			nextRed = (0x00FF & (nextColor >>> 16));
			nextGreen = (0x0000FF & (nextColor >>> 8));
			nextBlue = nextColor & (0x000000FF);
			if(red != nextRed){
				red = this.colorValue(red,nextRed,-1);
			}
			if(green != nextGreen){
				green = this.colorValue(green,nextGreen,-1);
			}
			if(blue != nextBlue){
				blue = this.colorValue(blue,nextBlue,-1);
			}
			if(red != nextRed || green != nextGreen || blue != nextBlue){
				this.currentScreenRgb[i] = (red << 16) | (green << 8) | blue;
				bolReturn = true;
			}
		}
		if (!bolReturn) {
			this.currentScreenRgb = null;
			this.nextScreenRgb = null;
			// TODO reset speed
		}
			this.count++;
			return bolReturn;
	
	}

	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		g.drawRGB(this.currentScreenRgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight, false );		
		this.display.callSerially( this );
	}

}
//for(int i = 0;i < this.currentScreenRgb.length;i++){
//	color = this.nextScreenRgb[i];			
//	red = (0x00FF & (color >>> 16));	
//	green = (0x0000FF & (color >>> 8));
//	blue = color & (0x000000FF );
//	int brightness = (red + green + blue) / 3;
//	if ( brightness > 127 ) {
//		this.currentScreenRgb[i] = 0xFFFFFF;
//	} else {
//		this.currentScreenRgb[i] = 0x0;
//	}
//
//}
//int id = 0;
//while(id < this.count){
//for(int i = 0;i < this.currentScreenRgb.length;i++){
//	color = this.currentScreenRgb[i];
//	nextColor = this.nextScreenRgb[i];
//	red = (0x00FF & (color >>> 16));	
//	green = (0x0000FF & (color >>> 8));
//	blue = color & (0x000000FF );
//	nextRed = (0x00FF & (nextColor >>> 16));
//	nextGreen = (0x0000FF & (nextColor >>> 8));
//	nextBlue = nextColor & (0x000000FF);
//	if(red != nextRed){
//		red = this.colorValue(red,nextRed,1);
//	}
//	if(green != nextGreen){
//		green = this.colorValue(green,nextGreen,1);
//	}
//	if(blue != nextBlue){
//		blue = this.colorValue(blue,nextBlue,1);
//	}
//	if(red != nextRed || green != nextGreen || blue != nextBlue){
//		this.currentScreenRgb[i] = (red << 16) | (green << 8) | blue;
//	}
//}id++;
//}