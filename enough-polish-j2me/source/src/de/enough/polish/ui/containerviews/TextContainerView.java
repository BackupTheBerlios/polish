//#condition polish.usePolishGui
/*
 * Created on May 24, 2011 at 10:21:00 PM.
 * 
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.WrappedText;

/**
 * <p>Arranges StringItems in overlapping boxes, so that one StringItem starts in the same line where the previous StringItem stops.</p>
 * <p>Usage:</p>
 * <pre>
 * .myScreen {
 * 		view-type: text;
 * }
 * </pre>
 * <p>Copyright Enough Software 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextContainerView extends ContainerView {

	private int[] textXOffsets;
	private Background focusedBackground;
	private Border focusedBorder;

	/**
	 * Creates a new 'text' container view.
	 */
	public TextContainerView() {
		// nothing to initialize
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) 
	{
		Container parent = (Container) parentContainerItem;
		this.focusedIndex = parent.getFocusedIndex();
		Item focItem = parent.getFocusedItem();
		if (focItem != this.focusedItem) {
			this.focusedItem = focItem;
			if (focItem != null) {
				Background bg = removeItemBackground(focItem);
				if (bg != null) {
					this.focusedBackground = bg;
				}
				Border border = removeItemBorder(focItem);
				if (border != null) {
					this.focusedBorder = border;
				}
			}
		}
		//#if polish.Container.allowCycling != false
			this.allowCycling = parent.allowCycling;
			Item ancestor = parent.getParent();
			while (this.allowCycling && ancestor != null) {
				if ( (ancestor instanceof Container)  && ((Container)ancestor).getNumberOfInteractiveItems()>1 ) {
					this.allowCycling = false;
					break;
				}
				ancestor = ancestor.getParent();
			}
		//#endif
		//#if polish.css.view-type-left-x-offset
			availWidth -= this.leftXOffset;
		//#endif
		//#if polish.css.view-type-right-x-offset
			availWidth -= this.rightXOffset;
		//#endif

		
		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		int myItemsLastIndex = myItems.length - 1;
		int currentX = 0;
		int currentY = 0;
		int maxCurrentX = availWidth - (availWidth/10);
		this.textXOffsets = new int[myItemsLastIndex+1];
		
		for (int i = 0; i <= myItemsLastIndex; i++) {
			Item item = myItems[i];
			if (item instanceof StringItem) {
				StringItem stringItem = (StringItem) item;
				//System.out.println(i + ": currentX=" + currentX + ", firstLineWidth=" + (availWidth - currentX ) + ", text=" + stringItem.getText());
				stringItem.relativeY = currentY;
				stringItem.relativeX = 0;
				int width = stringItem.getItemWidth(availWidth - currentX, availWidth);
				WrappedText wrappedText = stringItem.getWrappedText(); 
				int lineHeight = stringItem.getLineHeight();
				if (wrappedText.size() == 1) {
					// this text fits on the same line as the previous one:
					stringItem.relativeX = currentX;
					//System.out.println("currentX=" + currentX + ", width=" + width + ", ++=" + (currentX + width) + ", max=" + maxCurrentX);
					if (currentX + width < maxCurrentX) {
						currentX += width;
					} else {
						currentX = 0;
						currentY += stringItem.itemHeight;
					}
					if (i == myItemsLastIndex) {
						//System.out.println("this is the last line");
						currentY += lineHeight + stringItem.getPaddingBottom();
					}
				} else {
					// this text requires at least one other line:
					if (currentX > 0) {
						this.textXOffsets[i] = currentX;
					}
					currentY += stringItem.itemHeight;
					int lastLineWidth = stringItem.getMarginLeft() + stringItem.getPaddingLeft() + wrappedText.getLineWidth( wrappedText.size() - 1) + stringItem.getPaddingRight() + stringItem.getMarginRight();
					if (i != myItemsLastIndex && lastLineWidth < maxCurrentX) {
						currentX = lastLineWidth;
						//System.out.println("last line has space left, reducing currentY by one lineheight: lastLineWidth=" +  lastLineWidth + ", maxCurrentX=" + maxCurrentX + ", availWidth=" + availWidth);
						if (wrappedText.size() > 1) {
							currentY -= lineHeight + stringItem.getPaddingBottom();
						}
					} else {
						currentX = 0;
					}
				}
			} else {
				// deal with normal items later
			}
		}
		this.contentHeight = currentY;
		this.contentWidth = availWidth;
		
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int index, Item item, int direction,
			Style focusedStyle) 
	{
		Style focStyle = super.focusItem(index, item, direction, focusedStyle);
		Background bg = removeItemBackground(item);
		if (bg != null) {
			this.focusedBackground = bg;
		}
		Border border = removeItemBorder(item);
		if (border != null) {
			this.focusedBorder = border;
		}
		return focStyle;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x,
			int y, int leftBorder, int rightBorder, int clipX, int clipY,
			int clipWidth, int clipHeight, Graphics g) 
	{
		int[] offsets = this.textXOffsets;
		for (int i = 0; i < myItems.length; i++) {
			if (i != this.focusedIndex) {
				Item item = myItems[i];
				//System.out.println("item " + i + " at " + item.relativeX + "/" + item.relativeY);
				int itemX = x + item.relativeX + offsets[i];
				int itemY = y + item.relativeY;
//				leftBorder = itemX;
//				rightBorder = itemX + item.itemWidth;
				if (itemY > clipY + clipHeight) {
					break;
				}
				if (itemY + item.itemHeight < clipY) {
					continue;
				}
				paintItem(item, i, itemX, itemY, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		// paint focused item last:
		Item focItem = this.focusedItem;
		if (focItem != null) {
			x += focItem.relativeX;
			y += focItem.relativeY;
			Background background = this.focusedBackground;
			Border border = this.focusedBorder;
			int xAdjust = offsets[this.focusedIndex];
			if (background != null || border != null) {
				int backgroundWidth = rightBorder - leftBorder;
				int topLineHeight = 0;
				int bottomLineHeight = 0;
				int bottomLineWidth = focItem.itemWidth;
				int bodyHeight = focItem.itemHeight;
				if (focItem instanceof StringItem) {
					StringItem stringItem = (StringItem) focItem;
					WrappedText wrappedText = stringItem.getWrappedText();
					if (xAdjust > 0 && wrappedText.size() > 1) {
						topLineHeight = stringItem.getMarginTop() + stringItem.getPaddingTop() + stringItem.getLineHeight();
					}
					bottomLineWidth = stringItem.getMarginLeft() + stringItem.getPaddingLeft() + wrappedText.getLineWidth( wrappedText.size() - 1) + stringItem.getPaddingRight() + stringItem.getMarginRight();
					if (bottomLineWidth < backgroundWidth - (backgroundWidth/10)) {
						bottomLineHeight = stringItem.getMarginBottom() + stringItem.getPaddingBottom() + stringItem.getLineHeight();
					}
					bodyHeight -= topLineHeight + bottomLineHeight;
					if (wrappedText.size() == 1) {
						backgroundWidth = focItem.itemWidth;
					}
					//System.out.println("xAdjust=" + xAdjust + ", lineHeight=" + stringItem.getLineHeight() + ", top=" + topLineHeight + ", bottom=" + bottomLineHeight + ", body=" + bodyHeight + ", itemHeight=" + focItem.itemHeight + " for " + stringItem.getText());
				}
				if (topLineHeight > 0) {
					// okay, we have at least one line that starts not at the beginning:
					g.clipRect(x + xAdjust, y, backgroundWidth - xAdjust, topLineHeight);
					if (background != null) {
						background.paint(x, y, backgroundWidth, focItem.itemHeight, g); // remove margins
					}
					if (border != null) {
						border.paint( x + xAdjust, y, backgroundWidth - xAdjust, focItem.itemHeight, g);
					}
					g.setClip(clipX, clipY, clipWidth, clipHeight);
				} 
				g.clipRect( x, y + topLineHeight, backgroundWidth, bodyHeight);
				if (background != null) {
					background.paint(x, y, backgroundWidth, focItem.itemHeight, g); // remove margins
				}
				if (border != null) {
					border.paint( x, y, backgroundWidth, focItem.itemHeight, g);
					if (topLineHeight != 0) {
						g.clipRect( x, y + topLineHeight, xAdjust + border.getBorderWidthRight(), bodyHeight);
						border.paint( x, y + topLineHeight, backgroundWidth, bodyHeight + bottomLineHeight, g);						
					}  
					if (bottomLineHeight != 0) {
						if (topLineHeight != 0) {
							g.setClip(clipX, clipY, clipWidth, clipHeight);
						}
						g.clipRect( x + bottomLineWidth - border.getBorderWidthLeft(), y + topLineHeight + bodyHeight - border.getBorderWidthBottom(), backgroundWidth - bottomLineWidth, bodyHeight);
						border.paint( x + bottomLineWidth - 2, y, backgroundWidth - bottomLineWidth + 2, topLineHeight + bodyHeight, g);						
					}
				}
				g.setClip(clipX, clipY, clipWidth, clipHeight);
				if (bottomLineHeight > 0) {
					g.clipRect( x, y + topLineHeight + bodyHeight, bottomLineWidth, bottomLineHeight);
					if (background != null) {
						background.paint(x, y, backgroundWidth, focItem.itemHeight, g); // remove margins
					}
					if (border != null) {
						border.paint( x, y, bottomLineWidth, focItem.itemHeight, g);
					}
					g.setClip(clipX, clipY, clipWidth, clipHeight);					
				}
			}
			x += xAdjust;
			//paintItem(focItem, this.focusedIndex, x, y + focItem.relativeY, x, x + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			paintItem(focItem, this.focusedIndex, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getChildHeight(de.enough.polish.ui.Item)
	 */
	protected int getChildHeight(Item item) {
		int index = this.parentContainer.indexOf(item);
		if (this.textXOffsets[index] > 0) {
			int availWidth = item.getAvailableWidth();
			int h = item.getItemHeight( availWidth - this.textXOffsets[index], availWidth, item.getAvailableHeight() );
			return h;
		}
		return super.getChildHeight(item);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getChildWidth(de.enough.polish.ui.Item)
	 */
	protected int getChildWidth(Item item) {
		int index = this.parentContainer.indexOf(item);
		if (this.textXOffsets[index] > 0) {
			int availWidth = item.getAvailableWidth();
			int w = item.getItemWidth( availWidth - this.textXOffsets[index], availWidth, item.getAvailableHeight() );
			return w;
		}
		return super.getChildWidth(item);
	}
	
	

}
