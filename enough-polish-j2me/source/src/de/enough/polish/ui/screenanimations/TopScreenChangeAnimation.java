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
import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p>Moves the new screen from the top to the bottom.</p>
 * <p>Activate this animation by specifying it in the corresponding screen's style:
 * <pre>
 * .myAlert {
 * 		screen-change-animation: top;
 * 		top-screen-change-animation-speed: 4; ( 2 is default )
 * 		top-screen-change-animation-move-previous: true; ( false is default )
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
public class TopScreenChangeAnimation extends ScreenChangeAnimation {
	
	private int currentY;
	//#if polish.css.top-screen-change-animation-speed
		private int speed = 2;
	//#endif
	//#if polish.css.top-screen-change-animation-move-previous
		private boolean movePrevious;
	//#endif

	/**
	 * Creates a new animation 
	 */
	public TopScreenChangeAnimation() {
		super();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void show(Style style, Display dsplay, int width, int height,
			Image lstScreenImage, Image nxtScreenImage, AccessibleCanvas nxtCanvas, Displayable nxtDisplayable  ) 
	{
		//#if polish.css.top-screen-change-animation-speed
			Integer speedInt = style.getIntProperty( "top-screen-change-animation-speed" );
			if (speedInt != null ) {
				this.speed = speedInt.intValue();
			}
		//#endif
		//#if polish.css.top-screen-change-animation-move-previous
			Boolean movePreviousBool = style.getBooleanProperty("top-screen-change-animation-move-previous");
			if (movePreviousBool != null) {
				this.movePrevious = movePreviousBool.booleanValue();
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
			//#if polish.css.top-screen-change-animation-speed
				this.currentY += this.speed;
			//#else
				this.currentY += 2;
			//#endif
			return true;
		} else {
			//#if polish.css.top-screen-change-animation-speed
				this.speed = 2;
			//#endif
			this.currentY = 0;
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		int y = 0;
		//#if polish.css.top-screen-change-animation-move-previous
			if (this.movePrevious) {
				y = this.currentY;
			}
		//#endif
		g.drawImage( this.lastCanvasImage, 0, y, Graphics.TOP | Graphics.LEFT );
		g.drawImage( this.nextCanvasImage, 0, -this.screenHeight +  this.currentY, Graphics.TOP | Graphics.LEFT );
	}

}
