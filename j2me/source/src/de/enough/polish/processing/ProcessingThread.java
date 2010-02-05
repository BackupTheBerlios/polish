//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.processing;

import de.enough.polish.ui.AnimationThread;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingThread extends Thread implements Runnable {

    protected static Vector objects = new Vector();
    protected static Vector events = new Vector();
    protected static Thread thread = null;
    
    public static void addProcessingObject ( ProcessingInterface object )
    {
        removeProcessingObject(object);
        objects.addElement(object);

        if ( (objects.size() == 1) && (thread == null) )
        {
            startThread(new ProcessingThread());
        }
    }


    public static void queueEvent(ProcessingEvent event)
    {
        events.addElement(event);
    }
    
    public static void removeProcessingObject ( ProcessingInterface object )
    {
        objects.removeElement(object);
    }

    protected static void startThread(Runnable object)
    {
            thread = new Thread(object);
            thread.start();
    }

    public void run()
    {
        Enumeration objList;
        ProcessingInterface temp;
        ProcessingEvent event;
        
        while (true)
        {

            // Check if draw events have to be triggered.
            objList = objects.elements();
            while ( objList.hasMoreElements() )
            {
                temp = (ProcessingInterface) objList.nextElement();

                if ( temp.checkForRefresh() )
                {
                    // Only execute a refresh if we're in a loop.
                    // The reason for this is that if checkForRefresh() does
                    // return true and we're not in a loop, then the
                    // refresh mush have been triggered by an event handler
                    // which in turn called redraw() (thus the refresh flag was set to true ).
                    // Since redraw was already called, we should not call it again
                    // by asking for a hard refresh.
                    if ( temp.isLooping() )
                    {
                        queueEvent( new ProcessingEvent(temp,ProcessingEvent.EVENT_DRAW));
                    }
                }
            }
            

            // Execute any events that might be queued
            objList = events.elements();
            while ( objList.hasMoreElements() )
            {
                event = (ProcessingEvent) objList.nextElement();

                switch ( event.eventType )
                {
                    case ProcessingEvent.EVENT_DRAW :
                        event.object.executeRefresh(true);
                        event.object.triggerRepaint();
                        break;
                    case ProcessingEvent.EVENT_SETUP :
                        event.object.setup();
                        break;
                    case ProcessingEvent.EVENT_HAS_FOCUS :
                        event.object.focus();
                        break;
                    case ProcessingEvent.EVENT_LOST_FOCUS :
                        event.object.lostFocus();
                        break;
                    case ProcessingEvent.EVENT_POINTER_PRESSED :
                        event.object.setPointerCoordinates(event.param1, event.param2);
                        event.object.pointerPressed();
                        break;
                    case ProcessingEvent.EVENT_POINTER_DRAGGED :
                        event.object.setPointerCoordinates(event.param1, event.param2);
                        event.object.pointerDragged();
                        break;
                    case ProcessingEvent.EVENT_POINTER_RELEASED :
                        event.object.setPointerCoordinates(event.param1, event.param2);
                        event.object.pointerReleased();
                        break;
                    case ProcessingEvent.EVENT_KEY_PRESSED :
                        event.object.setKeyAndKeyCode( (char) event.param1, event.param2);
                        event.object.keyPressed();
                        break;
                    case ProcessingEvent.EVENT_KEY_RELEASED :
                        event.object.setKeyAndKeyCode( (char) event.param1, event.param2);
                        event.object.keyReleased();
                        break;
                    case ProcessingEvent.EVENT_SOFTKEY_PRESSED :
                        event.object.softkeyPressed(event.strParam1);
                        break;
                    case ProcessingEvent.EVENT_APP_SUSPEND :
                        event.object.suspend();
                        break;
                    case ProcessingEvent.EVENT_APP_RESUME :
                        event.object.resume();
                        break;
                    case ProcessingEvent.EVENT_APP_DESTROY :
                        event.object.destroy();
                        removeProcessingObject(event.object);
                        break;
                }
            }
            
            events.setSize(0);

            try
            {
                sleep(AnimationThread.ANIMATION_INTERVAL);
            }
            catch (Exception EX)
            {

            }
        }
    }


}
