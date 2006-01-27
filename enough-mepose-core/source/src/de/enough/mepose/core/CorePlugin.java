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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.enough.mepose.core.model.MeposeModelManager;
import de.enough.mepose.core.project.ProjectPersistence;
import de.enough.utils.log4j.Log4JPlugin;


public class CorePlugin extends Plugin {
	
    public static final String ID = CorePlugin.class.getName();
    public static Logger logger = Logger.getLogger(CorePlugin.class);
    
//    public QualifiedName ID_MEPOSE_MODEL = new QualifiedName(CorePlugin.class.getName(),"id.meposemodel");
//    public QualifiedName ID_BUILD_XML = new QualifiedName(CorePlugin.class.getName(),"id.buildxml");
//    public QualifiedName ID_POLISH_HOME = new QualifiedName(CorePlugin.class.getName(),"id.polish.home");
//    public QualifiedName ID_WTK_HOME = new QualifiedName(CorePlugin.class.getName(),"id.wtk.home");
//    public QualifiedName ID_DEVICES_CONFIGURED = new QualifiedName(CorePlugin.class.getName(),"id.devices.configured");
//    public QualifiedName ID_PLATFORMS_CONFIGURED = new QualifiedName(CorePlugin.class.getName(),"id.platforms.configured");
    
    private MeposeModelManager meposeModelManager;
	private static CorePlugin plugin;
	private ResourceBundle resourceBundle;
    
//    private MeposeModel meposeModel;
    private BundleContext bundleContext;

	public CorePlugin() {
		super();
        System.out.println("DEBUG:CorePlugin.CorePlugin(...):enter.");
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
        System.out.println("DEBUG:CorePlugin.stop(...):enter.");
		super.stop(context);
		plugin = null;
		this.resourceBundle = null;
	}

	
	public static CorePlugin getDefault() {
		return plugin;
	}

	
	public static String getResourceString(String key) {
		ResourceBundle bundle = CorePlugin.getDefault().getResourceBundle();
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

//	/**
//	 * Returns an image descriptor for the image file at the given
//	 * plug-in relative path. It does <it>not</it> use the image registry.
//	 *
//	 * @param path the path
//	 * @return the image descriptor
//	 */
//	public static ImageDescriptor getImageDescriptor(String path) {
//		return AbstractUIPlugin.imageDescriptorFromPlugin("de.enough.mepose.core.CorePlugin", path);
//	}
    
    // ####################################################################
    // Logging.
    
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
        if (status.getException() != null) {
            status.getException().printStackTrace(System.err);
        }
    }

    public static void log(String message, Throwable e) {
        IStatus status = new Status(IStatus.ERROR, ID, IStatus.ERROR,
            message, e); 
        log(status);
    }

    public static void log(Throwable e) {
        IStatus status = new Status(IStatus.ERROR, ID,IStatus.ERROR,"",e); 
        log(status);
    }
    
    public static void log(String message) {
        IStatus status = new Status(IStatus.ERROR, ID, IStatus.ERROR,
                                    message, null); 
        log(status);
    }
    
    public static void log(String message,int severity) {
        IStatus status = new Status(severity, ID, IStatus.ERROR,
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
    }

    
    private boolean isMeposeProject(IProject project) {
        try {
            return project.getNature(MeposeCoreConstants.ID_NATURE) != null;
        } catch (CoreException exception) {
            log("could not determine if mepose nature is set.",exception);
            return false;
        }
    }

    private void createModelMananger() {
        this.meposeModelManager = new MeposeModelManager();
    }
    
}
