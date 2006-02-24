//#condition polish.usePolishGui
/*
 * Created on 31-Jan-2006 at 00:04:45.
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

import javax.microedition.lcdui.Command;

/**
 * <p>Allows to access J2ME Polish specific features in a standard compliant way.</p>
 * <p>When a ScreenStateListener is registered with a screen, it will get notified when
 *    the screen changes its focus or another internal state (like changing a tab in the TabbedForm).
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        31-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class UiAccess {

	/**
	 * No instantiation is allowd.
	 */
	private UiAccess() {
		super();
	}
	
	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( javax.microedition.lcdui.Screen screen, ScreenStateListener listener ) {
		// ignore, just for being able to use the ScreenStateListener along with a normal screen.
	}

	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( Screen screen, ScreenStateListener listener ) {
		screen.screenStateListener = listener;
	}
	
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}

	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( Screen screen ) {
		if (screen.container != null) {
			return screen.container.getFocusedIndex();
		}
		return -1;
	}

	//#if polish.midp2
	/**
	 * Sets the title of the screen using a CustomItem.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.CustomItem title ) {
		// this is ignored.
	}
	//#endif

	/**
	 * Sets the title of the screen using a CustomItem.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 */
	public static void setTitle( Screen screen, CustomItem title ) {
		screen.setTitle( title );
	}

	/**
	 * Sets the title of the screen using a CustomItem.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @param style the style for the title
	 */
	public static void setTitle( Screen screen, CustomItem title, Style style ) {
		if (style != null) {
			title.setStyle(style);
		}
		screen.setTitle( title );
	}

	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public void addItemCommand( javax.microedition.lcdui.List list, int index, Command command ) {
		// ignore on real lists
	}
	
	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public void addItemCommand( List list, int index, Command command ) {
		Item item = list.getItem(index);
		item.addCommand(command);
	}
}
