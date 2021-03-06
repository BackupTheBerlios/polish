package de.enough.mepose.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.StringUtils;
import org.eclipse.ant.core.AntSecurityException;
import org.eclipse.ant.internal.core.AbstractEclipseBuildLogger;
import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.internal.ui.launchConfigurations.AntProcess;
import org.eclipse.ant.internal.ui.launchConfigurations.AntStreamMonitor;
import org.eclipse.ant.internal.ui.launchConfigurations.AntStreamsProxy;
import org.eclipse.ant.internal.ui.launchConfigurations.TaskLinkManager;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.console.IHyperlink;
    
public class AntProcessBuildLogger extends NullBuildLogger {
    
    public static final String BUILD_SUCCESS = "build.success";
    private long fStartTime;

    /** Line separator */
    protected static final String lSep = StringUtils.LINE_SEP;
    
    /**
     * Associated process - discovered as needed to log messages
     */
    private AntProcess fProcess = null;
    
    protected void logMessage(String message, BuildEvent event, int overridePriority) {
        int priority= overridePriority;
        if (priority == -1) {
            priority= event.getPriority();
        } 
        
        if (priority > getMessageOutputLevel()) {
            return;
        }
        String property = event.getProject().getProperty(AbstractEclipseBuildLogger.ANT_PROCESS_ID);
        //        AntProcess antProcess = getAntProcess(event.getProject().getUserProperty(AntProcess.ATTR_ANT_PROCESS_ID));
        AntProcess antProcess = getAntProcess(property);
        if (antProcess == null) {
            return;
        }
        
        StringBuffer fullMessage= new StringBuffer(System.getProperty("line.separator")); //$NON-NLS-1$
        
        if (event.getException() == null && event.getTask() != null && !this.fEmacsMode) {
            adornMessage(event, fullMessage);
        } else {
            fullMessage.append(message);
        }
        message= fullMessage.toString();
        
        AntStreamMonitor monitor = getMonitor(priority);
        monitor.append(message);
        logMessageToLogFile(message, priority);
    }

    /**
     * Builds a right justified task prefix for the given build event, placing it
     * in the given string buffer. Creates the hyperlinks for the task prefix. 
     *  
     * @param event build event
     * @param fullMessage buffer to place task prefix in
     */
    private void adornMessage(BuildEvent event, StringBuffer fullMessage) {
        String name = event.getTask().getTaskName();
        if (name == null) {
            name = "null"; //$NON-NLS-1$
        }
        Location location = event.getTask().getLocation();
        StringBuffer column= new StringBuffer();
        int size = IAntUIConstants.LEFT_COLUMN_SIZE - (name.length() + 3);
        for (int i = 0; i < size; i++) {
            column.append(' ');
        }
        StringBuffer labelBuff= new StringBuffer();
        labelBuff.append('[');
        labelBuff.append(name);
        labelBuff.append("] "); //$NON-NLS-1$
        
        int offset = Math.max(size, 0) + 1;
        String label= labelBuff.toString();
        if (event.getMessage() == null) {
            return;
        }
        try {
            BufferedReader r = new BufferedReader(new StringReader(event.getMessage()));
            String line = r.readLine();
            fullMessage.append(column);
            appendAndLink(fullMessage, location, label, offset, line);
            line = r.readLine();
            while (line != null) {
                fullMessage.append(System.getProperty("line.separator")); //$NON-NLS-1$
                fullMessage.append(column);
                appendAndLink(fullMessage, location, label, offset, line);
                line = r.readLine();
            }
        } catch (IOException e) {
            if (event.getMessage() != null) {
                fullMessage.append(label).append(event.getMessage());
            }
        }
    }
    
    private void appendAndLink(StringBuffer fullMessage, Location location, String label, int offset, String line) {
        fullMessage.append(label);
        fullMessage.append(line);
        if (location != null) {
            String newLine= (label + line).trim();
            IRegion region= new Region(offset, label.length() - 3); // only want the name length "[name] "
            IHyperlink link= getTaskLink(location);
            if (link != null) {
                TaskLinkManager.addTaskHyperlink(getAntProcess(null), link, region, newLine);
            }
        }
    }

    private AntStreamMonitor getMonitor(int priority) {
        AntStreamsProxy proxy = (AntStreamsProxy) this.fProcess.getStreamsProxy();
        AntStreamMonitor monitor = null;
        switch (priority) {
            case Project.MSG_INFO:
                monitor = (AntStreamMonitor)proxy.getOutputStreamMonitor();
                break;
            case Project.MSG_ERR:
                monitor = (AntStreamMonitor)proxy.getErrorStreamMonitor();
                break;
            case Project.MSG_DEBUG:
                monitor = (AntStreamMonitor)proxy.getDebugStreamMonitor();
                break;
            case Project.MSG_WARN:
                monitor = (AntStreamMonitor)proxy.getWarningStreamMonitor();
                break;
            case Project.MSG_VERBOSE:
                monitor = (AntStreamMonitor)proxy.getVerboseStreamMonitor();
                break;
        }
        return monitor;
    }

    private void logMessageToLogFile(String message, int priority) {
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
    
    
    
    /**
     * Returns a hyperlink for the given task, or <code>null</code> if unable to
     * parse a valid location for the task. The link is set to exist at the specified
     * offset and length.
     * 
     * @return hyper link, or <code>null</code>
     */
    private IHyperlink getTaskLink(Location location) {
        //TODO: What to do here?
//        if (location != null) {
//            return AntUtil.getTaskLink(location.toString(), fBuildFileParent);
//        }
        return null;
    }   
    
    /**
     * Returns the associated Ant process, finding it if necessary, if not
     * already found.
     */
    private AntProcess getAntProcess(String processId) {
        if (this.fProcess == null && processId != null) {
            IProcess[] all = DebugPlugin.getDefault().getLaunchManager().getProcesses();
            for (int i = 0; i < all.length; i++) {
                IProcess process = all[i];
                if (process instanceof AntProcess && processId.equals(process.getAttribute(AbstractEclipseBuildLogger.ANT_PROCESS_ID))) {
                    this.fProcess = (AntProcess)process;
                    break;
                }
            }
        }
        return this.fProcess;
    }

    /* (non-Javadoc)
     * Set the start time.
     * 
     * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
     */
    public void buildStarted(BuildEvent event) {
        this.fStartTime = System.currentTimeMillis();
    }
    
    /* (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
     */
    public void buildFinished(BuildEvent event) {
        handleException(event);

        Throwable error = event.getException();
        StringBuffer message = new StringBuffer();

        if (error == null) {
            message.append(StringUtils.LINE_SEP);
            message.append("BUILD SUCCESSFUL");
            event.getProject().setProperty(BUILD_SUCCESS,"true");
        } else {
            message.append(StringUtils.LINE_SEP);
            message.append("BUILD FAILED");
            message.append(StringUtils.LINE_SEP);
            event.getProject().setProperty(BUILD_SUCCESS,"false");

            if (Project.MSG_VERBOSE <= getMessageOutputLevel()
                || !(error instanceof BuildException)) {
                message.append(StringUtils.getStackTrace(error));
            } else {
                if (error instanceof BuildException) {
                    message.append(error.toString()).append(lSep);
                } else {
                    message.append(error.getMessage()).append(lSep);
                }
            }
        }
        message.append(StringUtils.LINE_SEP);
        message.append("Total time: ");
        message.append(getTimeString(System.currentTimeMillis() - this.fStartTime));

        String msg = message.toString();
        if (error == null) {
            logMessage(msg,Project.MSG_INFO);
        } else {
            logMessage(msg,Project.MSG_INFO);
        }
        
        this.fHandledException = null;
//        logMessage(getTimeString(System.currentTimeMillis() - fStartTime), event, fMessageOutputLevel);
        this.fProcess = null;
        event.getProject().removeBuildListener(this);
    }
    
    private String getTimeString(long milliseconds) {
            long seconds = milliseconds / 1000;
            long minutes = seconds / 60;
            seconds= seconds % 60;
        
//            StringBuffer result= new StringBuffer(AntSupportMessages.getString("AntProcessBuildLogger.Total_time")); //$NON-NLS-1$
            StringBuffer result= new StringBuffer("Total Time: "); //$NON-NLS-1$
            if (minutes > 0) {
                result.append(minutes);
                if (minutes > 1) {
//                    result.append(AntSupportMessages.getString("AntProcessBuildLogger._minutes_2")); //$NON-NLS-1$
                    result.append(" minutes"); //$NON-NLS-1$
                } else {
                    result.append(" minute"); //$NON-NLS-1$
                }
            }
            if (seconds > 0) {
                if (minutes > 0) {
                    result.append(' ');
                }
                result.append(seconds);
            
                if (seconds > 1) {
//                    result.append(AntSupportMessages.getString("AntProcessBuildLogger._seconds_4")); //$NON-NLS-1$
                    result.append(" seconds"); //$NON-NLS-1$
                } else {
//                    result.append(AntSupportMessages.getString("AntProcessBuildLogger._second_5")); //$NON-NLS-1$
                    result.append(" second"); //$NON-NLS-1$
                } 
            }
            if (seconds == 0 && minutes == 0) {
                result.append(milliseconds);
                result.append(" miliseconds");
            }
            
            result.append(System.getProperty("line.separator")); //$NON-NLS-1$
            return result.toString();
        }
    
    /* (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
     */
    public void messageLogged(BuildEvent event) {
        if (event.getPriority() > getMessageOutputLevel()) {
            return;
        }
        if (event.getMessage() != null && event.getMessage().length() > 0) {
            logMessage(event.getMessage(), event, -1);
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
//        logMessage(MessageFormat.format(AntSupportMessages.getString("AntProcessBuildLogger.BUILD_FAILED__{0}_1"), new String[] { exception.toString()}), //$NON-NLS-1$
//                    event, Project.MSG_ERR);    
    }

    protected void logMessage(String message, int priority) {
        if (priority > getMessageOutputLevel()) {
            return;
        }
        
        if (priority == Project.MSG_ERR) {
            PrintStream errorPrintStream = getErrorPrintStream();
            if (errorPrintStream != null) {
                //user has designated to log to a logfile
                errorPrintStream.println(message);
            }
        } else {
            PrintStream outputPrintStream = getOutputPrintStream();
            if (outputPrintStream != null) {
                //user has designated to log to a logfile
                outputPrintStream.println(message);
            } 
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
     */
    public void targetStarted(BuildEvent event) {
        if (Project.MSG_INFO > getMessageOutputLevel()) {
            return;
        }
        StringBuffer msg= new StringBuffer(System.getProperty("line.separator")); //$NON-NLS-1$
        msg.append(event.getTarget().getName());
        msg.append(':');
        logMessage(msg.toString(), event, Project.MSG_INFO);
    }
}