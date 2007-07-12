//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 14.09.2005 at 11:56:03.
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

public class CageScreenChangeAnimation extends ScreenChangeAnimation {
	//row = wo sich das neue image befindet am display
	private int row = 0;
	private int[] rgbNew ;
	private int[] rgbimage ;
	private int width, height;
	
	
	public CageScreenChangeAnimation() {
		super();
	}
	
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
			this.row = 0;
			int size = width * height;
			this.width = width;
			this.height = height;
			this.rgbNew = new int [size];
			this.rgbimage = new int[size];
			nxtScreenImage.getRGB(this.rgbNew, 0, width, 0, 0, width, height );
			lstScreenImage.getRGB(this.rgbimage, 0, width, 0, 0, width, height );
			super.show(style, dsplay, width, height, lstScreenImage, nxtScreenImage, nxtCanvas, nxtDisplayable );
	}

	
	
	protected boolean animate() {
		boolean doSwitchRow = true;
		int currentRow = 0;
		for(int i = 0; i < this.rgbimage.length;i++){		
			if(doSwitchRow && currentRow <= this.row){
				this.rgbimage[i] = this.rgbNew[i];
			}
			else if(!doSwitchRow && currentRow >= this.width-this.row ){
				this.rgbimage[i] = this.rgbNew[i];
			}	
			currentRow = (currentRow + 1) % this.width;
			if(currentRow == 0){
				doSwitchRow = !doSwitchRow;
			}
		}
		this.row+=4;
		if(this.row >= this.width) {
			this.row = 0;
			return false;
		}
		return true;
	}

	public void paintAnimation(Graphics g) {
		g.drawRGB(this.rgbimage,0,this.width,0,0,this.width,this.height,false);
	}

}
