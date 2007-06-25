//#condition polish.usePolishGui
/*
 * Created on June 21, 2007 at 10:48:13 AM.
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
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Arranges the items in a single row and scales items down.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        June 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FishEyeContainerView extends ContainerView {

	private int minSpeed = 2;
	private int maxSpeed = -1;
	private int[] targetXCenterPositions;
	private int[] referenceXCenterPositions;
	
	private boolean removeText = true;
	private boolean includeAllItems = true;
	private String[] labels;
	private StringItem focusedLabel;
	//#if polish.midp2
		private int[][] originalRgbData;
		private int[] originalRgbDataWidths;
		private int[][] shownRgbData;
		private int[] shownRgbDataWidths;
		private int scaleFactor = 50;
	//#endif
	private int referenceFocusedIndex;
	private Background focusedBackground;
	private Border focusedBorder;
	private Style focusedStyle;
	private int focusedDirection;
	private int focusedWidth;
	private int maxItemHeight;
	
	/**
	 * Creates a new fish eye view
	 */
	public FishEyeContainerView() {
		this.focusedLabel = new StringItem(null, null);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.targetXCenterPositions != null) {
			Item[] myItems = this.parentContainer.getItems();
			for (int i = 0; i < this.targetXCenterPositions.length; i++) {
				int target = this.targetXCenterPositions[i];
				Item item = myItems[i];
				int current = item.relativeX + (item.itemWidth >> 1);
				//System.out.println(i + ": current=" + current + ", target=" + target);
				if (current != target) {
					animated = true;
					item.relativeX = calculateCurrent( current, target ) - (item.itemWidth >> 1);
				}
				//#if polish.midp2
					if (this.scaleFactor != 100) {
						current = this.shownRgbDataWidths[ i ];
						if (i == this.focusedIndex) {
							target = this.originalRgbDataWidths[ i ];
						} else {
							target = (this.originalRgbDataWidths[ i ] * this.scaleFactor) / 100;
						}
						if (current != target) {
							animated = true;
							int[] data = this.originalRgbData[i];
							int originalWidth = this.originalRgbDataWidths[i];
							int originalHeight = data.length / originalWidth;
							int newWidth = calculateCurrent( current, target );
							int newHeight = (newWidth * originalHeight) / originalWidth;
							int distance = i - this.focusedIndex;
							if (distance != 0) {
								if (distance < 0) {
									distance += myItems.length;
								}
								if (distance > myItems.length >> 1) {
									distance = myItems.length - distance;
								}
								distance--;
							}
							int alpha = 255 - (255*distance)/(myItems.length>>1);
							this.shownRgbData[i] = ImageUtil.scale(alpha, data, newWidth, newHeight, originalWidth, originalHeight );
							this.shownRgbDataWidths[i] = newWidth;
						}
					}
				//#endif
			}
		}
		if (this.removeText) {
			animated |= this.focusedLabel.animate();
		}
		if (this.focusedBackground != null) {
			animated |= this.focusedBackground.animate();
		}
		return animated;
	}
	
	
	private int calculateCurrent(int current, int target) {
		int speed = Math.max( this.minSpeed, Math.abs( current - target ) / 3 );
		if (this.maxSpeed != -1 && speed > this.maxSpeed) {
			speed = this.maxSpeed;
		}
		if (current < target ) {
			current += speed;
			if (current > target) {
				current = target;
			}
		} else {
			current -= speed;
			if (current < target) {
				current = target;
			}
		}
		return current;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int lineWidth) {
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println("ContainerView: intialising content for " + this + " with vertical-padding " + this.paddingVertical );
		//#if polish.Container.allowCycling != false
			this.allowCycling = parent.allowCycling;
		//#endif

		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		if (this.referenceXCenterPositions != null && this.referenceXCenterPositions.length == myItems.length) {
			return;
		}
		
		if (this.removeText && this.labels == null || this.labels.length != myItems.length) {
			this.labels = new String[ myItems.length ];
		}

		int maxWidth = 0;
		int maxHeight = 0;
		boolean hasFocusableItem = false;
		//#if polish.midp2
			this.originalRgbData = new int[ myItems.length ][];
			this.originalRgbDataWidths = new int[ myItems.length ];
			this.shownRgbData = new int[ myItems.length ][];
			this.shownRgbDataWidths = new int[ myItems.length ];
		//#endif
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (this.removeText) {
				String text = item.getLabel();
				if (text != null) {
					this.labels[i] = text;
					item.setLabel( null );
				} else if ( item instanceof IconItem) {
					IconItem iconItem = (IconItem) item;
					text = iconItem.getText();
					if (text != null) {						
						this.labels[i] = text;
						iconItem.setTextVisible(false);
						//iconItem.setText(null);
					}
				}
			}
			int width = item.getItemWidth( firstLineWidth, lineWidth );
			int height = item.getItemHeight( firstLineWidth, lineWidth );
			//#if polish.midp2
				int[] data = UiAccess.getRgbData(item);
				this.originalRgbData[i] = data;
				this.originalRgbDataWidths[i] = width;
				if (this.scaleFactor == 100) {
					this.shownRgbData[i] = data;
					this.shownRgbDataWidths[i] = width;
				} else {
					int newWidth = (width * this.scaleFactor) / 100;
					int newHeight = (height * this.scaleFactor) / 100;
					this.shownRgbData[i] = ImageUtil.scale(data, newWidth, newHeight, width, height );
					this.shownRgbDataWidths[i] = newWidth;
				}
			//#endif
			if (item.appearanceMode != Item.PLAIN) {
				hasFocusableItem = true;
			}
			if (width > maxWidth) {
				maxWidth = width; 
			}
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		this.maxItemHeight = maxHeight;
		if (hasFocusableItem) {
			this.appearanceMode = Item.INTERACTIVE;
		} else {
			this.appearanceMode = Item.PLAIN;
		}
		
		this.referenceXCenterPositions = new int[myItems.length];
		this.referenceXCenterPositions[this.focusedIndex] = lineWidth >> 1;
		this.referenceFocusedIndex = this.focusedIndex;

		int completeWidth = maxWidth * myItems.length + ( myItems.length -1 ) * this.paddingHorizontal;
		
		if (this.focusedStyle != null && this.focusedItem != null) {
			UiAccess.focus(this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedWidth = this.focusedItem.getItemWidth( lineWidth, lineWidth );
			this.focusedItem.relativeX = (lineWidth - this.focusedWidth) >> 1;
		} else if (this.focusedWidth == 0) {
			this.focusedWidth = maxWidth;
		}
		int availableWidth;
		if ( (completeWidth > lineWidth && this.includeAllItems) || (completeWidth < lineWidth && isLayoutExpand() ) ) {
			availableWidth = ((lineWidth - this.focusedWidth) >> 1) - this.paddingHorizontal;
		} else {
			availableWidth = (completeWidth - this.focusedWidth >> 1) - this.paddingHorizontal; 
		}
		//System.out.println("available=" + availableWidth + ", lineWidth=" + lineWidth + ", completeWidth=" + completeWidth + ", maxItemWidth=" + maxItemWidth + ", paddingHorizontal=" + this.paddingHorizontal);
		int availableWidthPerItem = (availableWidth << 8) / (myItems.length -1);
		// process items on the left side:
		int index = this.focusedIndex - 1;
		int processed = 0;
		int length = (myItems.length -1) >> 1;
		int startX = availableWidth;
		while (processed < length ) {
			if (index < 0) {
				index = myItems.length - 1;
			}
			this.referenceXCenterPositions[index] = startX - ((processed * availableWidthPerItem) >>> 8); //  - (maxItemWidth >> 1);
			index--;
			processed++;
		}
		// process items on the right side:
		index = this.focusedIndex + 1;
		processed = 0;
		length = myItems.length >> 1;
		startX =  ((lineWidth + maxWidth ) >> 1) - this.paddingHorizontal;
		while (processed < length) {
			if (index >= myItems.length) {
				index = 0;
			}
			this.referenceXCenterPositions[index] = startX + ((processed * availableWidthPerItem) >>> 8) + (maxWidth >> 1);
			index++;
			processed++;
		}
		
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.relativeX = this.referenceXCenterPositions[i] - (item.getItemWidth(lineWidth, lineWidth) >> 1);
		}
		if (this.focusedItem != null) {
			focusItem( this.focusedIndex, this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedItem.relativeX = this.referenceXCenterPositions[this.focusedIndex] - (this.focusedItem.getItemWidth(lineWidth, lineWidth) >> 1);
			this.focusedStyle = null;
		}
		
		this.contentWidth = lineWidth; //TODO: this can change when no expanded layout is used
		this.contentHeight = maxHeight + this.focusedLabel.getItemHeight(lineWidth, lineWidth); // maxItemHeight + this.paddingVertical + this.focusedLabel.getItemHeight(lineWidth, lineWidth);
		
	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		if (this.referenceXCenterPositions == null) {
			this.focusedStyle = focStyle;
			this.focusedDirection = direction;
			this.focusedIndex = focIndex;
			this.focusedItem = item;
			return item.getStyle();
		} else {
			int difference = this.referenceFocusedIndex - focIndex;
			Item[] myItems = this.parentContainer.getItems();
			int[] targetXPositions;
			if (this.targetXCenterPositions == null) {
				targetXPositions = new int[ myItems.length ];
			} else {
				targetXPositions = this.targetXCenterPositions;
			}
			for (int i = 0; i < myItems.length; i++) {
				int nextIndex = i + difference;
				if (nextIndex < 0) {
					nextIndex = myItems.length + nextIndex;
				} else if (nextIndex >= myItems.length ) {
					nextIndex -= myItems.length;
				}
				targetXPositions[i] = this.referenceXCenterPositions[ nextIndex ];
			}
			this.targetXCenterPositions = targetXPositions;
			//#if polish.midp2
				// process items on the left side:
				int index = focIndex - 1;
				int processed = 0;
				int length = (myItems.length -1) >> 1;
				while (processed < length ) {
					if (index < 0) {
						index = myItems.length - 1;
					}
					int[] data = this.shownRgbData[ index ];
					int alpha = 255 - (255*processed)/length;
					ImageUtil.setTransparencyOnlyForOpaque( alpha, data);
					index--;
					processed++;
				}
				// process items on the right side:
				index = focIndex + 1;
				processed = 0;
				length = myItems.length >> 1;
				while (processed < length) {
					if (index >= myItems.length) {
						index = 0;
					}
					int[] data = this.shownRgbData[ index ];
					int alpha = 255 - (255*processed)/length;
					ImageUtil.setTransparencyOnlyForOpaque( alpha, data);
					index++;
					processed++;
				}
			//#endif
		}
		Style itemStyle = super.focusItem(focIndex, item, direction, focStyle);
		
		this.focusedBackground = removeItemBackground( item );
		this.focusedBorder = removeItemBorder( item );
		if (this.removeText) {
			this.focusedLabel.setText( this.labels[ focIndex ] );
			if (this.focusedLabel.getStyle() != focStyle ) {
				focStyle = this.focusedItem.getFocusedStyle();
				this.focusedLabel.setStyle( focStyle );
				removeItemBackground( this.focusedLabel );
				removeItemBorder( this.focusedLabel );
			}
		}
		return itemStyle;
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int lineWidth = rightBorder - leftBorder;
		int itemLabelDiff = 0;
		if (this.removeText) {
			Style labelStyle = this.focusedLabel.getStyle();
			itemLabelDiff = labelStyle.paddingVertical - labelStyle.paddingBottom - labelStyle.paddingTop - labelStyle.marginBottom - labelStyle.marginTop;
		}
		if (this.focusedItem != null && (this.focusedBackground != null || this.focusedBorder != null)) {
			Item item = this.focusedItem;
			int backgroundWidth;
			int backgroundHeight;
			if ( this.removeText ) {
				backgroundWidth = Math.max( item.itemWidth, this.focusedLabel.getItemWidth( lineWidth, lineWidth ) );
				backgroundHeight = item.itemHeight + this.focusedLabel.itemHeight + itemLabelDiff;
			} else {
				backgroundWidth = item.itemWidth;
				backgroundHeight = item.itemHeight;
			}
//			if (this.focusedBackground != null) {
//				//TODO add background.borderWidth << 1 + border.borderWidth << 1 ??
//			}
			int backgroundX = x + ((rightBorder - leftBorder) >> 1) - (backgroundWidth >> 1 ); 
			if (this.focusedBackground != null) {
				this.focusedBackground.paint( backgroundX, y, backgroundWidth, backgroundHeight, g);
			}
			if (this.focusedBorder != null) {
				this.focusedBorder.paint( backgroundX, y, backgroundWidth, backgroundHeight, g);
			}
		}
		int itemX;
		int itemY = y;
		
		int processed = ((myItems.length - 1) >> 1);
		int index = this.focusedIndex - processed;
		if (index < 0) {
			index += myItems.length;
		}
		// draw left titems:
		while (processed > 0) {
			Item item = myItems[index];
			//System.out.println("left: " + index + " at " + item.relativeX );
			itemX = x + item.relativeX;
			paintItem(item, index, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			processed--;
			index++;
			if (index == myItems.length) {
				index = 0;
			}
		}
		// draw right items:
		processed = (myItems.length >> 1);
		index = (this.focusedIndex + (myItems.length >> 1))  % myItems.length;
		while (processed > 0) {
			Item item = myItems[index];			
			//System.out.println("right: " + index + " at " + item.relativeX );
			itemX = x + item.relativeX;
			paintItem(item, index, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			processed--;
			index--;
			if (index == -1) {
				index = myItems.length - 1;
			}
		}
		// now paint focused item:
		Item item = this.focusedItem;
		if (item != null) {
			itemX = x + item.relativeX;
			//itemY = y + item.getItemHeight( lineWidth, lineWidth );
			paintItem(item, this.focusedIndex, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);

			// now paint label:
			if (this.removeText) {
				int labelX = x + ((rightBorder - leftBorder) >> 1) - (this.focusedLabel.getItemWidth( lineWidth, lineWidth ) >> 1);
				int labelY = itemY + item.itemHeight + itemLabelDiff;
				this.focusedLabel.paint( labelX, labelY, labelX, labelX + this.focusedLabel.itemWidth, g);
			}
		}
		
	}
	



	//#if polish.midp2
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#if polish.midp2
			int[] data = this.shownRgbData[ index ];
			int width = this.shownRgbDataWidths[ index ];
			int height = data.length / width;
			int itemLayout = item.getLayout();
			if ( (itemLayout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
				y += (this.maxItemHeight - height) >> 1;
			} else if ( (itemLayout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
				y += (this.maxItemHeight - height);
			} 
			g.drawRGB(data, 0, width, x, y, width, height, true );
		//#else
			super.paintItem(item, index, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
		//#endif
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.fisheyeview-remove-text
			Boolean removeTextBool = style.getBooleanProperty("fisheyeview-remove-text");
			if (removeTextBool != null) {
				this.removeText = removeTextBool.booleanValue();
			}
		//#endif
			
		//#if polish.css.fisheyeview-scale && polish.midp2
			Integer scaleInt = style.getIntProperty( "fisheyeview-scale" );
			if (scaleInt != null) {
				this.scaleFactor = scaleInt.intValue();
			}
		//#endif
	}

}
