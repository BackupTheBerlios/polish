package de.enough.mepose.core.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public interface IProjectPersistence
{
  @SuppressWarnings("unchecked")
  public void putMapInProject(Map map, IProject resource) throws CoreException;

  @SuppressWarnings("unchecked")
  public Map getMapFromProject(IProject project) throws CoreException;
}