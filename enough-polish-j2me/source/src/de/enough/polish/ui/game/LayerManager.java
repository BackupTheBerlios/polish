// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:43 CET 2003
//only include this file for midp1-devices:
//#condition (polish.midp1 && ! polish.api.siemens-color-game-api) || (polish.usePolishGameApi == true)
/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui.game;

import javax.microedition.lcdui.Graphics;


/**
 * The LayerManager manages a series of Layers.
 * 
 * The LayerManager manages a series of Layers.  The LayerManager simplifies
 * the process of rendering the Layers that have been added to it by
 * automatically rendering the correct regions of each Layer in the
 * appropriate order.
 * <p>
 * The LayerManager maintains an ordered list to which Layers can be appended,
 * inserted and removed.  A Layer's index correlates to its z-order; the layer
 * at index 0 is closest to the user while a the Layer with the highest index
 * is furthest away from the user.  The indices are always contiguous; that
 * is, if a Layer is removed, the indices of subsequent Layers will be
 * adjusted to maintain continuity.
 * <p>
 * The LayerManager class provides several features that control how the
 * game's Layers are rendered on the screen.
 * <p>
 * The <em>view window</em> controls the size of the visible region and its
 * position relative to the LayerManager's coordinate system.  Changing the
 * position of the view window enables effects such as scrolling or panning
 * the user's view.  For example, to scroll to the right, simply move the view
 * window's location to the right.  The size of the view window controls how
 * large the user's view will be, and is usually fixed at a size that is
 * appropriate for the device's screen.
 * In this example, the view window is set to 85 x 85 pixels and is located at
 * (52, 11) in the LayerManager's coordinate system.  The Layers appear at
 * their respective positions relative to the LayerManager's origin.
 * <br>
 * <center><img src="doc-files/viewWindow.gif" width=558 height=292
 * ALT="Specifying the View Window"></center>
 * <br>
 * <p>
 * The <A HREF="../../../../javax/microedition/lcdui/game/LayerManager.html#paint(javax.microedition.lcdui.Graphics, int, int)"><CODE>paint(Graphics, int, int)</CODE></A> method includes an (x,y) location
 * that controls where the view window is rendered relative to the screen.
 * Changing these parameters does not change the contents of the view window,
 * it simply changes the location where the view window is drawn.  Note that
 * this location is relative to the origin of the Graphics object, and thus
 * it is subject to the translation attributes of the Graphics object.
 * For example, if a game uses the top of the screen to display the current
 * score, the view window may be rendered at (17, 17) to provide enough space
 * for the score.
 * <br>
 * <center><img src="doc-files/drawWindow.gif" width=321 height=324
 * ALT="Drawing the View Window"></center>
 * <br>
 * 
 * @since MIDP 2.0
 */
public class LayerManager extends Object
{
	private Layer[] layers;
	private int size;
	private int viewX;
	private int viewY;
	private int viewWidth;
	private int viewHeight;
	private boolean isViewWindowSet;

	/**
	 * Creates a new LayerManager.</DL>
	 * 
	 * 
	 */
	public LayerManager()
	{
		this.layers = new Layer[ 5 ];
	}

	/**
	 * Appends a Layer to this LayerManager.  
	 * 
	 * The Layer is appended to the
	 * list of existing Layers such that it has the highest index (i.e. it
	 * is furthest away from the user).  The Layer is first removed
	 * from this LayerManager if it has already been added.
	 * 
	 * @param l the Layer to be added
	 * @throws NullPointerException if the Layer is  null
	 * @see #insert(Layer, int)
	 * @see #remove(Layer)
	 */
	public void append( Layer l)
	{
		if (this.size == this.layers.length) {
			Layer[] newLayers = new Layer[ this.size + 4 ];
			System.arraycopy( this.layers, 0, newLayers, 0,  this.size );
			this.layers = newLayers;
		}
		this.layers[ this.size ] = l;
		this.size++;
	}

	/**
	 * Inserts a new Layer in this LayerManager at the specified index.
	 * The Layer is first removed from this LayerManager if it has already
	 * been added.
	 * 
	 * @param l the Layer to be inserted
	 * @param index the index at which the new Layer is to be inserted
	 * @throws NullPointerException if the Layer is null
	 * @throws IndexOutOfBoundsException if the index is less than 0 or greater than the number of Layers already added to the this LayerManager
	 * @see #append(Layer)
	 * @see #remove(Layer)
	 */
	public void insert( Layer l, int index)
	{
		//#ifndef polish.skipArgumentCheck
			if (index > this.size) {
				//#ifdef polish.debugVerbose
					throw new IndexOutOfBoundsException("unable to insert layer at index [" + index + "] when the size is [" + this.size + "]." );
				//#else
					//# throw new IndexOutOfBoundsException();
				//#endif
			}
		//#endif
		remove( l );
		if (index > this.size) {
			index = this.size;
		}
		Layer[] source = this.layers;
		if (this.size == source.length || index == source.length) {
			// the internal buffer needs to be increased:
			Layer[] newLayers = new Layer[ this.size + 4 ];
			System.arraycopy( this.layers, 0, newLayers, 0,  this.size );
			source = newLayers;
		}
		
		for (int i = this.size; i>index; i--) {
			source[i] = source[ i-1 ];
		}
		source[index] = l;
		this.layers = source;
		this.size++;
	}

	/**
	 * Gets the Layer with the specified index.
	 * 
	 * @param index the index of the desired Layer, the first layer has the index 0
	 * @return the Layer that has the specified index
	 * @throws IndexOutOfBoundsException if the specified index is less than zero, 
	 * 			or if it is equal to or greater than the number of Layers added to the 
	 * 			this LayerManager
	 */
	public Layer getLayerAt(int index)
	{
		//#ifndef polish.skipArgumentCheck
			if (index >= this.size) {
				//#ifdef polish.debugVerbose
					throw new IndexOutOfBoundsException("unable to get layer at [" + index + "] of LayerManager with a size of [" + this.size + "] layers.");
				//#else
					//# throw new IndexOutOfBoundsException();
				//#endif
			}
		//#endif
		return this.layers[ index ];
	}

	/**
	 * Gets the number of Layers in this LayerManager.
	 * 
	 * @return the number of Layers
	 */
	public int getSize()
	{
		return this.size;
	}

	/**
	 * Removes the specified Layer from this LayerManager.  This method does
	 * nothing if the specified Layer is not added to the this LayerManager.
	 * 
	 * @param l the Layer to be removed
	 * @throws NullPointerException if the specified Layer is null
	 * @see #append(Layer)
	 * @see #insert(Layer, int)
	 */
	public void remove( Layer l)
	{
		Layer[] source = this.layers;
		boolean layerFound = false;
		for (int i = 0; i < this.size; i++) {
			Layer layer = source[i];
			if (layerFound) {
				source[i] = source[ i + 1 ];
			} else if (layer == l) {
				layerFound = true;
				this.size--;
				if (i != this.size) {
					source[i] = source[ i + 1 ];
				}
			}
		}
		if (layerFound) {
			source[ this.size ] = null;
		}
	}

	/**
	 * Renders the LayerManager's current view window at the specified location.
	 * <p>
	 * The LayerManager renders each of its layers in order of descending
	 * index, thereby implementing the correct z-order.  Layers that are
	 * completely outside of the view window are not rendered.
	 * </p>
	 * <p>
	 * The coordinates passed to this method determine where the
	 * LayerManager's view window will be rendered relative to the origin
	 * of the Graphics object.  For example, a game may use the top of the
	 * screen to display the current score, so to render the game's layers
	 * below that area, the view window might be rendered at (0, 20).  The
	 * location is relative to the Graphics object's origin, so translating
	 * the Graphics object will change where the view window is rendered on
	 * the screen.
	 * <p>
	 * The clip region of the Graphics object is intersected with a region
	 * having the same dimensions as the view window and located at (x,y).
	 * The LayerManager then translates the graphics object such that the
	 * point (x,y) corresponds to the location of the viewWindow in the
	 * coordinate system of the LayerManager.  The Layers are then rendered
	 * in the appropriate order.  The translation and clip region of the
	 * Graphics object are restored to their prior values before this method
	 * returns.
	 * </p>
	 * <p>
	 * Rendering is subject to the clip region and translation of the Graphics
	 * object.  Thus, only part of the specified view window may be rendered
	 * if the clip region is not large enough.
	 * </p>
	 * <p>
	 * For performance reasons, this method may ignore Layers that are
	 * invisible or that would be rendered entirely outside of the Graphics
	 * object's clip region.  The attributes of the Graphics object are not
	 * restored to a known state between calls to the Layers' paint methods.
	 * The clip region may extend beyond the bounds of a Layer; it is the
	 * responsibility of the Layer to ensure that rendering operations are
	 * performed within its bounds.
	 * </p>
	 * 
	 * @param g the graphics instance with which to draw the LayerManager
	 * @param x the horizontal location at which to render the view window,  relative to the Graphics' translated origin
	 * @param y the vertical location at which to render the view window, relative to the Graphics' translated origin
	 * @throws NullPointerException if g is null
	 * @see #setViewWindow(int, int, int, int)
	 */
	public void paint( Graphics g, int x, int y)
	{
		int minX, minY, maxX, maxY;
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		if (this.isViewWindowSet) {
			g.setClip( x, y, this.viewWidth, this.viewHeight );
			minX = this.viewX;
			maxX = minX + this.viewWidth;
			minY = this.viewY;
			maxY = minY + this.viewHeight;
		} else {
			minX = x;
			maxX = x + clipWidth;
			minY = y;
			maxY = y + clipHeight;
		}
		// translate the graphic origin:
		g.translate( x - this.viewX, y - this.viewY );
		for (int i = this.size-1; i >= 0; i--) {
			Layer layer = this.layers[ i ];
			if (layer.isVisible) {
				int layerX = layer.getX();
				int layerY = layer.getY();
				int layerWidth = layer.getWidth();
				int layerHeight = layer.getHeight();
				if (layerX + layerWidth < minX
						|| layerX > maxX
						|| layerY + layerHeight < minY
						|| layerY > maxY ) 
				{
					// the layer is not in the visible area:
					continue;
				}
				layer.paint( g );
			}
		}
		
		// re-translate the graphic origin:
		g.translate( -(x - this.viewX), -(y - this.viewY) );
		// reset original clip:
		if (this.isViewWindowSet) {
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		}
	}

	/**
	 * Sets the view window on the LayerManager.
	 * <p>
	 * The view window specifies the region that the LayerManager draws when
	 * its <A HREF="../../../../javax/microedition/lcdui/game/LayerManager.html#paint(javax.microedition.lcdui.Graphics, int, int)"><CODE>paint(javax.microedition.lcdui.Graphics, int, int)</CODE></A> method is called.  It allows the developer to
	 * control the size of the visible region, as well as the location of the
	 * view window relative to the LayerManager's coordinate system.
	 * <p>
	 * The view window stays in effect until it is modified by another call
	 * to this method.  By default, the view window is located at (0,0) in
	 * the LayerManager's coordinate system and its width and height are both
	 * set to Integer.MAX_VALUE.
	 * 
	 * @param x the horizontal location of the view window relative to the  LayerManager's origin
	 * @param y the vertical location of the view window relative to the LayerManager's origin
	 * @param width the width of the view window
	 * @param height the height of the view window
	 * @throws IllegalArgumentException if the width or height is less than 0
	 */
	public void setViewWindow(int x, int y, int width, int height)
	{
		this.isViewWindowSet = true;
		this.viewX = x;
		this.viewY = y;
		this.viewWidth = width;
		this.viewHeight = height;
	}

}
