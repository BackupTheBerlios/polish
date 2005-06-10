package de.enough.mepose.core;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class CorePlugin extends AbstractUIPlugin {
	
    public static final String ID = "de.enough.mepose.core.CorePlugin";
    
	private static CorePlugin plugin;
	private ResourceBundle resourceBundle;
    
    private MeposeProject meposeProject;
    
	/**
	 * The constructor.
	 */
	public CorePlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
        this.meposeProject = new MeposeProject();
	}
    

	public void stop(BundleContext context) throws Exception {
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

    public MeposeProject getMeposeProject() {
        return this.meposeProject;
    }

    public void setMeposeProject(MeposeProject meposeProject) {
        if(meposeProject == null){
            throw new IllegalArgumentException("ERROR:CorePlugin.setMeposeProject(...):Parameter 'meposeProject' is null.");
        }
        this.meposeProject = meposeProject;
    }
    
    // ###################################################################
    // Getter and Setter
    
    
}
