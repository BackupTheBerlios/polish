//#condition polish.usePolishGui
/*
 * Created on 30-Dez-2005 at 16:32:21.
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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Provides a tree of items that can contain several branches.</p>
 * <p>Each tree branch behaves like a normal J2ME Polish container, so 
 *    you can specify view-types, columns, colspans, etc.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2008</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TreeItem 
//#if polish.LibraryBuild
	extends FakeContainerCustomItem
//#else
	//# extends Container
//#endif
 
{
	
	//#if polish.css.treeitem-closed-indicator && polish.css.treeitem-opened-indicator
		//#define tmp.useIndicators
		private Image closedIndicator;
		private Image openedIndicator;
		private int indicatorWidth;
	//#endif

	
	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 */
	public TreeItem(String label ) {
		this( label, null );
	}

	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 * @param style the style
	 */
	public TreeItem(String label, Style style) {
		super( false, style );
		setLabel( label );
	}
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param item the item that should be added
	 */
	public void appendToRoot( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 */
	public void appendToNode( javax.microedition.lcdui.Item node, javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @return the created item
	 */
	public javax.microedition.lcdui.Item appendToNode( javax.microedition.lcdui.Item node, String text, Image image ) {
		// ignore, only for the users
		return null;
	}
	//#endif


	//#if polish.LibraryBuild
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
		return false;
	}
	//#endif
	

	/**
	 * Appends the specified text and image to this list.
	 * 
	 * @param text the text
	 * @param image the image
	 * @return the created item
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image ) {
		return null;
	//#else
		//# public Item appendToRoot( String text, Image image ) {
			//# return appendToRoot( text, image, null );
	//#endif
	}

	/**
	 * Appends the specified text and image to this list and provides it with the given style.
	 * 
	 * @param text the text
	 * @param image the image
	 * @param rootStyle the style
	 * @return return the created item
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image, Style rootStyle ) {
	//#else
		//# public Item appendToRoot( String text, Image image, Style rootStyle ) {
	//#endif
		IconItem item = new IconItem( text, image, rootStyle );
		appendToRoot( item );
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return item;
		//#endif
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void appendToRoot( Item item ) {
		add(item);
//		this.lastAddedItem = item;
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 * @param nodeStyle the style
	 */
	public void appendToRoot( Item item, Style nodeStyle ) {
		if (nodeStyle != null) {
			item.setStyle( nodeStyle );
		}
		add(item);
//		this.lastAddedItem = item;
	}
	
	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @return the created item
	 */
	public Item appendToNode( Item node, String text, Image image ) {
		return appendToNode(node, text, image, null);
	}

	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @param childStyle the style
	 * @return the created item
	 */
	public Item appendToNode( Item node, String text, Image image, Style childStyle ) {
		IconItem item = new IconItem( text, image);
		appendToNode( node, item, childStyle );
		return item;
	}

	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 */
	public void appendToNode( Item node, Item item ) {
		appendToNode(node, item, null);
	}
	
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 * @param nodeStyle the style
	 */
	public void appendToNode( Item node, Item item, Style nodeStyle  ) {
		if (nodeStyle != null) {
			item.setStyle( nodeStyle );
		}
		// find correct Node:
		Node parentNode;
		if ( !(node.parent instanceof Node) ) {
			// the item has to be converted into a node:
			Container parentContainer;
			//#if polish.LibraryBuild
				if ( (Object)node.parent == this) {
					// this is a root item:
					parentContainer = (Container) ((Object)this);
			//#else
				//# if (node.parent == this) {
				// this is a root item:
				//# parentContainer = this;
				//#endif
			} else {
				parentContainer = (Container) node.parent;
			}
			parentNode = new Node( node );
			Item[] myItems = parentContainer.getItems();
			for (int i = 0; i < myItems.length; i++) {
				Item rootItem = myItems[i];
				if ( node == rootItem ) {
					parentContainer.set(i, parentNode);
					node.parent = parentNode;
					break;
				}
			}
		} else {
			parentNode = ((Node)node.parent);
		}
		item.parent = parentNode;
		parentNode.addChild(item);
//		this.lastAddedItem = item;
		
	}

	
	/**
	 * Clears this list.
	 */
	public void removeAll() {
		clear();
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if tmp.useIndicators
			String closedUrl = style.getProperty("treeitem-closed-indicator");
			if (closedUrl != null) {
				try {
					this.closedIndicator = StyleSheet.getImage(closedUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load treeitem-closed-indicator " + closedUrl + e );
				}
			}
			String openedUrl = style.getProperty("treeitem-opened-indicator");
			if (openedUrl != null) {
				try {
					this.openedIndicator = StyleSheet.getImage(openedUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load treeitem-opened-indicator " + openedUrl + e );
				}
			}
		//#endif
	}






	class Node extends Item {
		private final Item root;
		private final Container children;
		private boolean isExpanded;
		int xLeftOffset = 10;
		private Style rootFocusedStyle;
		private Style rootPlainStyle;
		//private boolean isChildrenFocused;
//		private int availableWidth;
		
		public Node( Item root ) {
			super( null, 0, INTERACTIVE, null );
			this.root = root;
			this.root.parent = this;
			this.children = new Container( false );
			this.children.parent = this;
		}
		
		public void addChild( Item child ) {
			this.children.add( child );
		}

		protected void initContent(int firstLineWidth, int lineWidth) {
			//#debug
			System.out.println("Node (" + this.root + ").initContent()");
//			this.availableWidth = lineWidth - this.xLeftOffset;
			this.root.init(firstLineWidth, lineWidth);
			this.children.relativeX = this.xLeftOffset;
			this.children.relativeY = this.root.itemHeight;
			
			int rootWidth = this.root.itemWidth;
			//#if tmp.useIndicators
				int w = 0;
				if (TreeItem.this.openedIndicator != null) {
					w = TreeItem.this.openedIndicator.getWidth();
				}
				if (TreeItem.this.closedIndicator != null && TreeItem.this.closedIndicator.getWidth() > w) {
					w = TreeItem.this.closedIndicator.getWidth();
				}
				if (w != 0) {
					rootWidth += w + this.paddingHorizontal;
				}
				TreeItem.this.indicatorWidth = w;
			//#endif
			if (!this.isExpanded) {
				this.contentWidth = rootWidth;
				this.contentHeight = this.root.itemHeight;
			} else {
				lineWidth -= this.xLeftOffset;
				this.children.init(lineWidth, lineWidth);
				this.contentWidth = Math.max(rootWidth, this.children.itemWidth + this.xLeftOffset);
				this.contentHeight = this.root.itemHeight + this.paddingVertical + this.children.itemHeight;
			}
		}

		protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
			//#if tmp.useIndicators
				Image image;
				if (this.isExpanded) {
					image = TreeItem.this.openedIndicator;
				} else {
					image = TreeItem.this.closedIndicator;
				}
				if (image != null) {
					int height = image.getHeight();
					int rootHeight = this.root.itemHeight;
					g.drawImage(image, x, y + (rootHeight-height)/2, Graphics.TOP | Graphics.RIGHT );
				}
				x += TreeItem.this.indicatorWidth;
			//#endif
			this.root.paint(x, y, leftBorder, rightBorder, g);
			if (this.isExpanded) {
				leftBorder += this.xLeftOffset;
				x = leftBorder;
				y += this.root.itemHeight + this.paddingVertical;
				this.children.paint(x, y, leftBorder, rightBorder, g);
			}
		}

		//#ifdef polish.useDynamicStyles	
		protected String createCssSelector() {
			return "node";
		}
		//#endif

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
		 */
		protected boolean handleKeyPressed(int keyCode, int gameAction) {
			//#debug
			System.out.println("Node " + this + " handleKeyPressed: isExpanded=" + this.isExpanded);
			boolean handled = false;
			if (this.isExpanded) {
 				if (this.children.isFocused) {
					handled = this.children.handleKeyPressed(keyCode, gameAction);
					if (handled) {
						if (this.children.internalX != NO_POSITION_SET) {
							this.internalX = this.children.relativeX + this.children.contentX + this.children.internalX;
							this.internalY = this.children.relativeY + this.children.contentY + this.children.internalY;
							this.internalWidth = this.children.internalWidth;
							this.internalHeight = this.children.internalHeight;
						} else {
							this.internalX = this.children.relativeX;
							this.internalY = this.children.relativeY;
							this.internalWidth = this.children.itemWidth;
							this.internalHeight = this.children.itemHeight;
						}
						//System.out.println("TreeItem: children handled keyPressed, internal area: y=" + this.internalY + ", height=" + this.internalHeight );
					} else if (gameAction == Canvas.UP) {
						// focus this root:
						focusRoot();
						handled = true;
					}
				} else if (gameAction == Canvas.DOWN && this.children.appearanceMode != PLAIN) {
					// move focus to children
					if (this.rootPlainStyle != null) {
						this.root.defocus(this.rootPlainStyle);
					}
					this.children.focus(null, gameAction);
					//this.isChildrenFocused = true;
					handled = true;
				}

			}
			if (!handled && gameAction == Canvas.FIRE ) {
				handled = this.root.notifyItemPressedStart() || this.children.size() > 0;
				if (this.isExpanded) { //will be closed in keyReleased
					this.internalX = 0;
					this.internalY = 0;
					this.internalHeight = this.root.itemHeight;
				} else if (this.children.size() > 0) { // will be opened in keyReleased
					this.internalX = 0;
					this.internalY = 0;
					this.internalHeight = this.root.itemHeight + this.paddingVertical + (this.children.itemHeight != 0 ? this.children.itemHeight : 30);
					//System.out.println("about to open children: internalHeight=" + this.internalHeight);
				}
			}
			return handled;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
		 */
		protected boolean handleKeyReleased(int keyCode, int gameAction) {
			//#debug
			System.out.println("Node " + this + " handleKeyReleased: isExpanded=" + this.isExpanded);
			boolean handled = false;
			if (this.isExpanded) {
 				if (this.children.isFocused) {
					handled = this.children.handleKeyReleased(keyCode, gameAction);
					if (handled) {
						if (this.children.internalX != NO_POSITION_SET) {
							this.internalX = this.children.relativeX + this.children.contentX + this.children.internalX;
							this.internalY = this.children.relativeY + this.children.contentY + this.children.internalY;
							this.internalWidth = this.children.internalWidth;
							this.internalHeight = this.children.internalHeight;
						} else {
							this.internalX = this.children.relativeX;
							this.internalY = this.children.relativeY;
							this.internalWidth = this.children.itemWidth;
							this.internalHeight = this.children.itemHeight;
						}
					}
				}

			}
			if (!handled && gameAction == Canvas.FIRE) {
				this.root.notifyItemPressedEnd();
				setExpanded( !this.isExpanded );
				handled = true;
			}
			return handled;
		}
		
		//#ifdef polish.hasPointerEvents
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
		 */
		protected boolean handlePointerPressed(int x, int y) {
			boolean handled = false;
			if (this.isExpanded) {
				handled = this.children.handlePointerPressed(x - this.children.relativeX, y - this.children.relativeY );
			}
			//#debug
			System.out.println("Node: " + this + " handled pointerPressed=" + handled);
			return handled || super.handlePointerPressed(x, y);
		}
		//#endif
		
		//#ifdef polish.hasPointerEvents
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
		 */
		protected boolean handlePointerReleased(int x, int y) {
			boolean handled = false;
			if (this.isExpanded) {
				handled = this.children.handlePointerReleased(x - this.children.relativeX, y - this.children.relativeY );
				if (handled) {
					if (!this.children.isFocused) {
						this.children.isFocused = true;
					}
					if (this.rootPlainStyle != null) {
						this.root.setStyle( this.rootPlainStyle );
					}
				} else if ( this.root.isInItemArea(x, y)) {
					if (this.children.isFocused) {
						focusRoot();
					}
					setExpanded( false );
					handled = true;
				}
			}
			//#debug
			System.out.println("Node: " + this + " handled pointerReleased=" + handled);
			return handled || super.handlePointerReleased(x, y);
		}
		//#endif
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
		 */
		protected Style focus(Style focusstyle, int direction ) {
			//#debug
			System.out.println("focus node " + this.root + ", expanded=" + this.isExpanded);
			this.isFocused = true;
			this.rootFocusedStyle = focusstyle;
			if ( !this.isExpanded || direction != Canvas.UP || this.children.size() == 0 || this.children.appearanceMode == PLAIN)
			{
				this.rootPlainStyle  = this.root.focus(focusstyle, direction);
				return this.rootPlainStyle;
			}
			// focus one of the expanded children:
			//System.out.println("node " + this + ": forwarding focus event to children, (direction != Canvas.UP)=" + (direction != Canvas.UP));
			this.children.focus(focusstyle, direction); 
			return this.root.style;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
		 */
		protected void defocus(Style originalStyle) {
			this.isFocused = false;
			//System.out.println("defocus " + this );
			//if (this.isExpanded && this.isChildrenFocused) {
			if (this.isExpanded && this.children.isFocused) {
				this.children.defocus(originalStyle);
				//this.isChildrenFocused = false;
			} else {
				this.root.defocus( originalStyle );
			}
		}
		
		private void focusRoot() {
			//#debug
			System.out.println("focusing root " + this);
			this.internalX = -this.contentX;
			this.internalY = -this.contentY;
			this.internalWidth = this.root.itemWidth + this.contentX;
			this.internalHeight = this.root.itemHeight + this.contentY;
			if (this.children.isFocused) {
				this.children.defocus( null );
				this.children.focus( -1 );
				//this.isChildrenFocused = false;
				// move focus to root:
				if (this.rootFocusedStyle != null) {
					this.root.focus(this.rootFocusedStyle, Canvas.UP);
				} else {
					this.root.focus(this.focusedStyle, Canvas.UP);
				}
			}
		}
				
		private void setExpanded( boolean expand ) {
			if (!expand) {
				this.internalX = NO_POSITION_SET;
				// close down all chidren nodes as well when closing:
				Item[] myItems = this.children.getItems();
				for (int i = 0; i < myItems.length; i++) {
					Item item = myItems[i];
					if (item instanceof Node) {
						((Node)item).setExpanded(false);
					}
				}
				//if (this.isChildrenFocused) {
				focusRoot();
//			} else if (!this.isExpanded) {
//				// trick so that the parent container can scroll correctly when this node is expanded:
//				this.internalX = 0;
//				this.internalY = 0;
//				this.internalHeight = this.root.itemHeight + this.paddingVertical + (this.children.itemHeight != 0 ? this.children.itemHeight : 30);
			}
			if (expand != this.isExpanded) {
				this.isExpanded = expand;
				requestInit();
			}
		}
		

		//#if polish.debugEnabled
		public String toString() {
			return "Node " + this.root + "/" + super.toString();
		}
		//#endif

		
	}

}
