package de.enough.mepose.launcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MIDletLauncherPlugin
    extends AbstractUIPlugin
{
  // The shared instance.
  private static MIDletLauncherPlugin plugin;

  /**
   * The constructor.
   */
  public MIDletLauncherPlugin()
  {
    plugin = this;
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
  }

  /**
   * This method is called when the plug-in is stopped
   */
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static MIDletLauncherPlugin getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path.
   * 
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return AbstractUIPlugin.imageDescriptorFromPlugin("anotherDebugger", path);
  }

  /**
   * Returns the active workbench window
   * 
   * @return the active workbench window
   */
  // Copied from JDIDebugUIPlugin.
  public static IWorkbenchWindow getActiveWorkbenchWindow()
  {
    MIDletLauncherPlugin default1 = getDefault();
    IWorkbench workbench = default1.getWorkbench();
    IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
    return activeWorkbenchWindow;
  } 
  
  // Copied from JDIDebugUIPlugin.
  public static IWorkbenchPage getActivePage()
  {
    IWorkbenchWindow w = getActiveWorkbenchWindow();
  
    if (w != null)
      {
        return w.getActivePage();
      }
    
    return null;
  }
}
