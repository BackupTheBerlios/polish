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
	private Image image;
	private boolean stillRun = true;
	//row = wo sich das neue image befindet am display
	private int row = 0;
	private int[] rgbData ;
	private int[] rgbbuffer ;
	private int[] lstrgbbuffer ;
	private int[] scaleableHeight;
	private int lstScale = 0;
	private int width, height;
	private int scaleableWidth;
	private boolean first = true;
	private boolean letsGo = false;
	public CardScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
			int size = nxtScreenImage.getWidth() * nxtScreenImage.getHeight();
			this.height = height;
			this.width = width;
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
	
	private int getColorRGB(int i,int row,int column){
		int perWidth = this.scaleableWidth / this.width;
		int perHeight = this.scaleableHeight[row] / this.height;
		int newi = (i * this.scaleableHeight[row]);
//		column --;
//		if(column == 0 && row != 0)column++;
//		if(row == 0 && column != 0)row++;

		
		return this.rgbbuffer[i];
	}
	
	protected boolean animate() {
		// TODO Auto-generated method stub
		int row = 0,column = 5;
		for(int i = 0; i < this.rgbData.length;i++){
			row = (row + 1) % this.width;
			if(row == 0)column++;	
					if(this.scaleableHeight[row] < column || (this.height - this.scaleableHeight[row]) > column){
						this.rgbData[i] = 0x000000;
					}else{
						this.rgbData[i] = getColorRGB(i,row,column);
					}

		}
		if(this.first){this.lstScale--;this.cubeEffect();}
		else {this.newDirection();}
		if(this.row < this.width/2)this.row++;
		return this.stillRun;
	}
	
	
	private void newDirection(){
		//scaling for the lstImage
		for(int i = 0; i < this.width-1;i++){	

			if(this.scaleableHeight[i] == 0 && this.scaleableHeight[i+1] > this.height-60){
				this.scaleableHeight[i]=this.scaleableHeight[i+1]-1;
				break;
				}
			else if(this.scaleableHeight[i+1] != 0 && this.scaleableHeight[i] == 0){
				this.scaleableHeight[i] = this.scaleableHeight[i+1];
			}
			else if(this.scaleableHeight[i] != 0 && this.scaleableHeight[i] != this.height){
				this.scaleableHeight[i]++;
			}
			else if(i == this.row ){
				this.scaleableHeight[i]=this.height;
				this.row++;
				break;
			}
		}
	}
	
	
	private void cubeEffect(){
		//scaling for the lstImage
		for(int i = this.width-1; i >= 0;i--){	
			if(this.row < this.width/2 && i > this.lstScale && this.scaleableHeight[i] > this.height-60){
				this.scaleableHeight[i]-=2;
			}else if(this.scaleableHeight[i] <= this.height-60 && this.scaleableHeight[i]!=0) {
				this.scaleableHeight[i]=0;
				this.scaleableWidth--;
			}
			else{
				if(this.scaleableHeight[i] != this.height && this.scaleableHeight[i]!=0){
					this.scaleableHeight[i]=0;
					this.scaleableWidth--;
					break;
				}
			}
			
			if(i < this.row && this.scaleableHeight[i]!=0){
				this.scaleableWidth--;
				this.scaleableHeight[i]=0;
			}
			if(this.scaleableWidth == 1)this.first=false;
		}
	}
	
	public void paint(Graphics g) {
		g.drawRGB(this.rgbData,0,this.width,0,0,this.width,this.height,false);
		this.display.callSerially( this );
	}

}
