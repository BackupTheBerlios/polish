package de.enough.mepose.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.ListeningConnector;

import de.enough.mepose.core.CorePlugin;

public class MIDletListeningDebugRunner
	extends MIDletRunner
{
	class ConnectRunnable implements Runnable
	{
		private VirtualMachine fVirtualMachine = null;
		private ListeningConnector fConnector = null;
		private Map fConnectionMap = null;
		private Exception fException = null;
		
		/**
		 * Constructs a runnable to connect to a VM via the given connector
		 * with the given connection arguments.
		 * 
		 * @param connector
		 * @param map
		 */
		public ConnectRunnable(ListeningConnector connector, Map map)
		{
			fConnector = connector;
			fConnectionMap = map;
		}
		
		public void run()
		{
			try
			{
				fVirtualMachine = fConnector.accept(fConnectionMap);
			}
			catch (IOException e)
			{
				fException = e;
			}
			catch (IllegalConnectorArgumentsException e)
			{
				fException = e;
			}
		}
		
		/**
		 * Returns the VM that was attached to, or <code>null</code> if none.
		 * 
		 * @return the VM that was attached to, or <code>null</code> if none
		 */
		public VirtualMachine getVirtualMachine()
		{
			return fVirtualMachine;
		}
		
		/**
		 * Returns any exception that occurred while attaching, or <code>null</code>.
		 * 
		 * @return IOException or IllegalConnectorArgumentsException
		 */
		public Exception getException() {
			return fException;
		}
	}
	
	protected ListeningConnector getConnector()
	{
		List connectors = Bootstrap.virtualMachineManager().listeningConnectors();
		
		for (int i= 0; i < connectors.size(); i++)
		{
			ListeningConnector c = (ListeningConnector) connectors.get(i);
			
			if ("com.sun.jdi.SocketListen".equals(c.name())) //$NON-NLS-1$
			{
				return c;
			}
		}
		
		return null;
	}

	protected String renderDebugTarget(String classToRun, int port)
	{
		String format= LaunchingMessages.StandardVMRunner__0__at_localhost__1__1; //$NON-NLS-1$
		return MessageFormat.format(format, new String[] { classToRun, String.valueOf(port) });
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
//		arguments.add("-Xrunjdwp:transport=dt_socket,suspend=y,address=localhost:" + port);
		arguments.add("-Xrunjdwp:transport=dt_socket,server=n,address=" + port);

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
		
		ListeningConnector connector= getConnector();
		
		if (connector == null)
		{
			abort("Couldn't find an appropriate debug connector", null, IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE); //$NON-NLS-1$
		}
		
		Map map = connector.defaultArguments();
		addDebugArguments(map, port);
		
		Process p = null;
		
		try
		{
			try
			{
				connector.startListening(map);
				
				p = exec(cmdLine, workingDir);
				
				if (p == null)
				{
					return;
				}
				
				IProcess process = newProcess(launch, p, "a-process-label", getDefaultProcessMap());
				process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
				
				// Make the debugger connect for real.
				
				boolean retry= false;
				do
				{
					try
					{
						ConnectRunnable runnable = new ConnectRunnable(connector, map);
						Thread connectThread = new Thread(runnable, "Listening Connector"); //$NON-NLS-1$
                        connectThread.setDaemon(true);
						connectThread.start();
						
						while (connectThread.isAlive())
						{
							if (monitor.isCanceled())
							{
								connector.stopListening(map);
								p.destroy();
								return;
							}
							
							try
							{
								p.exitValue();
								// process has terminated - stop waiting for a connection
								try
								{
									connector.stopListening(map); 
								}
								catch (IOException e)
								{
									// expected
								}
								
//								checkErrorMessage(process);
							}
							catch (IllegalThreadStateException e)
							{
								// expected while process is alive
							}
							
							try 
							{
								Thread.sleep(100);
							}
							catch (InterruptedException e)
							{
								// ignored
							}
						}

						Exception ex = runnable.getException();
						
						if (ex instanceof IllegalConnectorArgumentsException)
						{
							throw (IllegalConnectorArgumentsException) ex;
						}
						
						if (ex instanceof InterruptedIOException)
						{
							throw (InterruptedIOException) ex;
						}
						
						if (ex instanceof IOException)
						{
							throw (IOException) ex;
						}
						
						VirtualMachine vm= runnable.getVirtualMachine();
						
						if (vm != null)
						{
							JDIDebugModel.newDebugTarget(launch, vm, renderDebugTarget(configuration.getClassToLaunch(), port), process, true, false, false);
						}
						
						return;
					}
					catch (InterruptedIOException e)
					{
//						checkErrorMessage(process);
						
						// timeout, consult status handler if there is one
						IStatus status = new Status(IStatus.ERROR, CorePlugin.ID, IJavaLaunchConfigurationConstants.ERR_VM_CONNECT_TIMEOUT, "", e); //$NON-NLS-1$
						IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
						
						retry= false;
						if (handler == null) {
							// if there is no handler, throw the exception
							throw new CoreException(status);
						} 
						Object result = handler.handleStatus(status, this);
						if (result instanceof Boolean) {
							retry = ((Boolean)result).booleanValue();
						}
					}
				} while (retry);
			}
			finally
			{
				connector.stopListening(map);
			}
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
		
		if (p != null)
		{
			p.destroy();
		}
	}
}
