
//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import java.util.Random;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;

public class TigerStripesBackground extends Background {
	
	
	private final int color;
	private final int stripesColor;
	private Random random;
	private int startX,endX, endStartX, endEndX;
	private static int number;
	private static int maxNumber;
	private static int minimalAbstand = 6, maximalAbstand = 10;
	public TigerStripesBackground(int color, int stripesColor,int number) {
		super();
		this.color = color;
		this.stripesColor = stripesColor;
		this.random = new Random();
		this.maxNumber = number;
	}

	private void stripe(int x,int y, int width, int height){
		do{
			this.startX = this.random.nextInt() % (x + width - this.maximalAbstand);
		}
		while(this.startX < x );
		do{
			this.endX = this.random.nextInt() % (x + width - this.maximalAbstand);
		}
		while(this.endX < x );
		do{
			this.endStartX = this.random.nextInt() % (this.startX + this.maximalAbstand);
		}
		while(this.endStartX <= (this.startX + this.minimalAbstand) );
		do{
			this.endEndX = this.random.nextInt() % (this.endX + this.maximalAbstand);
		}
		while(this.endEndX <= (this.endX + this.minimalAbstand));
	}
	
	private void stripeCounter(int number){
		do {
			this.number = Math.abs( this.random.nextInt() ) % number;
		} while( this.number < 1);
	}
	
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRect( x, y, width, height );
		g.setColor( this.stripesColor);
		g.setStrokeStyle(1);
		g.drawRect(x  ,y + 8,width ,2);
		g.drawRect(x,y,width,height);
		stripeCounter(this.maxNumber);
		for(int i = 0 ;i < this.number; i++){ 
			stripe(x,y,width,height);
			while(this.startX < this.endStartX || this.endX < this.endEndX){
				g.drawLine(this.startX,y+1,this.endX,y + height - 1);
				if(this.startX < this.endStartX){
					this.startX++;
				}
				if(this.endX < this.endEndX){
					this.endX++;
				}
			}
		}
	}
}
