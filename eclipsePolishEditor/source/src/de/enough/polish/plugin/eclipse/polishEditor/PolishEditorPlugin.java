package de.enough.polish.plugin.eclipse.polishEditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.enough.polish.plugin.eclipse.polishEditor.editor.PolishEditor;

/**
 * The main plugin class to be used in the desktop.
 */
public class PolishEditorPlugin extends AbstractUIPlugin {
	
    
    /**
     * 
     * <br>Copyright Enough Software 2005
     * <pre>
     * history
     *        May 30, 2006 - rickyn creation
     * </pre>
     * @author Richard Nkrumah, Richard.Nkrumah@enough.de
     */
    public class PartFocusListener implements IPartListener {

        /*
         * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partActivated(IWorkbenchPart part) {
            if(part instanceof PolishEditor) {
                System.out.println("DEBUG:PartFocusListener.partActivated(...):setting the new model");
                PolishEditor polishEditor = (PolishEditor)part;
                polishEditor.getDeviceDropdownChooserContributionItem().setMeposeModel(polishEditor.getMeposeModel());
            }
        }

        /*
         * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
         */
        public void partBroughtToTop(IWorkbenchPart part) {
            // TODO rickyn implement partBroughtToTop

        }

        /*
         * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
         */
        public void partClosed(IWorkbenchPart part) {
            // TODO rickyn implement partClosed

        }

        /*
         * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
         */
        public void partDeactivated(IWorkbenchPart part) {
            // TODO rickyn implement partDeactivated

        }

        /*
         * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
         */
        public void partOpened(IWorkbenchPart part) {
            // TODO rickyn implement partOpened

        }
        
        protected void updateChooser() {
            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if(activeEditor instanceof PolishEditor) {
                PolishEditor polishEditor = (PolishEditor)activeEditor;
                polishEditor.getDeviceDropdownChooserContributionItem().setMeposeModel(polishEditor.getMeposeModel());
            }
        }

    }



    //The shared instance.
	private static PolishEditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	public static final String ID = "de.enough.polish.plugin.eclipse.polishEditor.polishEditorPlugin";
	public static final String POLISH_NATURE_LOCAL_ID = "polishNature";
	public static final String POLISH_NATURE_ID = ID + "." + POLISH_NATURE_LOCAL_ID;

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

    public static void log(Throwable e) {
        IStatus status = new Status(IStatus.ERROR, ID,IStatus.ERROR,"",e); 
        log(status);
    }
    public static void log(String message) {
        IStatus status = new Status(IStatus.ERROR, ID, IStatus.ERROR,
            message, null); 
        log(status);
    }
    
     
   
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        
        String javaKeywordColorString = JavaPlugin.getDefault().getCombinedPreferenceStore().getString(IJavaColorConstants.JAVA_KEYWORD);
        String javaDefaultColorString = JavaPlugin.getDefault().getCombinedPreferenceStore().getString(IJavaColorConstants.JAVA_DEFAULT);
        
        //store.setDefault(IPolishConstants.POLISH_COLOR_DIRECTIVE,javaKeywordColorString);
        store.setDefault(IPolishConstants.POLISH_COLOR_DIRECTIVE,"230,25,25");    
        store.setDefault(IPolishConstants.POLISH_COLOR_DEFAULT,javaDefaultColorString);
        store.setDefault(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,javaDefaultColorString);
        store.setDefault(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION,javaKeywordColorString);
    }
}
