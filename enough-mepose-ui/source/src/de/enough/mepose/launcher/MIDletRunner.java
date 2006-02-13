package de.enough.mepose.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.AbstractVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.connect.Connector;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.model.MeposeModel;

public class MIDletRunner extends AbstractVMRunner
{
	protected String getPluginIdentifier()
	{
		return CorePlugin.ID;
	}
	
	protected final MeposeModel getMeposeModel()
	{
		return null;
	}

	protected static String renderCommandLine(String[] commandLine)
	{
		if (commandLine.length < 1)
			return ""; //$NON-NLS-1$
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters= commandLine[i].toCharArray();
			StringBuffer command= new StringBuffer();
			boolean containsSpace= false;
			for (int j = 0; j < characters.length; j++) {
				char character= characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command.toString());
				buf.append('\"');
			} else {
				buf.append(command.toString());
			}
		}	
		return buf.toString();
	}

	protected String convertClassPath(String[] cp)
	{
		int pathCount= 0;
		StringBuffer buf= new StringBuffer();
		if (cp.length == 0) {
			return "";    //$NON-NLS-1$
		}
		for (int i= 0; i < cp.length; i++) {
			if (pathCount > 0) {
				buf.append(File.pathSeparator);
			}
			buf.append(cp[i]);
			pathCount++;
		}
		return buf.toString();
	}
	
	public void run(VMRunnerConfiguration configuration, ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		ArrayList arguments = new ArrayList();

//		arguments.add("/usr/lib/j2sdk1.4-sun/bin/java");
		arguments.add("/home/mkoch/local/WTK2.2/bin/emulator");
		
//		String[] classpath = configuration.getClassPath();
//		if (classpath.length > 0)
//		{
//			arguments.add("-classpath");
//			arguments.add(convertClassPath(classpath));
//		}

//		arguments.add(configuration.getClassToLaunch());
		arguments.add("-Xdescriptor:/home/mkoch/runtime-EclipseApplication/debuggerTestMIDlet/dist/Lithium-0.5.15_Sun-WTK22_CLDC1.1-en.jad");

		arguments.add("com.navigon.lithium.LithiumMIDlet");

//		// TODO: For debugging purposes only.
//		for (int i = 0; i < arguments.size(); i++)
//		{
//			System.out.println("cmdLine: " + arguments.get(i));
//		}
		
		
		String[] cmdLine = new String[arguments.size()];
		arguments.toArray(cmdLine);
		
		File workingDir = new File("/home/mkoch/runtime-EclipseApplication/debuggerTest");
		
		Process p = exec(cmdLine, workingDir);

		if (p == null)
		{
			return;
		}
		
		IProcess process = newProcess(launch, p, "a-process-label", getDefaultProcessMap());
		process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
	}

	protected void addDebugArguments(Map map, int port)
	{
		Connector.IntegerArgument portArgument = (Connector.IntegerArgument) map.get("port");
		portArgument.setValue(port);
		
		Connector.IntegerArgument timeoutArg = (Connector.IntegerArgument) map.get("timeout");
		if (timeoutArg != null)
		{
			int timeout = JavaRuntime.getPreferences().getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
			timeoutArg.setValue(timeout);
		}
	}
}
