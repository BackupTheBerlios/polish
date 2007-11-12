//#condition polish.usePolishGui

/*
 * Created on 09.06.2006 at 15:41:12.
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

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;
/**
 * DoubleGradientVerticalBackground generates an nice Backgroundscreen,
 * with two gradients, each taking half of the available height.
 * 
 * @author Andre Schmidt
 */
public class MaskedGradientVerticalBackground  extends Background {
	private int firstTopColor = -1;
	private int firstBottomColor = -1;
	
	private int secondTopColor = -1;
	private int secondBottomColor = -1;
	
	private final int start;
	private final int end;
	private final boolean isPercent;
	
	private final boolean singleGradient; 
	
	private final int anchor;
	private final boolean doCenter;
	private final int xOffset;
	private final int yOffset;

	private int[] firstGradient;
	private int[] secondGradient;
	private int startLine;
	private int endLine;
	
	private Image mask;
	private int maskColor;
	private Image finalImage;
	
	private int data[];

	/**
	 * Creates a new double gradient background
	 * 
	 * @param firstTopColor the color at the top of the first gradient
	 * @param firstBottomColor the color at the bottom of the first gradient
	 * @param secondTopColor the color at the top of the second gradient
	 * @param secondBottomColor the color at the bottom of the second gradient
	 * @param stroke the line stroke style
	 */
	public MaskedGradientVerticalBackground(String maskUrl, int maskColor, boolean singleGradient,  int firstTopColor, int firstBottomColor, int secondTopColor, int secondBottomColor, int xOffset, int yOffset, int anchor) {
		this( maskUrl, maskColor, singleGradient, firstTopColor, firstBottomColor, secondTopColor, secondBottomColor, xOffset, yOffset, anchor, 0, 0, false );
		
	}
	
	/**
	 * Creates a new double gradient background
	 * 
	 * @param firstTopColor the color at the top of the first gradient
	 * @param firstBottomColor the color at the bottom of the first gradient
	 * @param secondTopColor the color at the top of the second gradient
	 * @param secondBottomColor the color at the bottom of the second gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param isPercent true when the start and end settings should be counted in percent
	 */
	public MaskedGradientVerticalBackground(String maskUrl, int maskColor, boolean singleGradient, int firstTopColor, int firstBottomColor, int secondTopColor, int secondBottomColor, int xOffset, int yOffset, int anchor, int start, int end, boolean isPercent ) {
		
		try
		{
			this.mask = Image.createImage(maskUrl);
		}
		catch(IOException e)
		{
			System.out.println("unable to load mask " + e);
		}
		
		this.maskColor = ImageUtil.getDeviceColor(maskColor);
		
		this.firstTopColor = firstTopColor;
		this.firstBottomColor = firstBottomColor;
		this.secondTopColor = secondTopColor;
		this.secondBottomColor = secondBottomColor;
		
		this.start = start;
		this.end = end;
		this.isPercent = isPercent;
		
		this.singleGradient = singleGradient;
		
		this.doCenter = ( anchor == (Graphics.VCENTER | Graphics.HCENTER) );
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	/*
	 * Paints the screen 
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (this.finalImage == null) {
			int delta = this.mask.getHeight() % 2;
						
			if(!this.singleGradient)
			{
				this.firstGradient = getGradient(this.firstTopColor, this.firstBottomColor, this.mask.getHeight() / 2);
				this.secondGradient = getGradient(this.secondTopColor, this.secondBottomColor, (this.mask.getHeight() / 2) + delta);
			}
			else
			{
				this.firstGradient = getGradient(this.firstTopColor, this.firstBottomColor, this.mask.getHeight());
			}
			
			getMaskData();
			
			if(this.singleGradient)
			{
				setData(this.firstGradient, 0, this.mask.getWidth(), this.mask.getHeight());
			}
			else
			{
				setData(this.firstGradient, 0, this.mask.getWidth(), this.mask.getHeight() / 2);
				setData(this.secondGradient,  this.mask.getHeight() / 2, this.mask.getWidth(), (this.mask.getHeight() / 2) + delta);
			}
			
			this.finalImage = Image.createRGBImage(this.data, this.mask.getWidth(), this.mask.getHeight(), true);
		}
		
		x += this.xOffset;
		y += this.yOffset;
		if (this.finalImage != null) {
			if (this.doCenter) {
				int centerX = x + (width / 2);
				int centerY = y + (height / 2);
				g.drawImage(this.finalImage, centerX, centerY, Graphics.HCENTER | Graphics.VCENTER );
			} else {
				if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
					x += (width / 2);
				} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
					x += width;
				}
				if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
					y += (height / 2);
				} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
					y += height;
				}
				//System.out.println("Drawing image at " + x + ", " + y);
				g.drawImage(this.finalImage, x, y, this.anchor );
			}
		}
	}
	
	public void getMaskData()
	{
		this.data = new int[this.mask.getWidth() * this.mask.getHeight()];
		this.mask.getRGB(this.data, 0, this.mask.getWidth(), 0, 0, this.mask.getWidth(), this.mask.getHeight());
		
	}
	
	public void setData(int[] gradient, int offset, int width, int height)
	{
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if(this.data[(w) + (h + offset) * width] == this.maskColor)
				{
					this.data[(w) + (h + offset) * width] = gradient[h];
				}
			}
		}
	}
	
	public int[] getGradient(int topColor, int bottomColor, int height)
	{
		int steps = height;
		if (this.start != this.end) {
			steps = this.end - this.start;
			if (this.isPercent) {
				this.startLine = (this.start * height) / 100;
				this.endLine = (this.end * height) / 100;
				steps = this.endLine - this.startLine;
			} else {
				this.startLine = this.start;
				this.endLine = this.end;
			}				
		} else {
			this.endLine = height;
		}
		
		return DrawUtil.getGradient( topColor, bottomColor, steps );
	}
}
