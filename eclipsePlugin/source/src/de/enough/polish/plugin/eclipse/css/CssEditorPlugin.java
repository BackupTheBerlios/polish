package de.enough.polish.plugin.eclipse.css;

import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class CssEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CssEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
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
}
