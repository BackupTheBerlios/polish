//#condition polish.usePolishGui
/*
 * Created on 27-Feb-2006 at 7:36:20.
 * 
 * Copyright (c) 2004-2006 Robert Virkus / Enough Software
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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

//#if polish.blackberry
import de.enough.polish.blackberry.ui.PolishEditField;
//#endif
import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.predictive.TrieProperties;
import de.enough.polish.predictive.TrieReader;
import de.enough.polish.util.TextUtil;

/**
 * <p>Provides a TextField that provides the user with possible matches for the current input.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        27-Feb-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PredictiveTextField 
//#if polish.LibraryBuild
	extends FakeTextFieldCustomItem
//#else
	//# extends TextField 
//#endif
{

	private final Container choicesContainer;
	private String[] choices;
	private String[] lowerCaseChoices;
	private int numberOfMatches;
	private boolean isInChoice;
	private Item[] choiceItems;
	private Style originalStyle;
	private Style focusingStyle;
	private boolean reenableCaretFlashing = true;
	private int choicesYOffsetAdjustment;
	private boolean isOpen;
	private Style choiceItemStyle;
	private String lastMatchingText;
	
	private TextBuilder builder = null;
	private TrieReader 	currentReader = null;
	private Display display = null;
	
	private int CLEAR_BUTTON = -8;
	private int SHIFT_BUTTON = Canvas.KEY_STAR;
	private int SPACE_BUTTON = Canvas.KEY_POUND;
	
	private StringItem status;
	
	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 * @param appendChoice true when the selected choices should be appended to the text rather than replacing the text
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. '\n' or ';' 
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 */
	public PredictiveTextField(String label, String text, int maxSize, int constraints, Display display, StringItem status) throws RecordStoreException {
		this(label, text, maxSize, constraints, display, status, null);
	}
	
	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 * @param appendChoice true when the selected choices should be appended to the text rather than replacing the text
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. "\n" or ";" or null. 
	 * @param style the style for this item
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 */
	public PredictiveTextField(String label, String text, int maxSize, int constraints, Display display, StringItem status, Style style) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		super(label, text, maxSize, constraints, style);
		this.choicesContainer = new Container( false );
		//#if polish.Container.allowCycling != false
			this.choicesContainer.allowCycling = false;
		//#endif
		//#if polish.usePolishGui && !polish.LibraryBuild
			//# this.choicesContainer.parent = this;
		//#endif		
		
		//#if polish.key.predictive.ClearKey:defined
		//#= CLEAR_BUTTON = ${polish.key.predictive.ClearKey};
		//#endif
		
		//#if polish.key.predictive.ShiftKey:defined
		//#= SHIFT_BUTTON = ${polish.key.predictive.ShiftKey};
		//#endif
		
		//#if polish.key.predictive.SpaceKey:defined
		//#= SPACE_BUTTON = ${polish.key.predictive.SpaceKey};
		//#endif
			
		this.builder = new TextBuilder();
		this.status = status;
		this.display =display;
		
		//Font uebernehmen
		//this.choicesContainer.getStyle().font = this.getStyle().font;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeTextFieldCustomItem#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		super.initContent(firstLineWidth, lineWidth);
		this.choicesContainer.relativeY = this.contentHeight + this.paddingVertical;
	}
		
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * 
	 * @param choices the new choices, null when no choices are given
	 */
	private void setChoices( String[] choices ) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.numberOfMatches = choices.length;
		this.choiceItems = new Item[ choices.length ];
		for (int i = 0; i < choices.length; i++) {
			String choiceText = choices[i];
			Item item = new ChoiceItem( choiceText, null, Choice.IMPLICIT, this.choiceItemStyle );
			this.choiceItems[i] = item;
			this.choicesContainer.add( item );
		}
		this.choices = choices;
		openChoices( choices.length > 0 );
	}
	
	
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * The given items should implement the "toString()" method and return the correct string value for the text field.
	 * 
	 * @param choices the new choices, null when no choices are available
	 */
	private void setChoices( Item[] choices ) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.choiceItems = choices;
		for (int i = 0; i < choices.length; i++) {
			Item item = choices[i];
			this.choicesContainer.add( item );
		}
		openChoices( choices.length > 0 );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style origStyle) {
		super.defocus(origStyle);
		if (this.numberOfMatches > 0) {
			Item item = this.choicesContainer.get( 0 );
			if (item instanceof StringItem) {
				setString( ((StringItem)item).getText() );
			} else {
				setString( item.toString() );
			}			
		}
		this.numberOfMatches = 0;
		this.choicesContainer.clear();
		openChoices(false);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.numberOfMatches > 0) {
			animated |= this.choicesContainer.animate();
		}
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focStyle, int direction) {
		this.originalStyle = super.focus(focStyle, direction);
		this.focusingStyle = this.style;
		return this.originalStyle;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("keyPressed( keyCode=" + keyCode + ", gameAction=" + gameAction +  ", isInChoice=" + this.isInChoice + " )");
		if (this.isInChoice) {
			if ( this.choicesContainer.handleKeyPressed(keyCode, gameAction) ) {
				//#debug
				System.out.println("keyPressed handled by choices container");
				return true;
			}
			//System.out.println("focusing textfield again, isFocused=" + this.isFocused);
			enterChoices( false );
			
			if (gameAction == Canvas.FIRE) {
				// option has been selected!
				if(currentReader != null)
				{
					currentReader.setSelectedWord(this.choicesContainer.getFocusedIndex());
					this.builder.setCurrentAlign(this.builder.ALIGN_RIGHT);
					
					openChoices( false );
					super.notifyStateChanged();
					
					setText(this.builder.getText());
					setCaretPosition(this.builder.getCaretPosition());
				}
			}
			return true;
		}
		else if ( (keyCode >= Canvas.KEY_NUM0) && keyCode <= Canvas.KEY_NUM9)
		{
			try
			{
				TrieProperties properties = new TrieProperties("predictive","Enough Software","PredictiveInstaller",true, 100,500);
				
				if( this.builder.isString(0) ||
					this.builder.getCurrentAlign() == TextBuilder.ALIGN_LEFT ||
					this.builder.getCurrentAlign() == TextBuilder.ALIGN_RIGHT)
				{
					currentReader = new TrieReader("predictive", properties);
					this.builder.addReader(currentReader);
				}
				else if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
					currentReader = this.builder.getCurrentReader();
				
				currentReader.setSelectedWord(0);
				
				switch(keyCode)
				{
					case Canvas.KEY_NUM0 : currentReader.keyNum('0'); break;
					case Canvas.KEY_NUM1 : currentReader.keyNum('1'); break;
					case Canvas.KEY_NUM2 : currentReader.keyNum('2'); break;
					case Canvas.KEY_NUM3 : currentReader.keyNum('3'); break;
					case Canvas.KEY_NUM4 : currentReader.keyNum('4'); break;
					case Canvas.KEY_NUM5 : currentReader.keyNum('5'); break;
					case Canvas.KEY_NUM6 : currentReader.keyNum('6'); break;
					case Canvas.KEY_NUM7 : currentReader.keyNum('7'); break;
					case Canvas.KEY_NUM8 : currentReader.keyNum('8'); break;
					case Canvas.KEY_NUM9 : currentReader.keyNum('9'); break;					
				}
			}
			catch(RecordStoreException e){e.printStackTrace();}
			
			if(!currentReader.isWordFound())
				showWordNotFound();
				
			this.setChoices(currentReader.getResults());
			
			setText(this.builder.getText());
			setCaretPosition(this.builder.getCaretPosition());
			
			System.gc();
			
			return true;
		}
		else if ( keyCode == this.CLEAR_BUTTON)
		{
			try
			{
				if(this.builder.getCurrentAlign() == this.builder.ALIGN_LEFT)
					this.builder.decreaseCaret();
				
				if(this.builder.isString(0))
					this.builder.deleteCurrent();
				else
				{
					currentReader = this.builder.getCurrentReader();
					currentReader.keyClear();
					
					if(currentReader.isEmpty())
					{
						this.builder.deleteCurrent();
						openChoices(false);
					}
					else
					{
						setChoices(currentReader.getResults());
						openChoices(true);
					}
				}	
			}
			catch(RecordStoreException e) {e.printStackTrace();}
			
			setText(this.builder.getText());
			setCaretPosition(this.builder.getCaretPosition());
			
			System.gc();
			return true;
		}
		else if ( keyCode == this.SHIFT_BUTTON )
		{
			
			return true;
		}
		else if ( keyCode == this.SPACE_BUTTON )
		{
			this.builder.addString(" ");
			
			setText(this.builder.getText());
			openChoices(false);
			
			System.gc();
			setCaretPosition(this.builder.getCaretPosition());
			return true;
		}
		else if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
				&& this.builder.getCurrentAlign() == this.builder.ALIGN_FOCUS)
		{
			//System.out.println("focusing choices container");
			if(this.numberOfMatches > 0)
			{
				enterChoices( true );
				return true;
			}
		
		}
		else if ( gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT )
		{
			if(gameAction == Canvas.LEFT)
				this.builder.decreaseCaret();
			else if(gameAction == Canvas.RIGHT)
				this.builder.increaseCaret();
			
			if(this.builder.getCurrentAlign() == this.builder.ALIGN_FOCUS)
			{
				currentReader = this.builder.getCurrentReader();
				String results[] = currentReader.getResults();
				if(results.length > 0)
				{
					setChoices(results);
					openChoices(true);
				}
			}
			else
				openChoices(false);
			
			setCaretPosition(this.builder.getCaretPosition());
			return true;
		}
		else if ( gameAction == Canvas.UP && !this.isInChoice)
		{
			int lineCaret = this.builder.getLineCaret(this.builder.JUMP_PREV, this.textLines);
			this.builder.setCurrentElementNear(lineCaret);
			
			setCaretPosition(this.builder.getCaretPosition());
			openChoices(false);
			
			return true;
		}
		else if ( gameAction == Canvas.DOWN && !this.isInChoice)
		{
			int lineCaret = this.builder.getLineCaret(this.builder.JUMP_NEXT, this.textLines);
			this.builder.setCurrentElementNear(lineCaret);
			
			setCaretPosition(this.builder.getCaretPosition());
			openChoices(false);
			
			return true;
		}
		else if (gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
			
			openChoices( false );
			if(!this.builder.isString(0))
				this.builder.setCurrentAlign(this.builder.ALIGN_RIGHT);

			return true;
		//#if polish.Key.ReturnKey:defined
		} else if (this.isOpen
				//#= && (keyCode == ${polish.Key.ReturnKey})
				) {
			openChoices(false);
			return true;
		//#endif
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}
		
	public void showWordNotFound()
	{
		Alert alert = new Alert("PredictiveDemo");
		alert.setInfo("Word not found");
		alert.setTimeout(2000);
		display.setCurrent(alert);
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		boolean handled = super.handlePointerPressed(x, y);
		if (!handled && this.isOpen) {
			handled = this.choicesContainer.handlePointerPressed(x, y - (this.choicesContainer.relativeY + this.contentY) );
			if (handled) {
				// select the current element:
				this.isInChoice = true;
				handleKeyPressed( 0, Canvas.FIRE );
			} else {
				openChoices( false );
				handled = true;
			}
		}
		return handled;
	}
	//#endif

	private void enterChoices( boolean enter ) {
		//#debug
		System.out.println("enter choices: " + enter + ", have been entered already: " + this.isInChoice);
		if (enter) {
			this.choicesContainer.focus(0);
			setStyle( this.originalStyle );
			//#if polish.usePolishGui && !polish.LibraryBuild
				this.flashCaret = false;
				this.showCaret = false;
				if (!this.isInChoice) {
					//# getScreen().removeItemCommands( this );
				}
			//#endif
			//#if polish.blackberry
				PolishEditField field = (PolishEditField) this._bbField;
				field.processKeyEvents = false;
			//#endif
		} else {
			setStyle( this.focusingStyle );
			this.flashCaret = this.reenableCaretFlashing;
			this.showCaret = true;
			this.choicesContainer.yOffset = 0;
			this.choicesContainer.targetYOffset = 0;
			// move focus to TextField input again
			this.choicesContainer.defocus( this.originalStyle );
			if (this.isInChoice) {
				//#if polish.usePolishGui  && !polish.LibraryBuild
					//# getScreen().setItemCommands( this );
				//#endif
			}
			//#if polish.blackberry
				PolishEditField field = (PolishEditField) this._bbField;
				field.processKeyEvents = true;
			//#endif
		}
		this.isInChoice = enter;
	}
	
	private void openChoices( boolean open ) {
		//#debug
		System.out.println("open choices: " + open + ", have been opened already:" + this.isOpen);
		this.choicesContainer.focus( -1 );
		if (open) {
			if (this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				if ( parentContainer.enableScrolling ) {
					int availableWidth = this.itemWidth - (this.marginLeft + this.marginRight);
					int choicesHeight = this.choicesContainer.getItemHeight( availableWidth, availableWidth );
					int choicesBottomY = this.contentY + this.contentHeight + this.paddingVertical + choicesHeight;
					//#debug
					System.out.println("choicesHeight " + choicesHeight + ", choicesBottom=" + choicesBottomY + ", parent.height=" + parentContainer.availableHeight  );
					int parentYOffset = parentContainer.getScrollYOffset();
					int overlap = choicesBottomY - (parentContainer.getContentScrollHeight() - (this.relativeY + parentYOffset));
					//System.out.println("overlap=" + overlap );
					if (overlap > 0) {
						// try to scroll up this item, so that the user sees all matches:
						int yOffsetAdjustment = Math.min( this.relativeY + parentYOffset, overlap );
						this.choicesYOffsetAdjustment = yOffsetAdjustment;
						//#debug
						System.out.println("Adjusting yOffset of parent by " + yOffsetAdjustment );
						parentContainer.setScrollYOffset( parentYOffset - yOffsetAdjustment, true );
						//System.out.println("choice.itemHeight=" + this.choicesContainer.itemHeight + ", parentContainer.availableHeight=" + parentContainer.availableHeight + ", (this.contentY + this.contentHeight + this.paddingVertical)=" + (this.contentY + this.contentHeight + this.paddingVertical) + ", children.relativeY=" + this.choicesContainer.relativeY );
						//TODO this needs some finetuning!
						int itHeight = this.itemHeight;
						int ctHeight = this.contentY + this.contentHeight + this.paddingVertical;
						int max = Math.max( itHeight, ctHeight);
						this.choicesContainer.setScrollHeight( parentContainer.getContentScrollHeight()  - max );
					} else {
						this.choicesYOffsetAdjustment = 0;
					}
				}
			}			
		} else {
			this.choicesContainer.clear();
			if (this.choicesYOffsetAdjustment != 0 && this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				parentContainer.setScrollYOffset( parentContainer.getScrollYOffset() + this.choicesYOffsetAdjustment, true );
				this.choicesYOffsetAdjustment = 0;
			}
		}
		this.isOpen = open;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if ( this.isFocused && this.numberOfMatches > 0 ) {
			// paint containert
			
			/*x += this.builder.getCurrentOffset() * 
				 this.choicesContainer.getStyle().font.getSize();*/
			
			y += this.contentHeight + this.paddingVertical;
			
			this.choicesContainer.paint(x, y, leftBorder, rightBorder, g);			
		}
	}
	
	/**
	 * Checks if the input and the available choice do match.
	 * 
	 * @param currentText the current input of the user
	 * @param choice one of the available choices
	 * @return true when they match - this depends on this chosen matching, usually the start need to be equal
	 * @see #setMatchMode(int)
	 */
	private boolean matches(String currentText, String choice) {
			return choice.startsWith( currentText );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyStateChanged()
	 */
	public void notifyStateChanged() {
		Screen scr = getScreen();
		if (scr != null && scr instanceof Form && ((Form)scr).itemStateListener != null ) {
			// let the external item state listener do the work
			super.notifyStateChanged();
		} else {		
			// find out possible matches yourself:
			if ( this.lowerCaseChoices == null ) {
				return; // no choices are known
			}
			if (this.isOpen) {
				this.choicesContainer.focus(-1);
			}
			String currentText = getString();
			if (currentText != null) {
				currentText = currentText.toLowerCase();
				// cycle through available choices and add the ones resulting in matches.
				// There is one special case, though: when only one of the available choices
				// can be used (=no free text entry alllowed), we need to ensure that there is at least one match, before updating
				// the choicesContainer:
				int foundMatches = 0;
				for (int i = 0; i < this.lowerCaseChoices.length; i++) {
					String choice = this.lowerCaseChoices[i];
					if ( matches( currentText, choice ) ) {
						// found a match!
						foundMatches++;
						Item item = this.choiceItems[i];
						if (item == null) {
							// create new ChoiceItem (lazy initialisation)
							item = new ChoiceItem( this.choices[i], null, Choice.IMPLICIT, this.choiceItemStyle );
						}
						//#debug
						System.out.println("found match: " + choice);
						this.choicesContainer.add( item );
					}
				}
				// handle case when there are no matches, but only matches are allowed as the input:
				if ( foundMatches == 0 ) {
					// re-set the text to the last match:
					setString( this.lastMatchingText );
				} else {
					// remove all previous matches and remember this text:						
					this.lastMatchingText = getString();
					for ( int i = this.numberOfMatches; --i >= 0; ) {
						System.out.println("size before removal=" + this.choicesContainer.size() );
						this.choicesContainer.remove( 0 );
						System.out.println("size after removal=" + this.choicesContainer.size() );
					}
					this.numberOfMatches = foundMatches;
				}
			}
			openChoices( this.numberOfMatches > 0 );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.usePolishGui
			//# super.setStyle(style);
		//#endif
		//#ifdef polish.css.textfield-caret-flash
			Boolean flashCursorBool = style.getBooleanProperty( "textfield-caret-flash" );
			if ( flashCursorBool != null ) {
				this.reenableCaretFlashing = flashCursorBool.booleanValue();
			}
		//#endif
		//#if polish.css.choicetextfield-containerstyle
			Style containerstyle = (Style) style.getObjectProperty("choicetextfield-containerstyle");
			if (containerstyle != null) {
				this.choicesContainer.setStyle( containerstyle );
			}
		//#endif
		//#if polish.css.choicetextfield-choicestyle
			Style choicestyle = (Style) style.getObjectProperty("choicetextfield-choicestyle");
			if (choicestyle != null) {
				this.choiceItemStyle = choicestyle;
			}
		//#endif
	}
}
