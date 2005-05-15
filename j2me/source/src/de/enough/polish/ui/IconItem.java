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
	private int imageHeight;
	private int imageWidth;
	private int yAdjust;
	//#if polish.midp2 && polish.css.scale-factor
		private int scaleFactor;
		private int scaleSteps;
		private int currentStep;
		private int[] rgbData;
		private int[] scaledRgbData;
		private boolean scaleDown;
		private boolean scaleFinished;
		private int scaleWidth;
		private int scaleHeight;
	//#endif

	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconItem( String text, Image image ) {
		this( text, image, null );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String text, Image image, Style style) {
		super(null, text, Item.HYPERLINK, style);
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
		if (this.imageAlign == Graphics.LEFT || this.imageAlign == Graphics.RIGHT ) {
			this.imageWidth += this.paddingHorizontal;
			firstLineWidth -= this.imageWidth;
			lineWidth -= this.imageWidth;
			super.initContent(firstLineWidth, lineWidth);
			if (this.imageHeight > this.contentHeight) {
				this.yAdjust = (this.imageHeight - this.contentHeight) / 2;
				this.contentHeight = this.imageHeight;
			} else {
				this.yAdjust = 0;
			}
			if (this.isLayoutExpand && this.imageAlign == Graphics.RIGHT) {
				this.contentWidth = firstLineWidth;
			}
			this.contentWidth += this.imageWidth;
		} else {
			this.imageHeight += this.paddingVertical;
			super.initContent(firstLineWidth, lineWidth);
			this.contentHeight += this.imageHeight;   
			if (this.imageWidth > this.contentWidth) {
				this.contentWidth = this.imageWidth;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.image != null) {
			if (this.imageAlign == Graphics.LEFT ) {
				//#if polish.midp2 && polish.css.scale-factor
					if (this.scaledRgbData != null) {
						g.drawRGB(this.scaledRgbData, 0, this.image.getWidth(), x, y, this.image.getWidth(), this.image.getHeight(), true );
					} else {
				//#endif
				g.drawImage(this.image, x, y, Graphics.TOP | Graphics.LEFT );
				//#if polish.midp2 && polish.css.scale-factor
					}
				//#endif
				x += this.imageWidth;
				leftBorder += this.imageWidth;
				y += this.yAdjust;
			} else if (this.imageAlign == Graphics.RIGHT ) {
				g.drawImage(this.image, x + this.contentWidth, y, Graphics.TOP | Graphics.RIGHT );
				rightBorder -= this.imageWidth;
				y += this.yAdjust;
			} else if (this.imageAlign == Graphics.TOP ) {
				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
				//System.out.println("left: " + leftBorder + "  right: " + rightBorder + "  contentWidth: " + this.contentWidth);
				//System.out.println("x: " + x + "  centerX: " + centerX );
				//#if polish.midp2 && polish.css.scale-factor
					if (this.scaledRgbData != null) {
						centerX -= this.scaleWidth / 2;
						if (centerX < 0) {
							centerX = 0;
						}
						int centerY = y - ((this.scaleHeight - this.image.getWidth()) / 2);
						//System.out.println("y=" + y + ", centerY=" + centerY );
						if (centerY < 0) {
							centerY = 0;
						}
						//#ifdef polish.Bugs.drawRgbOrigin
							g.drawRGB(this.scaledRgbData, 0, this.scaleWidth, centerX + g.getTranslateX(), centerY + g.getTranslateY(), this.scaleWidth, this.scaleHeight, true );
						//#else
							g.drawRGB(this.scaledRgbData, 0, this.scaleWidth, centerX, centerY, this.scaleWidth, this.scaleHeight, true );
						//#endif

					} else {
				//#endif
				g.drawImage(this.image, centerX, y, Graphics.TOP | Graphics.HCENTER );
				//#if polish.midp2 && polish.css.scale-factor
					}
				//#endif
				y += this.imageHeight;
			} else if (this.imageAlign == Graphics.BOTTOM ){
				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
				int bottomY = y + this.contentHeight;
				g.drawImage(this.image, centerX, bottomY, Graphics.BOTTOM | Graphics.HCENTER );
			} else {
				// imageAlign == Graphics.HCENTER | Graphics.VCENTER 
				int centerX = leftBorder + ((rightBorder - leftBorder) / 2);
				int centerY = y + this.contentHeight / 2;
				g.drawImage(this.image, centerX, centerY,  Graphics.HCENTER | Graphics.VCENTER);
			}
		}
		super.paintContent(x, y, leftBorder, rightBorder, g);
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
		//#ifdef polish.css.icon-image
			String imageName = style.getProperty("icon-image");
			if (imageName != null) {
				if (this.parent instanceof Container) {
					imageName = ((Container) this.parent).parseIndexUrl( imageName, this );
				}
				try {
					Image img = StyleSheet.getImage(imageName, this, true);
					if (img != null) {
						this.image = img;
						//#if polish.midp2 && polish.css.scale-factor
							this.rgbData = null;
							this.scaledRgbData = null;
						//#endif
					}
				} catch (IOException e) {
					//#debug error
					System.out.println("unable to load image [" + imageName + "]" + e);
				}
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
		if (this.isInitialised) {
			this.isInitialised = false;
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
		this.isInitialised = false;
		this.image = image;
		//#if polish.midp2 && polish.css.scale-factor
			this.rgbData = null;
			this.scaledRgbData = null;
		//#endif
	}
	
	/**
	 * Sets the image align for this icon.
	 * 
	 * @param imageAlign either Graphics.TOP, Graphics.LEFT, Graphics.BOTTOM or Graphics.RIGHT
	 */
	public void setImageAlign( int imageAlign ) {
		this.imageAlign = imageAlign;
		this.isInitialised = false;
	}
	
	//#if polish.midp2 && polish.css.scale-factor
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#hideNotify()
	 */
	public boolean animate() {
		if (this.scaleFactor != 0) {
			if (this.scaleFinished || this.image == null) {
				return false;
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
				if (step == 0) {
					this.scaleFinished =  true;
					this.scaledRgbData = null;
					return false;
				}
			} else {
				step++;
				if (step > this.scaleSteps) {
					this.scaleDown = true;
					return false;
				}
			}
			this.currentStep = step;
			this.scaleWidth = imgWidth + ((imgWidth * this.scaleFactor * step) / (this.scaleSteps * 100));
			this.scaleHeight = imgHeight + ((imgHeight * this.scaleFactor * step) / (this.scaleSteps * 100));
			//System.out.println("\nstep=" + step + ", scaleSteps=" + this.scaleSteps + "\nscaleWidth=" + this.scaleWidth + ", scaleHeight=" + this.scaleHeight + ", imgWidth=" + imgWidth + ", imgHeight=" + imgHeight + "\n");
			this.scaledRgbData = ImageUtil.scale(this.scaleWidth, this.scaleHeight, imgWidth, 
					imgWidth, imgHeight, this.rgbData);
			return true;
		}
		return false;
		
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
	}
	//#endif
	
//#ifdef polish.IconItem.additionalMethods:defined
	//#include ${polish.IconItem.additionalMethods}
//#endif

}
