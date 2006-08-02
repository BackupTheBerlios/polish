package de.enough.mepose.launcher;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.ui.MeposeUIPlugin;
import de.enough.polish.Device;
import de.enough.utils.AntBox;

public class MIDletLaunchConfigurationDelegate extends
        AbstractJavaLaunchConfigurationDelegate {

    private static final String CAN_NOT_BUILD_PROJECT = "Can not build project";
//    private static final int EMULATOR_STARTUP_TIME = 5000;

    public void launch(ILaunchConfiguration configuration, String mode,
                       ILaunch launch, final IProgressMonitor monitor)
                                                                throws CoreException {

        String projectName;
        projectName = configuration
                .getAttribute(MeposeConstants.ID_PROJECT_NAME, "");

        IProject project = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(projectName);

        if (project == null) {
            showErrorBox(CAN_NOT_BUILD_PROJECT,"The project '" + projectName + "' does not exists.");
            return;
        }

        MeposeModel model = MeposePlugin.getDefault().getMeposeModelManager().getModel(project);
        if(model == null) {
            showErrorBox(CAN_NOT_BUILD_PROJECT,"No model found for the project. Maybe the project does not have a Mepose nature.");
            return;
        }
        
        File buildxml = model.getBuildxml();
        if(buildxml == null) {
            showErrorBox(CAN_NOT_BUILD_PROJECT,"Internal error: 1");
            MeposeUIPlugin.log("No buildxml specified in model for project:"+projectName);
            return;
        }
        
//        DefaultLogger log = new DefaultLogger();
//        log.setErrorPrintStream(System.err);
//        log.setOutputPrintStream(System.out);
//        model.getAntBox().getProject().addBuildListener(log);
        
        
        setupConsole();
        
        AntBox antBox = model.getAntBox();
        
        Device currentDevice = model.getCurrentDevice();
        if(currentDevice == null) {
            showErrorBox(CAN_NOT_BUILD_PROJECT,"No device selected for build");
            return;
        }
        antBox.setProperty("device",currentDevice.getName());
        String[] targets = new String[] {"clean","test","j2mepolish"};
        antBox.run(targets);
        
//        AntRunner antRunner = new AntRunner();
//        String buildxmlAbsolutePath = buildxml.getAbsolutePath();
//        antRunner.setBuildFileLocation(buildxmlAbsolutePath);
//        antRunner.addBuildListener("de.enough.mepose.ui.BuildListenerConsoleWriter");
//        antRunner.run();
        
        
        // boolean debugMode = "debug".equals(mode);
        //        
        // String projectName =
        // configuration.getAttribute(MeposeConstants.ID_PROJECT_NAME, "");
        // String workspace =
        // configuration.getAttribute(MIDletLauncherConstants.WORKSPACE, "");
        // String jadFile =
        // configuration.getAttribute(MIDletLauncherConstants.JAD_FILE, "");
        // // TODO: Truncate trailing '/' if present.
        // String wtkPath =
        // configuration.getAttribute(MeposeConstants.ID_WTK_HOME,"/");
        //        
        // Map attrMap =
        // configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
        // (Map) null);
        // String hostName = (String) attrMap.get("hostname");
        // int port = Integer.parseInt((String) attrMap.get("port"));
        // IProject project =
        // (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
        //        
        // MeposeModel model =
        // MeposePlugin.getDefault().getMeposeModelManager().getModel(project);
        // model.build(null);
        // String[] commandLine;
        //    
        // if (debugMode){
        // commandLine= new String[] {
        // wtkPath + "/bin/emulator",
        // "-Xdebug",
        // "-Xdescriptor:" + jadFile,
        // "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" +
        // hostName + ":" + port};
        // }
        // else
        // {
        // commandLine= new String[] {
        // wtkPath + "/bin/emulator",
        // "-Xdescriptor:" + jadFile,
        // };
        // }
        //    
        // File workingDir = new File(workspace + File.separator + projectName);
        // Process process = DebugPlugin.exec(commandLine, workingDir);
        //    
        // if (! debugMode)
        // {
        // return;
        // }
        //        
        // DebugPlugin.newProcess(launch, process, mode);
        //        
        // // Give the emulator some time to start up and open the debugging
        // port.
        // try
        // {
        // Thread.sleep(EMULATOR_STARTUP_TIME);
        // }
        // catch (InterruptedException e)
        // {
        // // Ignore exception here.
        // }
        //    
        // //
        // ---------------------------------------------------------------------------------------------------------------
        //    
        // // DeviceDatabase deviceDB = new DeviceDatabase(new
        // File("/home/mkoch/J2ME-Polish"));
        // //
        // // Device device = deviceDB.getDevice("Generic/Midp2Cldc11");
        // // Environment environment = null;
        // //
        // // EmulatorSetting setting = null;
        // // Locale locale = Locale.ENGLISH;
        // // Project project = null;
        // // BooleanEvaluator evaluator = null;
        // // String wtkHome = "/home/mkoch/local/WTK2.2";
        // // File[] sourceDirs = new File[] { new
        // File("/home/mkoch/runtime-workspace/menu/src") };
        // // ExtensionManager manager = null;
        // //
        // // Emulator emulator = Emulator.createEmulator(device, setting,
        // environment, project, evaluator, wtkHome, sourceDirs, manager);
        // // emulator.execute(device, locale, environment);
        //        
        // //
        // ---------------------------------------------------------------------------------------------------------------
        //    
        // if (monitor == null)
        // {
        // monitor = new NullProgressMonitor();
        // }
        //    
        // monitor.beginTask(MessageFormat.format(MIDletLauncherMessages.AttachingTo,
        // new String[] { configuration.getName() }), 3);
        //    
        // // Check for cancellation.
        // if (monitor.isCanceled())
        // {
        // return;
        // }
        //    
        // monitor.subTask(MIDletLauncherMessages.VerifyingLaunchAttributes);
        //    
        // String connectorId =
        // configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
        // (String) null);
        // IVMConnector connector = null;
        //        
        // if (connectorId == null)
        // {
        // connector = JavaRuntime.getDefaultVMConnector();
        // }
        // else
        // {
        // connector = JavaRuntime.getVMConnector(connectorId);
        // }
        //        
        // if (connector == null)
        // {
        // abort(MIDletLauncherMessages.ConnectorNotSpecified, null,
        // IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
        // }
        //    
        // Map argMap =
        // configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
        // (Map) null);
        //    
        // int connectTimeout =
        // JavaRuntime.getPreferences().getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
        // argMap.put("timeout", "" + connectTimeout);
        //    
        // // Check for cancellation.
        // if (monitor.isCanceled())
        // {
        // return;
        // }
        //    
        // monitor.worked(1);
        //    
        // monitor.subTask(MIDletLauncherMessages.CreatingSourceLocator);
        //        
        // // Set the default source locator if required.
        // setDefaultSourceLocator(launch, configuration);
        // monitor.worked(1);
        //    
        // // Connect to remote VM.
        // connector.connect(argMap, monitor, launch);
        //    
        // // Check for cancellation.
        // if (monitor.isCanceled())
        // {
        // IDebugTarget[] debugTargets = launch.getDebugTargets();
        // for (int i = 0; i < debugTargets.length; i++)
        // {
        // IDebugTarget target = debugTargets[i];
        // if (target.canDisconnect())
        // {
        // target.disconnect();
        // }
        // }
        // return;
        // }
        //    
        // monitor.done();
        //        
        // //TODO: Way terminate the process right away?
        // // if (process != null)
        // // {
        // //
        // System.out.println("DEBUG:MIDletLaunchConfigurationDelegate.launch(...):destroying
        // process.");
        // // process.destroy();
        // // }
        //        
    }

    /**
     * 
     */
    private void setupConsole() {
        IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles = consoleManager.getConsoles();
        for (int i = 0; i < consoles.length; i++) {
            IConsole console = consoles[i];
            if(MeposeUIPlugin.CONSOLE_NAME.equals(console.getName())){
                consoleManager.showConsoleView(console);
                ((MessageConsole)console).clearConsole();
                break;
            }
        }
    }

    private void showErrorBox(final String title,final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Shell activeShell = Display.getDefault().getActiveShell();
                MessageDialog.openError(activeShell, title,message);
            }
        });
    }
}
