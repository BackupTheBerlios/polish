//#condition polish.usePolishGui
/*
 * Created on 13-Nov-2004 at 20:52:55.
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


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * <p>Shows the items in a normal list. During the beginning an animation is shown, in which the items fall into their place.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        13-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DroppingView extends ContainerView {
	
	private final static int START_MAXIMUM = 30;
	private final static int MAX_PERIODE = 5;
	private final static int DEFAULT_DAMPING = 10;
	private final static int SPEED = 10;
	private boolean isDownwardsAnimation;
	private int damping = DEFAULT_DAMPING;
	private int currentPeriode;
	private int maxPeriode = MAX_PERIODE;
	private int currentMaximum;
	private int startMaximum = START_MAXIMUM;
	private int speed = SPEED;
	private boolean animationInitialised;
	
	

	private boolean isAnimationRunning;
	private int[] yAdjustments;
	
	//#ifdef polish.css.droppingview-repeat-animation
		private boolean repeatAnimation;
	//#endif
	/**
	 * Creates new DroppingView
	 */
	public DroppingView() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Container parent, int firstLineWidth,
			int lineWidth) 
	{
		super.initContent(parent, firstLineWidth, lineWidth);
				
		if (!this.animationInitialised) {
			Item[] myItems = parent.getItems();
			this.yAdjustments = new int[ myItems.length ];
			initAnimation(myItems, this.yAdjustments);
		}
	}

	
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int adjustedY = y - this.yAdjustments[ index ];
		item.paint(x, adjustedY, leftBorder, rightBorder, g);
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
//			// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
//			// since after every item a line break will be done.
//			int adjustedY = y - this.yAdjustments[i];
//			item.paint(x, adjustedY, leftBorder, rightBorder, g);
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
		//#ifdef polish.css.droppingview-repeat-animation
			Boolean repeat = style.getBooleanProperty("droppingview-repeat-animation");
			if (repeat != null) {
				this.repeatAnimation = repeat.booleanValue();
			} else {
				this.repeatAnimation = false;
			}
		//#endif
		//#ifdef polish.css.droppingview-damping
			Integer dampingInt = style.getIntProperty("droppingview-damping");
			if (dampingInt != null) {
				this.damping = dampingInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-maximum
			Integer maxInt = style.getIntProperty("droppingview-maximum");
			if (maxInt != null) {
				this.startMaximum = maxInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-speed
			Integer speedInt = style.getIntProperty("droppingview-speed");
			if (speedInt != null) {
				this.speed = speedInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-maxperiode
			Integer periodeInt = style.getIntProperty("droppingview-maxperiode");
			if (periodeInt != null) {
				this.maxPeriode = periodeInt.intValue();
			}
		//#endif
	}
	
	//#ifdef polish.css.droppingview-repeat-animation
	public void showNotify() {
		super.showNotify();
		if (this.repeatAnimation && this.yAdjustments != null) {
			initAnimation( this.parentContainer.getItems(), this.yAdjustments );
		}
	}
	//#endif
	
	/**
	 * Initialises the animation.
	 *  
	 * @param items the items.
	 * @param yValues the y-adjustment-values
	 */
	private void initAnimation(Item[] items, int[] yValues) {
		this.isDownwardsAnimation = true;
		this.currentMaximum = this.startMaximum * -1;
		this.currentPeriode = 0;
		for (int i = 0; i < yValues.length; i++ ) {
			yValues[i] = this.contentHeight; 
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
		boolean animated = super.animate();
		if (this.isAnimationRunning) {
			boolean startNextPeriode = true;
			int max = this.currentMaximum;
			int column = 0;
			if (this.isDownwardsAnimation) {
				for (int i = 0; i < this.yAdjustments.length; i++ ) {
					int y = this.yAdjustments[i] ;
					if (y > max) {
						y -= this.speed;
						if (y < max) {
							y = max;
						}
						startNextPeriode = false;
					}
					this.yAdjustments[i] = y;
					column++;
					if (column >= this.numberOfColumns ) {
						max += this.damping;
						if (max > 0) {
							max = 0;
						}
						column = 0;
					}
				}
			} else {
				for (int i = 0; i < this.yAdjustments.length; i++ ) {
					int y = this.yAdjustments[i];
					if (y < max) {
						y += this.speed;
						if (y > max) {
							y = max;
						} 
						startNextPeriode = false;
					}
					this.yAdjustments[i] = y;
					column++;
					if (column >= this.numberOfColumns ) {
						max -= this.damping;
						if (max < 0) {
							max = 0;
						}
						column = 0;
					}
				}
			}
			if (startNextPeriode) {
				this.currentPeriode ++;
				if ((this.currentPeriode < this.maxPeriode) && (this.currentMaximum != 0)) {
					this.currentMaximum = (this.currentMaximum * -2) / 3;
					this.isDownwardsAnimation = !this.isDownwardsAnimation;
				} else {
					this.isAnimationRunning = false;
				}
			}
			return true;
		} else {
			return animated;
		}
	}

}
