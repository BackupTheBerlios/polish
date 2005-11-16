//#condition polish.usePolishGui && polish.midp2
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.AccessibleCanvas;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

public class TestScreenChangeAnimation extends ScreenChangeAnimation {
	private int [] x,y,z;
	//the rgb - images
	private int[] rgbData, rgbbuffer, lstrgbbuffer ;
	
	
	public TestScreenChangeAnimation() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		this.x = new int[4];
		this.y = new int[4];
		this.z = new int[4];           
		int i = 0;
		this.x[i] = 0;
		this.y[i] = 0;
		this.z[i] = 0;
		i++;
		this.x[i] = width;
		this.y[i] = 0;
		this.z[i] = 0;
		i++;
		this.x[i] = 0;
		this.y[i] = height;
		this.z[i] = 0;
		i++;
		this.x[i] = width;
		this.y[i] = height;
		this.z[i] = 0;	
		int size = width * height;
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
		int length = this.rgbData.length-1;
		int newI;
		int scale = 2;
		int x,y;
		int id,row = 1,column = 1;
		for(int i = 0; i < length;i++){
			row = (row + 1) % this.screenWidth;
			if(row == 0){
				column++;	
			}
			id = i + this.screenWidth;
			y = id / this.screenHeight;
			x = id / this.screenWidth;
			if( x < 1)x++;
			if( y < 1)y++;
			newI = (row * scale)*(column * scale);
			if(newI >= length)newI = length;
			if(newI < 0)newI = 0;
			this.rgbData[i] = this.rgbbuffer[newI];
		}
		return true;
	}

	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		g.drawRGB(this.rgbData,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		this.display.callSerially( this );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
