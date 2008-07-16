package de.enough.mepose.launcher;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.DemuxInputStream;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.eclipse.ant.internal.core.AbstractEclipseBuildLogger;
import org.eclipse.ant.internal.ui.launchConfigurations.AntProcess;
import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.ui.MeposeUIPlugin;
import de.enough.utils.AntBox;

public class MIDletLaunchConfigurationDelegate extends
        AbstractJavaLaunchConfigurationDelegate {

    private static final int EMULATOR_STARTUP_TIME_TILL_CONNECT_READY = 4000;
    private static final String MSG_CAN_NOT_BUILD_PROJECT = "Can not build project";

    private InputStream oldIn;
    private PrintStream oldOut;
    
    // private static final int EMULATOR_STARTUP_TIME = 5000;
    private PrintStream oldErr;
    private BuildRunnable buildRunnable;
    private IProject project;
    private AbstractSourceLookupDirector sourceLocator;

    public MIDletLaunchConfigurationDelegate() {
        System.out.println("Instance of MIDletLaunchConfigurationDelegate created");
    }
    
    public void launch(ILaunchConfiguration configuration, String mode,
                       ILaunch launch, final IProgressMonitor monitor)
                                                                      throws CoreException {

        boolean isDebug = "debug".equals(mode);

        this.project = extractProject(configuration);

        MeposeModel model = extractModel(configuration);

        File buildxml = model.getBuildxml();

        String currentDeviceIdentifier = model.getCurrentDeviceIdentifier();
        if (currentDeviceIdentifier == null) {
            showErrorBox(MSG_CAN_NOT_BUILD_PROJECT, "No device selected for build");
            return;
        }

        monitor.subTask("Setting up build environment.");
        monitor.worked(1);

        // Make a new antBox so everything is nicely initialized.
        final AntBox antBox = new AntBox(buildxml);
        try {
            antBox.createProject();
        }
        catch(Throwable t) {
            MultiStatus multiStatus = new MultiStatus(MeposeUIPlugin.ID,0,"Could not create ant project.",null);
            multiStatus.add(new Status(IStatus.ERROR,MeposeUIPlugin.ID,0,t.getClass().getName(),t));
            throw new CoreException(multiStatus);
        }
        
        //FIXME: Set more build.control properties.
        antBox.setProperty("build.control","true");
        antBox.setProperty("device", currentDeviceIdentifier);
        antBox.setProperty("dir.work", "build/test");
        antBox.setProperty("test", "true");
        if (isDebug) {
            antBox.setProperty("debug", "true");
        }
        Project antProject = antBox.getProject();
        AntProcessBuildLogger antProcessBuildLogger = new AntProcessBuildLogger();
        antProcessBuildLogger.setEmacsMode(false);

        try {
            this.oldIn = System.in;
            this.oldOut = System.out;
            this.oldErr = System.err;

            System.setIn(new DemuxInputStream(antProject));
            System.setOut(new PrintStream(new DemuxOutputStream(antProject,
                                                                false)));
            System.setErr(new PrintStream(new DemuxOutputStream(antProject,
                                                                true)));

            antProcessBuildLogger.setErrorPrintStream(System.err);
            antProcessBuildLogger.setOutputPrintStream(System.out);
            antBox.addLogger(antProcessBuildLogger);

            // final String[] targets = new String[]
            // {"clean","test","j2mepolish"};
            final String[] targets = new String[] { "emulator" };

            long timeStamp = System.currentTimeMillis();
            String idStamp = Long.toString(timeStamp);

            Map attributes = new HashMap();
            attributes.put(IProcess.ATTR_PROCESS_TYPE,IAntLaunchConfigurationConstants.ID_ANT_PROCESS_TYPE);
            attributes.put(AbstractEclipseBuildLogger.ANT_PROCESS_ID, idStamp);
            attributes.put(DebugPlugin.ATTR_CAPTURE_OUTPUT, "true");
            antBox.setProperty(AbstractEclipseBuildLogger.ANT_PROCESS_ID,idStamp);

            // MeposeSourceLocator meposeSourceLocator = new
            // MeposeSourceLocator(model);
            // meposeSourceLocator.initializeParticipants();
            // launch.setSourceLocator(meposeSourceLocator);

            monitor.subTask("Setting source lookup path.");
            monitor.worked(1);

            AntProcess antProcess = new MeposeProcess("J2ME Polish", launch,attributes);

            antProject.fireBuildStarted();
            monitor.subTask("Executing build.xml");
            monitor.worked(1);
            
            this.buildRunnable = new BuildRunnable(antBox,targets);
            Thread thread = new Thread(this.buildRunnable);
            thread.start();
            
            //TODO: Put this in the finally.
//            antProject.fireBuildFinished(r.get);
//            String property = antBox.getProject().getProperty(AntProcessBuildLogger.BUILD_SUCCESS);
//            error = "false".equals(property);
//            if (error) {
//                return;
//            }

            if(!isDebug) {
                monitor.done();
            }
            else {
                String connectorId = configuration
                        .getAttribute(
                                      IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
                                      (String) null);
                IVMConnector connector = null;

                if (connectorId == null) {
                    connector = JavaRuntime.getDefaultVMConnector();
                } else {
                    connector = JavaRuntime.getVMConnector(connectorId);
                }

                if (connector == null) {
                    abort(
                          MIDletLauncherMessages.ConnectorNotSpecified,
                          null,
                          IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
                }

                Map argMap = configuration
                        .getAttribute(
                                      IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
                                      new HashMap());

                int connectTimeout = JavaRuntime.getPreferences()
                        .getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
                argMap.put("timeout", "" + connectTimeout);

                // TODO: Get this value from the tab.
                argMap.put("port", "" + 8000);
                argMap.put("hostname", "127.0.0.1");

                // Check for cancellation.
                if (monitor.isCanceled()) {
                    return;
                }

                // monitor.worked(1);
                // monitor.subTask(MIDletLauncherMessages.CreatingSourceLocator);

                // Set the default source locator if required.
                // setDefaultSourceLocator(launch, configuration);

                monitor.worked(1);

                monitor.subTask("Waiting '" + EMULATOR_STARTUP_TIME_TILL_CONNECT_READY
                                + "' for emulator to come up.");
                try {
                    for(int i = 0;i<4;i++) {
                        if (monitor.isCanceled()) {
                            return;
                        }
                        Thread.sleep(EMULATOR_STARTUP_TIME_TILL_CONNECT_READY);
                        monitor.worked(1);
                    }
                } catch (InterruptedException exception) {
                    // TODO rickyn handle InterruptedException
                    exception.printStackTrace();
                }
                // Connect to remote VM.
                monitor.subTask("Connecting to emulator.");
                monitor.worked(1);
                
                while( ! this.buildRunnable.isFinished()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException exception) {
                        //
                    }
                }
                
                connector.connect(argMap, monitor, launch);
                
                // We have connected. Thats means the build should be finised.
                
                // Create SourceLocator objects.
                //FIXME: Not needed because we have the MeposeSourceLocator??
//                this.sourceLocator = (AbstractSourceLookupDirector) launch.getSourceLocator();
//                ISourceContainer[] sourceContainers = this.sourceLocator.getSourceContainers();
//                sourceContainers = createNewSourceContainers(model, sourceContainers);
//                this.sourceLocator.setSourceContainers(sourceContainers);

                monitor.done();
            }
            
            while( ! thread.isInterrupted() && ! antProcess.isCanceled() && ! monitor.isCanceled() && ! this.buildRunnable.isFinishedAbnormally()) {
                try {
//                    System.out.println("DEBUG:MIDletLaunchConfigurationDelegate.launch(...):sleeping.");
                    Thread.sleep(500);
                } catch (InterruptedException exception) {
                    // If this thread is interrupted, interrupt the worker, too and finish.
                    if( ! thread.isInterrupted()) {
                        thread.interrupt();
                    }
                }
            }
            //TODO: Is this the right place or should this be done in the finally?
            if(!thread.isInterrupted()) {
                thread.interrupt();
            }
//            if(!antProcess.isCanceled()) {
//                System.out.println("DEBUG:MIDletLaunchConfigurationDelegate.launch(...):terminating antProcess.");
//                antProcess.terminate();
//            }
//            System.out.println("DEBUG:MIDletLaunchConfigurationDelegate.launch(...):launch finished.");
        } finally {
//            System.out.println("DEBUG:MIDletLaunchConfigurationDelegate.launch(...):firing build finish");
            antProject.fireBuildFinished(this.buildRunnable.getErrorThrowable());
            disconnectDebugTargets(launch);
            terminateProcesses(launch);
            launch.terminate();
            System.setIn(this.oldIn);
            System.setOut(this.oldOut);
            System.setErr(this.oldErr);
            this.project.touch(new NullProgressMonitor());
            this.project.refreshLocal(IResource.DEPTH_INFINITE,monitor);
            monitor.done();
            
            
            //Do not remove the launch at the end of the game but at the start of a new game.
//            getLaunchManager().removeLaunch(launch);
        }
    }

    private IProject extractProject(ILaunchConfiguration configuration) throws CoreException{
        String projectName;
        projectName = configuration.getAttribute(MeposeConstants.ID_PROJECT_NAME, "");

        IProject tempProject = ResourcesPlugin.getWorkspace().getRoot()
                        .getProject(projectName);
        if (tempProject == null) {
            throw new CoreException(new Status(IStatus.ERROR,MeposeUIPlugin.ID,0,"The project '" + projectName + "' does not exists.",null));
        }
        return tempProject;
    }
    
    private MeposeModel extractModel(ILaunchConfiguration configuration) throws CoreException{
        MeposeModel model = MeposePlugin.getDefault().getMeposeModelManager().getModel(this.project);
        if (model == null) {
            throw new CoreException(new Status(IStatus.ERROR,MeposeUIPlugin.ID,0,"No MeposeModel for project '" + this.project.getName() + "'",null));
        }
        return model;
    }
    
    /**
     * @param model
     * @param sourceContainers
     */
//    private ISourceContainer[] createNewSourceContainers(MeposeModel model, ISourceContainer[] sourceContainers) {
//        // TODO: Do not hardwire the path here.
//        Path path;
//        LinkedList list = new LinkedList();
//        list.addAll(Arrays.asList(sourceContainers));
//        
//        path = new Path(model.getProjectHome()+"/source/src");
//        ISourceContainer mainSourceContainer = new DirectorySourceContainer(path,true);
//        list.add(mainSourceContainer);
//        
//        Device currentDevice = model.getCurrentDevice();
//        String sourceDir = currentDevice.getSourceDir();
//        if(sourceDir != null) {
//            path = new Path(sourceDir);
//            ISourceContainer deviceSourceContainer = new DirectorySourceContainer(path,true);
//            list.add(deviceSourceContainer);
//        }
//        
//        return (ISourceContainer[]) list.toArray(new ISourceContainer[list.size()]);
//        
//        //TODO: How to solve this? The sourcePath of a device is initialized first the build started.
////        String unprocessedBuildPath = model.getSourcePath(currentDevice);
////        if(unprocessedBuildPath != null) {
////            ISourceContainer unprocessedBuildContainer = new DirectorySourceContainer(new Path(unprocessedBuildPath),true);
////            sourceContainers.add(unprocessedBuildContainer);
////        }
//    }

    
    /**
     * @param launch
     */
    private void terminateProcesses(ILaunch launch) {
        IProcess[] processes = launch.getProcesses();
        for (int i = 0; i < processes.length; i++) {
            IProcess process = processes[i];
            if (process.canTerminate()) {
                if(process instanceof MeposeProcess) {
                    ((MeposeProcess)process).terminated();
                }
            }
        }
    }


    /**
     * @param launch
     * @throws DebugException
     */
    private void disconnectDebugTargets(ILaunch launch) throws DebugException {
        IDebugTarget[] debugTargets = launch.getDebugTargets();
        for (int i = 0; i < debugTargets.length; i++) {
            IDebugTarget target = debugTargets[i];
            if (target.canDisconnect()) {
                target.disconnect();
            }
        }
    }

//    /**
//     * 
//     */
//    private void setupConsole() {
//        IConsoleManager consoleManager = ConsolePlugin.getDefault()
//                .getConsoleManager();
//        IConsole[] consoles = consoleManager.getConsoles();
//        for (int i = 0; i < consoles.length; i++) {
//            IConsole console = consoles[i];
//            if (MeposeUIPlugin.CONSOLE_NAME.equals(console.getName())) {
//                consoleManager.showConsoleView(console);
//                ((MessageConsole) console).clearConsole();
//                break;
//            }
//        }
//    }

    private void showErrorBox(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Shell activeShell = Display.getDefault().getActiveShell();
                MessageDialog.openError(activeShell, title, message);
            }
        });
    }
    
    class BuildRunnable implements Runnable{

        private AntBox antBox;
        private String[] targets;
        private Throwable throwable;
        private boolean finished = false;
        private boolean finishedAbnormally = false;
        public BuildRunnable(AntBox antBox, String[] targets) {
            this.antBox = antBox;
            this.targets = targets;
        }
        
        public void run() {
            try{
                this.antBox.run(this.targets);
            } catch (final Throwable e) {
                this.throwable = e;
                this.finishedAbnormally = true;
            }
            this.finished = true;
        }

        public boolean isFinished() {
            return this.finished;
        }
        
        public Throwable getErrorThrowable() {
            return this.throwable;
        }
        
        public boolean isFinishedAbnormally() {
            return this.finishedAbnormally;
        }
    }
}
