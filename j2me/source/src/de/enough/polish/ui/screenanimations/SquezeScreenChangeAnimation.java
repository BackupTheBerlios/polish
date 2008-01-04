//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 14.09.2005 at 15:30:15.
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

public class SquezeScreenChangeAnimation extends ScreenChangeAnimation {
	private boolean stillRun = true;
	private int row ;
	private int[] left ,right ,up,down;
	//the rgb - images
	private int[] rgbData ;
	//the height of the columns
	private int[] scaleableHeight;
	private int[] scaleableWidth;
	
	public SquezeScreenChangeAnimation() {
		super();
		this.useLastCanvasRgb = true;
		this.useNextCanvasRgb = true;
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable, boolean isForward  ) 
	{
			this.row = 0;
			this.stillRun = true;
			int size = width * height;
			this.left = new int [height];
			this.right = new int [height];
			this.scaleableWidth = new int [height];
			for(int i = 0; i < this.scaleableWidth.length;i++){
				this.scaleableWidth[i] = width;
				this.left[i] = 0;
				this.right[i] = width;
			}
			this.up = new int [width];
			this.down = new int [width];
			this.scaleableHeight = new int [width];
			for(int i = 0;i < this.scaleableHeight.length;i++){
				this.scaleableHeight[i] = height;
				this.up[i] = 0;
				this.down[i] = height;
			}
			super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable, isForward );
			this.rgbData = new int [size];
			System.arraycopy(this.lastCanvasRgb, 0, this.rgbData, 0, this.lastCanvasRgb.length );
	}
	
	
	
	protected boolean animate() {
		int row = 0,column = 0;
		int length = this.rgbData.length-1;
		int sH,c,scalePercentH,scalePercentWidth,r,newI,sW = 0,left = 0,right = this.screenWidth;
		scalePercentWidth = this.screenWidth;
		for(int i = 0; i < length;i++){
			row = (row + 1) % this.screenWidth;
			if(row == 0){
				column++;	
				left = this.left[column];
				right = this.right[column];
				sW = this.scaleableWidth[column];
				scalePercentWidth = ((sW*100) / this.screenWidth);
			}
			sH = this.scaleableHeight[row];
			
			if(left > row || right < row || this.down[row] < column || this.up[row] > column){
				this.rgbData[i] = this.nextCanvasRgb[i];
			}
			else{
				c = column - (this.screenHeight - sH);
				if(c < 1)c++;
				scalePercentH = (((this.screenHeight-((this.screenHeight-sH)))*100)/this.screenHeight);
				this.row = left + ((this.screenWidth - right)/this.screenWidth);
				if(this.row <= row){
					r = row - this.row;
					scalePercentWidth = (sW*100) / this.screenWidth;
				}else{
					r = row;
					scalePercentWidth = (this.row*100) / this.screenWidth;
				}
				
//				if(r < 1)r++;
//				if(sW < 1)sW++;
				scalePercentWidth = (((this.screenWidth-((this.screenWidth-sW)))*100)/this.screenWidth);
				if(scalePercentWidth < 1)scalePercentWidth++;
				if(scalePercentH < 1)scalePercentH++;
				newI = ((r*100)/scalePercentWidth)+(this.screenWidth * ((c*100)/scalePercentH));
				if(newI >= length)newI = length;
				if(newI < 0)newI = 0;

				this.rgbData[i] = this.lastCanvasRgb[newI];
			}
		
		}
		squeezeEffect();
		if(this.scaleableHeight[0] <= 0) {
			this.stillRun = false;
		}
		return this.stillRun;
	}
	
	
	private void squeezeEffect(){		
		for(int i = 0; i < this.scaleableHeight.length;i++){
			this.scaleableHeight[i]-=18;
			this.up[i]+=18;
		}
	}
	
	
	public void paintAnimation(Graphics g) {
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
	}

}
