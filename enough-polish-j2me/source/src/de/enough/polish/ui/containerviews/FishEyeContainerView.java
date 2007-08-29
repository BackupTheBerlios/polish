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

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Arranges the items in a single row and scales items down.</p>
 * <p>Activate this view by specifying <code>view-type: fisheye</code> in the ChoiceGroup's, Container's or List's style.</p>
 * <p>Further attributes are:</p>
 * <ul>
 *  <li><b>fisheyeview-remove-text</b>: removes the text of embedded items and only shows the currently selected one</li>
 *  <li><b>fisheyeview-scale</b>: the percentage value to which the items should be scaled down, e.g. <code>fisheyeview-scale: 40%;</code></li>
 *  <li><b>show-text-in-title</b>: uses the text of embedded items for the screen title instead of displaying it under the item</li>
 *  <!--
 *  <li><b></b>: </li>
 *  -->
 * </ul>
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
	
	private boolean isRemoveText = true;
	private boolean includeAllItems = true;
	private String[] labels;
	private StringItem focusedLabel;
	//#if polish.midp2
		private int[][] originalRgbData;
		private int[] originalRgbDataWidths;
		private int[][] shownRgbData;
		private int[] shownRgbDataWidths;
		private int[] shownRgbDataHeight;
		private int scaleFactor = 50;
		//#if polish.css.fisheyeview-scale-end
			//#define tmp.scaleAll
			private int scaleFactorEnd;
		//#endif
		private int startTranslucency = 180;
		private int endTranslucency = 80;
		private int[] targetTranslucencies;
		private int[] currentTranslucencies;
	//#endif
	private int referenceFocusedIndex;
	private Background focusedBackground;
	private Border focusedBorder;
	private Style focusedStyle;
	private int focusedDirection;
	private int focusedWidth;
	private int maxItemHeight;
	private boolean isShowTextInTitle;
	
	/**
	 * Creates a new fish eye view
	 */
	public FishEyeContainerView() {
		this.allowsAutoTraversal = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.targetXCenterPositions != null) {
			Item[] myItems = this.parentContainer.getItems();
			int length = myItems.length;
			for (int i = 0; i <length; i++) {
				int target = this.targetXCenterPositions[i];
				Item item = myItems[i];
				int itemWidth = (item.itemWidth >> 1);
				int distance = getDistance( i, this.focusedIndex, length );
				if (distance != 0) {
					distance--;
				}
				//#if polish.midp2
					int factor = this.scaleFactor;
				//#endif
				//#if tmp.scaleAll
					factor = factor + ((this.scaleFactorEnd - factor ) * distance) / (length >> 1);
				//#endif
				//#if polish.midp2
					if (i != this.focusedIndex) {
						itemWidth = (itemWidth * factor) / 100;
					}
				//#endif
				int current = item.relativeX + itemWidth;
				//System.out.println(i + ": current=" + current + ", target=" + target);
				if (current != target) {
					animated = true;
					item.relativeX = calculateCurrent( current, target ) - itemWidth;
				}
				//#if polish.midp2
					int currentAlpha = this.currentTranslucencies[i];
					int targetAlpha = this.targetTranslucencies[i];
					boolean adjustAlpha = (currentAlpha != targetAlpha);
					if (adjustAlpha) {
						currentAlpha = calculateCurrent( currentAlpha, targetAlpha);
						this.currentTranslucencies[i] = currentAlpha;
					}
					boolean isScaled = false;
					if (factor != 100) {
						current = this.shownRgbDataWidths[ i ];
						if (i == this.focusedIndex) {
							target = this.originalRgbDataWidths[ i ];
						} else {
							target = (this.originalRgbDataWidths[ i ] * factor) / 100;
						}
						if (current != target && (distance < (length >> 2) || (Math.abs(current - target)*100/target > 5) ) ) {
							animated = true;
							isScaled = true;
							int[] data = this.originalRgbData[i];
							int originalWidth = this.originalRgbDataWidths[i];
							int originalHeight = data.length / originalWidth;
							int newWidth = calculateCurrent( current, target );
							int newHeight = (newWidth * originalHeight) / originalWidth;
							//int alpha = calculateAlpha( getDistance( i, this.focusedIndex, length ), length );
							//this.shownRgbData[i] = ImageUtil.scale(alpha, data, newWidth, newHeight, originalWidth, originalHeight );
							ImageUtil.scaleDownHq(this.shownRgbData[i],data, originalWidth, newWidth, 0, currentAlpha, false);
							this.shownRgbDataWidths[i] = newWidth;
							this.shownRgbDataHeight[i] = newHeight;
						}
					} 
					if (adjustAlpha && !isScaled) {
						// adjust only the translucency:
						animated = true;
						ImageUtil.setTransparencyOnlyForOpaque( currentAlpha,this.shownRgbData[i] );
					}
				//#endif
			}
		}
		if (this.isRemoveText && this.focusedLabel != null) {
			animated |= this.focusedLabel.animate();
		}
		if (this.focusedBackground != null) {
			animated |= this.focusedBackground.animate();
		}
		return animated;
	}
	
	
	/**
	 * Retrieves the distance between the given index and the focused element witin the list.
	 *  
	 * @param i the index
	 * @param focused the index of the focused element
	 * @param length the length of the list
	 * @return the distance between the index and the focused lement
	 */
	private static int getDistance(int i, int focused, int length) {
		/*
		 * r = Math.max( f, i )
		 * l = Math.min( f, i )
		 * distance = Math.min( length - r + l, r -l )
		 */
		if (i == focused) {
			return 0;
		}
		int right, left;
		if (focused > i) {
			right = focused;
			left = i;
		} else {
			right = i;
			left = focused;
		}
		return Math.min( length - right + left, right - left);
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
		this.isVertical = false;
		this.isHorizontal = true;
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println("ContainerView: intialising content for " + this + " with vertical-padding " + this.paddingVertical );

		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		int length = myItems.length;
		if (this.focusedIndex == -1 && length != 0) {
			//this.parentContainer.focus(0);
			if (parent.focusedIndex != -1) {
				this.focusedIndex = parent.focusedIndex;
			} else {
				this.focusedIndex = 0;
			}
			//System.out.println("AUTO-FOCUSSING ITEM " + this.focusedIndex );
			this.focusedItem = myItems[this.focusedIndex];
			this.focusedStyle = this.focusedItem.getFocusedStyle();
		}
		if (this.referenceXCenterPositions != null && this.referenceXCenterPositions.length == length) {
			return;
		}

		//#if polish.css.show-text-in-title
			if (this.isRemoveText && this.focusedLabel == null && !this.isShowTextInTitle) {
				this.focusedLabel = new StringItem(null, null);
			}
		//#else
			if (this.isRemoveText && this.focusedLabel == null) {
				this.focusedLabel = new StringItem(null, null);
			}
		//#endif

		if (this.isRemoveText && (this.labels == null || this.labels.length != length)) {
			this.labels = new String[ length ];
		}

		int maxWidth = 0;
		int maxHeight = 0;
		boolean hasFocusableItem = false;
		//#if polish.midp2
			this.originalRgbData = new int[ length ][];
			this.originalRgbDataWidths = new int[ length ];
			this.shownRgbData = new int[ length ][];
			this.shownRgbDataWidths = new int[ length ];
			this.shownRgbDataHeight= new int[ length ];
		//#endif
		for (int i = 0; i < length; i++) {
			Item item = myItems[i];
			if (this.isRemoveText) {
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
					this.shownRgbDataHeight[i] = height;
				} else {
					int newWidth = (width * this.scaleFactor) / 100;
					int newHeight = (height * this.scaleFactor) / 100;
					//this.shownRgbData[i] = ImageUtil.scale(data, newWidth, newHeight, width, height );
					int alpha = this.endTranslucency; // calculateAlpha( getDistance( i, this.focusedIndex, length ), length );
					this.shownRgbData[i]=new int[data.length];
					ImageUtil.scaleDownHq(this.shownRgbData[i], data,width, newWidth, 0, alpha, false);
					this.shownRgbDataWidths[i] = newWidth;
					this.shownRgbDataHeight[i] = newHeight;
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
		
		this.referenceXCenterPositions = new int[length];
		this.referenceXCenterPositions[this.focusedIndex] = lineWidth >> 1;
		this.referenceFocusedIndex = this.focusedIndex;

		int completeWidth;
		//#if polish.midp2
			completeWidth = maxWidth + ((maxWidth*this.scaleFactor)/100) * (length - 1) + ( length -1 ) * this.paddingHorizontal;
		//#else
			completeWidth = maxWidth * length + ( length - 1 ) * this.paddingHorizontal;
		//#endif
		
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
			availableWidth = ((completeWidth - this.focusedWidth) >> 1) - this.paddingHorizontal; 
		}
//		System.out.println("available=" + availableWidth + ", lineWidth=" + lineWidth + ", completeWidth=" + completeWidth + ", maxItemWidth=" + maxWidth + ", paddingHorizontal=" + this.paddingHorizontal);
		int availableWidthPerItem = (availableWidth << 8) / (length -1);
		// process items on the left side:
		int index = this.focusedIndex - 1;
		int processed = 0;
		int halfLength = (length - 1) >> 1;
		int startX = availableWidth;
//		System.out.println("left: startX=" + startX + ", center=" + (lineWidth/2) );
		while (processed < halfLength ) {
			if (index < 0) {
				index = length - 1;
			}
			//TODO add padding-horizontal
			this.referenceXCenterPositions[index] = startX - ((processed * availableWidthPerItem) >>> 8) - (processed * this.paddingHorizontal); //  - (maxItemWidth >> 1);
//			System.out.println( index + "=" + this.referenceXCenterPositions[index]);
			index--;
			processed++;
		}
		// process items on the right side:
		index = this.focusedIndex + 1;
		processed = 0;
		halfLength = length >> 1;
		startX =  lineWidth - startX; //(lineWidth >> 1) +  ((lineWidth >> 1) - startX);
//		System.out.println("right: startX=" + startX + ", center=" + (lineWidth/2) );
		while (processed < halfLength) {
			if (index >= length) {
				index = 0;
			}
			//TODO add padding-horizontal
			this.referenceXCenterPositions[index] = startX + ((processed * availableWidthPerItem) >>> 8) + (processed * this.paddingHorizontal); //+ (maxWidth >> 1);
//			System.out.println( index + "=" + this.referenceXCenterPositions[index]);
			index++;
			processed++;
		}
		
		for (int i = 0; i < length; i++) {
			Item item = myItems[i];
			int itemWidth = (item.getItemWidth(lineWidth, lineWidth) >> 1);
			//#if polish.midp2
				if (i != this.focusedIndex) {
					itemWidth = (itemWidth * this.scaleFactor) / 100;
//					System.out.println("adjusted item-width=" + itemWidth + ", original=" + item.itemWidth + ", max=" + maxWidth + ", scale-factor=" + this.scaleFactor);
				}
			//#endif
			item.relativeX = this.referenceXCenterPositions[i] - itemWidth;
//			System.out.println("item.relativeX for " + i + "=" + item.relativeX);
		}
		if (this.focusedStyle != null) {
			focusItem( this.focusedIndex, this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedItem.relativeX = this.referenceXCenterPositions[this.focusedIndex] - (this.focusedItem.getItemWidth(lineWidth, lineWidth) >> 1);
//			System.out.println("focused.relativeX=" + this.focusedItem.relativeX);
			this.focusedStyle = null;
		}
		
		
		this.contentWidth = lineWidth; //TODO: this can change when no expanded layout is used
		this.contentHeight = this.focusedLabel == null ? maxHeight : maxHeight + this.focusedLabel.getItemHeight(lineWidth, lineWidth); // maxItemHeight + this.paddingVertical + this.focusedLabel.getItemHeight(lineWidth, lineWidth);
		
		if (!this.isFocused) {
			AnimationThread.addAnimationItem( parent );
		}
	}
	
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		AnimationThread.addAnimationItem( this.parentItem );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focus(de.enough.polish.ui.Style, int)
	 */
	public void focus(Style focusstyle, int direction) {
		super.focus(focusstyle, direction);
		AnimationThread.removeAnimationItem( this.parentItem );
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
			//#if polish.midp2
				int[] targetAlphas;
				int[] currentAlphas;
			//#endif
			if (this.targetXCenterPositions == null || this.targetXCenterPositions.length != myItems.length) {
				targetXPositions = new int[ myItems.length ];
				//#if polish.midp2
					targetAlphas = new int[ myItems.length ];
					currentAlphas = new int[ myItems.length ];
				//#endif
			} else {
				targetXPositions = this.targetXCenterPositions;
				//#if polish.midp2
					targetAlphas = this.targetTranslucencies;
					currentAlphas = this.currentTranslucencies;
				//#endif
			}
			for (int i = 0; i < myItems.length; i++) {
				int nextIndex = i + difference;
				if (nextIndex < 0) {
					nextIndex = myItems.length + nextIndex;
				} else if (nextIndex >= myItems.length ) {
					nextIndex -= myItems.length;
				}
				targetXPositions[i] = this.referenceXCenterPositions[ nextIndex ];
				//#if polish.midp2
					targetAlphas[i] = calculateAlpha( getDistance(i, focIndex, myItems.length), myItems.length );
					//System.out.println("targetAlpha[" + i + "]=" + targetAlphas[i]);
					currentAlphas[i] = this.endTranslucency;
				//#endif
			}
			this.targetXCenterPositions = targetXPositions;
			//#if polish.midp2
				this.targetTranslucencies = targetAlphas;
				this.currentTranslucencies = currentAlphas;
			//#endif
		}
		
		Style itemStyle;
		if (!item.isFocused) {
			itemStyle = super.focusItem(focIndex, item, direction, focStyle);
		} else {
			itemStyle = item.getStyle();
		}
		
		this.focusedBackground = removeItemBackground( item );
		this.focusedBorder = removeItemBorder( item );
		if (this.isRemoveText) {
			if (this.isShowTextInTitle) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.setTitle( this.labels[ focIndex ] );
				}
			} else {
				this.focusedLabel.setText( this.labels[ focIndex ] );
				if (this.focusedLabel.getStyle() != item.getStyle() ) {
					this.focusedLabel.setStyle( item.getStyle() );
					removeItemBackground( this.focusedLabel );
					removeItemBorder( this.focusedLabel );
				}
			}
		}
		return itemStyle;
	}

	
	//#if polish.midp2

	/**
	 * @param distance
	 * @param length
	 * @return the target alpha value for the item
	 */
	private int calculateAlpha(int distance, int length) {
		if (distance == 0) {
			return 255;
		}
		int alpha = this.startTranslucency - ((this.startTranslucency - this.endTranslucency) * distance) / (length >> 1);
		//System.out.println("alpha for processed=" + distance + ", length=" + length + "=" + alpha);
		return alpha;
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int lineWidth = rightBorder - leftBorder;
		int itemLabelDiff = 0;
		if (this.isRemoveText && this.focusedLabel != null) {
			Style labelStyle = this.focusedLabel.getStyle();
			itemLabelDiff = labelStyle.paddingVertical - labelStyle.paddingBottom - labelStyle.paddingTop - labelStyle.marginBottom - labelStyle.marginTop;
		}
		if (this.focusedItem != null && (this.focusedBackground != null || this.focusedBorder != null)) {
			Item item = this.focusedItem;
			int backgroundWidth;
			int backgroundHeight;
			if ( this.isRemoveText && this.focusedLabel != null) {
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
			if (this.isRemoveText && this.focusedLabel != null) {
				//System.out.println("painting focused label with style " + this.focusedLabel.getStyle() );
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
			int height = this.shownRgbDataHeight[ index ];///data.length / width;
			
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
				this.isRemoveText = removeTextBool.booleanValue();
			}
		//#endif
			
		//#if polish.css.fisheyeview-scale && polish.midp2
			Integer scaleInt = style.getIntProperty( "fisheyeview-scale" );
			if (scaleInt != null) {
				this.scaleFactor = scaleInt.intValue();
			}
		//#endif
		//#if polish.css.fisheyeview-scale-start && polish.midp2
			Integer scaleStartInt = style.getIntProperty("fisheyeview-scale-start");
			if (scaleStartInt != null) {
				this.scaleFactor = scaleStartInt.intValue();
			}
		//#endif
		//#if polish.css.fisheyeview-scale-end && polish.midp2
			Integer scaleEndInt = style.getIntProperty("fisheyeview-scale-end");
			if (scaleEndInt != null) {
				this.scaleFactorEnd = scaleEndInt.intValue();
			} else if (this.scaleFactorEnd == 0) {
				this.scaleFactorEnd = this.scaleFactor;
			}
		//#endif
		//#if polish.css.show-text-in-title
			Boolean showTextInTitleBool = style.getBooleanProperty("show-text-in-title");
			if (showTextInTitleBool != null) {
				this.isShowTextInTitle = showTextInTitleBool.booleanValue();
				if (this.isShowTextInTitle) {
					this.isRemoveText = true;
				}
			}
		//#endif
			
	}

	
}
