/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.example;

import de.enough.polish.processing.ProcessingContext;

/**
 * An example ProcessingContext used as a background
 *
 * @author Ovidiu
 */
public class ProcessingBackgroundExample extends ProcessingContext {

    boolean isFocused = false ;

    int count = 0;

    public void setup()
    {
        framerate(3);
        setTransparentColor(255, 255, 255);

        // The default transparent color is white
        transparentDrawing();
    };

    public void draw()
    {
        count++;

        // If the item is focused, randomly change it's background color.
        // NOTE: Due to the way background() works ("the background
        // color is used to refresh the display window _between_ frames")
        // changes to the background won't be visible until the NEXT FRAME.
        if ( isFocused )
        {
            background(255,millis() % 100, millis() % 255);
        }
        else
        {
            // If we're not focused, make the background transparent
            background(255,255,255);
        }

        // Toggle betweeen fast line drawing and regular line drawing
        if ( count % 2 == 0 )
        {
            enableFastDrawing();
        }
        else
        {
            disableFastDrawing();
        }

        // Set the stroke color to white, which will make all strokes transparent.
        stroke(255,255,255);

        // Increase the stroke thickness
        strokeWeight(14);

        // Draw two lines on the background
        line(10,10,width-15, height/3);
        line(width-15, height/3+15,10,height-10);

        // Draw some guidelines to better see the difference in quality between fast and normal lines
        stroke(255,255,0);
        strokeWeight(1);
        line(10,10,width-15, height/3);
        line(width-15, height/3+15,10,height-10);;

        
    }

    public void focus()
    {
        isFocused = true;
        redraw();
    }

    public void lostFocus()
    {
        isFocused = false;
        redraw();
    }

}
