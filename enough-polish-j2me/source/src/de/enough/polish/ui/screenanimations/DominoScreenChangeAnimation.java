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

public class DominoScreenChangeAnimation extends ScreenChangeAnimation {
	private boolean stillRun = true;
	//the start degrees of the images
	private int degree = 1,lstdegree = 89;
	//the nxtImage to start in screen
	private int row = 0,currentX=0,id = 0;
	private int[] left ,right ,up,down;
	//the rgb - images
	private int[] rgbData ;
	private int[] rgbbuffer ;
	private int[] lstrgbbuffer ;
	//the height of the columns
	private int[] scaleableHeight;
	private int[] scaleableWidth;
 	//the scale from the row
	private int wayForScale,heightScale;
//	//kann nachher weg nur zum testen
//	private boolean first = true;
	public DominoScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
			System.gc();
			System.out.print("width:"+width+":height:"+height);
			this.row = 0;
			this.id = 0;
//			this.row = width;
			this.degree = 1;
			this.lstdegree = 89;
			this.stillRun = true;
			this.wayForScale = (width *100)/ 90;
			this.heightScale = ((height-((height * 12)/100))*100)/90;
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
			this.rgbbuffer = new int[size];
			this.lstrgbbuffer = new int [size];
			this.rgbData = new int [size];
			nxtScreenImage.getRGB(this.rgbbuffer, 0, width, 0, 0, width, height );
			lstScreenImage.getRGB(this.lstrgbbuffer, 0, width, 0, 0, width, height );
			lstScreenImage.getRGB(this.rgbData, 0, width, 0, 0, width, height );
			super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	
	
	protected boolean animate() {
		// TODO Auto-generated method stub
//		System.out.print("bishier.1\n");
		int row = 0,column = 0;
		int length = this.rgbData.length-1;
		int sH,c,scalePercentH,scalePercentWidth,r,newI,sW = 0,left = 0,right = this.screenWidth;
		scalePercentWidth = this.screenWidth;
		for(int i = 0; i < length; i++){
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
				this.rgbData[i] = this.rgbbuffer[i];
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

				this.rgbData[i] = this.lstrgbbuffer[newI];
			}
		}
//		System.out.print("bishier.2\n");
		this.cubeEffect();
		if(this.scaleableHeight[0] <= 0)this.stillRun = false;
//		this.id++;
		return this.stillRun;
	}
	
	
	private void cubeEffect(){		
//		for(int i = 0; i < this.scaleableHeight.length;i++){
//				this.scaleableHeight[i]--;this.up[i]++;
//		}
//		int up = this.up[0];
//		if(up > 110)up = 110;
//		for(int i = 0; i < up;i++){
//			if(this.scaleableWidth[up + i] > 10){
//				this.scaleableWidth[up + i]-=10;
//				this.right[up + i]-=5;
//				this.left[up + i]+=5;
//			}
//		}
//		int id=0,idNext=0;
		for(int i = 0; i < this.scaleableHeight.length;i++){
			this.scaleableHeight[i]-= 8;
			this.down[i]-= 8;
//			this.up[i]+=4;
		}
		for(int i = 0; i < this.scaleableWidth.length;i++){
			this.scaleableWidth[i]-= 8;
			this.right[i]-= 4;
			this.left[i]+= 4;
		}
	}
	
	public void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
		this.nextCanvasImage.getRGB( this.rgbbuffer, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}

	//#if polish.hasPointerEvents
	public void pointerPressed(int x, int y) {
		super.pointerPressed(x, y);
		this.nextCanvasImage.getRGB( this.rgbbuffer, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
	}
	//#endif
	
	public void paint(Graphics g) {
		g.fillRect(0,0,this.screenWidth,this.screenHeight);
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		this.currentX+=1;
		this.display.callSerially( this );
	}

}
