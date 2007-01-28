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
 * <p>Copyright (c) 2005, 2006, 2007 Enough Software</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TreeItem 
//#ifdef polish.usePolishGui
	//# extends Container
//#else
	extends FakeContainerCustomItem
//#endif
 
{
	
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
	 * @param style the style
	 * @return return the created item
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image, Style style ) {
	//#else
		//# public Item appendToRoot( String text, Image image, Style style ) {
	//#endif
		IconItem item = new IconItem( text, image, style );
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
	 * @param style the style
	 * @return the created item
	 */
	public Item appendToNode( Item node, String text, Image image, Style style ) {
		IconItem item = new IconItem( text, image);
		appendToNode( node, item, style );
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
			Item[] items = parentContainer.getItems();
			for (int i = 0; i < items.length; i++) {
				Item rootItem = items[i];
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

	
	
	
	static class Node extends Item {
		private Item root;
		private Container children;
		private boolean isExpanded;
		int xLeftOffset = 10;
		private Style rootFocusedStyle;
		private Style rootPlainStyle;
		private Style childrenPlainStyle;
		//private boolean isChildrenFocused;
		private int availableWidth;
		//#if polish.css.treeitem-closed-indicator && polish.css.treeitem-opened-indicator
			//#define tmp.useIndicators
			private Image closedIndicator;
			private Image openedIndicator;
			private int indicatorWidth;
		//#endif
		
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
			this.availableWidth = lineWidth - this.xLeftOffset;
			this.root.init(firstLineWidth, lineWidth);
			this.children.relativeX = this.xLeftOffset;
			this.children.relativeY = this.root.itemHeight;
			
			int rootWidth = this.root.itemWidth;
			//#if tmp.useIndicators
				int w = 0;
				if (this.openedIndicator != null) {
					w = this.openedIndicator.getWidth();
				}
				if (this.closedIndicator != null && this.closedIndicator.getWidth() > w) {
					w = this.closedIndicator.getWidth();
				}
				if (w != 0) {
					rootWidth += w + this.paddingHorizontal;
				}
				this.indicatorWidth = w;
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
					image = this.openedIndicator;
				} else {
					image = this.closedIndicator;
				}
				if (image != null) {
					int height = image.getHeight();
					int rootHeight = this.root.itemHeight;
					g.drawImage(image, x, y + (rootHeight-height)/2, Graphics.TOP | Graphics.RIGHT );
				}
				x += this.indicatorWidth;
			//#endif
			this.root.paint(x, y, leftBorder, rightBorder, g);
			if (this.isExpanded) {
				leftBorder += this.xLeftOffset;
				x = leftBorder;
				rightBorder -= this.xLeftOffset;
				y += this.root.itemHeight;
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
			System.out.println("Note " + this + " handleKeyPressed: isExpanded=" + this.isExpanded);
			boolean handled = false;
			if (this.isExpanded) {
				//if (this.isChildrenFocused) {
				if (this.children.isFocused) {
					handled = this.children.handleKeyPressed(keyCode, gameAction);
					if (!handled && gameAction == Canvas.UP) {
						// focus this root:
						focusRoot();
						handled = true;
					} else {
						if (this.children.internalX != -999) {
							this.internalX = this.children.relativeX + this.children.contentX + this.children.internalX;
							this.internalY = this.children.relativeY + this.children.contentY + this.children.internalY;
							this.internalWidth = this.children.internalWidth;
							this.internalHeight = this.children.internalHeight;
						} else {
							this.internalX = -9999;
						}
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
				if (handled) {
					if (!this.children.isFocused) {
						this.children.focus(this.style, 0);
						this.children.isFocused = true;
					}
					//System.out.println("PP: CHILD HANDLED PP for " + this );
					//this.isChildrenFocused = true;
					if (this.rootPlainStyle != null) {
						this.root.setStyle( this.rootPlainStyle );
					}
				} else if ( this.root.isInItemArea(x, y)) {
					//if (this.isChildrenFocused) {
					if (this.children.isFocused) {
						focusRoot();
					}
					setExpanded( false );
					handled = true;
				}
			}
			return handled || super.handlePointerPressed(x, y);
		}
		//#endif
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
		 */
		protected Style focus(Style focusstyle, int direction ) {
			//
			this.isFocused = true;
			this.rootFocusedStyle = focusstyle;
			if ( !this.isExpanded || direction != Canvas.UP || this.children.size() == 0 || this.children.appearanceMode == PLAIN)
			{
				this.rootPlainStyle  = this.root.focus(focusstyle, direction);
				return this.rootPlainStyle;
			}
			//this.isChildrenFocused = true;
			this.childrenPlainStyle = this.children.focus(focusstyle, direction); 
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
			this.internalX = 0;
			this.internalY = 0;
			this.internalWidth = this.root.itemWidth;
			this.internalHeight = this.root.itemHeight;
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
				
		private void setExpanded( boolean expand ) {
			if (!expand) {
				this.internalX = -9999;
				// close down all chidren nodes as well when closing:
				Item[] items = this.children.getItems();
				for (int i = 0; i < items.length; i++) {
					Item item = items[i];
					if (item instanceof Node) {
						((Node)item).setExpanded(false);
					}
				}
				//if (this.isChildrenFocused) {
				if (this.children.isFocused) {
					focusRoot();
				}
			} else if (!this.isExpanded) {
				// trick so that the parent container can scroll correctly when this node is expanded:
				this.internalX = 0;
				this.internalY = 0;
				this.internalHeight = this.root.itemHeight + this.children.getItemHeight(this.availableWidth, this.availableWidth);
			}
			if (expand != this.isExpanded) {
				requestInit();
				this.isExpanded = expand;
			}
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
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

		//#if polish.debugEnabled
		public String toString() {
			return "Node " + this.root + "/" + super.toString();
		}
		//#endif

		
	}

}
