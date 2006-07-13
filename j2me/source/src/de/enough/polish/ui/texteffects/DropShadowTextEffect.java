//#condition polish.usePolishGui && polish.midp2 && polish.cldc1.1
/*
 * Created on 10.07.2006 at 12:22:13.
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
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a dropshadow behind a text, whereas you are able to specify
 *  the shadows inner and outer color.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: drop-shadow;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-drop-shadow-start-color</b>: the inner color of the shadow, which should be less opaque than the text. </li>
 * 	 <li><b>text-drop-shadow-end-color</b>: the outer color of the shadow, which should be less than opaque the inner color. </li>
 * 	 <li><b>text-drop-shadow-offsetx:</b>: use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.</li>
 * 	 <li><b>text-drop-shadow-offsety:</b>: use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.</li>
 *   <li><b>text-drop-shadow-size:</b>: use this for finetuning the shadow's radius.</li>
 * </ul>
 * <p>Choosing the same inner and outer color and varying the transparency is recommended. Dropshadow just works, if the Text is opaque.</p>
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        11-Jul-2006
 * </pre>
 * @author Simon Schmitt
 */
public class DropShadowTextEffect extends TextEffect {
		
	private final static int CLEAR_COLOR = 0xFF000123;
	private int clearColor;
	
	private String lastText;
	private int lastTextColor;
	int[] localRgbBuffer;
	
	private int startColor = 0xA0909090;
	private int endColor = 0x20909090;
	private int size=6;
	private int xOffset=1, yOffset=2;
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		// calculate imagesize
		Font font = g.getFont();
		int fHeight = font.getHeight();
		int fWidth = font.stringWidth( text );
		int startX = getLeftX( x, orientation, fWidth );
		int startY = getTopY( y, orientation, fHeight, font.getBaselinePosition() );
		int iLeft=0, /*iRight=0,*/ iTop=0/*, iBottom=0*/; 
		
		// additional Margin for the image because of the shadow
		iLeft = this.size-this.xOffset<0 ? 0 : this.size-this.xOffset;
		//iRight = this.size+this.xOffset<0 ? 0 : this.size+this.xOffset;
		iTop = this.size-this.yOffset<0 ? 0 : this.size-this.yOffset;
		//iBottom = this.size+this.yOffset<0 ? 0 : this.size+this.yOffset;
		
		// offset of an invisble area caused by negative (x,y)
		int invX=Math.max(0, -(startX-iLeft));
		int invY=Math.max(0, -(startY-iTop));
		
		// check whether the string has to be rerendered
		if (lastText!=text || lastTextColor != textColor) {
			lastText=text;
			lastTextColor=textColor;
			
			// create Image, Graphics, ARGB-buffer
			Graphics bufferG;
			Image midp2ImageBuffer = Image.createImage( fWidth + this.size*2, fHeight + this.size*2); // iLeft+iRight=2*size??
			bufferG = midp2ImageBuffer.getGraphics();
			localRgbBuffer = new int[ (fWidth + this.size*2) * (fHeight + this.size*2) ];
			
			// draw pseudo transparent Background
			bufferG.setColor( CLEAR_COLOR );
			bufferG.fillRect(0,0,fWidth + this.size*2, fHeight + this.size*2);
			
			// draw String on Graphics
			bufferG.setFont(font);
			bufferG.setColor( textColor );
			bufferG.drawString(text,iLeft,iTop, Graphics.LEFT | Graphics.TOP);
			
			// get RGB-Data from Image
			midp2ImageBuffer.getRGB(localRgbBuffer,0,fWidth + this.size*2, 0, 0, fWidth + this.size*2, fHeight + this.size*2);
			
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
			
			// set colors
			int[] gradient = DrawUtil.getGradient( this.startColor, this.endColor, this.size );
			/*
			//	TODO: Test several gradients
			//int[] test
			gradient=DrawUtil.getTestGradient(this.startColor, this.endColor, this.size );
			// correct the transparency
			for (int i=0; i<gradient.length; i++){
				gradient[i]=(gradient[i] & 0x00FFFFFF) + ((254-i*10)<<24);
				System.out.println("   "+Integer.toHexString(gradient[i]));
			}*/
			
			// walk over the text and look for non-transparent Pixels	
			for (int ix=-this.size+1; ix<this.size; ix++){
				for (int iy=-this.size+1; iy<this.size; iy++){
					//int gColor=gradient[ Math.max(Math.abs(ix),Math.abs(iy))];
					//int gColor=gradient[(Math.abs(ix)+Math.abs(iy))/2];
	
					// compute the color and draw all shadowPixels with offset (ix, iy)
					if ( Math.sqrt(ix*ix+iy*iy)<this.size) {
						int gColor = gradient[(int)  Math.sqrt(ix*ix+iy*iy) ];
					
						for (int col=iLeft,row; col<fWidth+iLeft; col++) { 
							for (row=iTop;row<fHeight+iTop-1;row++){
								
								// draw if an opaque pixel is found and the destination is less opaque then the shadow
								if (localRgbBuffer[row*(fWidth + this.size*2) + col]>>>24==0xFF 
										&& localRgbBuffer[(row+this.yOffset+iy)*(fWidth + this.size*2) + col+this.xOffset+ix]>>>24 < gColor>>>24)
								{
									localRgbBuffer[(row+this.yOffset+iy)*(fWidth + this.size*2) + col+this.xOffset+ix]=gColor;
								}
							}
						}
					}
				}
			}
			
			/*DrawUtil.applyFilter(new byte[][]
			   //{{0,6,0},{0,10,0},{0,6,0}},90
			   //{{0,0,0},{6,10,6},{0,0,0}},100
			   //{{0,0,0},{6,10,6},{0,0,0}},150
			   {{1,1,1},{1,1,1},{1,1,1}},100
			   //{{1,1,1},{1,2,1},{1,1,1}},100
			   //{{1,1,1},{1,4,1},{1,1,1}},100
			   //{{1,1,1,1,1}},100
			   
			    //{{16,26,16},{26,41,26},{16,26,16}},100
			    //{{0,0,0},{0,1,0},{0,0,0}},100
			    //{{1}},100
			    //{{-10,6,4},{6,8,6},{4,19,4}},300
               //{{1},{5},{1}},100
			,localRgbBuffer,fWidth + this.size*2,fHeight + this.size*2);
			*/
		}
		
		
		// draw RGB-Data
		g.drawRGB(localRgbBuffer,invY*(fWidth + this.size*2)+invX,fWidth + this.size*2, ( startX-iLeft+invX<=0 ? 0 :startX-iLeft+invX), ( startY-iTop+invY<=0 ? 0 :startY-iTop+invY) , fWidth + this.size*2-invX, fHeight + this.size*2-invY, true);
		
		/*g.setColor( 0xFF000000 );
		g.drawString(text,startX,startY, Graphics.LEFT | Graphics.TOP);*/
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.text-drop-shadow-start-color
			Color sShadowColorObj = style.getColorProperty( "text-drop-shadow-start-color" );
			if (sShadowColorObj != null) {
				this.startColor = sShadowColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-drop-shadow-end-color
			Color eShadowColorObj = style.getColorProperty( "text-drop-shadow-end-color" );
			if (eShadowColorObj != null) {
				this.endColor = eShadowColorObj.getColor();
			}
		//#endif

		//#if polish.css.text-drop-shadow-size
			Integer sizeInt = style.getIntProperty( "text-drop-shadow-size" );
			if (sizeInt != null) {
				this.size = sizeInt.intValue();
			}
		//#endif
		//#if polish.css.text-drop-shadow-offsetx
			Integer oXInt = style.getIntProperty( "text-drop-shadow-offsetx" );
			if (oXInt != null) {
				this.xOffset = oXInt.intValue();
			}
		//#endif
		//#if polish.css.text-drop-shadow-offsety
			Integer oYInt = style.getIntProperty( "text-drop-shadow-offsety" );
			if (oYInt != null) {
				this.yOffset = oYInt.intValue();
			}
		//#endif
			
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
