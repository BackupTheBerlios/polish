/*
 * Created on 09-Nov-2004 at 00:32:59.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Is used for a performant showing of String with a bitmap font.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BitMapFontViewer {

	private final Image image;
	private final short[] xPositions;
	private final byte[] widths;
	private final int completeWidth;
	private final int fontHeight;

	/**
	 * Views a specific input string with a specific bitmap font.
	 * 
	 * @param image the basic font-image
	 * @param xPositions array of the x-positions of the to-be-displayed characters
	 * @param yPositions array of the y-positions of the to-be-displayed characters
	 * @param widths array of the widths of the to-be-displayed characters
	 * @param completeWidth the width of the full string.
	 * @param fontHeight the height of the font
	 * 
	 */
	public BitMapFontViewer(Image image, short[] xPositions, byte[] widths, int completeWidth, int fontHeight) {
		this.image = image;
		this.xPositions = xPositions;
		this.widths = widths;
		this.completeWidth = completeWidth;
		this.fontHeight = fontHeight;
	}
	
	public void paint( Graphics g, int x, int y, int orientation ) {
		//TODO respect the orientation of the font
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		for (int i = 0; i < this.xPositions.length; i++ ) {
			int width = this.widths[i];
			if (width == 0) {
				continue;
			}
			g.clipRect( x, y, width, this.fontHeight );
			g.drawImage( this.image, x - this.xPositions[i], y, Graphics.TOP | Graphics.LEFT );
			x += width;
			// reset clip:
			g.setClip(clipX, clipY, clipWidth, clipHeight);
		}
		
	}
	
	public int getWidth() {
		return this.completeWidth;
	}

}
