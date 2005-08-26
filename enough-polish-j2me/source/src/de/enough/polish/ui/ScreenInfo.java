//#condition polish.usePolishGui

/*
 * Created on 07-Jun-2005 at 16:20:01.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Can be used for displaying information on each screen. 
 * This needs to be enabled specifically by setting the 
 * "polish.ScreenInfo.enable" preprocessing variable to "true".
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        07-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScreenInfo {
	
	public static IconItem item;
	private static boolean visible = true;
	private static int itemY;
	private static int itemX;
	private static boolean positionSet;
	
	static {
		//#style screeninfo, default
		item = new IconItem( null, null );
	}

	/**
	 * No instantiation allowed 
	 */
	private ScreenInfo() {
		super();
	}
	
	public static void setVisible( boolean isVisible ) {
		visible = isVisible;
	}
	
	public static boolean isVisible() {
		return visible;
	}
	
	private static void repaint() {
		if (StyleSheet.display != null) {
			Displayable displayable = StyleSheet.display.getCurrent();
			if (displayable != null && displayable instanceof Canvas) {
				((Canvas) displayable).repaint();
			}
		}		
	}
	
	public static void setText( String text ) {
		item.setText( text );
		repaint();
	}
	
	public static void setImage( Image image ) {
		item.setImage( image );
		repaint();
	}

	public static void setPosition( int x, int y ) {
		itemX = x;
		itemY = y;
		positionSet = true;
		repaint();
	}
	
	public static void setBackground( Background background ) {
		item.background = background;
		repaint();
	}
	
	public static void setFontColor( int color ) {
		item.textColor = color;
		repaint();
	}

	
	public static void paint( Graphics g, int titleHeight, int screenWidth ) {
		if (!visible) {
			return;
		}
		if ( positionSet ) {
			item.paint( itemX, itemY, itemX, screenWidth, g );
		} else {
			item.paint( 0, titleHeight, 0, screenWidth, g );
		}
	}
}
