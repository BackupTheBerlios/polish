//#condition polish.usePolishGui

/*
 * Created on 27-May-2005 at 18:54:36.
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
package de.enough.polish.ui.screenanimations;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.AccessibleCanvas;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Moves the new screen diagonally over the old screen.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		screen-change-animation: diagonal;
 * 		diagonal-screen-change-animation-speed: 4; ( 2 is default )
 * 		diagonal-screen-change-animation-move-previous: true; ( false is default )
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2008</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DiagonalScreenChangeAnimation extends ScreenChangeAnimation {
	private int currentX;
	private int currentY;
	//#if polish.css.diagonal-screen-change-animation-speed
		private int speed = 3;
	//#endif
	//#if polish.css.diagonal-screen-change-animation-move-previous
		private boolean movePrevious;
	//#endif
	//#if polish.css.diagonal-screen-change-animation-background-color
		private int backgroundColor = 0xffffff;
	//#endif

	/**
	 * Creates a new animation 
	 */
	public DiagonalScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable ) 
	{
		//#if polish.css.diagonal-screen-change-animation-speed
			Integer speedInt = style.getIntProperty( "diagonal-screen-change-animation-speed" );
			if (speedInt != null ) {
				this.speed = speedInt.intValue();
			}
		//#endif
		//#if polish.css.diagonal-screen-change-animation-move-previous
			Boolean movePreviousBool = style.getBooleanProperty("diagonal-screen-change-animation-move-previous");
			if (movePreviousBool != null) {
				this.movePrevious = movePreviousBool.booleanValue();
			}
		//#endif
		//#if polish.css.diagonal-screen-change-animation-background-color
			Color color = style.getColorProperty( "diagonal-screen-change-animation-background-color" );
			if (color != null) {
				this.backgroundColor = color.getColor();
			}
		//#endif
		super.show(style, dsplay, width, height, lstScreenImage,
				nxtScreenImage, nxtCanvas, nxtDisplayable );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
		if (this.currentY < this.screenHeight) {
			//#if polish.css.diagonal-screen-change-animation-speed
				this.currentY += this.speed;
			//#else
				this.currentY += 3;
			//#endif
			this.currentX = ( this.currentY * this.screenWidth ) / this.screenHeight;
			return true;
		} else {
			//#if polish.css.diagonal-screen-change-animation-speed
				this.speed = 3;
			//#endif
			this.currentY = 0;
			this.currentX = 0;
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		int y = 0;
		int x = 0;
		//#if polish.css.diagonal-screen-change-animation-move-previous
			if (this.movePrevious) {
				//#if polish.css.diagonal-screen-change-animation-background-color
					g.setColor( this.backgroundColor );
				//#else
					g.setColor( 0xffffff );
				//#endif
				g.fillRect( 0, 0, this.screenWidth, this.screenHeight );
				x = -this.currentX;
				y = -this.currentY;
			}
		//#endif
		g.drawImage( this.lastCanvasImage, x, y, Graphics.TOP | Graphics.LEFT );
		g.drawImage( this.nextCanvasImage, this.screenWidth - this.currentX, this.screenHeight -  this.currentY, Graphics.TOP | Graphics.LEFT );
	}

}
