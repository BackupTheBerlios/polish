//#condition polish.usePolishGui && polish.midp2 && polish.cldc1.1
/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * @author Simon Schmitt
 *
 */
public class AlienGlowTextEffect extends TextEffect {
	
	private final static int CLEAR_COLOR = 0xFF000123;
	private int clearColor;

	private String lastText;
	private int lastTextColor;
	int[] localRgbBuffer;

	private int innerColor = 0xFFFFFFFF;
	private int outerColor = 0xFF00FF00;
	
	private int radius;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		// TODO ask for a gaussain radius
		radius=8;

		//calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		
		// offset of an invisble area caused by negative (x,y)
		int invX=Math.max(0, -(startX-radius));
		int invY=Math.max(0, -(startY-radius));
		
		// check whether the string has to be rerendered
		if (lastText!=text || lastTextColor != textColor) {
			lastText=text;
			lastTextColor=textColor;
			
			// create Image, Graphics, ARGB-buffer
			Graphics bufferG;
			Image midp2ImageBuffer = Image.createImage( fWidth + this.radius*2, fHeight + this.radius*2);
			bufferG = midp2ImageBuffer.getGraphics();
			localRgbBuffer = new int[ (fWidth + this.radius*2) * (fHeight + this.radius*2) ];
			
			// draw pseudo transparent Background
			bufferG.setColor( CLEAR_COLOR );
			bufferG.fillRect(0,0,fWidth + this.radius*2, fHeight + this.radius*2);
			
			// draw String on Graphics
			bufferG.setFont(font);
			
			// draw outlineText
			bufferG.setColor( outerColor );
			bufferG.drawString(text,radius-1,radius-1, Graphics.LEFT | Graphics.TOP);
			bufferG.drawString(text,radius-1,radius+1, Graphics.LEFT | Graphics.TOP);
			bufferG.drawString(text,radius+1,radius-1, Graphics.LEFT | Graphics.TOP);
			bufferG.drawString(text,radius+1,radius+1, Graphics.LEFT | Graphics.TOP);
			
			bufferG.setColor( innerColor );
			bufferG.drawString(text,radius,radius, Graphics.LEFT | Graphics.TOP);
			
			// get RGB-Data from Image
			midp2ImageBuffer.getRGB(localRgbBuffer,0,fWidth + this.radius*2, 0, 0, fWidth + this.radius*2, fHeight + this.radius*2);
			
			// check clearColor
			int[] clearColorArray = new int[1]; 
			midp2ImageBuffer.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
			this.clearColor = clearColorArray[0];
			
			// transform RGB-Data
			for (int i=0; i<localRgbBuffer.length;i++){
				//	 perform Transparency
				if  (localRgbBuffer[i] == this.clearColor){
					localRgbBuffer[i] = 0x00000000;
				}
			}
			
			// perform a gaussain convolution, with a 5x5 matrix
			DrawUtil.applyFilter(
					DrawUtil.FILTER_GAUSSIAN_3
					,150,localRgbBuffer,fWidth + this.radius*2,fHeight + this.radius*2);

		}
		
		//TODO: Positionierung überdanekn
		// draw RGB-Data
		g.drawRGB(localRgbBuffer, invY * ( fWidth + this.radius*2)+invX ,fWidth + this.radius*2, ( startX-this.radius+invX<=0 ? 0 :startX-this.radius+invX), ( startY-this.radius+invY<=0 ? 0 :startY-this.radius+invY) , fWidth + this.radius*2-invX, fHeight + this.radius*2-invY, true);
		
		g.setColor( textColor );
		g.drawString(text,startX,startY, Graphics.LEFT | Graphics.TOP);
	}
	
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.text-alien-glow-inner-color
			Color sShadowColorObj = style.getColorProperty( "text-alien-glow-inner-color" );
			if (sShadowColorObj != null) {
				this.innerColor = sShadowColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-alien-glow-outer-color
			Color eShadowColorObj = style.getColorProperty( "text-alien-glow-outer-color" );
			if (eShadowColorObj != null) {
				this.outerColor = eShadowColorObj.getColor();
			}
		//#endif
/*
		//#if polish.css.text-drop-shadow-size
			Integer sizeInt = style.getIntProperty( "text-drop-shadow-size" );
			if (sizeInt != null) {
				this.size = sizeInt.intValue();
			}
		//#endif
		 */
			
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		this.lastText = null;
		this.localRgbBuffer = null;
	}
	
}
