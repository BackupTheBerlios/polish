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
	private int degree = 1;
	private int row = 0;
	private int[] rgbData ;
	private int[] rgbbuffer ;
	private int[] lstrgbbuffer ;
	private int[] scaleableHeight;
	private int lstScale = 0;
	private int scaleableWidth;
	private int ImageWidth = 0;
	private boolean first = true;
	public CardScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
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
		if(column != 0)column = column - (this.screenHeight - sH);
		if(column <= 0)column = 1;
//		int intProHeight = ((this.height-((this.height-sH)*2))*100)/this.height;
		if(row != 0)row = row - this.row;
		if(row <= 0)row = 1;
		int u = (((this.screenHeight-((this.screenHeight-sH)*2))*100)/this.screenHeight);
		if(u == 0)u++;
		int o = ((this.scaleableWidth*100) / this.screenWidth);
		if(o == o)o++;
		int i = ((row*100)/o)+(this.screenWidth * ((column*100)/u));
		if(i >38719)i = 38719;
		if(i < 0)i = 0;
		if(lastImage)return this.lstrgbbuffer[i];
		else return this.rgbbuffer[i];
	}
	
	
	protected boolean animate() {
		// TODO Auto-generated method stub
		int row = 0,column = 0;
		int length = this.rgbData.length;
		for(int i = 0; i < length;i++){
			row = (row + 1) % this.screenWidth;
			if(row == 0)column++;	
			int sH = this.scaleableHeight[row];
					if(sH < column || (this.screenHeight - sH) > column || this.lstScale < row){// || row > (this.screenWidth-this.row) || row < this.row){
						this.rgbData[i] = 0x000000;
					}
					else if( this.row > row){
						this.rgbData[i] = getColorRGB(true,row,column);
					}
					else{
						this.rgbData[i] = getColorRGB(false,row,column);
					}
		}
		if(this.first){this.cubeEffect();}
		else {this.newDirection();}
//		if(this.scaleableWidth < 2)this.first = false;
		return this.stillRun;
	}
	
	
	private void newDirection(){
		//scaling for the lstImage
		for(int i = 0; i < this.screenWidth-1;i++){	
			this.scaleableHeight[i] = this.screenHeight;
			if(i != 0)this.scaleableHeight[i -1] = this.screenHeight;
		}
		this.scaleableWidth = this.screenWidth;
		this.lstScale = this.screenWidth;
		this.row = 0;
		this.degree = 0;
		this.first = true;
	}
	
	
	
	private void cubeEffect(){
		if(this.degree < 90)this.degree++;
		int scaleSum = (this.screenWidth *100)/ 90;
		this.scaleableWidth = (this.screenWidth -((scaleSum * this.degree)/100));
		if(this.scaleableWidth <= 0)this.scaleableWidth++;
		this.lstScale = this.scaleableWidth;
		this.row++;
		this.scaleableWidth-=this.row;
		int heightScale = ((this.screenHeight-((this.screenHeight * 12)/100))*100)/90;
		int endOfHeight =  (this.screenHeight -(heightScale * this.degree));
		int difference = this.screenHeight + (endOfHeight/100);
		int scale = ((this.screenHeight - difference)*100)/this.screenWidth;
		int sumScale = scale;
//		System.out.print(heightScale+".:."+endOfHeight+".o:o."+o+".:."+r+"\n");
		for(int i = this.row+1; i <= this.lstScale-1;i++){
//			this.scaleableHeight[i] = this.screenHeight + (endOfHeight/100);
			int newScale = this.screenHeight - (scale/100);
			this.scaleableHeight[i] = newScale;
			if(newScale <= 0)this.scaleableWidth--;
//			System.out.print(this.screenHeight - (r/100)+"\n");
			scale = scale + sumScale;
		}
		scale = ((this.screenHeight - difference)*100)/this.screenWidth;
		sumScale = scale;
		for(int i = this.row+1; i > 0;i--){
			int newScale = this.screenHeight - (scale/100);
			this.scaleableHeight[i] = newScale;
			scale = scale + sumScale;
		}
//		this.scaleableWidth--;
//		this.lstScale--;
//		System.out.print("degree"+this.degree+".sum."+scale+".scaleWidth."+this.scaleableWidth+"\n");
//		System.out.print("r."+this.row+".sW."+this.scaleableWidth+"::u."+u+".z."+z+".a.\n");
//		for(int i = this.row; i < this.scaleableWidth;i++){	
//		if(i <= z){
//			if(this.scaleableHeight[i] >= this.screenHeight-80){
//				if(this.scaleableHeight[i] > 0){
//					this.scaleableHeight[i]-=10;
//				}
//			}else if(this.scaleableHeight[i] != 0){
//				this.scaleableHeight[i] = 0;
//				this.scaleableWidth--;
//				this.scaleableHeight[i+1] = this.screenHeight-18;
//			}
//		}
//		if(i >= z)z = z+z;
//	}
//		scaling for the lstImage
//		for(int i = this.screenWidth-1; i >= 0;i--){	
//			if(this.row < this.screenWidth/2 && i > this.lstScale && this.scaleableHeight[i] > this.screenHeight-100){
//				this.scaleableHeight[i]-=2;
//			}
//			else if(this.scaleableHeight[i] <= this.screenHeight-100 && this.scaleableHeight[i]!=0) {
//				this.scaleableHeight[i]=0;
//				this.scaleableWidth--;
//			}
//			else{
//				if(this.scaleableHeight[i] != this.screenHeight && this.scaleableHeight[i]!=0){
//					this.scaleableHeight[i]=0;
//					this.scaleableWidth--;
//					break;
//				}
//			}			
//			if(i < this.row && this.scaleableHeight[i]!=0){
//				this.scaleableWidth--;
//				this.scaleableHeight[i]=0;
//			}
//			if(this.scaleableWidth <= 2)this.first=false;
//		}
//--------------------------------------------------------------
//		for(int i = this.screenWidth-1; i >= 0;i--){	
//			this.scaleableHeight[i]-=1;
//		}
//--------------------------------------------------------------
//			if(this.scaleableHeight[this.row]!=0 && this.scaleableHeight[this.screenWidth-1-this.row]!= 0)
//				this.scaleableHeight[this.screenWidth-1-this.row]= 0;
//				this.scaleableWidth--;
//				this.scaleableHeight[this.row]=0;
//				this.scaleableWidth--;
//		for(int i = this.row; i < this.scaleableWidth;i++){
//			this.scaleableHeight[i]--;
//		}
//---------------------------------------------------------------
//		this.scaleableHeight[this.row] = 5;
//		this.row++;
//		this.scaleableWidth--;
//		this.scaleableHeight[this.row]--;
//		int sH = this.scaleableHeight[this.row];
//		int u = this.screenHeight - sH ;
//		if(u == 0)u++;
//		int z = 1;	
//		if(u < this.scaleableWidth)z = (this.scaleableWidth) / u;
//		int a = this.screenWidth - z;
//		z -= this.row;
//		System.out.print("r."+this.row+".sW."+this.scaleableWidth+"::u."+u+".z."+z+".a."+a+"\n");
//		for(int i = this.row; i < this.scaleableWidth;i++){	
//			if(i <= z){
//				if(this.scaleableHeight[i] >= this.screenHeight-80){
//					if(this.scaleableHeight[i] > 0){
//						this.scaleableHeight[i]-=10;
//					}
//				}else if(this.scaleableHeight[i] != 0){
//					this.scaleableHeight[i] = 0;
//					this.scaleableWidth--;
//					this.scaleableHeight[i+1] = this.screenHeight-18;
//				}
//			}
//		}
	}
	
	public void paint(Graphics g) {
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		this.display.callSerially( this );
	}

}
