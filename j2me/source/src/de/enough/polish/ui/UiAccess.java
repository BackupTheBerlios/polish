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
import javax.microedition.lcdui.Display;

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
	 * No instantiation is allowd.
	 */
	private UiAccess() {
		super();
	}
	
	//#if polish.usePolishGui
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
	
	/**
	 * Retrieves the focused index of the specified screen
	 * 
	 * @param screen the screen
	 * @return the focused index, -1 when it is not known
	 */
	public static int getFocusedIndex( javax.microedition.lcdui.Screen screen ) {
		return -1;
	}

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

	//#if polish.usePolishGui
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
	//#endif

	//#if polish.usePolishGui
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
	//#endif

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
	
	//#if polish.usePolishGui
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

	/**
	 * Sets the parent for the given child item.
	 * 
	 * @param child the child
	 * @param parent the parent
	 */
	public static void setParent( javax.microedition.lcdui.Item child, javax.microedition.lcdui.Item parent ) {
		// ignore
	}
	
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
	//#if polish.usePolishGui
	public static boolean handleKeyPressed( Item item, int keyCode, int gameAction ) {
		return item.handleKeyPressed(keyCode, gameAction);
	}
	//#endif
	
	//#if polish.usePolishGui
	public static boolean handlePointerPressed( Item item, int x, int y ) {
		//#if polish.hasPointerEvents
			return item.handlePointerPressed(x, y);
		//#else
			//# return false;
		//#endif
	}
	//#endif
	
	
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

	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextField field, int inputMode ) {
		// ignore
	}
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textfield.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextField.
	 * 
	 * @param field the text field 
	 * @param inputMode the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static void setInputMode( TextField field, int inputMode ) {
		field.setInputMode( inputMode ); 
	}
	//#endif

	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static void setInputMode( javax.microedition.lcdui.TextBox box, int inputMode ) {
		// ignore
	}
	
	//#if polish.usePolishGui
	/**
	 * Sets the input mode for the given textbox.
	 * Warning: you have to ensure that the input mode matches the contraints of
	 * the given TextBox.
	 * 
	 * @param box the text box 
	 * @param inputMode the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static void setInputMode( TextBox box, int inputMode ) {
		box.textField.setInputMode( inputMode ); 
	}
	//#endif
	
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextField field ) {
		return -1;
	}
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textfield.
	 * 
	 * @param field the text field 
	 * @return the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static int getInputMode( TextField field ) {
		return field.inputMode; 
	}
	//#endif

	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static int getInputMode( javax.microedition.lcdui.TextBox box ) {
		return -1;
	}
	
	//#if polish.usePolishGui
	/**
	 * Retrieves the input mode for the given textbox.
	 * 
	 * @param box the text box 
	 * @return the input mode, either UiAcccess.MODE_NUMBERS, UiAcccess.MODE_LOWERCASE, UiAcccess.MODE_UPPERCASE or UiAcccess.MODE_FIRST_UPPERCASE 
	 * @see #MODE_NUMBERS
	 * @see #MODE_LOWERCASE
	 * @see #MODE_UPPERCASE
	 * @see #MODE_FIRST_UPPERCASE
	 */
	public static int getInputMode( TextBox box ) {
		return box.textField.inputMode; 
	}
	//#endif
	
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
	
	//#if polish.usePolishGui
	/**
	 * Adds the given command as a subcommand to the defined screen. When the J2ME Polish GUI is not used, this will just add the command to the screen like a normal command.
	 * 
	 * @param child the sub command
	 * @param parent the parent command
	 * @param screen the screen.
	 */
	public static void addSubCommand(  Command child, Command parent, Screen screen  ) {
		//#style menuitem?
		addSubCommand( child, parent, screen );
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

    /**
     * @param form
     * @param item
     */
    public static void focus( javax.microedition.lcdui.Form form, javax.microedition.lcdui.Item item ) {
        // ignore
    }

    //#if polish.usePolishGui
    public static void focus( Form form, Item item ) {
        form.focus( item );
    }
    //#endif

}
