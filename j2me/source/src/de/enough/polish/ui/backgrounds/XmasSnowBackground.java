//#condition polish.usePolishGui
/*
 * Created on 06.12.2005 at 11:38:02.
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
package de.enough.polish.ui.backgrounds;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import de.enough.polish.ui.Background;

public class XmasSnowBackground extends Background {
	private boolean isRunning = true,plus = true;
	private Image imgSnowflake;
	private int[] x,y,z;
	private int numbers,color,far;
	private final Random rand = new Random();
	public XmasSnowBackground(int color, String url, int width, int height, int far, int numbers) {
		super();
		this.color = color;
		this.far = far;
		this.numbers = numbers;
		this.x = new int[numbers];
		this.y = new int[numbers];
		this.z = new int[numbers];
		int i = 0;
		while(i < numbers){
			this.x[i] = Math.abs( rand.nextInt() % width );
			this.y[i] = Math.abs( rand.nextInt() % height );
			this.z[i] = Math.abs( rand.nextInt() % far );
			i++;
		}
		try {
			this.imgSnowflake = Image.createImage(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean animate() {
		// TODO Auto-generated method stub	
		return this.isRunning;
	}
	
	public void paint(int x, int y, int width, int height, Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(this.color);
		g.fillRect(0,0,width,height);
		g.setColor(0xffffff);
		int i = 0;
		while(i < this.numbers){
//			this.x[i]++;
			if(this.y[i] < height && this.x[i] < width){
				int z = this.z[i];
				this.y[i]+= z;	
				this.x[i]+=1;
			}
			else{
				this.y[i] = 0;
				this.x[i] = Math.abs( rand.nextInt() % width );
				this.z[i] = Math.abs( rand.nextInt() % this.far );
			}
//			System.out.print("X:"+this.x[i]+";Y:"+this.y[i]+";Z:"+this.z[i]+";width"+width+";height"+height+"\n");
			int size = this.z[i];
			g.fillRoundRect(this.x[i],this.y[i],size,size,size,size);
			i++;
		}
	}
}
