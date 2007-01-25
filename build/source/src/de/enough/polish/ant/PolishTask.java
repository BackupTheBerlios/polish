/*
 * Created on 21-Jan-2003 at 15:15:56.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Attribute;
import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.LicenseLoader;
import de.enough.polish.PolishProject;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.ant.build.ClassSetting;
import de.enough.polish.ant.build.CompilerTask;
import de.enough.polish.ant.build.FullScreenSetting;
import de.enough.polish.ant.build.JavaExtension;
import de.enough.polish.ant.build.LibrariesSetting;
import de.enough.polish.ant.build.LibrarySetting;
import de.enough.polish.ant.build.LocaleSetting;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.ant.build.LogSetting;
import de.enough.polish.ant.build.Midlet;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.ant.build.PostCompilerSetting;
import de.enough.polish.ant.build.PreCompilerSetting;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.ant.build.PreverifierSetting;
import de.enough.polish.ant.build.SourceSetting;
import de.enough.polish.ant.build.Variables;
import de.enough.polish.ant.buildlistener.BuildListenerExtensionSetting;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.descriptor.DescriptorCreator;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.emulator.Emulator;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.jar.DefaultPackager;
import de.enough.polish.jar.Packager;
import de.enough.polish.manifest.ManifestCreator;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.DebugManager;
import de.enough.polish.preprocess.ImportConverter;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.preprocess.css.CssConverter;
import de.enough.polish.preprocess.css.StyleSheet;
import de.enough.polish.preprocess.custom.TranslationPreprocessor;
import de.enough.polish.preverify.CldcPreverifier;
import de.enough.polish.preverify.Preverifier;
import de.enough.polish.resources.ResourceManager;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.PathClassLoader;
import de.enough.polish.util.ReflectionUtil;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;
import de.enough.polish.util.TextFile;
import de.enough.polish.util.TextFileManager;

/**
 * <p>Manages a J2ME project from the preprocessing to the packaging and obfuscation.</p>
 *
 * <p>Copyright Enough Software 2004, 2005, 2006</p>

 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishTask extends ConditionalTask {

  static
  {
    String tmp;
    
    try
    {
      ResourceBundle versions = ResourceBundle.getBundle("version");
      tmp = versions.getString("VersionName");
    }
    catch (MissingResourceException e)
    {
      tmp = "<Unknown CVS version>";
    }
    
    VERSION = tmp;
  }
  
	private static final String VERSION;

	private BuildSetting buildSetting;
	private InfoSetting infoSetting;
	private Requirements deviceRequirements;
	private List emulatorSettings;
	
	/** the project settings */ 
	private PolishProject polishProject;
	/** the manager of all devices */
	private DeviceManager deviceManager;
	/** the actual devices which are supported by this project */
	private Device[] devices;
	private Preprocessor preprocessor;
	//private File[] sourceDirs;
	private SourceSetting[] sourceSettings;
	private TextFile[][] sourceFiles;
	private String javacTarget;
	/** the source-compatibility-switch for the javac-compiler defaults to "1.3" */
	private String sourceCompatibility = "1.3";
	private Obfuscator[] obfuscators;
	private boolean doObfuscate;
	private String[] preserveClasses;
	private ImportConverter importConverter;
	private TextFile styleSheetSourceFile;
	private TextFile styleCacheSourceFile;
	private ResourceUtil resourceUtil;
	private String wtkHome;
	private HashMap midletClassesByName;
	private static final Pattern START_APP_PATTERN = 
		Pattern.compile("\\s*void\\s+startApp\\s*\\(\\s*\\)");
	private static final Pattern DESTROY_APP_PATTERN = 
		Pattern.compile("\\s*void\\s+destroyApp\\s*\\(\\s*(final)?\\s*boolean\\s+\\w+\\s*\\)");

	private LibraryManager libraryManager;
	private File errorLock;
	private boolean lastRunFailed;
	private StringList styleSheetCode;
	private int numberOfChangedFiles;
	private File polishSourceDir;
	private TextFile[] polishSourceFiles;
	private Variables variables;

	//private Variable[] conditionalVariables;

	private boolean binaryLibrariesUpdated;

	private JavaExtension[] javaExtensions;

	private CssAttributesManager cssAttributesManager;

	private PolishLogger polishLogger;
	private ArrayList runningEmulators;

	private ResourceManager resourceManager;

	private LocaleSetting[] supportedLocales;

	private TranslationPreprocessor translationPreprocessor;

	private TextFile localeSourceFile;

	private StringList localeCode;
	
	// move all classes into the default package
	// during the preprocessing phase:
	private boolean useDefaultPackage;

	private LocalizationSetting localizationSetting;

	private String polishHomePath;

	private File polishHomeDir;

	private ArrayList customPreprocessors;

	private boolean doPostCompile;

	private PostCompiler[] postCompilers;

	private LibrariesSetting binaryLibraries;

	private ExtensionManager extensionManager;
	private Environment environment;

	private DeviceDatabase deviceDatabase;

	private boolean isInitialized;

	private PreCompiler[] preCompilers;

	private BooleanEvaluator antPropertiesEvaluator;
	
	private ArrayList polishBuildListenerSettingsList;
	private PolishBuildListener[] polishBuildListeners;

	private LocaleSetting currentLocaleSetting;

	private StringList styleCacheCode;

	private Map licensingInformation;


	
	/**
	 * Creates a new empty task 
	 */
	public PolishTask() {
		// initialisation is done with the setter-methods.
		// if you should use the PolishTask not within an ant-build.xml
		// then make sure to set the project with .setProject(...)
		this.resourceUtil = new ResourceUtil( getClass().getClassLoader() );
	}
	
	public void addConfiguredBuildlistener( BuildListenerExtensionSetting setting ) {
		if (this.polishBuildListenerSettingsList == null) {
			this.polishBuildListenerSettingsList = new ArrayList();
		}
		this.polishBuildListenerSettingsList.add( setting );
	}
	
	public void addConfiguredInfo( InfoSetting setting ) {
		if (setting.getName() == null ) {
			throw new BuildException("The nested element <info> requires the attribute [name] which defines the name of this project.");
		}
		if (setting.getVendorName() == null) {
			throw new BuildException("The nested element <info> requires the attribute [vendorName] which defines the name of the vendor providing the application.");
		}
		if (setting.getVersion() == null) {
			throw new BuildException("The nested element <info> requires the attribute [version] which defines the version of this application, e.g. \"1.2.3\".");
		}
		setting.setDefaultJarUrl();
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		if (requirements.isActive( this.antPropertiesEvaluator )) {
			this.deviceRequirements = requirements;
		}
	}
	
	/**
	 * Creates and adds the build settings for this project.
	 * 
	 * @return the new build setting.
	 */
	public BuildSetting createBuild() {
		this.buildSetting = new BuildSetting( getProject(), this.antPropertiesEvaluator );
		return this.buildSetting;
	}
	
	/**
	 * Creates and adds a new run-setting for this project.
	 * 
	 * @return the new runsetting.
	 */
	public EmulatorSetting createEmulator() {
		if (this.emulatorSettings == null) {
			this.emulatorSettings =  new ArrayList();
		}
		EmulatorSetting emulatorSetting = new EmulatorSetting( getProject() );
		this.emulatorSettings.add( emulatorSetting );
		return emulatorSetting;
	}
	
	/**
	 * Executes this task. 
	 * For all selected devices the source code will be preprocessed,
	 * compiled, obfuscated and jared.
	 * 
	 * @throws BuildException when the build failed.
	 */
	public void execute() throws BuildException {
		System.out.println("J2ME Polish " + VERSION + " (" + getLicenseInfo() +")");
		if (!isActive()) {
			return;
		}
		try {
			if (!this.isInitialized) {
				checkSettings();
				initProject();
				selectDevices();
        clearDeviceManager();
				this.isInitialized = true;
			}
			// check if there has been an error at the last run:
			this.errorLock = new File( this.buildSetting.getWorkDir(),  "error.lock");
			if (this.errorLock.exists()) {
				this.lastRunFailed = true;
			} else {
				this.lastRunFailed = false;
				try {
					this.errorLock.createNewFile();
				} catch (IOException e) {
					System.err.println("Warning: unable to create temporary lock file: " + e.toString() );
				}
			}

			int numberOfDevices = this.devices.length;
			if (this.buildSetting.isInCompilerMode()) {
				System.out.println("Using J2ME Polish as compiler...");
			} else if (numberOfDevices > 1) {
				System.out.println("Processing [" + numberOfDevices + "] devices...");
			}
			boolean hasExtensions = (this.javaExtensions.length > 0);
			
			int successCount = 0;
			ArrayList failures = new ArrayList();
			boolean abortOnError = this.buildSetting.abortOnError();
			boolean enableCompilerMode = this.buildSetting.isInCompilerMode();
			for ( int i=0; i < numberOfDevices; i++) {
				Device device = this.devices[i];
				if ( !enableCompilerMode && numberOfDevices > 1) {
					System.out.println("Building application for [" + device.getIdentifier() + "] (" + (i+1) + "/" + numberOfDevices + "):");
				}
				if (this.supportedLocales != null) {
					for (int j = 0; j < this.supportedLocales.length; j++) {
						LocaleSetting localeSetting = this.supportedLocales[j];
						this.currentLocaleSetting = localeSetting;
						Locale locale = localeSetting.getLocale();
						System.out.println("Using locale [" + locale.toString() + "]...");
						try {
							execute(device, locale, hasExtensions);
							successCount++;
						} catch (BuildException e) {
							if (abortOnError) {
								throw e;
							}
							//e.printStackTrace();
							failures.add( new FailureInfo( device, locale, e ) );
						}
						if (enableCompilerMode) {
							// return immediately in the compiler mode:
							return;
						}
					}					
				} else {
					try {
						execute(device, null, hasExtensions);
						successCount++;
					} catch (BuildException e) {
						if (abortOnError) {
							throw e;
						}
						//e.printStackTrace();
						failures.add( new FailureInfo( device, null, e ) );
					}
					if (this.buildSetting.isInCompilerMode()) {
						// return immediately in the compiler mode:
						return;
					}
				}
				if (numberOfDevices > 1) {
					// print an empty as a separator between different devices: 
					System.out.println();
				}
			}
			test();
			deploy();
			finishProject();
			
			if (abortOnError || failures.size() == 0 ) {
				if (numberOfDevices > 1 || successCount > 1 ) {
					if ( successCount == numberOfDevices ) {
						System.out.println("Successfully processed [" + numberOfDevices + "] devices.");
					} else {
						if ( numberOfDevices == 1 ) {
							System.out.println("Successfully processed one device with [" + successCount + "] builds.");
						} else {
							System.out.println("Successfully processed [" + numberOfDevices + "] devices with [" + successCount + "] builds.");
						}
					}
				}
			} else {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Processed [" ).append( numberOfDevices ).append( "] devices with [" )
					.append( successCount ).append( "] successful builds and [" ).append( failures.size() ).append( "] errors:\n" );
				FailureInfo[] infos  = (FailureInfo[]) failures.toArray( new FailureInfo[ failures.size() ] );
				for (int i = 0; i < infos.length; i++) {
					FailureInfo info = infos[i];
					info.appendInfo( buffer );
					buffer.append('\n');
				}
				String message = buffer.toString();
				System.out.println( message );
				
				Project antProject = getProject();
				antProject.setProperty("j2mepolish.error", "true");
				antProject.setProperty("j2mepolish.error.message",  message );
				
				executeErrorTarget( this.buildSetting.getOnError(), null );
			}
		} catch (de.enough.polish.BuildException e) {
			executeErrorTarget( this.buildSetting.getOnError(), e );
			throw new BuildException( e.getMessage() );
		} catch (BuildException e) {
			executeErrorTarget( this.buildSetting.getOnError(), e );
			//e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			executeErrorTarget( this.buildSetting.getOnError(), e );
			throw new BuildException("Unable to execute J2ME Polish task: " + e.toString(), e );
		}
		if (this.runningEmulators != null) {
			System.out.println("Waiting for emulators...");
			while (this.runningEmulators.size() > 0) {
				Emulator[] emulators = (Emulator[]) this.runningEmulators.toArray( new Emulator[ this.runningEmulators.size() ]);
				for (int i = 0; i < emulators.length; i++) {
					Emulator emulator = emulators[i];
					if (emulator.isFinished()) {
						this.runningEmulators.remove( emulator );
					}					
				}
				if (this.runningEmulators.size() > 0) {
					try {
						Thread.sleep( 2000 );
					} catch (InterruptedException e1) {
						// ignore
					}
				}
			}
		}
	}


  private void clearDeviceManager() {
    this.deviceManager.clear();
  }
	

	protected void executeErrorTarget( String targetName, Exception e ) {
		try {
			if ( targetName != null ) {
				Project antProject = getProject();
				if (e != null) {
					antProject.setProperty("j2mepolish.error", "true");
					antProject.setProperty("j2mepolish.error.message",  e.toString() );
				}
				// invoke Ant target:
				CallTarget errorTarget = new CallTarget();
				// setting up a new ant project:
				errorTarget.setProject( antProject );
				errorTarget.setTarget( targetName );
				errorTarget.execute();
			}
		} catch (Exception targetE) {
			targetE.printStackTrace();
			System.err.println("Unable to execute error target [" + targetName + "]: " + targetE.toString() );
		}
	}
	

	/**
	 * Executes the sequence of J2ME Polish subtasks for the given device and locale.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param hasExtensions true when there are <java>-extensions
	 */
	protected void execute(Device device, Locale locale, boolean hasExtensions) {
		initialize( device, locale );
		assembleResources( device, locale );
		preprocess( device, locale );
		compile( device, locale );
		postCompile(device, locale);
		if (this.buildSetting.isInCompilerMode()) {
			if (this.buildSetting.doPreverifyInCompilerMode()) {
				preverify( device, locale );
			}
			finishProject();
			System.out.println();
			System.out.println("Successfully processed the activated compilerMode of J2ME Polish.");
			return;
		}
		if (this.doObfuscate) {
			obfuscate( device, locale );
		}
		if (this.buildSetting.doPreverify()) {
			preverify( device, locale );
		}
		jar( device, locale );
		jad( device, locale );
		if (hasExtensions) {
			callExtensions( device, locale );
		}
		finalize( device, locale );
		notifyPolishBuildListeners( PolishBuildListener.EVENT_BUILD_FINISHED, this.environment );
		if (this.emulatorSettings != null) {
			runEmulator( device, locale );
		}
	}

	/**
	 * Finishes this project.
	 * Basically removes the error-lock:
	 */
	protected void finishProject() {
		this.errorLock.delete();
	}

	/**
	 * Checks the settings of this task.
	 * 
	 * @throws BuildException when a setting is invalid
	 */
	public void checkSettings() {
		//System.out.println( getProject().getBaseDir().getAbsolutePath() );
		if (this.infoSetting == null) {
			throw new BuildException("Nested element [info] is required.");
		}
		if (this.buildSetting == null) {
			throw new BuildException("Nested element [build] is required.");
		}
		if (this.deviceRequirements == null) {
			log("Nested element [deviceRequirements] is missing, now the project will be optimized for all known devices.");
		}
		// check the nested element of <build>:

		//}
		// check if the ant-property wtk.home or alternatively mpp.home has been set:
		//e.g. with: <property name="wtk.home" value="c:\Java\wtk-1.0.4"/>
		this.wtkHome = getProject().getProperty("wtk.home");
		String mppHome = getProject().getProperty("mpp.home");
		//System.out.println("wtk.home=" + this.wtkHome + ", mpp.home=" + mppHome ) ;

		if (this.buildSetting.getPreverify() == null) {
			// no preverify has been set, that's okay when the wtk.home ant-property has been set:
			if (this.wtkHome == null && mppHome == null) { 
				System.err.println("Warning: Nested element [build] should define the attribute [preverify] which points to the preverify-executable of the wireless toolkit. Alternatively you can set the home directory of the Wireless Toolkit by defining the Ant-property [wtk.home]: <property name=\"wtk.home\" location=\"C:\\WTK2.3\"/>\nAnother option is to set the \"mpp.home\" property that points to the installation directory of the MPowerPlayer SDK.\nThese settings are only necessary when you target MIDP devices, though.");
			}
			if (this.wtkHome != null) {
				File wtkHomeFile = new File( this.wtkHome );
				if ( !wtkHomeFile.exists() ) {
					wtkHomeFile = new File( getProject().getBaseDir(), this.wtkHome );
					if ( wtkHomeFile.exists() ) {
						this.wtkHome = wtkHomeFile.getAbsolutePath();
					}
				}
				
				if (!this.wtkHome.endsWith( File.separator )) {
					this.wtkHome += File.separator;
				}
			}
			File preverifyFile = null;
			if (this.wtkHome != null && ((new File(this.wtkHome)).exists()) ) {
				String preverifyPath = this.wtkHome + "bin" + File.separator + "preverify";
				if ( File.separatorChar == '\\') {
					preverifyPath += ".exe";
				}
				preverifyFile = new File( preverifyPath );
//				System.out.println("preverify: " + preverifyPath + ".exists() = " + preverifyFile.exists() );
			} else if (mppHome != null) {
				if (!mppHome.endsWith(File.separator)) {
					mppHome += File.separator;
				}
				String preverifyPath = mppHome + "osx" + File.separator + "preverify" + File.separator + "preverify";
				preverifyFile = new File( preverifyPath );
				//System.out.println("preverifyHome=" + preverifyFile.getAbsolutePath() + ", exists=" + preverifyFile.exists() );
			}
			if (preverifyFile != null) {
				if (preverifyFile.exists()) {
					this.buildSetting.setPreverify( preverifyFile.getAbsolutePath() );
				} else if (this.wtkHome != null) {
					// probably the wtk.home path is wrong:
					File file = new File( this.wtkHome );
					if (!file.exists()) {
						throw new BuildException("The Ant-property [wtk.home] points to the non-existing directory [" + this.wtkHome + "]. Please adjust his setting in the build.xml file.");
					} else {
						throw new BuildException("Unable to find the preverify tool at the default location [" + preverifyFile.getAbsolutePath() + "]. Please specify where to find it with the \"preverify\"-attribute of the <build> element (in the build.xml file) or specify the \"wtk.home\" property.");
					}
				}
			}
		}
	}
	
	public void init() {
		super.init();
		Map antSymbols = new HashMap();
		Hashtable properties = getProject().getProperties();
		Object[] keys = properties.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			antSymbols.put( key + ":defined", Boolean.TRUE );
		}
		this.antPropertiesEvaluator = new BooleanEvaluator( antSymbols, properties );

	}
	
	/**
	 * Initialises this project and instantiates several helper classes.
	 */
	public void initProject() {
		// find out where J2ME Polish has been installed to:
		this.polishHomePath = getProject().getProperty( "polish.home" );
		if (this.polishHomePath != null) {
			File dir = new File( this.polishHomePath );
			if (!dir.isAbsolute()) {
				dir = new File( getProject().getBaseDir(), this.polishHomePath );
				this.polishHomePath = dir.getAbsolutePath();
			}
			this.polishHomePath += File.separatorChar;
			this.polishHomeDir = new File( this.polishHomePath );
		} else {
			this.polishHomeDir = new File( "." );
		}
		
		try {
			// load extensions:
			this.extensionManager = new ExtensionManager(  getProject(), this.buildSetting.openExtensions() );
			this.extensionManager.loadCustomDefinitions( this.buildSetting.getCustomExtensions() );
			this.extensionManager.loadCustomDefinitions( new File( this.polishHomeDir, "custom-extensions.xml" ) );
			this.extensionManager.loadCustomDefinitions( new File( getProject().getBaseDir(), "custom-extensions.xml" ) );
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to load extensions.xml - please report this error to j2mepolish@enough.de.");
		}
		
		// create environment
		this.environment = new Environment( this.extensionManager, getProject().getProperties(), getProject().getBaseDir() );
		this.environment.setBuildSetting( this.buildSetting );
		
		
		Midlet[] midlets = this.buildSetting.getMidlets(this.environment); 
		if (midlets == null || midlets.length == 0) {
			// check for main class or iappli class:
			if ( this.buildSetting.getMainClassSetting() == null
					&& this.buildSetting.getDojaClassSetting() == null
					) 
			{
				System.err.println("Warning: Midlets should to be defined in the <build>-section with either <midlets> or <midlet>. Alternatively you might use a <iappli> or <main> element for defining the DoJa main class or the BlackBerry main class.");
			}
		} else {
			// now check if the midlets do exist:
			SourceSetting[] sources = this.buildSetting.getSourceSettings();
			//if (!this.buildSetting.useDefaultPackage()) {
			for (int i = 0; i < midlets.length; i++) {
				Midlet midlet = midlets[i];
				String fileName = StringUtil.replace( midlet.getClassName(), '.', File.separatorChar) + ".java";
				boolean midletFound = false;
				for (int j = 0; j < sources.length; j++) {
					SourceSetting setting = sources[j]; 
					File sourceDir = setting.getDir();
					String sourceDirPath = sourceDir.getAbsolutePath();
					File midletFile = new File( sourceDirPath + File.separator + fileName );
					if (midletFile.exists()) {
						if (File.separatorChar == '\\') {
							// additionally check the case of the MIDlet file
							// on Windows systems:
							try {
								if (!midletFile.getCanonicalPath().endsWith(fileName)) {
									System.out.println("WARNING: The case of the <midlet> definition \"" + midlet.getClassName() + "\" seems to be wrong, since the source code is in " + midletFile.getCanonicalPath() + ". Check your build.xml script.");
									continue;
								}
							} catch (IOException e) {
								System.out.println("Warning: unable to get canonical path for file " + midletFile.getAbsolutePath() );
								e.printStackTrace();
							}
						} 
						midletFound = true;
						break;
					}
				}
				if (!midletFound) {
					String message = "The MIDlet [" + midlet.getClassName() + "] could not be found. Check your <midlet>-setting in the file [build.xml] or adjust the [sourceDir] attribute of the <build>-element.";
					if ( midlet.getClassName().indexOf('.') != -1) {
						if (sources.length > 1) {
							message += "The MIDlet should be in ${src}" + File.separatorChar + fileName + ", where ${src} is one your source folders.";
						} else {
							message += "The MIDlet should be in " + sources[0].getDir().toString()  + File.separatorChar + fileName;
						}
					}
					throw new BuildException( message );
				}
			}
		}
		
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		LogSetting debugSetting = this.buildSetting.getDebugSetting();
		if (isDebugEnabled) {
			try {
				debugManager = new DebugManager( debugSetting, this.extensionManager, this.environment );
			} catch (BuildException e) {
				throw new BuildException( e.getMessage(), e );
			}
		}
		
		// create project settings:
		this.polishProject = new PolishProject( this.buildSetting.usePolishGui(), isDebugEnabled, debugManager );
		this.polishProject.addDirectCapability("polish.buildVersion", VERSION);
		this.polishProject.addDirectFeature( "polish.active" );
		if ( isDebugEnabled) {
			this.polishProject.addCapability("polish.debug.level", "" + debugSetting.logLevel() );
			this.polishProject.addCapability("polish.debug.timestamp", "" + debugSetting.logTimestamp() );
			this.polishProject.addCapability("polish.debug.thread", "" + debugSetting.logThread() );
			this.polishProject.addCapability("polish.debug.className", "" + debugSetting.logClassName() );
			this.polishProject.addCapability("polish.debug.lineNumber", "" + debugSetting.logLineNumber() );
			this.polishProject.addCapability("polish.debug.message", "" + debugSetting.logMessage() );
			this.polishProject.addCapability("polish.debug.exception", "" + debugSetting.logException() );
		}
		if (debugManager != null && debugManager.isVerbose()) {
			this.polishProject.addFeature("debugVerbose");
			this.polishProject.addFeature("debug.verbose");
			this.polishProject.addDirectCapability("polish.debug.Verbose", "enabled");
		}
		if (debugManager != null && this.buildSetting.getDebugSetting().showLogOnError()) {
			this.polishProject.addFeature("showLogOnError");
		}
		this.polishProject.addCapability("license", getLicense() );
		if (isDebugEnabled) {
			this.polishProject.addFeature("debugEnabled");
		}
		// specify some preprocessing symbols depending on the selected features:
		this.polishProject.addFeature(this.buildSetting.getImageLoadStrategy());
		// this is actually outdated and only kept for backwards-compatibility:
		if (debugManager != null && this.buildSetting.getDebugSetting().useGui()) {
			this.polishProject.addFeature("useDebugGui");
		}
		// add all ant properties if desired: 
		if (this.buildSetting.includeAntProperties()) {
			Hashtable antProperties = getProject().getProperties();
			Set keySet = antProperties.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				this.polishProject.addDirectCapability( key, (String) antProperties.get(key) );
			}
		}
		// add all variables from the build.xml:
		/*
		Variable[] variables = this.buildSetting.getVariables();
		if (variables != null) {
			ArrayList conditionalVarsList = new ArrayList();
			for (int i = 0; i < variables.length; i++) {
				Variable var = variables[i];
				if (var.hasCondition()) {
					conditionalVarsList.add( var );
				} else {
					//System.out.println("adding variable [" + var.getName() + "]." );
					this.polishProject.addDirectCapability( var );
				}
			}
			this.conditionalVariables = (Variable[]) conditionalVarsList.toArray( new Variable[ conditionalVarsList.size() ]); 
		}
		*/
		this.variables = this.buildSetting.getVariables();
		Variable[] unconditionalVars = this.variables.getUnconditionalVariables();
		for (int i = 0; i < unconditionalVars.length; i++) {
			Variable variable = unconditionalVars[i];
			this.polishProject.addDirectCapability( variable );
		}
		
		// add all symbols from the build.xml:
		String symbolDefinition = this.buildSetting.getSymbols();
		if (symbolDefinition != null) {
			String[] symbols = StringUtil.splitAndTrim( symbolDefinition, ',' );
			for (int i = 0; i < symbols.length; i++) {
				this.polishProject.addDirectFeature( symbols[i] );
			}
		}
				
		// load CSS attributes:
		// create CSS attributes manager:
		if (this.buildSetting.usePolishGui()) {
			this.cssAttributesManager = new CssAttributesManager( this.buildSetting.openStandardCssAttributes() );
			InputStream is = this.buildSetting.openCustomCssAttributes();
			if (is != null) {
				this.cssAttributesManager.addCssAttributes(is);
			}
			boolean allowAllCssAttributes = "true".equals( this.polishProject.getCapability("polish.css.allowAllAttributes") );
			if (allowAllCssAttributes) {
				CssAttribute[] attributes = this.cssAttributesManager.getAttributes();
				for (int i = 0; i < attributes.length; i++) {
					CssAttribute attribute = attributes[i];
					String name = attribute.getName();
					String feature = "polish.css." + name;
					this.polishProject.addDirectFeature( feature );
				}
			}
		}

		// create device database:
		System.out.println("Loading device database...");
		this.deviceDatabase = DeviceDatabase.getInstance( getProject().getProperties(), this.polishHomeDir, getProject().getBaseDir(),
				this.buildSetting.getApiDir(), this.polishProject, this.buildSetting.getDeviceDatabaseInputStreams(), this.buildSetting.getDeviceDatabaseFiles() );
		System.out.println("  ...done.");
		
		this.libraryManager = this.deviceDatabase.getLibraryManager();
		this.environment.setLibraryManager(this.libraryManager);
		
		// create capability/vendor/group/device manager:
		this.deviceManager = this.deviceDatabase.getDeviceManager();
				
		// create preprocessor:
		boolean replacePropertiesWithoutDirective = false;
		if (this.variables != null) {
			replacePropertiesWithoutDirective = this.variables.replacePropertiesWithoutDirective();
		}
		if (!replacePropertiesWithoutDirective) {
			replacePropertiesWithoutDirective = this.buildSetting.replacePropertiesWithoutDirective();
		}
		this.preprocessor = new Preprocessor( this.polishProject, this.environment, null, false, true, replacePropertiesWithoutDirective, null );
		this.preprocessor.setCssAttributesManager( this.cssAttributesManager );
		this.environment.set( Preprocessor.ENVIRONMENT_KEY, this.preprocessor );
		// init custom preprocessors:
		this.customPreprocessors = new ArrayList();
		PreprocessorSetting[] settings = this.buildSetting.getPreprocessors();
		
		//CustomPreprocessor[] processors = new CustomPreprocessor[ settings.length + 1];
		// add the polish custom processor:
		// new PolishPreprocessor();
		//PreprocessorSetting preprocessorSetting = new PreprocessorSetting();
		//preprocessorSetting.setName("polish");
		//CustomPreprocessor customProcessor = CustomPreprocessor.getInstance(preprocessorSetting, this.preprocessor, this.extensionManager, this.environment );
		//customProcessor.init(this.preprocessor, null);
		//this.customPreprocessors.add( customProcessor );
		//processors[0] = customProcessor;
		for (int i = 0; i < settings.length; i++) {
			PreprocessorSetting preprocessorSetting = settings[i];
			try {
				//Extension customProcessor = this.extensionManager.getExtension( ExtensionManager.TYPE_PREPROCESSOR, preprocessorSetting, this.environment );
				CustomPreprocessor customProcessor = CustomPreprocessor.getInstance(preprocessorSetting, this.preprocessor, this.extensionManager, this.environment );
				this.customPreprocessors.add( customProcessor );
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Unable to initialize preprocessor [" + preprocessorSetting.getName() + "/" + preprocessorSetting.getClassName() + "]: " + e.toString() );
			}
		}
		
		//	initialise the preprocessing-source-directories:
		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setIncludes( new String[]{"**/*.java"} );
		this.sourceSettings = this.buildSetting.getSourceSettings();
		//File[] dirs = this.buildSetting.getSourceDirs();
		//this.sourceDirs = new File[ dirs.length];
		this.sourceFiles = new TextFile[ this.sourceSettings.length][];
		TextFileManager textFileManager = new TextFileManager();
		this.preprocessor.setTextFileManager(textFileManager);
		if (this.buildSetting.getPolishDir() != null) {
			// there is an explicit J2ME Polish directory:
			this.polishSourceDir = this.buildSetting.getPolishDir();
			if (this.variables == null) {
				this.variables = new Variables();
			}
			this.variables.addConfiguredVariable( new Variable( "polish.internal.source", this.polishSourceDir.getAbsolutePath() ));
			dirScanner.setBasedir(this.polishSourceDir);
			dirScanner.scan();
			this.polishSourceFiles = getTextFiles( this.polishSourceDir,  dirScanner.getIncludedFiles(), textFileManager );
		} else {
			// the J2ME Polish sources need to be loaded from the jar-file:
			long lastModificationTime = 0;
			File jarFile = new File( this.polishHomeDir.getAbsolutePath() + File.separator 
					+  "lib" + File.separator + "enough-j2mepolish-build.jar");
			if (!jarFile.exists()) {
				jarFile = new File( this.buildSetting.getApiDir().getAbsolutePath() 
						+ File.separator + "enough-j2mepolish-build.jar");
				if (!jarFile.exists()) {
					jarFile = new File("lib/enough-j2mepolish-build.jar");
					if (!jarFile.exists()) {
						jarFile = new File("import/enough-j2mepolish-build.jar");						
					}
				}
			}
			if (jarFile.exists()) {
				lastModificationTime = jarFile.lastModified();
			}
			this.polishSourceDir = new File("src");
			try {
				String[] fileNames = this.resourceUtil.readTextFile( ".", "build/j2mepolish.index.txt");
				this.polishSourceFiles = getTextFiles( "src", fileNames, lastModificationTime, textFileManager );
			} catch (IOException e) {
				throw new BuildException("Unable to load the J2ME source files from enough-j2mepolish-build.jar, if you want to use J2ME Polish client sources directly set the \"polishDir\" attribute of the <build> element in your build.xml script (" + e.getMessage() + ").", e );
			}
		}
		
		// load the normal source files:
		for (int i = 0; i < this.sourceSettings.length; i++) {
			SourceSetting setting = this.sourceSettings[i];
			File dir = setting.getDir();
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + dir.getAbsolutePath() + "] does not exist. Please check your settings in the \"sourceDir\" attribute of the <build> element.");
			}
			//this.sourceDirs[i] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i] = getTextFiles( dir,  dirScanner.getIncludedFiles(), textFileManager );
		}
		if (this.buildSetting.usePolishGui() && this.styleSheetSourceFile == null) {
			throw new BuildException("Did not find the file [StyleSheet.java] of the J2ME Polish GUI framework. Please adjust the \"polishDir\" attribute of the <build> element in the [build.xml] file. The [polishDir]-attribute should point to the directory which contains the J2ME Polish-Java-sources.");
		}
		
		// load third party binary libraries, if any.
		// When there are third party libraries, they will all be extracted
		// and copied to the build/binary folder for easier integration:
		this.binaryLibraries = this.buildSetting.getBinaryLibraries();
		if (this.binaryLibraries != null) {
			File binaryBaseDir = new File( this.buildSetting.getWorkDir(), "binary");
			try {
				this.binaryLibrariesUpdated = this.binaryLibraries.copyToCache( binaryBaseDir );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to copy the binary libraries to the internal cache [" + binaryBaseDir.getAbsolutePath() + "]: " + e.toString(), e );
			}
		} // done preparing of binary libraries.
		

		// init precompilers:
		if (this.buildSetting.doPreCompile()) {
			PreCompilerSetting[] postCompilerSettings = this.buildSetting.getPreCompilers();
			this.preCompilers = new PreCompiler[ postCompilerSettings.length ];
			for (int i = 0; i < postCompilerSettings.length; i++) {
				PreCompilerSetting setting = postCompilerSettings[i];
				this.preCompilers[i] = PreCompiler.getInstance(setting, this.extensionManager, this.environment);
			}
		}

		// init postcompilers:
		if (this.buildSetting.doPostCompile()) {
			this.doPostCompile = true;
			PostCompilerSetting[] postCompilerSettings = this.buildSetting.getPostCompilers();
			this.postCompilers = new PostCompiler[ postCompilerSettings.length ];
			for (int i = 0; i < postCompilerSettings.length; i++) {
				PostCompilerSetting setting = postCompilerSettings[i];
				this.postCompilers[i] = PostCompiler.getInstance(setting, this.extensionManager, this.environment);
			}
		}
				
		
		// init obfuscators:
		if (this.buildSetting.doObfuscate()) {
			ObfuscatorSetting[] obfuscatorSettings = this.buildSetting.getObfuscatorSettings();
			ArrayList obfuscatorsList = new ArrayList();
			String[] keepClasses = null;
			for (int i = 0; i < obfuscatorSettings.length; i++) {
				ObfuscatorSetting obfuscatorSetting = obfuscatorSettings[i];
				if (keepClasses == null && 
						obfuscatorSetting.hasKeepDefinitions()) {
					keepClasses = obfuscatorSetting.getPreserveClassNames();
				}
				if ((obfuscatorSetting.isEnabled())) {
					try {
						Obfuscator obfuscator = Obfuscator.getInstance( obfuscatorSetting, getProject(), this.extensionManager, this.environment );
						obfuscatorsList.add( obfuscator );
					} catch (BuildException e) {
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
						throw new BuildException("Unable to initialize the obfuscator [" + obfuscatorSetting.getName() + "/" + obfuscatorSetting.getClassName() + "]: " + e.toString() );
					}
				}
				
			}
			this.doObfuscate = (obfuscatorsList.size() > 0);
			if (this.doObfuscate) {
				this.obfuscators = (Obfuscator[]) obfuscatorsList.toArray( new Obfuscator[ obfuscatorsList.size() ] );
				//this.useDefaultPackage = this.buildSetting.useDefaultPackage();
				
				String[] midletClasses = this.buildSetting.getMidletClassNames( false, this.environment );
				ArrayList preserveList = new ArrayList();
				for (int i = 0; i < midletClasses.length; i++) {
					String className = midletClasses[i];
					preserveList.add( className );
					int lastDotPos = className.lastIndexOf( '.' );
					if ( lastDotPos != -1 ) {
						preserveList.add( className.substring( lastDotPos + 1 ) );							
					}					
				}
				if (keepClasses != null) {
					for (int i = 0; i < keepClasses.length; i++) {
						String className = keepClasses[i];
						preserveList.add( className );
						int lastDotPos = className.lastIndexOf( '.' );
						if ( lastDotPos != -1 ) {
							preserveList.add( className.substring( lastDotPos + 1 ) );							
						}	
					}
				}
				if (this.buildSetting.getDojaClassSetting() != null) {
					String className = this.buildSetting.getDojaClassSetting().getClassName(); 
					preserveList.add( className );
					int lastDotPos = className.lastIndexOf( '.' );
					if ( lastDotPos != -1 ) {
						preserveList.add( className.substring( lastDotPos + 1 ) );							
					}	
				}
				if (this.buildSetting.getMainClassSetting() != null) {
					String className = this.buildSetting.getMainClassSetting().getClassName(); 
					preserveList.add( className );
					int lastDotPos = className.lastIndexOf( '.' );
					if ( lastDotPos != -1 ) {
						preserveList.add( className.substring( lastDotPos + 1 ) );							
					}	
				}
				this.preserveClasses = (String[]) preserveList.toArray( new String[ preserveList.size() ] );
			}
		}
		
		// init import manager:
		this.importConverter = new ImportConverter();
		
		// init base style sheet:
//		if (this.buildSetting.usePolishGui()) {
//			ResourceSetting resourceSetting = this.buildSetting.getResourceSetting();
//			File cssFile = new File( resourceSetting.getDir().getAbsolutePath(), "polish.css");
//			if (!cssFile.exists()) {
//				log("Unable to find polish.css at [" + cssFile.getAbsolutePath() + "] - you should create this file when you want to make most of the J2ME Polish GUI.", Project.MSG_WARN );
//			}
//		}
		
		// set the names of the midlets:
		this.midletClassesByName = new HashMap();
		String[] midletClassNames = this.buildSetting.getMidletClassNames( false, this.environment );
		for (int i = 0; i < midletClassNames.length; i++) {
			String midletClassName = midletClassNames[i];
			this.midletClassesByName.put( midletClassName, Boolean.TRUE );
			int lastDotPos = midletClassName.lastIndexOf( '.' );
			if ( lastDotPos != -1 ) {
				this.midletClassesByName.put( midletClassName.substring( lastDotPos + 1 ), Boolean.TRUE );
			}
		}
		
		// get the java-extension:
		this.javaExtensions = this.buildSetting.getJavaExtensions();
		
		// get the javac-settings:
		this.javacTarget = this.buildSetting.getJavacTarget();
		
		// get the resource manager:
		this.resourceManager = new ResourceManager( this.buildSetting.getResourceSetting(), this.extensionManager, this.environment );
		LocaleSetting[] localizations = this.resourceManager.getLocales();
		if (localizations != null) {
			this.localizationSetting = this.buildSetting.getResourceSetting().getLocalizationSetting();
			if (this.localizationSetting.isDynamic()) {
				this.supportedLocales = null;
			} else {
				this.supportedLocales = localizations;
			}
			// add the preprocessor for translations:
			String className;
			if (this.localizationSetting != null) {
				className = this.localizationSetting.getPreprocessorClassName();
			} else {
				className = "de.enough.polish.preprocess.custom.TranslationPreprocessor";
			}
			PreprocessorSetting setting = new PreprocessorSetting();
			setting.setClass( className );
			this.translationPreprocessor = (TranslationPreprocessor) CustomPreprocessor.getInstance(setting, this.preprocessor,  this.extensionManager, this.environment );
			this.customPreprocessors.add( this.translationPreprocessor );
			if (this.localeSourceFile == null) {
				throw new BuildException("Unable to find [de.enough.polish.util.Locale.java] in the path, please set the \"polishDir\"-attribute of the <build>-element correctly.");
			}
		}
		
		try {
			// get the packager for creating the final jar-file:
			this.extensionManager.registerExtensions( ExtensionManager.TYPE_PACKAGER, this.buildSetting.getPackageSettings(), this.environment );
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to initialize packager: " + e.toString() );
		}
				
		// set J2ME Polish specific logger,
		// this logger will show the original source-code positions
		// and remove some verbose logging from ProGuard etc:
		Vector buildListeners = getProject().getBuildListeners();
		BuildListener logger = null;
		for (Iterator iter = buildListeners.iterator(); iter.hasNext();) {
			BuildListener listener = (BuildListener) iter.next();
			if (listener instanceof BuildLogger || listener.getClass().getName().indexOf("NbBuildLogger") != -1) {
				logger = listener;
				break;
//			} else {
//				System.out.println("Found BuildListener " + listener.getClass().getName() );
			}
		}
		if (logger != null) {
			// prepare the classPathTranslations-Map:
			HashMap classPathTranslationsMap = new HashMap(); 
			for (int i=0; i < this.sourceFiles.length; i++) {
				TextFile[] files = this.sourceFiles[i];
				for (int j = 0; j < files.length; j++) {
					TextFile file = files[j];
					String filePath = file.getFilePath();
					classPathTranslationsMap.put( filePath, file.getFile().getAbsolutePath() );
					int packageEnd = filePath.lastIndexOf( File.separatorChar );
					if ( packageEnd != -1 ) {
						classPathTranslationsMap.put( filePath.substring( packageEnd + 1 ), file.getFile().getAbsolutePath() );						
					}
					//System.out.println("classPathTranslation: from=" + file.getFilePath() + " to " + file.getFile().getAbsolutePath() );
				}
			}
			this.polishLogger = new PolishLogger(logger, classPathTranslationsMap, this.environment );
			getProject().addBuildListener( this.polishLogger );
			getProject().removeBuildListener(logger);
		} else {
			System.err.println("Warning: unable to replace Ant-logger. Compile errors will point to the preprocessed files instead of the original sources.");
		}
		
		
		// initialize polish build listeners:
		ArrayList polishBuildListenersList = new ArrayList();
		String classDefs = getProject().getProperty( PolishBuildListener.ANT_PROPERTY_NAME );
		if (classDefs != null ) {
			String[] classNames = StringUtil.splitAndTrim(classDefs, ',');
			for (int i = 0; i < classNames.length; i++) {
				String className = classNames[i];
				try {
					PolishBuildListener listener = (PolishBuildListener) Class.forName(className).newInstance();
					polishBuildListenersList.add( listener );
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to load PolishBuildListener " + className + ": " + e.toString(), e  );
				}
			}
		}
		if (this.polishBuildListenerSettingsList != null ) {
			BuildListenerExtensionSetting[] buildSettings = (BuildListenerExtensionSetting[]) this.polishBuildListenerSettingsList.toArray( new BuildListenerExtensionSetting[this.polishBuildListenerSettingsList.size()] );
			for (int i = 0; i < buildSettings.length; i++) {
				BuildListenerExtensionSetting setting = buildSettings[i];
				String className = setting.getClassName();
				//TODO use extension manager for instantiation of PolishBuildListeners
				try {
					PolishBuildListener listener = (PolishBuildListener) Class.forName(className).newInstance();
					polishBuildListenersList.add( listener );
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to load PolishBuildListener " + className + ": " + e.toString(), e  );
				}
			}
		}
		if (polishBuildListenersList.size() > 0 ) {
			this.polishBuildListeners = (PolishBuildListener[]) polishBuildListenersList.toArray( new PolishBuildListener[ polishBuildListenersList.size() ] ); 
		}
	}

	/**
	 * Retrieves the license for J2ME Polish
	 * 
	 * @return the license name
	 */
	private String getLicense() {
		if (this.licensingInformation == null) {
			// check project license.key:
			File polishDir = null;
			String polishHome = getProject().getProperty("polish.home");
			if (polishHome != null) {
				polishDir = getProject().resolveFile(polishHome);
			}
			File licenseKeyFile = new File( getProject().getBaseDir(), "license.key" );
			if (!licenseKeyFile.exists()) {
				// check ${polish.home}/license.key file:
				if (polishDir != null) {
					licenseKeyFile = new File( polishDir, "license.key" );
				}
			}
			if (licenseKeyFile.exists()) {
				try {
					PathClassLoader classLoader = new PathClassLoader( getClass().getClassLoader(), false );
					if (polishDir != null) {
						classLoader.addPathFile( new File( polishDir, "lib/enough-license.jar") );
						classLoader.addPathFile( new File( polishDir, "lib/enough-j2mepolish-extensions.jar") );
						classLoader.addPathFile( new File( polishDir, "bin/extensions") );
					}
					LicenseLoader licenseLoader = (LicenseLoader) classLoader.findClass("de.enough.polish.license.PolishLicenseLoader").newInstance();
					this.licensingInformation = licenseLoader.verifyLicense(licenseKeyFile);
				} catch (SecurityException e) {
					BuildException be = new BuildException("Encountered invalid license file " + licenseKeyFile.getAbsolutePath() + ": " + e.toString() );
					be.initCause( e );
					throw be;
				} catch (Exception e) {
					BuildException be = new BuildException("Unable to extract information from license file " + licenseKeyFile.getAbsolutePath() + ": " + e.toString() );
					be.initCause( e );
					throw be;

				}
			} else {
				this.licensingInformation = new HashMap();
			}
		}
		String license = (String) this.licensingInformation.get("license.name");
		if (license == null) {
			license = "GPL";
		}
		return license;
	}
	
	/**
	 * Retrieves full licensing information like "Single License for Companyname" or "GPL License".
	 * 
	 * @return the full licensing information
	 */
	private String getLicenseInfo() {
		String license = getLicense();
		if ("GPL".equals(license)) {
			return "GPL License";
		}
		// encountered a real license:
		String licenseeName = (String) this.licensingInformation.get("licensee.name");
		if (licenseeName == null) {
			licenseeName = "Unknown Licensee";
		}
		if ("Enterprise".equals(license)) {
			return "Enterprise License for " + licenseeName;
		}
		// either Single or J2ME Polish Developer Program license:
		String projectName = (String) this.licensingInformation.get("licensee.project");
		return license + " License for " + licenseeName + ", project \"" + projectName + "\"";
	}


	/**
	 * Creates an array of text files.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @param textFileManager the manager for textfiles
	 * @return an array of text-files
	 */
	protected TextFile[] getTextFiles(File baseDir, String[] fileNames, TextFileManager textFileManager ) 
	{
		
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			try {
				TextFile file = new TextFile( baseDir.getAbsolutePath(), fileName );
				textFileManager.addTextFile(file);
				if (fileName.startsWith("de")) {
					if (fileName.endsWith("StyleSheet.java")) {
						if ("de/enough/polish/ui/StyleSheet.java".equals(fileName)
							|| 	"de\\enough\\polish\\ui\\StyleSheet.java".equals(fileName)) {
							this.styleSheetSourceFile = file;
						}
					} else if (fileName.endsWith("StyleCache.java")) {
						if ("de/enough/polish/ui/StyleCache.java".equals(fileName)
								|| 	"de\\enough\\polish\\ui\\StyleCache.java".equals(fileName)) {
								this.styleCacheSourceFile = file;
						}
					} else if (fileName.endsWith("Locale.java")) {
						if ("de/enough/polish/util/Locale.java".equals(fileName)
								|| 	"de\\enough\\polish\\util\\Locale.java".equals(fileName)) {
								this.localeSourceFile = file;
						}
					} 
				}
				files[i] = file;
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to load java source [" + fileName + "]: " + e.getMessage(), e );
			}
		}
		return files;
	}

	/**
	 * Creates an array of text files and loads them from the jar-file.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @param lastModificationTime the time of the last modification of the files
	 * @param textFileManager the manager for textfiles
	 * @return an array of text-files
	 */
	protected TextFile[] getTextFiles(String baseDir, String[] fileNames, long lastModificationTime, TextFileManager textFileManager) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			TextFile file = new TextFile( baseDir, fileName, lastModificationTime, this.resourceUtil );
			textFileManager.addTextFile(file);
			if (fileName.startsWith("de")) {
				if (fileName.endsWith("StyleSheet.java")) {
					if ("de/enough/polish/ui/StyleSheet.java".equals(fileName)
						|| 	"de\\enough\\polish\\ui\\StyleSheet.java".equals(fileName)) {
						this.styleSheetSourceFile = file;
					}
				} else if (fileName.endsWith("StyleCache.java")) {
					if ("de/enough/polish/ui/StyleCache.java".equals(fileName)
							|| 	"de\\enough\\polish\\ui\\StyleCache.java".equals(fileName)) {
							this.styleCacheSourceFile = file;
					}
				} else if (fileName.endsWith("Locale.java")) {
					if ("de/enough/polish/util/Locale.java".equals(fileName)
							|| 	"de\\enough\\polish\\util\\Locale.java".equals(fileName)) {
							this.localeSourceFile = file;
					}
				} 
			}
			files[i] = file;
		}
		return files;
	}
	
	/**
	 * Selects the actual devices for which optimal applications should be generated.
	 */
	public void selectDevices() {
		if (this.deviceRequirements == null) {
			this.devices = this.deviceManager.getDevices();
			if (this.devices == null || this.devices.length == 0) {
				throw new BuildException("The [devices.xml] file does not define any devices at all - please specify a correct devices-file." );
			}
		} else {
			this.devices = this.deviceRequirements.filterDevices( this.deviceManager.getDevices() );
			if (this.devices == null || this.devices.length == 0) {
				throw new BuildException("Your device-requirements are too strict - no device fulfills them." );
			}
			Arrays.sort( this.devices );
		}
	}
	
	
	
	public void initialize( Device device, Locale locale ) {
		this.extensionManager.preInitialize(device, locale);
		// intialise the environment
		this.environment.initialize(device, locale);
		device.setEnvironment( this.environment );
		this.environment.set("ant.project", getProject() );
		// set variables and symbols:
		//this.environment.setSymbols( device.getFeatures() );
		//this.environment.setVariables( device.getCapabilities() );
		this.environment.addVariable( "polish.identifier", device.getIdentifier() );
		this.environment.addVariable( "polish.name", device.getName() );
		this.environment.addVariable( "polish.vendor", device.getVendorName() );
		this.environment.addVariable( "polish.version", this.infoSetting.getVersion() );
		// set localization-variables:
		if (locale != null || (this.localizationSetting != null && this.localizationSetting.isDynamic())) {
			if (locale == null) {
				locale = this.localizationSetting.getDefaultLocale().getLocale();
				this.environment.addSymbol("polish.i18n.useDynamicTranslations");
				this.environment.setLocale( locale );
			}
			this.environment.addVariable("polish.SupportedLocales", this.localizationSetting.getSupportedLocalesAsString( this.environment ) );
			this.environment.addVariable("polish.locale", locale.toString() );
			this.environment.addVariable("polish.language", locale.getLanguage() );
			String country = locale.getCountry();
			if ( country == null || country.length() == 0 ) {
				this.environment.removeVariable( "polish.country" );
			} else {
				this.environment.addVariable("polish.country", country );
			}
		}
		// set info-variables:
		String jarName = this.infoSetting.getJarName();
		if (jarName != null) {
			jarName = this.environment.writeProperties( jarName, true );
			this.environment.addVariable( "polish.jarName", jarName );
		}
				
		// enable the support for the J2ME Polish GUI, part 1: 
		// check if a preprocessing variable is set for using the Polish GUI:
		String usePolishGuiVariable = this.environment.getVariable("polish.usePolishGui");
		boolean usePolishGui = (this.buildSetting.usePolishGui()
		  && ( device.supportsPolishGui() || this.buildSetting.alwaysUsePolishGui()))
		  || (( "true".equals( usePolishGuiVariable) || "yes".equals( usePolishGuiVariable ) || "always".equals( usePolishGuiVariable )) );
		  
		if ( usePolishGui) {
			this.environment.addSymbol("polish.usePolishGui");
		}
		
		// set conditional variables:
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		Project antProject = getProject();
		Variable[] vars = this.variables.getVariables( this.environment );
		for (int i = 0; i < vars.length; i++) {
			Variable var = vars[i];
			this.environment.addVariable(var.getName(), var.getValue() );
		}
		ClassSetting dojaSetting = this.buildSetting.getDojaClassSetting();
		if (dojaSetting != null && dojaSetting.isActive( evaluator, antProject ) ) {
			this.environment.addVariable("polish.classes.iapplication", dojaSetting.getClassName() );
		}
		ClassSetting mainSetting = this.buildSetting.getMainClassSetting();
		if (mainSetting != null && mainSetting.isActive( evaluator, antProject ) ) {
			this.environment.addVariable("polish.classes.main", mainSetting.getClassName() );
		}

		// now set the full-screen-settings:
		String value = this.environment.getVariable("polish.FullScreen");
		if (value != null) {
			if ("menu".equalsIgnoreCase(value)) {
				this.environment.addSymbol("polish.useMenuFullScreen");
				this.environment.addSymbol("polish.useFullScreen");					
			} else if ("yes".equalsIgnoreCase( value ) || "true".equalsIgnoreCase(value)) {
				this.environment.addSymbol("polish.useFullScreen");					
			}
		} else {
			FullScreenSetting fullScreenSetting = this.buildSetting.getFullScreenSetting();
			if (fullScreenSetting != null) {
				if (fullScreenSetting.isMenu()) {
					this.environment.addSymbol("polish.useMenuFullScreen");
					this.environment.addSymbol("polish.useFullScreen");
				} else if (fullScreenSetting.isEnabled()) {
					this.environment.addSymbol("polish.useFullScreen");
				}
			}
		}
		
		jarName = this.environment.getVariable( "polish.jarName" );
		if (jarName != null) {
			String destPath = this.buildSetting.getDestDir( this.environment ).getAbsolutePath() + File.separatorChar;
			String jarPath = destPath + jarName;
			this.environment.addVariable( "polish.jarPath", jarPath );
			int dotIndex = jarName.lastIndexOf('.');
			if (dotIndex == -1) {
				// invalid JAR name
				throw new BuildException("Invalid JAR name \"" + jarName + "\" defined - check your \"polish.jarName\" setting. Usually a \".jar\" is missing.");
			}
			String jadName = jarName.substring(0, dotIndex ) + ".jad";
			this.environment.addVariable( "polish.jadName", jadName );
			String jadPath = destPath + jadName;
			this.environment.addVariable( "polish.jadPath", jadPath );
		}

		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getJadAttributes( this.environment );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute attribute = jadAttributes[i];
			this.environment.addVariable( attribute.getName(), attribute.getValue() );
		}
		String copyright = this.infoSetting.getCopyright();
		if (copyright != null) {
			this.environment.addVariable( "MIDlet-Copyright", copyright );
		}
				
		// set support for the J2ME Polish GUI, part 2:
		usePolishGuiVariable = this.environment.getVariable("polish.usePolishGui");
		if (usePolishGuiVariable != null) {
			if ("true".equalsIgnoreCase(usePolishGuiVariable) || "yes".equalsIgnoreCase(usePolishGuiVariable) || "always".equalsIgnoreCase(usePolishGuiVariable)) {
				usePolishGui = true;					
				this.environment.addSymbol("polish.usePolishGui");
			} else {
				usePolishGui = false;					
				this.environment.removeSymbol("polish.usePolishGui");
			}
		}
		
		// set the temporary build path used for preprocessing, compilation, preverification, etc:
		String deviceSpecificBuildPath = File.separatorChar + device.getVendorName() 
				+ File.separatorChar + device.getName();
		deviceSpecificBuildPath = deviceSpecificBuildPath.replace(' ', '_' );
		String buildPath = this.buildSetting.getWorkDir().getAbsolutePath() + deviceSpecificBuildPath;
		if (locale != null) {
			buildPath += File.separatorChar + locale.toString();
		}
		device.setBaseDir( buildPath );
		this.environment.addVariable("polish.base.dir", buildPath );
		String sourceDir = buildPath + File.separatorChar + "source";
		device.setSourceDir(sourceDir);
		this.environment.addVariable("polish.sourcedir", sourceDir);
		this.environment.addVariable("polish.source.dir", sourceDir);
		File resourceDir = new File( buildPath + File.separatorChar + "resources" );
		device.setResourceDir( resourceDir );
		this.environment.addVariable("polish.resources.dir", sourceDir);


		
		// okay, now initialize extension manager:
		this.extensionManager.initialize(device, locale, this.environment);
		
		// check if there is an active obfuscator with the "useDefaultPackage" option:
		this.useDefaultPackage =  "true".equals( this.environment.getVariable("polish.useDefaultPackage"));
		if (this.useDefaultPackage && this.environment.hasSymbol("polish.api.j2mepolish")) {
			this.useDefaultPackage = false;
			this.environment.removeVariable("polish.useDefaultPackage");
			this.environment.removeSymbol("polish.useDefaultPackage");
		}
		this.preprocessor.setUseDefaultPackage( this.useDefaultPackage );
		// adjust polish.classes.ImageLoader in case the default package is used:
		if (this.useDefaultPackage) {
			String imageLoaderClass = this.environment.getVariable("polish.classes.ImageLoader");
			if (imageLoaderClass != null) {
				int classStartIndex = imageLoaderClass.lastIndexOf('.');
				if (classStartIndex != -1) {
					imageLoaderClass = imageLoaderClass.substring( classStartIndex + 1 );
					this.environment.addVariable("polish.classes.ImageLoader", imageLoaderClass );
				}
			}
		}

		// add primary midlet-class-definition:
		String[] midletClassNames = this.buildSetting.getMidletClassNames( this.useDefaultPackage, this.environment );
		if (midletClassNames.length > 0) {
			this.environment.addVariable("polish.midlet.class", midletClassNames[0] );
			for (int i = 0; i < midletClassNames.length; i++) {
				this.environment.addVariable("polish.classes.midlet-" + (i+1), midletClassNames[i] );			
			}
		}
		
		
		// check settings:
		String version = this.environment.getVariable( "MIDlet-Version");
		if (version != null) {
			String[] versionChunks = StringUtil.split( version, '.' );
			for (int i = 0; i < versionChunks.length; i++) {
				String versionChunk = versionChunks[i];
				int versionNumber = Integer.parseInt( versionChunk );
				if (versionNumber > 99) {
					if ( this.environment.hasSymbol("polish.Bugs.VersionNumberMustNotExceed99") ) {
						throw new BuildException("Invalid MIDlet-Version [" + version + "]: each version-part must not exceed 99 on the " + device.getIdentifier() + "." );
					} else {
						System.out.println("WARNING: the MIDlet-Version [" + version + "] contains a number greater than 99, This can cause problems on some devices.");
					}
				}
			}
		}
		
		this.extensionManager.postInitialize(device, locale, this.environment);
		
		
		
		
		this.environment.set( "polish.home", this.polishHomeDir );
		this.environment.set( "polish.apidir", this.buildSetting.getApiDir() );
		
		// set the absolute path to polish.home  - this minimizes problems resolving paths
		// relative to polish.home (since the polish.home can be relative
		// to the build.xml script).
		this.environment.addVariable( "polish.home", this.polishHomeDir.getAbsolutePath() );
		
		//TODO call initialialize on all active extensions
		// add settings of active postcompilers:
		// let postcompilers adjust the bootclasspath:
		/*
		if (this.doPostCompile) {
			PostCompiler[] compilers = getActivePostCompilers();
			for (int i = 0; i < compilers.length; i++) {
				PostCompiler postCompiler = compilers[i];
				postCompiler.addPreprocessingSettings(device, this.preprocessor);
			}
		}
		*/
	}
	
	/**
	 * Assembles all resources for the specified device and locale and copies them to a tempoary folder.
	 * This step is done before the preprocessing phase, so the programmer can access values
	 * like image-widths during the preprocessing step already.
	 * 
	 * @param device the device
	 * @param locale the locale, can be null
	 */
	protected void assembleResources( Device device, Locale locale ) {
		System.out.println("assembling resources for device [" +  device.getIdentifier() + "]." );
		File resourceDir = device.getResourceDir(); 
		try {
			// copy resources:
			this.resourceManager.copyResources(resourceDir, device, locale);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to assemble resources: " + e.toString(), e );
		}				
	}
	
	/**
	 * Preprocesses the source code for all devices.
	 * 
	 * @param device The device for which the preprocessing should be done.
	 * @param locale the current locale, can be null
	 */
	protected void preprocess( Device device, Locale locale ) {
		System.out.println("preprocessing for device [" +  device.getIdentifier() + "]." );
		try {

			this.numberOfChangedFiles = 0;
			String targetDir = device.getSourceDir();
			
			notifyPolishBuildListeners( PolishBuildListener.EVENT_PREPROCESS_SOURCE_DIR, new File( targetDir ) );
			// initialise the preprocessor (other initialisation is done in the initialized() method):
			this.preprocessor.setTargetDir( targetDir );
			long lastLocaleModification = 0;
			TranslationManager translationManager = null;
			// set localization-variables:
			if (locale != null || (this.localizationSetting != null && this.localizationSetting.isDynamic())) {
				LocaleSetting setting = this.currentLocaleSetting;
				if (setting == null) {
					setting = this.localizationSetting.getDefaultLocale();
				}
				// load localized messages, this also sets localized variables automatically:
				translationManager = this.resourceManager.getTranslationManager(device, setting );
				this.translationPreprocessor.setTranslationManager( translationManager );
				lastLocaleModification = translationManager.getLastModificationTime();
			}
			BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
			Project antProject = getProject();
			// add custom preprocessors
			CustomPreprocessor[] preprocessors = (CustomPreprocessor[]) this.customPreprocessors.toArray( new CustomPreprocessor[ this.customPreprocessors.size() ] );
			this.preprocessor.clearCustomPreprocessors();
			for (int i = 0; i < preprocessors.length; i++) {
				CustomPreprocessor processor = preprocessors[i];
				PreprocessorSetting setting = processor.getSetting();
				if (setting == null || setting.isActive( evaluator, antProject ) ) {
					this.preprocessor.addCustomPreprocessor( processor );
				}
			}
			// add autostart preprocessors:
			Extension[] autoStartPreprocessors = this.extensionManager.getAutoStartExtensions( ExtensionManager.TYPE_PREPROCESSOR, device, locale, this.environment );
			for (int i = 0; i < autoStartPreprocessors.length; i++) {
				Extension extension = autoStartPreprocessors[i];
				this.preprocessor.addCustomPreprocessor( (CustomPreprocessor) extension );
			}
			
			// get the last modfication time of the build.xml file
			// so that it can be checked whether there are any changes at all:
			File buildXml = new File( getProject().getBaseDir().getAbsolutePath() 
						+ File.separatorChar + "build.xml" );
			long buildXmlLastModified = buildXml.lastModified();
			// check if the polish gui is used at all:
			boolean usePolishGui = this.environment.hasSymbol("polish.usePolishGui");
			//this.preprocessor.setUsePolishGui(usePolishGui);
			long lastCssModification = lastLocaleModification;
			StyleSheet cssStyleSheet = null;
			if (usePolishGui) {
				// read CSS files:
				cssStyleSheet = this.resourceManager.loadStyleSheet( device, locale, this.preprocessor, this.environment );
				if (cssStyleSheet.lastModified() > lastLocaleModification) {
					lastCssModification = cssStyleSheet.lastModified();
				}
			}
			this.preprocessor.setSyleSheet( cssStyleSheet, device );
			this.preprocessor.notifyDevice(device, usePolishGui);
			this.preprocessor.notifyLocale( locale );
			// preprocess each source file:
			for (int i = 0; i < this.sourceSettings.length; i++) {
				SourceSetting setting = this.sourceSettings[i];
				if (setting.isActive(evaluator, getProject())) {
					File sourceDir = setting.getDir();
					//System.out.println("Preprocessing source dir [" + sourceDir.getAbsolutePath() + "]");
					TextFile[] files = this.sourceFiles[i];
					processSourceDir(sourceDir, files, locale, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, false);
				}
			} // for each source folder
			this.preprocessor.notifyPolishPackageStart();
			boolean deviceSupportsJ2mePolishApi = this.environment.hasSymbol("polish.api.j2mepolish");
			if (!deviceSupportsJ2mePolishApi) {
				// now process the J2ME package files:
				processSourceDir(this.polishSourceDir, this.polishSourceFiles, locale, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, true);
			}
			// notify preprocessor about the end of preprocessing:
			this.preprocessor.notifyDeviceEnd( device, usePolishGui );
			
			
			// now all files have been preprocessed.
			// Now the StyleSheet.java file needs to be written,
			// but only when the polish GUI should be used:
			File baseDirectory = new File( targetDir );
			if (usePolishGui) {
				// check if the CSS declarations have changed since the last run:
				TextFile stylesFile = this.styleSheetSourceFile;
				if (deviceSupportsJ2mePolishApi) {
					stylesFile = this.styleCacheSourceFile;
				}
				File targetFile =  stylesFile.getTargetFile( baseDirectory, this.useDefaultPackage );
					//new File( targetDir + File.separatorChar + this.styleSheetSourceFile.getFilePath() );				
				boolean cssIsNew = this.lastRunFailed
					|| (!targetFile.exists())
					|| ( lastCssModification > targetFile.lastModified() )
					|| ( buildXmlLastModified > targetFile.lastModified() );
				if (cssIsNew) {
					//System.out.println("CSS is new and the style sheet will be generated.");
					if (!deviceSupportsJ2mePolishApi && this.styleSheetCode == null) {
						// the style sheet has not been preprocessed:
						this.styleSheetCode = new StringList( stylesFile.getContent() );
						String className = "de.enough.polish.ui.StyleSheet";
						this.preprocessor.preprocess( className, this.styleSheetCode );
					} else if (deviceSupportsJ2mePolishApi && this.styleCacheCode == null ){
						this.styleCacheCode = new StringList( stylesFile.getContent() );
						String className = "de.enough.polish.ui.StyleCache";
						this.preprocessor.preprocess( className, this.styleCacheCode );
					}
					StringList styleCode = this.styleSheetCode;
					if (deviceSupportsJ2mePolishApi) {
						styleCode = this.styleCacheCode;
					}
					// now insert the CSS information for this device
					// into the StyleSheet.java source-code:
					CssConverter cssConverter;
					String cssConverterClassName = this.environment.getVariable( "polish.classes.CssConverter" );
					if ( cssConverterClassName != null ) { 
						try {
							cssConverter = (CssConverter) Class.forName( cssConverterClassName ).newInstance();
							System.out.println("Using CSS Converter [" + cssConverterClassName + "]...");
						} catch (Exception e) {
							e.printStackTrace();
							throw new BuildException("Unable to initialize CSS converter [" + cssConverterClassName + "]: " + e.toString(), e );
						}
					} else {
						// use default CSS converter
						cssConverter = new CssConverter();
					}
					cssConverter.setAttributesManager( this.cssAttributesManager );
					
					styleCode.reset();
					if (!this.environment.hasSymbol("polish.LibraryBuild") ) {
						cssConverter.convertStyleSheet(styleCode, 
								this.preprocessor.getStyleSheet(),
								device,
								this.preprocessor,
								this.environment );
					}
					//this.styleSheetSourceFile.saveToDir(targetDir, this.styleSheetCode.getArray(), false );
					if (!deviceSupportsJ2mePolishApi && this.useDefaultPackage) {
						this.styleSheetCode.reset();
						this.importConverter.processImports(true, device.isMidp1(), this.styleSheetCode, device, this.preprocessor);
						this.styleSheetCode.reset();
						this.importConverter.removeDirectPackages( this.styleSheetCode, this.preprocessor.getTextFileManager() );
					}
					FileUtil.writeTextFile(targetFile, styleCode.getArray());
					this.numberOfChangedFiles++;
				//} else {
				//	System.out.println("CSSS is not new - last CSS modification == " + lastCssModification + " <= StyleSheet.java.lastModified() == " + targetFile.lastModified() );
				}
				
			}
			// now check if the de.enough.polish.util.Locale.java needs to be rewritten:
			//if (locale != null) {
				File targetFile =  this.localeSourceFile.getTargetFile( baseDirectory, this.useDefaultPackage );
				//File targetFile = new File( targetDir + File.separatorChar + this.localeSourceFile.getFilePath() );				
				boolean localizationIsNew = (!targetFile.exists())
					|| ( lastLocaleModification > targetFile.lastModified() )
					|| ( buildXmlLastModified > targetFile.lastModified() );
				if (localizationIsNew) {
					//System.out.println("Localization is new and the Locale.java will be generated.");
					if (this.localeCode == null) {
						// the style sheet has not been preprocessed:
						this.localeCode = new StringList( this.localeSourceFile.getContent() );
						String className = "de.enough.polish.util.Locale";
						this.preprocessor.preprocess( className, this.localeCode );
					}
					
					// now insert the localozation data for this device
					// into the Locale.java source-code:					
					translationManager.processLocaleCode( this.localeCode );
					if (this.useDefaultPackage) {
						this.localeCode.reset();
						this.importConverter.processImports(true, device.isMidp1(), this.localeCode, device, this.preprocessor);
					}
					FileUtil.writeTextFile(targetFile, this.localeCode.getArray());
					this.numberOfChangedFiles++;
				}
			//}
			device.setNumberOfChangedFiles( this.numberOfChangedFiles );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (BuildException e) {
			throw e;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BuildException( e.getMessage() );
		}
	}
	
	protected void processSourceDir( File sourceDir, 
									TextFile[] files, 
									Locale locale,
									Device device, 
									boolean usePolishGui, 
									String targetDir, 
									long buildXmlLastModified,  
									long lastCssModification,
									boolean isInPolishPackage)
	throws IOException, BuildException
	{
		
		this.environment.addVariable( "polish.source", sourceDir.getAbsolutePath() );
		File baseDirectory = new File( targetDir );
		//System.out.println("current source dir: " + sourceDir + " -  locale: " + locale );
		
		// preprocess each file in that source-dir:
		for (int j = 0; j < files.length; j++) {
			TextFile file = files[j];
			//System.out.println("processing [" + file.getFileName() + "]");
			// check if file needs to be preprocessed at all:
			long sourceLastModified = file.lastModified();
			File targetFile = file.getTargetFile(baseDirectory, this.useDefaultPackage );
			long targetLastModified = targetFile.lastModified();
			// preprocess this file, but only when there can
			// be changes at all - this could be when
			// 1. The preprocessed file does not yet exists
			// 2. The source file has been modified since the last run
			// 3. The build.xml has been modified since the last run
			// 4. One of the polish.css files has been modified since the last run 
			// when only the CSS files have changed
			boolean saveInAnyCase =  ( !targetFile.exists() )
					 || ( sourceLastModified > targetLastModified )
					 || ( buildXmlLastModified > targetLastModified )
					 || ( this.preprocessor.isInPreprocessQueue( file.getFilePath() ) ); 
			boolean preprocess = ( saveInAnyCase )
					 || ( lastCssModification > targetLastModified)
					 || ( this.lastRunFailed );
			if (   preprocess ) {
				// preprocess this file:
				StringList sourceCode = new StringList( file.getContent() );
				// generate the class-name from the file-name:
				String className = file.getFilePath();
				if (className.endsWith(".java")) {
					className = className.substring(0, className.length() - 5 );
					// in a jarfile the files always have a '/' as a path-seperator:
					className = StringUtil.replace(className, '/', '.' );
				}
				className = StringUtil.replace(className, File.separatorChar, '.' );
				int result = this.preprocessor.preprocess( className, sourceCode );
				if (usePolishGui && result != Preprocessor.SKIP_FILE) {
					// set the StyleSheet.display variable in all MIDlets:
					String adjustedClassName = className;
					if (this.useDefaultPackage) {
						int lastDotPos = className.lastIndexOf('.');
						if (lastDotPos != -1) {
							adjustedClassName = className.substring( lastDotPos + 1 );
						}
					}
					if ( (this.midletClassesByName.get( adjustedClassName ) != null) ) {
						sourceCode.reset();
						insertDisplaySetting( className, sourceCode );
						sourceCode.reset();
						insertExitSetting( className, sourceCode );
					}
				}
				// only think about saving when the file should not be skipped 
				// and when it is not the StyleSheet.java file:
				if (file == this.styleSheetSourceFile ) {
					this.styleSheetCode = sourceCode;
				} else if (file == this.localeSourceFile) {
					//System.out.println("setting locale code for locale " + locale );
					this.localeCode = sourceCode;
				} else if (result == Preprocessor.SKIP_FILE ) {
					//System.out.println("skipping " + file.getClassName());
					if (targetFile.exists()) {
						boolean deleted = FileUtil.delete(targetFile);
						if (!deleted) {
							System.out.println("Warning: unable to deleted unused source file [" + targetFile.getAbsolutePath() + ": please make a clean rebuild.");
						}
					}
				} else {
					//if (!isInPolishPackage || this.useDefaultPackage ) {
						// sourceCode.reset();
						// now replace the import statements:
						// boolean changed = this.importConverter.processImports(usePolishGui, device.isMidp1(), sourceCode, device, this.preprocessor );
						//if (changed) {
						//	result = Preprocessor.CHANGED;
						// }
					//}
					// save modified file:
					if ( ( saveInAnyCase ) 
					  || (result == Preprocessor.CHANGED) ) 
					{
						//System.out.println( "preprocessed [" + className + "]." );
						//file.saveToDir(targetDir, sourceCode.getArray(), false );
						FileUtil.writeTextFile(targetFile, sourceCode.getArray() );
						this.numberOfChangedFiles++;
					//} else {
					//	System.out.println("not saving " + file.getFileName() );
					}
				//} else {
				//	System.out.println("Skipping file " + file.getFileName() );
				}
			} // when preprocessing should be done.
		} // for each file
		
	}
	
	/**
	 * Sets the StyleSheet.display variable in a MIDlet class.
	 * 
	 * @param className the name of the class
	 * @param sourceCode the source code
	 * @throws BuildException when the startApp()-method could not be found
	 */
	protected void insertDisplaySetting( String className, StringList sourceCode ) {
		// at first try to find the startApp method:
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			Matcher matcher = START_APP_PATTERN.matcher(line);
			if (matcher.find()) {
				int lineIndex = sourceCode.getCurrentIndex();
				while ((line.indexOf('{') == -1) && (sourceCode.next()) ) {
					line = sourceCode.getCurrent();
				}
				if (!sourceCode.next()) {
					throw new BuildException("Unable to process MIDlet [" + className + "]: startApp method is not opened with '{': line [" + (++lineIndex) + "].");
				}
				line  = sourceCode.getCurrent();
				String displayVar;
				if (this.useDefaultPackage) {
					displayVar = "StyleSheet.display";
				} else {
					displayVar = "de.enough.polish.ui.StyleSheet.display";
				}
				/*
				System.out.println( className + ": changing line [" + line + "] to ["
						+ displayVar + " = javax.microedition.lcdui.Display.getDisplay( this );"
						+ line + "]"
						);
				*/
				//TODO use import() preprocessing directive for such stuff!!
				String displayClassName = "javax.microedition.lcdui.Display";
				if ( this.environment.hasSymbol("polish.blackberry")) {
					if (this.useDefaultPackage) {
						displayClassName = "Display";
					} else {
						displayClassName = "de.enough.polish.blackberry.ui.Display";
					}
				} else if ( this.environment.hasSymbol("polish.doja")) {
					if (this.useDefaultPackage) {
						displayClassName = "Display";
					} else {
						displayClassName = "de.enough.polish.doja.ui.Display";
					}
				}
				sourceCode.setCurrent( displayVar + " = " + displayClassName + ".getDisplay( this );"
						+ line );
				return;
			}
		}
		System.out.println(START_APP_PATTERN.pattern());
		throw new BuildException("Unable to find startApp method in MIDlet [" + className + "].");

	}
	
	/**
	 * Sets the StyleSheet.display variable in a MIDlet class.
	 * 
	 * @param className the name of the class
	 * @param sourceCode the source code
	 * @throws BuildException when the startApp()-method could not be found
	 */
	protected void insertExitSetting( String className, StringList sourceCode ) {
		if (!this.environment.hasSymbol("polish.debugEnabled")) {
			return;
		}
		// at first try to find the startApp method:
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			Matcher matcher = DESTROY_APP_PATTERN.matcher(line);
			if (matcher.find()) {
				int lineIndex = sourceCode.getCurrentIndex();
				while ((line.indexOf('{') == -1) && (sourceCode.next()) ) {
					line = sourceCode.getCurrent();
				}
				String debugExit;
				if (this.useDefaultPackage) {
					debugExit = "Debug.exit();";
				} else {
					debugExit = "de.enough.polish.util.Debug.exit();";
				}
				int closePos = line.indexOf('}');
				if (closePos != -1 ) {
					// the destroyApp() method is empty!
					// Now include the exit call between those parentheses:
					sourceCode.setCurrent( line.substring(0, closePos) + debugExit + line.substring( closePos ) );
				} else {
					if (!sourceCode.next()) {
						throw new BuildException("Unable to process MIDlet [" + className + "]: destroyApp method is not opened with '{': line [" + (++lineIndex) + "].");
					}
					line  = sourceCode.getCurrent();
					sourceCode.setCurrent( debugExit + line );
				}
				return;
			}
		}
		System.out.println(DESTROY_APP_PATTERN.pattern());
		throw new BuildException("Unable to find destroyApp method in MIDlet [" + className + "].");

	}


	/**
	 * Compiles the source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 * @param locale the current localization
	 */
	public void compile( Device device, Locale locale ) {
		// setting target directory:
		String targetDirName = device.getBaseDir() + File.separatorChar + "classes";
		File targetDir; // = new File( targetDirName );
		if ( this.buildSetting.isInCompilerMode()) { //  && ! this.buildSetting.doPreverifyInCompilerMode() && !this.doObfuscate is not needed
			targetDir = this.buildSetting.getCompilerDestDir();
			targetDirName = targetDir.getAbsolutePath();
		} else {
			targetDir = new File( targetDirName );
		}
		device.setClassesDir( targetDirName );
		
		if (device.getNumberOfChangedFiles() == 0 && !this.lastRunFailed) {
			System.out.println("nothing to compile for device [" +  device.getIdentifier() + "]." );
			return;			
		}
		
		Project antProject = getProject();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		// add binary class files, if there are any:
		if (this.binaryLibrariesUpdated || (!targetDir.exists() && this.binaryLibraries != null) ) {
			System.out.println("copying binary libraries to [" + targetDirName + "]...");
			LibrarySetting[] settings = this.binaryLibraries.getLibraries();
			for (int i = 0; i < settings.length; i++) {
				LibrarySetting setting = settings[i];
				if (setting.isActive(evaluator, antProject)) {
					setting.copyFromCache( targetDir );
				}
			}
			/*
			try {
				FileUtil.copyDirectoryContents( this.binaryLibrariesDir, targetDirName, true );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to copy binary class files: " + e.toString() + ". Please report this error to j2mepolish@enough.de.", e );
			}
			*/
		}
		// invoking all precompilers:
		precompile( device, locale );
		System.out.println("compiling for device [" +  device.getIdentifier() + "]." );
		// init javac task:
		CompilerTask compiler = this.buildSetting.getCompiler(evaluator );
		compiler.setProject( antProject );
		if (!compiler.isTaskNameSet()) {
			compiler.setDirectTaskName(getTaskName() + "-javac-" + device.getIdentifier() );
		}
		//javac.target=1.1 or javac.target=1.2 is needed for the preverification:
		if (!compiler.isTargetSet()) {
			//System.out.println("setting javac-target " + getJavacTarget() );
			compiler.setDirectTarget( getJavacTarget() );
		}
		// -source == 1.3 is apparently always needed for J2SE 1.5
		if (!compiler.isSourceSet()) {
			//System.out.println("setting javac-source " + getJavacSource() );
			compiler.setDirectSource( getJavacSource() );
		}
		//if (this.buildSetting.isDebugEnabled() && !compiler.isDebugSet()) {
		// always add debugging settings, since the obfuscator removes them anyhow:
		compiler.setDirectDebug(true);
		//}
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		if (!compiler.isDestDirSet()) {
			compiler.setDirectDestdir( targetDir );
		}
		if (!compiler.isSourceDirSet()) {
			compiler.setDirectSrcdir(new Path( getProject(),  device.getSourceDir() ) );
		}
		//javac.setSourcepath(new Path( getProject(),  "" ));
		String classPath = device.getClassPath();
		String completePath = classPath; // just used for printing out the classpath in case of an error
		if (!compiler.isBootClassPathSet()) {
			Path bootClassPath = new Path( antProject, device.getBootClassPath() );
			// let postcompilers adjust the bootclasspath:
			if (this.doPostCompile) {
				String path = bootClassPath.toString();
				String originalBootClassPath = path;
				PostCompiler[] compilers = getActivePostCompilers();
				for (int i = 0; i < compilers.length; i++) {
					PostCompiler postCompiler = compilers[i];
					path = postCompiler.verifyBootClassPath(device, path);
					if ( classPath != null ) {
						classPath = postCompiler.verifyClassPath(device, classPath);
					}
				}
				if (path != originalBootClassPath) {
					bootClassPath = new Path( antProject, path );
				}
			}
			//device.setBootClassPath( bootClassPath.toString() );
			compiler.setDirectBootclasspath( bootClassPath );
			if (completePath != null) {
				completePath = bootClassPath.toString() + File.pathSeparatorChar + completePath;
			} else {
				completePath = bootClassPath.toString();				
			}
		//} else {
		//	device.setBootClassPath( compiler.getBootclasspath().toString() );
		}
		if ( !compiler.isClassPathSet()) {
			if (classPath != null) {
				compiler.setDirectClasspath( new Path(antProject, classPath ) );
			} else {
				compiler.setDirectClasspath( new Path(antProject, "" ) );
			}
		}
		// start compile:
		if (this.polishLogger != null) {
			this.polishLogger.setCompileMode( true );
		}
		String taskName = getTaskName();
		try {
			//compiler.setTaskName("javac");
			setTaskName("javac"  );
			compiler.execute();
		} catch (BuildException e) {
			if (this.polishLogger != null) {
				this.polishLogger.setCompileMode(false);
				if (this.polishLogger.isInternalCompilationError()) {
					System.out.println("An internal class of J2ME Polish could not be compiled. " +
							"Please try a clean rebuild by either calling \"ant clean j2mepolish\"" +
							" or by removing the working directory \"" 
							+ this.buildSetting.getWorkDir().getAbsolutePath() + "\".");
					System.out.println("When an API-class was not found, you might need " +
							"to define where to find the device-APIs. Following classpath " +
							"has been used: [" + completePath + "].");
					throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
				} else {
					System.out.println("When an API-class was not found, you might need to define where to find the device-APIs. Following classpath has been used: [" + completePath + "].");
					throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
				}
			} else {
				System.out.println("If an error occured in the J2ME Polish packages, please try a clean rebuild - e.g. [ant clean] and then [ant].");
				System.out.println("Alternatively you might need to define where to find the device-APIs. Following classpath has been used: [" + completePath + "].");
				throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
			}
		} finally {
			setTaskName( taskName );
		}
		if (this.polishLogger != null) {
			this.polishLogger.setCompileMode( false );
		}
	}
	
	private String getJavacSource() {
		String source = this.environment.getVariable("javac.source");
		if (source == null) {
			source = this.sourceCompatibility; 
		}
		return source;
	}

	private String getJavacTarget() {
		String jTarget = this.environment.getVariable("javac.target");
		if (jTarget == null) {
			jTarget = this.javacTarget;
		}
		return jTarget;
	}

	/**
	 * Retrieves all currently active post compilers.
	 * 
	 * @return an array of all active postcompiler, can be empty but not null.
	 */
	protected PreCompiler[] getActivePreCompilers() {
		if (this.preCompilers == null || this.preCompilers.length == 0) {
			return new PreCompiler[ 0 ];
		}
		ArrayList list = new ArrayList();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		Project antProject = getProject();
		for (int i = 0; i < this.preCompilers.length; i++) {
			PreCompiler preCompiler = this.preCompilers[i];
			if (preCompiler.getSetting().isActive(evaluator, antProject)) {
				list.add( preCompiler );
			}
		}
		return (PreCompiler[]) list.toArray( new PreCompiler[ list.size() ]);
	}

	/**
	 * Is called by the compile-target after the binary libraries have been copied.
	 * 
	 * @param device The device for which the obfuscation should be done.
	 * @param locale the current localization
	 */
	protected void precompile(Device device, Locale locale) {
		// deactivate logging:
		String className;
		if (this.useDefaultPackage) {
			className = "Debug";
		} else {
			className = "de.enough.polish.util.Debug";
		}
		ClassLoader classLoader = device.getClassLoader();
		try {
			Class debugClass = classLoader.loadClass(className);
			ReflectionUtil.setStaticField( debugClass, "suppressMessages", Boolean.TRUE );
		} catch (Exception e) {
			//System.err.println("Precompile: Unable to deactivate logging in Debug class: " + e.toString() );
			//e.printStackTrace();
		}
		// execute active precompilers:
		PreCompiler[] compilers = getActivePreCompilers();
		for (int i = 0; i < compilers.length; i++) {
			PreCompiler compiler = compilers[i];
			compiler.execute( device, locale, this.environment );
		}
		this.extensionManager.preCompile(device, locale, getEnvironment());
	}

	/**
	 * Retrieves all currently active post compilers.
	 * 
	 * @return an array of all active postcompiler, can be empty but not null.
	 */
	protected PostCompiler[] getActivePostCompilers() {
		if (this.postCompilers == null || this.postCompilers.length == 0) {
			return new PostCompiler[ 0 ];
		}
		ArrayList list = new ArrayList();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		Project antProject = getProject();
		for (int i = 0; i < this.postCompilers.length; i++) {
			PostCompiler postCompiler = this.postCompilers[i];
			if (postCompiler.getSetting().isActive(evaluator, antProject)) {
				list.add( postCompiler );
			}
		}
		return (PostCompiler[]) list.toArray( new PostCompiler[ list.size() ]);
	}
	
	/**
	 * Postcompiles the project for the given device.
	 * 
	 * @param device The device for which the obfuscation should be done.
	 * @param locale the current localization
	 */
	protected void postCompile( Device device, Locale locale ) {
		// deactivate logging:
		String className;
		if (this.useDefaultPackage) {
			className = "Debug";
		} else {
			className = "de.enough.polish.util.Debug";
		}
		ClassLoader classLoader = device.getClassLoader();
		try {
			Class debugClass = classLoader.loadClass(className);
			ReflectionUtil.setStaticField( debugClass, "suppressMessages", Boolean.TRUE );
		} catch (Exception e) {
			//System.err.println("Postcompile: Unable to deactivate logging in Debug class: " + e.toString() );
			//e.printStackTrace();
			// ignore - this is just because there is no compiled Debug class yet...
		}
		
		// execute active postcompilers:
		PostCompiler[] compilers = getActivePostCompilers();
		for (int i = 0; i < compilers.length; i++) {
			PostCompiler postCompiler = compilers[i];
			postCompiler.execute( device, locale, this.environment );
		}
		this.extensionManager.postCompile( device, locale, this.environment );
	}


	/**
	 * Obfuscates the compiled source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 * @param locale the current localization
	 */
	protected void obfuscate( Device device, Locale locale ) {
		if (this.polishLogger != null) {
			this.polishLogger.setObfuscateMode( true );
		}
		Path bootPath = new Path( getProject(), device.getBootClassPath() );
		// create the initial jar-file: only include to class files,
		// for accelerating the obfuscation:
		File sourceFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "source.jar");
		//long time = System.currentTimeMillis();
		try {
			JarUtil.jar( new File( device.getClassesDir()), sourceFile, false );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to prepare the obfuscation-jar from [" + sourceFile.getAbsolutePath() + "] to [" + device.getClassesDir() + "]: " + e.getMessage(), e );
		}
		//System.out.println("Jaring took " + ( System.currentTimeMillis() - time) + " ms.");	
		
		File destFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "dest.jar");
		
		// start the obfuscation:
		int maxIndex = this.obfuscators.length - 1;
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		Project antProject = getProject();
		boolean hasBeenObfuscated = false;
		for (int i=0; i <= maxIndex ; i++ ) {
			Obfuscator obfuscator = this.obfuscators[i];
			if (obfuscator.getSetting().isActive(evaluator, antProject) ) {
				if (!hasBeenObfuscated) {
					System.out.println("obfuscating for device [" + device.getIdentifier() + "].");
					hasBeenObfuscated = true;
				}
				obfuscator.obfuscate(device, sourceFile, destFile, this.preserveClasses, bootPath );
				if ( i != maxIndex ) {
					sourceFile = destFile;
					destFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
							+ File.separatorChar + "dest" + (i + 1) + ".jar");
				}
			}
		}
		
		
		//time = System.currentTimeMillis();
		//unjar destFile to build/[vendor]/[name]/obfuscated:
		if (hasBeenObfuscated) {
			try {
				File targetDir;
				if (this.buildSetting.isInCompilerMode()) {
					targetDir = this.buildSetting.getCompilerDestDir();
				} else {
					String targetPath = device.getBaseDir() + File.separatorChar + "obfuscated";
					device.setClassesDir(targetPath);
					targetDir = new File( targetPath );
				}
				if (targetDir.exists()) {
					// when the directory for extracting the obfuscated files
					// exists, delete it so that no old classes are remaining
					// in it:
					FileUtil.delete( targetDir );
				}
				JarUtil.unjar( destFile,  targetDir  );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to extract the obfuscated jar file: " + e.getMessage(), e );
			}
		}
		
		if (this.polishLogger != null) {
			this.polishLogger.setObfuscateMode( false );
		}
	}

	/**
	 * Preverifies the compiled and a\obfuscated code.
	 *  
	 * @param device The device for which the preverification should be done.
	 * @param locale the current localization
	 */
	protected void preverify( Device device, Locale locale ) {
		System.out.println("preverifying for device [" + device.getIdentifier() + "].");
		
		// add environment settings:
		File preverifyExecutable = this.buildSetting.getPreverify();
		if ( preverifyExecutable != null ) {
			this.environment.set( "preverify.executable", preverifyExecutable );
		}
		File destinationDir;
		if (this.buildSetting.isInCompilerMode()) {
			destinationDir = this.buildSetting.getCompilerDestDir();
		} else {
			destinationDir = new File( device.getClassesDir() );
			if ( !destinationDir.exists() ) {
				destinationDir = new File( getProject().getBaseDir(), device.getClassesDir() );
			}
		}
		this.environment.set( "preverify.target", destinationDir );

		// get the correct preverifier:
		Preverifier preverifier = null;
		PreverifierSetting[] settings =  this.buildSetting.getPreverifierSettings();
		Project antProject = getProject();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		for (int i = 0; i < settings.length; i++) {
			PreverifierSetting setting = settings[i];
			if ( setting.isActive( evaluator, antProject ) ) {
				try {
					preverifier = (Preverifier) this.extensionManager.getExtension( ExtensionManager.TYPE_PREVERIFIER, setting,  this.environment );
					break;
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to preverify for device [" + device.getIdentifier() + "], preverifier [" + setting.getName()  + "]: " + e.toString(), e );
				}
			}
		}
		if ( preverifier == null ) {
			// load default preverifier:
			String preverifierName = this.environment.getVariable( "polish.build.Preverifier" );
			if (  preverifierName != null ) {
				try {
					preverifier = (Preverifier) this.extensionManager.getTemporaryExtension( ExtensionManager.TYPE_PREVERIFIER, preverifierName, this.environment );
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to preverify for device [" + device.getIdentifier() + "], preverifier [" + preverifierName + "]: " + e.toString(), e );
				}
			} else {
				preverifier = new CldcPreverifier();
			}
		}
		
		// execute preverifier:
		preverifier.execute( device, locale, getEnvironment() );
		
	}

	/**
	 * Packages the code and assembles the resources for the application.
	 * 
	 * @param device The device for which the code should be jared.
	 * @param locale the current locale, can be null
	 */
	protected void jar( Device device, Locale locale ) {
		File classesDir = new File( device.getClassesDir() );
		
		// copy resources to final destination:
		try {
			FileUtil.copyDirectoryContents( device.getResourceDir(), classesDir, true );
		} catch (IOException e) {
			System.out.println("creating JAR for device [" + device.getIdentifier() + "]." );
			e.printStackTrace();
			throw new BuildException("Unable to copy resources: " + e.toString(), e );
		}		

		// retrieve the name of the jar-file:
		String jarName = this.environment.getVariable("polish.jarName");
		File jarFile = new File( this.buildSetting.getDestDir( this.environment ).getAbsolutePath() 
						+ File.separatorChar + jarName );
		device.setJarFile( jarFile );
		String test = this.polishProject.getCapability("polish.license");
		if ( !getLicense().equals(test)) {
			throw new BuildException("Encountered invalid license.");
		}
		
		// create manifest:
		HashMap attributesByName = new HashMap();
		attributesByName.put( "Manifest-Version", new Attribute( "Manifest-Version", "1.0" ) ); 
		// set MicroEdition-Profile:
		String profile = this.infoSetting.getProfile();
		if (profile == null) {			
			if (device.isMidp2()) {
				profile = InfoSetting.MIDP2;
			} else {
				profile = InfoSetting.MIDP1;
			}
		}
		attributesByName.put( InfoSetting.MICRO_EDITION_PROFILE, new Attribute(InfoSetting.MICRO_EDITION_PROFILE, profile) );

		// set MicroEdition-Configuration:
		String config = this.infoSetting.getConfiguration();
		if (config == null) {			
			if (!device.isCldc10()) {
				config = InfoSetting.CLDC1_1;
			} else {
				config = InfoSetting.CLDC1_0;
			}
		}
		attributesByName.put( InfoSetting.MICRO_EDITION_CONFIGURATION, new  Attribute(InfoSetting.MICRO_EDITION_CONFIGURATION, config) );

		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getManifestAttributes( this.environment );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute attribute = jadAttributes[i];
			attributesByName.put( attribute.getName(), attribute );
		}
		
		// add build properties - midlet infos:
		String mainClassName = this.environment.getVariable( "polish.classes.main" );
		if (mainClassName != null) {
			//System.out.println("Using Main-Class " + mainClassName );
			attributesByName.put( "Main-Class", new Attribute( "Main-Class", mainClassName ) );
			attributesByName.put( "MIDlet-1", new Attribute( "MIDlet-1", ",," ) );
		} else {
			String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon(), this.useDefaultPackage, this.environment );
			for (int i = 0; i < midletInfos.length; i++) {
				String info = midletInfos[i];
				attributesByName.put( InfoSetting.NMIDLET + (i+1), new  Attribute(InfoSetting.NMIDLET + (i+1), info ) );
			}
		}
		// add user-defined attributes:
		if (this.buildSetting.getJadAttributes() != null) {
			Attribute[] attributes = this.buildSetting.getJadAttributes().getAttributes( this.environment);
			for (int i = 0; i < attributes.length; i++) {
				Attribute attribute = attributes[i];
				if ( attribute.targetsManifest() ) {
					attributesByName.put(attribute.getName(),attribute );
				}
			}
		}
		
		//add polish version:
		attributesByName.put( "Polish-Version", new Attribute("Polish-Version", VERSION ) );
		
		// sort and filter the attributes if this is requested:
		Attribute[] manifestAttributes = null;
		try {
			manifestAttributes = this.buildSetting.filterManifestAttributes(attributesByName, this.environment.getBooleanEvaluator() );
		} catch (BuildException e) {
			System.err.println("Unable to filter MANIFEST attributes: " + e.getMessage() );
			throw e;
		}
		this.environment.set( ManifestCreator.MANIFEST_ATTRIBUTES_KEY, manifestAttributes );
		this.environment.set( ManifestCreator.MANIFEST_ENCODING_KEY, this.buildSetting.getEncoding() );
//		Jad jad = new Jad( this.environment );
//		jad.setAttributes(manifestAttributes);
		
		try {
			if (jarFile.exists()) {
				boolean success = jarFile.delete();
				if (!success) {
					throw new BuildException("Unable to delete the existing JAR-file [" + jarFile.getAbsolutePath() + "], please call \"ant clean\" or remove the folder \"" + jarFile.getParent() + "\"-folder manually." );
				}
			}
			System.out.println("creating JAR file ["+ jarFile.getAbsolutePath() + "].");
			// writing manifest:
			String creatorName = this.environment.getVariable( "polish.build.ManifestCreator" );
			if (creatorName == null ) {
				creatorName = "default";
			}
			this.extensionManager.executeTemporaryExtension(ExtensionManager.TYPE_MANIFEST_CREATOR, creatorName, this.environment );
//			Manifest manifest = new Manifest( this.environment, this.buildSetting.getEncoding() );
//			manifest.setAttributes( manifestAttributes );
//			File manifestFile = new File( device.getClassesDir() 
//					+ File.separator + "META-INF" + File.separator + "MANIFEST.MF");
//			manifest.write(manifestFile);
			//FileUtil.writeTextFile( manifestFile, jad.getContent(), this.buildSetting.getEncoding() );
			// package all classes and resources:
			Packager packager = (Packager) this.extensionManager.getActiveExtension( ExtensionManager.TYPE_PACKAGER, this.environment );
			if (packager == null ) {
				packager = new DefaultPackager();
			}
			this.environment.set( Packager.KEY_ENVIRONMENT, packager );
			packager.createPackage(classesDir, jarFile, device, locale, this.environment );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create final JAR file: " + e.getMessage(), e );
		}
		
	}
	
	/**
	 * Creates the JAD file for the given device.
	 * 
	 * @param device The device for which the JAD file should be created.
	 * @param locale
	 */
	protected void jad(Device device, Locale locale) {		
		// now create the JAD file:
		HashMap attributesByName = new HashMap();
		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getJadAttributes( this.environment );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute var  = jadAttributes[i];
			attributesByName.put( var.getName(), 
					new Attribute(var.getName(), var.getValue() ) );
		}
		
		// add build properties - midlet infos:
		// add build properties - midlet infos:
//		String mainClassName = this.environment.getVariable( "polish.classes.main" );
//		if (mainClassName != null) {
//			//attributesByName.put( "Main-Class", new Attribute( "Main-Class", mainClassName ) );
//			attributesByName.put( "MIDlet-1", new Attribute( "MIDlet-1", ",," ) );
//		} else {
			String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon(), this.useDefaultPackage, this.environment );
			for (int i = 0; i < midletInfos.length; i++) {
				String info = midletInfos[i];
				attributesByName.put( InfoSetting.NMIDLET + (i+1), 
							new Attribute(InfoSetting.NMIDLET + (i+1), info) );
				this.environment.setVariable( InfoSetting.NMIDLET + (i+1), info );
			}
//		}
		
		// add size of jar:
		long size = device.getJarFile().length();
		attributesByName.put(InfoSetting.MIDLET_JAR_SIZE,
					new Attribute( InfoSetting.MIDLET_JAR_SIZE, "" + size ) );
		this.environment.setVariable( InfoSetting.MIDLET_JAR_SIZE, "" + size );
		
		// add user-defined attributes:
		if (this.buildSetting.getJadAttributes() != null) {
			Attribute[] attributes = this.buildSetting.getJadAttributes().getAttributes( this.environment );
			for (int i = 0; i < attributes.length; i++) {
				Attribute attribute = attributes[i];
				if ( attribute.targetsJad() ) {
					attributesByName.put(attribute.getName(),
							new Attribute( attribute.getName(), attribute.getValue() ) );
				}
			}
		}
		
		// sort and filter the JAD attributes if requested:
		Attribute[] filteredAttributes = null;
		try {
			filteredAttributes = this.buildSetting.filterJadAttributes(attributesByName, this.environment.getBooleanEvaluator() );
		} catch (BuildException e) {
			System.err.println("Unable to filter JAD attributes: " + e.getMessage() );
			throw e;
		}
		
		this.environment.set( DescriptorCreator.DESCRIPTOR_ATTRIBUTES_KEY, filteredAttributes );
		this.environment.set( DescriptorCreator.DESCRIPTOR_ENCODING_KEY, this.buildSetting.getEncoding() );
			// writing JAD:
		
		String creatorName = this.environment.getVariable( "polish.build.DescriptorCreator" );
		if (creatorName == null ) {
			creatorName = "default";
		}
		this.extensionManager.executeTemporaryExtension( ExtensionManager.TYPE_DESCRIPTOR_CREATOR, creatorName, this.environment );
//		Jad jad = new Jad( this.environment );
//		jad.setAttributes( filteredAttributes );
//		
//		String jadPath = this.environment.getVariable("polish.jadPath");
//		File jadFile = new File( jadPath );
//		try {
//			System.out.println("creating JAD file [" + jadFile.getAbsolutePath() + "].");
//			FileUtil.writeTextFile(jadFile, jad.getContent(), this.buildSetting.getEncoding() );
//		} catch (IOException e) {
//			throw new BuildException("Unable to create JAD file [" + jadFile.getAbsolutePath() +"] for device [" + device.getIdentifier() + "]: " + e.getMessage() );
//		}
	}
	
	/**
	 * Calls java-extension.
	 * These can be used for example to sign the MIDlet.
	 * 
	 * @param device the current device
	 * @param locale
	 */
	protected void callExtensions( Device device, Locale locale ) {
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		
		for (int i = 0; i < this.javaExtensions.length; i++) {
			JavaExtension extension = this.javaExtensions[i];
			if (extension.isActive( evaluator )) {
				System.out.println("Executing <java> extension for device [" + device.getIdentifier() + "]." );
				// now call the extension:
				extension.execute(device, this.environment.getVariables());
			}
		}
	}

	protected void finalize( Device device, Locale locale ) {
		this.extensionManager.finalize(device, locale, this.environment );
		Finalizer[] finalizers = this.buildSetting.getFinalizers( this.extensionManager, this.environment );
		for (int i = 0; i < finalizers.length; i++) {
			Finalizer finalizer = finalizers[i];
			finalizer.execute(device, locale, this.environment);
		}
		String deviceFinalizersStr = device.getCapability( "polish.build.Finalizer");
		if (deviceFinalizersStr != null) {
			String[] deviceFinalizers = StringUtil.splitAndTrim(deviceFinalizersStr, ',');
			for (int i = 0; i < deviceFinalizers.length; i++) {
				String finalizerName = deviceFinalizers[i];
				System.out.println("Executing device specific finalizer [" + finalizerName + "]" );
				Finalizer finalizer = null;
				try {
					finalizer = (Finalizer) this.extensionManager.getTemporaryExtension( ExtensionManager.TYPE_FINALIZER, finalizerName, this.environment );
					finalizer.execute( device, locale, this.environment );
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to execute finalizer [" + finalizerName + "] for device [" + device.getIdentifier() + "]: " + e.toString() );
				}
			}
		}
		device.resetEnvironment();
	}

	/**
	 * Launches the emulator if the user wants to.
	 * 
	 * @param device the current device.
	 * @param locale current locale
	 */
	protected void runEmulator( Device device, Locale locale ) {
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		EmulatorSetting[] settings = getEmulatorSettings();
		for (int i = 0; i < settings.length; i++) {
			EmulatorSetting emulatorSetting = settings[i];
			if ( emulatorSetting.isActive( evaluator ) ) {
				Project antProject = getProject();
				// get currently active source directories:
				ArrayList sourceDirsList = new ArrayList();
				for (int j = 0; j < this.sourceSettings.length; j++) {
					SourceSetting setting = this.sourceSettings[j];
					if (setting.isActive(evaluator, antProject)) {
						sourceDirsList.add( setting.getDir() );
					}
				}
				File[] sourceDirs = (File[]) sourceDirsList.toArray( new File[ sourceDirsList.size() ] );
				Emulator emulator = Emulator.createEmulator(device, emulatorSetting, this.environment, sourceDirs, this.extensionManager );
				if (emulator != null) {
					emulator.execute( device, locale, this.environment );
					if (this.runningEmulators == null) {
						this.runningEmulators = new ArrayList();
					}
					this.runningEmulators.add( emulator );
				}
				break;
			}			
		}
	}
	
	private EmulatorSetting[] getEmulatorSettings() {
		if (this.emulatorSettings == null) {
			return new EmulatorSetting[0];
		} else {
			return (EmulatorSetting[]) this.emulatorSettings.toArray( new EmulatorSetting[ this.emulatorSettings.size()]);
		}
	}

	/**
	 * 
	 */
	private void test() {
		// TODO enough implement test
		
	}

	/**
	 * 
	 */
	private void deploy() {
		// TODO enough implement deploy
		
	}
	
	
	public Device[] getDevices() {
		return this.devices;
	}
	
	public Environment getEnvironment() {
		return this.environment;
	}
	
	/**
	 * <p>Accepts only non CSS-files.</p>
	 *
	 * <p>Copyright Enough Software 2004, 2005</p>

	 * <pre>
	 * history
	 *        19-Feb-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	class CssFileFilter implements FileFilter {

		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return false;
			}
			String extension = file.getName();
			int extPos = extension.lastIndexOf('.'); 
			if ( extPos != -1) {
				extension = extension.substring( extPos + 1 ); 
			}
			//TODO enough also filter settings.xml
			if ( ("css".equals( extension )) || ("CSS".equals(extension)) ) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	class FailureInfo {
		private final Date time;
		private final Device device;
		private final Locale locale;
		private final BuildException exception;
		
		/**
		 * @param device
		 * @param locale
		 * @param exception
		 */
		public FailureInfo(Device device, Locale locale, BuildException exception) {
			super();
			this.time = new Date();
			this.device = device;
			this.exception = exception;
			this.locale = locale;
		}
		
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			appendInfo( buffer );
			return buffer.toString();
		}
		
		public void appendInfo( StringBuffer buffer ) {
			buffer.append( this.time.toString() ).append(": ");
			buffer.append( this.device.getIdentifier() );
			if ( this.locale != null ) {
				buffer.append( "[ " ).append( this.locale.toString() ).append("]");
			}
			buffer.append(": ").append( this.exception.toString() );
		}
	}
	
	protected void notifyPolishBuildListeners( String eventName, Object data ) {
		if (this.polishBuildListeners != null) {
			for (int i = 0; i < this.polishBuildListeners.length; i++) {
				PolishBuildListener listener = this.polishBuildListeners[i];
				try {
					listener.notifyBuildEvent(eventName, data);
				} catch (Exception e) {
					e.printStackTrace();
					throw new BuildException("Unable to notofy build listener " + listener + " about event " + eventName + ": " + e.toString(), e );
				}
			}
		}
	}
	

}
