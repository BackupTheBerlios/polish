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
 * <p>Provides a TextField that provides the user with possible matches for the current input.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        27-Feb-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChoiceTextField 
//#if polish.usePolishGui
	//# extends TextField 
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
{

	/** 
	 * The matching mode that selects choices that start with the same characters as the current input
	 */
	public static final int MATCH_STARTS_WITH = 0;
	/** 
	 * The matching mode that selects choices that contain the same characters as the current input
	 */
	public static final int MATCH_INDEX_OF = 1;
	//public static final int MATCH_STARTS_WITH = 0;
	
	//#if !polish.usePolishGui
	protected Item parent;
	protected int marginLeft;
	protected int marginRight;
	protected int marginTop;
	protected int marginBottom;
	protected int paddingVertical;
	protected int itemWidth;
	protected int itemHeight;
	protected int contentWidth;
	protected int contentHeight;
	protected int contentY;
	protected int yTopPos;
	protected boolean isFocused;
	//#endif


	private final boolean isAllowFreeTextEntry;
	private final Container choicesContainer;
	private String[] choices;
	private String[] lowerCaseChoices;
	private int numberOfMatches;
	private boolean isInChoice;
	private Item[] choiceItems;
	private String lastMatchingText;
	private Style originalStyle;
	private Style focusingStyle;
	private int matchMode;
	private boolean reenableCaretFlashing = true;
	private int choicesYOffsetAdjustment;
	private boolean isOpen;
	private Style choiceItemStyle;
	private char appendChoiceDelimiter;
	private boolean isAppendMode;
	private int appendDelimiterIndex;

	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, false, ';', null);
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
	 * @param style the style for this item
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, Style style) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, false, ';', style );		
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
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. '\n' or ';' 
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, boolean appendChoice, char appendChoiceDelimiter) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, appendChoice, appendChoiceDelimiter, null);
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
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. '\n' or ';' 
	 * @param style the style for this item
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, boolean appendChoice, char appendChoiceDelimiter, Style style) {
		//#if polish.usePolishGui
			//# super(label, text, maxSize, constraints, style);
		//#else
			super( label );
		//#endif
		this.choices = availableChoices;
		if (availableChoices != null) {
			this.lowerCaseChoices = new String[ availableChoices.length ];
			for (int i = 0; i < availableChoices.length; i++) {
				String choice = availableChoices[i];
				this.lowerCaseChoices[i] = choice.toLowerCase();
			}
			this.choiceItems = new Item[ availableChoices.length ];
		}
		this.isAllowFreeTextEntry = allowFreeTextEntry;
		this.choicesContainer = new Container( false );
		//#if polish.Container.allowCycling != false
			this.choicesContainer.allowCycling = false;
		//#endif
		//#if polish.usePolishGui
			//# this.choicesContainer.parent = this;
		//#endif	
		this.isAppendMode = appendChoice;
		this.appendChoiceDelimiter = appendChoiceDelimiter;
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
		//#if polish.usePolishGui
			//# super.defocus(origStyle);
		//#endif
		if (!this.isAllowFreeTextEntry && this.numberOfMatches > 0) {
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
		//#if polish.usePolishGui
			//# boolean animated = super.animate();
		//#else
			boolean animated = false;
		//#endif
		if (this.numberOfMatches > 0) {
			animated |= this.choicesContainer.animate();
		}
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focStyle, int direction) {
		//#if polish.usePolishGui
			//# this.originalStyle = super.focus(focStyle, direction);
			//# this.focusingStyle = this.style;
		//#endif
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
				Item item = this.choicesContainer.getFocusedItem();
				String choiceText;
				if ( item instanceof ChoiceItem ) {
					choiceText = ((ChoiceItem) item).getText();
				} else {
					choiceText = item.toString();
				}
				if (this.isAppendMode) {
					String currentText = getString();
					if ( (currentText != null) && (currentText.length() > 1) ) {
						if (this.appendDelimiterIndex != -1) {
							currentText = currentText.substring( 0, this.appendDelimiterIndex );
						}	
						if ( currentText.charAt( currentText.length() - 1) != this.appendChoiceDelimiter ) {
							choiceText = currentText + this.appendChoiceDelimiter + choiceText + this.appendChoiceDelimiter;
						} else {
							choiceText = currentText + choiceText + this.appendChoiceDelimiter;
						}
					} else {
						choiceText += this.appendChoiceDelimiter;
					}
				}
				if (!this.isAllowFreeTextEntry) {
					this.lastMatchingText = choiceText;
				}
				//#if polish.usePolishGui	
					setString( choiceText );
					//# setCaretPosition( choiceText.length() );
				//#endif
				this.numberOfMatches = 0;
				openChoices( false );
				super.notifyStateChanged();
			}
			return true;
		} else if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
				&& this.numberOfMatches > 0) {
			//System.out.println("focusing choices container");
			enterChoices( true );
			return true;
		} else if (gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
			//if (this.numberOfMatches == 0) {
				if (this.choices == null) {
					//#if polish.usePolishGui
						//# return super.handleKeyPressed(keyCode, gameAction);
					//#else
						return true;
					//#endif
				}
				if (this.numberOfMatches == this.choices.length) {
					this.numberOfMatches = 0; // close choices container
					openChoices( false );
				} else {
					this.appendDelimiterIndex = -1;
//					if (!this.isAllowFreeTextEntry) {
//						
//					}
					this.choicesContainer.clear();
					for (int i = 0; i < this.choices.length; i++) {
						Item item = this.choiceItems[i];
						if (item == null) {
							// create new ChoiceItem (lazy initialisation)
							item = new ChoiceItem( this.choices[i], null, Choice.IMPLICIT, this.choiceItemStyle );
						}
						this.choicesContainer.add( item );
					}
					this.numberOfMatches = this.choicesContainer.size();
					openChoices( true );
				}
			//}
			return true;
		}
		//#if polish.usePolishGui
			//# return super.handleKeyPressed(keyCode, gameAction);
		//#else
			return true;
		//#endif
	}

	private void enterChoices( boolean enter ) {
		//#debug
		System.out.println("enter choices: " + enter + ", have been entered already: " + this.isInChoice);
		if (enter) {
			this.choicesContainer.focus(0);
			setStyle( this.originalStyle );
			//#if polish.usePolishGui
				//# this.flashCaret = false;
				//# this.showCaret = false;
				if (!this.isInChoice) {
					//# getScreen().removeItemCommands( this );
				}
			//#endif
		} else {
			setStyle( this.focusingStyle );
			//#if polish.usePolishGui
				//# this.flashCaret = this.reenableCaretFlashing;
				//# this.showCaret = true;
			//#endif
			this.choicesContainer.yOffset = 0;
			this.choicesContainer.targetYOffset = 0;
			// move focus to TextField input again
			this.choicesContainer.defocus( this.originalStyle );
			if (this.isInChoice) {
				//#if polish.usePolishGui
					//# getScreen().setItemCommands( this );
				//#endif
			}
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
			this.choicesContainer.clear();
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
		//#if polish.usePolishGui
			//# super.paintContent(x, y, leftBorder, rightBorder, g);
		//#endif
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
		//#if polish.usePolishGui
			//# Screen scr = getScreen();
		//#else
			Screen scr = null;
		//#endif
		if (scr != null && scr instanceof Form && ((Form)scr).itemStateListener != null ) {
			// let the external item state listener do the work
			super.notifyStateChanged();
		} else {
			// find out possible matches yourself:
			if ( this.lowerCaseChoices == null ) {
				return; // no choices are known
			}
			String currentText = getString();
			if (currentText != null) {
				if (this.isAppendMode) {
					this.appendDelimiterIndex = currentText.lastIndexOf( this.appendChoiceDelimiter );
					if (this.appendDelimiterIndex != -1) {
						currentText = currentText.substring( this.appendDelimiterIndex + 1 );
					}
				}
				currentText = currentText.toLowerCase();
				// cycle through available choices and add the ones resulting in matches.
				// There is one special case, though: when only one of the available choices
				// can be used (=no free text entry alllowed), we need to ensure that there is at least one match, before updating
				// the choicesContainer:
				if (this.isAllowFreeTextEntry) {
					this.choicesContainer.clear();
				}
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
				if ( this.isAllowFreeTextEntry ) {
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

	/**
	 * Checks if the input and the available choice do match.
	 * 
	 * @param currentText the current input of the user
	 * @param choice one of the available choices
	 * @return true when they match - this depends on this chosen matching, usually the start need to be equal
	 * @see #setMatchMode(int)
	 */
	private boolean matches(String currentText, String choice) {
		if (this.matchMode == MATCH_STARTS_WITH) {
			return choice.startsWith( currentText );
		} else {
			return choice.indexOf(currentText) != -1; 
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

	//#if !polish.usePolishGui
	protected int getMinContentWidth() {
		// only signature for CustomItem
		return 0;
	}

	protected int getMinContentHeight() {
		// only signature for CustomItem
		return 0;
	}

	protected int getPrefContentWidth(int arg0) {
		// only signature for CustomItem
		return 0;
	}

	protected int getPrefContentHeight(int arg0) {
		// only signature for CustomItem
		return 0;
	}

	protected void paint(Graphics arg0, int arg1, int arg2) {
		// only signature for CustomItem
	}
	
	public String getString() {
		// ignore since implemented by TextField
		return null;
	}
	
	public void setString( String  input ) {
		// ignore since implemented by TextField
	}
	//#endif

	
	
	
	
}
