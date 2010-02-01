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

    public void setup()
    {
        framerate(3);
        background(120);
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
        
    }


    public void focus()
    {
        isFocused = true ;
        loop();

        // If the FPS is too low, we want an immediate redraw.
        redraw();
    }

    public void lostFocus()
    {
        isFocused = false ;

        // Item is not focused, the background will be static,
        // so there is no need to loop.
        noLoop();

        // Change the BG color
        background(120);

        // One last redraw to clear the screen and apply the BG color.
        redraw();
    }

}
