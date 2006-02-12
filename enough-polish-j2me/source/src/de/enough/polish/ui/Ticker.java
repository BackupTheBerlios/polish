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
 * Implements a &quot;ticker-tape&quot;, a piece of text that runs
 * continuously across the display. The direction and speed of scrolling are
 * determined by the implementation. While animating, the ticker string
 * scrolls continuously. That is, when the string finishes scrolling off the
 * display, the ticker starts over at the beginning of the string.
 * 
 * <p> There is no API provided for starting and stopping the ticker. The
 * application model is that the ticker is always scrolling continuously.
 * However, the implementation is allowed to pause the scrolling for power
 * consumption purposes, for example, if the user doesn't interact with the
 * device for a certain period of time. The implementation should resume
 * scrolling the ticker when the user interacts with the device again. </p>
 * 
 * <p>The text of the ticker may contain
 * <A HREF="Form.html#linebreak">line breaks</A>.
 * The complete text MUST be displayed in the ticker;
 * line break characters should not be displayed but may be used
 * as separators. </p>
 * 
 * <p> The same ticker may be shared by several <code>Displayable</code>
 * objects (&quot;screens&quot;). This can be accomplished by calling
 * <A HREF="../../../javax/microedition/lcdui/Displayable.html#setTicker(javax.microedition.lcdui.Ticker)"><CODE>setTicker()</CODE></A> on each of them.
 * Typical usage is for an application to place the same ticker on
 * all of its screens. When the application switches between two screens that
 * have the same ticker, a desirable effect is for the ticker to be displayed
 * at the same location on the display and to continue scrolling its contents
 * at the same position. This gives the illusion of the ticker being attached
 * to the display instead of to each screen. </p>
 * 
 * <p> An alternative usage model is for the application to use different
 * tickers on different sets of screens or even a different one on each
 * screen. The ticker is an attribute of the <code>Displayable</code> class
 * so that
 * applications may implement this model without having to update the ticker
 * to be displayed as the user switches among screens. </p>
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class Ticker extends Item
{
	private int xOffset;
	private String chunk;
	private int chunkIndex;
	private int chunkWidth;
	private String[] chunks;
	private String text;
	private Font font;
	private int textColor;
	private int step = 2;
	//#if polish.css.ticker-left-clip
		private int tickerLeftClip;
	//#endif
	//#if polish.css.ticker-right-clip
		private int tickerRightClip;
	//#endif

	/**
	 * Constructs a new <code>Ticker</code> object, given its initial
	 * contents string.
	 * 
	 * @param str string to be set for the Ticker
	 * @throws NullPointerException if str is null
	 */
	public Ticker( String str)
	{
		this( str, null );
	}

	/**
	 * Constructs a new <code>Ticker</code> object, given its initial
	 * contents string.
	 * 
	 * @param str string to be set for the Ticker
	 * @param style the CSS style for this item
	 * @throws NullPointerException if str is null
	 */
	public Ticker( String str, Style style )
	{
		super( style );
		setString( str );
	}
	
	public String getString() {
		return this.text;
	}
	
	public void setString( String text ) {
		this.text = text;
		this.chunks = TextUtil.split( text, '\n');
		this.chunk = this.chunks[0];
		this.isInitialised = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.font == null) {
			this.font = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL );
		}
		this.chunkWidth = this.font.stringWidth( this.chunk );
		this.contentWidth = firstLineWidth;
		this.contentHeight = this.font.getHeight();
		this.xOffset = - firstLineWidth;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		x -= this.xOffset;
		//System.out.println("painting ticker at " + x);
		//#if polish.css.ticker-left-clip || polish.css.ticker-right-clip
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipHeight = g.getClipHeight();
			int clipWidth = g.getClipWidth();
			int width = (rightBorder - leftBorder);
		//#endif
		//#if polish.css.ticker-left-clip && polish.css.ticker-right-clip
			g.clipRect( x + this.tickerLeftClip, clipY, width - (this.tickerLeftClip + this.tickerRightClip), clipHeight);
		//#elif polish.css.ticker-left-clip
			g.clipRect( x + this.tickerLeftClip, clipY, width - this.tickerLeftClip, clipHeight);
		//#elif polish.css.ticker-right-clip
			g.clipRect( x, clipY, width - this.tickerRightClip, clipHeight);
		//#endif
		g.setColor( this.textColor );
		g.setFont( this.font );
		g.drawString( this.chunk, x, y, Graphics.TOP | Graphics.LEFT );
		//#if polish.css.ticker-left-clip || polish.css.ticker-right-clip
			g.setClip(clipX, clipY, clipWidth, clipHeight);
		//#endif
	}

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "ticker";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		this.font = style.font;
		this.textColor = style.fontColor;
		//#ifdef polish.css.ticker-step
			Integer stepInt = style.getIntProperty("ticker-step");
			if (stepInt != null) {
				this.step = stepInt.intValue();
			}
		//#endif
		//#if polish.css.ticker-left-clip
			Integer tickerLeftClipInt = style.getIntProperty("ticker-left-clip");
			if (tickerLeftClipInt != null) {
				this.tickerLeftClip = tickerLeftClipInt.intValue(); 
			}
		//#endif
		//#if polish.css.ticker-right-clip
			Integer tickerRightClipInt = style.getIntProperty("ticker-right-clip");
			if (tickerRightClipInt != null) {
				this.tickerRightClip = tickerRightClipInt.intValue(); 
			}
		//#endif
		super.setStyle(style);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate()
	 */
	public boolean animate() {
		if (this.xOffset < this.chunkWidth) {
			this.xOffset += this.step;
			//System.out.println("changing offset");
		} else {
			//System.out.println("changing chunk: xOffset=" + this.xOffset + " chunkWidth=" + this.chunkWidth );
			this.xOffset = -this.contentWidth;
			this.chunkIndex++;
			if ( this.chunkIndex >= this.chunks.length  ) {
				this.chunkIndex = 0;
			}
			this.chunk = this.chunks[ this.chunkIndex ];
			this.chunkWidth = this.font.stringWidth( this.chunk );
		}
		return true;
	}
}
