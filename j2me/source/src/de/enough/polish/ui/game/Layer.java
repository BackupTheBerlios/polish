// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sat Dec 06 15:06:43 CET 2003
// only include this file for midp1-devices:
//#condition (polish.midp1 && ! polish.api.siemens-color-game-api) || (polish.usePolishGameApi == true)
/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * A Layer is an abstract class representing a visual element of a game.
 * 
 * A Layer is an abstract class representing a visual element of a game.
 * Each Layer has position (in terms of the upper-left corner of its visual
 * bounds), width, height, and can be made visible or invisible.
 * Layer subclasses must implement a <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#paint(javax.microedition.lcdui.Graphics)"><CODE>paint(Graphics)</CODE></A> method so that
 * they can be rendered.
 * <p>
 * The Layer's (x,y) position is always interpreted relative to the coordinate
 * system of the Graphics object that is passed to the Layer's paint() method.
 * This coordinate system is referred to as the <em>painter's</em> coordinate
 * system.  The initial location of a Layer is (0,0).
 * 
 * <p>
 * 
 * @since MIDP 2.0
 */
public abstract class Layer extends Object
{
	protected int xPosition;
	protected int yPosition;
	protected int width;
	protected int height;
	protected boolean isVisible = true;

	/**
	 * Sets this Layer's position such that its upper-left corner
	 * is located at (x,y) in the painter's coordinate system.
	 * A Layer is located at (0,0) by default.
	 * <br>
	 * 
	 * @param x the horizontal position
	 * @param y the vertical position
	 * @see #move(int, int)
	 * @see #getX()
	 * @see #getY()
	 */
	public void setPosition(int x, int y)
	{
		this.xPosition = x;
		this.yPosition = y;
	}

	/**
	 * Moves this Layer by the specified horizontal and vertical distances.
	 * <br>
	 * The Layer's coordinates are subject to wrapping if the passed
	 * parameters will cause them to exceed beyond Integer.MAX_VALUE
	 * or Integer.MIN_VALUE.
	 * 
	 * @param dx the distance to move along horizontal axis (positive to the right, negative to the left)
	 * @param dy the distance to move along vertical axis (positive down, negative up)
	 * @see #setPosition(int, int)
	 * @see #getX()
	 * @see #getY()
	 */
	public void move(int dx, int dy)
	{
		this.xPosition += dx;
		this.yPosition += dy;
	}

	/**
	 * Gets the horizontal position of this Layer's upper-left corner
	 * in the painter's coordinate system.
	 * <p>
	 * 
	 * @return the Layer's horizontal position.
	 * @see #getY()
	 * @see #setPosition(int, int)
	 * @see #move(int, int)
	 */
	public final int getX()
	{
		return this.xPosition;
	}

	/**
	 * Gets the vertical position of this Layer's upper-left corner
	 * in the painter's coordinate system.
	 * <p>
	 * 
	 * @return the Layer's vertical position.
	 * @see #getX()
	 * @see #setPosition(int, int)
	 * @see #move(int, int)
	 */
	public final int getY()
	{
		return this.yPosition;
	}

	/**
	 * Gets the current width of this layer, in pixels.
	 * 
	 * @return the width in pixels
	 * @see #getHeight()
	 */
	public final int getWidth()
	{
		return this.width;
	}

	/**
	 * Gets the current height of this layer, in pixels.
	 * 
	 * @return the height in pixels
	 * @see #getWidth()
	 */
	public final int getHeight()
	{
		return this.height;
	}

	/**
	 * Sets the visibility of this Layer.  A visible Layer is rendered when
	 * its <A HREF="../../../../javax/microedition/lcdui/game/Layer.html#paint(javax.microedition.lcdui.Graphics)"><CODE>paint(Graphics)</CODE></A> method is called; an invisible Layer is
	 * not rendered.
	 * 
	 * @param visible - true to make the Layer visible,  false to make it invisible
	 * @see #isVisible()
	 */
	public void setVisible(boolean visible)
	{
		this.isVisible = visible;
	}

	/**
	 * Gets the visibility of this Layer.
	 * 
	 * @return true if the Layer is visible, false if it is invisible.
	 * @see #setVisible(boolean)
	 */
	public final boolean isVisible()
	{
		return this.isVisible;
	}

	/**
	 * Paints this Layer if it is visible.  The upper-left corner of the Layer
	 * is rendered at it's current (x,y) position relative to the origin of
	 * the provided Graphics object.  Applications may make use of Graphics
	 * clipping and translation to control where the Layer is rendered and to
	 * limit the region that is rendered.
	 * <P>
	 * Implementations of this method are responsible for checking if this
	 * Layer is visible; this method does nothing if the Layer is not
	 * visible.
	 * <p>
	 * The attributes of the Graphics object (clip region, translation,
	 * drawing color, etc.) are not modified as a result of calling this
	 * method.
	 * 
	 * @param g - the graphics object for rendering the Layer
	 * @throws NullPointerException - if g is null
	 */
	public abstract void paint( Graphics g);

}
