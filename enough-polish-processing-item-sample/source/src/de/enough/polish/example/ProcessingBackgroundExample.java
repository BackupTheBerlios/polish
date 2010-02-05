/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    int ceva = 22;

    public void setup()
    {
        framerate(3);

        // The default transparent color is white
        transparentDrawing();
    };

    public void draw()
    {
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

        stroke(255,255,255);
        strokeWeight(10);
        line(0,0,100,100);

        
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
