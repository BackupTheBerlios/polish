//#condition polish.usePolishGui

/*
 * Created on 22-Aug-2005 at 12:53:15.
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

import javax.microedition.lcdui.Graphics;

//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif

import de.enough.polish.ui.Background;

/**
 * <p>Fades in the background from fully transparent to the target color, which might also be translucent.</p>
 * <p>This background is only available when the target device supports MIDP 2.0</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FadeInBackground extends Background {

	private final int targetArgbColor;
	private int currentColor;
	private long lastUpdateTime;
	private int lastX;
	private int lastY;
	private boolean animationRunning = true;
	//#ifdef polish.api.nokia-ui
		private final int[] xCoords;
		private final int[] yCoords;
	//#elifdef polish.midp2
		// int MIDP/2.0 the buffer is always used:
		private int[] buffer;
		private int lastWidth;
		//#if polish.Bugs.drawRgbNeedsFullBuffer
			private int lastHeight;
		//#endif
	//#endif
	private final boolean restartOnPositionChange;
	private final boolean restartOnTime;
	
	public FadeInBackground( int targetArgbColor, boolean restartOnTime, boolean restartOnPositionChange ) {
		super();
		this.targetArgbColor = targetArgbColor;
		this.restartOnTime = restartOnTime;
		this.restartOnPositionChange = restartOnPositionChange;
		this.currentColor = 0x00FFFFFF & targetArgbColor;
		//#if polish.api.nokia-ui
			this.xCoords = new int[4];
			this.xCoords[0] = Integer.MIN_VALUE;
			this.yCoords = new int[4];
			this.yCoords[0] = Integer.MIN_VALUE;
		//#endif
	}

	public void paint(int x, int y, int width, int height, Graphics g) {
		System.out.println("FadeInBackround: currentColor=" + Integer.toHexString(this.currentColor));
		if ( this.restartOnTime ) {
			long updateTime = System.currentTimeMillis();
			if (!this.animationRunning) {
				boolean restart = (updateTime - this.lastUpdateTime) > 5 * 1000; // restart after 5 seconds
				this.lastUpdateTime = updateTime;
				if (restart) {
					this.currentColor = 0x00FFFFFF & this.targetArgbColor;
					this.animationRunning = true;
					return;
				}
			}
			this.lastUpdateTime = updateTime;
		}
		//#ifdef polish.api.nokia-ui
			if ( this.lastX != x) {
				this.lastX = x;
				this.xCoords[0] = x;
				this.xCoords[1] = x + width;
				this.xCoords[2] = x + width;
				this.xCoords[3] = x;
				if (this.restartOnPositionChange) {
					this.animationRunning = true;
					this.currentColor = 0x00FFFFFF & this.targetArgbColor;
					return;
				}
			}
			if ( this.lastY != y) {
				this.lastY = y;
				this.yCoords[0] = y;
				this.yCoords[1] = y;
				this.yCoords[2] = y + height;
				this.yCoords[3] = y + height;
				if (this.restartOnPositionChange) {
					this.animationRunning = true;
					this.currentColor = 0x00FFFFFF & this.targetArgbColor;
					return;
				}
			}
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);
			dg.fillPolygon( this.xCoords, 0, this.yCoords, 0, 4, this.currentColor );
		//#elifdef polish.midp2
			// on the SE K700 for example the translated origin of the graphics 
			// does not seem to used. Instead the real origin is used:
			//#ifdef polish.Bugs.drawRgbOrigin
				x += g.getTranslateX();
				y += g.getTranslateY();
			//#endif
				
			// check if the buffer needs to be re-created:
			
			//#if polish.Bugs.drawRgbNeedsFullBuffer
				if (width != this.lastWidth || height != this.lastHeight) {
					this.lastWidth = width;
					this.lastHeight = height;
					int[] newBuffer = new int[ width * height ];
					for (int i = newBuffer.length; --i >= 0; ) {
						newBuffer[i] = this.currentColor;
					}
					this.buffer = newBuffer;
					if (this.restartOnPositionChange) {
						this.animationRunning = true;
						this.currentColor = 0x00FFFFFF & this.targetArgbColor;
						return;
					}
				}
			//#else
				if (width != this.lastWidth) {
					this.lastWidth = width;
					int[] newBuffer = new int[ width ];
					for (int i = width; --i >= 0;) {
						newBuffer[i] = this.currentColor;
					}
					this.buffer = newBuffer;
					if (this.restartOnPositionChange) {
						this.animationRunning = true;
						this.currentColor = 0x00FFFFFF & this.targetArgbColor;
						return;
					}
				}
			//#endif
			if (x != this.lastX || y != this.lastY ) {
				this.lastX = x;
				this.lastY = y;
				if (this.restartOnPositionChange) {
					this.animationRunning = true;
					this.currentColor = 0x00FFFFFF & this.targetArgbColor;
					return;
				}
			}
			if (x < 0) {
				width += x;
				if (width < 0) {
					return;
				}
				x = 0;
			}
			if (y < 0) {
				height += y;
				if (height < 0) {
					return;
				}
				y = 0;
			}
			//#if polish.Bugs.drawRgbNeedsFullBuffer
				g.drawRGB(this.buffer, 0, width, x, y, width, height, true);
			//#else
				g.drawRGB(this.buffer, 0, 0, x, y, width, height, true);
			//#endif
		//#else
			// ignore alpha-value
			g.setColor( this.currentColor );
			g.fillRect(x, y, width, height);
		//#endif
		this.lastX = x;
		this.lastY = y;
	}
	
	public boolean animate() {
		if (this.restartOnTime) {
			this.lastUpdateTime = System.currentTimeMillis();
		} 
		//#if !polish.api.nokia-ui && polish.midp2
		if (this.buffer == null) {
			return false;
		}
		//#endif
		
		// okay, increase the alpha value, until the translucency is as high as the target translucency:
		int currentTranslucency = this.currentColor >>> 24;
		int targetTranslucency = this.targetArgbColor >>> 24;
		
		currentTranslucency += 20;
		if (currentTranslucency > targetTranslucency) {
			currentTranslucency = targetTranslucency;
			this.animationRunning = false;
		}
		this.currentColor = (0x00FFFFFF & this.targetArgbColor) | ( currentTranslucency << 24 );
		//#if ! polish.api.nokia-ui && polish.midp2
			//#if polish.Bugs.drawRgbNeedsFullBuffer
				for (int i = this.buffer.length; --i >= 0 ;) {
					this.buffer[i] = this.currentColor;
				}
			//#else
				for (int i = this.buffer.length; --i >= 0 ; ) {
					this.buffer[i] = this.currentColor;
				}
			//#endif
		//#endif
				
				//System.out.println("FadeInBackground.animate()=" + this.animationRunning);

		return this.animationRunning;
	}

}
 