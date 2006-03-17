package de.enough.mepose.launcher;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;

public class MIDletLaunchConfigurationDelegate
    extends AbstractJavaLaunchConfigurationDelegate
{
  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
    throws CoreException
  {
    boolean debugMode = "debug".equals(mode);
    
    String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
    String workspace = configuration.getAttribute(MIDletLauncherConstants.WORKSPACE, (String) null);
    String jadFile = configuration.getAttribute(MIDletLauncherConstants.JAD_FILE, (String) null);
    // TODO: Truncate trailing '/' if present.
    String wtkPath = configuration.getAttribute(MIDletLauncherConstants.WTK_HOME,(String) null);
    
    Map attrMap = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);
    String hostName = (String) attrMap.get("hostname");
    int port = Integer.parseInt((String) attrMap.get("port")); 
    
    String[] commandLine;

    if (debugMode)
      {
        commandLine= new String[] {
            wtkPath + "/bin/emulator",
            "-Xdebug",
            "-Xdescriptor:" + jadFile,
            "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + port
        };
      }
    else
      {
        commandLine= new String[] {
            wtkPath + "/bin/emulator",
            "-Xdescriptor:" + jadFile,
            
        };
      }

    File workingDir = new File(workspace + File.separator + projectName);

    Process process = DebugPlugin.exec(commandLine, workingDir);

    if (! debugMode)
      {
        return;
      }
    
    DebugPlugin.newProcess(launch, process, mode);
    
    // Give the emulator some time to start up and open the debugging port. 
    try
      {
        Thread.sleep(5000);
      }
    catch (InterruptedException e)
      {
        // Ignore exception here.
      }
    
    // ---------------------------------------------------------------------------------------------------------------

//    DeviceDatabase deviceDB = new DeviceDatabase(new File("/home/mkoch/J2ME-Polish"));
//    
//    Device device = deviceDB.getDevice("Generic/Midp2Cldc11");
//    Environment environment = null;
//
//    EmulatorSetting setting = null;
//    Locale locale = Locale.ENGLISH;
//    Project project = null;
//    BooleanEvaluator evaluator = null;
//    String wtkHome = "/home/mkoch/local/WTK2.2";
//    File[] sourceDirs = new File[] { new File("/home/mkoch/runtime-workspace/menu/src") };
//    ExtensionManager manager = null;
//    
//    Emulator emulator = Emulator.createEmulator(device, setting, environment, project, evaluator, wtkHome, sourceDirs, manager);
//    emulator.execute(device, locale, environment);
    
    // ---------------------------------------------------------------------------------------------------------------

    if (monitor == null)
      {
        monitor = new NullProgressMonitor();
      }

    monitor.beginTask(MessageFormat.format(MIDletLauncherMessages.AttachingTo, new String[] { configuration.getName() }), 3);

    // Check for cancellation.
    if (monitor.isCanceled())
      {
        return;
      }

    monitor.subTask(MIDletLauncherMessages.VerifyingLaunchAttributes);

    String connectorId = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR, (String) null);
    IVMConnector connector = null;
    
    if (connectorId == null)
      {
        connector = JavaRuntime.getDefaultVMConnector();
      }
    else
      {
        connector = JavaRuntime.getVMConnector(connectorId);
      }
    
    if (connector == null)
      {
        abort(MIDletLauncherMessages.ConnectorNotSpecified, null, IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
      }

    Map argMap = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);

    int connectTimeout = JavaRuntime.getPreferences().getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
    argMap.put("timeout", "" + connectTimeout);

    // Check for cancellation.
    if (monitor.isCanceled())
      {
        return;
      }

    monitor.worked(1);

    monitor.subTask(MIDletLauncherMessages.CreatingSourceLocator);
    
    // Set the default source locator if required.
    setDefaultSourceLocator(launch, configuration);
    monitor.worked(1);

    // Connect to remote VM.
    connector.connect(argMap, monitor, launch);

    // Check for cancellation.
    if (monitor.isCanceled())
      {
        IDebugTarget[] debugTargets = launch.getDebugTargets();
        for (int i = 0; i < debugTargets.length; i++)
          {
            IDebugTarget target = debugTargets[i];
            if (target.canDisconnect())
              {
                target.disconnect();
              }
          }
        return;
      }

    monitor.done();
    
    if (process != null)
      {
        process.destroy();
      }
    
  }
}
