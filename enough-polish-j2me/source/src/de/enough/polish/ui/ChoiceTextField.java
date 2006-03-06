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
import javax.microedition.lcdui.Graphics;

/**
 * @author robertvirkus
 *
 */
public class ChoiceTextField 
extends TextField 
{

	public static final int MATCH_STARTS_WITH = 0;
	public static final int MATCH_INDEX_OF = 1;
	//public static final int MATCH_STARTS_WITH = 0;

	private final boolean allowFreeTextEntry;
	private String[] choices;
	private String[] lowerCaseChoices;
	private final Container choicesContainer;
	private int numberOfMatches;
	private boolean isInChoice;
	private Item[] choiceItems;
	private String lastMatchingText;
	private Style originalStyle;
	private Style focusingStyle;
	private int matchMode;
	private boolean reanableCaretFlashing = true;
	private int choicesYOffsetAdjustment;
	private boolean isOpen;

	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, null);
	}
	
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, Style style) {
		super(label, text, maxSize, constraints, style);
		this.choices = availableChoices;
		if (availableChoices != null) {
			this.lowerCaseChoices = new String[ availableChoices.length ];
			for (int i = 0; i < availableChoices.length; i++) {
				String choice = availableChoices[i];
				this.lowerCaseChoices[i] = choice.toLowerCase();
			}
			this.choiceItems = new Item[ availableChoices.length ];
		}
		this.allowFreeTextEntry = allowFreeTextEntry;
		//TODO check how to apply different styles to container, also check how to create open/close animations
		this.choicesContainer = new Container( false );
		this.choicesContainer.allowCycling = false;
		this.choicesContainer.parent = this;
	}
	
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * 
	 * @param choices the new choices, null when no choices are given
	 */
	public void setChoices( String[] choices ) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.choiceItems = new Item[ choices.length ];
		for (int i = 0; i < choices.length; i++) {
			String choiceText = choices[i];
			// TODO apply style to choice item
			Item item = new ChoiceItem( choiceText, null, Choice.IMPLICIT );
			this.choiceItems[i] = item;
			this.choicesContainer.add( item );
		}
		this.choices = choices;
		openChoices( choices.length > 0 );
	}
	
	
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * 
	 * @param choices the new choices, null when no choices are available
	 */
	public void setChoices( Item[] choices ) {
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
	
	/**
	 * Sets the matching algorithm that is used for finding out whether an available choices matches the input of the user.
	 * 
	 * @param mode the matching mode, the default mode is ChoiceTextField.MATCH_STARTS_WITH, so the user input is compared with the start of the available choices. 
	 * @see #MATCH_STARTS_WITH
	 * @see #MATCH_INDEX_OF
	 * @see #setChoices(Item[]) for using complex matching rules
	 * @see #setChoices(String[]) for using complex matching rules
	 */
	public void setMatchMode( int mode ) {
		this.matchMode = mode;
	}
	
	/**
	 * Retrieves the matching algorithm that is used for finding out whether an available choices matches the input of the user.
	 * 
	 * @return the matching mode, the default mode is ChoiceTextField.MATCH_STARTS_WITH, so the user input is compared with the start of the available choices. 
	 * @see #MATCH_STARTS_WITH
	 * @see #MATCH_INDEX_OF
	 * @see #setChoices(Item[]) for using complex matching rules
	 * @see #setChoices(String[]) for using complex matching rules
	 */
	public int getMatchMode() {
		return this.matchMode;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style origStyle) {
		super.defocus(origStyle);
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
	 * @see de.enough.polish.ui.TextField#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		// TODO Auto-generated method stub
		super.initContent(firstLineWidth, lineWidth);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("keyPressed( keyCode=" + keyCode + ", gameAction=" + gameAction + " )");
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
				//TODO question: should the item state listener be notified?
				Item item = this.choicesContainer.getFocusedItem();
				String choiceText;
				if ( item instanceof ChoiceItem ) {
					choiceText = ((ChoiceItem) item).getText();
				} else {
					choiceText = item.toString();
				}
				setString( choiceText );
				setCaretPosition( choiceText.length() );
				this.numberOfMatches = 0;
				this.choicesContainer.clear();
				openChoices( false );
				super.notifyStateChanged();
			}
			return true;
		} else if ( gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8 && this.numberOfMatches > 0) {
			//System.out.println("focusing choices container");
			enterChoices( true );
			return true;
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}

	private void enterChoices( boolean enter ) {
		//#debug
		System.out.println("enter choices: " + enter + ", have been entered already: " + this.isInChoice);
		if (enter) {
			this.choicesContainer.focus(0);
			setStyle( this.originalStyle );
			this.flashCaret = false;
			this.showCaret = false;
			if (!this.isInChoice) {
				getScreen().removeItemCommands( this );
			}
		} else {
			setStyle( this.focusingStyle );
			this.flashCaret = this.reanableCaretFlashing;
			this.showCaret = true;
			this.choicesContainer.yOffset = 0;
			this.choicesContainer.targetYOffset = 0;
			// move focus to TextField input again
			this.choicesContainer.defocus( this.originalStyle );
			if (this.isInChoice) {
				getScreen().setItemCommands( this );
			}
		}
		this.isInChoice = enter;
	}
	
	private void openChoices( boolean open ) {
		//#debug
		System.out.println("open choices: " + open + ", have been opened already:" + this.isOpen);
		if (open) {
			if (this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				if ( parentContainer.enableScrolling ) {
					int availableWidth = this.itemWidth - (this.marginLeft + this.marginRight);
					int choicesHeight = this.choicesContainer.getItemHeight( availableWidth, availableWidth );
					int choicesBottomY = this.contentY + this.contentHeight + this.paddingVertical + choicesHeight;
					//#debug
					System.out.println("choicesHeight " + choicesHeight + ", choicesBottom=" + choicesBottomY + ", parent.yBottom=" + parentContainer.yBottom  );
					if ( choicesBottomY > parentContainer.yBottom ) {
						// try to scroll up this item, so that the user sees all matches:
						int yOffsetAdjustment = Math.min( this.yTopPos - parentContainer.yTop, choicesBottomY - parentContainer.yBottom );
						this.choicesYOffsetAdjustment += yOffsetAdjustment;
						//#debug
						System.out.println("Adjusting yOffset of parent by " + yOffsetAdjustment );
						parentContainer.targetYOffset -= yOffsetAdjustment;
						//#if polish.scroll-mode
							if (!parentContainer.scrollSmooth) {
								parentContainer.yOffset = parentContainer.targetYOffset;
							}
						//#endif						
					} else {
						int newYOffsetAdjustment = Math.max( this.choicesYOffsetAdjustment - ( parentContainer.yBottom - choicesBottomY), 0 );
						int difference = this.choicesYOffsetAdjustment - newYOffsetAdjustment;
						//#debug
						 System.out.println("Re-Adjusting yOffset of parent from " + this.choicesYOffsetAdjustment + " over " +  difference + " to " + (this.choicesYOffsetAdjustment - difference ) );
						this.choicesYOffsetAdjustment -= difference;
						parentContainer.targetYOffset += difference;
						//#if polish.scroll-mode
							if (!parentContainer.scrollSmooth) {
								parentContainer.yOffset = parentContainer.targetYOffset;
							}
						//#endif						
					}
				}
			}			
		} else {
			if (this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				parentContainer.targetYOffset += this.choicesYOffsetAdjustment;
				this.choicesYOffsetAdjustment = 0;
				//#if polish.scroll-mode
					if (!parentContainer.scrollSmooth) {
						parentContainer.yOffset = parentContainer.targetYOffset;
					}
				//#endif
			}			
		}
		this.isOpen = open;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		//TODO disable cursor when user is within choices
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if ( this.isFocused && this.numberOfMatches > 0 ) {
			// paint containert
			y += this.contentHeight + this.paddingVertical;
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipWidth = g.getClipWidth();
			int clipHeight = g.getClipHeight();
			this.choicesContainer.setVerticalDimensions( y, clipY + clipHeight );
			g.clipRect( clipX, y, clipWidth, clipY - y + clipHeight);
			this.choicesContainer.paint(x, y, leftBorder, rightBorder, g);			
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		}
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
			String currentText = getString();
			if (currentText != null) {
				currentText = currentText.toLowerCase();
				// cycle through available choices and add the ones resulting in matches.
				// There is one special case, though: when only one of the available choices
				// can be used, we need to ensure that there is at least one match, before updating
				// the choicesContainer:
				if (this.allowFreeTextEntry) {
					this.choicesContainer.clear();
				}
				int foundMatches = 0;
				for (int i = 0; i < this.lowerCaseChoices.length; i++) {
					String choice = this.lowerCaseChoices[i];
					boolean matches;
					if (this.matchMode == MATCH_STARTS_WITH) {
						matches = choice.startsWith( currentText );
					} else {
						matches = choice.indexOf(currentText) != -1; 
					}
					if ( matches ) {
						// found a match!
						foundMatches++;
						Item item = this.choiceItems[i];
						if (item == null) {
							// create new ChoiceItem (lazy initialisation)
							//TODO apply style for the choice!
							item = new ChoiceItem( this.choices[i], null, Choice.IMPLICIT );
						}
						//#debug
						System.out.println("found match: " + choice);
						this.choicesContainer.add( item );
					}
				}
				// handle case when there are no matches, but only matches are allowed as the input:
				if ( this.allowFreeTextEntry ) {
					this.numberOfMatches = foundMatches;
				} else {
					if ( foundMatches == 0 ) {
						// re-set the text to the last match:
						setString( this.lastMatchingText );
					} else {
						// remove all previous matches and remember this text:
						this.lastMatchingText = getString();
						for ( int i = this.numberOfMatches; --i >= 0; ) {
							this.choicesContainer.remove( 0 );
						}
						this.numberOfMatches = foundMatches;
					}
					
				}
			}
			openChoices( this.numberOfMatches > 0 );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.textfield-caret-flash
			Boolean flashCursorBool = style.getBooleanProperty( "textfield-caret-flash" );
			if ( flashCursorBool != null ) {
				this.reanableCaretFlashing = flashCursorBool.booleanValue();
			}
		//#endif
	}

	
	
	
	
}
