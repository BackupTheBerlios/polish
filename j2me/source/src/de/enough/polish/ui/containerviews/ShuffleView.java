//#condition polish.usePolishGui
/*
 * Created on 18-Nov-2004 at 20:52:55.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * <p>Shows the items in a normal list. During the beginning an animation is shown, in which the items are moved horizontally from left and right into their position.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        18-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ShuffleView extends ContainerView {
	
	private final static int SPEED = 10;
	private int speed = SPEED;
	private boolean animationInitialised;
	private boolean isAnimationRunning;
	private int[] xAdjustments;
	
	//#ifdef polish.css.shuffleview-repeat-animation
		private boolean repeatAnimation;
	//#endif
	/**
	 * Creates new DroppingView
	 */
	public ShuffleView() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth,
			int lineWidth) 
	{
		super.initContent(parentItm, firstLineWidth, lineWidth);
		
		Container parent = (Container) parentItm;
		if (!this.animationInitialised) {
			Item[] myItems = parent.getItems();
			this.xAdjustments = new int[ myItems.length ];
			initAnimation(myItems, this.xAdjustments);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int xAdjustment = this.xAdjustments[index];
		item.paint(x - xAdjustment, y, leftBorder - xAdjustment, rightBorder - xAdjustment, g);
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
//	 */
//	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
//			Graphics g) 
//	{
//		Item[] myItems = this.parentContainer.getItems();
//		for (int i = 0; i < myItems.length; i++) {
//			Item item = myItems[i];
//			int xAdjustment = this.xAdjustments[i];
//			item.paint(x - xAdjustment, y, leftBorder - xAdjustment, rightBorder - xAdjustment, g);
//			y += item.itemHeight + this.paddingVertical;
//		}
//	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
//	 */
//	protected Item getNextItem(int keyCode, int gameAction) {
//		if (gameAction == Canvas.DOWN) {
//			return getNextFocusableItem( this.parentContainer.getItems(), true, 1, true);
//		} else if (gameAction == Canvas.UP) {
//			return getNextFocusableItem( this.parentContainer.getItems(), false, 1, true);
//		} else {
//			return null;
//		}
//	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.shuffleview-repeat-animation
			Boolean repeat = style.getBooleanProperty("shuffleview-repeat-animation");
			if (repeat != null) {
				this.repeatAnimation = repeat.booleanValue();
			} else {
				this.repeatAnimation = false;
			}
		//#endif
		//#ifdef polish.css.shuffleview-speed
			Integer speedInt = style.getIntProperty("shuffleview-speed");
			if (speedInt != null) {
				this.speed = speedInt.intValue();
			}
		//#endif
	}
	
	//#ifdef polish.css.shuffleview-repeat-animation
	public void showNotify() {
		super.showNotify();
		if (this.repeatAnimation && this.xAdjustments != null) {
			initAnimation( this.parentContainer.getItems(), this.xAdjustments );
		}
	}	
	//#endif
	
	/**
	 * Initialises the animation.
	 *  
	 * @param items the items.
	 * @param xValues the x-adjustment-values
	 */
	private void initAnimation(Item[] items, int[] xValues) {
		int factor = 1;
		int column = 0;
		for (int i = 0; i < xValues.length; i++ ) {
			xValues[i] = this.contentWidth * factor;
			column++;
			if (column >= this.numberOfColumns) {
				factor *= -1;
				column = 0;
			}
		}
		
		this.isAnimationRunning = true;
		this.animationInitialised = true;
	}


	/**
	 * Animates this view - the items appear to drop from above.
	 * 
	 * @return true when the view was really animated.
	 */
	public boolean animate() {
		if (this.isAnimationRunning) {
			int counter = 0;
			for (int i = 0; i < this.xAdjustments.length; i++ ) {
				int x = this.xAdjustments[i];
				if ( x < 0 ) {
					x += this.speed;
					if ( x > 0 ) {
						x = 0;
					}
				} else if ( x > 0 ) {
					x -= this.speed;
					if ( x < 0 ) {
						x = 0;
					}
				} else {
					counter++;
				}
				this.xAdjustments[i] = x;
			}
			this.isAnimationRunning = ( counter < this.xAdjustments.length );
			return true;
		} else {
			return false;
		}
	}
}
