package de.enough.mepose.launcher;
 
  
 public class AntLogger extends AntProcessBuildLogger /*implements IAntDebugController, IDebugBuildLogger*/ {
//     hs_err_pid12945.log
//     private AntDebugState fDebugState= null;
//     
//     private List fBreakpoints= null;
//     
//     private AntDebugTarget fAntDebugTarget;
//     private boolean fResumed= false;
//     
//     /* (non-Javadoc)
//      * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
//      */
//     public void buildStarted(BuildEvent event) {
//         fDebugState= new AntDebugState(this);
//         super.buildStarted(event);
//         IProcess process= getAntProcess(fProcessId);
//         ILaunch launch= process.getLaunch();
//         fAntDebugTarget= new AntDebugTarget(launch, process, this);
//         launch.addDebugTarget(fAntDebugTarget);
//         
//         fAntDebugTarget.buildStarted();
//         fDebugState.buildStarted();
//     }
// 
//     /* (non-Javadoc)
//      * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
//      */
//     public void taskFinished(BuildEvent event) {
//         super.taskFinished(event);
//         fDebugState.taskFinished();
//     }
//     
//     /* (non-Javadoc)
//      * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
//      */
//     public void taskStarted(BuildEvent event) {
//         super.taskStarted(event);
//         fDebugState.taskStarted(event);
//     }
//     
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.antsupport.logger.util.IDebugBuildLogger#waitIfSuspended()
//      */
//     public synchronized void waitIfSuspended() {
//         fResumed= false;
//         IBreakpoint breakpoint= breakpointAtLineNumber(fDebugState.getBreakpointLocation());
//         if (breakpoint != null) {
//              fAntDebugTarget.breakpointHit(breakpoint);
//              try {
//                  while (!fResumed) {
//                      wait(500);
//                      checkCancelled();
//                  }
//              } catch (InterruptedException e) {
//              }
//         } else if (fDebugState.getCurrentTask() != null) {
//             int detail= -1;
//             boolean shouldSuspend= true;
//             if (fDebugState.isStepIntoSuspend()) {
//                 detail= DebugEvent.STEP_END;
//                 fDebugState.setStepIntoSuspend(false); 
//             } else if ((fDebugState.getLastTaskFinished() != null && fDebugState.getLastTaskFinished() == fDebugState.getStepOverTask()) || fDebugState.shouldSuspend()) {
//                 detail= DebugEvent.STEP_END;
//                 fDebugState.setShouldSuspend(false);
//                 fDebugState.setStepOverTask(null);
//             } else if (fDebugState.isClientSuspend()) {
//                 detail= DebugEvent.CLIENT_REQUEST;
//                 fDebugState.setClientSuspend(false);
//             } else {
//                 shouldSuspend= false;
//             }
//             if (shouldSuspend) {
//                 fAntDebugTarget.suspended(detail);
//                 try {
//                     while (!fResumed) {
//                         wait(500);
//                         checkCancelled();
//                     }
//                 } catch (InterruptedException e) {
//                 }
//             }
//         }
//     }
// 
//     private void checkCancelled() {
//         AntProcess process= getAntProcess(fProcessId);
//         if (process != null && process.isCanceled()) {
//             throw new OperationCanceledException(AntSupportMessages.AntProcessDebugBuildLogger_1);
//         }
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#resume()
//      */
//     public synchronized void resume() {
//         fResumed= true;
//         notifyAll();
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#suspend()
//      */
//     public synchronized void suspend() {
//         fDebugState.setClientSuspend(true);
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#stepInto()
//      */
//     public synchronized void stepInto() {
//         fDebugState.setStepIntoSuspend(true);
//         fResumed= true;
//         notifyAll();
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#stepOver()
//      */
//     public synchronized void stepOver() {
//         fResumed= true;
//         fDebugState.stepOver();
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#handleBreakpoint(org.eclipse.debug.core.model.IBreakpoint, boolean)
//      */
//     public void handleBreakpoint(IBreakpoint breakpoint, boolean added) {
//         if (added) {
//             if (fBreakpoints == null) {
//                 fBreakpoints= new ArrayList();
//             }
//             if (!fBreakpoints.contains(breakpoint)) {
//                 fBreakpoints.add(breakpoint);
//             }
//         } else {
//             fBreakpoints.remove(breakpoint);
//         }
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#getProperties()
//      */
//     public void getProperties() {
//         if (!fAntDebugTarget.isSuspended()) {
//             return;
//         }
//         StringBuffer propertiesRepresentation= new StringBuffer();
//         fDebugState.marshallProperties(propertiesRepresentation, false);
//         if (fAntDebugTarget.getThreads().length > 0) {
//             ((AntThread) fAntDebugTarget.getThreads()[0]).newProperties(propertiesRepresentation.toString());
//         }
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#getStackFrames()
//      */
//     public void getStackFrames() {
//         StringBuffer stackRepresentation= new StringBuffer();
//         fDebugState.marshalStack(stackRepresentation);
//         ((AntThread) fAntDebugTarget.getThreads()[0]).buildStack(stackRepresentation.toString());
//     }
//     
//     private IBreakpoint breakpointAtLineNumber(Location location) {
//         if (fBreakpoints == null || location == null || location == Location.UNKNOWN_LOCATION) {
//             return null;
//         }
//         int lineNumber= fDebugState.getLineNumber(location);
//         File locationFile= new File(fDebugState.getFileName(location));
//         for (int i = 0; i < fBreakpoints.size(); i++) {
//             ILineBreakpoint breakpoint = (ILineBreakpoint) fBreakpoints.get(i);
//             int breakpointLineNumber;
//             try {
//                 if (!breakpoint.isEnabled()) {
//                     continue;
//                 }
//                 breakpointLineNumber = breakpoint.getLineNumber();
//             } catch (CoreException e) {
//                return null;
//             }
//             IFile resource= (IFile) breakpoint.getMarker().getResource();
//             if (breakpointLineNumber == lineNumber && resource.getLocation().toFile().equals(locationFile)) {
//                 return breakpoint;
//             }
//         }
//         return null;
//     }
//     
//     /* (non-Javadoc)
//      * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
//      */
//     public void targetStarted(BuildEvent event) {
//         fDebugState.targetStarted(event);
//         waitIfSuspended();
//         super.targetStarted(event);
//     }
//     
//     /* (non-Javadoc)
//      * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
//      */
//     public void targetFinished(BuildEvent event) {
//         super.targetFinished(event);
//         fDebugState.setTargetExecuting(null);
//     }
// 
//     /* (non-Javadoc)
//      * @see org.eclipse.ant.internal.ui.debug.IAntDebugController#unescapeString(java.lang.StringBuffer)
//      */
//     public StringBuffer unescapeString(StringBuffer propertyValue) {
//         return propertyValue;
//     }
 }

