/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    String text = "Press the softkey." ;

    public void setup()
    {
        framerate(10);
        softkey("Press me !");
        strokeWeight(10);
        test = loadFont("example.bmf", color(120,100,50,255));
        textFont(test);
        textAlign(CENTER);
    };

    public void draw()
    {
        int lineLength = 30;
        stroke(5,100,millis() % 255);
        int x = 0 + ( millis() / 30 ) % ( width - lineLength );
        int y = 10;
        text(text,0, y+15, width, height);
        line(x,y,x+lineLength, y);
    }

    public void softkeyPressed(String label)
    {
        System.out.println("Softkey command pressed : " + label);
        text = "You pressed the softkey !" ;
    }

    public void focus()
    {
        strokeWeight(20);
        background(255,255,0);
    }

    public void lostFocus()
    {
        strokeWeight(10);
        background(120);
    }

    public void keyPressed()
    {
        System.out.println("Pressed key: " + key );
    }

    public void keyReleased()
    {
        System.out.println("Released key: " + key );
    }

    public void pointerPressed()
    {
        System.out.println("Pointer pressed position : X-" + pointerX + " Y-" + pointerY );
    }

    public void pointerReleased()
    {
        System.out.println("Pointer released position : X-" + pointerX + " Y-" + pointerY );
    }

    public void pointerDragged()
    {
        System.out.println("Pointer dragged position : X-" + pointerX + " Y-" + pointerY );
    }

}
