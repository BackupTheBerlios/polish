package de.enough.mepose.core;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import de.enough.mepose.core.model.MeposeModelManager;
import de.enough.mepose.core.model.MiDletChangeListener;
import de.enough.mepose.core.project.ProjectPersistence;
import de.enough.utils.log4j.Log4JPlugin;


public class MeposePlugin extends Plugin {
	
    public static Logger logger = Logger.getLogger(MeposePlugin.class);
    
    private MeposeModelManager meposeModelManager;
	private static MeposePlugin plugin;
	private ResourceBundle resourceBundle;
    
//    private MeposeModel meposeModel;
    private BundleContext bundleContext;

    private MiDletChangeListener miDletChangeListener;

	public MeposePlugin() {
		super();
        System.out.println("DEBUG:MeposePlugin.MeposePlugin(...):enter.");
		plugin = this;
        Log4JPlugin.init();
	}

	public void start(BundleContext context) throws Exception {
        this.bundleContext = context;
		super.start(context);
        createModelMananger();
        initModelManager();
        
	}
    

	public void stop(BundleContext context) throws Exception {
        System.out.println("DEBUG:MeposePlugin.stop(...):enter.");
		super.stop(context);
		plugin = null;
		this.resourceBundle = null;
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.miDletChangeListener);
	}

	
	public static MeposePlugin getDefault() {
		return plugin;
	}

	
	public static String getResourceString(String key) {
		ResourceBundle bundle = MeposePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
    
	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
				this.resourceBundle = ResourceBundle.getBundle("de.enough.mepose.core.CorePluginResources");
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
            
		}
		return this.resourceBundle;
	}

    
    
    // ####################################################################
    // Logging.
    

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
        if (status.getException() != null) {
            status.getException().printStackTrace(System.err);
        }
    }

    public static void log(String message, Throwable e) {
        IStatus status = new Status(IStatus.ERROR, MeposeConstants.ID_PLUGIN, IStatus.ERROR,
            message, e); 
        log(status);
    }

    public static void log(Throwable e) {
        IStatus status = new Status(IStatus.ERROR, MeposeConstants.ID_PLUGIN,IStatus.ERROR,"",e); 
        log(status);
    }
    
    public static void log(String message) {
        IStatus status = new Status(IStatus.ERROR, MeposeConstants.ID_PLUGIN, IStatus.ERROR,
                                    message, null); 
        log(status);
    }
    
    public static void log(String message,int severity) {
        IStatus status = new Status(severity, MeposeConstants.ID_PLUGIN, IStatus.ERROR,
            message, null); 
        log(status);
    }

    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public MeposeModelManager getMeposeModelManager() {
        return this.meposeModelManager;
    }
    
    // ###################################################################
    // Private methods.
    
    private void initModelManager() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        ProjectPersistence persistence = new ProjectPersistence();
        for (int i = 0; i < projects.length; i++) {
            IProject project = projects[i];
            if(project.isOpen() && isMeposeProject(project)) {
                Map map = null;
                try {
                    map = persistence.getMapFromProject(project);
                } catch (CoreException exception) {
                    log("Could not extract properties from project.project:"+project+".exception:"+exception);
                    continue;
                }
                getMeposeModelManager().addModel(project,map);
            }
        }
        this.miDletChangeListener = new MiDletChangeListener();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.miDletChangeListener);
    }

    
    private boolean isMeposeProject(IProject project) {
        try {
            return project.getNature(MeposeConstants.ID_NATURE) != null;
        } catch (CoreException exception) {
            log("could not determine if mepose nature is set.",exception);
            return false;
        }
    }

    private void createModelMananger() {
        this.meposeModelManager = new MeposeModelManager();
    }
}
