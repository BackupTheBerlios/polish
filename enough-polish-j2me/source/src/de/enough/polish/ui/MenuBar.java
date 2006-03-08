//#condition polish.usePolishGui
/*
 * Created on 24-Jan-2005 at 05:39:27.
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


import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * <p>Provides a more powerful alternative to the build-in menu bar of the Screen-class.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MenuBar extends Item {
	
	//#ifdef polish.key.LeftSoftKey:defined
		//#= private final static int LEFT_SOFT_KEY = ${polish.key.LeftSoftKey};
	//#else
		private final static int LEFT_SOFT_KEY = -6;
	//#endif
	//#ifdef polish.key.RightSoftKey:defined
		//#= private final static int RIGHT_SOFT_KEY = ${polish.key.RightSoftKey};
	//#else
		private final static int RIGHT_SOFT_KEY = -7;
	//#endif
	//#if polish.blackberry && (polish.BlackBerry.useStandardMenuBar != true)
		//#define tmp.useInvisibleMenuBar
		private Command cancelCommand;
	//#endif
	private final ArrayList commandsList;
	private final Container commandsContainer;
	protected boolean isOpened;
	private Command singleLeftCommand;
	private final IconItem singleLeftCommandItem;
	private Command singleRightCommand;
	private final IconItem singleRightCommandItem;
	private int commandsContainerY;
	private int topY;
	private int commandsContainerWidth;
	protected boolean isSoftKeyPressed;
	
	protected boolean canScrollDownwards; // indicator for parent screen
	protected boolean canScrollUpwards; // indicator for parent screen
	protected boolean paintScrollIndicator; // indicator for parent screen
	private Image optionsImage;
	private boolean showImageAndText;
	private Image selectImage;
	private Image cancelImage;
	private int singleLeftCommandY;
	private int singleRightCommandY;

	/**
	 * Creates a new menu bar\
	 * 
	 * @param screen the parent screen
	 */
	public MenuBar( Screen screen ) {
		this( screen, null );
	}

	/**
	 * Creates a new menu bar
	 * 
	 * @param screen the parent screen
	 * @param style the style of this menu-bar
	 */
	public MenuBar(Screen screen, Style style) {
		super(style);
		this.screen = screen;
		this.commandsList = new ArrayList();
		//#style menu, default
		this.commandsContainer = new Container( true );
		this.commandsContainer.screen = screen;
		this.commandsContainer.parent = this;
		//#style rightcommand, default
		this.singleRightCommandItem = new IconItem( null, null );
		this.singleRightCommandItem.setImageAlign( Graphics.LEFT );
		//#style leftcommand, default
		this.singleLeftCommandItem = new IconItem( null, null );
		this.singleLeftCommandItem.setImageAlign( Graphics.LEFT );
	}

	public void addCommand(Command cmd) {
		if (cmd == this.singleLeftCommand || cmd == this.singleRightCommand || this.commandsList.contains(cmd)) {
			// do not add an existing command again...
			//#debug
			System.out.println("Ignoring existing command " + cmd.getLabel() );
			return;
		}
		//#debug
		System.out.println("adding command " + cmd.getLabel() + " (" + cmd + ")");
		int type = cmd.getCommandType();
		int priority = cmd.getPriority();
		//#if tmp.useInvisibleMenuBar
			if ( this.cancelCommand == null ) {
				// add cancel command:
				//#ifdef polish.i18n.useDynamicTranslations
					String text =  Locale.get("polish.command.hide");
				//#elifdef polish.command.hide:defined
					//#= String text =  "${polish.command.hide}";
				//#else
					//# String text =  "Hide";
				//#endif
				this.cancelCommand = new Command( text, Command.CANCEL, 2000 );
				addCommand( this.cancelCommand );
			}
			if ( (cmd != this.cancelCommand) && 
					(type == Command.BACK || type == Command.CANCEL || type == Command.EXIT) ) 
			{
				if ( this.singleRightCommand == null || this.singleRightCommand.getPriority() > priority ) {
					this.singleRightCommand = cmd;
				}
			}
		//#else
			//#if polish.blackberry
				//# if (type == Command.BACK || type == Command.CANCEL || type == Command.EXIT ) {
			//#else
			if (type == Command.BACK || type == Command.CANCEL) {
			//#endif
				if (this.singleRightCommand == null) {
					this.singleRightCommand = cmd;
					this.singleRightCommandItem.setImage( (Image)null );
					this.singleRightCommandItem.setText( cmd.getLabel() );
					if (this.isInitialised) {
						this.isInitialised = false;
						repaint();
					}
					return;
				} else if ( this.singleRightCommand.getPriority() > priority ) {
					Command oldRightCommand = this.singleRightCommand;
					this.singleRightCommand = cmd;
					this.singleRightCommandItem.setText( cmd.getLabel() );
					cmd = oldRightCommand;
					priority = oldRightCommand.getPriority();
				}
			}
			if (this.singleLeftCommand != null) {
				// add existing left command first:
				//#style menuitem, menu, default
				CommandItem item = new CommandItem( this.singleLeftCommand, this );
				this.commandsList.add( this.singleLeftCommand );
				this.commandsContainer.add( item );
				this.singleLeftCommand = null;
			}  else if (this.commandsList.size() == 0) {
				// this is the new single left command!
				//#debug
				System.out.println("Setting single left command " + cmd.getLabel() );
				this.singleLeftCommand = cmd;
				this.singleLeftCommandItem.setText( cmd.getLabel() );
				if (this.isInitialised) {
					this.isInitialised = false;
					repaint();
				}
				return;
			} 
		//#endif
		//#style menuitem, menu, default
		CommandItem item = new CommandItem( cmd, this );
		if ( this.commandsList.size() == 0 ) {
			this.commandsList.add( cmd );
			this.commandsContainer.add( item );
		} else {
			// there are already several commands,
			// so add this cmd to the appropriate sorted position:
			Command[] myCommands = (Command[]) this.commandsList.toArray( new Command[ this.commandsList.size() ]);
			boolean inserted = false;
			for (int i = 0; i < myCommands.length; i++) {
				Command command = myCommands[i];
				if ( cmd == command ) {
					return;
				}
				if (command.getPriority() > priority ) {
					this.commandsList.add( i, cmd );
					this.commandsContainer.add(i, item);
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				this.commandsList.add( cmd );
				this.commandsContainer.add( item );
			}
		}
		
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		//#debug
		System.out.println("removing command " + cmd.getLabel() + " (" + cmd + ")");
		// 1.case: cmd == this.singleLeftCommand
		if ( cmd == this.singleLeftCommand ) {
			this.singleLeftCommand = null;
			if (this.isInitialised) {
				this.isInitialised = false;
				repaint();
			}
			//System.out.println("remiving single left");
			return;
		}
		
		// 2.case: cmd == this.singleRightCommand
		if ( cmd == this.singleRightCommand ) {
			// remove single right command:
			this.singleRightCommand = null;
			//System.out.println("removing single right command");
			// check if there is another BACK or CANCEL command and 
			// select the one with the highest priority:
			if (this.singleLeftCommand != null) {
				if ( this.singleLeftCommand.getCommandType()  == Command.BACK 
					|| this.singleLeftCommand.getCommandType()  == Command.CANCEL ) 
				{
					this.singleRightCommand = this.singleLeftCommand;
					this.singleRightCommandItem.setText( this.singleLeftCommand.getLabel() );
					this.singleLeftCommand = null;
				}
				if (this.isInitialised) {
					this.isInitialised = false;
					repaint();
				}
				return;
			}
			// there are several commands available, from the which the BACK/CANCEL command
			// with the highest priority needs to be chosen:
			Command[] myCommands = (Command[]) this.commandsList.toArray( new Command[ this.commandsList.size() ]);
			int maxPriority = 1000;
			int maxPriorityId = -1;
			for (int i = 0; i < myCommands.length; i++) {
				Command command = myCommands[i];
				int type = command.getCommandType();
				//#if polish.blackberry
					if ((type == Command.BACK || type == Command.CANCEL || type == Command.EXIT)
				//#else
					//# if ((type == Command.BACK || type == Command.CANCEL)
				//#endif
						&& command.getPriority() < maxPriority ) 
				{
					maxPriority = command.getPriority();
					maxPriorityId = i;
				}
			}
			if ( maxPriorityId != -1 ) {
				this.singleRightCommand = (Command) this.commandsList.remove(maxPriorityId);
				this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
				this.commandsContainer.remove( maxPriorityId );
			}
			// don't return here yet, since it could well be that there is only
			// one remaining item in the commandsList. In such a case the 
			// single left command is used instead.
		}
		
		// 3.case: cmd belongs to command collection
		int index = this.commandsList.indexOf( cmd );
		if (index != -1) {
			//System.out.println("removing normal command");
			this.commandsList.remove( index );
			this.commandsContainer.remove( index );
		}
		// if there is only one remaining item in the commandsList, the 
		// single left command is used instead:
		//#if !tmp.useInvisibleMenuBar
		if (this.commandsList.size() == 1) {
			//System.out.println("moving only left command to single-left-one");
			Command command = (Command) this.commandsList.remove( 0 );
			this.commandsContainer.remove( 0 );
			this.singleLeftCommand = command;
			this.singleLeftCommandItem.setText( command.getLabel() );
		}
		//#endif

		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		//#debug
		System.out.println("Init content of MenuBar - isOpened=" + this.isOpened );
		if (this.isOpened) {
			int titleHeight = this.screen.titleHeight; // + this.screen.subTitleHeight + this.screen.infoHeight;
			int screenHeight = this.screen.screenHeight;
			this.topY = titleHeight;
			this.commandsContainer.setVerticalDimensions( titleHeight, screenHeight );
			//System.out.println("setting vertical dimension: " + topMargin + ", " + (this.screen.screenHeight - topMargin) );
			this.commandsContainerWidth = (this.screen.screenWidth * 2) / 3;
			int containerHeight = this.commandsContainer.getItemHeight(this.commandsContainerWidth, this.commandsContainerWidth);
			this.commandsContainerY = screenHeight - containerHeight - 1;
			if (this.commandsContainerY < titleHeight) {
				this.commandsContainerY = titleHeight;
			}
			/*
			int focusedIndex = this.commandsContainer.focusedIndex;
			this.canScrollUpwards = (focusedIndex != 0); 
			this.canScrollDownwards = (focusedIndex != this.commandsList.size() - 1 );
			*/
			this.canScrollUpwards = (this.commandsContainer.yOffset != 0)
				&& (this.commandsContainer.focusedIndex != 0);
			this.canScrollDownwards = (this.commandsContainer.yOffset + containerHeight > screenHeight - titleHeight) 
				&& (this.commandsContainer.focusedIndex != this.commandsList.size() - 1 );
			this.paintScrollIndicator = this.canScrollUpwards || this.canScrollDownwards;
			//#if !tmp.useInvisibleMenuBar
				if (this.selectImage != null) {
					this.singleLeftCommandItem.setImage( this.selectImage );
					if (this.showImageAndText) {
						//#ifdef polish.i18n.useDynamicTranslations
							this.singleLeftCommandItem.setText( Locale.get( "polish.command.select" ) ); 
						//#elifdef polish.command.select:defined
							//#= this.singleLeftCommandItem.setText( "${polish.command.select}" );
						//#else
							this.singleLeftCommandItem.setText( "Select" );
						//#endif
					} else {
						this.singleLeftCommandItem.setText( null );
					}
				} else {
					this.singleLeftCommandItem.setImage( (Image)null );
					//#ifdef polish.i18n.useDynamicTranslations
						this.singleLeftCommandItem.setText( Locale.get( "polish.command.select" ) ); 
					//#elifdef polish.command.select:defined
						//#= this.singleLeftCommandItem.setText( "${polish.command.select}" );
					//#else
						this.singleLeftCommandItem.setText( "Select" );
					//#endif
				}
				if (this.cancelImage != null) {
					this.singleRightCommandItem.setImage( this.cancelImage );
					if (this.showImageAndText) {
						//#ifdef polish.i18n.useDynamicTranslations
							this.singleRightCommandItem.setText( Locale.get( "polish.command.cancel" ) ); 
						//#elifdef polish.command.cancel:defined
							//#= this.singleRightCommandItem.setText(  "${polish.command.cancel}" );
						//#else
							this.singleRightCommandItem.setText( "Cancel" );
						//#endif
					} else {
						this.singleRightCommandItem.setText( null );
					}
				} else {
					//#ifdef polish.i18n.useDynamicTranslations
						this.singleRightCommandItem.setText( Locale.get( "polish.command.cancel" ) ); 
					//#elifdef polish.command.cancel:defined
						//#= this.singleRightCommandItem.setText(  "${polish.command.cancel}"  );
					//#else
						this.singleRightCommandItem.setText( "Cancel" );
					//#endif
				}
			//#endif
		} else {
			//#if tmp.useInvisibleMenuBar
				this.background = null;
				this.border = null;
				this.contentWidth = 0;
				this.contentHeight = 0;
			//#else
				if (this.singleLeftCommand == null && this.singleRightCommand == null 
						&& this.commandsList.size() == 0 ) 
				{
					this.contentWidth = 0;
					this.contentHeight = 0;
					return;
				}
				// the menu is closed
				this.paintScrollIndicator = false;
				if (this.singleRightCommand != null) {
					this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
					this.singleRightCommandItem.setImage( (Image)null );
				}
				if (this.commandsList.size() > 0) {
					if (this.optionsImage != null) {
						this.singleLeftCommandItem.setImage( this.optionsImage );
						if (this.showImageAndText) {
							//#ifdef polish.i18n.useDynamicTranslations
								this.singleLeftCommandItem.setText( Locale.get( "polish.command.options" ) ); 
							//#elifdef polish.command.options:defined
								//#= this.singleLeftCommandItem.setText( "${polish.command.options}" );
							//#else
								this.singleLeftCommandItem.setText( "Options" );				
							//#endif
						} else {
							this.singleLeftCommandItem.setText( null );
						}
					} else {
						this.singleLeftCommandItem.setImage( (Image)null );
						//#ifdef polish.i18n.useDynamicTranslations
							this.singleLeftCommandItem.setText( Locale.get( "polish.command.options" ) ); 
						//#elifdef polish.command.options:defined
							//#= this.singleLeftCommandItem.setText( "${polish.command.options}" );
						//#else
							this.singleLeftCommandItem.setText( "Options" );				
						//#endif
					}
				}
			//#endif
		}
		//#if !tmp.useInvisibleMenuBar
			int availableWidth = lineWidth / 2;
//			if ( ! this.isOpened && this.singleRightCommand == null ) {
//				availableWidth = lineWidth;
//			}
			//System.out.println("Initialising single commands with a width of " + availableWidth + " lineWidth is " + lineWidth);
			int height = this.singleLeftCommandItem.getItemHeight( availableWidth, availableWidth);
			if (this.singleRightCommandItem.getItemHeight( availableWidth, availableWidth ) > height) {
				height = this.singleRightCommandItem.itemHeight;
			}
			if (height > this.contentHeight) {
				this.contentHeight = height; 
			}
			this.singleLeftCommandY = this.contentHeight - this.singleLeftCommandItem.itemHeight;
			this.singleRightCommandY = this.contentHeight - this.singleRightCommandItem.itemHeight;
			this.contentWidth = lineWidth;
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) 
	{
		if (this.isOpened) {
			// paint opened menu:
			//System.out.println("setting clip " + this.topY + ", " + (this.screen.screenHeight - this.topY) );
			//#if tmp.useInvisibleMenuBar
				// paint menu at the top left corner:
				g.setClip(0, this.screen.contentY, this.screen.screenWidth , this.screen.screenHeight - this.screen.contentY);
				this.commandsContainer.paint( this.screen.screenWidth - this.commandsContainerWidth, this.topY, this.commandsContainerWidth, this.screen.screenWidth, g);
				g.setClip(0, 0, this.screen.screenWidth , this.screen.screenHeight );
			//#else
				// paint menu at the lower right corner:
				g.setClip(0, this.topY, this.screen.screenWidth , this.screen.screenHeight - this.topY);
				this.commandsContainer.paint(0, this.commandsContainerY, 0, this.commandsContainerWidth , g);
			//#endif
			//System.out.println("MenuBar: commandContainer.background == null: " + ( this.commandsContainer.background == null ) );
			//System.out.println("MenuBar: commandContainer.style.background == null: " + ( this.commandsContainer.style.background == null ) );
		//#if !tmp.useInvisibleMenuBar
			//#ifdef polish.FullCanvasSize:defined
				//#= g.setClip(0, 0, ${polish.FullCanvasWidth}, ${polish.FullCanvasHeight} );
			//#else
				//TODO rob check if resetting of clip really works
				g.setClip(0, this.commandsContainerY, this.screen.screenWidth, this.screen.screenHeight );
			//#endif
			// paint menu-bar:
			int centerX = leftBorder + ((rightBorder - leftBorder)/2); 
			this.singleLeftCommandItem.paint(leftBorder, y + this.singleLeftCommandY, leftBorder, centerX, g);			
			this.singleRightCommandItem.paint(centerX, y + this.singleRightCommandY, centerX, rightBorder, g);
		} else {
			int centerX = leftBorder + ((rightBorder - leftBorder)/2); 
			if (this.commandsContainer.size() > 0 || this.singleLeftCommand != null) {
				//System.out.println("painting left command from " + leftBorder + " to " + centerX );
				this.singleLeftCommandItem.paint(leftBorder, y + this.singleLeftCommandY, leftBorder, centerX, g);
			}
			if (this.singleRightCommand != null) {
				//System.out.println("painting right command from " + centerX + " to " + rightBorder );
				this.singleRightCommandItem.paint(centerX, y + this.singleRightCommandY, centerX, rightBorder, g);
			}
		//#endif
		}
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "menubar";
	}
	//#endif
	
	/**
	 * Used to toggle the opened state of the menu bar
	 */
	protected void setOpen( boolean open ) {
		this.isInitialised = (open == this.isOpened);
		if (!open && this.isOpened) {
			this.commandsContainer.hideNotify();
		} else if (open && !this.isOpened) {
			this.commandsContainer.showNotify();
		}
		this.isOpened = open;
		//#if !polish.MenuBar.focusFirstAfterClose
			if (!open) {
				// focus the first item again, so when the user opens the menu again, it will be "fresh" again
				this.commandsContainer.focus(0);
			}
		//#endif
	}
	


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("MenuBar: handleKeyPressed(" + keyCode + ", " + gameAction  );
		this.isSoftKeyPressed = false;
		if (keyCode == LEFT_SOFT_KEY) {
			this.isSoftKeyPressed = true;
			if (this.isOpened) {
				CommandItem commandItem = (CommandItem) this.commandsContainer.getFocusedItem();
				//#if tmp.useInvisibleMenuBar
					if (commandItem.command == this.cancelCommand ) {
						setOpen( false );
					}
					//TODO find a way how to handle the hide command on BlackBerry when it is invoked directly...
				//#endif
				boolean handled = commandItem.handleKeyPressed( 0, Canvas.FIRE );
				if (!handled) { // CommandItem returns false when it invokes the command listener
					setOpen( false );
				}
				
				return true;
			} else if (this.singleLeftCommand != null) {
				this.screen.callCommandListener(this.singleLeftCommand);
				return true;
			} else if (this.commandsList.size() > 0) {
				setOpen( true );
				return true;
			}
		} else if (keyCode == RIGHT_SOFT_KEY) {
			this.isSoftKeyPressed = true;
			if (this.isOpened) {
				setOpen( false );
				return true;
			} else if (this.singleRightCommand != null) {
				this.screen.callCommandListener(this.singleRightCommand);
				return true;
			}
		} else if (this.isOpened){
			if (keyCode != 0) {
				gameAction = this.screen.getGameAction(keyCode);
			}
//			if (gameAction == Canvas.FIRE) {
//				int focusedIndex = this.commandsContainer.focusedIndex;
//				Command command = (Command) this.commandsList.get(focusedIndex);
//				setOpen( false );
//				this.screen.callCommandListener(command);
//				return true;
//			}
			boolean handled = this.commandsContainer.handleKeyPressed(keyCode, gameAction);
			if (handled) {
				this.isInitialised = false;
			} else { // command item is returning false when a command has been triggered
				setOpen( false );
			}
			return handled;
		}
		return false;
	}
	
	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerPressed(int x, int y) {
		// check if one of the command buttons has been pressed:
		int leftCommandEndX = this.marginLeft + this.paddingLeft + this.singleLeftCommandItem.itemWidth;
		int rightCommandStartX = this.screen.screenWidth - (this.marginRight + this.paddingRight + this.singleRightCommandItem.itemWidth);
		//System.out.println("MenuBar: handlePointerPressed( x=" + x + ", y=" + y + " )\nleftCommandEndX = " + leftCommandEndX + ", rightCommandStartXs = " + rightCommandStartX + " screenHeight=" + this.screen.screenHeight);
		if (y > this.screen.screenHeight) {
			//System.out.println("menubar clicked");
			if (this.isOpened) {
				if ( x < leftCommandEndX ) {
					//System.out.println("selecting command from opened menu");
					setOpen( false );
					Command command = (Command) this.commandsList.get( this.commandsContainer.focusedIndex );
					this.screen.callCommandListener(command);
				} else if ( x > rightCommandStartX ) {
					//System.out.println("closing menu");
					setOpen( false );
				}
			} else if (this.singleLeftCommand != null 
					&& x < leftCommandEndX) 
			{
				//System.out.println("calling single left command");
				this.screen.callCommandListener(this.singleLeftCommand);
			} else if (this.singleRightCommand != null
					&& x > rightCommandStartX) 
			{
				//System.out.println("calling single right command");
				this.screen.callCommandListener(this.singleRightCommand);
			} else if (this.commandsList.size() > 0 
					&& x < leftCommandEndX)
			{
				//System.out.println("opening menu");
				setOpen( true );
			}
			//System.out.println("nothing was clicked...");
			return true;
		// okay, y is above the menu bar, so let the commandContainer process the event:
		} else if (this.isOpened) {
			setOpen( false );
			// a menu-item could have been selected:
			if (x <= this.commandsContainer.xLeftPos + this.commandsContainerWidth 
					&& y >= this.commandsContainerY ) 
			{
				
				//boolean handled = this.commandsContainer.handlePointerPressed( x, y );
				//System.out.println("handling pointer pressed in open menu at " + x + ", " + y + ": " + handled);
				this.commandsContainer.handlePointerPressed( x, y );
				int focusedIndex = this.commandsContainer.getFocusedIndex();
				Command cmd = (Command) this.commandsList.get( focusedIndex );
				this.screen.callCommandListener( cmd );						
				return true;
			}
		}
		return false;
	}
	//#endif
	
	
	public void setStyle(Style style) {
		//#if tmp.useInvisibleMenuBar
			this.background = null;
			this.border = null;
		//#else
			super.setStyle(style);
			
			//#ifdef polish.css.menubar-show-image-and-text
				Boolean showImageAndTextBool = style.getBooleanProperty( "menubar-show-image-and-text" );
				if (showImageAndTextBool != null) {
					this.showImageAndText = showImageAndTextBool.booleanValue();
				}
			//#endif
			//#ifdef polish.css.menubar-options-image
				String optionsUrl = style.getProperty("menubar-options-image");
				if (optionsUrl != null) {
					try {
						this.optionsImage = StyleSheet.getImage(optionsUrl, this, false);
						int imageHeight = this.optionsImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load options-image " + optionsUrl + e );
					}
				}
			//#endif
			//#ifdef polish.css.menubar-select-image
				String selectUrl = style.getProperty("menubar-select-image");
				if (selectUrl != null) {
					try {
						this.selectImage = StyleSheet.getImage(selectUrl, this, false);
						int imageHeight = this.selectImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load select-image " + selectUrl + e );
					}
				}
			//#endif
			//#ifdef polish.css.menubar-cancel-image
				String cancelUrl = style.getProperty("menubar-cancel-image");
				if (cancelUrl != null) {
					try {
						this.cancelImage = StyleSheet.getImage(cancelUrl, this, false);
						int imageHeight = this.cancelImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load cancel-image " + cancelUrl + e );
					}
				}
			//#endif
		//#endif
	}
	
	
	public boolean animate() {
		boolean animated = false;
		if (this.background != null) {
			animated = animated | this.background.animate();
		}
		if (this.isOpened) {
			animated = animated | this.commandsContainer.animate();
		}
		if (this.singleLeftCommandItem != null) {
			animated = animated | this.singleLeftCommandItem.animate();
		}
		if (this.singleRightCommandItem != null) {
			animated = animated | this.singleRightCommandItem.animate();
		}
		return animated;
	}

	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param parentCommand the parent command
	 * @param childCommand the child command
	 * @param commandStyle the style for the command
	 * @throws IllegalStateException when the parent command has not be added before
	 */
	public void addSubCommand(Command childCommand, Command parentCommand, Style commandStyle) {
		// find parent CommandItem, could be tricky, especially when there are nested commands over several layers
		int index = this.commandsList.indexOf( parentCommand );
		CommandItem parentCommandItem = null;
		if (index != -1) {
			// found it:
			parentCommandItem = (CommandItem) this.commandsContainer.get( index );
		} else {
			// search through all commands
			for ( int i=0; i < this.commandsContainer.size(); i++ ) {
				CommandItem item = (CommandItem) this.commandsContainer.get( i );
				parentCommandItem = item.getChild( parentCommand );
				if ( parentCommandItem != null ) {
					break;
				}
			}
		}
		if ( parentCommandItem == null ) {
			throw new IllegalStateException();
		}
		parentCommandItem.addChild( childCommand, commandStyle );
		if (this.isOpened) {
			this.isInitialised = false;
			repaint();
		}
	}

	
	
	
//#ifdef polish.MenuBar.additionalMethods:defined
	//#include ${polish.MenuBar.additionalMethods}
//#endif

}
