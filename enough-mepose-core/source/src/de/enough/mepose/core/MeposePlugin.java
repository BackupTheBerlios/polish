package de.enough.mepose.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.IAntClasspathEntry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.bundlefile.ZipBundleFile;
import org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader;
import org.osgi.framework.BundleContext;

import de.enough.mepose.core.model.MeposeModelManager;
import de.enough.mepose.core.model.MiDletChangeListener;
import de.enough.mepose.core.project.ProjectPersistence;
import de.enough.polish.util.PopulateUtil;


public class MeposePlugin extends Plugin {
	
//    public static Logger logger = Logger.getLogger(MeposePlugin.class);
    
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
//        Log4JPlugin.init();
        putToolsJarOnClasspath();
	}

    public static boolean isMacOS() {
        String osname= System.getProperty("os.name").toLowerCase(Locale.US); //$NON-NLS-1$
        return osname.indexOf("mac") != -1; //$NON-NLS-1$
   }
    
    private void putToolsJarOnClasspath() {
        
        
        
        // This is the 3.1. style of doing classpath injection.
//        EclipseClassLoader classLoader = (EclipseClassLoader)getClass().getClassLoader();
//        AntCorePreferences antPreferences = AntCorePlugin.getPlugin().getPreferences();
//
//        File toolsJarFile = new File(antPreferences.getToolsJarEntry().toString());
//        try {
//            BundleFile bundleFile = new BundleFile.ZipBundleFile(toolsJarFile,null);
//            Object toolsJarClasspathEntryObject = PopulateUtil.callMethod("createClassPathEntry",classLoader,new Class[] {BundleFile.class,ProtectionDomain.class},new Object[] {bundleFile,null});
//            Field classpathEntriesField = PopulateUtil.getField(classLoader,"classpathEntries");
//            classpathEntriesField.setAccessible(true);
//            Object classpathEntriesObject = classpathEntriesField.get(classLoader);
//            Object[] classpathEntriesArray = (Object[])classpathEntriesObject;
////            Object[] newClasspathEntriesArray = new Object[classpathEntriesArray.length+1];
//            Object[] newClasspathEntriesArray = (Object[])Array.newInstance(toolsJarClasspathEntryObject.getClass(),classpathEntriesArray.length+1);
//            System.arraycopy(classpathEntriesArray,0,newClasspathEntriesArray,0,classpathEntriesArray.length);
//            newClasspathEntriesArray[classpathEntriesArray.length] = toolsJarClasspathEntryObject;
//            classpathEntriesField.set(classLoader,newClasspathEntriesArray);
//        } catch (NoSuchMethodException exception) {
//            log("Could not inject tools.jar (1)",exception);
//        } catch (IOException exception) {
//            log("Could not inject tools.jar (2)",exception);
//        } catch (NoSuchFieldException exception) {
//            log("Could not inject tools.jar (3)",exception);
//        } catch (IllegalArgumentException exception) {
//            log("Could not inject tools.jar (4)",exception);
//        } catch (IllegalAccessException exception) {
//            log("Could not inject tools.jar (5)",exception);
//        }
        DefaultClassLoader classLoader = (DefaultClassLoader)getClass().getClassLoader();

        AntCorePreferences antPreferences = AntCorePlugin.getPlugin().getPreferences();
        IAntClasspathEntry toolsJarEntry = antPreferences.getToolsJarEntry();
        
        // On MacOS X there is no tools.jar but javac is always on the classpath. See AntPreferencePage for details.
        if(toolsJarEntry == null) {
            return;
        }
        File toolsJarFile = new File(toolsJarEntry.toString());
        try {
            
            BundleFile bundleFile = new ZipBundleFile(toolsJarFile,null);
            Object toolsJarClasspathEntryObject = PopulateUtil.callMethod("createClassPathEntry",classLoader,new Class[] {BundleFile.class,ProtectionDomain.class},new Object[] {bundleFile,null});
            
            Field classpathManagerField = PopulateUtil.getField(classLoader,"manager");
            Object classpathManagerObject = classpathManagerField.get(classLoader);
            
            Field classpathEntriesField = PopulateUtil.getField(classpathManagerObject,"entries");
            classpathEntriesField.setAccessible(true);
            Object classpathEntriesObject = classpathEntriesField.get(classpathManagerObject);
            Object[] classpathEntriesArray = (Object[])classpathEntriesObject;
//          Object[] newClasspathEntriesArray = new Object[classpathEntriesArray.length+1];
            Object[] newClasspathEntriesArray = (Object[])Array.newInstance(toolsJarClasspathEntryObject.getClass(),classpathEntriesArray.length+1);
            System.arraycopy(classpathEntriesArray,0,newClasspathEntriesArray,0,classpathEntriesArray.length);
            newClasspathEntriesArray[classpathEntriesArray.length] = toolsJarClasspathEntryObject;
            classpathEntriesField.set(classpathManagerObject,newClasspathEntriesArray);
        } catch (NoSuchFieldException exception) {
            log("Could not inject tools.jar into bundle classLoader. (1)",exception);
        } catch (IllegalArgumentException exception) {
            log("Could not inject tools.jar into bundle classLoader. (2)",exception);
        } catch (IllegalAccessException exception) {
            log("Could not inject tools.jar into bundle classLoader. (3)",exception);
        } catch (IOException exception) {
            log("Could not inject tools.jar into bundle classLoader. (4)",exception);
        } catch (NoSuchMethodException exception) {
            log("Could not inject tools.jar into bundle classLoader. (5)",exception);
        }
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
        // TODO: This registration should be done somewhere else.
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
