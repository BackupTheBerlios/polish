/*
 * Created on 01-Jun-2005 at 13:49:09.
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
package de.enough.roadrunner;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AccessibleCanvas;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        01-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TestCanvas extends Canvas
implements AccessibleCanvas
{
	
	private int counter;
	private int parentWidth;
	private int parentHeight;
	private final CommandListener listener;
	private final Command returnCommand;

	/**
	 * @param parent
	 * @param listener
	 * @param returnCommand
	 * 
	 */
	public TestCanvas( Displayable parent, CommandListener listener, Command returnCommand ) {
		super();
		this.listener = listener;
		this.returnCommand = returnCommand;
		if (parent instanceof Canvas) {
			Canvas canvas = (Canvas) parent;
			this.parentHeight = canvas.getHeight();
			this.parentWidth = canvas.getWidth();
		}
		//#if polish.midp2
			setFullScreenMode(true);
		//#endif
	}
	
	

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#showNotify()
	 */
	public void showNotify() {
		// ignore
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#hideNotify()
	 */
	public void hideNotify() {
		// ignore
	}
	
	public void keyPressed( int keyCode ) {
		this.listener.commandAction(this.returnCommand, this);
	}
	
	public void keyRepeated( int keyCode ) {
		// ignore
	}

	public void keyReleased( int keyCode ) {
		// ignore
	}

	public void pointerPressed( int x, int y ) {
		// ignore
	}
	
	public void sizeChanged( int width, int height ) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		// simulate activity, so the last internal buffer is visible on Nokia Series 60 phones:
		try {
			Thread.sleep( 100 );
		} catch (Exception e) {
			// ignore
		}
		
		if (this.counter == 0) {
			g.setColor( 0 );
			g.fillRect( 0, 0, getWidth(), getHeight() );
			g.setColor( 0xFFFF00 );
			g.drawString("width=" + this.parentWidth, 20, 20, Graphics.TOP | Graphics.LEFT );
			g.drawString("height=" + this.parentHeight, 20, 50, Graphics.TOP | Graphics.LEFT );
			this.counter++;
		} else if (this.counter == 1) {
			g.setColor( 0x00FF00 );
			g.fillRect( 0, 0, getWidth(), getHeight() );
			g.setColor( 0x0 );
			g.drawString("width=" + this.parentWidth, 20, 20, Graphics.TOP | Graphics.LEFT );
			g.drawString("height=" + this.parentHeight, 20, 50, Graphics.TOP | Graphics.LEFT );
			this.counter++;
		} else {
			g.setColor( 0xFF0000 );
			g.fillRect( 0, 0, getWidth(), getHeight() );
			g.setColor( 0x0 );
			g.drawString("width=" + this.parentWidth, 20, 20, Graphics.TOP | Graphics.LEFT );
			g.drawString("height=" + this.parentHeight, 20, 50, Graphics.TOP | Graphics.LEFT );
			g.drawString("Testing 2 Red", 20, 80, Graphics.TOP | Graphics.LEFT );
			this.counter = 0;
		}
		
	}

}
