package de.enough.mepose.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class MIDletAttachingDebugRunner
	extends MIDletRunner
{
	protected AttachingConnector getConnector()
	{
		List connectors = Bootstrap.virtualMachineManager().attachingConnectors();
            
		for (int i = 0; i < connectors.size(); i++)
		{
            AttachingConnector c = (AttachingConnector) connectors.get(i);
        
            if ("com.sun.jdi.SocketAttach".equals(c.name()))
            {
            	return c;
            }
		}
    
		return null;
	}

	public void run(VMRunnerConfiguration configuration, ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		int port= SocketUtil.findFreePort();
		
		if (port == -1)
		{
			abort("No free socket available", null, IJavaLaunchConfigurationConstants.ERR_NO_SOCKET_AVAILABLE); //$NON-NLS-1$
		}
		
		ArrayList arguments = new ArrayList();
		
//		arguments.add("/usr/lib/j2sdk1.4-sun/bin/java");
		arguments.add("/home/mkoch/local/WTK2.2/bin/emulator");
		arguments.add("-Xdescriptor:/home/mkoch/runtime-EclipseApplication/debuggerTestMIDlet/dist/Lithium-0.5.15_Sun-WTK22_CLDC1.1-en.jad");

//		String[] classpath = configuration.getClassPath();
//		if (classpath.length > 0)
//		{
//			arguments.add("-classpath");
//			arguments.add(convertClassPath(classpath));
//		}
		
		arguments.add("-Xverbose:class");
		arguments.add("-Xdomain:trusted");
		arguments.add("-Xheapsize:30M");
		arguments.add("-Xdebug");
//		arguments.add("-Xnoagent");
//		arguments.add("-Xrunjdwp:transport=dt_socket,server=y,address=localhost:" + port);
		arguments.add("-Xrunjdwp:address=" + port);

//		arguments.add(configuration.getClassToLaunch());
		arguments.add("com.navigon.lithium.LithiumMIDlet");

		// TODO: For debugging purposes only.
		for (int i = 0; i < arguments.size(); i++)
		{
			System.out.println("cmdLine: " + arguments.get(i));
		}
		
		String[] cmdLine = new String[arguments.size()];
		arguments.toArray(cmdLine);
		
		File workingDir = new File("/home/mkoch/runtime-EclipseApplication/debuggerTest");
		
		AttachingConnector connector = getConnector();
		
		if (connector == null)
		{
			abort("Couldn't find an appropriate debug connector", null, IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE); //$NON-NLS-1$
		}
		
		Map map = connector.defaultArguments();
		addDebugArguments(map, port);
		
		Process p = null;
		
		try
		{
			p = exec(cmdLine, workingDir);
				
			if (p == null)
			{
				return;
			}
			
			IProcess process = DebugPlugin.newProcess(launch, p, "a-process-label", getDefaultProcessMap());
            process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
            
            // TODO: Rewrite comment.
            // If the emulator debugger acts as a remote server,
            // delay a bit to give the emulator a chance to start
            // up.
            try
            { 
            	// TODO: Don't hardcode the time to sleep here.
            	Thread.sleep(5000); 
            }
            catch (InterruptedException e)
            {
            	// Ignored.
            }
            
            System.out.println("Attaching connector connecting to port " + port);
            
            VirtualMachine vm = connector.attach(map);

//            try
//            { 
//            	// TODO: Don't hardcode the time to sleep here.
//            	Thread.sleep(5000); 
//            }
//            catch (InterruptedException e)
//            {
//            	// Ignored.
//            }

            JDIDebugModel.newDebugTarget(launch, vm, "Lithium at localhost " + port, process, true, false, false);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalConnectorArgumentsException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if (p != null)
//		{
//			p.destroy();
//		}
	}
}
