//#condition polish.midp || polish.usePolishGui
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
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.HashMap;

/**
 * <p>Allows to access J2ME Polish specific features in a standard compliant way.</p>
 * <p>When a ScreenStateListener is registered with a screen, it will get notified when
 *    the screen changes its focus or another internal state (like changing a tab in the TabbedForm).
 * </p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        31-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class UiAccess {
	
	/**
	 * A constant for setting the input mode of an TextField to lowercase.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_LOWERCASE = 0;
	/**
	 * A constant for setting the input mode of an TextField to uppercase for the first character, followed by lowercase characters.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_FIRST_UPPERCASE = 1; // only the first character should be written in uppercase
	/**
	 * A constant for setting the input mode of an TextField to uppercase.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_UPPERCASE = 2;
	/**
	 * A constant for setting the input mode of an TextField to numbers.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_NUMBERS = 3;
	/**
	 * A constant for setting the input mode of an TextField to it't native input - that's useful for using T9 and similar input helpers.
	 * @see #setInputMode(javax.microedition.lcdui.TextField, int)
	 */
	public static final int MODE_NATIVE = 4;
	private static HashMap attributes;

	/**
	 * No instantiation is allowd.
	 */
	private UiAccess() {
		super();
	}
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( javax.microedition.lcdui.Screen screen, ScreenStateListener listener ) {
		// ignore, just for being able to use the ScreenStateListener along with a normal screen.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Registers a ScreenStateListener to any J2ME Polish screen.
	 * 
	 * @param screen the screen
	 * @param listener the listener
	 */
	public static void setScreenStateListener( Screen screen, ScreenStateListener listener ) {
		screen.setScreenStateListener( listener );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.usePolishGui
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
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused item, null when it is not known
	 */
	public static javax.microedition.lcdui.Item getFocusedItem( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused item, null when it is not known
	 */
	public static Item getFocusedItem( Screen screen ) {
		return screen.getCurrentItem();
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @param index the focused index, -1 when none should be focused
	 */
	public static void setFocusedIndex( javax.microedition.lcdui.Screen screen, int index ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @param index the focused index, -1 when none should be focused
	 */
	public static void setFocusedIndex( Screen screen, int index ) {
		screen.focus( index );
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the focused item of the specified screen
	 * 
	 * @param screen the screen
	 * @param item the focused item, null when none should be focused
	 */
	public static void setFocusedItem( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focused item of the specified screen
	 * 
	 * @param screen the screen
	 * @param item the focused item, null when none should be focused
	 */
	public static void setFocusedItem( Screen screen, Item item ) {
		screen.focus(item);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item title ) {
		// this is ignored.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 */
	public static void setTitle( Screen screen, Item title ) {
		screen.setTitle( title );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * This method is meant to be used in conjunction with the //#style preprocessing directive.
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @param style the style for the title
	 */
	public static void setTitle( Screen screen, Item title, Style style ) {
		if (style != null) {
			title.setStyle(style);
		}
		screen.setTitle( title );
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, Item title ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the title of the screen using an Item.
	 * <b>important</b>: you cannot call screen.setTitle(String) afterwards anymore!
	 * This method is meant to be used in conjunction with the //#style preprocessing directive.
	 * 
	 * @param screen the screen for which the title should be replaced 
	 * @param title the element responsible for painting the title.
	 * @param style the style for the title
	 */
	public static void setTitle( javax.microedition.lcdui.Screen screen, Item title, Style style ) {
		// ignore
	}
	//#endif


	//#if polish.midp
	/**
	 * Sets the fullscreen mode of the screen.
	 * The title and the menubar will be hidden by this call.
	 * 
	 * @param screen the screen
	 * @param fullScreen true when the fullscreen mode should be entered
	 */
	public static void setFullScreenMode( javax.microedition.lcdui.Screen screen, boolean fullScreen ) {
		// this is ignored.
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the fullscreen mode of the screen.
	 * The title and the menubar will be hidden by this call.
	 * 
	 * @param screen the screen
	 * @param fullScreen true when the fullscreen mode should be entered
	 */
	public static void setFullScreenMode( Screen screen, boolean fullScreen ) {
		screen.setFullScreenMode(fullScreen);
	}
	//#endif

	
	//#if polish.midp
	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public static void addItemCommand( javax.microedition.lcdui.List list, int index, Command command ) {
		// ignore on real lists
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds a command to a list item.
	 * Warning: this method won't add any commands when the J2ME Polish GUI is not activated.
	 * 
	 * @param list the list
	 * @param index the index of the item
	 * @param command the item command
	 */
	public static void addItemCommand( List list, int index, Command command ) {
		Item item = list.getItem(index);
		item.addCommand(command);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( Item child, Item parent ) {
		child.parent = parent;
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( Item child, javax.microedition.lcdui.Item parent ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( javax.microedition.lcdui.Item child, javax.microedition.lcdui.Item parent ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the internal x position of the given item.
	 * When it is equal -9999 this item's internal position is not known.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal x position of this item's currently selected content, -9999 when it is unknown.
	 */
	public static int getInternalX( javax.microedition.lcdui.Item item ) {
		return -9999;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the internal y position of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal y position of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalY( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the internal width of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal width of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalWidth( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the internal height of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal height of this item's currently selected content.
	 * @see #getInternalX(javax.microedition.lcdui.Item)
	 */
	public static int getInternalHeight( javax.microedition.lcdui.Item item ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the internal x position of the given item.
	 * When it is equal -9999 this item's internal position is not known.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal x position of this item's currently selected content, -9999 when it is unknown.
	 */
	public static int getInternalX( Item item ) {
		return item.internalX;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the internal y position of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal y position of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalY( Item item ) {
		return item.internalY;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the internal width of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal width of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalWidth( Item item ) {
		return item.internalWidth;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves the internal height of the given item.
	 * The internal position is useful for items that have a large content which
	 * needs to be scrolled, e.g. containers.  
	 * 
	 * @param item the item
	 * @return the internal height of this item's currently selected content.
	 * @see #getInternalX(Item)
	 */
	public static int getInternalHeight( Item item ) {
		return item.internalHeight;
	}
	//#endif
	
	//#if polish.usePolishGui
	public static Style focus( Item item, int direction, Style style ) {
		return item.focus(style, direction);
	}
	//#endif
	
	//#if polish.usePolishGui
	public static void defocus( Item item, Style style ) {
		item.defocus(style);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the focus to the given index of the specified list.
	 * When the list is not shown, it will be shown in this call.
	 * When the J2ME Polish GUI is not used, only the list will be shown.
	 * 
	 * @param display the display 
	 * @param list the list
	 * @param index the index
	 */
	public static void setCurrentListIndex( Display display, javax.microedition.lcdui.List list, int index ) {
		//#if !polish.blackberry && polish.usePolishGui
			display.setCurrent( list );
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the focus to the given index of the specified list.
	 * When the list is not shown, it will be shown in this call.
	 * When the J2ME Polish GUI is not used, only the list will be shown.
	 * 
	 * @param display the display 
	 * @param list the list
	 * @param index the index
	 */
	public static void setCurrentListIndex( Display display, List list, int index ) {
		Item item = list.getItem( index );
		item.show( display );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setListItem( List list, int index, ChoiceItem item ) {
		list.set(index, item);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getListItem( List list, int index ) {
		return list.getItem(index);
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param item the new item
	 */
	public static void appendListItem( List list, ChoiceItem item ) {
		list.append( item );
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be exchanged
	 * @param item the new item
	 */
	public static void setListItem( javax.microedition.lcdui.List list, int index, ChoiceItem item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param index the index of the item that should be returned
	 * @return the item at the given position
	 */
	public static ChoiceItem getListItem( javax.microedition.lcdui.List list, int index ) {
		return null;
	}
	//#endif

	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Sets a ChoiceItem or a subclass for the given list. 
	 * @param list the list
	 * @param item the new item
	 */
	public static void appendListItem( javax.microedition.lcdui.List list, ChoiceItem item ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Applies a style to the given item if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myItem );
	 * </pre>
	 * @param item the item which should get the new style
	 */
	public static void setStyle( javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif

	//#if polish.midp
	/**
	 * Applies a style to the given screen if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myScreen );
	 * </pre>
	 * @param screen the screen which should get the new style
	 */
	public static void setStyle( javax.microedition.lcdui.Screen screen ) {
		// ignore
	}
	//#endif


	//#if polish.usePolishGui
	/**
	 * Applies a style to the given item if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myItem );
	 * </pre>
	 * @param item the item which should get the new style
	 */
	public static void setStyle( Item item ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Applies a style to the given screen if used in conjunction with the //#style preprocessing directive.
	 * 
	 * Example:
	 * <pre>
	 * //#style myStyle
	 * UiAccess.setStyle( myScreen );
	 * </pre>
	 * @param screen the screen which should get the new style
	 */
	public static void setStyle( Screen screen ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Applies a style to the given item.
	 * 
	 * @param item the item which should get the new style
	 * @param style the style
	 */
	public static void setStyle( Item item, Style style ) {
		item.setStyle( style );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Applies a style to the given screen.
	 * 
	 * @param screen the screen which should get the new style
	 * @param style the style
	 */
	public static void setStyle( Screen screen, Style style ) {
		screen.setStyle( style );
	}
	//#endif	

	//#if polish.midp
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 */
	public static void setStyle(javax.microedition.lcdui.List list, int itemIndex) {
		// ignore
		
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 */
	public static void setStyle(List list, int itemIndex) {
		// ignore
		
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 * @param style the new style of the item
	 */
	public static void setStyle(List list, int itemIndex, Style style) {
		Item item = list.getItem(itemIndex);
		item.setStyle(style);
	}
	//#endif	

	//#if polish.usePolishGui && polish.midp
	/**
	 * Gets the current style of the given item.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myItem );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param item the item of which the style should be retrieved
   * @return the style of the item
	 */
	public static Style getStyle( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif	

	//#if polish.usePolishGui && polish.midp
	/**
	 * Gets the current style of the given screen.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myScreen );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param screen the screen of which the style should be retrieved
   * @return the style of the screen
	 */
	public static Style getStyle( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Gets the current style of the given item.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myItem );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param item the item of which the style should be retrieved
   * @return the style of the item
	 */
	public static Style getStyle( Item item ) {
		return item.style;
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Gets the current style of the given screen.
	 * 
	 * Example:
	 * <pre>
	 * //#if polish.usePolishGui
	 * 	Style style = UiAccess.getStyle( myScreen );
	 * 	if (style != null) }
	 * 		style.background = new SimpleBackground( 0x00FF00 );
	 *  }
	 * //#endif
	 * </pre>
	 * Note: this method is only available when the J2ME Polish GUI is used, so you better check for the polish.usePolishGui prepocessing symbol.
   * 
	 * @param screen the screen of which the style should be retrieved
   * @return the style of the screen
	 */
	public static Style getStyle( Screen screen ) {
		return screen.style;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyPressed( Item item, int keyCode, int gameAction ) {
		return item.handleKeyPressed(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyPressed( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyReleased call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyReleased( Item item, int keyCode, int gameAction ) {
		return item.handleKeyReleased(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyReleased( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	
	
	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyRepeated call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyRepeated( Item item, int keyCode, int gameAction ) {
		return item.handleKeyRepeated(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Forwards a key event to the specified item.
	 * The handleKeyPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param keyCode the code of the key
	 * @param gameAction the associated game action
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handleKeyRepeated( javax.microedition.lcdui.Item item, int keyCode, int gameAction ) {
		return false;
	}
	//#endif
	

	//#if polish.usePolishGui
	/**
	 * Forwards a key event to the specified item.
	 * The handlePointerPressed call is protected, this is an public accessor for any item.
	 * 
	 * @param item the item 
	 * @param x the x position of the pointer pressing relative to this item's left position
	 * @param y the y position of the pointer pressing relative to this item's top position
	 * @return true when the event has been consumed by the item
	 */
	public static boolean handlePointerPressed( Item item, int x, int y ) {
		//#if polish.hasPointerEvents
			return item.handlePointerPressed(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif

	//#if polish.midp
	public static boolean handlePointerPressed( javax.microedition.lcdui.Item item, int x, int y ) {
		return false;
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Sets the caret position in the given text field.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param field the text field 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( javax.microedition.lcdui.TextField field, int position ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the caret position in the given text field.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param field the text field 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( TextField field, int position ) {
		field.setCaretPosition( position ); 
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the caret position in the given text box.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param box the text box 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( javax.microedition.lcdui.TextBox box, int position ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the caret position in the given text box.
	 * Please note that this operation requires the direct input mode to work.
	 * 
	 * @param box the text box 
	 * @param position the new caret position,  0 puts the caret at the start of the line, getString().length moves the caret to the end of the input.
	 */
	public static void setCaretPosition( TextBox box, int position ) {
		box.textField.setCaretPosition( position ); 
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextField field, int inputMode ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( TextField field, int inputMode ) {
		field.setInputMode( inputMode ); 
	}
	//#endif

	//#if polish.midp
	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextBox box, int inputMode ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASEor UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static void setInputMode( TextBox box, int inputMode ) {
		box.textField.setInputMode( inputMode ); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASEor UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextField field ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( TextField field ) {
		return field.inputMode; 
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextBox box ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAccess.MODE_NUMBERS, UiAcccss.MODE_LOWERCASE, UiAccess.MODE_UPPERCASE, UiAccess.MODE_FIRST_UPPERCASE or UiAccess.MODE_NATIVE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 * @see #MODE_NATIVE
	 */
	public static int getInputMode( TextBox box ) {
		return box.textField.inputMode; 
	}
	//#endif
	
	
	//#if polish.midp
	/**
	 * Retrieves the decimal value entered with a dot as the decimal mark.
	 * <ul>
	 * <li>When the value has no decimal places it will be returned as it is: 12</li>
	 * <li>When the value is null, null will be returned: null</li>
	 * <li>When the value has decimal places, a dot will be used: 12.3</li>
	 * </ul>
	 * When the J2ME Polish GUI is not used, this method will only detect commas as possible
	 * alternative decimal marks.
	 * 
	 * @param field the text field with a DECIMAL constraint
	 * @return either the formatted value or null, when there was no input.
	 * @throws IllegalStateException when the TextField is not DECIMAL constrained
	 */
	public static String getDotSeparatedDecimalString( javax.microedition.lcdui.TextField field ) {
		//#if polish.midp2
		if (( field.getConstraints() & javax.microedition.lcdui.TextField.DECIMAL)!= javax.microedition.lcdui.TextField.DECIMAL) {
			throw new IllegalStateException();
		}
		//#endif
		String value = field.getString();
		if (value == null) {
			return null;
		}
		return value.replace(',', '.');
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the decimal value entered with a dot as the decimal mark.
	 * <ul>
	 * <li>When the value has no decimal places it will be returned as it is: 12</li>
	 * <li>When the value is null, null will be returned: null</li>
	 * <li>When the value has decimal places, a dot will be used: 12.3</li>
	 * </ul>
	 * @param field the text field with a DECIMAL constraint
	 * @return either the formatted value or null, when there was no input.
	 * @throws IllegalStateException when the TextField is not DECIMAL constrained
	 */
	public static String getDotSeparatedDecimalString( TextField field ) {
		return field.getDotSeparatedDecimalString(); 
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  Command child, Command parent, javax.microedition.lcdui.Screen screen  ) {
		//#if !polish.blackberry
		screen.addCommand( child );
		//#endif
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  Command child, Command parent, Screen screen  ) {
		screen.addSubCommand( child, parent );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 * @param style the style of the command
	 */
	public static void addSubCommand(  Command child, Command parent, Screen screen, Style style  ) {
		screen.addSubCommand( child, parent, style );
	}
	//#endif

	//#if polish.midp
	/**
	 * Removes all commands from the given screen
	 * This option is only available when the "menu" fullscreen mode is activated.
	 * 
	 * @param screen the screen.
	 */
	public static void removeAllCommands(  javax.microedition.lcdui.Screen screen ) {
		// ignore
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Removes all commands from the given screen
	 * This option is only available when the "menu" fullscreen mode is activated.
	 * 
	 * @param screen the screen.
	 */
	public static void removeAllCommands(  Screen screen ) {
		screen.removeAllCommands();
	}
	//#endif

	
	//#if polish.midp
	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @param screen the screen
	 * @return true when the commands menu is opened.
	 */
	public static boolean isMenuOpened( javax.microedition.lcdui.Screen screen ) {
		return false;
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @param screen the screen
	 * @return true when the commands menu is opened.
	 */
	public static boolean isMenuOpened( Screen screen ) {
		return screen.isMenuOpened();
	}
	//#endif

	//#if polish.midp
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item item ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param item the item that should be focused
     */
    public static void focus( Screen screen, Item item ) {
        screen.focus( item );
    }
    //#endif
    
	//#if polish.midp
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( javax.microedition.lcdui.Screen screen, int index ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given screen.
     * 
     * @param screen the screen
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( Screen screen, int index ) {
        screen.focus( index );
    }
    //#endif

	//#if polish.midp
    /**
     * Focuses the specified item on the given choice group.
     * 
     * @param choiceGroup the choice group
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( javax.microedition.lcdui.ChoiceGroup choiceGroup, int index ) {
        // ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Focuses the specified item on the given choice group.
     * 
     * @param choiceGroup the choice group
     * @param index the index of the item that should be focused, first item has the index 0
     */
    public static void focus( ChoiceGroup choiceGroup, int index ) {
    	if (!choiceGroup.isFocused) {
    		Screen screen = choiceGroup.getScreen();
    		if (screen != null) {
    			screen.focus(choiceGroup);
    		}
    	}
        choiceGroup.focus( index );
    }
    //#endif

    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI.
     */
    public static void releaseResources() {
        //#if polish.usePolishGui
    		StyleSheet.releaseResources();
    		Displayable displayable = StyleSheet.display.getCurrent();
    		if ( displayable instanceof Screen ) {
    			((Screen)displayable).releaseResources();
    		}
        //#endif    	
    }
    
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI when a non-J2ME Polish screen is shown.
     */
    public static void releaseResourcesOnScreenChange() {
        //#if polish.usePolishGui
    		AnimationThread.releaseResourcesOnScreenChange = true;
        //#endif    	
    }

    
    //#if polish.usePolishGui
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI for the specified screen.
     * 
     * @param screen the screen for which the resources should be released. 
     */
    public static void releaseResources( Screen screen ) {
    		screen.releaseResources();
    }
    //#endif

	//#if polish.midp
    /**
     * Releases all (memory) instensive resources that are currently hold by the J2ME Polish GUI for the specified screen.
     * 
     * @param screen the screen for which the resources should be released. 
     */
    public static void releaseResources( javax.microedition.lcdui.Screen screen ) {
    		// ignore
    }
    //#endif
    
	//#if polish.midp
    public static void setSubtitle( javax.microedition.lcdui.Screen screen, String subtitle ) {
    		// ignore
    }
    //#endif

    //#if polish.usePolishGui
    public static void setSubtitle( Screen screen, String subtitle ) {
    		setSubtitle( screen, new StringItem(null, subtitle));
    }
    //#endif

	//#if polish.midp
    public static void setSubtitle( javax.microedition.lcdui.Screen screen, javax.microedition.lcdui.Item subtitle ) {
    		// ignore
    }
    //#endif

    //#if polish.usePolishGui
    public static void setSubtitle( Screen screen, Item subtitle ) {
    		screen.setSubTitle(subtitle);
    }
    //#endif
    
	//#if polish.midp
    /**
     * Scrolls the screen to the given position.
     * 
     * @param screen the screen
     * @param yOffset the vertical offset: 0 is the very top, negative values scroll the screen towards the end.
     */
    public static void scroll( javax.microedition.lcdui.Screen screen, int yOffset ) {
    	// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Scrolls the screen to the given position.
     * 
     * @param screen the screen
     * @param yOffset the vertical offset: 0 is the very top, negative values scroll the screen towards the end.
     */
    public static void scroll( Screen screen, int yOffset ) {
    	Container container = screen.container;
    	if (container != null) {
    		container.yOffset = yOffset;
    		container.targetYOffset = yOffset;
    	}
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Retrieves the background of the given screen.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( form );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param screen the screen
     * @return the background for the screen
     */
    public static Background getBackground( Screen screen ) {
    	return screen.background;
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Retrieves the background of the given screen.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( form );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param screen the screen
     * @return the background for the screen
     */
    public static Background getBackground( javax.microedition.lcdui.Screen screen ) {
    	return null;
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the background for the given screen.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param screen the screen
     * @param background - the new background
     */
    public static void setBackground( Screen screen, Background background ) {
    	screen.background = background;
    	screen.repaint();
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Sets the background for the given screen.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param screen the screen
     * @param background - the new background
     */
    public static void setBackground( javax.microedition.lcdui.Screen screen, Background background ) {
    	// ignore
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Retrieves the background of the given item.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( item );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param item the item
     * @return the background for the item
     */
    public static Background getBackground( Item item ) {
    	return item.background;
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Retrieves the background of the given item.
     * This can be used to dynamically alter the background, e.g. by setting a different image:
     * <pre>
     * //#if polish.usePolishGui
     *   ImageBackground ib = (ImageBackground) UiAccess.getBackground( item );
     *   ib.setImage( newImage );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * 
     * @param item the item
     * @return the background for the item
     */
    public static Background getBackground(  javax.microedition.lcdui.Item item ) {
    	return null;
    }
    //#endif

    //#if polish.usePolishGui
    /**
     * Sets the background for the given item.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param item the item
     * @param background - the new background
     */
    public static void setBackground( Item item, Background background ) {
    	item.background = background;
    	item.repaint();
    }
    //#endif

    //#if polish.usePolishGui && polish.midp
    /**
     * Sets the background for the given item.
     * This can be used to dynamically alter the background:
     * <pre>
     * //#if polish.usePolishGui
     *   SimpleBackground bg = new SimpleBackground( 0x00FF00 );
     *   UiAccess.setBackground( item, screen );
     * //#endif
     * </pre>
     * Note: this method is only available when the J2ME Polish GUI is used! Check for the preprocessing symbold polish.usePolishGui.
     * @param item the item
     * @param background - the new background
     */
    public static void setBackground( javax.microedition.lcdui.Item item, Background background ) {
    	// ignore
    }
    //#endif

	//#if polish.midp
    /**
     * Retrieves the command listener belonging to this screen.
     * 
     * @param screen the screen 
     * @return the associated command listener, always null for javax.microedition.lcdui.Screen objects that have not been converted to J2ME Polish components
     */
	public static CommandListener getCommandListener( javax.microedition.lcdui.Screen screen ) {
		return null;
	}
	//#endif


    //#if polish.usePolishGui
    /**
     * Retrieves the command listener belonging to this screen.
     * 
     * @param screen the screen 
     * @return the associated command listener, always null for javax.microedition.lcdui.Screen objects that have not been converted to J2ME Polish components
     */
	public static CommandListener getCommandListener( Screen screen ) {
		return screen.getCommandListener();
	}
    //#endif

	
    //#if polish.usePolishGui
	/**
	 * Sets an arbitrary attribute for the given item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( Item item, Object key, Object value ) {
		item.setAttribute( key, value );
	}
    //#endif
	
    //#if polish.usePolishGui
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( Item item, Object key ) {
		return item.getAttribute( key );
	}
    //#endif
	
	//#if polish.usePolishGui
  /**
   * Returns a HashMap object with all registered attributes.
   * 
   * @param item the item from which the attributes should be retrieved
   * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
   */
  public static HashMap getAttributes( Item item ) {
	  return item.getAttributes();
  }
  //#endif

  //#if polish.usePolishGui
	/**
	 * Sets an arbitrary attribute for the specified list item.
	 * 
   * @param list a list of items
	 * @param index the index of the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( List list, int index, Object key, Object value ) {
		Item item = list.getItem(index);
		item.setAttribute( key, value );
	}
  //#endif
	
  //#if polish.usePolishGui
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
   * @param list a list of items
	 * @param index the index of item from which the attribute should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( List list, int index, Object key ) {
		Item item = list.getItem( index );
		return item.getAttribute( key );
	}
  //#endif
	
	//#if polish.usePolishGui
	/**
	 * Returns a HashMap object with all registered attributes.
	 * 
   * @param list a list of items
	 * @param index the index of item from which the attributes should be retrieved
	 * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
	 */
	public static HashMap getAttributes( List list, int index ) {
		Item item = list.getItem(index);
		return item.getAttributes();
	}
	//#endif
	
	//#if polish.midp
  /**
	 * Sets an arbitrary attribute for the given item.
	 * 
	 * @param item the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( javax.microedition.lcdui.Item item, Object key, Object value ) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			itemAttributes = new HashMap();
			attributes.put( item, itemAttributes );
		}
		itemAttributes.put( key, value );
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
	 * @param item the item from which the attribute should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( javax.microedition.lcdui.Item item, Object key ) {
		if ( attributes == null ) {
			return null;
		}
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			return null;
		}
		return itemAttributes.get( key );
	}
	//#endif
  
	//#if polish.midp
  /**
   * Returns a HashMap object with all registered attributes.
   * 
   * @param item the item from which the attributes should be retrieved
   * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
   */
  public static HashMap getAttributes( javax.microedition.lcdui.Item item ) {
    if ( attributes == null ) {
      return null;
    }
    return (HashMap) attributes.get( item );
  }
  //#endif
  
//#if polish.midp
	/**
	 * Sets an arbitrary attribute for the specified list item.
	 * 
   * @param list a list of items
	 * @param index the index of the item to which the attribute should be added
	 * @param key the key for the attribute
	 * @param value the attribute value
	 */
	public static void setAttribute( javax.microedition.lcdui.List list, int index, Object key, Object value ) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		String item = list.toString() + index;
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			itemAttributes = new HashMap();
			attributes.put( item, itemAttributes );
		}
		itemAttributes.put( key, value );
	}
//#endif
	
//#if polish.midp
	/**
	 * Gets an previously added attribute of the specified item.
	 * 
   * @param list a list of items
   * @param index the index of item from which the attributes should be retrieved
	 * @param key the key of the attribute
	 * @return the attribute value, null if none has been registered under the given key before
	 */
	public static Object getAttribute( javax.microedition.lcdui.List list, int index, Object key ) {
		if ( attributes == null ) {
			return null;
		}
		String item = list.toString() + index;
		HashMap itemAttributes = (HashMap) attributes.get( item );
		if (itemAttributes == null) {
			return null;
		}
		return itemAttributes.get( key );
	}
//#endif
	
	//#if polish.midp
	/**
	 * Returns a HashMap object with all registered attributes.
	 * 
   * @param list a list of items
	 * @param index the index of the item from which the attributes should be retrieved
	 * @return a HashMap object with all attribute key/value pairs, null if no attribute was stored before.
	 */
	public static HashMap getAttributes( javax.microedition.lcdui.List list, int index ) {
	    if ( attributes == null ) {
	        return null;
	      }
	    String item = list.toString() + index;
	    return (HashMap) attributes.get( item );
	}
	//#endif

	//#if polish.midp
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( javax.microedition.lcdui.Item item, boolean isAccessible ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * You can set a new style at the same time by adding a style directive:
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( Item item, boolean isAccessible ) {
		if (isAccessible) {
			item.setAppearanceMode( Item.INTERACTIVE );
		} else {
			item.setAppearanceMode( Item.PLAIN );
		}
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * You can set a new style at the same time by adding a style directive:
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myItem, false );
	 * </pre>
	 * 
	 * @param item the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style, is ignored when it is null
	 */
	public static void setAccessible( Item item, boolean isAccessible, Style style ) {
		if (!isAccessible && item.isFocused) {
			// first defocus item:
			Item parent = item.parent;
			if (parent instanceof Container) {
				((Container)parent).focus(-1);
			}
		}
		if (style != null) {
			item.setStyle(style);
		}
		if (isAccessible) {
			item.setAppearanceMode( Item.INTERACTIVE );
		} else {
			item.setAppearanceMode( Item.PLAIN );
		}
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 */
	public static void setAccessible(javax.microedition.lcdui.List list, int itemIndex, boolean isAccessible) {
		// ignore
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible(List list, int itemIndex, boolean isAccessible) {
		setAccessible(list.getItem(itemIndex), isAccessible);
	}
	//#endif	

	//#if polish.usePolishGui
	/**
	 * Applies a style to the specified list item.
	 * 
	 * @param list the list 
	 * @param itemIndex the index of the list
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style of the item
	 */
	public static void setAccessible(List list, int itemIndex, boolean isAccessible, Style style) {
		setAccessible( list.getItem(itemIndex), isAccessible, style );
	}
	//#endif	
	
	//#if polish.midp
	/**
	 * Makes the command interactive (accessible) or non-interactive.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( javax.microedition.lcdui.Screen screen, Command command, boolean isAccessible ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 */
	public static void setAccessible( Screen screen, Command command, boolean isAccessible ) {
		setAccessible( screen.getCommandItem(command), isAccessible );
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Makes the item interactive (accessible) or non-interactive.
	 * <pre>
	 * //#style inactiveStyle
	 * UiAccess.setAccessible( myScreen, myCommand, false );
	 * </pre>
	 * 
	 * @param screen the screen that contains the command
	 * @param command the item that should be made accessible 
	 * @param isAccessible true when the item should be accessible/selectable
	 * @param style the new style for the command, is ignored when null
	 */
	public static void setAccessible( Screen screen, Command command, boolean isAccessible, Style style ) {
		setAccessible( screen.getCommandItem(command), isAccessible, style );

	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
	 * @param ticker the ticker item which will the image be set 
	 * @param image that image that will be set to the ticker
	 */
	public static void setTickerImage( javax.microedition.lcdui.Ticker ticker, Image image ) {
		// ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
	 * @param ticker the ticker item which will the image be set 
	 * @param image that image that will be set to the ticker
	 */
	public static void setTickerImage( Ticker ticker, Image image ) {
		ticker.setImage(image);
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Sets an image for the specified ticker.
	 * This method is ignored when the J2ME Polish UI is not activated.
	 * 
   * @param ticker the ticker item which will the image be set 
   * @param image that image that will be set to the ticker
	 */
	public static void setAccessible( Ticker ticker, Image image ) {
		ticker.setImage(image);
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Attaches data to the specified screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which the data should be stored
	 * @param data the screen specific data
	 * @see #getData(Screen)
	 */
	public static void setData(Screen screen, Object data) {
		screen.setScreenData( data );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which data has been previously stored using UiAccess.setData()
	 * @return any screen specific data or null when no data has been attached before
	 * @see #setData(Screen, Object)
	 */
	public static Object getData(Screen screen) {
		return screen.getScreenData();
	}
	//#endif

	//#if polish.midp
	/**
	 * Attaches data to the specified screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which the data should be stored
	 * @param data the screen specific data
	 * @see #getData(Screen)
	 */
	public static void setData( javax.microedition.lcdui.Screen screen, Object data) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		attributes.put(screen, data);
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param screen the screen in which data has been previously stored using UiAccess.setData()
	 * @return any screen specific data or null when no data has been attached before
	 * @see #setData(Screen, Object)
	 */
	public static Object getData(javax.microedition.lcdui.Screen screen) {
		if (attributes == null) {
			return null;
		}
		return attributes.get( screen );
	}
	//#endif

	//#if polish.midp
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param screen the screen that contains the command
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( javax.microedition.lcdui.Screen screen, Command command, String label) {
		//ignore
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Changes the shown label of the specified command.
	 * Note that command.getLabel() will afterwards retrieve the same string as before, 
	 * only the shown label will be changed. You cannot change the labels of the
	 * commands that are shown on the left or right side of the menu, unless the extended menubar is activated 
	 * (polish.MenuBar.useExtendedMenuBar=true).
	 * This call is ignored when J2ME Polish does not render the menu.
	 *
	 * @param screen the screen that contains the command
	 * @param command the command
	 * @param label the new label that should be shown
	 */
	public static void setCommandLabel( Screen screen, Command command, String label) {
		CommandItem item = screen.getCommandItem(command);
		//#debug
		System.out.println("setting text=" + label + " for previous " + command.getLabel() + "=" + item );
		if (item != null) {
			item.setText(label);
		}
	}
	//#endif

	//#if polish.midp
	/**
	 * Adds a ticker as a normal item to the specified form.
	 * 
	 * @param ticker the ticker 
	 * @param form  the form
	 * @return the index of the ticker within the form, -1 when the J2ME Polish UI is not used.
	 */
	public static int append( javax.microedition.lcdui.Ticker ticker, javax.microedition.lcdui.Form form ) {
		return -1;
	}
	//#endif


	//#if polish.usePolishGui and polish.midp
	/**
	 * Adds the specified (J2ME Polish) item to the given form.
	 * This can be used for example to add several tickers to a form.
	 * 
	 * @param item the item  
	 * @param form  the form
	 * @return the index of the item within the form, -1 when the J2ME Polish UI is not used.
	 */
	public static int append( Item item, javax.microedition.lcdui.Form form ) {
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui
	/**
	 * Adds the specified (J2ME Polish) item to the given form.
	 * 
	 * @param item the item  
	 * @param form  the form
	 * @return the index of the item within the form
	 */
	public static int append( Item item, Form form ) {
		return form.append(item);
	}
	//#endif


	//#if polish.usePolishGui
	/**
	 * Gets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param item the item that might be invisible
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( Item item ) {
		//#if polish.supportInvisibleItems
			return !item.isInvisible;
		//#else
			//# return true; 
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Gets the visible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( List list, int index ) {
		return isVisible( list.getItem( index ) );
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the invisible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param item the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( Item item, boolean visible ) {
		//#if polish.supportInvisibleItems
			item.setVisible( visible );
		//#endif
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets the invisible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( List list, int index, boolean visible ) {
		setVisible( list.getItem(index), visible );
	}
	//#endif
	

	//#if polish.midp
	/**
	 * Gets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true (and the J2ME Polish UI is used).
	 * 
	 * @param item the item that might be invisible
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( javax.microedition.lcdui.Item item ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Gets the visible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @return true when this item is visible.
	 */
	public static boolean isVisible( javax.microedition.lcdui.List list, int index ) {
		return false;
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the visible status of the specified item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true (and the J2ME Polish UI is used).
	 * 
	 * @param item the item
	 * @param visible true when the item should be visible.
	 */
	public static void setVisible( javax.microedition.lcdui.Item item, boolean visible ) {
		// ignore
	}
	//#endif
	
	//#if polish.midp
	/**
	 * Sets the invisible status of the specified list-item.
	 * Invisible items occupy no space on the UI screen and cannot be focused/traversed. 
	 * Invisible items are only supported when the preprocessing variable polish.supportInvisibleItems is true.
	 * 
	 * @param list the list 
	 * @param index the index of the item
	 * @param visible true when the item should become invisible.
	 */
	public static void setVisible( javax.microedition.lcdui.List list, int index, boolean visible ) {
		// ignore
	}
	//#endif

	
	//#if polish.usePolishGui
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( Item item, Screen screen ) {
		if ( screen.container != null ) {
			return screen.container.indexOf(item);
		}
		return -1;
	}
	//#endif
	
	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( Item item, javax.microedition.lcdui.Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.usePolishGui && polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( javax.microedition.lcdui.Item item, Screen screen ) {
		return -1;
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the index of the specified item in the screen.
	 * 
	 * @param item the item
	 * @param screen the screen
	 * @return the index of the item; -1 when the item is not part of the given screen
	 */
	public static int indexOf( javax.microedition.lcdui.Item item, javax.microedition.lcdui.Screen screen ) {
		if (screen instanceof javax.microedition.lcdui.Form) {
			javax.microedition.lcdui.Form form = (javax.microedition.lcdui.Form) screen;
			int size = form.size();
			for (int i = 0; i < size; i++) {
				if (form.get(i) == item) {
					return i;
				}
			}
		}
		return -1;
	}
	//#endif
	
	//#if polish.midp
	public static int[] getRgbData( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif

	//#if polish.usePolishGui
	public static int[] getRgbData( Item item ) {
		//#if polish.midp2
			Image image = Image.createImage( item.itemWidth, item.itemHeight );
			int transparentColor = 0x12345678;
			Graphics g = image.getGraphics();
			g.setColor(transparentColor);
			g.fillRect(0, 0, item.itemWidth, item.itemHeight );
			int[] transparentColorRgb = new int[1];
			image.getRGB(transparentColorRgb, 0, 1, 0, 0, 1, 1 );
			transparentColor = transparentColorRgb[0];
			item.paint( 0, 0, 0, item.itemWidth, g );
			int[] itemRgbData = new int[  item.itemWidth * item.itemHeight ];
			image.getRGB(itemRgbData, 0, item.itemWidth, 0, 0, item.itemWidth, item.itemHeight );
			// ensure transparent parts are indeed transparent
			for (int i = 0; i < itemRgbData.length; i++) {
				if( itemRgbData[i] == transparentColor ) {
					itemRgbData[i] = 0;
				}
			}
			return itemRgbData;
		//#else
			//# return null;
		//#endif
	}
	//#endif

	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @return an int array containing the RGB data of that item
	 */
	public static int[] getRgbDataOfContent( javax.microedition.lcdui.Item item ) {
		return null;
	}
	//#endif

	
 	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @return an int array containing the RGB data of that item (null on MIDP 1.0 devices)
	 */
	public static int[] getRgbDataOfContent( Item item ) {
		//#if polish.midp2
			Image image = Image.createImage( item.contentWidth, item.contentHeight );
			int transparentColor = 0x12345678;
			Graphics g = image.getGraphics();
			g.setColor(transparentColor);
			g.fillRect(0, 0, item.contentWidth, item.contentHeight );
			int[] transparentColorRgb = new int[1];
			image.getRGB(transparentColorRgb, 0, 1, 0, 0, 1, 1 );
			transparentColor = transparentColorRgb[0];
			item.paintContent( 0, 0, 0, item.contentWidth, g );
			int[] itemRgbData = new int[  item.contentWidth * item.contentHeight ];
			image.getRGB(itemRgbData, 0, item.contentWidth, 0, 0, item.contentWidth, item.contentHeight );
			// ensure transparent parts are indeed transparent
			for (int i = 0; i < itemRgbData.length; i++) {
				if( itemRgbData[i] == transparentColor ) {
					itemRgbData[i] = 0;
				}
			}
			return itemRgbData;
		//#else
			//# return null;
		//#endif
	}
	//#endif
	
 	//#if polish.usePolishGui
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @param rgbData an int array in which the RGB data of the item's content is written
	 * @param x the horizontal start position for the RGB data
	 * @param y the vertical start position for the RGB data
	 * @param width the width of a single row in the rgbData
	 */
	public static void getRgbDataOfContent(Item item, int[] rgbData, int x, int y, int width) {
		//#if polish.midp2
			int contentWidth = item.contentWidth;
			int contentHeight = item.contentHeight;
			Image image = Image.createImage( contentWidth, contentHeight );
			int transparentColor = 0x12345678;
			Graphics g = image.getGraphics();
			g.setColor(transparentColor);
			g.fillRect(0, 0, contentWidth, contentHeight );
			int[] transparentColorRgb = new int[1];
			image.getRGB(transparentColorRgb, 0, 1, 0, 0, 1, 1 );
			transparentColor = transparentColorRgb[0];
			item.paintContent( 0, 0, 0, contentWidth, g );
			int[] itemRgbData = new int[  contentWidth * contentHeight ];
			image.getRGB(itemRgbData, 0, contentWidth, 0, 0, contentWidth, contentHeight );
			// ensure transparent parts are indeed transparent and copy to target array:
			for (int row = 0; row < contentHeight; row++ ) {
				for (int column = 0; column < contentWidth; column++ ) {
					int index = row * contentWidth + column;
					int pixel = itemRgbData[index];
					if ( pixel == transparentColor ) {
						pixel = 0;
					}
					int target = y * width + row * width + x + column;
					rgbData[ target ] = pixel;
				}
			}
		//#endif		
	}
	//#endif
	
 	//#if polish.midp
	/**
	 * Retrieves the RGB data of the specified item's content without using a view-type/ItemView.
	 * This is often used by ItemView that want to change the original RGB data.
	 * In ItemView please ensure to call initContentByParent( item ) before using this function.
	 * 
	 * @param item the item
	 * @param rgbData an int array in which the RGB data of the item's content is written
	 * @param x the horizontal start position for the RGB data
	 * @param y the vertical start position for the RGB data
	 * @param width the width of a single row in the rgbData
	 */
	public static void getRgbDataOfContent(javax.microedition.lcdui.Item item, int[] rgbData, int x, int y, int width) {
		// ignore
	}
	//#endif


	//#if polish.TextField.useDirectInput && !polish.blackberry && polish.midp && polish.TextField.usePredictiveInput
	public static void setPredictiveDictionary(javax.microedition.lcdui.TextField field, String[] words)
	{
		// ignore
	}
	//#endif

 	//#if polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput
	public static void setPredictiveDictionary(TextField field, String[] words)
	{
			PredictiveAccess predictive = field.getPredictiveAccess();
			predictive.initPredictiveInput(words);
			
			field.setString("");
			predictive.synchronize();
			
			if(words == null)
			{
				predictive.setPredictiveType(PredictiveAccess.TRIE);
			}
			else
			{
				predictive.setPredictiveType(PredictiveAccess.ARRAY);
			}
	}
	//#endif

	//#if polish.usePolishGui
	/**
	 * Sets an ItemStateListener specifically for this item.
	 * Change events are forwarded to both this listener as well as a possibly set listener of the
	 * corresponding screen.
	 * 
	 * @param item the item
	 * @param listener the listener
	 */
	public static void setItemStateListener(Item item, ItemStateListener listener) {
		item.setItemStateListener( listener );
		
	}
	//#endif

	//#if polish.midp2
	/**
	 * Sets an ItemStateListener specifically for this item.
	 * Change events are forwarded to both this listener as well as a possibly set listener of the
	 * corresponding screen.
	 * 
	 * @param item the item
	 * @param listener the listener
	 */
	public static void setItemStateListener(javax.microedition.lcdui.Item item, javax.microedition.lcdui.ItemStateListener listener) {
		// ignore
		
	}
	//#endif

}
