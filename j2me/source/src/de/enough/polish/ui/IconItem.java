//#condition polish.usePolishGui
/*
 * Created on 04-Apr-2004 at 21:30:32.
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
package de.enough.polish.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ImageUtil;

/**
 * <p>Shows a string with an optional image attached to it.</p>
 * <p>The dynamic CSS selector of the IconItem is "icon".</p>
 * <p>
 * Following CSS attributes can be set:
 * <ul>
 * 		<li><b>icon-image-align</b>: The position of the icon-image relative to the text,
 * 				either "right", "left", "top" or "bottom". Default is "left".</li>
 * 		<li><b>icon-image</b>: The name of the image for this icon. The name can
 * 			include the index of this item relative to the parent-container:
 * 		    The icon-image "%INDEX%icon.png" would be renamed to "0icon.png" when
 * 			this icon would be the first one in a list.</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 * 
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        04-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IconItem extends StringItem 
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	
	Image image;
	private int imageAlign = Graphics.LEFT;
	//#ifdef polish.css.icon-image-align-next
		private boolean imageAlignNext;
	//#endif
	private int imageHeight;
	private int imageWidth;
	private int yAdjustText;
	protected boolean isTextVisible = true;
	//#if polish.midp2 && polish.css.scale-factor
		private int scaleFactor;
		private int scaleSteps = 5;
		private int currentStep;
		private int[] rgbData;
		private boolean scaleDown;
		private boolean scaleFinished;
		// a short array, so that we don't need to use synchronization.
		// scaleData[0] = scaled RGB Data
		// scaleData[1] = width of RGB Data 
		private Object[] scaleData;
	//#endif
	//#if polish.css.icon-vertical-adjustment
		private int verticalAdjustment;
	//#endif
	//#if polish.css.icon-horizontal-adjustment
		private int horizontalAdjustment;
	//#endif
	//private int yAdjustImage;
	private int relativeIconX;
	private int relativeIconY;

	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconItem( String text, Image image ) {
		this( null, text, image, null );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String text, Image image, Style style) {
		this( null, text, image, style );
	}

	/**
	 * Creates a new icon.
	 * 
	 * @param label the label of this item
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String label, String text, Image image, Style style) {
		super(label, text, Item.HYPERLINK, style);
		if (image != null) {
			setImage( image );
		}
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "icon";
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.image == null) {
			super.initContent(firstLineWidth, lineWidth);
			return;
		} 
		this.imageWidth = this.image.getWidth();
		this.imageHeight = this.image.getHeight();
		int yAdjustImage = 0;
		if (this.imageAlign == Graphics.LEFT || this.imageAlign == Graphics.RIGHT ) {
			this.imageWidth += this.paddingHorizontal;
			firstLineWidth -= this.imageWidth;
			lineWidth -= this.imageWidth;
			if (this.isTextVisible) {
				super.initContent(firstLineWidth, lineWidth);
			} else {
				this.contentWidth = 0;
				this.contentHeight = this.imageHeight;
			}
			// this.originalContentWidth = this.contentWidth;
			if (this.imageHeight > this.contentHeight) {
				int verticalAlign = this.layout & LAYOUT_VCENTER;
				if ( verticalAlign == LAYOUT_VCENTER || verticalAlign == 0 ) {
					this.yAdjustText = (this.imageHeight - this.contentHeight) / 2;
				} else if ( verticalAlign == LAYOUT_BOTTOM ) {
					this.yAdjustText = this.imageHeight - this.contentHeight;
				} else {
					// top layout:
					this.yAdjustText = 0;
				}
				this.contentHeight = this.imageHeight;
			} else if (this.contentHeight > this.imageHeight) {
				this.yAdjustText = 0;
				int verticalAlign = this.layout & LAYOUT_VCENTER;
				if ( verticalAlign == LAYOUT_VCENTER || verticalAlign == 0 ) {
					yAdjustImage = (this.contentHeight - this.imageHeight) / 2;
				} else if ( verticalAlign == LAYOUT_BOTTOM ) {
					yAdjustImage = (this.contentHeight - this.imageHeight);
				}	
			} else {
				this.yAdjustText = 0;
			}
			//#ifdef polish.css.icon-image-align-next
				if (this.isLayoutExpand && this.imageAlign == Graphics.RIGHT && !this.imageAlignNext) 
			//#else
				if (this.isLayoutExpand && this.imageAlign == Graphics.RIGHT) 
			//#endif
			{
				this.contentWidth = firstLineWidth;
			}
			this.contentWidth += this.imageWidth;
		} else {
			this.imageHeight += this.paddingVertical;
			if (this.isTextVisible) {
				super.initContent(firstLineWidth, lineWidth);
			} else {
				this.contentHeight = 0;
				this.contentWidth = 0;
			}
			this.contentHeight += this.imageHeight;   
			if (this.imageWidth > this.contentWidth) {
				this.contentWidth = this.imageWidth;
			}
		}
		
		// calculate icon positions:
		int iconLeftX = 0;
		int iconTopY = yAdjustImage;
		if (this.imageAlign == Graphics.LEFT ) {
			//#ifdef polish.css.icon-image-align-next
				if (this.imageAlignNext && this.isLayoutExpand) {
					if (this.isLayoutCenter) {
						iconLeftX = (firstLineWidth + this.imageWidth - this.contentWidth) >> 1;
					} else if (this.isLayoutRight) {
						iconLeftX = firstLineWidth  + this.imageWidth - this.contentWidth;
					}
				}
			//#endif
		} else if (this.imageAlign == Graphics.RIGHT ) {
			iconLeftX = firstLineWidth; // - this.imageWidth (imageWidth is already subtracted);
			//#ifdef polish.css.icon-image-align-next
				if (this.imageAlignNext && this.isLayoutExpand) {
					if (this.isLayoutCenter) {
						iconLeftX -= (firstLineWidth + this.imageWidth - this.contentWidth) >> 1;
					} else if (!this.isLayoutRight) {
						iconLeftX = this.contentWidth - this.imageWidth;
					}
				}
			//#endif
		} else {
			if (this.isLayoutExpand) {
				iconLeftX = (firstLineWidth - this.imageWidth) >> 1;
			} else {
				iconLeftX = (this.contentWidth - this.imageWidth) >> 1;
			}
			if (this.imageAlign == Graphics.BOTTOM ){
				iconTopY += this.contentHeight - this.imageHeight;
			}
		}
		this.relativeIconX = iconLeftX;
		this.relativeIconY = iconTopY;
	} 
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.image != null) {
			//#if polish.css.icon-horizontal-adjustment
				x += this.horizontalAdjustment;
			//#endif
			//#if polish.css.icon-vertical-adjustment
				y += this.verticalAdjustment;
			//#endif
			//#if polish.midp2 && polish.css.scale-factor
				Object[] localeScaleData = this.scaleData;
				boolean useScaledImage = this.isFocused && (localeScaleData != null) && !this.scaleFinished;
				if (useScaledImage) {
					int[] sData = (int[]) localeScaleData[0];
					int sWidth = ((Integer) localeScaleData[1]).intValue();
					int sHeight = sData.length / sWidth;
					int scaleX = x + this.relativeIconX + ((this.imageWidth - sWidth) >> 1);
					int scaleY = y + this.relativeIconY + ((this.imageHeight - sHeight) >> 1);
					//#ifdef polish.Bugs.drawRgbOrigin
						scaleX += g.getTranslateX();
						scaleY += g.getTranslateY();						
					//#endif
					if (scaleX < 0) {
						scaleX = 0;
					}
					if (scaleY < 0) {
						scaleY = 0;
					}
					g.drawRGB(sData, 0, sWidth, scaleX, scaleY, sWidth, sHeight, true );
				} else {
			//#endif
				g.drawImage( this.image, x + this.relativeIconX, y + this.relativeIconY, Graphics.TOP | Graphics.LEFT );
			//#if polish.midp2 && polish.css.scale-factor
				}
			//#endif
			if (this.imageAlign == Graphics.LEFT ) {
//				int leftX = x;
//				//#ifdef polish.css.icon-image-align-next
//					if (this.imageAlignNext) {
//						if (this.isLayoutCenter) {
//							leftX += ((rightBorder - leftBorder) - this.contentWidth) >> 1;
//						} else if (this.isLayoutRight) {
//							leftX += (rightBorder - leftBorder) - this.contentWidth;
//						}
//					}
//				//#endif
//				//#if polish.midp2 && polish.css.scale-factor
//					if (useScaledImage) { 
//						scaleX = leftX  -  ((sWidth - this.image.getWidth()) >> 1);
//						scaleY = y - ((sHeight - this.image.getHeight()) >> 1) + this.yAdjustImage;
//					} else {
//				//#endif
//						g.drawImage(this.image, leftX, y + this.yAdjustImage, Graphics.TOP | Graphics.LEFT );
//				//#if polish.midp2 && polish.css.scale-factor
//					}
//				//#endif
				x += this.imageWidth;
				leftBorder += this.imageWidth;
				y += this.yAdjustText;
			} else if (this.imageAlign == Graphics.RIGHT ) {
//				int rightX = x + this.contentWidth;
//				//#ifdef polish.css.icon-image-align-next
//					if (this.imageAlignNext) {
//						if (this.isLayoutCenter) {
//							rightX += ((rightBorder - leftBorder) - this.contentWidth) >> 1;
//						} else if (this.isLayoutRight) {
//							rightX += (rightBorder - leftBorder) - this.contentWidth;
//						}
//					}
//				//#endif
//				//#if polish.midp2 && polish.css.scale-factor
//					if (useScaledImage) {
//						scaleX = rightX  -  ((this.image.getWidth()  + sWidth) >> 1);
//						scaleY = y - ((sHeight - this.image.getHeight()) >> 1) + this.yAdjustImage;
//					} else {
//				//#endif
//						g.drawImage(this.image, rightX, y + this.yAdjustImage, Graphics.TOP | Graphics.RIGHT );
//				//#if polish.midp2 && polish.css.scale-factor
//					}
//				//#endif
				rightBorder -= this.imageWidth;
				y += this.yAdjustText;
			} else if (this.imageAlign == Graphics.TOP ) {
//				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
//				//System.out.println("left: " + leftBorder + "  right: " + rightBorder + "  contentWidth: " + this.contentWidth);
//				//System.out.println("x: " + x + "  centerX: " + centerX );
//				//#if polish.midp2 && polish.css.scale-factor
//					if (useScaledImage) {
//						scaleX = centerX - (sWidth / 2);
//						scaleY = y - ((sHeight - this.image.getHeight()) / 2);
//					} else {
//				//#endif
//						g.drawImage(this.image, centerX, y, Graphics.TOP | Graphics.HCENTER );
//				//#if polish.midp2 && polish.css.scale-factor
//					}
//				//#endif
				y += this.imageHeight;
//			} else if (this.imageAlign == Graphics.BOTTOM ){
//				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
//				int bottomY = y + this.contentHeight;
//				//#if polish.midp2 && polish.css.scale-factor
//					if (useScaledImage) {
//						scaleX = centerX  - (sWidth / 2);
//						scaleY = bottomY - sHeight;
//					} else {
//				//#endif
//						g.drawImage(this.image, centerX, bottomY, Graphics.BOTTOM | Graphics.HCENTER );
//				//#if polish.midp2 && polish.css.scale-factor
//					}
//				//#endif
//			} else {
//				// imageAlign == Graphics.HCENTER | Graphics.VCENTER 
//				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
//				int centerY = y + this.contentHeight / 2;
//				//#if polish.midp2 && polish.css.scale-factor
//					if (useScaledImage) {
//						scaleX = centerX  - (sWidth / 2);
//						scaleY = centerY - (sHeight / 2);
//					} else {
//				//#endif
//						g.drawImage(this.image, centerX, centerY,  Graphics.HCENTER | Graphics.VCENTER);
//				//#if polish.midp2 && polish.css.scale-factor
//					}
//				//#endif
			}
		
			//#if polish.css.icon-horizontal-adjustment
				x -= this.horizontalAdjustment;
			//#endif
			//#if polish.css.icon-vertical-adjustment
				y -= this.verticalAdjustment;
			//#endif
		}
		if (this.isTextVisible) {
			super.paintContent(x, y, leftBorder, rightBorder, g);
		}
	}
	
	/**
	 * Retrieves the image of this item.
	 * 
	 * @return the image of this icon.
	 */
	public Image getImage() {
		return this.image;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.icon-image-align
			Integer align = style.getIntProperty("icon-image-align");
			if (align == null) {
				// keep align setting
			} else {
				switch (align.intValue()) {
					case 0: this.imageAlign = Graphics.LEFT; break; 
					case 1: this.imageAlign = Graphics.RIGHT; break; 
					case 2: this.imageAlign = Graphics.TOP; break; 
					case 3: this.imageAlign = Graphics.BOTTOM; break; 
					case 4: this.imageAlign = Graphics.HCENTER | Graphics.VCENTER; break; 
				}
			}
		//#endif
		//#ifdef polish.css.icon-image-align-next
			Boolean alignExpandBool = style.getBooleanProperty("icon-image-align-next");
			if (alignExpandBool != null) {
				this.imageAlignNext = alignExpandBool.booleanValue();
			}
		//#endif
		//#ifdef polish.css.icon-image
			String imageName = style.getProperty("icon-image");
			if (imageName != null) {
				Item item = this;
				Item container = this.parent;
				while ((container != null) 
						&& !(container instanceof Container) 
						&& (container.parent != null)) 
				{
					item = container;
					container = container.parent;
				}
				if (container instanceof Container) {
					imageName = ((Container) container).parseIndexUrl( imageName, item );
				}
				//#if polish.debug.error
					else if ( imageName.indexOf( "%INDEX%") != -1) {
						throw new IllegalStateException("IconItem cannot resolve %INDEX% in url since parent is not a container: " + container + ", parent=" + this.parent);
					}
				//#endif
				try {
					Image img = StyleSheet.getImage(imageName, this, true);
					if (img != null) {
						this.image = img;
						//#if polish.midp2 && polish.css.scale-factor
							this.rgbData = null;
							this.scaleData = null;
						//#endif
					}
				} catch (IOException e) {
					//#debug error
					System.out.println("unable to load image [" + imageName + "]" + e);
				}
			}
		//#endif
		//#if polish.css.icon-vertical-adjustment
			Integer verticalAdjustmentInt = style.getIntProperty("icon-vertical-adjustment");
			if (verticalAdjustmentInt != null) {
				this.verticalAdjustment = verticalAdjustmentInt.intValue();
			}
		//#endif
		//#if polish.css.icon-horizontal-adjustment
			Integer horizontalAdjustmentInt = style.getIntProperty("icon-horizontal-adjustment");
			if (horizontalAdjustmentInt != null) {
				this.horizontalAdjustment = horizontalAdjustmentInt.intValue();
			}
		//#endif

		//#if polish.midp2	
			//#ifdef polish.css.scale-factor
				Integer scaleFactorInt = style.getIntProperty( "scale-factor" );
				if (scaleFactorInt != null) {
					this.scaleFactor = scaleFactorInt.intValue();
				}
			//#endif
			//#ifdef polish.css.scale-steps
				Integer scaleStepsInt = style.getIntProperty( "scale-steps" );
				if (scaleStepsInt != null) {
					this.scaleSteps = scaleStepsInt.intValue();
				}
			//#endif
		//#endif	
		//#if polish.css.icon-inactive
			Boolean inactiveBool = style.getBooleanProperty("icon-inactive");
			if (inactiveBool != null) {
				if (inactiveBool.booleanValue()) {
					this.appearanceMode = Item.PLAIN;
				} else {
					this.appearanceMode = Item.INTERACTIVE;								
				}
			}
		//#endif

	}

	/**
	 * Loads the specified image.
	 * 
	 * @param url the local URL of the image
	 */
	public void setImage( String url ) {
		try {
			Image img = StyleSheet.getImage(url, this, false);
			if (img != null) {
				setImage( img );
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load image [" + url + "]" + e);
		}		
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		this.image = image;
		//System.out.println("image [" + name + "] has been set.");
		if (this.isInitialized) {
			this.isInitialized = false;
			repaint();
		}
	}
	//#endif
	
	/**
	 * Sets the image for this icon.
	 * 
	 * @param image the image for this icon, when null is given, no image is painted.
	 */
	public void setImage( Image image ) {
		setImage( image, null );
	}
	
	/**
	 * Sets the image for this icon.
	 * 
	 * @param img the image for this icon, when null is given, no image is painted.
	 * @param style the new style of this item, is ignored when null
	 */
	public void setImage(Image img, Style style) {
		if (style != null) {
			setStyle( style );
		}
		this.image = img;
		//#if polish.midp2 && polish.css.scale-factor
			this.rgbData = null;
			this.scaleData = null;
		//#endif
		requestInit();
	}

	
	/**
	 * Sets the image align for this icon.
	 * 
	 * @param imageAlign either Graphics.TOP, Graphics.LEFT, Graphics.BOTTOM or Graphics.RIGHT
	 */
	public void setImageAlign( int imageAlign ) {
		this.imageAlign = imageAlign;
		this.isInitialized = false;
	}
	
	//#if polish.midp2 && polish.css.scale-factor
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		if (this.scaleFactor != 0) {
			if (this.scaleFinished || this.image == null) {
				return;
			}
			int imgWidth = this.image.getWidth();
			int imgHeight = this.image.getHeight();
			if (this.rgbData == null) {
				this.rgbData = new int[ imgWidth * imgHeight ];
				this.image.getRGB(this.rgbData, 0, imgWidth, 0, 0, imgWidth, imgHeight );
			}
			int step = this.currentStep;
			if (this.scaleDown) {
				step--;
				if (step <= 0) {
					this.scaleFinished =  true;
					this.scaleData = null;
					return;
				}
			} else {
				step++;
				if (step > this.scaleSteps) {
					this.scaleDown = true;
					return;
				}
			}
			this.currentStep = step;
			int scaleWidth = imgWidth + ((imgWidth * this.scaleFactor * step) / (this.scaleSteps * 100));
			int scaleHeight = imgHeight + ((imgHeight * this.scaleFactor * step) / (this.scaleSteps * 100));
			//System.out.println("\nstep=" + step + ", scaleSteps=" + this.scaleSteps + "\nscaleWidth=" + this.scaleWidth + ", scaleHeight=" + this.scaleHeight + ", imgWidth=" + imgWidth + ", imgHeight=" + imgHeight + "\n");
			int[] scaledRgbData = ImageUtil.scale( scaleWidth, scaleHeight, imgWidth, 
					imgWidth, imgHeight, this.rgbData);
			Object[] localeScaleData = new Object[]{ scaledRgbData, new Integer( scaleWidth ) };
			// read the last scale data for repainting the correctly sized area:
			Object[] lastScaleData = this.scaleData;
			if (lastScaleData != null) {
				int[] sData = (int[]) lastScaleData[0];
				int lastScaleWidth = ((Integer) lastScaleData[1]).intValue();
				int lastScaleHeight = sData.length / lastScaleWidth;
				if (lastScaleWidth > scaleWidth) {
					scaleWidth = lastScaleWidth;
				}
				if (lastScaleHeight > scaleHeight) {
					scaleHeight = lastScaleHeight;
				}
			}
			// this operation is atomic:
			this.scaleData = localeScaleData;
			// add repaint area:
			addRelativeRegion( repaintRegion, 
								this.relativeIconX + ((this.imageWidth - scaleWidth) >> 1), 
								this.relativeIconY + ((this.imageHeight - scaleHeight) >> 1),
								scaleWidth,
								scaleHeight 
			);
		}	
	}
	//#endif

	//#if polish.midp2 && polish.css.scale-factor
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		this.scaleFinished = false;
		this.scaleDown = false;
		this.currentStep = 0;
		this.scaleData = null;
	}
	//#endif

	//#if polish.debug.enabled
		public String toString(){
			return "IconItem(" + this.text + ")/" + super.toString();
		}
	//#endif
		
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this item.
	 * The default implementation does release any background resources.
	 */
	public void releaseResources() {
		super.releaseResources();
		//#if polish.midp2 && polish.css.scale-factor
			this.scaleData = null;
		//#endif
		this.image = null;
		this.isStyleInitialised = false;
	}

	/**
	 * Determines if the text of this icon item is visible
	 * @return true when the text is shown
	 */
	public boolean isTextVisible() {
		return this.isTextVisible;
	}
	
	/**
	 * Toggles the visibility of this icon's text
	 * @param isTextVisible true when the text should be shown
	 */
	public void setTextVisible( boolean isTextVisible ) {
		this.isTextVisible = isTextVisible;
		if (!isTextVisible) {
			this.textLines = null;
		}
		this.isInitialized = false;
	}

	
	
//#ifdef polish.IconItem.additionalMethods:defined
	//#include ${polish.IconItem.additionalMethods}
//#endif

}
