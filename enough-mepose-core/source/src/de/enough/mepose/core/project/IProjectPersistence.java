package de.enough.mepose.core.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public interface IProjectPersistence
{
  public void putMapInProject(Map map, IProject resource) throws CoreException;

  public Map getMapFromProject(IProject project) throws CoreException;
}