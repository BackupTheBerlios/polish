//#condition polish.usePolishGui
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:43 CET 2003
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

import de.enough.polish.util.BitMapFont;
import de.enough.polish.util.BitMapFontViewer;
import de.enough.polish.util.TextUtil;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * An item that can contain a string.
 * 
 * A <code>StringItem</code> is display-only; the user
 * cannot edit the contents. Both the label and the textual content of a
 * <code>StringItem</code> may be modified by the application. The
 * visual representation
 * of the label may differ from that of the textual contents.
 * 
 * @author Robert Virkus, robert@enough.de
 */
public class StringItem extends Item
{
	protected String text;
	protected String[] textLines;
	protected int textColor;
	protected Font font;
	//#ifdef polish.css.font-bitmap
		protected BitMapFont bitMapFont;
		protected BitMapFontViewer bitMapFontViewer;
	//#endif
	//#ifdef polish.css.text-horizontal-adjustment
		protected int textHorizontalAdjustment;
	//#endif
	//#ifdef polish.css.text-vertical-adjustment
		protected int textVerticalAdjustment;
	//#endif
	//#ifdef polish.css.text-effect
		protected TextEffect textEffect;
	//#endif

	/**
	 * Creates a new <code>StringItem</code> object.  Calling this
	 * constructor is equivalent to calling
	 * 
	 * <pre><code>
	 * StringItem(label, text, Item.PLAIN, null);     
	 * </code></pre>
	 * 
	 * @param label the Item label
	 * @param text the text contents
	 * @see #StringItem(String, String, int, Style)
	 */
	public StringItem( String label, String text)
	{
		this( label, text, PLAIN );
	}

	/**
	 * Creates a new <code>StringItem</code> object.  Calling this
	 * constructor is equivalent to calling
	 * 
	 * <pre><code>
	 * StringItem(label, text, Item.PLAIN, style);     
	 * </code></pre>
	 * 
	 * @param label the Item label
	 * @param text the text contents
	 * @param style the style
	 * @see #StringItem(String, String, int, Style)
	 */
	public StringItem( String label, String text, Style style )
	{
		this( label, text, PLAIN, style );
	}
	
	
	/**
	 * Creates a new <code>StringItem</code> object with the given label,
	 * textual content, and appearance mode.
	 * Either label or text may be present or <code>null</code>.
	 * 
	 * <p>The <code>appearanceMode</code> parameter
	 * (see <a href="Item.html#appearance">Appearance Modes</a>)
	 * is a hint to the platform of the application's intended use
	 * for this <code>StringItem</code>.  To provide hyperlink- or
	 * button-like behavior,
	 * the application should associate a default <code>Command</code> with this
	 * <code>StringItem</code> and add an
	 * <code>ItemCommandListener</code> to this
	 * <code>StringItem</code>.
	 * 
	 * <p>Here is an example showing the use of a
	 * <code>StringItem</code> as a button: </p>
	 * <pre><code>
	 * StringItem strItem = new StringItem("Default: ", "Set", Item.BUTTON);
	 * strItem.setDefaultCommand(
	 * new Command("Set", Command.ITEM, 1);
	 * // icl is ItemCommandListener
	 * strItem.setItemCommandListener(icl);     
	 * </code></pre>
	 * 
	 * @param label the StringItem's label, or null if no label
	 * @param text the StringItem's text contents, or null if the contents are initially empty
	 * @param appearanceMode the appearance mode of the StringItem, one of Item.PLAIN, Item.HYPERLINK, or Item.BUTTON
	 * @throws IllegalArgumentException if appearanceMode invalid
	 * @since  MIDP 2.0
	 */
	public StringItem( String label, String text, int appearanceMode)
	{
		this( label, text, appearanceMode, null );
	}

	/**
	 * Creates a new <code>StringItem</code> object with the given label,
	 * textual content, and appearance mode.
	 * Either label or text may be present or <code>null</code>.
	 * 
	 * <p>The <code>appearanceMode</code> parameter
	 * (see <a href="Item.html#appearance">Appearance Modes</a>)
	 * is a hint to the platform of the application's intended use
	 * for this <code>StringItem</code>.  To provide hyperlink- or
	 * button-like behavior,
	 * the application should associate a default <code>Command</code> with this
	 * <code>StringItem</code> and add an
	 * <code>ItemCommandListener</code> to this
	 * <code>StringItem</code>.
	 * 
	 * <p>Here is an example showing the use of a
	 * <code>StringItem</code> as a button: </p>
	 * <pre><code>
	 * StringItem strItem = new StringItem("Default: ", "Set", Item.BUTTON);
	 * strItem.setDefaultCommand(
	 * new Command("Set", Command.ITEM, 1);
	 * // icl is ItemCommandListener
	 * strItem.setItemCommandListener(icl);     
	 * </code></pre>
	 * 
	 * @param label the StringItem's label, or null if no label
	 * @param text the StringItem's text contents, or null if the contents are initially empty
	 * @param appearanceMode the appearance mode of the StringItem, one of Item.PLAIN, Item.HYPERLINK, or Item.BUTTON
	 * @param style the style for this item
	 * @throws IllegalArgumentException if appearanceMode invalid
	 * @since  MIDP 2.0
	 */
	public StringItem( String label, String text, int appearanceMode, Style style )
	{
		super( label, LAYOUT_DEFAULT, appearanceMode, style );
		this.text = text;
	}
	
	
	//#ifdef polish.css.text-effect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.textEffect != null) {
			animated |= this.textEffect.animate();
		}
		return animated;
	}
	//#endif

	//#ifdef polish.css.text-effect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#hideNotify()
	 */
	protected void hideNotify() {
		if (this.textEffect != null) {
			this.textEffect.hideNotify();
		}
		super.hideNotify();
	}
	//#endif

	//#ifdef polish.css.text-effect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showNotify()
	 */
	protected void showNotify() {
		if (this.textEffect != null) {
			this.textEffect.showNotify();
		}
		super.showNotify();
	}
	//#endif

	
	/**
	 * Gets the text contents of the <code>StringItem</code>, or
	 * <code>null</code> if the <code>StringItem</code> is
	 * empty.
	 * 
	 * @return a string with the content of the item
	 * @see #setText(java.lang.String)
	 */
	public String getText()
	{
		return this.text;
	}

	/**
	 * Sets the text contents of the <code>StringItem</code>. If text
	 * is <code>null</code>,
	 * the <code>StringItem</code>
	 * is set to be empty.
	 * 
	 * @param text - the new content
	 * @see #getText()
	 */
	public void setText( String text)
	{
		//#debug
		System.out.println("StringItem: setText( \"" + text + "\" )");
		this.text = text;
		if (text == null) {
			this.textLines = null;
		}
		requestInit();
	}

	/**
	 * Sets the application's preferred font for
	 * rendering this <code>StringItem</code>.
	 * The font is a hint, and the implementation may disregard
	 * the application's preferred font.
	 * 
	 * <p> The <code>font</code> parameter must be a valid <code>Font</code>
	 * object or <code>null</code>. If the <code>font</code> parameter is
	 * <code>null</code>, the implementation must use its default font
	 * to render the <code>StringItem</code>.</p>
	 * 
	 * @param font - the preferred font to use to render this StringItem
	 * @see #getFont()
	 * @since  MIDP 2.0
	 */
	public void setFont( Font font)
	{
		this.font = font;
		this.isInitialised = false;
	}

	/**
	 * Gets the application's preferred font for
	 * rendering this <code>StringItem</code>. The
	 * value returned is the font that had been set by the application,
	 * even if that value had been disregarded by the implementation.
	 * If no font had been set by the application, or if the application
	 * explicitly set the font to <code>null</code>, the value is the default
	 * font chosen by the implementation.
	 * 
	 * @return the preferred font to use to render this StringItem
	 * @see #setFont(javax.microedition.lcdui.Font)
	 * @since  MIDP 2.0
	 */
	public Font getFont()
	{
		return this.font;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.text != null) {
			//#ifdef polish.css.font-bitmap
				if (this.bitMapFontViewer != null) {
					if (this.isLayoutCenter) {
						x = leftBorder + (rightBorder - leftBorder) / 2;
						//#ifdef polish.css.text-horizontal-adjustment
							x += this.textHorizontalAdjustment;
						//#endif
					} else if (this.isLayoutRight) {

						x = rightBorder;
						//#ifdef polish.css.text-horizontal-adjustment
							x += this.textHorizontalAdjustment;
						//#endif
					}
					//#ifdef polish.css.text-vertical-adjustment
						y += this.textVerticalAdjustment;
					//#endif
					this.bitMapFontViewer.paint( x, y, g );
					return;
				}
			//#endif
			//#ifdef polish.css.text-vertical-adjustment
				y += this.textVerticalAdjustment;
			//#endif
			g.setFont( this.font );
			g.setColor( this.textColor );
			int lineHeight = this.font.getHeight() + this.paddingVertical; 
			int centerX = 0;
			if (this.isLayoutCenter) {
				centerX = leftBorder + (rightBorder - leftBorder) / 2;
				//#ifdef polish.css.text-horizontal-adjustment
					centerX += this.textHorizontalAdjustment;
				//#endif
			}
			//#ifdef polish.css.text-horizontal-adjustment
				x += this.textHorizontalAdjustment;
				rightBorder += this.textHorizontalAdjustment;
			//#endif
			for (int i = 0; i < this.textLines.length; i++) {
				String line = this.textLines[i];
				int lineX = x;
				int lineY = y;
				int orientation;
				// adjust the painting according to the layout:
				if (this.isLayoutRight) {
					lineX = rightBorder;
					orientation = Graphics.TOP | Graphics.RIGHT;
					//g.drawString( line, rightBorder, y, Graphics.TOP | Graphics.RIGHT );
				} else if (this.isLayoutCenter) {
					lineX = centerX;
					orientation = Graphics.TOP | Graphics.HCENTER;
					//g.drawString( line, centerX, y, Graphics.TOP | Graphics.HCENTER );
				} else {
					orientation = Graphics.TOP | Graphics.LEFT;
					// left layout (default)
					//g.drawString( line, x, y, Graphics.TOP | Graphics.LEFT );
				}
				//#if polish.css.text-effect
					if (this.textEffect != null) {
						this.textEffect.drawString( line, this.textColor, lineX, lineY, orientation, g );
					} else {
				//#endif
						g.drawString( line, lineX, lineY, orientation );
				//#if polish.css.text-effect
					}
				//#endif
				x = leftBorder;
				y += lineHeight;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth){
		if (this.text != null && this.font == null) {
			this.font = Font.getDefaultFont();
		}
		if (this.text == null) {
			this.contentHeight = 0;
			this.contentWidth = 0;
			return;
		}
		//#ifdef polish.css.font-bitmap
			if (this.bitMapFont != null) {
				int orientation = Graphics.LEFT;
				if (this.isLayoutCenter) {
					orientation = Graphics.HCENTER;
				} else if (this.isLayoutRight) {
					orientation = Graphics.RIGHT;
				}
				this.bitMapFontViewer = this.bitMapFont.getViewer( this.text );
				this.bitMapFontViewer.layout( firstLineWidth, lineWidth, this.paddingVertical, orientation );
				this.contentHeight = this.bitMapFontViewer.getHeight();
				this.contentWidth = this.bitMapFontViewer.getWidth();
				return;
			}
		//#endif
		String[] lines = TextUtil.split(this.text, this.font, firstLineWidth, lineWidth);
		int fontHeight = this.font.getHeight();
		this.contentHeight = (lines.length * (fontHeight + this.paddingVertical)) - this.paddingVertical;
		int maxWidth = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int width = this.font.stringWidth(line);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		this.contentWidth = maxWidth;
		this.textLines = lines;
		//this.isSingleLine = (lines.length == 1);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		this.textColor = style.fontColor;
		this.font = style.font;
		//#ifdef polish.css.font-bitmap
			String bitMapUrl = style.getProperty("font-bitmap");
			if (bitMapUrl != null) {
				this.bitMapFont = BitMapFont.getInstance( bitMapUrl );
			} else {
				this.bitMapFont = null;
				this.bitMapFontViewer = null;
			}
		//#endif
		//#ifdef polish.css.text-horizontal-adjustment
			Integer textHorizontalAdjustmentInt = style.getIntProperty( "text-horizontal-adjustment" );
			if ( textHorizontalAdjustmentInt != null ) {
				this.textHorizontalAdjustment = textHorizontalAdjustmentInt.intValue();		
			}
		//#endif
		//#ifdef polish.css.text-vertical-adjustment
			Integer textVerticalAdjustmentInt = style.getIntProperty( "text-vertical-adjustment" );
			if ( textVerticalAdjustmentInt != null ) {
				this.textVerticalAdjustment = textVerticalAdjustmentInt.intValue();		
			}
		//#endif
		//#ifdef polish.css.text-effect
			TextEffect effect = (TextEffect) style.getObjectProperty( "text-effect" );
			if (effect != null) {
				this.textEffect = effect;
				effect.setStyle(style);
			} else {
				this.textEffect = null;
			}
		//#endif	

	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		if ( this.appearanceMode == BUTTON ) {
			return "button";
		} else if (this.appearanceMode == HYPERLINK ) {
			return "a";
		} else {
			return "p";
		}
	}
	//#endif

}
