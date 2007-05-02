//#condition polish.usePolishGui

/*
 * Created on 22-Aug-2005 at 16:50:12.
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
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Border;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a translucent shadow on MIDP 2.0 and Nokia-UI-API devices.</p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        22-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DropShadowBorder extends Border {
	
	public final static int BOTTOM_RIGHT = 0;
	public final static int TOP_RIGHT = 1;
	public final static int BOTTOM_LEFT = 2;
	public final static int TOP_LEFT = 3;
	
	private final int[] shadowColors;
	private final int orientation;
	private final int offset;
	private int innerColor;
	private int outerColor;

	public DropShadowBorder( int innerColor, int outerColor, int width, int offset, int orientation ) {
		super();
		this.borderWidth = width;
		this.offset = offset;
		this.orientation = orientation;
		this.innerColor = innerColor;
		this.outerColor = outerColor;
		this.shadowColors = DrawUtil.getGradient(innerColor, outerColor, width);
	}

	public void paint(int x, int y, int width, int height, Graphics g) {
		int left = x;
		int top = y;
		int right = x + width;
		int bottom = y + height;
		for (int i = 0; i < this.borderWidth; i++ ) {
			int color = this.shadowColors[i];
			
			switch (this.orientation) {
			case BOTTOM_RIGHT:
				// right:
				DrawUtil.drawLine(color, right + i, top + i, right + i, bottom + i, g);
				// bottom:
				DrawUtil.drawLine(color, left + i, bottom + i, right + i, bottom + i, g);
				break;
			case TOP_RIGHT:
				// right:
				DrawUtil.drawLine(color, right + i, top - i, right + i, bottom - i, g);
				// top:
				DrawUtil.drawLine(color, left + i, top - i, right + i, top - i, g);
				break;
			case BOTTOM_LEFT:
				// left:
				DrawUtil.drawLine(color, left - i, top + i, left - i, bottom + i, g);
				// bottom:
				DrawUtil.drawLine(color, left - i, bottom + i, left - i, bottom + i, g);
				break;
			case TOP_LEFT:
				// left:
				DrawUtil.drawLine(color, left - i, top - i, left - i, bottom - i, g);
				// top:
				DrawUtil.drawLine(color, left - i, top - i, right - i, top - i, g);
				break;

			}
		}	
	}

}
