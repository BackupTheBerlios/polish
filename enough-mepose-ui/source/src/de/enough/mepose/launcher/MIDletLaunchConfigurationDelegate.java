package de.enough.mepose.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public class MIDletLaunchConfigurationDelegate
	extends AbstractJavaLaunchConfigurationDelegate
{

	public void launch(ILaunchConfiguration configuration, String mode,
					   ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		// TODO: Implement me.
		
//		String mainTypeName = verifyMainTypeName(configuration);
//		String mainTypeName = "HelloWorld"; 
		String mainTypeName = "com.navigon.lithium.LithiumMIDlet";
		// TODO: Make sure the main type extends javax.microedition.MIDlet
	    IJavaProject javaProject = getJavaProject(configuration);
	    IType mainType = getMainType(mainTypeName, javaProject);

//	    IVMInstall vm = verifyVMInstall(configuration);
//	    IVMRunner runner = vm.getVMRunner(mode);
	    
	    IVMRunner runner = null;

	    if ("debug".equals(mode))
	    {
//	    	runner = new MIDletListeningDebugRunner();
	    	runner = new MIDletAttachingDebugRunner();
	    }
	    else
	    {
	    	runner = new MIDletRunner();
	    }
	    
	    VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName,
	    															getClasspath(configuration));
//	    String[] vmArgs = runConfig.getVMArguments();
//	    String[] realArgs = new String[vmArgs.length+1];
//	    System.arraycopy(vmArgs, 0, realArgs, 1, vmArgs.length);
//	    realArgs[0] = javaPolicy;
//	    runConfig.setVMArguments(realArgs);
//	    runConfig.setVMArguments(vmArgs);
	       
//	    runConfig.setWorkingDirectory(workingDirName);

	    // Bootpath
	    String[] bootpath = getBootpath(configuration);
	    runConfig.setBootClassPath(bootpath);

	    // TODO: Might not be needed accorind to javadocs.
	    setDefaultSourceLocator(launch, configuration);
	    
	    // Launch the configuration
//	    this.fCurrentLaunchConfiguration = configuration;
	    runner.run(runConfig, launch, monitor); 
	}
	
	private IType getMainType(String mainTypeName, IJavaProject javaProject)
	{
		try
		{
			String pathStr = mainTypeName.replace('.', '/') + ".java"; //$NON-NLS-1$
			IJavaElement javaElement = javaProject.findElement(new Path(pathStr));
			
			if (javaElement == null)
			{
				return null;
			}
			else if (javaElement instanceof IType)
			{
				return (IType) javaElement;
			}
			else if (javaElement.getElementType() == IJavaElement.COMPILATION_UNIT)
			{
				String simpleName = Signature.getSimpleName(mainTypeName);
				return ((ICompilationUnit) javaElement).getType(simpleName);
			}
			else if (javaElement.getElementType() == IJavaElement.CLASS_FILE)
			{
				return ((IClassFile) javaElement).getType();
			}
		}
		catch (JavaModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
