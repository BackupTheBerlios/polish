//#condition polish.usePolishGui && polish.TextField.useDirectInput && !polish.blackberry
/*
 * Created on 27-Feb-2006 at 7:36:20.
 * 
 * Copyright (c) 20007 Robert Virkus / Enough Software
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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

//#if polish.blackberry
import de.enough.polish.blackberry.ui.PolishEditField;
//#endif
import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.predictive.TextElement;
import de.enough.polish.predictive.TrieReader;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

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
//#if !polish.LibraryBuild
	extends TextField
//#else
	//# extends FakeTextFieldCustomItem
//#endif
{

	private final Container choicesContainer;
	private int numberOfMatches;
	private boolean isInChoice;
	private Item[] choiceItems;
	private Style originalStyle;
	private Style focusingStyle;
	private boolean reenableCaretFlashing = true;
	private int choicesYOffsetAdjustment;
	private boolean isOpen;
	private Style choiceItemStyle;
	
	private TextBuilder builder = null;
	private TrieReader 	currentReader = null;
	
	private HashMap stores = null;
	private HashMap records = null;
	
	private Display display = null;
	private int spaceButton;
	
	private int elementX = 0;
	private int elementY = 0;
	private boolean refreshChoices = true;
		
	protected static Command ENABLE_PREDICTIVE_CMD = new Command( "Enable Predictive" , Command.ITEM, 0 );
	protected static Command DISABLE_PREDICTIVE_CMD = new Command( "Disable Predictive" , Command.ITEM, 0 );
	private boolean predictiveInput;
	
	private long currentTime;
	private long memory = 0;
	
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
	public PredictiveTextField(String label, String text, int maxSize, Display display, StringItem status) throws RecordStoreException {
		this(label, text, maxSize, display, status, null);
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
	public PredictiveTextField(String label, String text, int maxSize, Display display, StringItem status, Style style) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		super(label, text, maxSize, TextField.ANY, style);
		this.choicesContainer = new Container( false );
		//#if polish.Container.allowCycling != false
			this.choicesContainer.allowCycling = false;
		//#endif
		//#if polish.usePolishGui && !polish.LibraryBuild
			//# this.choicesContainer.parent = this;
		//#endif		
		
		this.builder 	= new TextBuilder(maxSize);
//		this.custom		= new TextCustom();
		this.display 	= display;
		
		this.inputMode 		= this.builder.getInputMode();
		this.spaceButton 	= getSpaceKey();
		
		this.stores 		= new HashMap();
		this.records		= new HashMap();
		
		this.addCommand(DISABLE_PREDICTIVE_CMD);
		predictiveInput = true;
		
		updateInfo();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeTextFieldCustomItem#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		super.initContent(firstLineWidth, lineWidth);
		this.choicesContainer.relativeY = this.contentHeight + this.paddingVertical;
		
		//Display commands in screen
		((Screen)this.display.getCurrent()).setItemCommands(this);
	}
		
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * 
	 * @param choices the new choices, null when no choices are given
	 */
	private void setChoices( ArrayList choices) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.numberOfMatches = choices.size();
		this.choiceItems = new Item[ choices.size() ];
		for (int i = 0; i < choices.size(); i++) {
			String choiceText = ((StringBuffer)choices.get(i)).toString();
			Item item = new ChoiceItem( choiceText, null, Choice.IMPLICIT, this.choiceItemStyle );
			this.choiceItems[i] = item;
			this.choicesContainer.add( item );
		}
		if(!isOpen)
			openChoices( choices.size() > 0 );
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
	
	protected int getSpaceKey()
	{
		if(TextField.charactersKeyPound != null)
			if(TextField.charactersKeyPound.charAt(0) == ' ')
				return Canvas.KEY_POUND;
		
		if(TextField.charactersKeyStar != null)
			if(TextField.charactersKeyStar.charAt(0) == ' ')
				return Canvas.KEY_STAR;
		
		if(TextField.charactersKey0 != null)
			if(TextField.charactersKey0.charAt(0) == ' ')
				return Canvas.KEY_NUM0;
		
		return -1;
	}
		
	protected boolean handleKeyInsert(int keyCode, int gameAction) {
		
		if(!this.predictiveInput)
			return super.handleKeyInsert(keyCode, gameAction);
		
		if(	(keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) ||  
			keyCode == Canvas.KEY_POUND || 
			keyCode == Canvas.KEY_STAR  )
		{
			if(keyCode != this.spaceButton)
			{	
				try
				{
					
					if( this.builder.isChar(0) ||
						this.builder.getCurrentAlign() == TextBuilder.ALIGN_LEFT ||
						this.builder.getCurrentAlign() == TextBuilder.ALIGN_RIGHT)
					{
						currentReader = new TrieReader(stores,records);
						this.builder.addReader(currentReader);
					}
					else if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
						currentReader = this.builder.getCurrentReader();
					
					currentReader.setSelectedWord(0);
					
					currentReader.keyNum(keyCode); 
					//custom.keyNum(keyCode);
				}
				catch(RecordStoreException e){e.printStackTrace();}
				
				if(!currentReader.isWordFound())
				{
					showWordNotFound();
				}
				else
				{
					this.builder.getCurrentElement().pushChar(this.builder.getInputMode());
						
					if(this.builder.getInputMode() == TextField.MODE_FIRST_UPPERCASE)
					{
						this.builder.setInputMode(TextField.MODE_LOWERCASE);
						this.inputMode = this.builder.getInputMode();
						updateInfo();
					}
				}
				
				this.setChoices(this.builder.getCurrentElement().getResults());
//				this.setChoices(this.custom.getResults(),true);
			}
			else
			{
				this.builder.addChar(' ');
				this.openChoices(false);
			}
			
			setText(this.builder.getText());
			setCaretPosition(this.builder.getCaretPosition());
			this.refreshChoices = true;
			
			return true;
		}
		
		return false;
	}
	
	protected boolean handleKeyClear(int keyCode, int gameAction) {
		
		if(!this.predictiveInput)
			return super.handleKeyClear(keyCode, gameAction);
		
		try
		{
			if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_LEFT)
				if(this.builder.getCurrentIndex() !=0)
					this.builder.decreaseCaret();
				else
					return true;
			
			if(this.builder.isChar(0))
				this.builder.deleteCurrent();
			else
			{
				currentReader = this.builder.getCurrentReader();
				
				currentReader.keyClear();
				this.builder.getCurrentElement().popChar();
				
				if(currentReader.isEmpty())
				{
					this.builder.deleteCurrent();
				}
				else
				{
					setChoices(this.builder.getCurrentElement().getResults());
					//setChoices(this.custom.getResults(),true);
					this.builder.setCurrentAlign(TextBuilder.ALIGN_FOCUS);
					this.builder.getCurrentElement().setSelectedWord(0);
				}
				
				openChoices(!currentReader.isEmpty());
			}	
		}
		catch(RecordStoreException e) {e.printStackTrace();}
		
		setText(this.builder.getText());
		setCaretPosition(this.builder.getCaretPosition());
		this.refreshChoices = true;
		
		return true;
	}
	
	protected boolean handleKeyMode(int keyCode, int gameAction) {
		//#if polish.key.ChangeNumericalAlphaInputModeKey:defined
		//#= if ( keyCode == ${polish.key.ChangeNumericalAlphaInputModeKey}
		//#else
		if ( keyCode == KEY_CHANGE_MODE 
		//#endif
		//#if polish.key.shift:defined
		//#= || keyCode == ${polish.key.shift}
		//#endif
		)
		{
			if(!this.predictiveInput)
				return super.handleKeyMode(keyCode, gameAction);
			
			this.inputMode = (this.inputMode + 1) % 3;
			this.builder.setInputMode(this.inputMode);
					
			updateInfo();
			
			this.refreshChoices = true;
			return true;
		}
		
		return false;
	}
	
	protected boolean handleKeyNavigation(int keyCode, int gameAction) {
		if(!this.predictiveInput)
			return super.handleKeyNavigation(keyCode, gameAction);
		
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
				if(!this.builder.isChar(0))
				{
					currentReader = this.builder.getCurrentReader();
					currentReader.setSelectedWord(this.choicesContainer.getFocusedIndex());
					this.builder.setCurrentAlign(TextBuilder.ALIGN_RIGHT);
					
					openChoices( false );
					super.notifyStateChanged();
					
					setText(this.builder.getText());
					setCaretPosition(this.builder.getCaretPosition());
					this.refreshChoices = true;
				}
			}
			
			return true;
		}
		if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
				&& this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
		{
			if(this.builder.isChar(0))
				return true;
			else
			{
				currentReader = this.builder.getCurrentReader();
				setChoices(currentReader.getResults());
				
				if(this.numberOfMatches > 0)
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
			
			if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
			{
				ArrayList results = this.builder.getCurrentElement().getResults();
				if(results.size() > 0)
				{
					setChoices(results);
				}
			}
			else
				openChoices(false);
			
			setCaretPosition(this.builder.getCaretPosition());
									
			return true;
		}
		else if ( gameAction == Canvas.UP && !this.isInChoice)
		{
			int lineCaret = this.builder.getJumpPosition(TextBuilder.JUMP_PREV, this.textLines);
			
			if(lineCaret != -1)
			{
				this.builder.setCurrentElementNear(lineCaret);
				
				setCaretPosition(this.builder.getCaretPosition());
								
				openChoices(false);
			}
			return true;
		}
		else if ( gameAction == Canvas.DOWN && !this.isInChoice)
		{
			int lineCaret = this.builder.getJumpPosition(TextBuilder.JUMP_NEXT, this.textLines);
			
			if(lineCaret != -1)
			{
				this.builder.setCurrentElementNear(lineCaret);
				
				setCaretPosition(this.builder.getCaretPosition());
								
				openChoices(false);
			}
			return true;
		}
		else if (gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
			
			openChoices( false );
			if(!this.builder.isChar(0))
				this.builder.setCurrentAlign(TextBuilder.ALIGN_RIGHT);
			
			return true;
		}
		
		return false;
	}
	
	protected void handleCommandAction(Command cmd, Item item)
	{
		if(!this.predictiveInput && cmd != ENABLE_PREDICTIVE_CMD)
		{
			super.handleCommandAction(cmd, item);
			return;
		}
		
		if ( cmd == DELETE_CMD ) {
				//#ifdef polish.key.ClearKey:defined
				//#= handleKeyClear(${polish.key.ClearKey},0);
				//#else
				handleKeyClear(-8,0);
				//#endif
		} else if ( cmd == CLEAR_CMD ) {
			while(this.builder.deleteCurrent());
				
			openChoices(false);
			setText(this.builder.getText());
			setCaretPosition(this.builder.getCaretPosition());
		} else if ( cmd == ENABLE_PREDICTIVE_CMD ) {
			this.predictiveInput = true;
			
			enablePredictive();
			
			this.removeCommand(ENABLE_PREDICTIVE_CMD);
			this.addCommand(DISABLE_PREDICTIVE_CMD);
			
		} else if ( cmd == DISABLE_PREDICTIVE_CMD ) {
			this.predictiveInput = false;
			
			disablePredictive();
			
			this.removeCommand(DISABLE_PREDICTIVE_CMD);
			this.addCommand(ENABLE_PREDICTIVE_CMD);
		}
	}
	
	public void enablePredictive()
	{
		while(this.builder.deleteCurrent());
		
		for(int i=0;i<this.getText().length(); i++)
		{
			this.builder.addChar(this.getText().charAt(i));
		}
		
		this.setInputMode(MODE_LOWERCASE);
		this.builder.setInputMode(MODE_LOWERCASE);
		
		updateInfo();
		
		this.builder.setCurrentElementNear(this.getCaretPosition());
	}
	
	public void disablePredictive()
	{
		this.setString(this.builder.getText());
		this.setCaretPosition(this.builder.getCaretPosition());
		
		openChoices(false);
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
		this.refreshChoices = open;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if ( this.isFocused && this.numberOfMatches > 0 ) {
			
			if(this.isOpen)
			{
				y += this.paddingVertical;
							
				if(this.refreshChoices)
				{
					this.elementX = getChoicesX(leftBorder,rightBorder,this.choicesContainer.getItemWidth(this.itemWidth, this.itemWidth));
					this.elementY = getChoicesY();
					this.refreshChoices = false;
				}
				
				this.choicesContainer.paint(x + this.elementX , y + this.elementY, leftBorder + this.elementX, rightBorder, g);
			}
		}
		
		if((System.currentTimeMillis() - currentTime) > 1000)
		{
			currentTime = System.currentTimeMillis();
			this.memory += 300;
			System.out.println(Runtime.getRuntime().freeMemory() + this.memory);
		}
	}
	
	protected int getChoicesY()
	{
		if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
			return 	(this.contentHeight / this.textLines.length) * 
				   	(this.builder.getElementLine(this.textLines) + 1);
		else
			return 0;
	}
	
	protected int getChoicesX(int leftBorder, int rightBorder, int itemWidth)
	{	
		if(this.builder.getCurrentAlign() == TextBuilder.ALIGN_FOCUS)
		{
			int line = this.builder.getElementLine(this.textLines);
			int charsToLine = 0;
			
			for(int i = 0; i < line; i++)
				charsToLine += this.textLines[i].length() + 1;
			
			TextElement element = this.builder.getCurrentElement();
			
			if(element != null)
			{
				int result = 0;
				
				for(int i=charsToLine; i<this.builder.getCurrentCaret() - element.getLength(); i++)
					result += this.font.charWidth(this.builder.getTextChar(i));
				
				int overlap = (rightBorder) - (leftBorder + result + itemWidth);
				
				if(overlap < 0)
					result += overlap;
				
				return result;
			}
		}
		
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyStateChanged()
	 */
	public void notifyStateChanged() {
		
		Screen scr = getScreen();
		if (scr != null && scr instanceof Form && ((Form)scr).itemStateListener != null ) {
			// let the external item state listener do the work
			super.notifyStateChanged();
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
