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

public class CardScreenChangeAnimation extends ScreenChangeAnimation {
	private boolean stillRun = true;
	//the start degrees of the images
	private int degree = 1,lstdegree = 89;
	//the nxtImage to start in screen
	private int row = 0;
	//the rgb - images
	private int[] rgbData ;
	private int[] rgbbuffer ;
	private int[] lstrgbbuffer ;
	//the height of the columns
	private int[] scaleableHeight;
	private int lstScale = 0;
	//the scale from the row
	private int scaleableWidth,wayForScale,heightScale;
//	//kann nachher weg nur zum testen
//	private boolean first = true;
	public CardScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
			System.out.print("width:"+width+":height:"+height);
			this.wayForScale = (width *100)/ 90;
			this.heightScale = ((height-((height * 12)/100))*100)/90;
			int size = nxtScreenImage.getWidth() * nxtScreenImage.getHeight();
			this.lstScale = width;
			this.scaleableWidth = width;
			this.scaleableHeight = new int [width];
			for(int i = 0;i < this.scaleableHeight.length;i++){
				this.scaleableHeight[i] = height;
			}
			this.rgbbuffer = new int[size];
			this.lstrgbbuffer = new int [size];
			this.rgbData = new int [size];
			nxtScreenImage.getRGB(this.rgbbuffer, 0, width, 0, 0, width, height );
			lstScreenImage.getRGB(this.lstrgbbuffer, 0, width, 0, 0, width, height );
			super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	private int getColorRGB(boolean lastImage,int row,int column){
//		newI = ((row-1))+ (this.width * ((column)));
//----------------------------------------------------
//		float newI = 0;
//		float proWidth = ((float)this.scaleableWidth / (float)this.width);
//		int sH = this.scaleableHeight[row];
//		float sHeight = (float)this.height -(float)sH;
//		if(column != 0)column = column - (this.height - sH);
//		if(column < 0)column = 0;
//		float proHeight = (((float)this.height-(sHeight*2.0f)) / (float)this.height);
//		if(row != 0)row = row - this.row;
//		if(row < 0)row = 0;
//		newI = (((float)row/proWidth))+ (this.width * (int)(column/proHeight));
//		if(newI >38719)newI = 38719;
//		int i = (int)newI;
//-----------------------------------------------------
//		int intProWidth = (this.scaleableWidth*100) / this.width;
//-----------------------------------------------------
		int sH = this.scaleableHeight[row];
		column = column - (this.screenHeight - sH);
		if(column <= 0)column = 1;
//		int intProHeight = ((this.height-((this.height-sH)*2))*100)/this.height;
		int u = (((this.screenHeight-((this.screenHeight-sH)*2))*100)/this.screenHeight);
		if(u <= 0)u++;
		int o = 0;
		if(!lastImage){
			row = row - this.row;
			if(row <= 0)row = 1;
			o = ((this.scaleableWidth*100) / this.screenWidth);
		}else{
			o = (((this.row)*100) / this.screenWidth);
		}
		if(o <= 0)o++;
		int i = ((row*100)/o)+(this.screenWidth * ((column*100)/u));
//		if(i > 38719)i = 38719;
		if(i > this.rgbData.length-1)i = this.rgbData.length-1;
		if(i < 0)i = 0;
		if(lastImage)return this.rgbbuffer[i];
		else return this.rgbbuffer[i];
	}
	
	
	protected boolean animate() {
		// TODO Auto-generated method stub
		int row = 0,column = 0;
		int length = this.rgbData.length;
		int sH,c,u,o,r,newI;
		for(int i = 0; i < length;i++){
			row = (row + 1) % this.screenWidth;
			if(row == 0)column++;	
			sH = this.scaleableHeight[row];
			if(sH < column || (this.screenHeight - sH) > column || this.lstScale < row){// || row > (this.screenWidth-this.row) || row < this.row){
				this.rgbData[i] = 0x000000;
			}
			else{
				c = column - (this.screenHeight - sH);
				if(c <= 0)c = 1;
				u = (((this.screenHeight-((this.screenHeight-sH)*2))*100)/this.screenHeight);
				if(u <= 0)u=1;
				o = 1;
				if(this.row <= row){
					r = row - this.row;
					if(r <= 0)r = 1;
					o = ((this.scaleableWidth*100) / this.screenWidth);
				}else{
					r = row;
					o = (((this.row)*100) / this.screenWidth);
				}
				if(o <= 0)o++;
				newI = ((r*100)/o)+(this.screenWidth * ((c*100)/u));
				if(newI > this.rgbData.length-1)newI = this.rgbData.length-1;
				if(newI < 0)newI = 0;
				this.rgbData[i] = this.rgbbuffer[newI];
			}
//			else if( this.row > row){
//				this.rgbData[i] = getColorRGB(true,row,column);
//			}else{
//				this.rgbData[i] = getColorRGB(false,row,column);
//			}
		}
		this.cubeEffect();
		if(this.lstdegree <= 1)this.stillRun = false;
		return this.stillRun;
	}
	
	
	private void newDirection(){
		//scaling for the lstImage
		int sum = this.screenWidth-1;
		for(int i = 0; i < sum;i++){	
			this.scaleableHeight[i] = this.screenHeight;
			if(i != 0)this.scaleableHeight[i -1] = this.screenHeight;
		}
		this.scaleableWidth = this.screenWidth;
		this.lstScale = this.screenWidth;
		this.row = 0;
		this.degree = 0;
//		this.first = true;
	}
	
	
	
	private void cubeEffect(){		
//		the way to go by degrees in percent
//		the new scalableWidth for the front scaling of the cube
//		if(this.row < this.screenWidth-2)this.row+=2;
		
		this.row = (this.screenWidth -((this.wayForScale * this.lstdegree)/100));
		int endOfHeight; int difference; int scale; int sumScale;
		if(this.degree < 90){		
//			if(this.scaleableWidth <= 0)this.scaleableWidth++;
//			this.lstScale = this.scaleableWidth;
//			this.scaleableWidth-=this.row;
			this.degree++;
			this.scaleableWidth = this.screenWidth - this.row;
			this.lstScale = this.screenWidth;
			endOfHeight =  (this.screenHeight -(this.heightScale * this.degree));
			difference = this.screenHeight + (endOfHeight/100);
			scale = ((this.screenHeight - difference)*100)/this.screenWidth;
			sumScale = scale;	
			int sum = this.lstScale-1;
			int start = this.row+1;
			int finishAnimationSide = this.screenWidth-9;
			int newScale;
				for(int i = start; i <= sum;i++){
//			this.scaleableHeight[i] = this.screenHeight + (endOfHeight/100);
					if(this.row > finishAnimationSide){
						newScale =0 ;
					}else{
						newScale = this.screenHeight - (scale/100);
						scale = scale + sumScale;
					}
					this.scaleableHeight[i] = newScale;
//					if(newScale <= 0)this.scaleableWidth--;
				}
		}	
			if(this.lstdegree > 1)this.lstdegree--;
			endOfHeight =  (this.screenHeight -(this.heightScale * this.lstdegree));
			difference = this.screenHeight + (endOfHeight/100);
			scale = ((this.screenHeight - difference)*100)/this.screenWidth;
			sumScale = scale;
			if(this.lstScale <= this.row)this.lstScale = this.row;
			int start = this.row+1;
			int newScale;
			for(int i = start; i > 0;i--){			
				if(this.row < 9){
					newScale =0 ;
				}else{
					newScale = this.screenHeight - (scale/100);
					scale = scale + sumScale;
				}
				this.scaleableHeight[i] = newScale;			
			}
	}
	
	public void paint(Graphics g) {
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		this.display.callSerially( this );
	}

}
