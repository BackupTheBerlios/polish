//#condition polish.usePolishGui

/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif


import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * Paints a gradient font where the color changes from top to bottom.
 * 
 * @author robertvirkus
 *
 */
public class VerticalGradientTextEffect extends TextEffect {
	
	private int[] colors;
	private String oldText;
	//#if polish.api.nokia-ui && !polish.Bugs.TransparencyNotWorkingInNokiaUiApi
		//#define tmp.useNokiaUiApi
		private Image nokiaImageBuffer;
	//#elif polish.midp2
		//#if polish.GradientText.ClearColor:defined
			//#= private final static int CLEAR_COLOR = ${polish.GradientText.ClearColor};
		//#else
			private final static int CLEAR_COLOR = 0xFF000123;
		//#endif
		private int[] rgbBuffer;
	//#endif
	private boolean useTransparency;
	
	public VerticalGradientTextEffect() {
		// initialization is done upon request.
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) 
	{
		if (this.colors == null) {
			g.drawString( text, x, y, orientation );
			return;
		//#if tmp.useNokiaUiApi
			} else if ( this.nokiaImageBuffer == null || text != this.oldText  ){
		//#elif polish.midp2
			} else if ( this.rgbBuffer == null || text != this.oldText  ){
		//#else
			//# } else {
		//#endif
			// create image buffer
			Font font = g.getFont();
			int height = font.getHeight();
			int width = font.stringWidth( text );
			int startX = getLeftX( x, orientation, width );
			int startY = getTopY( y, orientation, height, font.getBaselinePosition() );
			//#if polish.api.nokia-ui || polish.midp2
				Graphics bufferG;
			//#else
				int clipX = g.getClipX();
				int clipY = g.getClipY();
				int clipWidth = g.getClipWidth();
				int clipHeight = g.getClipHeight();
			//#endif
			//#if tmp.useNokiaUiApi
				this.nokiaImageBuffer = DirectUtils.createImage( width, height, 0xFFFFFFFF );
				bufferG = this.nokiaImageBuffer.getGraphics();
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			//#elif polish.midp2
				Image midp2ImageBuffer = Image.createImage( width, height );
				bufferG = midp2ImageBuffer.getGraphics();
				bufferG.setColor( CLEAR_COLOR );
				bufferG.fillRect( 0, 0, width + 1, height + 1 );
				int[] clearColorArray = new int[1]; 
				midp2ImageBuffer.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
				int clearColor = clearColorArray[0];
			//#endif
			int j = 0;
			bufferG.setFont(font);
			boolean increase = true;
			int maxJ = this.colors.length - 1;
			for (int i = 0; i < height; i++) {
				int color = this.colors[j];
				//#if tmp.useNokiaUiApi || polish.midp2
					bufferG.setClip( 0, i, width, 1 );
					//#if tmp.useNokiaUiApi
						if (this.useTransparency) {
							dg.setARGBColor(color);
						} else {
							bufferG.setColor( color );
						}
					//#else
						bufferG.setColor( color );
					//#endif
					bufferG.drawString( text, 0, 0, Graphics.TOP | Graphics.LEFT );
				//#else
					g.setColor( color );
					g.setClip( startX, startY + i, width, 1 );
					g.drawString( text, startX, startY, Graphics.TOP | Graphics.LEFT );
				//#endif
				if (increase) {	
					j++;
					if (j >= maxJ) {
						increase = false;
					}
				} else {
					j--;
					if ( j <= 0 ) {
						increase = true;
					}
				}
			}
			//#if tmp.useNokiaUiApi
				//DirectGraphics dg = DirectUtils.getDirectGraphics( g );
				dg.drawImage( this.nokiaImageBuffer, startX, startY, Graphics.TOP | Graphics.LEFT, 0 );
			//#elif polish.midp2
				// clear RGB array:
				int[] localRgbBuffer = new int[ width * height ];
				midp2ImageBuffer.getRGB( localRgbBuffer, 0, width, 0, 0, width, height );
				//int number = 0;
				for (int i = 0; i < localRgbBuffer.length; i++) {
					int color = localRgbBuffer[i];
					if ( color == clearColor ) {
						//number++;
						localRgbBuffer[i] = 0x00000000; // full transparent
					}
				}
				//System.out.println("Set " + number + " pixels to transparent");
				this.rgbBuffer = localRgbBuffer;
				g.drawRGB( localRgbBuffer, 0, width, startX, startY, width, height, true );
			//#else
				g.setClip( clipX, clipY, clipWidth, clipHeight );
			//#endif
			this.oldText = text;	
		}
		//#if tmp.useNokiaUiApi || polish.midp2
			else {
				// text has been buffered:
				Font font = g.getFont();
				int height = font.getHeight();
				int width = font.stringWidth( text );
				int startX = getLeftX( x, orientation, width );
				int startY = getTopY( y, orientation, height, font.getBaselinePosition() );
				//#if tmp.useNokiaUiApi
					DirectGraphics dg = DirectUtils.getDirectGraphics( g );
					dg.drawImage( this.nokiaImageBuffer, startX, startY, Graphics.TOP | Graphics.LEFT, 0 );
				//#elif polish.midp2
					g.drawRGB( this.rgbBuffer, 0, width, startX, startY, width, height, true );
				//#endif

			}
		//#endif
	}

	public void setStyle(Style style) {
		super.setStyle(style);
		int startColor = 0xFFFFFFFF;
		//#if polish.css.text-vertical-gradient-start-color
			Integer startColorInt = style.getIntProperty("text-vertical-gradient-start-color");
			if (startColorInt != null) {
				startColor = startColorInt.intValue();
			}
		//#endif
		int endColor = 0xFF000000;
		//#if polish.css.text-vertical-gradient-end-color
			Integer endColorInt = style.getIntProperty("text-vertical-gradient-end-color");
			if (endColorInt != null) {
				endColor = endColorInt.intValue();
			}
		//#endif
		this.useTransparency = ((startColor & 0xFF000000) != 0) || ((endColor & 0xFF000000) != 0);
		int steps;
		//#if polish.css.text-vertical-gradient-end-color
			Integer stepsInt = style.getIntProperty("text-vertical-gradient-steps");
			if (stepsInt != null) {
				steps = stepsInt.intValue();
			} else {
				steps = Font.getDefaultFont().getHeight();
			}
		//#else
			steps = Font.getDefaultFont().getHeight();
		//#endif
		this.colors = DrawUtil.getGradient(startColor, endColor, steps);
		this.oldText = null;
	}
	
	

}
