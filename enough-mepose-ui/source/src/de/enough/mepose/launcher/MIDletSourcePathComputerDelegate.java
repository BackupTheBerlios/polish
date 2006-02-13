package de.enough.mepose.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;

public class MIDletSourcePathComputerDelegate
	implements ISourcePathComputerDelegate
{
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration,
													  IProgressMonitor monitor)
		throws CoreException
	{
		String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		
		if (projectName == null)
		{
			return new ISourceContainer[0];
		}
		
		IProject project = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
		IJavaProject javaProject = JavaCore.create(project);
		
		return new ISourceContainer[] {
				new JavaProjectSourceContainer(javaProject),
		};
	}
}
