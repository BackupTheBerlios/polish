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
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        14-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FramedForm extends Form {
	
	protected Container leftFrame;
	protected Container rightFrame;
	protected Container topFrame;
	protected Container bottomFrame;
	private int originalContentHeight;
	private int originalContentWidth;
	private boolean expandRightFrame;
	private boolean expandLeftFrame;
	
	protected Container currentlyActiveContainer;
	private int originalContentY;
	private int originalContentX;

	/**
	 * Creates a new FramedForm
	 * 
	 * @param title the title of this form
	 */
	public FramedForm(String title ) {
		this( title, null );
	}

	/**
	 * Creates a new FramedForm
	 * 
	 * @param title the title of this form
	 * @param style the style of this form, usually set with a #style directive
	 */
	public FramedForm(String title, Style style) {
		super( title, style );
		this.currentlyActiveContainer = this.container;
		//#if polish.Container.allowCycling != false
			this.container.allowCycling = false;
		//#endif

	}

	private Container getFrame( int frameOrientation ) {
		switch (frameOrientation) {
		case  Graphics.TOP:
			return this.topFrame;
		case  Graphics.BOTTOM:
			return this.bottomFrame;
		case  Graphics.LEFT:
			return this.leftFrame;
		case  Graphics.RIGHT:
			return this.rightFrame;
		}		
		return this.container;
	}
	
	/**
	 * Deletes all the items from all frames of this <code>FramedForm</code>, leaving  it with zero items.
	 * This method does nothing if the <code>FramedForm</code> is already empty.
	 * 
	 * @since  MIDP 2.0
	 */
	public void deleteAll()
    {
        super.deleteAll();
        if (this.leftFrame != null)
        {
            this.leftFrame.clear();
        }
        if (this.rightFrame != null)
        {
            this.rightFrame.clear();
        }
        if (this.topFrame != null)
        {
            this.topFrame.clear();
        }
        if (this.bottomFrame != null)
        {
            this.bottomFrame.clear();
        }
    }
	
	/**
	 * Removes all items from the specifid frame.
	 * 
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 */
	public void deleteAll( int frameOrientation ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.clear();
		}
	}


	//#if polish.LibraryBuild
	public int append( javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
		return -1;
	}
	//#endif

	//#if polish.LibraryBuild
	public void set( int frameIndex, int itemNumber, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if polish.LibraryBuild
	public void set( int itemNumber, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the append( Item item ) method is called
	}
	//#endif

	//#if polish.LibraryBuild
	public void append( int frameOrientation, javax.microedition.lcdui.Item item ) {
		// just a convenience method, in reality the addItem( Item item, int frameOrientation ) method is called
	}
	//#endif
	
	//#if polish.LibraryBuild
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener listener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a framed form.");
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
	
	
	/**
	 * Updates an existing item in the specified frame
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param itemNum the index of the previous item
	 * @param item the new item
	 */
	public void set( int frameOrientation, int itemNum, Item item ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.set(itemNum, item);
		}
	}
	
	/**
	 * Removes the given item from the specifid frame.
	 * The <code>itemNum</code> parameter must be
	 * within the range <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param itemNum the index of the item
	 */
	public void delete( int frameOrientation, int itemNum ) {
		Container frame = getFrame( frameOrientation );
		if (frame != null) {
			frame.remove(itemNum);
		}
	}

	/**
	 * Adds the given item to the specifid frame.
	 * 
	 * @param frameOrientation either Graphics.TOP, Graphics.BOTTOM, Graphics.LEFT or Graphics.RIGHT
	 * @param item the item
	 * @param itemStyle the style for that item, is ignored when null
	 */
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
	protected void calculateContentArea(int x, int y, int width, int height) {
		super.calculateContentArea(x, y, width, height);
		x = this.contentX;
		y = this.contentY;
		width = this.contentWidth;
		height = this.contentHeight;
		this.originalContentWidth = width;
		this.originalContentHeight = height;
		this.originalContentY = this.contentY;
		this.originalContentX = this.contentX;
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
		this.container.requestFullInit();
		this.container.init(width, width);
		this.contentX = x;
		this.contentY = y;
		this.contentWidth = width;
		this.contentHeight = height;
		this.container.setScrollHeight( height );
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
	 */
	protected void paintScreen(Graphics g) {
//		//#ifdef tmp.menuFullScreen
//		 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
//		//#else
//		 	g.setClip(0, 0, this.screenWidth, this.originalScreenHeight );
//		//#endif

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
			int y = this.originalContentY;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y += (this.originalContentHeight - frameStyle.marginBottom - this.leftFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y += this.originalContentHeight - frameStyle.marginBottom - this.leftFrame.itemHeight;
			}
			this.leftFrame.relativeX = 0;
			this.leftFrame.relativeY = y;
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
			int y = this.originalContentY;
			if ( (frameStyle.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				y += (this.originalContentHeight - frameStyle.marginBottom - this.rightFrame.itemHeight) / 2;
			} else if ( (frameStyle.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				y += this.originalContentHeight - frameStyle.marginBottom - this.rightFrame.itemHeight;
			}
			this.rightFrame.relativeX = this.contentWidth;
			this.rightFrame.relativeY = y;
			this.rightFrame.paint( this.contentWidth, y, this.contentWidth, this.screenWidth, g );
		}
		if (this.topFrame != null ) {
			this.topFrame.relativeX = this.originalContentX;
			this.topFrame.relativeY = this.originalContentY;
			this.topFrame.paint( this.originalContentX, this.originalContentY, this.originalContentX, this.originalContentX + this.originalContentWidth, g );
		}
		if (this.bottomFrame != null ) {
			this.bottomFrame.relativeX = this.originalContentX;
			this.bottomFrame.relativeY = this.contentY + this.contentHeight;
			this.bottomFrame.paint( this.originalContentX, this.contentY + this.contentHeight, this.originalContentX, this.originalContentX + this.originalContentWidth, g );
		}
		super.paintScreen(g);

	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		boolean handled = this.currentlyActiveContainer.handleKeyPressed(keyCode, gameAction);
		if (! handled ) {
			Container newFrame = null;
			if (this.currentlyActiveContainer == this.container ) {
				Container[] nextFrames;
				switch (gameAction) {
					case DOWN:
						nextFrames = new Container[]{ this.bottomFrame, this.leftFrame, this.rightFrame, this.topFrame };
						break;
					case UP:
						nextFrames = new Container[]{ this.topFrame, this.leftFrame, this.rightFrame, this.bottomFrame };
						break;
					case LEFT:
						nextFrames = new Container[]{ this.leftFrame, this.topFrame, this.bottomFrame, this.rightFrame };
						break;
					case RIGHT:
						nextFrames = new Container[]{ this.rightFrame, this.topFrame, this.bottomFrame, this.leftFrame };
					default:
						return false;
				}
				for (int i = 0; i < nextFrames.length; i++) {
					Container frame = nextFrames[i];
					if (frame != null && frame.appearanceMode != Item.PLAIN) {
						newFrame = frame;
						break;
					}
				}
			} else {
				//System.out.println("Changing back to default container");
				newFrame = this.container;
			}
			if ( newFrame != null && newFrame != this.currentlyActiveContainer ) {
				setActiveFrame(newFrame);
				handled = true;
			}
		//} else {
		//	System.out.println("currently active container has handled the key press event");
		}
		return handled;
	}
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when none is focused.
	 */
	public Item getCurrentItem() {
		if (this.currentlyActiveContainer != null) {
			return this.currentlyActiveContainer.focusedItem;
		}
		return null;
	}
	
	/**
	 * Activates another frame.
	 * 
	 * @param newFrame the next frame
	 */
	private void  setActiveFrame(Container newFrame) {
		this.currentlyActiveContainer.defocus( this.currentlyActiveContainer.style );
		newFrame.focus( StyleSheet.focusedStyle, 0 );
		this.currentlyActiveContainer = newFrame;
		if (this.screenStateListener != null) {
			this.screenStateListener.screenStateChanged( this );
		}
	}

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		Container newFrame = null;
		if (this.container.handlePointerPressed(x - this.container.relativeX, y - this.container.relativeY)) {
			newFrame = this.container;
		} else if ( this.topFrame != null && this.topFrame.handlePointerPressed(x - this.topFrame.relativeX, y - this.topFrame.relativeY) ) {
			newFrame = this.topFrame;
		} else if ( this.bottomFrame != null && this.bottomFrame.handlePointerPressed(x - this.bottomFrame.relativeX, y - this.bottomFrame.relativeY) ) {
			newFrame = this.bottomFrame;
		} else if ( this.leftFrame != null && this.leftFrame.handlePointerPressed(x - this.leftFrame.relativeX, y - this.leftFrame.relativeY) ) {
			newFrame = this.leftFrame;
		} else if ( this.rightFrame != null && this.rightFrame.handlePointerPressed(x - this.rightFrame.relativeX, y - this.rightFrame.relativeY) ) {
			newFrame = this.rightFrame;
		}
		if (newFrame != null && newFrame != this.currentlyActiveContainer ) {
			setActiveFrame(newFrame);
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
