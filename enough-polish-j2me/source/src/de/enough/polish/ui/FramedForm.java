//#condition polish.usePolishGui
/*
 * Created on 12-Apr-2005 at 13:31:53.
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

import javax.microedition.lcdui.Graphics;

/**
 * <p>Allows to split up a form into several frames.</p>
 * <p>The main frame is used for the normal content. Additional frames
 *    can be used for keeping GUI elements always in the same position,
 *    regardless whether the form is scrolled.
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FramedForm extends Form {
	
	private Container leftFrame;
	private Container rightFrame;
	private Container topFrame;
	private Container bottomFrame;
	private int originalContentHeight;
	private int originalContentWidth;
	private boolean expandRightFrame;
	private boolean expandLeftFrame;
	
	private Container currentlyActiveContainer;

	/**
	 * @param title
	 */
	public FramedForm(String title ) {
		this( title, null );
	}

	/**
	 * @param title
	 * @param style
	 */
	public FramedForm(String title, Style style) {
		super( title, style );
		this.currentlyActiveContainer = this.container;
		//#if polish.Container.allowCycling != false
			this.container.allowCycling = false;
		//#endif

	}

	//#if false
	public void append( javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if false
	public void set( int itemNumber, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if false
	public void append( int frameOrientation, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the addItem( Item item, int frameOrientation ) method is called
	}
	//#endif
	
	//#if false
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener listener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a tabbed form.");
	}
	//#endif


	/**
	 * Adds the given item to the specifid frame.
	 * 
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param item the item
	 */
	public void append( int frameOrientation, Item item ) {
		append( frameOrientation, item, null );
	}

	public void append( int frameOrientation, Item item, Style itemStyle ) {
		if (itemStyle != null) {
			item.setStyle( itemStyle );
		}
		Container frame;
		switch (frameOrientation) {
			case  Graphics.TOP:
				if (this.topFrame == null) {
					//#style topframe, frame, default
					this.topFrame = new Container( false );
				}
				frame = this.topFrame;
				break;
			case  Graphics.BOTTOM:
				if (this.bottomFrame == null) {
					//#style bottomframe, frame, default
					this.bottomFrame = new Container( false );
				}
				frame = this.bottomFrame;
				break;
			case  Graphics.LEFT:
				if (this.leftFrame == null) {
					//#style leftframe, frame, default
					this.leftFrame = new Container( false );
				}
				frame = this.leftFrame;
				break;
			default:
				if (this.rightFrame == null) {
					//#style rightframe, frame, default
					this.rightFrame = new Container( false );
				}
				frame = this.rightFrame;
		}
		frame.screen = this;
		//#if polish.Container.allowCycling != false
			frame.allowCycling = false;
		//#endif
		frame.add( item );
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setContentArea(int, int, int, int)
	 */
	protected void setContentArea(int x, int y, int width, int height) {
		this.originalContentWidth = width;
		this.originalContentHeight = height;
		if (this.leftFrame != null) {
			this.expandLeftFrame = (this.leftFrame.style.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND;
			if (this.expandLeftFrame) {
				this.leftFrame.setStyle( this.leftFrame.style, true );
			}
			int frameWidth = this.leftFrame.getItemWidth(width/2, width/2);
			x += frameWidth;
			width -= frameWidth;
		}
		if (this.rightFrame != null) {
			this.expandRightFrame = (this.rightFrame.style.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND; 
			if (this.expandRightFrame) {
				this.rightFrame.setStyle( this.rightFrame.style, true );
			}
			width -= this.rightFrame.getItemWidth(this.originalContentWidth/2, this.originalContentWidth/2);
		}
		if (this.topFrame != null ) {
			int frameHeight = this.topFrame.getItemHeight(this.originalContentWidth, this.originalContentWidth);
			y += frameHeight;
			height -= frameHeight;
		}
		if (this.bottomFrame != null ) {
			height -= this.bottomFrame.getItemHeight(this.originalContentWidth, this.originalContentWidth);
		}
		super.setContentArea(x, y, width, height);
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
		super.paintScreen(g);
		if (this.leftFrame != null) {
		 	Style frameStyle = this.leftFrame.style;
			if (this.expandLeftFrame) {
			 	if ( frameStyle.background != null ) {
			 		frameStyle.background.paint( frameStyle.marginLeft, frameStyle.marginTop, this.leftFrame.backgroundWidth, this.originalContentHeight - frameStyle.marginTop - frameStyle.marginBottom, g);
			 	}
			 	if ( frameStyle.border != null ) {
			 		frameStyle.border.paint( frameStyle.marginLeft, frameStyle.marginTop, this.leftFrame.backgroundWidth, this.originalContentHeight - frameStyle.marginTop - frameStyle.marginBottom, g);
			 	}
			}
			int y = 0;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y = (this.originalContentHeight - frameStyle.marginBottom - this.leftFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y = this.originalContentHeight - frameStyle.marginBottom - this.leftFrame.itemHeight;
			}
			this.leftFrame.paint( 0, y, 0, this.contentWidth, g );
		}
		if (this.rightFrame != null) {
		 	Style frameStyle = this.rightFrame.style;
			if (this.expandRightFrame) {
			 	if ( frameStyle.background != null ) {
			 		frameStyle.background.paint( this.contentWidth + frameStyle.marginLeft, frameStyle.marginTop, this.rightFrame.backgroundWidth, this.originalContentHeight - frameStyle.marginTop - frameStyle.marginBottom, g);
			 	}
			 	if ( frameStyle.border != null ) {
			 		frameStyle.border.paint( this.contentWidth + frameStyle.marginLeft, frameStyle.marginTop, this.rightFrame.backgroundWidth, this.originalContentHeight - frameStyle.marginTop - frameStyle.marginBottom, g);
			 	}
			}
			int y = 0;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y = (this.originalContentHeight - frameStyle.marginBottom - this.rightFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y = this.originalContentHeight - frameStyle.marginBottom - this.rightFrame.itemHeight;
			}
			this.rightFrame.paint( this.contentWidth, y, this.contentWidth, this.screenWidth, g );
		}
		if (this.topFrame != null ) {
			this.topFrame.paint( 0, 0, 0, this.screenWidth, g );
		}
		if (this.bottomFrame != null ) {
			this.bottomFrame.paint( 0, this.contentHeight, 0, this.screenWidth, g );
		}
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = this.currentlyActiveContainer.handleKeyPressed(keyCode, gameAction);
		if (! handled ) {
			Container newFrame = null;
			if (this.currentlyActiveContainer == this.container ) {
				if ( gameAction == DOWN && this.bottomFrame != null && this.bottomFrame.appearanceMode != Item.PLAIN ) {
					newFrame = this.bottomFrame;
					System.out.println("activating bottom frame");
				} else if ( gameAction == LEFT && this.leftFrame != null && this.leftFrame.appearanceMode != Item.PLAIN ) {
					newFrame = this.leftFrame;
				} else if ( gameAction == RIGHT && this.rightFrame != null && this.rightFrame.appearanceMode != Item.PLAIN ) {
					newFrame = this.rightFrame;
					System.out.println("activating right frame");
				} else if ( gameAction == UP && this.topFrame != null && this.topFrame.appearanceMode != Item.PLAIN ) {
					newFrame = this.topFrame;
				}
			} else {
				System.out.println("Changing back to default container");
				newFrame = this.container;
			}
			if (newFrame != null) {
				this.currentlyActiveContainer.defocus( this.currentlyActiveContainer.style );
				newFrame.focus( StyleSheet.focusedStyle );
				this.currentlyActiveContainer = newFrame;
				handled = true;
			}
		} else {
			System.out.println("currently active container has handled the key press event");
		}
		return handled;
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		Container newFrame = null;
		if (this.container.handlePointerPressed(x, y)) {
			newFrame = this.container;
		} else if ( this.topFrame != null && this.topFrame.handlePointerPressed(x, y) ) {
			newFrame = this.topFrame;
		} else if ( this.bottomFrame != null && this.bottomFrame.handlePointerPressed(x, y) ) {
			newFrame = this.bottomFrame;
		} else if ( this.leftFrame != null && this.leftFrame.handlePointerPressed(x, y) ) {
			newFrame = this.leftFrame;
		} else if ( this.rightFrame != null && this.rightFrame.handlePointerPressed(x, y) ) {
			newFrame = this.rightFrame;
		}
		if (newFrame != null && newFrame != this.currentlyActiveContainer ) {
			this.currentlyActiveContainer.defocus( this.currentlyActiveContainer.style );
			newFrame.focus( StyleSheet.focusedStyle );
			this.currentlyActiveContainer = newFrame;
		}
		return (newFrame != null);
	}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate()
			| ( this.leftFrame != null && this.leftFrame.animate() )
			| ( this.rightFrame != null && this.rightFrame.animate() )
			| ( this.topFrame != null && this.topFrame.animate() )
			| ( this.bottomFrame != null && this.bottomFrame.animate() );
		return animated;
	}
}
