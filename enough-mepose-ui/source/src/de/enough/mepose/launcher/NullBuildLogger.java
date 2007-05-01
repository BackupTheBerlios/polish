/*
 * Created on Aug 17, 2006 at 10:54:17 AM.
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
package de.enough.mepose.launcher;

import java.io.PrintStream;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.eclipse.ant.core.AntSecurityException;
import org.eclipse.core.runtime.OperationCanceledException;

public class NullBuildLogger implements BuildLogger {

    protected int fMessageOutputLevel = Project.MSG_INFO;
    private PrintStream fErr= null;
    private PrintStream fOut= null;
    protected boolean fEmacsMode= false;
    
    /**
     * An exception that has already been logged.
     */
    protected Throwable fHandledException= null;
    
    /**
     * @see org.apache.tools.ant.BuildLogger#setMessageOutputLevel(int)
     */
    public void setMessageOutputLevel(int level) {
        this.fMessageOutputLevel = level;
    }
    
    protected int getMessageOutputLevel() {
        return this.fMessageOutputLevel;
    }

    /**
     * @see org.apache.tools.ant.BuildLogger#setEmacsMode(boolean)
     */
    public void setEmacsMode(boolean emacsMode) {
        this.fEmacsMode = emacsMode;
    }

    /**
     * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
     */
    public void buildStarted(BuildEvent event) {
    }

    /**
     * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
     */
    public void buildFinished(BuildEvent event) {
        handleException(event);
        this.fHandledException = null;
    }

    /**
     * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
     */
    public void targetStarted(BuildEvent event) {
    }

    /**
     * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
     */
    public void targetFinished(BuildEvent event) {
        handleException(event);
    }

    /**
     * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
     */
    public void taskStarted(BuildEvent event) {
    }

    /**
     * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
     */
    public void taskFinished(BuildEvent event) {
        handleException(event);
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
     */
    public void messageLogged(BuildEvent event) {
        logMessage(event.getMessage(), event.getPriority());
    }

    protected PrintStream getErrorPrintStream() {
        return this.fErr;
    }
    
    protected PrintStream getOutputPrintStream() {
        return this.fOut;
    }
    
    /**
     * @see org.apache.tools.ant.BuildLogger#setErrorPrintStream(java.io.PrintStream)
     */
    public void setErrorPrintStream(PrintStream err) {
//        //this build logger logs to "null" unless
//        //the user has explicitly set a logfile to use
//        if (err == System.err) {
//            fErr= null;
//        } else {
            this.fErr = err;
//        }
    }

    /**
     * @see org.apache.tools.ant.BuildLogger#setOutputPrintStream(java.io.PrintStream)
     */
    public void setOutputPrintStream(PrintStream output) {
//        //this build logger logs to "null" unless
//        //the user has explicitly set a logfile to use
//        if (output == System.out) {
//            fOut= null;
//        } else {
            this.fOut = output;
//        }
    }
    
    protected void logMessage(String message, int priority) {
        if (priority > getMessageOutputLevel()) {
            return;
        }
        
        if (priority == Project.MSG_ERR) {
            if (getErrorPrintStream() != null && getErrorPrintStream() != System.err) {
                //user has designated to log to a logfile
                getErrorPrintStream().println(message);
            }
        } else {
            if (getOutputPrintStream() != null && getOutputPrintStream() != System.out) {
                //user has designated to log to a logfile
                getOutputPrintStream().println(message);
            } 
        }
    }
    
    protected void handleException(BuildEvent event) {
        Throwable exception = event.getException();
        if (exception == null || exception == this.fHandledException
        || exception instanceof OperationCanceledException
        || exception instanceof AntSecurityException) {
            return;
        }
        this.fHandledException = exception;
//        logMessage(MessageFormat.format()format(""),new String[] { exception.toString()}),
//                    Project.MSG_ERR);   
    }
}