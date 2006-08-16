package de.enough.mepose.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;

public class MIDletSourcePathComputerDelegate extends JavaSourcePathComputer{
    
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration,
													  IProgressMonitor monitor)
		throws CoreException{
        
        System.out.println("DEBUG:MIDletSourcePathComputerDelegate.computeSourceContainers(...):enter.");
//		String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
//		
//		if (projectName == null)
//		{
//			return new ISourceContainer[0];
//		}
//		
//		IProject project = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
//		IJavaProject javaProject = JavaCore.create(project);
//		
//		return new ISourceContainer[] {
//				new JavaProjectSourceContainer(javaProject),
//		};
        return super.computeSourceContainers(configuration,monitor);
	}
}
