package de.enough.polish.plugin.eclipse.css;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class CssEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CssEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private BundleContext bundleContext;
	
	/**
	 * The constructor.
	 */
	public CssEditorPlugin() {
		super();
		plugin = this;
		try {
			this.resourceBundle = ResourceBundle.getBundle("CssEditor.CssEditorPluginResources");
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation.
	 * @param context
	 * @exception Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.bundleContext = context;
	}

	/**
	 * This method is called when the plug-in is stopped.
	 * @param context
	 * @exception Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return CssEditorPlugin
	 */
	public static CssEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key
	 * @return String
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CssEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle.
	 * @return ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourceBundle;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		URL iconDirectoryURL = this.bundleContext.getBundle().getEntry("/icons/");
		
		String[] iconNames = {"sample.gif","book.gif"};
		ImageDescriptor imageDescriptor = null;
		for(int i = 0; i < iconNames.length; i++){
			imageDescriptor = null;
			try {
				URL url = new URL(iconDirectoryURL, iconNames[i]);
				imageDescriptor = ImageDescriptor.createFromURL(url);
			} catch (MalformedURLException e) {
				// should not happen
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			reg.put(iconNames[i],imageDescriptor);
		}
		
	}
}
