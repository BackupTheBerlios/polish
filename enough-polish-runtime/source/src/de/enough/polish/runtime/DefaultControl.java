/*
 * Created on 15-Jun-2005 at 14:35:21.
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
package de.enough.polish.runtime;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.enough.polish.util.ArrayList;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        15-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultControl extends Control {
	private static BufferedImage scrollButton;
	private static BufferedImage leftSoftButton;
	private static BufferedImage rightSoftButton;
	
	private final Rectangle[] buttons;

	/**
	 * @param simulation
	 */
	public DefaultControl(Simulation simulation) {
		super(simulation);
		this.controlWidth = simulation.getCanvasWidth() + 20;
		this.controlHeight = simulation.getCanvasHeight() + 100;
		
		BufferedImage image =
		    new BufferedImage( this.controlWidth, this.controlHeight, BufferedImage.TYPE_4BYTE_ABGR );
		Graphics2D g = image.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	    //                RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor( Color.BLACK );
		g.fillRoundRect( 0, 0, this.controlWidth - 1, this.controlHeight - 1, 10, 10 );
		g.setColor( Color.WHITE );
		String text = simulation.getDevice().getIdentifier();
		Font font = Font.decode("ARIAL-Bold-12");
		g.setFont( font );
		FontRenderContext fc = g.getFontRenderContext();
		Rectangle2D fontBounds = font.getStringBounds(text,fc);
		g.drawString( text, (int)((this.controlWidth / 2) - (fontBounds.getWidth() / 2)), font.getSize()  );
		
		if (scrollButton == null) {
			try {
				InputStream in = getClass().getResourceAsStream("/resources/scrollbutton.png");
				if (in != null) {
					scrollButton = ImageIO.read( in );
				}
				in = getClass().getResourceAsStream("/resources/leftsoftbutton.png");
				if (in != null) {
					leftSoftButton = ImageIO.read( in );
				}
				in = getClass().getResourceAsStream("/resources/rightsoftbutton.png");
				if (in != null) {
					rightSoftButton = ImageIO.read( in );
				}			
			} catch (IOException e) {
				// TODO enough handle IOException
				e.printStackTrace();
			}
		}
		this.screenX = 10;
		this.screenY = (int) fontBounds.getHeight() + 2;
		int screenBottomY = this.screenY + simulation.getCanvasHeight() + 5;
		g.setColor( Color.LIGHT_GRAY );
		g.fillRoundRect( 10, screenBottomY, this.controlWidth - 20, this.controlHeight - screenBottomY - 5, 10, 10 );
		
		ArrayList rectanglesList = new ArrayList();
		drawImageAddRectangle(leftSoftButton, g, LEFT, screenBottomY, rectanglesList);
		drawImageAddRectangle(rightSoftButton, g, RIGHT, screenBottomY, rectanglesList);
		drawImageAddRectangle(null, g, CENTER, screenBottomY, rectanglesList); // mode change
		drawImageAddRectangle(null, g, CENTER, screenBottomY, rectanglesList); // clear
		drawImageAddRectangle(scrollButton, g, CENTER, screenBottomY, rectanglesList);
		Rectangle scrollButtonRect = (Rectangle) rectanglesList.remove( rectanglesList.size() - 1 );
		// fire:
		Rectangle rect = new Rectangle(scrollButtonRect.x + 22, scrollButtonRect.y + 22, 24, 24 );
		rectanglesList.add( rect );
		// up:
		rect = new Rectangle(scrollButtonRect.x + 20, scrollButtonRect.y, 25, 22 );
		rectanglesList.add( rect );
		// left:
		rect = new Rectangle(scrollButtonRect.x + 1, scrollButtonRect.y + 22, 22, 25 );
		rectanglesList.add( rect );
		// down:
		rect = new Rectangle(scrollButtonRect.x + 20, scrollButtonRect.y + 48, 25, 22 );
		rectanglesList.add( rect );
		// right:
		rect = new Rectangle(scrollButtonRect.x + 45, scrollButtonRect.y + 22, 22, 25 );
		rectanglesList.add( rect );
		
		this.buttons = (Rectangle[]) rectanglesList.toArray( new Rectangle[ rectanglesList.size() ] );
		
		setOpaque( true );
		setIcon( new ImageIcon( image ) );
	}
	
	private void drawImageAddRectangle( BufferedImage image, Graphics2D g, int orientation, int screenBottomY, ArrayList rectanglesList ) {
		if ( image != null ) {
			int width = image.getWidth();
			int xOffset = 10;
			int x;
			if (orientation == LEFT ) {
				x = xOffset;
			} else if (orientation == RIGHT ) {
				x = this.controlWidth - xOffset - width;
			} else {
				x = this.controlWidth / 2  - width / 2;
			}
			int height = image.getHeight();
			int y = screenBottomY + 2;
			g.drawImage( image, x, y , null );
			rectanglesList.add( new Rectangle( x, y, width, height ));
		} else {			
			rectanglesList.add( new Rectangle( -1, -1, 0, 0 ));
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.Control#getButton(int, int)
	 */
	public int getButton(int x, int y) {
		return getButton( x, y, this.buttons );
	}

}
