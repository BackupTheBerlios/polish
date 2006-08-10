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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Provides a tree of items that can contain several branches.</p>
 * <p>Each tree branch behaves like a normal J2ME Polish container, so 
 *    you can specify view-types, columns, colspans, etc.
 * </p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TreeItem 
//#ifdef polish.usePolishGui
	//# extends CustomItem
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
 
{
	
	private final Container root;
	private int availableWidth;
	
	private Item lastAddedItem;

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
		super(label);
		this.root = new Container( false, style );
		//#if polish.Container.allowCycling != false
			this.root.allowCycling = false;
		//#endif

	}
	
	//#if false
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param item the item that should be added
	 */
	public void appendToRoot( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif
	
	//#if false
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

	//#if false
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


	//#if false
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
	//#if false
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
	//#if false
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image, Style style ) {
	//#else
		//# public Item appendToRoot( String text, Image image, Style style ) {
	//#endif
		IconItem item = new IconItem( text, image, style );
		appendToRoot( item );
		//#if false
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
		this.root.add(item);
		//#if true
			//# item.parent = this;
		//#endif
		this.lastAddedItem = item;
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 * @param style the style
	 */
	public void appendToRoot( Item item, Style style ) {
		if (style != null) {
			item.setStyle( style );
		}
		//#if true
			//# item.parent = this;
		//#endif
		this.root.add(item);
		this.lastAddedItem = item;
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
	 * @param style the style
	 */
	public void appendToNode( Item node, Item item, Style style  ) {
		if (style != null) {
			item.setStyle( style );
		}
		// find correct Node:
		Node parentNode;
		if ( !(node.parent instanceof Node) ) {
			// the item has to be converted into a node:
			Container parentContainer;
			//#if false
				if ( (Object)node.parent == this) {
			//#else
				//# if (node.parent == this) {
			//#endif
				// this is a root item:
				parentContainer = this.root;
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
		this.lastAddedItem = item;
		
	}

	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( Item item ) {
		return this.root.remove(item);
	}
	
	/**
	 * Clears this list.
	 */
	public void removeAll() {
		this.root.clear();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentWidth()
	 */
	protected int getMinContentWidth() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getMinContentHeight()
	 */
	protected int getMinContentHeight() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentWidth(int)
	 */
	protected int getPrefContentWidth(int maxHeight) {
		// try to use the maximum available width:
		return Integer.MAX_VALUE;
		//return this.prefContentWidth;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#getPrefContentHeight(int)
	 */
	protected int getPrefContentHeight(int maxWidth) {
		this.availableWidth = maxWidth;
		return this.root.getItemHeight(maxWidth, maxWidth);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	 */
	protected void paint(Graphics g, int w, int h) {
		this.root.paint( 0, 0, 0, w, g );
	}

	protected void hideNotify() {
		this.root.hideNotify();
	}
	
	protected void showNotify() {
		this.root.showNotify();
	}


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = this.root.handleKeyPressed(keyCode, gameAction);
		if (handled) {
			//System.out.println("invalidating through keyPressed...");
			invalidate();
		}
		return handled;
	}

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y) {
		if (this.root.handlePointerPressed(x, y)) {
			invalidate();
		}
	}
	//#endif


	protected boolean traverse(int direction, int viewWidth, int viewHeight, int[] viewRect_inout) {
		boolean handled = (this.root.handleKeyPressed(0, direction));
		if (handled) {
			viewRect_inout[0] = this.root.internalX;
			viewRect_inout[1] = this.root.internalY;
			viewRect_inout[2] = this.root.internalWidth;
			viewRect_inout[3] = this.root.internalHeight;
		}
		return handled;
	}
	
	protected void traverseOut() {
		this.root.defocus(null);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusstyle, int direction ) {
		invalidate();
		return this.root.focus(focusstyle, direction);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		invalidate();
		this.root.defocus(originalStyle);
	}
	
	
	
	static class Node extends Item {
		private Item root;
		private Container children;
		private boolean isExpanded;
		int xLeftOffset = 10;
		private Style rootFocusedStyle;
		private Style rootPlainStyle;
		private Style childrenPlainStyle;
		private boolean isChildrenFocused;
		
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
			this.root.init(firstLineWidth, lineWidth);
			if (!this.isExpanded) {
				this.contentWidth = this.root.itemWidth;
				this.contentHeight = this.root.itemHeight;
			} else {
				//TODO re-implement drawing of lines when no ContainerView is used 
				lineWidth -= this.xLeftOffset;
				this.children.init(lineWidth, lineWidth);
				this.contentWidth = Math.max( this.root.itemWidth, this.children.itemWidth + this.xLeftOffset);
				this.contentHeight = this.root.itemHeight + this.paddingVertical + this.children.itemHeight;
			}
		}

		protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
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
			boolean handled = false;
			if (this.isExpanded) {
				if (this.isChildrenFocused) {
					handled = this.children.handleKeyPressed(keyCode, gameAction);
					if (!handled && gameAction == Canvas.UP) {
						// focus this root:
						setExpanded( false );
						handled = true;
					} else if (gameAction == Canvas.FIRE) {
						// leaf selected!
//					} else {
//						this.isChildrenFocused = false;
						// this is done by the unfocus method automagically
					}
				} else if (gameAction == Canvas.DOWN && this.children.appearanceMode != PLAIN) {
					// move focus to children
					if (this.rootPlainStyle != null) {
						this.root.defocus(this.rootPlainStyle);
					}
					this.children.focus(null, gameAction);
					this.isChildrenFocused = true;
					handled = true;
				}

			}
			if (!handled && gameAction == Canvas.FIRE ) {
				setExpanded( !this.isExpanded );
				handled = true;
			}
			return handled;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
		 */
		protected Style focus(Style focusstyle, int direction ) {
			//System.out.println("focus " + this );
			//if ( !this.isExpanded || this.children.size() == 0) {
				//System.out.println("focus root " + this.root );
				this.rootFocusedStyle = focusstyle;
				this.rootPlainStyle  = this.root.focus(focusstyle, direction);
				return this.rootPlainStyle;
//			}
//			this.childrenPlainStyle = this.children.focus(focusstyle, direction); 
//			return this.childrenPlainStyle;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
		 */
		protected void defocus(Style originalStyle) {
			//System.out.println("defocus " + this );
			if (this.isExpanded && this.isChildrenFocused) {
				this.children.defocus(originalStyle);
				this.isChildrenFocused = false;
			} else {
				this.root.defocus( originalStyle );
			}
		}
		
		private void setExpanded( boolean expand ) {
			if (!expand) {
				//System.out.println( "defocussing children while contracting" );
				if (this.isChildrenFocused) {
					this.children.defocus( null );
					this.children.focus( -1 );
					this.isChildrenFocused = false;
					// move focus to root:
					if (this.rootFocusedStyle != null) {
						this.root.focus(this.rootFocusedStyle, Canvas.UP);
					} else {
						this.root.focus(this.focusedStyle, Canvas.UP);
					}
				}
			}
			if (expand != this.isExpanded) {
				requestInit();
				this.isExpanded = expand;
			}
		}
		
		//#if polish.debugEnabled
		public String toString() {
			return "Node " + this.root + "/" + super.toString();
		}
		//#endif

		
	}

}
