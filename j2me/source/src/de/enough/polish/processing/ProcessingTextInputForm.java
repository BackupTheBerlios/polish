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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.processing;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.TextBox;
import de.enough.polish.ui.TextField;

/**
 *
 * @author Ovidiu
 */
public class ProcessingTextInputForm  implements CommandListener, Runnable {

    Command cmdOk = new Command("OK", Command.OK, 0);
    final Object internalInputLock = new Object();
    public static final Object externalInputLock = new Object();
    TextBox textBox;
    String result;
    boolean var = true ;
    protected String title = "";
    protected String text = "";
    protected int maxSize = Integer.MAX_VALUE;

    public ProcessingTextInputForm(String title, String text, int maxSize) {
        this.title = title;
        this.text = text;
        this.maxSize = maxSize;
    }

    public Object getExternalLockObject()
    {
        return externalInputLock;
    }

    public synchronized void run() {

        synchronized ( externalInputLock )
        {
            TextBox box = new TextBox( title, text, maxSize, TextField.ANY);
            box.addCommand( this.cmdOk );
            box.setCommandListener( this );
            Display.getInstance().setCurrent( box );
            this.textBox = box;
        }
        

    }

    public String getText()
    {
        return result;
    }

    public void commandAction(Command cmd, Displayable dis) {
        if (cmd == this.cmdOk)
        {
            this.result = this.textBox.getString();
        }
        
        synchronized ( externalInputLock )
        {
            externalInputLock.notifyAll();
        }
    }
}
