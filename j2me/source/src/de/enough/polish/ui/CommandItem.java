//#condition polish.usePolishGui
/*
 * Created on Mar 4, 2006 at 3:17:15 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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


/**
 * <p>Wraps a javax.microedition.lcdui.Command object and allows to add subcommands, specific styles etc to single commands.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Mar 4, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandItem extends IconItem {
	
	protected final Command command;
	private Container children;
	protected boolean hasChildren;
	private int childIndicatorWidth = 5;
	private int childIndicatorYOffset;
	private int childIndicatorHeight = 5;
	//#if polish.css.command-child-indicator
		private Image childIndicator;
	//#endif
	//#if polish.css.command-child-indicator-color
		private int childIndicatorColor = -1;
	//#endif
	private boolean isOpen;

	
	/**
	 * Creates a new command item.
	 * 
	 * @param command the commmand represented by this item.
	 * @param parent the parent item
	 */
	public CommandItem( Command command, Item parent ) {
		this( command, parent, null );
	}

	/**
	 * Creates a new command item.
	 * 
	 * @param command the commmand represented by this item.
	 * @param parent the parent item
	 * @param style the style for this item
	 */
	public CommandItem( Command command, Item parent, Style style ) {
		super( null, command.getLabel(), null, style );
		this.appearanceMode = Item.INTERACTIVE;
		this.command = command;
		this.parent = parent;
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param childCommand the child command
	 */
	public void addChild( Command childCommand ) {
		addChild( childCommand, null );
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param childCommand the child command
	 * @param childStyle the style for the child command
	 */
	public void addChild( Command childCommand, Style childStyle ) {
		CommandItem child = new CommandItem( childCommand, this, childStyle );
		addChild( child );
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param child the child command item
	 */
	public void addChild(CommandItem child) {
		boolean inserted = false;
		if ( this.children == null ) {
			int layer = getLayer();
			if (layer == 0) {
				//#style menu1?
				this.children = new Container( true );
			} else if (layer == 1) {
				//#style menu2?
				this.children = new Container( true );
			} else if (layer == 2) {
				//#style menu3?
				this.children = new Container( true );
			}
			if (this.children == null) {
				this.children = new Container( true, this.parent.style );
			} else if (this.children.style == null) {
				this.children.style = this.parent.style;
			}
			this.hasChildren = true;
			this.children.parent = this;
		} else {
			int priority = child.command.getPriority();
			for (int i = 0; i < this.children.size(); i++) {
				CommandItem item = (CommandItem) this.children.get(i);
				if (item.command.getPriority() > priority ) {
					this.children.add( i, child );
					inserted = true;
					break;
				}
			}
		}
		if (!inserted) {
			this.children.add( child );
		}
	}

	/**
	 * Retrieves the layer to which this command item belongs.
	 * This method is useful for sub commands.
	 * 
	 * @return the layer, 0 means this command item belongs to the main commands.
	 */
	public int getLayer() {
		Item parentItem = this.parent;
		int layer = 0;
		while (parentItem != null) {
			while (! (parentItem instanceof CommandItem) ) {
				//System.out.println("getLayer - skipping parent " + parentItem );
				parentItem = parentItem.parent;
				if (parentItem == null) {
					//System.out.println("getLayer - returning because of null parent ");
					return layer;
				}
			}
			if (parentItem == null) {
				return layer;
			}
			parentItem = parentItem.parent;
			layer++;
		}
		return layer;
	}
	
	/**
	 * Removes a child from this command item.
	 * 
	 * @param childCommand the child that should be removed
	 * @return true when the child was found
	 */
	public boolean removeChild( Command childCommand ) {
		if (this.children == null) {
			return false;
		}
		for ( int i=0; i < this.children.size(); i++ ) {
			CommandItem item = (CommandItem) this.children.get(i);
			if ( item.command == childCommand ) {
				this.children.remove( i );
				return true;
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int lineWidth) {
		if (this.hasChildren) {
			firstLineWidth -= this.childIndicatorWidth + this.paddingHorizontal;
			lineWidth -= this.childIndicatorWidth + this.paddingHorizontal;
		}
		
		super.initContent(firstLineWidth, lineWidth);
		
		if (this.hasChildren) {
			this.contentWidth += this.childIndicatorWidth + this.paddingHorizontal;
			if ( this.childIndicatorHeight > this.contentHeight ) {
				this.contentHeight = this.childIndicatorHeight;
			} else {
				this.childIndicatorYOffset = (this.contentHeight - this.childIndicatorHeight) / 2;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO robertvirkus adjust yOffset when childIndicatorHeight > contentHeight
		super.paintContent(x, y, leftBorder, rightBorder, g);
		
//		if (this.isFocused) {
//			System.out.println( this + ": backgroundYOffset=" + this.backgroundYOffset);
//		}
		if (this.hasChildren) {
			// paint child indicator
			//rightBorder -= this.childIndicatorWidth;
			//#if polish.css.command-child-indicator
				if (this.childIndicator != null) {
					int height = this.childIndicator.getHeight(); // center image if lower than the font:
					g.drawImage( this.childIndicator, rightBorder, y + (this.contentHeight - height)/2, Graphics.TOP | Graphics.RIGHT );
				} else {
			//#endif
					//#if polish.css.command-child-indicator-color
						if ( this.childIndicatorColor != -1 ) {
							g.setColor( this.childIndicatorColor );							
						}
					//#endif
					x = rightBorder - this.childIndicatorWidth;
					int indicatorY = y + this.childIndicatorYOffset;
					//#if polish.midp2
						g.fillTriangle(x, indicatorY, rightBorder, indicatorY + this.childIndicatorHeight/2, x, indicatorY + this.childIndicatorHeight );
					//#else
						g.drawLine( x, indicatorY, rightBorder, indicatorY + this.childIndicatorHeight/2 );
						g.drawLine( x, indicatorY + this.childIndicatorHeight, rightBorder, indicatorY + this.childIndicatorHeight/2 );
						g.drawLine( x, indicatorY, x, indicatorY + this.childIndicatorHeight );
					//#endif
			//#if polish.css.command-child-indicator
				}
			//#endif
			
			if (this.isOpen) {
				int originalY = y;
				// draw children:
				// when there is enough space to the right, open it on the right side, otherwise open it on the left:
				int clipX = g.getClipX();
				int clipWidth = g.getClipWidth();
				int clipY = g.getClipY() - 1;
				int clipHeight = g.getClipHeight();
//				g.setColor(0x00FF00);
//				g.fillRect(clipX, clipY, clipWidth, clipHeight);
				
				int availableWidth = (clipWidth * 2) / 3;
				int childrenWidth = this.children.getItemWidth( availableWidth, availableWidth );
				int childrenHeight = this.children.itemHeight; // is initialised because of the getItemWidth() call
				rightBorder += this.paddingHorizontal;
//				System.out.println("drawing children: children-width " + childrenWidth + ", clipWidth=" + clipWidth + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder);
//				System.out.println("rightBorder + childrenWidth=" + (rightBorder + childrenWidth) + ", clipX + clipWidth=" + ( clipX + clipWidth ));
//				System.out.println("clipX + clipWidth - childrenWidth=" + (clipX + clipWidth - childrenWidth) + ", (leftBorder + 10)=" + (leftBorder + 10));
//				System.out.println("clipX=" + clipX + ", leftBorder - childrenWidth=" + (leftBorder - childrenWidth));
				if ( rightBorder + childrenWidth < clipX + clipWidth ) {					
					x = rightBorder;
				} else if ( clipX + clipWidth - childrenWidth > (leftBorder + 10) ) {
					x = clipX + clipWidth - (childrenWidth + 1);
				} else {
					x = Math.max( leftBorder - childrenWidth, clipX );
				}
				//System.out.println("submenu: y=" + y + ", y + childrenHeight=" + (y + childrenHeight) + ", clipY + clipHeight=" + (clipY + clipHeight));
				if ( y + childrenHeight > clipY + clipHeight ) {
					y -= (y + childrenHeight) - (clipY + clipHeight);
					//System.out.println("submenu: adjusted y=" + y + ", clipY=" + clipY);
					if ( y < clipY ) {
						y = clipY;
					}
//					g.setColor( 0x00FFFF);
//					for (int i = 0; i < 15; i++) {
//						g.drawLine( x - 20,y-i, x+childrenWidth+10, y-i);						
//					}
				}
				this.children.relativeX = x - leftBorder;
				this.children.relativeY = y - originalY;
				this.children.setScrollHeight( clipHeight );
				this.children.paint( x, y, x, x + childrenWidth, g);
				//System.out.println("set height for children to " + clipHeight + ", yOffset=" + this.children.yOffset + ", internalY=" + this.children.internalY);				
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println( this + " handleKeyPressed, isOpen=" + this.isOpen);
		if ( this.isOpen ) {
			if (gameAction == Canvas.LEFT) {
				// close menu:
				open( false );
			} else {
				boolean handled = this.children.handleKeyPressed(keyCode, gameAction);
				if (!handled) {
					open( false );
				}
			}
			return true;
		} else if ( this.hasChildren && this.appearanceMode != PLAIN ) { // has children but is not open
			if ( gameAction == Canvas.FIRE || gameAction == Canvas.RIGHT ) {
				open( true );
				return true;
			}
		} else if ( gameAction == Canvas.FIRE  && this.appearanceMode != PLAIN ){ // has no children:
			// fire command action event:
			//#debug
			System.out.println( this + " invoking command " + this.command.getLabel() );
			Screen scr = getScreen();
			//#if polish.debug.error
			if (scr == null) {
				//#debug error
				System.out.println("Unable to retrieve screen for " + this + ", parent=" + this.parent );
			}
			//#endif
			scr.callCommandListener( this.command );
			return true;
		}
		return false;
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		boolean handled = false;
		if (this.isOpen) {
			handled = this.children.handlePointerPressed(x - this.children.relativeX, y - this.children.relativeY );
		}
		return handled || super.handlePointerPressed(x, y);
	}
	//#endif

	/**
	 * Opens or closes this command item so that the children commands are visible (or not).
	 * 
	 * @param open true when the children should be visible and the focus is moved to them.
	 */
	protected void open(boolean open) {
		//#debug
		System.out.println( this + ": opening children: " + open);
		this.isOpen = open;
		if ( open ) {
			// move focus to first child:
			this.children.focus( getFocusedStyle(), 0 );
		} else {
			// focus myself:
			// reset selected command element to the first one in the list:
			if ( this.children != null ) {
				this.children.focus( 0 );
				this.children.setScrollYOffset( 0 );
			}
		}
	}
	

	/**
	 * Retrieves the child item for the specified command.
	 * 
	 * @param parentCommand the corresponding command
	 * @return the corresponding item or null when the child is not found
	 */
	public CommandItem getChild(Command parentCommand) {
		if (!this.hasChildren) {
			return null;
		}
		for ( int i=0; i<this.children.size(); i++ ) {
			CommandItem child = (CommandItem) this.children.get(i);
			if ( child.command == parentCommand ) {
				return child;
			} else if (child.hasChildren) {
				child = child.getChild(parentCommand);
				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.command-child-indicator-color
			Integer childIndicatorColorInt = style.getIntProperty("command-child-indicator-color");
			if ( childIndicatorColorInt != null ) {
				this.childIndicatorColor = childIndicatorColorInt.intValue();
			}
		//#endif
		//#if polish.css.command-child-indicator
			if (this.hasChildren) {
				String childIndicatorUrl = style.getProperty( "command-child-indicator" );
				if (childIndicatorUrl != null) {
					try {
						this.childIndicator = StyleSheet.getImage(childIndicatorUrl, this, true);
						this.childIndicatorWidth = this.childIndicator.getWidth();
						this.childIndicatorHeight = this.childIndicator.getHeight();
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load command-child-indicator[ " + childIndicatorUrl + "] " + e );
					}
				}
			}
			if (this.hasChildren && this.childIndicator == null) {
		//#endif			
				this.childIndicatorWidth = this.font.getHeight();
				this.childIndicatorHeight = this.childIndicatorWidth;
		//#if polish.css.command-child-indicator
			}
		//#endif
		//#if polish.css.command-child-indicator-width
			Integer childIndicatorWidthInt = style.getIntProperty("command-child-indicator-width");
			if ( childIndicatorWidthInt != null ) {
				this.childIndicatorWidth = childIndicatorWidthInt.intValue();
			}
		//#endif
		//#if polish.css.command-child-indicator-height
			Integer childIndicatorHeightInt = style.getIntProperty("command-child-indicator-height");
			if ( childIndicatorHeightInt != null ) {
				this.childIndicatorHeight = childIndicatorHeightInt.intValue();
			}
		//#endif

	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.isOpen && this.isFocused) {
			animated |= this.children.animate();
		}
		return animated;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#hideNotify()
	 */
	protected void hideNotify() {
		if (this.hasChildren) {
			this.children.hideNotify();
			if (this.isOpen) {
				open( false );
			}
		}
		super.hideNotify();
	}

	/**
	 * @return
	 */
	public Command getCommand() {
		return this.command;
	}


	
	
	


}
