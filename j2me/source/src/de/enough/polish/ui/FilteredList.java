//#condition polish.usePolishGui
/*
 * Created on Jun 21, 2007 at 11:28:35 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;

/**
 * <p>Displays a list of choices that can be limited by the user by entering some input.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jun 21, 2007 - michael creation
 * </pre>
 * @author Michael Koch
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FilteredList 
extends List
implements ItemStateListener, CommandListener
{
	
	private static final int POSITION_TOP = 0;
	private static final int POSITION_BOTTOM = 1;
	private static final int FILTER_STARTS_WITH = 0;
	private static final int FILTER_INDEX_OF = 1;
	protected int filterPosition = POSITION_BOTTOM;
	protected int filterMode = FILTER_STARTS_WITH;
	protected final TextField filterTextField;
	private final ArrayList itemsList;
	private CommandListener originalCommandListener;

	/**
	 * @param title
	 * @param listType
	 */
	public FilteredList(String title, int listType) {
		this( title, listType, (ChoiceItem[])null, (Style) null );
	}
	
	/**
	 * @param title
	 * @param listType
	 * @param style
	 */
	public FilteredList(String title, int listType, Style style) {
		this( title, listType, (ChoiceItem[])null, style );
	}

	/**
	 * @param title
	 * @param listType
	 * @param stringElements
	 * @param imageElements
	 */
	public FilteredList(String title, int listType, String[] stringElements, Image[] imageElements) {
		this( title, listType, stringElements, imageElements, null );
	}


	/**
	 * @param title
	 * @param listType
	 * @param stringElements
	 * @param imageElements
	 * @param style
	 */
	public FilteredList(String title, int listType, String[] stringElements, Image[] imageElements, Style style) {
		this( title, listType, ChoiceGroup.buildChoiceItems(stringElements, imageElements, listType, style), style );
	}


	/**
	 * @param title
	 * @param listType
	 * @param items
	 */
	public FilteredList(String title, int listType, ChoiceItem[] items) {
		this(title, listType, items, null);
	}


	
	/**
	 * @param title
	 * @param listType
	 * @param items
	 * @param style
	 */
	public FilteredList(String title, int listType, ChoiceItem[] items, Style style) {
		super(title, listType, items, style);
		//#style filterTextField?
		this.filterTextField = new TextField( null, "", 30, TextField.ANY );
		this.filterTextField.screen = this;
		setItemStateListener( this );
		this.itemsList = new ArrayList();
		super.setCommandListener( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = this.filterTextField.handleKeyPressed(keyCode, gameAction);
		if (!handled) {
			handled = super.handleKeyPressed(keyCode, gameAction);
		}
		return handled;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		boolean handled = this.filterTextField.handleKeyReleased(keyCode, gameAction);
		if (!handled) {
			handled = super.handleKeyReleased(keyCode, gameAction);
		}
		return handled;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		boolean handled = this.filterTextField.handleKeyRepeated(keyCode, gameAction);
		if (!handled) {
			handled = super.handleKeyRepeated(keyCode, gameAction);
		}
		return handled;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		animated |= this.filterTextField.animate();
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#calculateContentArea(int, int, int, int)
	 */
	protected void calculateContentArea(int x, int y, int width, int height) {
		super.calculateContentArea(x, y, width, height);
		int filterHeight = this.filterTextField.getItemHeight( this.contentWidth, this.contentWidth );
		this.contentHeight -= filterHeight;
		this.container.setScrollHeight( this.contentHeight );
		if (this.filterPosition == POSITION_TOP) {
			this.filterTextField.relativeY = this.contentY;
			this.contentY += filterHeight;
		} else {
			this.filterTextField.relativeY = this.contentY + this.contentHeight;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#showNotify()
	 */
	public void showNotify() {
		//#debug
		System.out.println("showNotify of FilteredList" + this );
		if (!this.filterTextField.isFocused) {
			this.filterTextField.setShowInputInfo( false );
			this.filterTextField.focus( this.filterTextField.getFocusedStyle(), 0 );
		}
		itemStateChanged( this.filterTextField );
		super.showNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCurrentItem()
	 */
	public Item getCurrentItem() {
		return this.filterTextField;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		super.paintScreen(g);
		this.filterTextField.paint( this.contentX, this.filterTextField.relativeY, this.contentX, this.contentX + this.contentWidth, g );
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#append(de.enough.polish.ui.ChoiceItem)
	 */
	public int append(ChoiceItem item) {
		this.itemsList.add( item );
		return this.itemsList.size() - 1;
	}
	
	
	
	//TODO override set and delete methods as well
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#delete(int)
	 */
	public void delete(int elementNum) {
		this.itemsList.remove(elementNum);
		itemStateChanged( this.filterTextField );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#deleteAll()
	 */
	public void deleteAll() {
		this.itemsList.clear();
		itemStateChanged( this.filterTextField );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getItem(int)
	 */
	public ChoiceItem getItem(int elementNum) {
		return (ChoiceItem) this.itemsList.get(elementNum);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getSelectedFlags(boolean[])
	 */
	public int getSelectedFlags(boolean[] selectedArray_return) {
		int count = 0;
		for (int i = 0; i < selectedArray_return.length; i++) {
			boolean selected = ((ChoiceItem) this.itemsList.get(i)).isSelected;
			selectedArray_return[i] = selected;
			if (selected) {
				count++;
			}
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		if (this.listType == Choice.IMPLICIT) {
			Item focItem = this.container.getFocusedItem();
			if (focItem == null) {
				return -1;
			}
			return this.itemsList.indexOf(focItem);
		}
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++) {
			Object object = items[i];
			if (object == null) {
				return -1;
			}
			ChoiceItem item = (ChoiceItem) object;
			if ( item.isSelected ) {
				return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#insert(int, de.enough.polish.ui.ChoiceItem)
	 */
	public void insert(int elementNum, ChoiceItem item) {
		this.itemsList.add(elementNum, item);
		itemStateChanged( this.filterTextField );
	}
	
	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum the index of the element to be set
	 * @param stringPart the string part of the new element
	 * @param imagePart the image part of the element, or null if there is no image part
	 * @param elementStyle the style for the new list element.
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#set(int, String, Image) in interface Choice
	 */
	public void set(int elementNum, String stringPart, Image imagePart, Style elementStyle )
	{
		ChoiceItem item = getItem(elementNum );
		item.setText( stringPart );
		if (imagePart != null) {
			item.setImage(imagePart);
		}
		if (elementStyle != null) {
			item.setStyle(elementStyle);
		}
		itemStateChanged( this.filterTextField );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#set(int, de.enough.polish.ui.ChoiceItem)
	 */
	public void set(int elementNum, ChoiceItem item) {
		this.itemsList.set(elementNum, item);
		itemStateChanged( this.filterTextField );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setSelectedFlags(boolean[])
	 */
	public void setSelectedFlags(boolean[] selectedArray) {
		for (int i = 0; i < selectedArray.length; i++) {
			((ChoiceItem) this.itemsList.get(i)).select( selectedArray[i] );
		}
		itemStateChanged( this.filterTextField );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setSelectedIndex(int, boolean)
	 */
	public void setSelectedIndex(int elementNum, boolean selected) {
		if ( this.listType == Choice.MULTIPLE ) {
			ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
			item.select( selected );
		} else {
			if (selected == false) {
				return; // ignore this call
			}
			int oldIndex = getSelectedIndex(); 
			if ( oldIndex != -1) {
				ChoiceItem oldSelected = (ChoiceItem) this.itemsList.get( oldIndex );
				oldSelected.select( false );
			}
			ChoiceItem newSelected = (ChoiceItem) this.itemsList.get( elementNum );
			newSelected.select( true );
		}
		itemStateChanged( this.filterTextField );
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#size()
	 */
	public int size() {
		return this.itemsList.size();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCommandListener()
	 */
	public CommandListener getCommandListener() {
		return this.originalCommandListener;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setCommandListener(javax.microedition.lcdui.CommandListener)
	 */
	public void setCommandListener(CommandListener listener) {
		this.originalCommandListener = listener;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable screen) {
		if (this.container.itemCommandListener != null && this.container.commands != null && this.container.commands.contains(command)) {
			this.container.itemCommandListener.commandAction(command, this.container);
		} else if (this.originalCommandListener != null) {
			this.originalCommandListener.commandAction(command, this);
		}
	}

	public void setFilterLabel( String label ) {
		this.filterTextField.setLabel(label);
	}
	
	public void setFilterText( String text ) {
		this.filterTextField.setString( text );
		itemStateChanged( this.filterTextField );
	}
	
	public String getFilterText() {
		return this.filterTextField.getString();
	}
	
	/**
	 * @param filterStyle
	 */
	public void setFilterStyle(Style filterStyle) {
		this.filterTextField.focusedStyle = filterStyle;
		if (isShown()) {
			this.filterTextField.focus(filterStyle, 0);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemStateListener#itemStateChanged(de.enough.polish.ui.Item)
	 */
	public void itemStateChanged(Item item) {
		if (item == this.filterTextField) {
			String filterText = this.filterTextField.getString().toLowerCase();
			//System.out.println("filter=" + filterText );
			Item focItem = this.container.focusedItem;
			super.deleteAll();
			Object[] itemObjects = this.itemsList.getInternalArray();
			boolean checkForSelectedRadioItem = (this.listType == Choice.EXCLUSIVE);
			for (int i = 0; i < itemObjects.length; i++) {
				Object object = itemObjects[i];
				if (object == null) {
					break;
				}
				ChoiceItem cItem = (ChoiceItem) object;
				String choiceText = cItem.getText();
				boolean isMatch;
				if (checkForSelectedRadioItem && cItem.isSelected) {
					isMatch = true;
				} else if (this.filterMode == FILTER_STARTS_WITH) {
					isMatch = (choiceText.toLowerCase().startsWith(filterText));
				} else {
					isMatch = (choiceText.toLowerCase().indexOf( filterText ) != -1);
				}
				if (isMatch) {
					int index = super.append(cItem);
					if (cItem == focItem) {
						super.focus( index, cItem, false );
					}
				}
			}
			// problem: this also adds the delete command
			setItemCommands( this.filterTextField );
			//#if !polish.TextField.suppressCommands
			if (this.filterTextField.getCaretPosition() == 0) {
				removeCommand( TextField.DELETE_CMD );
			}
			//#endif
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.List#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.filter-position
			Integer filterPositionInt = style.getIntProperty("filter-position");
			if (filterPositionInt != null) {
				this.filterPosition = filterPositionInt.intValue();
			}
		//#endif
		//#if polish.css.filter-style
			Style filterStyle = (Style) style.getObjectProperty("filter-style");
			if (filterStyle != null) {
				this.filterTextField.focusedStyle = filterStyle;
				this.filterTextField.focus(filterStyle, 0);
			}
		//#endif
	}
	
	

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#getCssSelector()
	 */
	protected String createCssSelector() {
		return "filteredlist";
	}
	//#endif	


}
