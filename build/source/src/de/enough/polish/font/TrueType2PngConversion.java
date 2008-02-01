/*
 * Created on Feb 1, 2008 at 12:58:26 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.font;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Converts a true type font into a PNG image
 * @author vera
 */
public class TrueType2PngConversion
{
	protected Font trueTypeFont;
	protected Color fontColor;
	
	protected String stringToConvert;
	protected boolean isAntiAliased;
	protected int characterSpacing;
	
	private BufferedImage image;
	private Graphics2D imageGraphics;
	private int imageWidth;
	private int imageHeight;
	private int imageY;
	private int[] charWidthArray;
	
	/**
	 * Just for testing purposes
	 */
	protected TrueType2PngConversion()
	{
	}
	
	public TrueType2PngConversion(Font trueTypeFont, Color fontColor, String stringToConvert, boolean isAntiAliased, int characterSpacing)
	{
		this.trueTypeFont = trueTypeFont;
		this.fontColor = fontColor;
		this.stringToConvert = stringToConvert;
		this.isAntiAliased = isAntiAliased;
		this.characterSpacing = characterSpacing;
	}
	

	public BufferedImage createFontPng()
	{
		calculateImageSize();
		
		image = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		imageGraphics = image.createGraphics();
		Color transparent = new Color( 1, 1, 1, 0);
		imageGraphics.setBackground( transparent );
		imageGraphics.clearRect(0, 0, imageWidth, imageHeight );
		setImageRenderingHint();
		FontRenderContext fc = imageGraphics.getFontRenderContext();
		
		imageGraphics.setFont( this.trueTypeFont );
		imageGraphics.setColor( this.fontColor );
		
		drawChars2Image(fc);
		
		return image;
	}
	
	private void calculateImageSize()
	{
		image = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
		imageGraphics= image.createGraphics();
		setImageRenderingHint();
		FontRenderContext fc = imageGraphics.getFontRenderContext();
		
		TextLayout fulltl = new TextLayout(stringToConvert, this.trueTypeFont, fc);
		Rectangle2D bounds = fulltl.getBounds();
		imageHeight = (int) Math.ceil(bounds.getHeight());
		imageY = (int) Math.ceil(-bounds.getY());
		
		imageWidth = drawChars2Image(fc);
	}
	
	private int drawChars2Image(FontRenderContext fontRenderContext)
	{
		int x = 0;
		String onechar;
		
		charWidthArray = new int[stringToConvert.length()];
		
		for(int i = 0; i < stringToConvert.length(); i++)
		{
			onechar = stringToConvert.substring(i, i + 1);
			TextLayout tl = new TextLayout(onechar, this.trueTypeFont, fontRenderContext);
			Rectangle2D bounds = tl.getBounds();

			int minx = (int)Math.floor(bounds.getMinX());
			if(minx < 0)
				x -= (int)Math.floor(bounds.getMinX());

			bounds = this.trueTypeFont.getStringBounds(onechar, fontRenderContext);
			int charwidth = (int)Math.ceil(bounds.getMaxX());
			
			imageGraphics.drawString(onechar, x, imageY);
			x += charwidth + this.characterSpacing;
			charWidthArray[i] = charwidth + this.characterSpacing;
		}
		
		return x;
	}

	private void setImageRenderingHint()
	{
		if (isAntiAliased) {
			imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	public int[] getCharWidthArray()
	{
		return charWidthArray;
	}
}
