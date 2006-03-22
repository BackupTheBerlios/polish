package de.enough.mepose.launcher;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourcePathComputer;

public class MIDletSourcePathComputer
  extends JavaSourcePathComputer
{
  public static final String ID = "anotherDebugger.sourcePathComputer1";
  
  public String getID()
  {
    return ID;
  }
  
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration,
													  IProgressMonitor monitor) throws CoreException{
    
        System.out.println("DEBUG:MIDletSourcePathComputer.computeSourceContainers(...):enter.");
    
        return super.computeSourceContainers(configuration,monitor);
        
//        ISourceContainer[] tmpContainers = super.computeSourceContainers(configuration, monitor);
//        ISourceContainer[] sourceContainers = new ISourceContainer[tmpContainers.length + 1];
//        String buildDirectory = "/home/mkoch/runtime-workspace/menu/build/test/Generic/Midp2Cldc11/en_US/classes/";
//        sourceContainers[0] = new DirectorySourceContainer(new File(buildDirectory), false);
//        System.arraycopy(tmpContainers, 0, sourceContainers, 1, tmpContainers.length);
//        return sourceContainers;
	}
}
