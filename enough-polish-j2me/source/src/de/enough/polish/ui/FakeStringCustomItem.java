//#condition polish.LibraryBuild
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
 * Meant for classes that want to be compatible with javax.microedition.lcdui.CustomItem for IDEs only while extending de.enough.polish.ui.StringItem in reality.
 * 
 * <p>Subclasses can change the hierarchy with preprocessing like this:
 * <pre>
 * public class MyCustomItem
 * //#if polish.LibraryBuild
 * 		 extends FakeStringCustomItem
 * //#else
 * 		//# extends StringItem
 * //#endif	 
 * </pre>
 * </p>
 * <p>This allows subclasses to access all fields and methods of the J2ME Polish item class.</p>
 * <p>Note that this class can never be used in reality. Ever.</p>
 * 
 * @since J2ME Polish 1.3
 * @author Robert Virkus, robert@enough.de
 */
public class FakeStringCustomItem extends FakeCustomItem
{
	private static final int DIRECTION_BACK_AND_FORTH = 0;
	private static final int DIRECTION_LEFT = 1;
	private static final int DIRECTION_RIGHT = 2;
	protected String text;
	protected String[] textLines;
	protected int textColor;
	protected Font font;
	//#ifdef polish.css.font-bitmap
		protected BitMapFont bitMapFont;
		protected BitMapFontViewer bitMapFontViewer;
	//#endif
	//#ifdef polish.css.text-wrap
		protected boolean useSingleLine;
		protected boolean clipText;
		protected int xOffset;
		private int textWidth;
		private boolean isHorizontalAnimationDirectionRight;
		protected boolean animateTextWrap = true;
		//#ifdef polish.css.text-wrap-animation-direction
			protected int textWrapDirection = DIRECTION_BACK_AND_FORTH;
		//#endif
		//#ifdef polish.css.text-wrap-animation-speed
			protected int textWrapSpeed = DIRECTION_BACK_AND_FORTH;
		//#endif
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
	 * @see de.enough.polish.ui.StringItem#StringItem(String, String, int, Style)
	 */
	public FakeStringCustomItem( String label, String text)
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
	 * @see de.enough.polish.ui.StringItem#StringItem(String, String, int, Style)
	 */
	public FakeStringCustomItem( String label, String text, Style style )
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
	public FakeStringCustomItem( String label, String text, int appearanceMode)
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
	public FakeStringCustomItem( String label, String text, int appearanceMode, Style style )
	{
		super( label, LAYOUT_DEFAULT, appearanceMode, style );
		this.text = text;
	}
	
	
	
	//#if polish.css.text-effect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		//#if polish.css.text-wrap
			if (this.animateTextWrap) {
				if (this.useSingleLine && this.clipText) {
					
					int speed = 1;
					//#ifdef polish.css.text-wrap-animation-speed
						speed = this.textWrapSpeed;
					//#endif
					//#ifdef polish.css.text-wrap-animation-direction
						if (this.textWrapDirection == 0) {
					//#endif
							if (this.isHorizontalAnimationDirectionRight) {
								this.xOffset += speed;
								if (this.xOffset >= 0) {
									this.isHorizontalAnimationDirectionRight = false;
								}
							} else {
								this.xOffset -= speed;
								if (this.xOffset + this.textWidth < this.contentWidth) {
									this.isHorizontalAnimationDirectionRight = true;
								}
							}
					//#ifdef polish.css.text-wrap-animation-direction
						} else if (this.textWrapDirection == DIRECTION_LEFT) {
							int offset = this.xOffset - speed;
							if (offset + this.textWidth < 0) {
								offset = this.contentWidth;
							}
							this.xOffset = offset;
						} else {
							int offset = this.xOffset + speed;
							if (offset > this.contentWidth) {
								offset = -this.textWidth;
							}
							this.xOffset = offset;							
						}
					//#endif

					addRelativeToContentRegion(repaintRegion, 0, 0, this.contentWidth, this.contentHeight );
				}
			}
		//#endif
		//#if polish.css.text-effect
			if (this.textEffect != null) {
				// Nothing to do here.
			}
		//#endif
	}
	//#endif

	
	//#if polish.css.text-wrap
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		if (this.clipText) {
			this.xOffset = 0;
		}
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
	 * Sets the text contents of the <code>StringItem</code>. 
	 * If text
	 * is <code>null</code>,
	 * the <code>StringItem</code>
	 * is set to be empty.
	 * 
	 * @param text the new content
	 * @see #getText()
	 */
	public void setText( String text)
	{
		setText( text, null );
	}
	
	/**
	 * Sets the text contents of the <code>StringItem</code> along with a style. 
	 * If text is <code>null</code>,
	 * the <code>StringItem</code>
	 * is set to be empty.
	 * 
	 * @param text the new content
	 * @param style the new style, is ignored when null
	 * @see #getText()
	 */
	public void setText( String text, Style style)
	{
		//#debug
		System.out.println("StringItem: setText( \"" + text + "\" )");
		if ( style != null ) {
			setStyle( style );
		}
		this.text = text;
		if (text == null) {
			this.textLines = null;
		}
		requestInit();
	}
	
	/**
	 * Sets the text color for contents of the <code>StringItem</code>.
	 *
	 * @param color the new color for the content
	 */
	public void setTextColor( int color)
	{
		this.textColor = color;
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
		this.isInitialized = false;
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
		if (this.font == null) {
			if (this.style != null) {
				this.font = this.style.getFont();
			}
			if (this.font == null) {
				this.font = Font.getDefaultFont();
			}
		}
		return this.font;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.text != null) {
			//#if polish.css.text-wrap
				int clipX = 0;
				int clipY = 0;
				int clipWidth = 0;
				int clipHeight = 0;
				if (this.useSingleLine && this.clipText ) {
					clipX = g.getClipX();
					clipY = g.getClipY();
					clipWidth = g.getClipWidth();
					clipHeight = g.getClipHeight();
					g.clipRect( x, y, this.contentWidth, this.contentHeight );
				}
			//#endif
			//#ifdef polish.css.text-vertical-adjustment
				y += this.textVerticalAdjustment;
			//#endif
			//#ifdef polish.css.font-bitmap
				if (this.bitMapFontViewer != null) {
					//#if polish.css.text-wrap
						if (this.useSingleLine && this.clipText ) {
							x += this.xOffset;
						} else {
					//#endif
							if (this.isLayoutCenter) {
								x = leftBorder + (rightBorder - leftBorder) / 2;
							} else if (this.isLayoutRight) {
								x = rightBorder;
							} 
					//#if polish.css.text-wrap
						}
					//#endif
					//#ifdef polish.css.text-horizontal-adjustment
						x += this.textHorizontalAdjustment;
					//#endif
					this.bitMapFontViewer.paint( x, y, g );
					//#if polish.css.text-wrap
						if (this.useSingleLine && this.clipText ) {
							g.setClip( clipX, clipY, clipWidth, clipHeight );
						}
					//#endif
					return;
				}
			//#endif
				
			//#if polish.Bugs.needsBottomOrientiationForStringDrawing
				y += this.font.getHeight();
			//#endif

			g.setFont( this.font );
			g.setColor( this.textColor );
			
			int lineHeight = getFontHeight() + this.paddingVertical; 
			int centerX = 0;
			if (this.isLayoutCenter) {
				centerX = leftBorder + (rightBorder - leftBorder) / 2;
				//#ifdef polish.css.text-horizontal-adjustment
					centerX += this.textHorizontalAdjustment;
				//#endif
//			} else if (!this.isLayoutRight) {
//				x = leftBorder;
			}
			//#ifdef polish.css.text-horizontal-adjustment
				x += this.textHorizontalAdjustment;
				leftBorder += this.textHorizontalAdjustment;
				rightBorder += this.textHorizontalAdjustment;
			//#endif

			//#if polish.css.text-effect
				if (this.textEffect != null) {
					//#if polish.css.text-wrap
						if (this.useSingleLine && this.clipText ) {
							x += this.xOffset;
							//#if polish.Bugs.needsBottomOrientiationForStringDrawing
								this.textEffect.drawStrings( this, this.textLines, this.textColor, x, y, leftBorder, rightBorder, lineHeight, this.contentWidth, Graphics.LEFT | Graphics.BOTTOM, g );
							//#else
								this.textEffect.drawStrings( this, this.textLines, this.textColor, x, y, leftBorder, rightBorder, lineHeight, this.contentWidth, Graphics.LEFT | Graphics.TOP, g );
							//#endif
						} else {
					//#endif
							this.textEffect.drawStrings( this, this.textLines, this.textColor, x, y, leftBorder, rightBorder, lineHeight, this.contentWidth, this.layout, g );
					//#if polish.css.text-wrap
						}
					//#endif
				} else {
			//#endif
					for (int i = 0; i < this.textLines.length; i++) {
						String line = this.textLines[i];
						int lineX = x;
						int lineY = y;
						int orientation;
						// adjust the painting according to the layout:
						if (this.isLayoutRight) {
							lineX = rightBorder;
							//#if polish.Bugs.needsBottomOrientiationForStringDrawing
								orientation = Graphics.BOTTOM | Graphics.RIGHT;
							//#else
								orientation = Graphics.TOP | Graphics.RIGHT;
							//#endif
							//g.drawString( line, rightBorder, y, Graphics.TOP | Graphics.RIGHT );
						} else if (this.isLayoutCenter) {
							lineX = centerX;
							//#if polish.Bugs.needsBottomOrientiationForStringDrawing
								orientation = Graphics.BOTTOM | Graphics.HCENTER;
							//#else
								orientation = Graphics.TOP | Graphics.HCENTER;
							//#endif
							//g.drawString( line, centerX, y, Graphics.TOP | Graphics.HCENTER );
						} else {
							//#if polish.Bugs.needsBottomOrientiationForStringDrawing
								orientation = Graphics.BOTTOM | Graphics.LEFT;
							//#else
								orientation = Graphics.TOP | Graphics.LEFT;
							//#endif
							// left layout (default)
							//g.drawString( line, x, y, Graphics.TOP | Graphics.LEFT );
						}	
						//#if polish.css.text-wrap
							if (this.clipText) {
								// when clipping (and therefore a scrolling animation) is needed,
								// center and right layouts don't really make sense - this would
								// start and stop the scrolling at wrong places outside of the clipping area: 
								//#if polish.Bugs.needsBottomOrientiationForStringDrawing
									orientation = Graphics.BOTTOM | Graphics.LEFT;
								//#else
									orientation = Graphics.TOP | Graphics.LEFT;
								//#endif
								lineX = x + this.xOffset;
							}
						//#endif
						g.drawString( line, lineX, lineY, orientation );
						x = leftBorder;
						y += lineHeight;
					}
			//#if polish.css.text-effect
				}
			//#endif
			//#if polish.css.text-wrap
				if (this.useSingleLine && this.clipText ) {
					g.setClip( clipX, clipY, clipWidth, clipHeight );
				}
			//#endif
		}
	}
	
	/**
	 * Calculates the width of the given text.
	 * When a bitmap font is used, the calculation is forwarded to it.
	 * When a texteffect is used, the calculation is forwared to it.
	 * In other cases font.stringWidth(text) is returned.
	 * 
	 * @param str the text of which the width should be determined
	 * @return the width of the text
	 */
	public int stringWidth( String str ) {
		//#ifdef polish.css.font-bitmap
			if (this.bitMapFont != null) {
				return this.bitMapFont.stringWidth( str );
			} else {
		//#endif
			//#if polish.css.text-effect
				if (this.textEffect != null) {
					return this.textEffect.stringWidth( str );
				} else {
			//#endif
					return getFont().stringWidth(str);
			//#if polish.css.text-effect
				}
			//#endif
		//#ifdef polish.css.font-bitmap
			}
		//#endif
	}
	/**
	 * Retrieves the height necessary for displaying a row of text without the padding-vertical.
	 * 
	 * @return the font height (either from the bitmap, the text-effect or the font used)
	 */
	public int getFontHeight() {
		//#ifdef polish.css.font-bitmap
			if (this.bitMapFont != null) {
				return this.bitMapFont.getFontHeight();
			} else {
		//#endif
			//#if polish.css.text-effect
				if (this.textEffect != null) {
					return this.textEffect.getFontHeight();
				} else {
			//#endif
					return getFont().getHeight();
			//#if polish.css.text-effect
				}
			//#endif
		//#ifdef polish.css.font-bitmap
			}
		//#endif
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight){
		if (this.text != null && this.font == null) {
			this.font = Font.getDefaultFont();
		}
		if (this.text == null) {
			this.contentHeight = 0;
			this.contentWidth = 0;
			this.textLines = null;
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
				this.bitMapFontViewer = this.bitMapFont.getViewer(
						//#if polish.i18n.rightToLeft
							TextUtil.reverseForRtlLanguage(
						//#endif
								this.text 
						//#if polish.i18n.rightToLeft
							),
							this.textColor
						//#endif
				);
				if (this.bitMapFontViewer != null) {
					//#if polish.css.text-wrap
						if (this.useSingleLine) {
							this.contentHeight = this.bitMapFontViewer.getHeight();
							int width = this.bitMapFontViewer.getWidth();
							if (width > availWidth) {
								this.clipText = true;
								this.textWidth = width;
								width = availWidth;
								this.bitMapFontViewer.setHorizontalOrientation(Graphics.LEFT);
							} else {
								this.bitMapFontViewer.setHorizontalOrientation(orientation);
								this.clipText = false;
							}
							this.contentWidth = width;
						} else {
					//#endif
							// wrap the text:
							this.bitMapFontViewer.layout( firstLineWidth, availWidth, this.paddingVertical, orientation );
							this.contentHeight = this.bitMapFontViewer.getHeight();
							this.contentWidth = this.bitMapFontViewer.getWidth();
					//#if polish.css.text-wrap
						}
					//#endif
					return;
				}
			}
		//#endif
		//#if polish.css.text-wrap
			if ( this.useSingleLine ) {
				this.textLines = new String[]{ 
					//#if polish.i18n.rightToLeft
						TextUtil.reverseForRtlLanguage(
					//#endif
							this.text 
					//#if polish.i18n.rightToLeft
						)
					//#endif
				};
				int myTextWidth = stringWidth(this.text);
				if (myTextWidth > availWidth) {
					this.clipText = true;
					this.textWidth = myTextWidth;
					this.isHorizontalAnimationDirectionRight = false;
					this.contentWidth = availWidth;
					//TODO do this only when no animation should be used
//					int numberOfChars = (this.text.length() * lineWidth) / myTextWidth - 1;
//					if (numberOfChars > 1) {
//						this.textLines = new String[] { this.text.substring( 0, numberOfChars ) + ".." };
//					}
				} else {
					this.clipText = false;
					this.contentWidth = myTextWidth;
				}
				this.contentHeight = getFontHeight();
			} else {
		//#endif
				String[] lines;
				//#ifdef polish.css.text-effect
					if (this.textEffect != null) {
						lines = this.textEffect.wrap( this, this.text, this.textColor,
								this.font, firstLineWidth, availWidth );
					} else {
				//#endif
						lines = TextUtil.wrap(this.text, this.font, firstLineWidth, availWidth);
				//#ifdef polish.css.text-effect
					}
				//#endif
						
				int fontHeight = getFontHeight();
				this.contentHeight = (lines.length * (fontHeight + this.paddingVertical)) - this.paddingVertical;
				int maxWidth = 0;
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					int width = stringWidth(line);
					if (width > maxWidth) {
						maxWidth = width;
					}
					//#if polish.i18n.rightToLeft
						lines[i] =  TextUtil.reverseForRtlLanguage( line );
					//#endif

				}
				this.contentWidth = maxWidth;
				this.textLines = lines;
		//#if polish.css.text-wrap
			}
		//#endif
	}
	
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		// TODO robertvirkus implement setStyle
		super.setStyle(style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		if (resetStyle) {
			this.textColor = style.getFontColor();
			//System.out.println("reset style: color=" + Integer.toHexString(this.textColor) + " for " + this);
			this.font = style.getFont();
		}
		//#ifdef polish.css.font-bitmap
			String bitMapUrl = style.getProperty("font-bitmap");
			if (bitMapUrl != null) {
				//#debug
				System.out.println("getting bitmapfont " + bitMapUrl );
				this.bitMapFont = BitMapFont.getInstance( bitMapUrl );
				//#debug
				System.out.println("bitmap=" + this.bitMapFont + " for " + this );
			} else if (resetStyle){
				this.bitMapFont = null;
				this.bitMapFontViewer = null;
			}
		//#endif
		//#ifdef polish.css.font-color
			Color textColorObj = style.getColorProperty("font-color");
			if ( textColorObj != null ) {
				this.textColor = textColorObj.getColor();
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
			} else if (resetStyle) {
				this.textEffect = null;
			} else if (!resetStyle && this.textEffect != null) {
				this.textEffect.setStyle( style, false );
			}
		//#endif	
		//#ifdef polish.css.text-wrap
			Boolean textWrapBool = style.getBooleanProperty("text-wrap");
			if (textWrapBool != null) {
				this.useSingleLine = !textWrapBool.booleanValue();
			}
			//#if polish.css.text-wrap-animate
				Boolean animateTextWrapBool = style.getBooleanProperty("text-wrap-animate");
				if (animateTextWrapBool != null) {
					this.animateTextWrap = animateTextWrapBool.booleanValue();
				}
			//#endif
			//#ifdef polish.css.text-wrap-animation-direction
				Integer directionInt = style.getIntProperty("text-wrap-animation-direction");
				if (directionInt != null) {
					this.textWrapDirection = directionInt.intValue();
				}
			//#endif
			//#ifdef polish.css.text-wrap-animation-speed
				Integer animationSpeedInt = style.getIntProperty("text-wrap-animation-speed");
				if (animationSpeedInt != null) {
					this.textWrapSpeed = animationSpeedInt.intValue();
				}
			//#endif
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
	
	//#if polish.debug.error || polish.keepToString
	public String toString() {
		return  "StringItem " + super.toString() + ": \"" + this.getText() + "\""; 
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#releaseResources()
	 */
	public void releaseResources() {
		super.releaseResources();
		//#ifdef polish.css.text-effect
			 if ( this.textEffect != null ) {
				 this.textEffect.releaseResources();
			 }
		//#endif
	}

}
