//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.util.ImageUtil;

/**
 * Creates a patch-based background
 * @author Ovidiu
 *
 */
public class PatchBackground extends ImageResourceBackground {
	
	public final static int STYLE_FULL = 0x00;
	public final static int STYLE_TOP = 0x01;
	public final static int STYLE_CENTER = 0x02;
	public final static int STYLE_BOTTOM = 0x03;
	
	private final int colorX;
	private final int colorY;
	private final int leftWidth;
	private final int topHeight;
	private final int rightWidth;
	private final int bottomHeight;
	private final int marginLeft;
	private final int marginTop;
	private final int marginRight;
	private final int marginBottom;
	private final int style;
	
	private int color;
	
	/**
	 * 
	 * @param imageUrl
	 * @param colorX
	 * @param colorY
	 * @param leftWidth
	 * @param topHeight
	 * @param rightWidth
	 * @param bottomHeight
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 * @param style
	 */
	public PatchBackground( String imageUrl, int colorX, int colorY, int leftWidth, int topHeight, int rightWidth, int bottomHeight, int marginLeft, int marginTop, int marginRight, int marginBottom, int style) {
		super(imageUrl);
		
		this.colorX = colorX;
		this.colorY = colorY;
		
		this.leftWidth = leftWidth;
		this.topHeight = topHeight;
		this.rightWidth = rightWidth;
		this.bottomHeight = bottomHeight;
		
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
		
		this.style = style;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isLoaded) {
			load();
			if (this.colorX >= 0 && this.colorY >= 0) {
				this.color = ImageUtil.getPixelColor(this.image, this.colorX, this.colorY);
			} else {
				this.color = Item.TRANSPARENT;
			}
		}
		x -= this.marginLeft;
		y -= this.marginTop; 
		width = width + this.marginLeft + this.marginRight;
		height = height + this.marginTop + this.marginBottom;
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		paintHorizontalFill(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		paintVerticalFill(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		if (this.color != Item.TRANSPARENT) {
			paintColorFill(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		}
		paintCorners(x, y, width,height,clipX, clipY, clipWidth, clipHeight, g);
	}
	
	private void paintCorners(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int dstX;
		int dstY;
		
		int srcX;
		int srcY;
		
		if(this.style == STYLE_FULL || this.style == STYLE_TOP) {
			// draw top left
			srcX = 0;
			srcY = 0;
			dstX = x + srcX;
			dstY = y + srcY;
			drawRegion(this.image, srcX, srcY, this.leftWidth, this.topHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		if(this.style == STYLE_FULL || this.style == STYLE_TOP) {
			// draw top right
			srcX = this.imageWidth - this.rightWidth;
			srcY = 0;
			dstX = x + (width - this.rightWidth);
			dstY = y;
			drawRegion(this.image, srcX, srcY, this.rightWidth, this.topHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		if(this.style == STYLE_FULL || this.style == STYLE_BOTTOM) {
			// draw bottom left
			srcX = 0;
			srcY = this.imageHeight - this.bottomHeight;
			dstX = x;
			dstY = y + (height - this.bottomHeight);
			drawRegion(this.image, srcX, srcY, this.leftWidth, this.bottomHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}

		if(this.style == STYLE_FULL || this.style == STYLE_BOTTOM) {
			// draw bottom right
			srcX = this.imageWidth - this.rightWidth;
			srcY = this.imageHeight - this.bottomHeight;
			dstX = x + (width - this.rightWidth);
			dstY = y + (height - this.bottomHeight);
			drawRegion(this.image, srcX, srcY, this.rightWidth, this.bottomHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
	}
	
	private void paintHorizontalFill(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int srcX = this.leftWidth;
		int srcY;
		
		int srcWidth = this.imageWidth - (this.leftWidth + this.rightWidth);
		int srcHeight = this.topHeight;
		
		int dstX = x + this.leftWidth;
		int dstY;
		
		int fillWidth = width - (this.leftWidth + this.rightWidth);
		
		storeClipping(g);
		g.clipRect(dstX, y, fillWidth, height);
		
		if(this.style == STYLE_FULL || this.style == STYLE_TOP) {
			// draw horizontal top fill
			srcY = 0;
			dstY = y;
			
			for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
				drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX + xOffset, dstY, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		if(this.style == STYLE_FULL || this.style == STYLE_BOTTOM) {
			// draw horizontal bottom fill
			srcY = this.imageHeight - this.bottomHeight;
			dstY = y + (height - this.bottomHeight); 
			
			for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
				drawRegion(this.image, srcX, srcY, srcWidth, srcHeight,dstX + xOffset, dstY, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		restoreClipping(g);
	}
	
	/**
	 * Paint the vertical fill. The tile to paint the vertical fills
	 * is calculated with the 
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param g the Graphics instance
	 */
	private void paintVerticalFill(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		// draw vertical left fill
		int srcX = 0;
		int srcY = this.topHeight;
		
		int srcWidth = this.leftWidth;
		int srcHeight = this.imageHeight - (this.topHeight + this.bottomHeight);
		
		int dstX = x;
		int dstY = y;
		
		if(this.style == STYLE_TOP || this.style == STYLE_FULL) {
			dstY += this.topHeight;
		}
		
		int fillHeight = height;
		
		if(this.style == STYLE_BOTTOM || this.style == STYLE_FULL) {
			fillHeight -= this.bottomHeight;
		} 
		
		if(this.style == STYLE_TOP || this.style == STYLE_FULL) {
			fillHeight -= this.topHeight;
		} 
		
		storeClipping(g);
		g.clipRect(x, dstY, width, fillHeight);
		
		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		// draw vertical right fill
		srcX = this.imageWidth - this.rightWidth;
		dstX = x + (width - this.rightWidth);
		
		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		restoreClipping(g);
	}
	
	/**
	 * Fill the inner area of a patch with the specified color
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param g the Graphics instance
	 */
	private void paintColorFill(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int dstX = x + this.leftWidth;
		int dstY = y;
		
		if(this.style == STYLE_TOP || this.style == STYLE_FULL) {
			dstY += this.topHeight;
		}
		
		int fillWidth = width - (this.leftWidth + this.rightWidth);
		int fillHeight = height;
		
		if(this.style == STYLE_BOTTOM || this.style == STYLE_FULL) {
			fillHeight -= this.bottomHeight;
		} 
		
		if(this.style == STYLE_TOP || this.style == STYLE_FULL) {
			fillHeight -= this.topHeight;
		} 
		
		g.setColor(this.color);
		g.fillRect(dstX, dstY, fillWidth, fillHeight);
	}
}
