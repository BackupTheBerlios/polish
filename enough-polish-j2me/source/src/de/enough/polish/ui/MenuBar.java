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

import de.enough.polish.ui.backgrounds.TranslucentSimpleBackground;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * <p>Provides a more powerful alternative to the build-in menu bar of the Screen-class.</p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
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
		private Command hideCommand;
		private Command positiveCommand;
	//#endif
	//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} == left
		//#define tmp.RightOptions
		//#define tmp.OkCommandOnLeft
		//# private final int openOptionsMenuKey = RIGHT_SOFT_KEY;
		//# private final int selectOptionsMenuKey = LEFT_SOFT_KEY;
		//# private final int closeOptionsMenuKey = RIGHT_SOFT_KEY;
	//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
		//#define tmp.RightOptions
		//# private final int openOptionsMenuKey = RIGHT_SOFT_KEY;
		//# private final int selectOptionsMenuKey = RIGHT_SOFT_KEY;
		//# private final int closeOptionsMenuKey = LEFT_SOFT_KEY;
	//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
		//#define tmp.MiddleOptions
		//# private final int openOptionsMenuKey = MIDDLE_SOFT_KEY;
		//# private final int selectOptionsMenuKey = LEFT_SOFT_KEY;
		//# private final int closeOptionsMenuKey = RIGHT_SOFT_KEY;
	//#else
		//#define tmp.LeftOptions
		private final int openOptionsMenuKey = LEFT_SOFT_KEY;
		private final int selectOptionsMenuKey = LEFT_SOFT_KEY;
		private final int closeOptionsMenuKey = RIGHT_SOFT_KEY;
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
	//#if !polish.Bugs.noTranslucencyWithDrawRgb
		private Background overlayBackground;
	//#endif

	/**
	 * Creates a new menu bar
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
		if (this.commandsContainer.style != null) {
			this.commandsContainer.setStyle( this.commandsContainer.style );
		}
		this.commandsContainer.layout |= LAYOUT_SHRINK;
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
			System.out.println( this + ": Ignoring existing command " + cmd.getLabel() + ",  cmd==this.singleRightCommand: " + ( cmd == this.singleRightCommand) + ", cmd==this.singleLeftCommand: " + (cmd == this.singleLeftCommand) + ", this.commandsList.contains(cmd): " + this.commandsList.contains(cmd)  );
			//#if polish.debug.debug
				for (int i = 0; i < this.commandsList.size(); i++) {
					System.out.println(  ((Command)this.commandsList.get(i)).getLabel() );
				}
			//#endif
			return;
		}
		//#debug
		System.out.println(this + ": adding command " + cmd.getLabel() + " (" + cmd + ")");
		int type = cmd.getCommandType();
		int priority = cmd.getPriority();
		//#if tmp.useInvisibleMenuBar
			if ( this.hideCommand == null )	{
				// add hide command:
				//#ifdef polish.i18n.useDynamicTranslations
					String text =  Locale.get("polish.command.hide");
				//#elifdef polish.command.hide:defined
					//#= String text =  "${polish.command.hide}";
				//#else
					//# String text =  "Hide";
				//#endif
				this.hideCommand = new Command( text, Command.CANCEL, 2000 );
				addCommand( this.hideCommand );
			}
			if ( (cmd != this.hideCommand) && 
					(type == Command.BACK || type == Command.CANCEL || type == Command.EXIT) ) 
			{
				//#if tmp.RightOptions
					if ( this.singleLeftCommand == null || this.singleLeftCommand.getPriority() > priority ) {
						this.singleLeftCommand = cmd;
					}
				//#else
					if ( this.singleRightCommand == null || this.singleRightCommand.getPriority() > priority ) {
						this.singleRightCommand = cmd;
					}
				//#endif
			}
		//#else
			//#if tmp.OkCommandOnLeft
				if (type == Command.OK || type == Command.ITEM || type == Command.SCREEN) {
					if (this.singleLeftCommand == null) {
						this.singleLeftCommand = cmd;
						this.singleLeftCommandItem.setImage( (Image)null );
						this.singleLeftCommandItem.setText( cmd.getLabel() );
						if (this.isInitialised) {
							this.isInitialised = false;
							repaint();
						}
						return;
					} else if ( this.singleLeftCommand.getPriority() > priority ) {
						Command oldLeftCommand = this.singleLeftCommand;
						this.singleLeftCommand = cmd;
						this.singleLeftCommandItem.setText( cmd.getLabel() );
						cmd = oldLeftCommand;
						priority = oldLeftCommand.getPriority();		
					}
				}
			//#else
				if (type == Command.BACK || type == Command.CANCEL || type == Command.EXIT ) {
					//#if tmp.RightOptions
						if (this.singleLeftCommand == null) {
							this.singleLeftCommand = cmd;
							this.singleLeftCommandItem.setImage( (Image)null );
							this.singleLeftCommandItem.setText( cmd.getLabel() );
							if (this.isInitialised) {
								this.isInitialised = false;
								repaint();
							}
							return;
						} else if ( this.singleLeftCommand.getPriority() > priority ) {
							Command oldLeftCommand = this.singleLeftCommand;
							this.singleLeftCommand = cmd;
							this.singleLeftCommandItem.setText( cmd.getLabel() );
							cmd = oldLeftCommand;
							priority = oldLeftCommand.getPriority();
						}
					//#else
						if (this.singleRightCommand == null) {
							//#debug
							System.out.println("Setting single right command " + cmd.getLabel() );
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
							//#debug
							System.out.println("exchanging right command " + oldRightCommand.getLabel() );
						}
					//#endif
				}
			//#endif
			//#if tmp.RightOptions
				if (this.singleRightCommand != null) {
					// add existing right command first:
					//#style menuitem, menu, default
					CommandItem item = new CommandItem( this.singleRightCommand, this );
					this.commandsList.add( this.singleRightCommand );
					this.commandsContainer.add( item );
					this.singleRightCommand = null;
				}  else if (this.commandsList.size() == 0) {
					// this is the new single right command!
					//#debug
					System.out.println("Setting single right command " + cmd.getLabel() );
					this.singleRightCommand = cmd;
					this.singleRightCommandItem.setText( cmd.getLabel() );
					if (this.isInitialised) {
						this.isInitialised = false;
						repaint();
					}
					return;
				}
			//#else
				if (this.singleLeftCommand != null) {
					// add existing left command first:
					//#debug
					System.out.println("moving single left command " + this.singleLeftCommand.getLabel() + " to commandsContainer");
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
		//#endif
			
		//#if tmp.useInvisibleMenuBar
			if (this.positiveCommand == null 
					&& type != Command.BACK
					&& type != Command.CANCEL
					&& type != Command.EXIT) 
			{
				this.positiveCommand = cmd;
			}
		//#endif
	
		//#debug
		System.out.println("Adding command " + cmd.getLabel() + " to the commands list...");
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
		System.out.println(this + ": removing command " + cmd.getLabel() + " (" + cmd + ")");
		//#if tmp.useInvisibleMenuBar
			if (cmd == this.positiveCommand) {
				this.positiveCommand = null;
			}
		//#endif
		// 1.case: cmd == this.singleLeftCommand
		if ( cmd == this.singleLeftCommand ) {
			this.singleLeftCommand = null;
			if (this.isInitialised) {
				this.isInitialised = false;
				repaint();
			}
			//#if tmp.RightOptions
				if (this.singleRightCommand != null) {
					if ( this.singleRightCommand.getCommandType() == Command.BACK 
						|| this.singleRightCommand.getCommandType() == Command.CANCEL ) 
					{
						this.singleLeftCommand = this.singleRightCommand;
						this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
						this.singleRightCommand = null;
					}
					if (this.isInitialised) {
						this.isInitialised = false;
						repaint();
					}
					return;
				}
				int newSingleRightCommandIndex = getNextNegativeCommandIndex();
				if ( newSingleRightCommandIndex != -1 ) {
					this.singleLeftCommand = (Command) this.commandsList.remove(newSingleRightCommandIndex);
					this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
					this.commandsContainer.remove( newSingleRightCommandIndex );
				}	
				// don't return here yet, since it could well be that there is only
				// one remaining item in the commandsList. In such a case the 
				// single right command is used instead.
	
			//#endif
		}
		
		// 2.case: cmd == this.singleRightCommand
		if ( cmd == this.singleRightCommand ) {
			// remove single right command:
			this.singleRightCommand = null;
			//System.out.println("removing single right command");
			// check if there is another BACK or CANCEL command and 
			// select the one with the highest priority:
			//#if !tmp.RightOptions
				if (this.singleLeftCommand != null) {
					if ( this.singleLeftCommand.getCommandType() == Command.BACK 
						|| this.singleLeftCommand.getCommandType() == Command.CANCEL ) 
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
				int newSingleRightCommandIndex = getNextNegativeCommandIndex();
				if ( newSingleRightCommandIndex != -1 ) {
					this.singleRightCommand = (Command) this.commandsList.remove(newSingleRightCommandIndex);
					this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
					this.commandsContainer.remove( newSingleRightCommandIndex );
				}	
				// don't return here yet, since it could well be that there is only
				// one remaining item in the commandsList. In such a case the 
				// single left command is used instead.

			//#endif

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
			CommandItem item = (CommandItem) this.commandsContainer.get( 0 );
			if (!item.hasChildren) {
				Command command = (Command) this.commandsList.remove( 0 );
				this.commandsContainer.remove( 0 );
				//#if tmp.RightOptions
					this.singleRightCommand = command;
					this.singleRightCommandItem.setText( command.getLabel() );
				//#else
					this.singleLeftCommand = command;
					this.singleLeftCommandItem.setText( command.getLabel() );
				//#endif
			}
		}
		//#endif

		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}
	}

	private int getNextNegativeCommandIndex() {
	
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
		return maxPriorityId;
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
			//#if polish.Screen.maxMenuWidthInPercent:defined
				//#= this.commandsContainerWidth = (this.screen.screenWidth * ${polish.Screen.maxMenuWidthInPercent}) / 100;
			//#else
				this.commandsContainerWidth = this.screen.screenWidth;
			//#endif
			int containerHeight = this.commandsContainer.getItemHeight(this.commandsContainerWidth, this.commandsContainerWidth);
			this.commandsContainerWidth = this.commandsContainer.itemWidth;
			//#debug
			System.out.println("commandsContainerWidth=" + this.commandsContainerWidth);
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
				//#if tmp.OkCommandOnLeft
					IconItem item = this.singleLeftCommandItem;
				//#elif tmp.RightOptions
					//# IconItem item = this.singleRightCommandItem;
				//#else
					//# IconItem item = this.singleLeftCommandItem;
				//#endif
				if (this.selectImage != null) {
					item.setImage( this.selectImage );
					if (this.showImageAndText) {
						//#ifdef polish.i18n.useDynamicTranslations
							item.setText( Locale.get( "polish.command.select" ) ); 
						//#elifdef polish.command.select:defined
							//#= item.setText( "${polish.command.select}" );
						//#else
							item.setText( "Select" );
						//#endif
					} else {
						item.setText( null );
					}
				} else {
					item.setImage( (Image)null );
					//#ifdef polish.i18n.useDynamicTranslations
						item.setText( Locale.get( "polish.command.select" ) ); 
					//#elifdef polish.command.select:defined
						//#= item.setText( "${polish.command.select}" );
					//#else
						item.setText( "Select" );
					//#endif
				}
				//#if tmp.OkCommandOnLeft
					this.singleRightCommandItem.setText(null);
					this.singleRightCommandItem.setImage( (Image)null );
				//#else
					//#if tmp.RightOptions
						item = this.singleLeftCommandItem;
					//#else
						//# item = this.singleRightCommandItem;
					//#endif
					if (this.cancelImage != null) {
						item.setImage( this.cancelImage );
						if (this.showImageAndText) {
							//#ifdef polish.i18n.useDynamicTranslations
								item.setText( Locale.get( "polish.command.cancel" ) ); 
							//#elifdef polish.command.cancel:defined
								//#= item.setText(  "${polish.command.cancel}" );
							//#else
								item.setText( "Cancel" );
							//#endif
						} else {
							item.setText( null );
						}
					} else {
						//#ifdef polish.i18n.useDynamicTranslations
							item.setText( Locale.get( "polish.command.cancel" ) ); 
						//#elifdef polish.command.cancel:defined
							//#= item.setText(  "${polish.command.cancel}"  );
						//#else
							item.setText( "Cancel" );
						//#endif
					}
				//#endif
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
				//#if tmp.RightOptions
					if (this.singleLeftCommand != null) {
						this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
					} else {
						this.singleLeftCommandItem.setText( null );
					}
					this.singleLeftCommandItem.setImage( (Image)null );
				//#else
					if (this.singleRightCommand != null) {
						this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
					} else {
						this.singleRightCommandItem.setText( null );
					}
					this.singleRightCommandItem.setImage( (Image)null );
				//#endif
				if (this.commandsList.size() > 0) {
					//#if tmp.RightOptions
						IconItem item = this.singleRightCommandItem;
					//#else
						//# IconItem item = this.singleLeftCommandItem;
					//#endif
					if (this.optionsImage != null) {
						item.setImage( this.optionsImage );
						if (this.showImageAndText) {
							//#ifdef polish.i18n.useDynamicTranslations
								item.setText( Locale.get( "polish.command.options" ) ); 
							//#elifdef polish.command.options:defined
								//#= item.setText( "${polish.command.options}" );
							//#else
								item.setText( "Options" );				
							//#endif
						} else {
							item.setText( null );
						}
					} else {
						item.setImage( (Image)null );
						//#ifdef polish.i18n.useDynamicTranslations
							item.setText( Locale.get( "polish.command.options" ) ); 
						//#elifdef polish.command.options:defined
							//#= item.setText( "${polish.command.options}" );
						//#else
							item.setText( "Options" );				
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
			// paint overlay background:
			//#if !polish.Bugs.noTranslucencyWithDrawRgb
				if (this.overlayBackground != null) {
					this.overlayBackground.paint( 0, this.screen.contentY, this.screen.screenWidth , this.screen.screenHeight - this.screen.contentY, g );
				}
			//#endif
			// paint opened menu:
			//System.out.println("setting clip " + this.topY + ", " + (this.screen.screenHeight - this.topY) );
			//#if tmp.useInvisibleMenuBar
				// paint menu at the top right corner:
				g.setClip(0, this.screen.contentY, this.screen.screenWidth , this.screen.screenHeight - this.screen.contentY);
				this.commandsContainer.paint( this.screen.screenWidth - this.commandsContainerWidth, this.topY, this.screen.screenWidth - this.commandsContainerWidth, this.screen.screenWidth, g);
				g.setClip(0, 0, this.screen.screenWidth , this.screen.screenHeight );
			//#elif tmp.RightOptions
				// paint menu at the lower right corner:
				g.setClip(0, this.topY, this.screen.screenWidth , this.screen.screenHeight - this.topY);
				this.commandsContainer.paint( this.screen.screenWidth - this.commandsContainerWidth, this.commandsContainerY, this.screen.screenWidth - this.commandsContainerWidth, this.screen.screenWidth, g);
				g.setClip(0, 0, this.screen.screenWidth , this.screen.screenHeight );
			//#else
				// paint menu at the lower left corner:
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
			//#if tmp.RightOptions
				//# if (this.singleLeftCommand != null) {
			//#else
				if (this.commandsContainer.size() > 0 || this.singleLeftCommand != null) {
			//#endif
				//System.out.println("painting left command from " + leftBorder + " to " + centerX );
				this.singleLeftCommandItem.paint(leftBorder, y + this.singleLeftCommandY, leftBorder, centerX, g);
			}
			//#if tmp.RightOptions
				if (this.commandsContainer.size() > 0 || this.singleRightCommand != null) {
			//#else
				//# if (this.singleRightCommand != null) {
			//#endif
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
	 * 
	 * @param open true when the menu should be opened
	 */
	protected void setOpen( boolean open ) {
		this.isInitialised = (open == this.isOpened);
		if (!open && this.isOpened) {
			this.commandsContainer.hideNotify();
		} else if (open && !this.isOpened) {
			//#if !polish.MenuBar.focusFirstAfterClose
				// focus the first item again, so when the user opens the menu again, it will be "fresh" again
				this.commandsContainer.focus(0);
			//#endif
			this.commandsContainer.showNotify();
		}
		this.isOpened = open;
		this.isInitialised = false;
	}
	


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("MenuBar: handleKeyPressed(" + keyCode + ", " + gameAction  );
		this.isSoftKeyPressed = false;
		if (this.isOpened) {
			if (keyCode == this.selectOptionsMenuKey) {
				this.isSoftKeyPressed = true;			
				CommandItem commandItem = (CommandItem) this.commandsContainer.getFocusedItem();
				//#if tmp.useInvisibleMenuBar
					if (commandItem.command == this.hideCommand ) {
						setOpen( false );
						return true;
					}
					//TODO find a way how to handle the hide command on BlackBerry when it is invoked directly...
				//#endif
				commandItem.handleKeyPressed( 0, Canvas.FIRE );
	//			boolean handled = commandItem.handleKeyPressed( 0, Canvas.FIRE );
	//			if (!handled) { // CommandItem returns false when it invokes the command listener
				// this is now done automatically by the Screen class
	//				setOpen( false );
	//			}				
				return true;
			//#if polish.key.ReturnKey:defined
				//#= } else  if (keyCode == this.closeOptionsMenuKey || keyCode == ${polish.key.ReturnKey}) {
			//#else
				} else  if (keyCode == this.closeOptionsMenuKey) {
			//#endif
				this.isSoftKeyPressed = true;
				int selectedIndex = this.commandsContainer.getFocusedIndex();
				if (!this.commandsContainer.handleKeyPressed(0, Canvas.LEFT)
						|| selectedIndex != this.commandsContainer.getFocusedIndex() ) 
				{
					setOpen( false );
				}
				//System.out.println("MenuBar is closing due to key " + keyCode);
				return true;
			} else {
				if (keyCode != 0) {
					gameAction = this.screen.getGameAction(keyCode);
				}
				//#if tmp.useInvisibleMenuBar
					// handle hide command specifically:
					if (  gameAction == Canvas.FIRE && ((CommandItem)this.commandsContainer.focusedItem).command == this.hideCommand ) {
						setOpen( false );
						return true;
					}
				//#endif
//				if (gameAction == Canvas.FIRE) {
//					int focusedIndex = this.commandsContainer.focusedIndex;
//					Command command = (Command) this.commandsList.get(focusedIndex);
//					setOpen( false );
//					this.screen.callCommandListener(command);
//					return true;
//				}
				boolean handled = this.commandsContainer.handleKeyPressed(keyCode, gameAction);
				if (handled) {
					this.isInitialised = false;
				} else { 
					if (gameAction == Canvas.DOWN || gameAction == Canvas.UP) {
						//#debug error
						System.out.println("Container DID NOT HANDLE DOWN OR UP, selectedIndex=" + this.commandsContainer.getFocusedIndex() + ", count="+ this.commandsContainer.size() );
					}
					setOpen( false );
				}
				return handled;				
			}
		} else {
			if (keyCode == LEFT_SOFT_KEY && this.singleLeftCommand != null) {
				this.isSoftKeyPressed = true;			
				this.screen.callCommandListener(this.singleLeftCommand);
				return true;			
			} else if (keyCode == RIGHT_SOFT_KEY && this.singleRightCommand != null) {
				this.isSoftKeyPressed = true;			
				this.screen.callCommandListener(this.singleRightCommand);
				return true;			
			} else if (keyCode == this.openOptionsMenuKey ) {
				this.isSoftKeyPressed = true;			
				//#if tmp.useInvisibleMenuBar
					if ( !this.isOpened && this.positiveCommand != null 
							&& ((this.singleRightCommand != null && this.commandsContainer.size() == 3) ) )
							//|| (this.singleRightCommand == null && this.commandsContainer.size() == 2)) ) 
					{
						// invoke positive command:
						this.screen.callCommandListener(this.positiveCommand);
						return true;
					} else 
				//#endif
				if (this.commandsList.size() > 0) {
					setOpen( true );
					return true;
				}
			}
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
			boolean isCloseKeySelected;
			boolean isOpenKeySelected;
			
			//#if tmp.RightOptions
				isCloseKeySelected = x > rightCommandStartX;
				isOpenKeySelected =  x < leftCommandEndX;
			//#else
				isCloseKeySelected = x < leftCommandEndX;
				isOpenKeySelected = x > rightCommandStartX;
			//#endif
			if (this.isOpened) {
				if ( isCloseKeySelected ) {
					//System.out.println("selecting command from opened menu");
					setOpen( false );
					Command command = (Command) this.commandsList.get( this.commandsContainer.focusedIndex );
					this.screen.callCommandListener(command);
				} else if ( isOpenKeySelected ) {
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
			y -= this.commandsContainerY;
			boolean handled = this.commandsContainer.handlePointerPressed(x, y);
			return handled;
//			setOpen( false );
//			// a menu-item could have been selected:
//			if (x <= this.commandsContainer.xLeftPos + this.commandsContainerWidth 
//					&& y >= this.commandsContainerY ) 
//			{
//				
//				//boolean handled = this.commandsContainer.handlePointerPressed( x, y );
//				//System.out.println("handling pointer pressed in open menu at " + x + ", " + y + ": " + handled);
//				this.commandsContainer.handlePointerPressed( x, y );
//				int focusedIndex = this.commandsContainer.getFocusedIndex();
//				Command cmd = (Command) this.commandsList.get( focusedIndex );
//				this.screen.callCommandListener( cmd );						
//				return true;
//			}
		}
		return false;
	}
	//#endif
	
	
	public void setStyle(Style style) {
		//#if !polish.Bugs.noTranslucencyWithDrawRgb
			if (this.overlayBackground == null) {
				//#if polish.color.overlay:defined
					int color = 0;
					//#= color =  ${polish.color.overlay};
					if ((color & 0xFF000000) != 0) {
						this.overlayBackground = new TranslucentSimpleBackground( color );
					}
				//#else
					this.overlayBackground = new TranslucentSimpleBackground( 0xAA000000 );
				//#endif
	
			}
		//#endif
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
        //#if tmp.useInvisibleMenuBar
	        if (parentCommand == this.positiveCommand) {
	            this.positiveCommand = null;
	        }
	    //#endif
		// find parent CommandItem, could be tricky, especially when there are nested commands over several layers
		int index = this.commandsList.indexOf( parentCommand );
		CommandItem parentCommandItem = null;
		if (index != -1) {
			// found it:
			parentCommandItem = (CommandItem) this.commandsContainer.get( index );
		} else if (parentCommand == this.singleLeftCommand ){
			parentCommandItem = new CommandItem( parentCommand, this );
			this.commandsContainer.add( parentCommandItem );
			this.commandsList.add( parentCommand );
			this.singleLeftCommand = null;
		} else if (parentCommand == this.singleRightCommand ){
			parentCommandItem = new CommandItem( parentCommand, this );
			this.commandsContainer.add( parentCommandItem );
			this.commandsList.add( parentCommand );
			this.singleRightCommand = null;
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

	/**
	 * Removes all commands from this MenuBar.
	 * This option is only available when the "menu" fullscreen mode is activated.
	 */
	public void removeAllCommands() {
		this.commands.clear();
		this.commandsContainer.clear();
	}

	
	
	
//#ifdef polish.MenuBar.additionalMethods:defined
	//#include ${polish.MenuBar.additionalMethods}
//#endif

}
