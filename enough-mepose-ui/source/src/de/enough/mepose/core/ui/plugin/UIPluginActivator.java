package de.enough.mepose.core.ui.plugin;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;

import de.enough.mepose.MeposeCoreUIConstants;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class UIPluginActivator extends AbstractUIPlugin {
	//The shared instance.
	private static UIPluginActivator plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
    private static final String ID = "de.enough.mepose.core.ui.plugin";
    public static Logger logger = Logger.getLogger(UIPluginActivator.class);
	
	/**
	 * The constructor.
	 */
	public UIPluginActivator() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static UIPluginActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = UIPluginActivator.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("de.enough.mepose.ui.UIPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("de.enough.mepose.core.ui.plugin", path);
	}

    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(MeposeCoreUIConstants.KEY_IMAGE_LOGO,getImageDescriptor("/icons/polish_logo_16x16.png"));
        reg.put(MeposeCoreUIConstants.KEY_IMAGE_OK,getImageDescriptor("/icons/tsk_green.png"));
        reg.put(MeposeCoreUIConstants.KEY_IMAGE_WARNING,getImageDescriptor("/icons/tsk_yellow.png"));
        reg.put(MeposeCoreUIConstants.KEY_IMAGE_ERROR,getImageDescriptor("/icons/tsk_red.png"));
    }

    /**
     * @param imageKey
     * @return an imageDescriptor or null when imageKey is invalid or null.
     */
    public ImageDescriptor getImage(String imageKey) {
        if(imageKey == null){
            logger.error("Parameter 'imageKey' is null contrary to API.");
            return null;
        }
        return getImageRegistry().getDescriptor(imageKey);
    }
    
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
    
}
