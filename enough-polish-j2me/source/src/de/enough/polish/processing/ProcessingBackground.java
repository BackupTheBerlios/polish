

package de.enough.polish.processing;

import de.enough.polish.ui.Background;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

/**
 * Implements a Processing Background
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingBackground extends Background implements ProcessingContextContainerInterface {

    public transient ProcessingInterface context ;

    public boolean lastFocusedState = false;
    public boolean focusHasBeenInitialized = false;

    public ProcessingBackground ( ProcessingInterface context)
    {
        this.context = context ;
        this.context.setParent(this);
        this.borderWidth = 0;
        context.signalInitialization();
    }

    public void setParentItem(Item parent)
    {
        super.setParentItem(parent);
        
        // Disable item image caching.

        //#if polish.css.filter
        parent.cacheItemImage = false ;
        //#endif
    }

    public void paint(int x, int y, int width, int height, Graphics g) {

        // This part of the code deals with focus/lostfocus events.
        // The background class has no provisions for focus events
        // but based on the state of the parent item we can
        // deduce if such an event has occured.
        if ( focusHasBeenInitialized == true )
        {
            if ( parent.isFocused != lastFocusedState )
            {
                if ( parent.isFocused == true )
                {
                    lastFocusedState = true ;
                    context.signalHasFocus();
                }
                else
                {
                    lastFocusedState = false ;
                    context.signalLostFocus();
                }
            }
        }
        else
        {
                // Set the focus initialized to true
                focusHasBeenInitialized = true ;

                // Set the last focused state to the opposite of the current one
                // so that the next time paint() is called a focus/lostfocus
                // event will triggered
                lastFocusedState = ! parent.isFocused ;

                // Recursively call paint to trigger the focus/lostfocus
                // event and then graciously exit.
                paint(x, y, width, height, g);
                return;
        }

       context.signalSizeChange(width, height);

       if ( context.isDrawingTransparent() == false )
       {
          g.drawImage(context.getBuffer(), x, y, Graphics.TOP | Graphics.LEFT );
       }
       else
       {
          context.getTransparentRgbImage().paint(x, y, g);
       }
    }

    public void processingRequestRepaint() {

        if ( parent != null )
        {
            Screen scr = parent.getScreen();

            if ( scr != null )
            {
              scr.repaint(parent.getAbsoluteX(), parent.getAbsoluteY(), parent.getBackgroundWidth(), parent.getBackgroundHeight());
            }
        }
    } 

    public void releaseResources() {
		context.signalDestroy();
	}

    public void setSoftkey(String text)
    {
        // Do nothing
    }
    

}
