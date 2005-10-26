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
	private int row = 0;
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
		int sH,c,u,o,r,newI,sW = 0,left = 0,right = this.screenWidth;
		o = this.screenWidth;
		for(int i = 0; i < length;i++){
			row = (row + 1) % this.screenWidth;
			if(row == 0){
				column++;	
				left = this.left[column];
				right = this.right[column];
				sW = this.scaleableWidth[column];
				o = ((sW*100) / this.screenWidth);
			}
			sH = this.scaleableHeight[row];
			
			if(left > row || right < row || this.down[row] < column || this.up[row] > column){
				this.rgbData[i] = 0x028D0A;
			}
			else{
				c = column - (this.screenHeight - sH);
				u = (((this.screenHeight-((this.screenHeight-sH)))*100)/this.screenHeight);
				this.row = this.screenWidth - (left+(this.screenWidth-right));
				if(this.row <= row){
					r = row - this.row;
					o = ((sW*100) / this.screenWidth);
				}else{
					r = row;
					o = (((this.row)*100) / this.screenWidth);
				}
				newI = ((r*100)/o)+(this.screenWidth * ((c*100)/u));
				if(newI >= length)newI = length;
				if(newI < 0)newI = 0;
				if( this.row > row){
					this.rgbData[i] = this.rgbbuffer[newI];
				}else{
					this.rgbData[i] = this.lstrgbbuffer[newI];
				}
			}
		}
//		System.out.print("bishier.2\n");
		this.cubeEffect();
		if(this.lstdegree <= 1)this.stillRun = false;
		return this.stillRun;
	}
	
	
	private void cubeEffect(){		
		for(int i = 0; i < this.scaleableHeight.length;i++){
				this.scaleableHeight[i]--;this.up[i]++;
		}
		int up = this.up[0];
		for(int i = 0; i < up;i++){
			this.scaleableWidth[up + i]-=2;
			this.right[up + i]--;
			this.left[up + i]++;
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
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		this.display.callSerially( this );
	}

}
