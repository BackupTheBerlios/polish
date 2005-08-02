//#condition polish.usePolishGui
/*
 * Created on 08-Apr-2005 at 11:17:51.
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
package de.enough.polish.ui.containerviews;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Shows only the currently selected item of an exclusive ChoiceGroup or an exclusive List.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        08-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExclusiveSingleLineView extends ContainerView {
	
	private int arrowColor;
	//#ifdef polish.css.exclusiveview-left-arrow
		private Image leftArrow;
	//#endif
	//#ifdef polish.css.exclusiveview-right-arrow
		private Image rightArrow;
	//#endif
	private int arrowWidth = 10;
	private int currentItemIndex;
	private ChoiceItem currentItem;
	private int leftArrowEndX;
	private int rightArrowStartX;

	/**
	 * Creates a new view
	 */
	public ExclusiveSingleLineView() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Container parent, int firstLineWidth,
			int lineWidth) 
	{
		//#debug
		System.out.println("Initalizing ExclusiveSingleLineView");
		int selectedItemIndex = ((ChoiceGroup) parent).getSelectedIndex();
		parent.focusedIndex = selectedItemIndex;
		//#if polish.css.exclusiveview-left-arrow || polish.css.exclusiveview-right-arrow
			int width = 0;
			//#ifdef polish.css.exclusiveview-left-arrow
				if (this.leftArrow != null) {
					width = this.leftArrow.getWidth();
				}
			//#endif
			//#ifdef polish.css.exclusiveview-right-arrow
				if (this.rightArrow != null) {
					if ( this.rightArrow.getWidth() > width) {
						width = this.rightArrow.getWidth();
					}
				}
			//#endif
			if (width > this.arrowWidth) {
				this.arrowWidth = width;
			}
		//#endif
		int completeArrowWidth;
		if (selectedItemIndex > 0 && selectedItemIndex < parent.size() -1 ) {
			completeArrowWidth = 2 * ( this.arrowWidth + this.paddingHorizontal );
		} else {
			completeArrowWidth = 1 * ( this.arrowWidth + this.paddingHorizontal );
		}
		lineWidth -= completeArrowWidth;
		ChoiceItem selectedItem = (ChoiceItem) parent.get( selectedItemIndex );
		selectedItem.drawBox = false;
		this.contentHeight = selectedItem.getItemHeight(lineWidth, lineWidth);
		this.contentWidth = selectedItem.getItemWidth( lineWidth, lineWidth ) + completeArrowWidth;
		this.appearanceMode = Item.INTERACTIVE;
		this.currentItem = selectedItem;
		this.currentItemIndex = selectedItemIndex;
	}
	
	

	protected void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.exclusiveview-left-arrow
			String leftArrowUrl = style.getProperty("exclusiveview-left-arrow");
			if (leftArrowUrl != null) {
				try {
					this.leftArrow = StyleSheet.getImage( leftArrowUrl, this, false );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load left arrow image [" + leftArrowUrl + "]" + e );
				}
			}
		//#endif
		//#ifdef polish.css.exclusiveview-right-arrow
			String rightArrowUrl = style.getProperty("exclusiveview-right-arrow");
			if (rightArrowUrl != null) {
				try {
					this.rightArrow = StyleSheet.getImage( rightArrowUrl, this, false );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load right arrow image [" + rightArrowUrl + "]" + e );
				}
			}
		//#endif
		//#ifdef polish.css.exclusiveview-arrow-color
			Integer colorInt = style.getIntProperty("exclusiveview-arrow-color");
			if ( colorInt != null ) {
				this.arrowColor = colorInt.intValue();
			}
		//#endif
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder,
			Graphics g) 
	{
		//#debug
		System.out.println("ExclusiveView.start: x=" + x + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
		/*
		Item[] items = this.parentContainer.getItems();
		ChoiceItem selectedItem = (ChoiceItem) items[0];
		int selectedItemIndex = 0;
		for (int i = 0; i < items.length; i++) {
			ChoiceItem item = (ChoiceItem) items[i];
			if (item.isSelected) {
				selectedItemIndex = i;
				selectedItem = item;
				break;
			}
		}
		*/
		g.setColor( this.arrowColor );
		if (this.currentItemIndex > 0) {
			// draw left arrow
			//#ifdef polish.css.exclusiveview-left-arrow
				if (this.leftArrow != null) {
					g.drawImage( this.leftArrow, x, y + this.contentHeight, Graphics.LEFT | Graphics.BOTTOM );
				} else {
			//#endif
			//#if polish.midp2
				g.fillTriangle( 
						x, y + this.contentHeight/2, 
						x + this.arrowWidth, y,
						x + this.arrowWidth, y + this.contentHeight );
			//#else
				int y1 = y + this.contentHeight / 2;
				int x2 = x + this.arrowWidth;
				int y3 = y + this.contentHeight;
				g.drawLine( x, y1, x2, y );
				g.drawLine( x, y1, x2, y3 );
				g.drawLine( x2, y, x2, y3 );
			//#endif
			//#ifdef polish.css.exclusiveview-left-arrow
				}
			//#endif
			x += this.arrowWidth + this.paddingHorizontal;
			leftBorder += this.arrowWidth + this.paddingHorizontal;
			this.leftArrowEndX = x;
		}
				
		if (this.currentItemIndex < this.parentContainer.size() - 1) {
			// draw right arrow
			//#ifdef polish.css.exclusiveview-right-arrow
				if (this.rightArrow != null) {
					g.drawImage( this.rightArrow, rightBorder, y + this.contentHeight, Graphics.RIGHT | Graphics.BOTTOM );
				} else {
			//#endif
			//#if polish.midp2
				g.fillTriangle( 
						rightBorder, y + this.contentHeight/2, 
						rightBorder - this.arrowWidth, y,
						rightBorder - this.arrowWidth, y + this.contentHeight );
			//#else
				int y1 = y + this.contentHeight / 2;
				int x2 = rightBorder - this.arrowWidth;
				int y3 = y + this.contentHeight;
				g.drawLine( rightBorder, y1, x2, y );
				g.drawLine( rightBorder, y1, x2, y3 );
				g.drawLine( x2, y, x2, y3 );
			//#endif
			//#ifdef polish.css.exclusiveview-right-arrow
				}
			//#endif
			rightBorder -= this.arrowWidth + this.paddingHorizontal;
			this.rightArrowStartX = rightBorder;
		}

		// draw item:
		//#debug
		System.out.println("ExclusiveView.item: x=" + x + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
		//selectedItem.drawBox = false;
		this.currentItem.paint(x, y, leftBorder, rightBorder, g);
		//this.currentItemIndex = selectedItemIndex;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem(int keyCode, int gameAction) {
		Item[] items = this.parentContainer.getItems();
		Item lastItem = this.currentItem;
		//ChoiceItem currentItem = (ChoiceItem) items[ this.currentItemIndex ];
		ChoiceGroup choiceGroup = (ChoiceGroup) this.parentContainer;
		if ( gameAction == Canvas.LEFT && this.currentItemIndex > 0 ) {
			this.currentItem.select( false );
			this.currentItemIndex--;
			this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
			this.currentItem.adjustProperties( lastItem );
			//this.currentItem.select( true );
			choiceGroup.setSelectedIndex( this.currentItemIndex, true );
			choiceGroup.notifyStateChanged();
			return this.currentItem;
		} else if ( gameAction == Canvas.RIGHT && this.currentItemIndex < items.length - 1 ) {
			this.currentItem.select( false );
			this.currentItemIndex++;
			this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
			this.currentItem.adjustProperties( lastItem );
			choiceGroup.setSelectedIndex( this.currentItemIndex, true );
			choiceGroup.notifyStateChanged();
			//this.currentItem.select( true );
			
			return this.currentItem;
		}
		// in all other cases there is no next item:
		return null;
	}

	//#ifdef polish.hasPointerEvents
	/**
	 * Handles pointer pressed events.
	 * This is an optional feature that doesn't need to be implemented by subclasses.
	 * 
	 * @param x the x position of the event
	 * @param y the y position of the event
	 * @return true when the event has been handled. When false is returned the parent container
	 *         will forward the event to the affected item.
	 */
	public boolean handlePointerPressed(int x, int y) {
		Item[] items = this.parentContainer.getItems();
		this.currentItem.select( false );
		if (this.currentItemIndex > 0 && x < this.leftArrowEndX ) {
			this.currentItemIndex--;
		} else if ( this.currentItemIndex < items.length - 1 && x > this.rightArrowStartX ) {
			this.currentItemIndex++;
		} else  {
			this.currentItemIndex++;
			if (this.currentItemIndex >= items.length ) {
				this.currentItemIndex = 0;
			}
		}  
		this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
		//this.currentItem.select( true );
		((ChoiceGroup) this.parentContainer).setSelectedIndex( this.currentItemIndex, true );
		this.parentContainer.focus(this.currentItemIndex, this.currentItem);
		return true;
	}
	//#endif

	// TODO allow to repeat scrolling at first and last item

}
