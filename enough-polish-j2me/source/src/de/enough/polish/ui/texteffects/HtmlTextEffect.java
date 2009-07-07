//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;

/**
 * Allows to use simple HTML markup for the design of the text.
 * @author Robert Virkus
 *
 */
public class HtmlTextEffect extends TextEffect {

	private Midp2ContainerView midp2View;
	private Item[] textItems;


	/**
	 * Creates a new HTML text effect
	 */
	public HtmlTextEffect() {
		// use style for further initialization, if required.
		this.isTextSensitive = true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(java.lang.String[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(String[] textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g) 
	{
		//System.out.println("drawStrings: " + this.midp2View + " at x=" + x + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder);
		if (this.midp2View == null) {
			super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
		} else {
			//#if polish.Bugs.needsBottomOrientiationForStringDrawing
				y -= this.midp2View.getContentHeight();
			//#endif
			if ((layout & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER) {
				x += ((rightBorder - leftBorder) - this.midp2View.getContentWidth()) / 2;
			} else if ((layout & Item.LAYOUT_CENTER) == Item.LAYOUT_RIGHT) {
				x += ((rightBorder - leftBorder) - this.midp2View.getContentWidth());				
			}
			this.midp2View.paintContent( this.textItems, x, y, leftBorder, rightBorder, g );
		}
	}





	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(java.lang.String, int, javax.microedition.lcdui.Font, int, int)
	 */
	public String[] wrap(String htmlText, int textColor, Font font,
			int firstLineWidth, int lineWidth) 
	{
		XmlDomNode root = XmlDomParser.parseTree( "<r>" + htmlText + "</r>" );
		ArrayList childList = new ArrayList();
		Style baseStyle = this.style.clone(true);
		baseStyle.removeAttribute("text-effect");
		baseStyle.layout = Item.LAYOUT_LEFT;
		baseStyle.background = null;
		baseStyle.border = null;
		
		addNode( root, baseStyle, childList );
		Item[] items = (Item[]) childList.toArray( new Item[childList.size()]);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			item.getItemWidth( lineWidth, lineWidth, -1);
		}
		Midp2ContainerView view = new Midp2ContainerView();
		view.initContent(items, firstLineWidth, lineWidth, -1);
		this.textItems = items;
		this.midp2View = view;
		//System.out.println("WRAPPING RESULT: items=" + items.length + ", view=" + view.getContentWidth() + " X " + view.getContentHeight());
		return new String[]{""};
	}



	private void addNode(XmlDomNode node, Style nodeStyle, ArrayList childList) {
		String name = node.getName().toLowerCase();
		int addedFontStyle = -1;
		if ("b".equals(name)) {
			addedFontStyle = Font.STYLE_BOLD;
		} else if ("i".equals(name)) {
			addedFontStyle = Font.STYLE_ITALIC;
		}
		String styleName = node.getAttribute("class");
		if (styleName == null) {
			node.getAttribute("id");
		}
		if (styleName != null) {
			Style nextNodeStyle = StyleSheet.getStyle(styleName);
			if (nextNodeStyle != null) {
				nodeStyle = nextNodeStyle;
			}
		}
		if (addedFontStyle != -1) {
			nodeStyle = nodeStyle.clone(true);
			Integer fontStyle = nodeStyle.getIntProperty("font-style");
			nodeStyle.addAttribute("font-style", addToFontStyle( addedFontStyle, fontStyle ) );
		}
		String text = node.getText();
		if (text != null) {
			addText( text, nodeStyle, childList );
		}
		for (int i=0; i<node.getChildCount(); i++) {
			XmlDomNode child = node.getChild(i);
			addNode( child, nodeStyle, childList );
		}

	}


	private Object addToFontStyle(int styleSetting, Integer fontStyle) {
		if (fontStyle == null) {
			return new Integer( styleSetting );
		}
		return new Integer( fontStyle.intValue() | styleSetting );
	}


	private void addText(String text, Style textStyle, ArrayList childList) {
		
		String[] texts = TextUtil.split(text, ' ');
		for (int i = 0; i < texts.length; i++) {
			String chunk = texts[i];
			childList.add( new StringItem( null, chunk, textStyle));
		}
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int anchor, Graphics g) 
	{
		// just in case no font is defined:
		g.drawString( text, x, y, anchor );
		

	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getFontHeight()
	 */
	public int getFontHeight() {
		if (this.midp2View == null) {
			return super.getFontHeight();
		} else {
			return this.midp2View.getContentHeight();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#stringWidth(java.lang.String)
	 */
	public int stringWidth(String str) {
		if (this.midp2View == null) {
			wrap( str, 0, getFont(), Integer.MAX_VALUE, Integer.MAX_VALUE );
		} 
		return this.midp2View.getContentWidth();
	}


	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getMaxWidth(de.enough.polish.ui.Item, java.lang.String[])
	 */
	public int getMaxWidth(Item parent, String[] lines) {
		if (this.midp2View == null) {
			return super.getMaxWidth(parent, lines);
		} else {
			return this.midp2View.getContentWidth();
		}
	}

	
	

}
