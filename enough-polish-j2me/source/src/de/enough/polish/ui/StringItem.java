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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.TextUtil;

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
	private static final int DIRECTION_BACK_AND_FORTH = 0;
	private static final int DIRECTION_LEFT = 1;
	private static final int DIRECTION_RIGHT = 2;
	protected String text;
	protected String[] textLines;
	protected int textColor;
	protected Font font;
	//#ifdef polish.css.text-wrap
		protected boolean useSingleLine;
		protected boolean clipText;
		protected int xOffset;
		protected int textWidth;
		protected boolean isHorizontalAnimationDirectionRight;
		protected boolean animateTextWrap = true;
		protected int availableTextWidth;
		//#ifdef polish.css.text-wrap-animation-direction
			protected int textWrapDirection = DIRECTION_BACK_AND_FORTH;
		//#endif
		//#ifdef polish.css.text-wrap-animation-speed
			protected int textWrapSpeed = 1;
		//#endif
	//#endif
	//#ifdef polish.css.text-horizontal-adjustment
		protected int textHorizontalAdjustment;
	//#endif
	//#ifdef polish.css.text-vertical-adjustment
		protected int textVerticalAdjustment;
	//#endif
	//#if polish.css.text-effect || polish.css.font-bitmap
		//#define tmp.useTextEffect
		protected TextEffect textEffect;
	//#endif
	//#ifdef polish.css.max-lines
		protected int maxLines = TextUtil.MAXLINES_UNLIMITED;
	//#endif
	//#if polish.css.text-layout
		private int textLayout;
	//#endif
	//#if polish.css.text-visible
		private boolean isTextVisible = true;
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
	
	
	
	//#if tmp.useTextEffect
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
								if (this.xOffset + this.textWidth < this.availableTextWidth) {
									this.isHorizontalAnimationDirectionRight = true;
								}
							}
					//#ifdef polish.css.text-wrap-animation-direction
						} else if (this.textWrapDirection == DIRECTION_LEFT) {
							int offset = this.xOffset - speed;
							if (offset + this.textWidth < 0) {
								offset = this.availableTextWidth;
							}
							this.xOffset = offset;
						} else if (this.textWrapDirection == DIRECTION_RIGHT) { // direction is right:
							int offset = this.xOffset + speed;
							if (offset > this.availableTextWidth) {
								offset = -this.textWidth;
							}
							this.xOffset = offset;							
						} 
					//#endif

					addRelativeToContentRegion(repaintRegion, 0, 0, this.contentWidth, this.contentHeight );
				}
			}
		//#endif
		//#if tmp.useTextEffect
			if (this.textEffect != null) {
				this.textEffect.animate( this, currentTime, repaintRegion );
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


	//#ifdef tmp.useTextEffect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#hideNotify()
	 */
	protected void hideNotify() {
		if (this.textEffect != null) {
			this.textEffect.onDetach(this);
			this.textEffect.hideNotify();
		}
		super.hideNotify();
	}
	//#endif

	//#ifdef tmp.useTextEffect
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showNotify()
	 */
	protected void showNotify() {
		if (this.textEffect != null) {
			this.textEffect.onAttach(this);
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
		if (text != this.text) {
			this.text = text;
			if (text == null) {
				this.textLines = null;
			}
			requestInit();
		}
	}
	
	//#if tmp.useTextEffect
	public void setTextEffect(TextEffect effect)
	{
		this.textEffect = effect;
	}
	//#endif
	
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
		//#debug
		System.out.println("painting at left=" + leftBorder + ", right=" + rightBorder + ", x=" + x + ", text=" + this.text);
		//#if polish.css.text-visible
			if (!this.isTextVisible) {
				return;
			}
		//#endif
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
					g.clipRect( x, y, this.availableTextWidth, this.contentHeight );
				}
			//#endif
			//#ifdef polish.css.text-vertical-adjustment
				y += this.textVerticalAdjustment;
			//#endif
				
			//#if polish.Bugs.needsBottomOrientiationForStringDrawing
				y += getFontHeight();
			//#endif

			g.setFont( this.font );
			g.setColor( this.textColor );
			
			int lineHeight = getFontHeight() + this.paddingVertical; 
			//#ifdef polish.css.text-horizontal-adjustment
				x += this.textHorizontalAdjustment;
				leftBorder += this.textHorizontalAdjustment;
				rightBorder += this.textHorizontalAdjustment;
			//#endif

			//#if tmp.useTextEffect
				if (this.textEffect != null) {
					//#if polish.css.text-wrap
						if (this.useSingleLine && this.clipText ) {
							x += this.xOffset;
						}
					//#endif
					int lo;
					//#if polish.css.text-layout
						lo = this.textLayout & ~Item.LAYOUT_VCENTER;
					//#else
						lo = this.layout & ~Item.LAYOUT_VCENTER;
					//#endif
					//#if polish.Bugs.needsBottomOrientiationForStringDrawing
						this.textEffect.drawStrings( this, this.textLines, this.textColor, x, y, leftBorder, rightBorder, lineHeight, this.contentWidth, lo	 | Item.LAYOUT_BOTTOM, g );
					//#else
						this.textEffect.drawStrings( this, this.textLines, this.textColor, x, y, leftBorder, rightBorder, lineHeight, this.contentWidth, lo | Item.LAYOUT_TOP, g );
					//#endif
				} else {
			//#endif
					int centerX = 0;
					boolean isCenter;
					boolean isRight;
					//#if polish.css.text-layout
						int lo = this.textLayout;
						isCenter = (lo & LAYOUT_CENTER) == LAYOUT_CENTER;
						isRight = (lo & LAYOUT_CENTER) == LAYOUT_RIGHT;
					//#else
						isCenter = this.isLayoutCenter;
						isRight = this.isLayoutRight;
					//#endif
					
					if (isCenter) {
						centerX = leftBorder + (rightBorder - leftBorder) / 2;
						//#ifdef polish.css.text-horizontal-adjustment
							centerX += this.textHorizontalAdjustment;
						//#endif
					}
					int lineX = x;
					int lineY = y;
					int orientation;
					// adjust the painting according to the layout:
					if (isRight) {
						lineX = rightBorder;
						orientation = Graphics.RIGHT;
					} else if (isCenter) {
						lineX = centerX;
						orientation = Graphics.HCENTER;
					} else {
						orientation = Graphics.LEFT;
					}
					
					//#if polish.css.text-wrap
						if (this.clipText) {
							// when clipping (and therefore a scrolling animation) is needed,
							// center and right layouts don't really make sense - this would
							// start and stop the scrolling at wrong places outside of the clipping area: 
							orientation = Graphics.LEFT;
							lineX = x + this.xOffset;
						}
					//#endif
					//#if polish.Bugs.needsBottomOrientiationForStringDrawing
						orientation = Graphics.BOTTOM | orientation;
					//#else
						orientation = Graphics.TOP | orientation;
					//#endif
					for (int i = 0; i < this.textLines.length; i++) {
						String line = this.textLines[i];
						g.drawString( line, lineX, lineY, orientation );
						lineY += lineHeight;
					}
			//#if tmp.useTextEffect
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
		//#if tmp.useTextEffect
			if (this.textEffect != null) {
				return this.textEffect.stringWidth( str );
			} else {
		//#endif
				return getFont().stringWidth(str);
		//#if tmp.useTextEffect
			}
		//#endif
	}
	
	/**
	 * Retrieves the width of the given char
	 * @param c the char
	 * @return the width of that char
	 */
	public int charWidth( char c) {
		return this.font.charWidth(c);
	}
	
	/**
	 * Retrieves the height necessary for displaying a row of text without the padding-vertical.
	 * 
	 * @return the font height (either from the bitmap, the text-effect or the font used)
	 */
	public int getFontHeight() {
		//#if tmp.useTextEffect
			if (this.textEffect != null) {
				return this.textEffect.getFontHeight();
			} else {
		//#endif
				return getFont().getHeight();
		//#if tmp.useTextEffect
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight){
		//#debug
		System.out.println("initContent for " + this.text);
		String body = this.text;
		if (body != null && this.font == null) {
			this.font = Font.getDefaultFont();
		}
		if ((body == null)
			//#if polish.css.text-visible
				|| !this.isTextVisible
			//#endif
		) {
			this.contentHeight = 0;
			this.contentWidth = 0;
			this.textLines = null;
			return;
		}

		//#if polish.css.text-wrap
			if ( this.useSingleLine ) {
				this.availableTextWidth = availWidth;
				this.textLines = new String[]{ 
					//#if polish.i18n.rightToLeft
						TextUtil.reverseForRtlLanguage(
					//#endif
							body 
					//#if polish.i18n.rightToLeft
						)
					//#endif
				};
				int myTextWidth = stringWidth(body);
				if (myTextWidth > availWidth) {
					this.clipText = true;
					this.textWidth = myTextWidth;
					this.isHorizontalAnimationDirectionRight = false;
					this.contentWidth = availWidth;
				} else {
					this.clipText = false;
					this.contentWidth = myTextWidth;
				}
				this.contentHeight = getFontHeight();
			} else {
		//#endif
				String[] lines = wrap(body, firstLineWidth, availWidth);
						
				int fontHeight = getFontHeight();
				this.contentHeight = (lines.length * (fontHeight + this.paddingVertical)) - this.paddingVertical;
				int maxWidth = 0;
				//#ifdef tmp.useTextEffect
				if(this.textEffect != null)
				{
					maxWidth = this.textEffect.getMaxWidth(this,lines);
				}
				else
				{
				//#endif
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
				//#ifdef tmp.useTextEffect
				}
				//#endif
				this.contentWidth = maxWidth;
				this.textLines = lines;
		//#if polish.css.text-wrap
			}
		//#endif
	}
	
	String[] wrap(String body, int firstLineWidth, int availWidth)
	{
		String[] result;
		//#ifdef tmp.useTextEffect
		if (this.textEffect != null) {
			result = this.textEffect.wrap( this, body, this.textColor, this.font, firstLineWidth, availWidth );
		} else {
		//#endif
			//#ifdef polish.css.max-lines
				result = TextUtil.wrap(body, this.font, firstLineWidth, availWidth, this.maxLines);
			//#else
				result = TextUtil.wrap(body, this.font, firstLineWidth, availWidth);
			//#endif
		//#ifdef tmp.useTextEffect
			}
		//#endif
		return result;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		//#ifdef tmp.useTextEffect
			TextEffect effect = (TextEffect) style.getObjectProperty( "text-effect" );
			if (effect != null)  {
				if (this.textEffect == null || this.textEffect.getClass() != effect.getClass()) {
					if (effect.isTextSensitive) {
						try
						{
							effect = (TextEffect) effect.getClass().newInstance();
						} catch (Exception e)
						{
							//#debug error
							System.out.println("Unable to copy effect " + effect + e);
						}
					}
					this.textEffect = effect;
				} else {
					effect = this.textEffect;
				}
				effect.setStyle(style);
			} else {
				this.textEffect = null;
			}
		//#endif
		//#if polish.css.text-layout
			Integer layoutInt = style.getIntProperty("text-layout");
			if (layoutInt != null) {
				this.textLayout = layoutInt.intValue();
			} else {
				this.textLayout = style.layout;
			}
		//#endif 
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
		// reset useSingleLine:
		//#if polish.css.text-wrap
			Boolean textWrapBool = style.getBooleanProperty("text-wrap");
			if (textWrapBool != null) {
				this.useSingleLine = !textWrapBool.booleanValue();
			} else if (resetStyle) {
				this.useSingleLine = false;
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
		//#ifdef tmp.useTextEffect
			if (!resetStyle && this.textEffect != null) {
				this.textEffect.setStyle( style, false );
			}
		//#endif
		//#ifdef polish.css.max-lines
			Integer maxLinesInt = style.getIntProperty("max-lines");
			if (maxLinesInt != null) {
				this.maxLines = maxLinesInt.intValue();
			}
		//#endif
		//#if polish.css.text-visible
			Boolean textVisibleBool = style.getBooleanProperty("text-visible");
			if (textVisibleBool != null) {
				this.isTextVisible = textVisibleBool.booleanValue();
			} else {
				this.isTextVisible = true;
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
		//#ifdef tmp.useTextEffect
			 if ( this.textEffect != null ) {
				 this.textEffect.releaseResources();
			 }
		//#endif
	}

}
