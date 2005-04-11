package de.enough.polish.plugin.eclipse.polishEditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;


import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class PolishEditorPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static PolishEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	public static final String ID = "de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin";
	public static final String POLISH_NATURE_ID = "de.enough.polish.plugin.eclipse.polish.nature";
	
	
	
	/**
	 * The constructor.
	 */
	public PolishEditorPlugin() {
		super();
		plugin = this;
		try {
			this.resourceBundle = ResourceBundle.getBundle("de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPluginResources");
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		//this.polishPartitioner = new PolishPartitioner();
	}

	/**
	 * This method is called upon plug-in activation
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return the plugin class
	 */
	public static PolishEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key
	 * @return the resource string.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PolishEditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * @return the ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourceBundle;
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

    public static void log(String message) {
        IStatus status = new Status(IStatus.ERROR, ID, IStatus.ERROR,
            message, null); 
        log(status);
    }
    
     
   
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        
//      TODO: Search for suitable color defaults.
        String javaKeywordColorString = JavaPlugin.getDefault().getCombinedPreferenceStore().getString(IJavaColorConstants.JAVA_KEYWORD);

//      TODO: Only set values when they are unset. So we do not overwrite previous choices.
        store.putValue(IPolishConstants.POLISH_COLOR_DEFAULT,"0,200,0");
        store.putValue(IPolishConstants.POLISH_COLOR_DIRECTIVE,javaKeywordColorString);
        store.putValue(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,"0,200,0");
        store.putValue(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION,"0,200,0");

    }

    /**
     * @param project
     * @throws CoreException
     */
    public static void addPolishNatureToProject(IProject project) throws CoreException {
        
        if(project.hasNature(POLISH_NATURE_ID)){
            System.out.println("ERROR:PolishEditorPlugin.addPolishNatureToProject(...):Project has Polish nature already.");
            return;
        }
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        String[] newIds = new String[ids.length+1];
        System.arraycopy(ids,0,newIds,0,ids.length);
        newIds[ids.length] = POLISH_NATURE_ID;
        description.setNatureIds(newIds);
        project.setDescription(description,null);
    }
}
