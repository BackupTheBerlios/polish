//#condition polish.usePolishGui
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:44 CET 2003
/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

/**
 * A <code>ChoiceGroup</code> is a group of selectable elements intended to be
 * placed within a <CODE>Form</CODE>. The group may be created with a mode that requires a
 * single choice to be made or that allows multiple choices. The
 * implementation is responsible for providing the graphical representation of
 * these modes and must provide visually different graphics for different
 * modes. For example, it might use &quot;radio buttons&quot; for the
 * single choice
 * mode and &quot;check boxes&quot; for the multiple choice mode.
 * 
 * <p> <strong>Note:</strong> most of the essential methods have been
 * specified in the <CODE>Choice</CODE> interface.</p>
 * <HR>
 * 
 * @author Robert Virkus, robert@enough.de
 * @since MIDP 1.0
 */
public class ChoiceGroup extends Container implements Choice
{
	private int selectedIndex;
	//private boolean isExclusive;
	private boolean isMultiple;
	private int choiceType;
	private boolean isImplicit;
	private Command selectCommand;
	//#ifdef polish.usePopupItem
		private static Image popupImage;
		private boolean isPopup;
		private int popupColor = 0;
		private int popupBackgroundColor = 0xFFFFFF;
		private IconItem popupItem;
		private boolean isPopupClosed;
	//#endif
	

	/**
	 * Creates a new, empty <code>ChoiceGroup</code>, specifying its
	 * title and its type.
	 * The type must be one of <code>EXCLUSIVE</code>,
	 * <code>MULTIPLE</code>, or <code>POPUP</code>. The
	 * <code>IMPLICIT</code>
	 * choice type is not allowed within a <code>ChoiceGroup</code>.
	 * 
	 * @param label the item's label (see Item)
	 * @param choiceType EXCLUSIVE, MULTIPLE,  or POPUP
	 * @throws IllegalArgumentException - if choiceType is not one of EXCLUSIVE, MULTIPLE, or POPUP
	 * @see Choice#EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT, Choice.POPUP
	 */
	public ChoiceGroup( String label, int choiceType)
	{
		this( label, choiceType, new String[0], null, null, false );
	}

	/**
	 * Creates a new, empty <code>ChoiceGroup</code>, specifying its
	 * title and its type.
	 * The type must be one of <code>EXCLUSIVE</code>,
	 * <code>MULTIPLE</code>, or <code>POPUP</code>. The
	 * <code>IMPLICIT</code>
	 * choice type is not allowed within a <code>ChoiceGroup</code>.
	 * 
	 * @param label the item's label (see Item)
	 * @param choiceType EXCLUSIVE, MULTIPLE,  or POPUP
	 * @param style the CSS style for this item
	 * @throws IllegalArgumentException if choiceType is not one of EXCLUSIVE, MULTIPLE, or POPUP
	 * @see Choice#EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT, Choice.POPUP
	 */
	public ChoiceGroup( String label, int choiceType, Style style)
	{
		this( label, choiceType, new String[0], null, style, false );
	}

	/**
	 * Creates a new <code>ChoiceGroup</code>, specifying its title,
	 * the type of the
	 * <code>ChoiceGroup</code>, and an array of <code>Strings</code>
	 * and <code>Images</code> to be used as its
	 * initial contents.
	 * 
	 * <p>The type must be one of <code>EXCLUSIVE</code>,
	 * <code>MULTIPLE</code>, or <code>POPUP</code>.  The
	 * <code>IMPLICIT</code>
	 * type is not allowed for <code>ChoiceGroup</code>.</p>
	 * 
	 * <p>The <code>stringElements</code> array must be non-null and
	 * every array element
	 * must also be non-null.  The length of the
	 * <code>stringElements</code> array
	 * determines the number of elements in the <code>ChoiceGroup</code>.  The
	 * <code>imageElements</code> array
	 * may be <code>null</code> to indicate that the
	 * <code>ChoiceGroup</code> elements have no images.
	 * If the
	 * <code>imageElements</code> array is non-null, it must be the
	 * same length as the
	 * <code>stringElements</code> array.  Individual elements of the
	 * <code>imageElements</code> array
	 * may be <code>null</code> in order to indicate the absence of an
	 * image for the
	 * corresponding <code>ChoiceGroup</code> element.  Non-null elements
	 * of the
	 * <code>imageElements</code> array may refer to mutable or
	 * immutable images.</p>
	 * 
	 * @param label the item's label (see Item)
	 * @param choiceType EXCLUSIVE, MULTIPLE, or POPUP
	 * @param stringElements set of strings specifying the string parts of the ChoiceGroup elements
	 * @param imageElements set of images specifying the image parts of the ChoiceGroup elements
	 * @throws NullPointerException if stringElements is null or if the stringElements array contains any null elements
	 * @throws IllegalArgumentException if the imageElements array is non-null and has a different length from the stringElements array
	 *   			 or  if choiceType is not one of EXCLUSIVE, MULTIPLE, or POPUP
	 * @see Choice#EXCLUSIVE, Choice#MULTIPLE, Choice#IMPLICIT, Choice#POPUP
	 */
	public ChoiceGroup( String label, int choiceType, String[] stringElements, Image[] imageElements)
	{
		this( label, choiceType, stringElements, imageElements, null, false );
	}

	/**
	 * Creates a new <code>ChoiceGroup</code>, specifying its title,
	 * the type of the
	 * <code>ChoiceGroup</code>, and an array of <code>Strings</code>
	 * and <code>Images</code> to be used as its
	 * initial contents.
	 * 
	 * <p>The type must be one of <code>EXCLUSIVE</code>,
	 * <code>MULTIPLE</code>, or <code>POPUP</code>.  The
	 * <code>IMPLICIT</code>
	 * type is not allowed for <code>ChoiceGroup</code>.</p>
	 * 
	 * <p>The <code>stringElements</code> array must be non-null and
	 * every array element
	 * must also be non-null.  The length of the
	 * <code>stringElements</code> array
	 * determines the number of elements in the <code>ChoiceGroup</code>.  The
	 * <code>imageElements</code> array
	 * may be <code>null</code> to indicate that the
	 * <code>ChoiceGroup</code> elements have no images.
	 * If the
	 * <code>imageElements</code> array is non-null, it must be the
	 * same length as the
	 * <code>stringElements</code> array.  Individual elements of the
	 * <code>imageElements</code> array
	 * may be <code>null</code> in order to indicate the absence of an
	 * image for the
	 * corresponding <code>ChoiceGroup</code> element.  Non-null elements
	 * of the
	 * <code>imageElements</code> array may refer to mutable or
	 * immutable images.</p>
	 * 
	 * @param label the item's label (see Item)
	 * @param choiceType EXCLUSIVE, MULTIPLE, or POPUP
	 * @param stringElements set of strings specifying the string parts of the ChoiceGroup elements
	 * @param imageElements set of images specifying the image parts of the ChoiceGroup elements
	 * @param style The CSS style for this item
	 * @throws NullPointerException if stringElements is null or if the stringElements array contains any null elements
	 * @throws IllegalArgumentException if the imageElements array is non-null and has a different length from the stringElements array
	 *   			 or  if choiceType is not one of EXCLUSIVE, MULTIPLE, or POPUP
	 * @see Choice#EXCLUSIVE, Choice#MULTIPLE, Choice#IMPLICIT, Choice#POPUP
	 */
	public ChoiceGroup( String label, int choiceType, String[] stringElements, Image[] imageElements, Style style )
	{
		this( label, choiceType, stringElements, imageElements, style, false );
	}
	
	/**
	 * Creates a new <code>ChoiceGroup</code>, specifying its title,
	 * the type of the
	 * <code>ChoiceGroup</code>, and an array of <code>Strings</code>
	 * and <code>Images</code> to be used as its
	 * initial contents.
	 * 
	 * <p>The type must be one of <code>EXCLUSIVE</code>,
	 * <code>MULTIPLE</code>, or <code>POPUP</code>.  The
	 * <code>IMPLICIT</code>
	 * type is not allowed for <code>ChoiceGroup</code>.</p>
	 * 
	 * <p>The <code>stringElements</code> array must be non-null and
	 * every array element
	 * must also be non-null.  The length of the
	 * <code>stringElements</code> array
	 * determines the number of elements in the <code>ChoiceGroup</code>.  The
	 * <code>imageElements</code> array
	 * may be <code>null</code> to indicate that the
	 * <code>ChoiceGroup</code> elements have no images.
	 * If the
	 * <code>imageElements</code> array is non-null, it must be the
	 * same length as the
	 * <code>stringElements</code> array.  Individual elements of the
	 * <code>imageElements</code> array
	 * may be <code>null</code> in order to indicate the absence of an
	 * image for the
	 * corresponding <code>ChoiceGroup</code> element.  Non-null elements
	 * of the
	 * <code>imageElements</code> array may refer to mutable or
	 * immutable images.</p>
	 * 
	 * @param label the item's label (see Item)
	 * @param choiceType EXCLUSIVE, MULTIPLE, or POPUP
	 * @param stringElements set of strings specifying the string parts of the ChoiceGroup elements
	 * @param imageElements set of images specifying the image parts of the ChoiceGroup elements
	 * @param style The CSS style for this item
	 * @param allowImplicit true when the Choice.IMPLICIT choiceType is also allowed
	 * @throws NullPointerException if stringElements is null or if the stringElements array contains any null elements
	 * @throws IllegalArgumentException if the imageElements array is non-null and has a different length from the stringElements array
	 *   			 or  if choiceType is not one of EXCLUSIVE, MULTIPLE, or POPUP (unless allowImplicit is defined)
	 * @see Choice#EXCLUSIVE, Choice#MULTIPLE, Choice#IMPLICIT, Choice#POPUP
	 */
	public ChoiceGroup( String label, int choiceType, String[] stringElements, Image[] imageElements, Style style, boolean allowImplicit )
	{
		super( label, false, style, -1, -1 );
		if (choiceType == Choice.EXCLUSIVE) {
			//this.isExclusive = true;
		} else if (choiceType == Choice.MULTIPLE) {
			this.isMultiple = true;
		//#ifdef polish.usePopupItem
		} else if (choiceType == Choice.POPUP) {
			this.isPopup = true;
			this.isPopupClosed = true;
			this.popupItem = new IconItem( null, null, this.style );
			this.popupItem.setImageAlign( Graphics.RIGHT );
			this.popupItem.setAppearanceMode( BUTTON );
			this.popupItem.parent = this;
		//#endif
		} else if (choiceType == Choice.IMPLICIT && allowImplicit ) {
			this.isImplicit = true;
			this.focusFirstElement = true;
		} else {
			//#ifdef polish.verboseDebug
				throw new IllegalArgumentException("invalid choiceType [" + choiceType + "] - IMPLICIT=" + Choice.IMPLICIT + ".");
			//#else
				//# throw new IllegalArgumentException();
			//#endif
		}
		//#ifndef polish.skipArgumentCheck
			if (imageElements != null && imageElements.length != stringElements.length) {
				//#ifdef polish.verboseDebug
					throw new IllegalArgumentException("imageElements need to have the same length as the stringElements.");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		this.choiceType = choiceType;
		for (int i = 0; i < stringElements.length; i++) {
			Image img = null;
			if (imageElements != null) {
				img = imageElements[i];
			}
			append( stringElements[i], img, style );
		}
	}
	
	//#ifdef polish.usePopupItem
	/**
	 * Creates or returns the default image for popup groups.
	 * 
	 * @return the default popup image
	 */
	protected Image createPopupImage() {
		if (popupImage == null) {
			popupImage = Image.createImage( 9, 12 );
			Graphics g = popupImage.getGraphics();
			g.setColor( this.popupBackgroundColor );
			g.fillRect(0, 0, 10, 13 );
			g.setColor( this.popupColor );
			g.drawLine(0, 0, 9, 0 );
			g.drawLine( 3, 3, 3, 9 );
			g.drawLine( 4, 3, 4, 10 );
			g.drawLine( 5, 3, 5, 9 );
			g.drawLine( 2, 8, 6, 8 );	
			g.drawLine( 1, 7, 7, 7 );	
		}
		return popupImage;
	}
	//#endif

	/**
	 * Gets the <code>String</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * 
	 * @param elementNum the index of the element to be queried
	 * @return the string part of the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#getString(int) in interface Choice
	 * @see #getImage(int)
	 */
	public String getString(int elementNum)
	{
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		return item.getText();
	}

	/**
	 * Gets the <code>Image</code> part of the element referenced by
	 * <code>elementNum</code>.
	 * 
	 * @param elementNum the number of the element to be queried
	 * @return the image part of the element, or null if there is no image
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#getImage(int) in interface Choice
	 * @see #getString(int)
	 */
	public Image getImage(int elementNum)
	{
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		return item.getImage();
	}

	/**
	 * Appends an element to the <code>ChoiceGroup</code>.
	 * 
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added, or null if there is no image part
	 * @return the assigned index of the element
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#append( String, Image) in interface Choice
	 */
	public int append( String stringPart, Image imagePart)
	{
		return append( stringPart, imagePart, null );
	}
	
	/**
	 * Appends an element to the <code>ChoiceGroup</code>.
	 * 
	 * @param stringPart the string part of the element to be added
	 * @param imagePart the image part of the element to be added, or null if there is no image part
	 * @param elementStyle the style for the appended ChoiceItem
	 * @return the assigned index of the element
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#append( String, Image) in interface Choice
	 */
	public int append( String stringPart, Image imagePart, Style elementStyle )
	{
		ChoiceItem item = new ChoiceItem( stringPart, imagePart, this.choiceType, elementStyle );
		add( item );
		return this.itemsList.size() - 1;
	}

	/**
	 * Inserts an element into the <code>ChoiceGroup</code> just prior to
	 * the element specified.
	 * 
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#insert(int, String, Image)  in interface Choice
	 */
	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		insert( elementNum, stringPart, imagePart );
	}

	/**
	 * Inserts an element into the <code>ChoiceGroup</code> just prior to
	 * the element specified.
	 * 
	 * @param elementNum the index of the element where insertion is to occur
	 * @param stringPart the string part of the element to be inserted
	 * @param imagePart the image part of the element to be inserted, or null if there is no image part
	 * @param elementStyle the style for the inserted ChoiceItem
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @throws NullPointerException if stringPart is null
	 * @see Choice#insert(int, String, Image)  in interface Choice
	 */
	public void insert(int elementNum, String stringPart, Image imagePart, Style elementStyle)
	{
		ChoiceItem item = new ChoiceItem( stringPart, imagePart, this.choiceType, elementStyle );
		add(elementNum, item);
	}

	/**
	 * Sets the <code>String</code> and <code>Image</code> parts of the
	 * element referenced by <code>elementNum</code>,
	 * replacing the previous contents of the element.
	 * 
	 * @param elementNum - the index of the element to be set
	 * @param stringPart - the string part of the new element
	 * @param imagePart - the image part of the element, or null if there is no image part
	 * @throws IndexOutOfBoundsException - if elementNum is invalid
	 * @throws NullPointerException - if stringPart is null
	 * @see Choice#set(int, String, Image) in interface Choice
	 */
	public void set(int elementNum, String stringPart, Image imagePart)
	{
		set( elementNum, stringPart, imagePart, null );
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
		ChoiceItem item = new ChoiceItem( stringPart, imagePart, this.choiceType, elementStyle );
		set(elementNum, item);
	}

	/**
	 * Deletes the element referenced by <code>elementNum</code>.
	 * 
	 * @param elementNum the index of the element to be deleted
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#delete(int) in interface Choice
	 */
	public void delete(int elementNum)
	{
		remove(elementNum);
	}

	/**
	 * Deletes all elements from this <code>ChoiceGroup</code>.
	 * 
	 * @see Choice#deleteAll() in interface Choice
	 */
	public void deleteAll()
	{
		clear();
	}

	
	/**
	 * Gets a boolean value indicating whether this element is selected.
	 * 
	 * @param elementNum the index of the element to be queried
	 * @return selection state of the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#isSelected(int) in interface Choice
	 */
	public boolean isSelected(int elementNum)
	{
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		return item.isSelected;
	}

	/**
	 * Returns the index number of an element in the
	 * <code>ChoiceGroup</code> that is
	 * selected. For <code>ChoiceGroup</code> objects of type
	 * <code>EXCLUSIVE</code> and <code>POPUP</code>
	 * there is at most one element selected, so
	 * this method is useful for determining the user's choice.
	 * Returns <code>-1</code> if
	 * there are no elements in the <code>ChoiceGroup</code>.
	 * 
	 * <p>For <code>ChoiceGroup</code> objects of type
	 * <code>MULTIPLE</code>, this always
	 * returns <code>-1</code> because no
	 * single value can in general represent the state of such a
	 * <code>ChoiceGroup</code>.
	 * To get the complete state of a <code>MULTIPLE</code>
	 * <code>Choice</code>, see <A HREF="../../../javax/microedition/lcdui/ChoiceGroup.html#getSelectedFlags(boolean[])"><CODE>getSelectedFlags</CODE></A>.</p>
	 * 
	 * @return index of selected element, or -1 if none
	 * @see Choice#getSelectedIndex() in interface Choice
	 * @see #setSelectedIndex(int, boolean)
	 */
	public int getSelectedIndex()
	{
		if (this.isMultiple || this.itemsList.size() == 0) {
			return -1;
		} else if (this.isImplicit) {
			return this.focusedIndex;
		} else {
			return this.selectedIndex;
		}
	}

	/**
	 * Queries the state of a <code>ChoiceGroup</code> and returns the state of
	 * all elements in the
	 * boolean array
	 * <code>selectedArray_return</code>. <strong>Note:</strong> this
	 * is a result parameter.
	 * It must be at least as long as the size
	 * of the <code>ChoiceGroup</code> as returned by <code>size()</code>.
	 * If the array is longer, the extra
	 * elements are set to <code>false</code>.
	 * 
	 * <p>For <code>ChoiceGroup</code> objects of type
	 * <code>MULTIPLE</code>, any
	 * number of elements may be selected and set to true in the result
	 * array.  For <code>ChoiceGroup</code> objects of type
	 * <code>EXCLUSIVE</code> and <code>POPUP</code>
	 * exactly one element will be selected, unless there are
	 * zero elements in the <code>ChoiceGroup</code>. </p>
	 * 
	 * @param selectedArray_return array to contain the results
	 * @return the number of selected elements in the ChoiceGroup
	 * @throws IllegalArgumentException if selectedArray_return is shorter than the size of the ChoiceGroup
	 * @throws NullPointerException if selectedArray_return is null
	 * @see Choice#getSelectedFlags(boolean[]) in interface Choice
	 * @see #setSelectedFlags(boolean[])
	 */
	public int getSelectedFlags(boolean[] selectedArray_return)
	{
		//#ifndef polish.skipArgumentCheck
			if (selectedArray_return.length < this.itemsList.size()) {
				//#ifdef polish.verboseDebug
					throw new IllegalArgumentException("length of selectedArray is too small");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		ChoiceItem[] myItems = (ChoiceItem[]) this.itemsList.toArray( new ChoiceItem[ this.itemsList.size() ] );
		int selectedItems = 0;
		for (int i = 0; i < myItems.length; i++) {
			ChoiceItem item = myItems[i];
			if (item.isSelected) {
				selectedArray_return[i] = true;
				selectedItems++;
			} else {
				selectedArray_return[i] = false;
			}
		}
		return selectedItems;
	}

	/**
	 * For <code>ChoiceGroup</code> objects of type
	 * <code>MULTIPLE</code>, this simply sets an
	 * individual element's selected state.
	 * 
	 * <P>For <code>ChoiceGroup</code> objects of type
	 * <code>EXCLUSIVE</code> and <code>POPUP</code>, this can be used only to
	 * select an element.  That is, the <code> selected </code> parameter must
	 * be <code> true </code>. When an element is selected, the previously
	 * selected element is deselected. If <code> selected </code> is <code>
	 * false </code>, this call is ignored.</P>
	 * 
	 * <p>For both list types, the <code>elementNum</code> parameter
	 * must be within
	 * the range
	 * <code>[0..size()-1]</code>, inclusive. </p>
	 * 
	 * @param elementNum the number of the element. Indexing of the elements is zero-based
	 * @param selected the new state of the element true=selected, false=not selected
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#setSelectedIndex(int, boolean) in interface Choice
	 * @see #getSelectedIndex()
	 */
	public void setSelectedIndex(int elementNum, boolean selected)
	{
		if (this.isMultiple) {
			ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
			item.select( selected );
		} else {
			if (selected == false) {
				return; // ignore this call
			}
			ChoiceItem oldSelected = (ChoiceItem) this.itemsList.get( this.selectedIndex );
			oldSelected.select( false );
			ChoiceItem newSelected = (ChoiceItem) this.itemsList.get( elementNum );
			newSelected.select( true );
			this.selectedIndex = elementNum;
			focus( elementNum, newSelected );
			//#ifdef polish.usePopupItem
			if (this.isPopup) {
				this.popupItem.setText( newSelected.getText() );
			} else if (this.isImplicit) {
			//#else
				//# if (this.isImplicit) {
			//#endif
				// call command listener:
				Screen scr = getScreen();
				if (scr != null) {
					Command selectCmd = this.selectCommand;
					if (selectCmd == null) {
						selectCmd = List.SELECT_COMMAND;
					}
					scr.callCommandListener( selectCmd );
				}
			}
		}
		repaint();
	}

	/**
	 * Attempts to set the selected state of every element in the
	 * <code>ChoiceGroup</code>. The array
	 * must be at least as long as the size of the
	 * <code>ChoiceGroup</code>. If the array is
	 * longer, the additional values are ignored. <p>
	 * 
	 * For <code>ChoiceGroup</code> objects of type
	 * <code>MULTIPLE</code>, this sets the selected
	 * state of every
	 * element in the <code>Choice</code>. An arbitrary number of
	 * elements may be selected.
	 * <p>
	 * 
	 * For <code>ChoiceGroup</code> objects of type
	 * <code>EXCLUSIVE</code> and <code>POPUP</code>, exactly one array
	 * element must have the value <code>true</code>. If no element is
	 * <code>true</code>,
	 * the first element
	 * in the <code>Choice</code> will be selected. If two or more
	 * elements are <code>true</code>, the
	 * implementation will choose the first <code>true</code> element
	 * and select it. <p>
	 * 
	 * @param selectedArray an array in which the method collect the selection status
	 * @throws IllegalArgumentException if selectedArray is shorter than the size of the ChoiceGroup
	 * @throws NullPointerException if the selectedArray is null
	 * @see Choice#setSelectedFlags(boolean[]) in interface Choice
	 * @see #getSelectedFlags(boolean[])
	 */
	public void setSelectedFlags(boolean[] selectedArray)
	{
		//#ifndef polish.skipArgumentCheck
			if (selectedArray.length < this.itemsList.size()) {
				//#ifdef polish.verboseDebug
					throw new IllegalArgumentException("length of selectedArray is too small");
				//#else
					//# throw new IllegalArgumentException();
				//#endif
			}
		//#endif
		if (this.isMultiple) {
			ChoiceItem[] myItems = (ChoiceItem[]) this.itemsList.toArray( new ChoiceItem[ this.itemsList.size() ] );
			for (int i = 0; i < myItems.length; i++) {
				ChoiceItem item = myItems[i];
				item.select( selectedArray[i]);
			}
		} else {
			int index = 0;
			for (int i = 0; i < selectedArray.length; i++) {
				if (selectedArray[i]) {
					index = i;
					break;
				}
			}
			if (index > this.itemsList.size()) {
				index = 0;
			}
			setSelectedIndex( index, true );
		}
		repaint();
	}

	/**
	 * Sets the application's preferred policy for fitting
	 * <code>Choice</code> element contents to the available screen space. The set policy applies for all
	 * elements of the <code>Choice</code> object.  Valid values are
	 * <CODE>Choice.TEXT_WRAP_DEFAULT</CODE>, 
	 * <CODE>Choice.TEXT_WRAP_ON</CODE>,
	 * and <CODE>Choice.TEXT_WRAP_OFF</CODE>. 
	 * Fit policy is a hint, and the
	 * implementation may disregard the application's preferred policy.
	 * The J2ME Polish implementation always uses the TEXT_WRAP_ON policy.
	 * 
	 * @param fitPolicy preferred content fit policy for choice elements
	 * @see Choice#setFitPolicy(int) in interface Choice
	 * @see #getFitPolicy()
	 * @since  MIDP 2.0
	 */
	public void setFitPolicy(int fitPolicy)
	{
		//this.fitPolicy = fitPolicy;
		// ignore hint
	}

	/**
	 * Gets the application's preferred policy for fitting
	 * <code>Choice</code> element contents to the available screen space.  The value returned is the
	 * policy that had been set by the application, even if that value had
	 * been disregarded by the implementation.
	 * 
	 * @return always Choice.TEXT_WRAP_ON
	 * @see Choice#getFitPolicy() in interface Choice
	 * @see #setFitPolicy(int)
	 * @since  MIDP 2.0
	 */
	public int getFitPolicy()
	{
		return Choice.TEXT_WRAP_ON;
	}

	/**
	 * Sets the application's preferred font for
	 * rendering the specified element of this <code>Choice</code>.
	 * An element's font is a hint, and the implementation may disregard
	 * the application's preferred font.
	 * The J2ME Polish implementation uses the font defined by the appropriate
	 * CSS style and ignores the font which is set here.
	 * 
	 * @param elementNum the index of the element, starting from zero
	 * @param font the preferred font to use to render the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#setFont(int, Font) in interface Choice
	 * @see #getFont(int)
	 * @since  MIDP 2.0
	 */
	public void setFont(int elementNum, Font font)
	{
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		item.setPreferredFont( font );
	}

	/**
	 * Gets the application's preferred font for
	 * rendering the specified element of this <code>Choice</code>. The
	 * value returned is the font that had been set by the application,
	 * even if that value had been disregarded by the implementation.
	 * If no font had been set by the application, or if the application
	 * explicitly set the font to <code>null</code>, the value is the default
	 * font chosen by the implementation.
	 * 
	 * <p> The <code>elementNum</code> parameter must be within the range
	 * <code>[0..size()-1]</code>, inclusive.</p>
	 * 
	 * @param elementNum the index of the element, starting from zero
	 * @return the preferred font to use to render the element
	 * @throws IndexOutOfBoundsException if elementNum is invalid
	 * @see Choice#getFont(int) in interface Choice
	 * @see #setFont(int elementNum, Font font)
	 * @since  MIDP 2.0
	 */
	public Font getFont(int elementNum)
	{
		ChoiceItem item = (ChoiceItem) this.itemsList.get( elementNum );
		Font font = item.preferredFont;
		if (font == null) {
			font = item.font;
		}
		return font;
	}

	//#ifdef polish.usePopupItem
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paint(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.isPopup && this.isPopupClosed) {
			this.popupItem.paintContent(x, y, leftBorder, rightBorder, g);
		} else {
			super.paintContent(x, y, leftBorder, rightBorder, g );
		}
	}
	//#endif

	//#ifdef polish.usePopupItem
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		super.initContent(firstLineWidth, lineWidth);
		if (this.isPopup) {
			if (this.popupItem.image == null) {
				this.popupItem.setImage( createPopupImage() );
			}
			if (this.isPopupClosed) {
				if (this.popupItem.getText() == null && this.itemsList.size() > 0) {
					ChoiceItem selectedItem = (ChoiceItem) this.itemsList.get( 0 );
					this.popupItem.setText( selectedItem.getText() );
				}
				if (!this.popupItem.isInitialised) {
					int noneContentWidth = this.marginLeft + this.borderWidth + this.paddingLeft
								+ this.marginRight + this.borderWidth + this.paddingRight;
					this.popupItem.init(firstLineWidth + noneContentWidth, lineWidth + noneContentWidth);
				}
				this.contentWidth = this.popupItem.contentWidth;			
				this.contentHeight = this.popupItem.contentHeight;
			}
		}
	}
	//#endif

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "choicegroup";
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean processed = false;
		//#ifdef polish.usePopupItem
		if (!(this.isPopup && this.isPopupClosed)) {
			processed = super.handleKeyPressed(keyCode, gameAction);
		}		
		if (!processed) {
			if (gameAction == Canvas.FIRE) {
				if (this.isMultiple) {
					ChoiceItem item = (ChoiceItem) this.focusedItem;
					item.toggleSelect();
				} else if (this.isPopup){
					if (this.isPopupClosed) {
						this.isPopupClosed = false;
						focus( this.selectedIndex );
					} else {
						this.isPopupClosed = true;
						setSelectedIndex(this.focusedIndex, true);
					}
					requestInit();
				} else {
					setSelectedIndex(this.focusedIndex, true);
				}
				if (this.choiceType != IMPLICIT) {
					notifyStateChanged();
				}
				return true;
			} else {
				if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
					int index = keyCode - Canvas.KEY_NUM1;
					if (index < this.itemsList.size()) {
						if (!this.isPopup || !this.isPopupClosed) {
							setSelectedIndex( index, true );
							if (this.isPopup) {
								this.isPopupClosed = true;
								requestInit();
							}
							if (this.choiceType != IMPLICIT) {
								notifyStateChanged();
							}
							return true;
						}
					}
				} else if (this.isPopup && (this.isPopupClosed == false)) {
					this.isPopupClosed = true;
					requestInit();
					return true;
				}
			}
		}
		//#else
		// no popup item is used by this application:
		processed = super.handleKeyPressed(keyCode, gameAction);
		if (!processed) {
			if (gameAction == Canvas.FIRE) {
				if (this.isMultiple) {
					ChoiceItem item = (ChoiceItem) this.focusedItem;
					item.toggleSelect();
				} else {
					setSelectedIndex(this.focusedIndex, true);
				}
				if (this.choiceType != IMPLICIT) {
					notifyStateChanged();
				}
				return true;
			} else {
				if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
					int index = keyCode - Canvas.KEY_NUM1;
					if (index < this.itemsList.size()) {
						setSelectedIndex( index, true );
						if (this.choiceType != IMPLICIT) {
							notifyStateChanged();
						}
						return true;
					}
				}
			}
		}
		//#endif
		return processed;
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		if (this.isPopup && this.isPopupClosed) {
			this.isPopupClosed = false;
			return true;
		} else {
			return super.handlePointerPressed(x, y);
		}
	}	
	//#endif

	/**
	 * Sets the select command for this choice group.
	 * This will be ignored unless this group has the type IMPLICIT.
	 *
	 * @param command the new select command
	 */
	protected void setSelectCommand(Command command) {
		this.selectCommand = command;
	}
	
	//#ifdef polish.usePopupItem
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		if (this.isPopup && this.isPopupClosed) {
			this.popupItem.setStyle( originalStyle );
			setStyle( originalStyle );
		} else {
			this.isPopupClosed = true;
			super.defocus(originalStyle);
		}
	}
	//#endif

	//#ifdef polish.usePopupItem	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style)
	 */
	protected Style focus(Style focusStyle) {
		if (this.isPopup && this.isPopupClosed) {
			Style original = this.style;
			this.popupItem.setStyle( focusStyle );
			setStyle( focusStyle );
			return original;
		} else {
			return super.focus(focusStyle);
		}
	}
	//#endif

	//#ifdef polish.usePopupItem
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean ignoreBackground) {
		super.setStyle(style, ignoreBackground);
		if (this.isPopup && this.popupItem.image == null ) {
			//#ifdef polish.css.popup-image
				String url = style.getProperty("popup-image");
				if (url != null ) {
					this.popupItem.setImage( url );
				}
			//#endif
			//#ifdef polish.css.popup-color
				String colorStr = style.getProperty("popup-color");
				if (colorStr != null) {
					this.popupColor = Integer.parseInt( colorStr );
				}
			//#endif
			//#ifdef polish.css.popup-background-color
				colorStr = style.getProperty("popup-background-color");
				if (colorStr != null) {
					this.popupBackgroundColor = Integer.parseInt( colorStr );
				}
			//#endif
		}
	}
	//#endif
}
