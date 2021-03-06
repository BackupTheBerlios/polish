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

import de.enough.polish.processing.PFont;
import de.enough.polish.processing.ProcessingContext;

/**
 * An example ProcessingContext
 *
 * @author Ovidiu
 */
public class ProcessingContextExample extends ProcessingContext {

    PFont test ;

    String text = "Never been focused." ;

    public void setup()
    {
        framerate(10);
        softkey("Hello! :)");
        strokeWeight(10);
        test = loadFont("example.bmf", color(120,100,255,255));
        textFont(test);
        textAlign(CENTER);
        transparentDrawing();
        background(255,255,255); // Make the background white, which is also the default transparent color
    };

    public void draw()
    {
        int lineLength = 30;
        stroke(5,100,millis() % 255);
        int x = 0 + ( millis() / 30 ) % ( width - lineLength );
        int y = 13;
        text(text,0, y+15, 200, 12000);
        line(x,y,x+lineLength, y);
    }

    public void softkeyPressed(String label)
    {
        println("Softkey command pressed : " + label);
    }

    public void focus()
    {
        strokeWeight(20);
        background(255,255,0);
        text = "Press 9 to change this text." ;
    }

    public void lostFocus()
    {
        text = "I don't have focus.";
        strokeWeight(10);
        background(255,255,255);
    }


    public void keyPressed()
    {
        println("Pressed key: " + key );
    }

    public void keyReleased()
    {
        if ( key == '9' )
        {
            text = textInput("Please enter new text", "Lorem ipsum lorem.",999);
        }
        
        println("Released key: " + key );
    }

    public void pointerPressed()
    {
        println("Pointer pressed position : X-" + pointerX + " Y-" + pointerY );
    }

    public void pointerReleased()
    {
        println("Pointer released position : X-" + pointerX + " Y-" + pointerY );
    }

    public void pointerDragged()
    {
        println("Pointer dragged position : X-" + pointerX + " Y-" + pointerY );
    }

}
