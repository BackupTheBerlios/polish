package de.enough.mepose.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.enough.mepose.core.MeposePlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class MeposeUIPlugin extends AbstractUIPlugin {
	private static MeposeUIPlugin plugin;
	private ResourceBundle resourceBundle;
    
    public static Logger logger = Logger.getLogger(MeposeUIPlugin.class);
	
    private class ProjectSelected implements ISelectionListener{
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            if(selection instanceof IStructuredSelection) {
                IStructuredSelection s = (IStructuredSelection)selection;
                Object o = s.getFirstElement();
                if(o instanceof IJavaProject) {
                    IJavaProject javaProject = (IJavaProject)o;
                    setCurrentProject(javaProject.getProject());
                }
            }
        }
        protected void setCurrentProject(IProject project) {
            MeposePlugin.getDefault().getMeposeModelManager().setCurrentProject(project);
         }
    }    
    
	public MeposeUIPlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
        
        registerProjectListener();
//        MeposePlugin.getDefault().getMeposeModelManager().addBuildListener(new BuildListenerConsoleWriter());
	}

    
    
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		this.resourceBundle = null;
	}

	public static MeposeUIPlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = MeposeUIPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
                this.resourceBundle = ResourceBundle.getBundle("de.enough.mepose.ui.UIPluginResources");
		} catch (MissingResourceException x) {
            this.resourceBundle = null;
		}
		return this.resourceBundle;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(MeposeUIConstants.ID_PLUGIN, path);
	}

    public ImageDescriptor getImage(String imageKey) {
        if(imageKey == null){
            logger.error("Parameter 'imageKey' is null contrary to API.");
            return null;
        }
        return getImageRegistry().getDescriptor(imageKey);
    }
    
    
    // ###################################################################
    // Logging.
    
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
        if (status.getException() != null) {
            status.getException().printStackTrace(System.err);
        }
    }

    public static void log(String message, Throwable e) {
        IStatus status = new Status(IStatus.ERROR, MeposeUIConstants.ID_PLUGIN, IStatus.ERROR,
            message, e); 
        log(status);
    }

    public static void log(Throwable e) {
        IStatus status = new Status(IStatus.ERROR, MeposeUIConstants.ID_PLUGIN, IStatus.ERROR,"",e); 
        log(status);
    }
    
    public static void log(String message) {
        IStatus status = new Status(IStatus.ERROR, MeposeUIConstants.ID_PLUGIN, IStatus.ERROR,
                                    message, null); 
        log(status);
    }
    
    public static void log(String message,int severity) {
        IStatus status = new Status(severity, MeposeUIConstants.ID_PLUGIN, IStatus.ERROR,
            message, null); 
        log(status);
    }
    

    // ###################################################################
    // Protected methods.
    
        protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(MeposeUIConstants.KEY_IMAGE_LOGO,getImageDescriptor("/icons/polish_logo_16x16.png"));
        reg.put(MeposeUIConstants.KEY_IMAGE_OK,getImageDescriptor("/icons/tsk_green.png"));
        reg.put(MeposeUIConstants.KEY_IMAGE_WARNING,getImageDescriptor("/icons/tsk_yellow.png"));
        reg.put(MeposeUIConstants.KEY_IMAGE_ERROR,getImageDescriptor("/icons/tsk_red.png"));
    }
    
    
    // ###################################################################
    // Private methods.
    
    private void registerProjectListener() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                IWorkbench wb = PlatformUI.getWorkbench();
                IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
                ISelectionService ss = wbw.getSelectionService();
                ProjectSelected ps = new ProjectSelected();
                ss.addSelectionListener(ps);
            }
        });
    }
    
    /**
     * Returns the active workbench window
     * 
     * @return the active workbench window
     */
    // Copied from JDIDebugUIPlugin.
    public static IWorkbenchWindow getActiveWorkbenchWindow()
    {
      return getDefault().getWorkbench().getActiveWorkbenchWindow();
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
