/*
 * Created on Jan 26, 2006 at 5:09:51 PM.
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
package de.enough.mepose.ui;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 26, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class BuildListenerConsoleWriter implements BuildListener{

    private MessageConsoleStream consoleStream;

    public BuildListenerConsoleWriter() {
        MessageConsole console = new MessageConsole ("Polish Build",null); 
//        JavaStackTraceConsole javaConsole = new JavaStackTraceConsole();
        
//        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console}); 
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console}); 
        this.consoleStream = console.newMessageStream();
    }

    /*
     * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
     */
    public void buildStarted(BuildEvent event) {
        // TODO rickyn implement buildStarted
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
     */
    public void buildFinished(BuildEvent event) {
        // TODO rickyn implement buildFinished
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
     */
    public void targetStarted(BuildEvent event) {
        // TODO rickyn implement targetStarted
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
     */
    public void targetFinished(BuildEvent event) {
        // TODO rickyn implement targetFinished
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
     */
    public void taskStarted(BuildEvent event) {
        // TODO rickyn implement taskStarted
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
     */
    public void taskFinished(BuildEvent event) {
        // TODO rickyn implement taskFinished
        
    }

    /*
     * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
     */
    public void messageLogged(BuildEvent event) {
        this.consoleStream.println (event.getMessage());
    }
}
