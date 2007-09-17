//#condition polish.usePolishGui
/*
 * Created on Aug 21, 2006 at 12:48:38 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * <p>Slides the parent container upwards, an animation used for menus for example.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Aug 21, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SlideUpView extends ContainerView {

	private boolean isAnimationFinished;
	private int yOffset;
	private int minSpeed = 2;
	private int maxSpeed = -1;

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (!this.isAnimationFinished ) {
			int y = this.yOffset;
			int speed = Math.max( this.minSpeed, y / 3 );
			if (this.maxSpeed != -1 && speed > this.maxSpeed) {
				speed = this.maxSpeed;
			}
			//System.out.println("animate: y=" + y + ", speed=" + speed);
			y -= speed;
			if (y <= 0) {
				y = 0;
				this.isAnimationFinished = true;
			}
			this.yOffset = y;
			animated = true;
		}
		return animated;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#showNotify()
	 */
	public void showNotify() {
		super.showNotify();
		if (this.contentHeight != 0) {
			this.yOffset = this.contentHeight;			
//			System.out.println("slide up: showNotify: " + this.yOffset);
			this.restartAnimation = false;
			this.isAnimationFinished = false;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int lineWidth) {
		super.initContent(parentContainerItem, firstLineWidth, lineWidth);
		// not sufficient to check this only in the showNotify method,
		// since the container dimensions might not yet be known:
		if (this.restartAnimation) {
//			System.out.println("initContent: " + this.contentHeight);
			this.restartAnimation = false;
			this.isAnimationFinished = false;
			this.yOffset = this.contentHeight;			
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		y += this.yOffset;		
		super.paintContent(parent, x, y, leftBorder, rightBorder, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintBackground(de.enough.polish.ui.Background, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintBackground(Background background, int x, int y, int width, int height, Graphics g) {
		super.paintBackground(background, x, y + this.yOffset, width, height, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintBorder(de.enough.polish.ui.Border, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintBorder(Border border, int x, int y, int width, int height, Graphics g) {
		super.paintBorder(border, x, y + this.yOffset, width, height, g);
	}
	

}
