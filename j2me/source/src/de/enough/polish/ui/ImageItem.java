//#condition polish.usePolishGui
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:44 CET 2003
/*
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * An item that can contain an image.
 * 
 * 
 * <P> Each <code>ImageItem</code> object contains a reference to an
 * <A HREF="../../../javax/microedition/lcdui/Image.html"><CODE>Image</CODE></A> object.
 * This <code>Image</code> may be mutable or immutable.  If the
 * <code>Image</code> is mutable, the
 * effect is as if snapshot of its contents is taken at the time the
 * <code>ImageItem</code>
 * is constructed with this <code>Image</code> and when
 * <code>setImage</code> is called with an <code>Image</code>.
 * The snapshot is used whenever the contents of the
 * <code>ImageItem</code> are to be
 * displayed.  Even if the application subsequently draws into the
 * <code>Image</code>, the
 * snapshot is not modified until the next call to
 * <code>setImage</code>.  The snapshot is
 * <em>not</em> updated when the container of the
 * <code>ImageItem</code> becomes current or
 * becomes visible on the display.  (This is because the application does not
 * have control over exactly when <code>Displayables</code> and Items
 * appear and disappear
 * from the display.)</P>
 * 
 * <P>The value <code>null</code> may be specified for the image
 * contents of an <code>ImageItem</code>.
 * If
 * this occurs (and if the label is also <code>null</code>) the
 * <code>ImageItem</code> will occupy no
 * space on the screen. </p>
 * 
 * <p><code>ImageItem</code> contains layout directives that were
 * originally defined in
 * MIDP 1.0.  These layout directives have been moved to the
 * <A HREF="../../../javax/microedition/lcdui/Item.html"><CODE>Item</CODE></A> class and now apply to all items.  The declarations are left
 * in <code>ImageItem</code> for source compatibility purposes.</p>
 * 
 * <P>The <code>altText</code> parameter specifies a string to be
 * displayed in place of the
 * image if the image exceeds the capacity of the display. The
 * <code>altText</code>
 * parameter may be <code>null</code>.</P>
 * <HR>
 * 
 * @author Robert Virkus, robert@enough.de
 * @since MIDP 1.0
 */
public class ImageItem extends Item
{

	//following variables are implicitely defined by getter- or setter-methods:
	private Image image;
	//#if polish.api.svg
		
	//#endif
	private String altText;
	private int textColor;
	private Font font;
	private int yOffset;
	private int height = -1;

	/**
	 * Creates a new <code>ImageItem</code> with the given label, image, layout
	 * directive, and alternate text string.  Calling this constructor is
	 * equivalent to calling
	 * 
	 * <pre><code>
	 * ImageItem(label, image, layout, altText, PLAIN, null );     
	 * </code></pre>
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * @see #ImageItem(String, Image, int, String, int)
	 */
	public ImageItem( String label, Image image, int layout, String altText)
	{
		this( label, image, layout, altText, PLAIN, null );
	}

	/**
	 * Creates a new <code>ImageItem</code> with the given label, image, layout
	 * directive, and alternate text string.  Calling this constructor is
	 * equivalent to calling
	 * 
	 * <pre><code>
	 * ImageItem(label, image, layout, altText, PLAIN, null );     
	 * </code></pre>
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * @param style the style of this image item.
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * @see #ImageItem(String, Image, int, String, int)
	 */
	public ImageItem( String label, Image image, int layout, String altText, Style style )
	{
		this( label, image, layout, altText, PLAIN, style );
	}
	
	/**
	 * Creates a new <code>ImageItem</code> object with the given label, image,
	 * layout directive, alternate text string, and appearance mode.
	 * Either label or alternative text may be present or <code>null</code>.
	 * 
	 * <p>The <code>appearanceMode</code> parameter
	 * (see <a href="Item.html#appearance">Appearance Modes</a>)
	 * is a hint to the platform of the application's intended use
	 * for this <code>ImageItem</code>. To provide hyperlink- or
	 * button-like behavior,
	 * the application should associate a default <code>Command</code> with this
	 * <code>ImageItem</code> and add an
	 * <code>ItemCommandListener</code> to this
	 * <code>ImageItem</code>.
	 * 
	 * <p>Here is an example showing the use of an
	 * <code>ImageItem</code> as a button: <p>
	 * <pre><code>
	 * ImageItem imgItem =
	 * new ImageItem("Default: ", img,
	 * Item.LAYOUT_CENTER, null,
	 * Item.BUTTON);
	 * imgItem.setDefaultCommand(
	 * new Command("Set", Command.ITEM, 1);
	 * // icl is ItemCommandListener
	 * imgItem.setItemCommandListener(icl);      
	 * </code></pre>
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * @param appearanceMode the appearance mode of the ImageItem, one of Item.PLAIN, Item.HYPERLINK, or Item.BUTTON
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * 												   or if appearanceMode invalid
	 * @since  MIDP 2.0
	 */
	public ImageItem( String label, Image image, int layout, String altText, int appearanceMode)
	{
		this( label, image, layout, altText, appearanceMode, null);
	}

	/**
	 * Creates a new <code>ImageItem</code> object with the given label, image,
	 * layout directive, alternate text string, and appearance mode.
	 * Either label or alternative text may be present or <code>null</code>.
	 * 
	 * <p>The <code>appearanceMode</code> parameter
	 * (see <a href="Item.html#appearance">Appearance Modes</a>)
	 * is a hint to the platform of the application's intended use
	 * for this <code>ImageItem</code>. To provide hyperlink- or
	 * button-like behavior,
	 * the application should associate a default <code>Command</code> with this
	 * <code>ImageItem</code> and add an
	 * <code>ItemCommandListener</code> to this
	 * <code>ImageItem</code>.
	 * 
	 * <p>Here is an example showing the use of an
	 * <code>ImageItem</code> as a button: <p>
	 * <pre><code>
	 * ImageItem imgItem =
	 * new ImageItem("Default: ", img,
	 * Item.LAYOUT_CENTER, null,
	 * Item.BUTTON);
	 * imgItem.setDefaultCommand(
	 * new Command("Set", Command.ITEM, 1);
	 * // icl is ItemCommandListener
	 * imgItem.setItemCommandListener(icl);      
	 * </code></pre>
	 * 
	 * @param label the label string
	 * @param image the image, can be mutable or immutable
	 * @param layout a combination of layout directives
	 * @param altText the text that may be used in place of the image
	 * @param appearanceMode the appearance mode of the ImageItem, one of Item.PLAIN, Item.HYPERLINK, or Item.BUTTON
	 * @param style the style of this image item.
	 * @throws IllegalArgumentException - if the layout value is not a legal combination of directives
	 * 												   or if appearanceMode invalid
	 */
	public ImageItem( String label, Image image, int layout, String altText, int appearanceMode, Style style )
	{
		super( label, layout, appearanceMode, style );
		this.image = image;
		this.altText = altText;
	}
	
	/**
	 * Gets the image contained within the <code>ImageItem</code>, or
	 * <code>null</code> if there is no
	 * contained image.
	 * 
	 * @return image used by the ImageItem
	 * @see #setImage(Image)
	 */
	public Image getImage()
	{
		return this.image;
	}

	/**
	 * Sets the <code>Image</code> object contained within the
	 * <code>ImageItem</code>.  The image may be
	 * mutable or immutable.  If <code>img</code> is
	 * <code>null</code>, the <code>ImageItem</code> is set to be
	 * empty.  If <code>img</code> is mutable, the effect is as if a
	 * snapshot is taken of
	 * <code>img's</code> contents immediately prior to the call to
	 * <code>setImage</code>.  This
	 * snapshot is used whenever the contents of the
	 * <code>ImageItem</code> are to be
	 * displayed.  If <code>img</code> is already the
	 * <code>Image</code> of this <code>ImageItem</code>, the effect
	 * is as if a new snapshot of img's contents is taken.  Thus, after
	 * painting into a mutable image contained by an
	 * <code>ImageItem</code>, the
	 * application can call
	 * 
	 * <pre><code>
	 * imageItem.setImage(imageItem.getImage());       
	 * </code></pre>
	 * 
	 * <p>to refresh the <code>ImageItem's</code> snapshot of its Image.</p>
	 * 
	 * <p>If the <code>ImageItem</code> is visible on the display when
	 * the snapshot is
	 * updated through a call to <code>setImage</code>, the display is
	 * updated with the new
	 * snapshot as soon as it is feasible for the implementation to so do.</p>
	 * 
	 * @param image the Image for this ImageItem, or null if none
	 * @see #getImage()
	 */
	public void setImage( Image image)
	{
		this.image = image;
		if (this.isInitialized) {
			this.isInitialized = false;
			repaint();
		}
	}

	/**
	 * Gets the text string to be used if the image exceeds the device's
	 * capacity to display it.
	 * 
	 * @return the alternate text value, or null if none
	 * @see #setAltText(java.lang.String)
	 */
	public String getAltText()
	{
		return this.altText;
	}

	/**
	 * Sets the alternate text of the <code>ImageItem</code>, or
	 * <code>null</code> if no alternate text is provided.
	 * 
	 * @param text the new alternate text
	 * @see #getAltText()
	 */
	public void setAltText( String text)
	{
		this.altText = text;
		if (this.image == null) {
			this.isInitialized = false;
			repaint();
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.image != null) {
			y += this.yOffset;
			if (this.isLayoutExpand) {
				if (this.isLayoutCenter) {
					g.drawImage( this.image, x + (rightBorder - x)/2 , y, Graphics.TOP | Graphics.HCENTER );
				} else if (this.isLayoutRight) {
					g.drawImage( this.image, rightBorder, y, Graphics.TOP | Graphics.RIGHT );
				} else {
					g.drawImage(this.image, x, y, Graphics.TOP | Graphics.LEFT );
				}
			} else {
				g.drawImage( this.image, x, y, Graphics.TOP | Graphics.LEFT );
			}
		} else if (this.altText != null) {
			g.setColor( this.textColor );
			g.setFont( this.font );
			g.drawString(this.altText, x, y, Graphics.TOP | Graphics.LEFT );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.image != null) {
			this.contentHeight = this.image.getHeight();
			this.contentWidth = this.image.getWidth();
		} else if (this.altText != null) {
			if (this.font == null) {
				this.font = Font.getDefaultFont();
			}
			this.contentHeight = this.font.getHeight();
			this.contentWidth = this.font.stringWidth(this.altText);
		} else {
			this.contentHeight = 0;
			this.contentWidth = 0;
		}
		if (this.height != -1) {
			this.contentHeight = this.height;
		}
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "img";
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		this.textColor = style.getFontColor();
		this.font = style.font;
		//#if polish.css.image-y-offset
			Integer yOffsetInt = style.getIntProperty("image-y-offset");
			if (yOffsetInt != null) {
				this.yOffset = yOffsetInt.intValue(); 
			}
		//#endif
		//#if polish.css.image-height
			Integer imageHeightInt = style.getIntProperty("image-height");
			if (imageHeightInt != null) {
				this.height = imageHeightInt.intValue(); 
			}
		//#endif
	}
}
