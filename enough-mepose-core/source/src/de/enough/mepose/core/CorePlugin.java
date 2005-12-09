package de.enough.mepose.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import de.enough.mepose.core.model.MeposeModel;


public class CorePlugin extends Plugin {
	
    public static final String ID = "de.enough.mepose.core.CorePlugin";
    
	private static CorePlugin plugin;
	private ResourceBundle resourceBundle;
    
    private MeposeModel meposeModel;
    
	public CorePlugin() {
		super();
        System.out.println("DEBUG:CorePlugin.CorePlugin(...):enter.");
		plugin = this;
        
        // TODO:Only for bootstrap testing.
        //createTestProject();
	}

//    public void createTestProject() {
//        String buildxml1_basedir = "/Users/ricky/workspace/enough-polish-demo";
//        String buildxml1_filename = "build.xml";
//        this.meposeModel.setProjectPath(buildxml1_basedir);
//        this.meposeModel.setBuildxml(new File(buildxml1_filename));
//        this.meposeModel.setEnvironmentToDevice(this.meposeModel.getConfiguredDevices()[0]);
//    }
    
	public void start(BundleContext context) throws Exception {
        System.out.println("DEBUG:CorePlugin.start(...):enter.");
        this.meposeModel = new MeposeModel();
		super.start(context);
        
	}
    

	public void stop(BundleContext context) throws Exception {
        System.out.println("DEBUG:CorePlugin.stop(...):enter.");
		super.stop(context);
        //new MeposeProject();
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
    

    // TODO: This can be enhanced with a MeposeProjectManager which can deal with several models keyed by name. 
    public MeposeModel getDefaultMeposeModel() {
        return this.meposeModel;
    }

    public void setDefaultMeposeModel(MeposeModel meposeModel) {
        if(meposeModel == null){
            throw new IllegalArgumentException("ERROR:CorePlugin.setMeposeProject(...):Parameter 'meposeModel' is null.");
        }
        this.meposeModel = meposeModel;
    }
    
}
