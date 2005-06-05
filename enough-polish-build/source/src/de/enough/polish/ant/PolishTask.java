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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Attribute;
import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.PolishProject;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.ant.build.CompilerTask;
import de.enough.polish.ant.build.FullScreenSetting;
import de.enough.polish.ant.build.JavaExtension;
import de.enough.polish.ant.build.LibrariesSetting;
import de.enough.polish.ant.build.LibrarySetting;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.ant.build.Midlet;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.ant.build.PostCompilerSetting;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.ant.build.ResourceSetting;
import de.enough.polish.ant.build.SourceSetting;
import de.enough.polish.ant.build.Variables;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.emulator.Emulator;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.jar.DefaultPackager;
import de.enough.polish.jar.Packager;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.preprocess.CssAttribute;
import de.enough.polish.preprocess.CssAttributesManager;
import de.enough.polish.preprocess.CssConverter;
import de.enough.polish.preprocess.CssReader;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.DebugManager;
import de.enough.polish.preprocess.ImportConverter;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.StyleSheet;
import de.enough.polish.preprocess.custom.TranslationPreprocessor;
import de.enough.polish.resources.ResourceManager;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;
import de.enough.polish.util.TextFile;
import de.enough.polish.util.TextFileManager;

/**
 * <p>Manages a J2ME project from the preprocessing to the packaging and obfuscation.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishTask extends ConditionalTask {

	private static final String VERSION = "1.3<beta1>";

	private BuildSetting buildSetting;
	private InfoSetting infoSetting;
	private Requirements deviceRequirements;
	private EmulatorSetting emulatorSetting;
	
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
	private Path midp1BootClassPath;
	private Path midp2BootClassPath;
	private Path midp2Cldc11BootClassPath;
	private String javacTarget;
	/** the source-compatibility-switch for the javac-compiler defaults to "1.3" */
	private String sourceCompatibility = "1.3";
	private Obfuscator[] obfuscators;
	private boolean doObfuscate;
	private String[] preserveClasses;
	private StyleSheet styleSheet;
	private ImportConverter importConverter;
	private TextFile styleSheetSourceFile;
	private ResourceUtil resourceUtil;
	private String wtkHome;
	private HashMap midletClassesByName;
	private static final Pattern START_APP_PATTERN = 
		Pattern.compile("\\s*void\\s+startApp\\s*\\(\\s*\\)");

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

	private Locale[] supportedLocales;

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

	
	/**
	 * Creates a new empty task 
	 */
	public PolishTask() {
		// initialisation is done with the setter-methods.
		// if you should use the PolishTask not within an ant-build.xml
		// then make sure to set the project with .setProject(...)
		this.resourceUtil = new ResourceUtil( getClass().getClassLoader() );
	}
	
	public void addConfiguredInfo( InfoSetting setting ) {
		if (setting.getName() == null ) {
			throw new BuildException("The nested element <info> requires the attribute [name] which defines the name of this project.");
		}
		if (setting.getlicense() == null) {
			throw new BuildException("The nested element <info> requires the attribute [license] with either \"GPL\" for open source software or the commercial license, which can be obtained at http://www.j2mepolish.org.");
		}
		if (setting.getVendorName() == null) {
			throw new BuildException("The nested element <info> requires the attribute [vendor] which defines the name of the vendor providing the application.");
		}
		if (setting.getVersion() == null) {
			throw new BuildException("The nested element <info> requires the attribute [version] which defines the version of this application, e.g. \"1.2.3\".");
		}
		setting.setDefaultJarUrl();
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		if (requirements.isActive(getProject())) {
			this.deviceRequirements = requirements;
		}
	}
	
	/**
	 * Creates and adds the build settings for this project.
	 * 
	 * @return the new build setting.
	 */
	public BuildSetting createBuild() {
		this.buildSetting = new BuildSetting( getProject() );
		return this.buildSetting;
	}
	
	/**
	 * Creates and adds a new run-setting for this project.
	 * 
	 * @return the new runsetting.
	 */
	public EmulatorSetting createEmulator() {
		this.emulatorSetting = new EmulatorSetting( getProject() );
		return this.emulatorSetting;
	}
	
	/**
	 * Executes this task. 
	 * For all selected devices the source code will be preprocessed,
	 * compiled, obfuscated and jared.
	 * 
	 * @throws BuildException when the build failed.
	 */
	public void execute() throws BuildException {
		System.out.println("J2ME Polish " + VERSION );
		if (!isActive()) {
			return;
		}
		try {
			if (!this.isInitialized) {
				checkSettings();
				initProject();
				selectDevices();
				this.isInitialized = true;
			}
			int numberOfDevices = this.devices.length;
			if (this.buildSetting.isInCompilerMode()) {
				System.out.println("Using J2ME Polish as compiler...");
			} else if (numberOfDevices > 1) {
				System.out.println("Processing [" + numberOfDevices + "] devices...");
			}
			boolean hasExtensions = (this.javaExtensions.length > 0);
			
			for ( int i=0; i < numberOfDevices; i++) {
				Device device = this.devices[i];
				if (numberOfDevices > 1) {
					System.out.println("Building application for [" + device.getIdentifier() + "] (" + (i+1) + "/" + numberOfDevices + "):");
				}
				if (this.supportedLocales != null) {
					for (int j = 0; j < this.supportedLocales.length; j++) {
						Locale locale = this.supportedLocales[j];
						System.out.println("Using locale [" + locale.toString() + "]...");
						execute(device, locale, hasExtensions);
						if (this.buildSetting.isInCompilerMode()) {
							// return immediately in the compiler mode:
							return;
						}
					}					
				} else {
					execute(device, null, hasExtensions);
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
			if (numberOfDevices > 1) {
				System.out.println("Successfully processed [" + numberOfDevices + "] devices.");
			}
		} catch (BuildException e) {
			//e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
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
	

	/**
	 * Executes the sequence of J2ME Polish subtasks for the given device and locale.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param hasExtensions true when there are <java>-extensions
	 */
	private void execute(Device device, Locale locale, boolean hasExtensions) {
		initialize( device, locale );
		preprocess( device, locale );
		compile( device );
		if (this.doPostCompile) {
			postCompile(device, locale);
		}
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
		if (this.emulatorSetting != null) {
			runEmulator( device, locale );
		}
	}

	/**
	 * Finishes this project.
	 * Basically removes the error-lock:
	 */
	private void finishProject() {
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
		Midlet[] midlets = this.buildSetting.getMidlets(); 
		if (midlets == null || midlets.length == 0) {
			throw new BuildException("Midlets need to be defined in the <build>-section with either <midlets> or <midlet>.");
		}
		// now check if the midlets do exist:
		SourceSetting[] sources = this.buildSetting.getSourceSettings();
		if (!this.buildSetting.useDefaultPackage()) {
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
						midletFound = true;
						break;
					}
				}
				if (!midletFound) {
					throw new BuildException("The MIDlet [" + midlet.getClassName() + "] could not be found. Check your <midlet>-setting in the file [build.xml] or adjust the [sourceDir] attribute of the <build>-element.");
				}
			}
		}
		// check if the ant-property WTK_HOME has been set:
		//e.g. with: <property name="wtk.home" value="c:\Java\wtk-1.0.4"/>
		this.wtkHome = getProject().getProperty("wtk.home");
		if (this.buildSetting.getPreverify() == null) {
			// no preverify has been set, that's okay when the wtk.home ant-property has been set:
			if (this.wtkHome == null) { 
				throw new BuildException("Nested element [build] needs to define the attribute [preverify] which points to the preverify-executable of the wireless toolkit. Alternatively you can set the home directory of the Wireless Toolkit by defining the Ant-property [wtk.home]: <property name=\"wtk.home\" location=\"/home/user/WTK2.1\"/>");
			}
			if (!this.wtkHome.endsWith( File.separator )) {
				this.wtkHome += File.separator;
			}
			String preverifyPath = this.wtkHome + "bin" + File.separator + "preverify";
			if ( File.separatorChar == '\\') {
				preverifyPath += ".exe";
			}
			File preverifyFile = new File( preverifyPath );
			if (preverifyFile.exists()) {
				this.buildSetting.setPreverify( preverifyFile.getAbsolutePath() );
			} else {
				// probably the wtk.home path is wrong:
				File file = new File( this.wtkHome );
				if (!file.exists()) {
					throw new BuildException("The Ant-property [wtk.home] points to a non-existing directory. Please adjust his setting in the build.xml file.");
				} else {
					throw new BuildException("Unable to find the preverify tool at the default location [" + preverifyPath + "]. Please specify where to find it with the \"preverify\"-attribute of the <build> element (in the build.xml file).");
				}
			}
		}
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
			this.extensionManager = new ExtensionManager( getProject(), this.buildSetting.openExtensions() );
			this.extensionManager.loadCustomDefinitions( this.buildSetting.getCustomExtensions() );
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to load extensions.xml - please report this error to j2mepolish@enough.de.");
		}
		
		// create environment
		this.environment = new Environment( this.extensionManager, getProject(), this );
		this.environment.setBuildSetting( this.buildSetting );
		
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		if (isDebugEnabled) {
			try {
				debugManager = new DebugManager( this.buildSetting.getDebugSetting(), this.extensionManager, this.environment );
			} catch (BuildException e) {
				throw new BuildException( e.getMessage(), e );
			}
		}
		
		// create project settings:
		this.polishProject = new PolishProject( this.buildSetting.usePolishGui(), isDebugEnabled, debugManager );
		this.polishProject.addDirectCapability("polish.buildVersion", VERSION);
		this.polishProject.addDirectFeature( "polish.active" );
		if (debugManager != null && debugManager.isVerbose()) {
			this.polishProject.addFeature("debugVerbose");
			this.polishProject.addDirectCapability("polish.debug.Verbose", "enabled");
		}
		if (debugManager != null && this.buildSetting.getDebugSetting().showLogOnError()) {
			this.polishProject.addFeature("showLogOnError");
		}
		this.polishProject.addCapability("license", this.infoSetting.getlicense() );
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
		
		// add primary midlet-class-definition:
		String[] midletClassNames = this.buildSetting.getMidletClassNames();
		this.polishProject.addDirectCapability("polish.midlet.class", midletClassNames[0] );
		for (int i = 0; i < midletClassNames.length; i++) {
			this.polishProject.addDirectCapability("polish.classes.midlet-" + (i+1), midletClassNames[i] );			
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
		this.deviceDatabase = new DeviceDatabase( getProject().getProperties(), this.polishHomeDir, getProject().getBaseDir(),
				this.buildSetting.getApiDir(), this.polishProject, this.buildSetting.getDeviceDatabaseInputStreams(), this.buildSetting.getDeviceDatabaseFiles() );
		
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
		this.preprocessor.setUseDefaultPackage( this.buildSetting.useDefaultPackage() );
		this.preprocessor.setCssAttributesManager( this.cssAttributesManager );
		// init custom preprocessors:
		this.customPreprocessors = new ArrayList();
		PreprocessorSetting[] settings = this.buildSetting.getPreprocessors();
		//CustomPreprocessor[] processors = new CustomPreprocessor[ settings.length + 1];
		// add the polish custom processor:
		// new PolishPreprocessor();
		PreprocessorSetting preprocessorSetting = new PreprocessorSetting();
		preprocessorSetting.setName("polish");
		CustomPreprocessor customProcessor = CustomPreprocessor.getInstance(preprocessorSetting, this.preprocessor, this.extensionManager, this.environment );
		//customProcessor.init(this.preprocessor, null);
		this.customPreprocessors.add( customProcessor );
		//processors[0] = customProcessor;
		for (int i = 0; i < settings.length; i++) {
			preprocessorSetting = settings[i];
			customProcessor = CustomPreprocessor.getInstance(preprocessorSetting, this.preprocessor, this.extensionManager, this.environment );
			this.customPreprocessors.add( customProcessor );
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
			dirScanner.setBasedir(this.polishSourceDir);
			dirScanner.scan();
			this.polishSourceFiles = getTextFiles( this.polishSourceDir,  dirScanner.getIncludedFiles(), textFileManager );
		} else {
			// the J2ME Polish sources need to be loaded from the jar-file:
			long lastModificationTime = 0;
			File jarFile = new File( this.buildSetting.getApiDir().getAbsolutePath() 
					+ File.separator + "enough-j2mepolish-build.jar");
			if (jarFile.exists()) {
				lastModificationTime = jarFile.lastModified();
			} else {
				jarFile = new File("import/enough-j2mepolish-build.jar");
				if (jarFile.exists()) {
					lastModificationTime = jarFile.lastModified();
				}
			}
			this.polishSourceDir = new File("src");
			try {
				String[] fileNames = this.resourceUtil.readTextFile( ".", "build/j2mepolish.index.txt");
				this.polishSourceFiles = getTextFiles( "src", fileNames, lastModificationTime, textFileManager );
			} catch (IOException e) {
				throw new BuildException("Unable to load the J2ME source files from enough-j2mepolish-build.jar: " + e.getMessage(), e );
			}
		}
		
		// load the normal source files:
		for (int i = 0; i < this.sourceSettings.length; i++) {
			SourceSetting setting = this.sourceSettings[i];
			File dir = setting.getDir();
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + dir.getAbsolutePath() + "] does not exist. Please check your settings in the [sourceDir] attribute of the <build> element.");
			}
			//this.sourceDirs[i] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i] = getTextFiles( dir,  dirScanner.getIncludedFiles(), textFileManager );
		}
		if (this.buildSetting.usePolishGui() && this.styleSheetSourceFile == null) {
			throw new BuildException("Did not find the file [StyleSheet.java] of the J2ME Polish GUI framework. Please adjust the [polishDir] attribute of the <build> element in the [build.xml] file. The [polishDir]-attribute should point to the directory which contains the J2ME Polish-Java-sources.");
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
		
		// init boot class path:
		this.midp1BootClassPath = new Path( getProject(), this.buildSetting.getMidp1Path().getAbsolutePath());
		this.midp2BootClassPath = new Path( getProject(), this.buildSetting.getMidp2Path().getAbsolutePath());
		this.midp2Cldc11BootClassPath = new Path( getProject(), this.buildSetting.getMidp2Cldc11Path().getAbsolutePath());
		
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
					Obfuscator obfuscator;
					try {
						obfuscator = Obfuscator.getInstance( obfuscatorSetting, getProject(), this.extensionManager, this.environment );
					} catch (BuildException e) {
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
						throw new BuildException("Unable to initialize the obfuscator [" + obfuscatorSetting.getName() + "/" + obfuscatorSetting.getClassName() + "]: " + e.toString() );
					}
					obfuscatorsList.add( obfuscator );
				}
				
			}
			this.doObfuscate = (obfuscatorsList.size() > 0);
			if (this.doObfuscate) {
				this.obfuscators = (Obfuscator[]) obfuscatorsList.toArray( new Obfuscator[ obfuscatorsList.size() ] );
				this.useDefaultPackage = this.buildSetting.useDefaultPackage();
				
				String[] midletClasses = this.buildSetting.getMidletClassNames();
				if (keepClasses == null) {
					keepClasses = new String[0];
				}
				this.preserveClasses = new String[ keepClasses.length + midletClasses.length ];
				System.arraycopy( keepClasses, 0, this.preserveClasses, 0,  keepClasses.length );
				System.arraycopy( midletClasses, 0, this.preserveClasses, keepClasses.length, midletClasses.length  );
			}
		}
		
		// init import manager:
		this.importConverter = new ImportConverter();
		
		// init base style sheet:
		if (this.buildSetting.usePolishGui()) {
			ResourceSetting resourceSetting = this.buildSetting.getResourceSetting();
			File cssFile = new File( resourceSetting.getDir().getAbsolutePath() + File.separatorChar + "polish.css");
			if (!cssFile.exists()) {
				log("Unable to find polish.css at [" + cssFile.getAbsolutePath() + "] - you should create this file when you want to make most of the J2ME Polish GUI.", Project.MSG_WARN );
				this.styleSheet = new StyleSheet();
			} else {
				CssReader cssReader = new CssReader();
				try {
					cssReader.add(cssFile);
				} catch (IOException e) {
					throw new BuildException("Unable to load polish.css: " + e.getMessage(), e );
				}
				this.styleSheet = cssReader.getStyleSheet();
			}
		}
		
		// set the names of the midlets:
		this.midletClassesByName = new HashMap();
		String[] midletClasses = this.buildSetting.getMidletClassNames();
		for (int i = 0; i < midletClasses.length; i++) {
			this.midletClassesByName.put( midletClasses[i], Boolean.TRUE );			
		}
		
		// get the java-extension:
		this.javaExtensions = this.buildSetting.getJavaExtensions();
		
		// get the javac-settings:
		this.javacTarget = this.buildSetting.getJavacTarget();
		
		// get the resource manager:
		this.resourceManager = new ResourceManager( this.buildSetting.getResourceSetting(), this.extensionManager, this.environment );
		Locale[] localizations = this.resourceManager.getLocales();
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
		
		// check if there has been an error at the last run:
		this.errorLock = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separator + "error.lock");
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
		
		// set J2ME Polish specific logger,
		// this logger will show the original source-code positions
		// and remove some verbose logging from ProGuard etc:
		Vector buildListeners = getProject().getBuildListeners();
		BuildLogger logger = null;
		for (Iterator iter = buildListeners.iterator(); iter.hasNext();) {
			BuildListener listener = (BuildListener) iter.next();
			if (listener instanceof BuildLogger) {
				logger = (BuildLogger) listener;
				break;
			}			
		}
		if (logger != null) {
			// prepare the classPathTranslations-Map:
			HashMap classPathTranslationsMap = new HashMap(); 
			for (int i=0; i < this.sourceFiles.length; i++) {
				TextFile[] files = this.sourceFiles[i];
				for (int j = 0; j < files.length; j++) {
					TextFile file = files[j];
					classPathTranslationsMap.put( file.getFilePath(), file.getFile().getAbsolutePath() );
				}
			}
			this.polishLogger = new PolishLogger(logger, classPathTranslationsMap );
			getProject().addBuildListener( this.polishLogger );
			getProject().removeBuildListener(logger);
		} else {
			System.err.println("Warning: unable to replace Ant-logger. Compile errors will point to the preprocessed files instead of the original sources.");
		}
	}

	/**
	 * Creates an array of text files.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @param textFileManager the manager for textfiles
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(File baseDir, String[] fileNames, TextFileManager textFileManager ) 
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
	private TextFile[] getTextFiles(String baseDir, String[] fileNames, long lastModificationTime, TextFileManager textFileManager) 
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
		// set variables and symbols:
		this.environment.setSymbols( device.getFeatures() );
		this.environment.setVariables( device.getCapabilities() );
		this.environment.addVariable( "polish.identifier", device.getIdentifier() );
		this.environment.addVariable( "polish.name", device.getName() );
		this.environment.addVariable( "polish.vendor", device.getVendorName() );
		this.environment.addVariable( "polish.version", this.infoSetting.getVersion() );
		if (this.useDefaultPackage) {
			this.environment.addSymbol("polish.useDefaultPackage");
			this.environment.addVariable("polish.useDefaultPackage", "true");
		}
		// set localization-variables:
		if (locale != null || (this.localizationSetting != null && this.localizationSetting.isDynamic())) {
			if (locale == null) {
				locale = this.localizationSetting.getDefaultLocale();
				this.environment.addSymbol("polish.i18n.useDynamicTranslations");
				this.environment.setLocale( locale );
			}
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
		jarName = this.environment.writeProperties( jarName, true );
		this.environment.addVariable( "polish.jarName", jarName );
		
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
		Variable[] vars = this.variables.getVariables( antProject, evaluator, this.environment );
		for (int i = 0; i < vars.length; i++) {
			Variable var = vars[i];
			this.environment.addVariable(var.getName(), var.getValue() );
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
		String jarPath = this.buildSetting.getDestDir().getAbsolutePath() + File.separator + jarName;
		this.environment.addVariable( "polish.jarPath", jarPath );
		String jadName = jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad";
		this.environment.addVariable( "polish.jadName", jadName );
		String jadPath = this.buildSetting.getDestDir().getAbsolutePath() + File.separator + jadName;
		this.environment.addVariable( "polish.jadPath", jadPath );

		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getJadAttributes( this.environment.getVariables() );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute attribute = jadAttributes[i];
			this.environment.addVariable( attribute.getName(), attribute.getValue() );
		}
		String copyright = this.infoSetting.getCopyright();
		if (copyright != null) {
			this.environment.addVariable( "MIDlet-Copyrigh", copyright );
		}
		
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

		// okay, now initialize extension manager:
		this.extensionManager.initialize(device, locale, this.environment);
		this.extensionManager.postInitialize(device, locale, this.environment);
		
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
	 * Preprocesses the source code for all devices.
	 * 
	 * @param device The device for which the preprocessing should be done.
	 * @param locale the current locale, can be null
	 */
	private void preprocess( Device device, Locale locale ) {
		System.out.println("preprocessing for device [" +  device.getIdentifier() + "]." );
		try {
			this.numberOfChangedFiles = 0;
			String deviceSpecificBuildPath = File.separatorChar + device.getVendorName() 
											+ File.separatorChar + device.getName();
			deviceSpecificBuildPath = deviceSpecificBuildPath.replace(' ', '_' );
			String targetDir = this.buildSetting.getWorkDir().getAbsolutePath() + deviceSpecificBuildPath;
			if (locale != null) {
				targetDir += File.separatorChar + locale.toString();
			}
			device.setBaseDir( targetDir );
			targetDir += File.separatorChar + "source";
			device.setSourceDir(targetDir);
			// initialise the preprocessor:
			this.preprocessor.setTargetDir( targetDir );
			// set variables and symbols:
			/*
			this.preprocessor.setSymbols( device.getFeatures() );
			this.preprocessor.setVariables( device.getCapabilities() );
			this.preprocessor.addVariable( "polish.identifier", device.getIdentifier() );
			this.preprocessor.addVariable( "polish.name", device.getName() );
			this.preprocessor.addVariable( "polish.vendor", device.getVendorName() );
			this.preprocessor.addVariable( "polish.version", this.infoSetting.getVersion() );
			if (this.useDefaultPackage) {
				this.preprocessor.addSymbol("polish.useDefaultPackage");
				this.preprocessor.addVariable("polish.useDefaultPackage", "true");
			}
			*/
			long lastLocaleModification = 0;
			TranslationManager translationManager = null;
			// set localization-variables:
			if (locale != null || (this.localizationSetting != null && this.localizationSetting.isDynamic())) {
				if (locale == null) {
					locale = this.localizationSetting.getDefaultLocale();
					//this.preprocessor.addSymbol("polish.i18n.useDynamicTranslations");
				}
				/*
				this.preprocessor.addVariable("polish.locale", locale.toString() );
				this.preprocessor.addVariable("polish.language", locale.getLanguage() );
				String country = locale.getCountry();
				if ( country == null || country.length() == 0 ) {
					this.preprocessor.removeVariable( "polish.country" );
				} else {
					this.preprocessor.addVariable("polish.country", country );
				}
				*/
				// load localized messages, this also sets localized variables automatically:
				translationManager = this.resourceManager.getTranslationManager(device, locale, this.preprocessor );
				this.translationPreprocessor.setTranslationManager( translationManager );
				lastLocaleModification = translationManager.getLastModificationTime();
			}
			// set info-variables:
			/*
			String jarName = this.infoSetting.getJarName();
			jarName = PropertyUtil.writeProperties(jarName, this.preprocessor.getVariables());
			this.preprocessor.addVariable( "polish.jarName", jarName );
			String jarPath = this.buildSetting.getDestDir().getAbsolutePath() + File.separator + jarName;
			this.preprocessor.addVariable( "polish.jarPath", jarPath );
			String jadName = jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad";
			this.preprocessor.addVariable( "polish.jadName", jadName );
			String jadPath = this.buildSetting.getDestDir().getAbsolutePath() + File.separator + jadName;
			this.preprocessor.addVariable( "polish.jadPath", jadPath );
			// set conditional variables:
			BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
			Project antProject = getProject();
			Variable[] vars = this.variables.getVariables( antProject, evaluator, this.environment );
			for (int i = 0; i < vars.length; i++) {
				Variable var = vars[i];
				this.preprocessor.addVariable(var.getName(), var.getValue() );
			}
			*/
			/*
			if (this.conditionalVariables != null) {
				// add variables which fulfill the conditions: 
				BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
				for (int i = 0; i < this.conditionalVariables.length; i++) {
					Variable var = this.conditionalVariables[i];
					if (var.isConditionFulfilled( evaluator, getProject() )) {
						this.preprocessor.addVariable(var.getName(), var.getValue() );
					}
				}
			}
			*/
			// now set the full-screen-settings:
			/*
			String value = this.preprocessor.getVariable("polish.FullScreen");
			if (value != null) {
				if ("menu".equalsIgnoreCase(value)) {
					this.preprocessor.addSymbol("polish.useMenuFullScreen");
					this.preprocessor.addSymbol("polish.useFullScreen");					
				} else if ("yes".equalsIgnoreCase( value ) || "true".equalsIgnoreCase(value)) {
					this.preprocessor.addSymbol("polish.useFullScreen");					
				}
			} else {
				FullScreenSetting fullScreenSetting = this.buildSetting.getFullScreenSetting();
				if (fullScreenSetting != null) {
					if (fullScreenSetting.isMenu()) {
						this.preprocessor.addSymbol("polish.useMenuFullScreen");
						this.preprocessor.addSymbol("polish.useFullScreen");
					} else if (fullScreenSetting.isEnabled()) {
						this.preprocessor.addSymbol("polish.useFullScreen");
					}
				}
			}
			// adjust polish.classes.ImageLoader in case the default package is used:
			if (this.useDefaultPackage) {
				String imageLoaderClass = this.preprocessor.getVariable("polish.classes.ImageLoader");
				if (imageLoaderClass != null) {
					int classStartIndex = imageLoaderClass.lastIndexOf('.');
					if (classStartIndex != -1) {
						imageLoaderClass = imageLoaderClass.substring( classStartIndex + 1 );
						this.preprocessor.addVariable("polish.classes.ImageLoader", imageLoaderClass );
					}
				}
			}
			// add settings of active postcompilers:
			// let postcompilers adjust the bootclasspath:
			if (this.doPostCompile) {
				PostCompiler[] compilers = getActivePostCompilers();
				for (int i = 0; i < compilers.length; i++) {
					PostCompiler postCompiler = compilers[i];
					postCompiler.addPreprocessingSettings(device, this.preprocessor);
				}
			}
			*/
			BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
			// add custom preprocessors
			CustomPreprocessor[] preprocessors = (CustomPreprocessor[]) this.customPreprocessors.toArray( new CustomPreprocessor[ this.customPreprocessors.size() ] );
			this.preprocessor.clearCustomPreprocessors();
			for (int i = 0; i < preprocessors.length; i++) {
				CustomPreprocessor processor = preprocessors[i];
				PreprocessorSetting setting = processor.getSetting();
				if (setting == null || setting.isActive( evaluator, getProject() ) ) {
					this.preprocessor.addCustomPreprocessor(processor);
				}
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
				cssStyleSheet = this.resourceManager.loadStyleSheet( this.styleSheet, device, locale );
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
					processSourceDir(sourceDir, files, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, false);
				}
			} // for each source folder
			this.preprocessor.notifyPolishPackageStart();
			// now process the J2ME package files:
			processSourceDir(this.polishSourceDir, this.polishSourceFiles, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, true);
			// notify preprocessor about the end of preprocessing:
			this.preprocessor.notifyDeviceEnd( device, usePolishGui );
			
			
			// now all files have been preprocessed.
			// Now the StyleSheet.java file needs to be written,
			// but only when the polish GUI should be used:
			File baseDirectory = new File( targetDir );
			if (usePolishGui) {
				// check if the CSS declarations have changed since the last run:
				File targetFile =  this.styleSheetSourceFile.getTargetFile( baseDirectory, this.useDefaultPackage );
					//new File( targetDir + File.separatorChar + this.styleSheetSourceFile.getFilePath() );				
				boolean cssIsNew = (!targetFile.exists())
					|| ( lastCssModification > targetFile.lastModified() )
					|| ( buildXmlLastModified > targetFile.lastModified() );
				if (cssIsNew) {
					//System.out.println("CSS is new and the style sheet will be generated.");
					if (this.styleSheetCode == null) {
						// the style sheet has not been preprocessed:
						this.styleSheetCode = new StringList( this.styleSheetSourceFile.getContent() );
						String className = "de.enough.polish.ui.StyleSheet";
						this.preprocessor.preprocess( className, this.styleSheetCode );
					}
					// now insert the CSS information for this device
					// into the StyleSheet.java source-code:
					CssConverter cssConverter = new CssConverter( this.cssAttributesManager );
					this.styleSheetCode.reset();
					cssConverter.convertStyleSheet(this.styleSheetCode, 
							this.preprocessor.getStyleSheet(),
							device,
							this.preprocessor ); 				
					//this.styleSheetSourceFile.saveToDir(targetDir, this.styleSheetCode.getArray(), false );
					if (this.useDefaultPackage) {
						this.styleSheetCode.reset();
						this.importConverter.processImports(true, device.isMidp1(), this.styleSheetCode, device, this.preprocessor);
						this.styleSheetCode.reset();
						this.importConverter.removeDirectPackages( this.styleSheetCode, this.preprocessor.getTextFileManager() );
					}
					FileUtil.writeTextFile(targetFile, this.styleSheetCode.getArray());
					this.numberOfChangedFiles++;
				//} else {
				//	System.out.println("CSSS is not new - last CSS modification == " + lastCssModification + " <= StyleSheet.java.lastModified() == " + targetFile.lastModified() );
				}
				
			}
			// now check if the de.enough.polish.util.Locale.java needs to be rewritten:
			if (locale != null) {
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
					if (!translationManager.isDynamic()) {
						translationManager.processLocaleCode( this.localeCode );
						//this.localeSourceFile.saveToDir(targetDir, this.localeCode.getArray(), false );
					}
					if (this.useDefaultPackage) {
						this.localeCode.reset();
						this.importConverter.processImports(true, device.isMidp1(), this.localeCode, device, this.preprocessor);
					}
					FileUtil.writeTextFile(targetFile, this.localeCode.getArray());
					this.numberOfChangedFiles++;
				}
			}
			device.setNumberOfChangedFiles( this.numberOfChangedFiles );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( e.getMessage() );
		} catch (BuildException e) {
			//e.printStackTrace();
			throw new BuildException( e.getMessage() );
		}
	}
	
	private void processSourceDir( File sourceDir, 
									TextFile[] files, 
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
		//System.out.println("current source dir: " + sourceDir );
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
					 || ( lastCssModification > targetLastModified);
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
					}
				}
				// only think about saving when the file should not be skipped 
				// and when it is not the StyleSheet.java file:
				if (file == this.styleSheetSourceFile ) {
					this.styleSheetCode = sourceCode;
				} else if (file == this.localeSourceFile) {
					this.localeCode = sourceCode;
				} else if (result == Preprocessor.SKIP_FILE ) {
					if (targetFile.exists()) {
						boolean deleted = FileUtil.delete(targetFile);
						if (!deleted) {
							System.out.println("Warning: unable to deleted unused source file [" + targetFile.getAbsolutePath() + ": please make a clean rebuild.");
						}
					}
				} else {
					if (!isInPolishPackage || this.useDefaultPackage ) {
						sourceCode.reset();
						// now replace the import statements:
						boolean changed = this.importConverter.processImports(usePolishGui, device.isMidp1(), sourceCode, device, this.preprocessor );
						if (changed) {
							result = Preprocessor.CHANGED;
						}
					}
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
	private void insertDisplaySetting( String className, StringList sourceCode ) {
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
				sourceCode.setCurrent( displayVar + " = javax.microedition.lcdui.Display.getDisplay( this );"
						+ line );
				return;
			}
		}
		System.out.println(START_APP_PATTERN.pattern());
		throw new BuildException("Unable to find startApp method in MIDlet [" + className + "].");

	}


	/**
	 * Compiles the source code.
	 *  
	 * @param device The device for which the source code should be compiled.
	 */
	private void compile( Device device ) {
		// set the class-path:
		String[] classPaths = this.libraryManager.getClassPaths(device);
		device.setClassPaths( classPaths );

		// setting target directory:
		String targetDirName = device.getBaseDir() + File.separatorChar + "classes";
		device.setClassesDir( targetDirName );
		File targetDir = new File( targetDirName );
		
		if (device.getNumberOfChangedFiles() == 0 && !this.lastRunFailed) {
			System.out.println("nothing to compile for device [" +  device.getIdentifier() + "]." );
			return;			
		}
		
		Project antProject = getProject();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		// add binary class files, if there are any:
		if (this.binaryLibrariesUpdated) {
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
		System.out.println("compiling for device [" +  device.getIdentifier() + "]." );
		// init javac task:
		CompilerTask compiler = this.buildSetting.getCompiler(evaluator );
		compiler.setProject( antProject );
		if (!compiler.isTaskNameSet()) {
			compiler.setDirectTaskName(getTaskName() + "-javac-" + device.getIdentifier() );
		}
		//javac.target=1.1 or javac.target=1.2 is needed for the preverification:
		if (!compiler.isTargetSet()) {
			compiler.setDirectTarget( this.javacTarget );
		}
		// -source == 1.3 is apparently always needed for J2SE 1.5
		if (!compiler.isSourceSet()) {
			compiler.setDirectSource( this.sourceCompatibility );
		}
		if (this.buildSetting.isDebugEnabled() && !compiler.isDebugSet()) {
			compiler.setDirectDebug(true);
		}
		if ( this.buildSetting.isInCompilerMode() && ! this.buildSetting.doPreverifyInCompilerMode()) { // && !this.doObfuscate is not needed
			targetDir = this.buildSetting.getCompilerDestDir();
		} else {
			targetDir = new File( targetDirName );
		}
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
		if (!compiler.isBootClassPathSet()) {
			Path bootClassPath;
			if (device.isMidp1()) {
				 bootClassPath = this.midp1BootClassPath;
			} else {
				if (device.isCldc10()) {
					bootClassPath = this.midp2BootClassPath;
				} else {
					bootClassPath = this.midp2Cldc11BootClassPath;
				}
			}
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
			device.setBootClassPath( bootClassPath.toString() );
			compiler.setDirectBootclasspath( bootClassPath );
		} else {
			device.setBootClassPath( compiler.getBootclasspath().toString() );
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
		try {
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
							"has been used: [" + device.getClassPath() + "].");
					throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
				} else {
					System.out.println("When an API-class was not found, you might need to define where to find the device-APIs. Following classpath has been used: [" + device.getClassPath() + "].");
					throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
				}
			} else {
				System.out.println("If an error occured in the J2ME Polish packages, please try a clean rebuild - e.g. [ant clean] and then [ant].");
				System.out.println("Alternatively you might need to define where to find the device-APIs. Following classpath has been used: [" + device.getClassPath() + "].");
				throw new BuildException( "Unable to compile source code for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
			}
		}
		if (this.polishLogger != null) {
			this.polishLogger.setCompileMode( false );
		}
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
	private void postCompile( Device device, Locale locale ) {
		File classDir = new File( device.getClassesDir() );
		PostCompiler[] compilers = getActivePostCompilers();
		for (int i = 0; i < compilers.length; i++) {
			PostCompiler postCompiler = compilers[i];
			postCompiler.postCompile( classDir, device );
		}
	}


	/**
	 * Obfuscates the compiled source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 * @param locale the current localization
	 */
	private void obfuscate( Device device, Locale locale ) {
		if (this.polishLogger != null) {
			this.polishLogger.setObfuscateMode( true );
		}
		Path bootPath;
		if (device.isMidp1()) {
			bootPath = this.midp1BootClassPath;
		} else {
			if (device.isCldc10()) {
				bootPath = this.midp2BootClassPath;
			} else {
				bootPath = this.midp2Cldc11BootClassPath;
			}
		}
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
	private void preverify( Device device, Locale locale ) {
		System.out.println("preverifying for device [" + device.getIdentifier() + "].");
		
		File preverify = this.buildSetting.getPreverify();
		String classPath;
		if (device.isMidp1()) {
			classPath = this.midp1BootClassPath.toString();
		} else {
			if (device.isCldc10()) {
				classPath = this.midp2BootClassPath.toString();
			} else {
				classPath = this.midp2Cldc11BootClassPath.toString();
			}			
		}
		classPath += File.pathSeparatorChar + device.getClassPath();
		/* File preverfyDir = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "preverfied" );
		device.setPreverifyDir( preverifyDir ); 
		*/
		String cldc = null;
		if (device.isCldc10()) {
			cldc = "-cldc";
		} else {
			cldc = "-nonative";
		}
		String destinationDir = device.getClassesDir();
		if (this.buildSetting.isInCompilerMode()) {
			destinationDir = this.buildSetting.getCompilerDestDir().getAbsolutePath();
		}
		String[] commands = new String[] {
			preverify.getAbsolutePath(), 
			"-classpath", classPath,
			"-d", destinationDir, // destination-dir - default is ./output
			cldc,
			device.getClassesDir()
		};
		StringBuffer commandBuffer = new StringBuffer();
		for (int i = 0; i < commands.length; i++) {
			commandBuffer.append( commands[i] ).append(' ');
		}
		
		try {
			Process preverifyProc = Runtime.getRuntime().exec( commands, null );
			InputStream in = preverifyProc.getErrorStream();
			int ch;
			StringBuffer message = new StringBuffer();
			while ( (ch = in.read()) != -1) {
				message.append((char) ch );
			}
			int exitValue = preverifyProc.waitFor();
			if (exitValue != 0) {
				throw new BuildException("Unable to preverify: " + message.toString()  
					+ " - The exit-status is [" + exitValue + "]\n"
					+ " The call was: [" + commandBuffer.toString() + "]."
				);
			}
		} catch (IOException e ) {
			throw new BuildException("Unable to preverify: " + e.getMessage()
					+ "\n The call was: [" + commandBuffer.toString() + "].", e);
		} catch (InterruptedException e) {
			throw new BuildException("Unable to preverify: " + e.getMessage()
					+ "\n The call was: [" + commandBuffer.toString() + "].", e);
		}
		
	}

	/**
	 * Packages the code and assembles the resources for the application.
	 * 
	 * @param device The device for which the code should be jared.
	 * @param locale the current locale, can be null
	 */
	private void jar( Device device, Locale locale ) {
		File classesDir = new File( device.getClassesDir() );
		
		try {
			// copy resources:
			this.resourceManager.copyResources(classesDir, device, locale);
		} catch (IOException e) {
			System.out.println("creating JAR for device [" + device.getIdentifier() + "]." );
			e.printStackTrace();
			throw new BuildException("Unable to assemble resources: " + e.toString(), e );
		}		

		// retrieve the name of the jar-file:
		String jarName = this.environment.getVariable("polish.jarName");
		File jarFile = new File( this.buildSetting.getDestDir().getAbsolutePath() 
						+ File.separatorChar + jarName );
		device.setJarFile( jarFile );
		String test = this.polishProject.getCapability("polish.license");
		if ( !this.infoSetting.getlicense().equals(test)) {
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
		Attribute[] jadAttributes = this.infoSetting.getManifestAttributes( this.environment.getVariables() );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute attribute = jadAttributes[i];
			attributesByName.put( attribute.getName(), attribute );
		}
		
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
		for (int i = 0; i < midletInfos.length; i++) {
			String info = midletInfos[i];
			attributesByName.put( InfoSetting.NMIDLET + (i+1), new  Attribute(InfoSetting.NMIDLET + (i+1), info ) );
		}
		
		// add user-defined attributes:
		Project antProject = getProject();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		if (this.buildSetting.getJadAttributes() != null) {
			Attribute[] attributes = this.buildSetting.getJadAttributes().getAttributes(antProject, evaluator, this.environment);
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
		Variable[] manifestAttributes = this.buildSetting.filterManifestAttributes( attributesByName, this.environment.getBooleanEvaluator() );
		Jad jad = new Jad( this.environment );
		jad.setAttributes(manifestAttributes);
		
		File manifestFile = new File( device.getClassesDir() 
				+ File.separator + "META-INF" + File.separator + "MANIFEST.MF");
		try {
			if (jarFile.exists()) {
				boolean success = jarFile.delete();
				if (!success) {
					throw new BuildException("Unable to delete the existing JAR-file [" + jarFile.getAbsolutePath() + "], please call \"ant clean\" or remove the folder \"" + jarFile.getParent() + "\"-folder manually." );
				}
			}
			System.out.println("creating JAR file ["+ jarFile.getAbsolutePath() + "].");
			FileUtil.writeTextFile( manifestFile, jad.getContent(), this.buildSetting.getEncoding() );
			Packager packager = (Packager) this.extensionManager.getActiveExtension( ExtensionManager.TYPE_PACKAGER, this.environment );
			if (packager == null ) {
				packager = new DefaultPackager();
			}
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
	private void jad(Device device, Locale locale) {		
		// now create the JAD file:
		HashMap attributesByName = new HashMap();
		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getJadAttributes( this.environment.getVariables() );
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute var  = jadAttributes[i];
			attributesByName.put( var.getName(), 
					new Attribute(var.getName(), var.getValue() ) );
		}
		
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
		for (int i = 0; i < midletInfos.length; i++) {
			String info = midletInfos[i];
			attributesByName.put( InfoSetting.NMIDLET + (i+1), 
						new Attribute(InfoSetting.NMIDLET + (i+1), info) );
		}
		
		// add size of jar:
		long size = device.getJarFile().length();
		attributesByName.put(InfoSetting.MIDLET_JAR_SIZE,
					new Attribute( InfoSetting.MIDLET_JAR_SIZE, "" + size ) );
		
		// add user-defined attributes:
		Project antProject = getProject();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		if (this.buildSetting.getJadAttributes() != null) {
			Attribute[] attributes = this.buildSetting.getJadAttributes().getAttributes(antProject, evaluator, this.environment);
			for (int i = 0; i < attributes.length; i++) {
				Attribute attribute = attributes[i];
				if ( attribute.targetsJad() ) {
					attributesByName.put(attribute.getName(),
							new Attribute( attribute.getName(), attribute.getValue() ) );
				}
			}
		}
		
		// sort and filter the JAD attributes if requested:
		Attribute[] filteredAttributes = this.buildSetting.filterJadAttributes(attributesByName, this.environment.getBooleanEvaluator() );
		Jad jad = new Jad( this.environment );
		jad.setAttributes( filteredAttributes );
		
		String jadPath = this.environment.getVariable("polish.jadPath");
		File jadFile = new File( jadPath );
		try {
			System.out.println("creating JAD file [" + jadFile.getAbsolutePath() + "].");
			FileUtil.writeTextFile(jadFile, jad.getContent(), this.buildSetting.getEncoding() );
		} catch (IOException e) {
			throw new BuildException("Unable to create JAD file [" + jadFile.getAbsolutePath() +"] for device [" + device.getIdentifier() + "]: " + e.getMessage() );
		}
	}
	
	/**
	 * Calls java-extension.
	 * These can be used for example to sign the MIDlet.
	 * 
	 * @param device the current device
	 * @param locale
	 */
	private void callExtensions( Device device, Locale locale ) {
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

	private void finalize( Device device, Locale locale ) {
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
				try {
					Finalizer finalizer = (Finalizer) this.extensionManager.getExtension( ExtensionManager.TYPE_FINALIZER, finalizerName, this.environment );
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
	 * @param locale
	 */
	private void runEmulator( Device device, Locale locale ) {
		if ( this.emulatorSetting.isActive(getProject()) ) {
			BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
			Project antProject = getProject();
			// get currently active source directories:
			ArrayList sourceDirsList = new ArrayList();
			for (int i = 0; i < this.sourceSettings.length; i++) {
				SourceSetting setting = this.sourceSettings[i];
				if (setting.isActive(evaluator, antProject)) {
					sourceDirsList.add( setting.getDir() );
				}
			}
			File[] sourceDirs = (File[]) sourceDirsList.toArray( new File[ sourceDirsList.size() ] );
			Emulator emulator = Emulator.createEmulator(device, this.emulatorSetting, this.environment, getProject(), evaluator, this.wtkHome, sourceDirs );
			if (emulator != null) {
				if (this.runningEmulators == null) {
					this.runningEmulators = new ArrayList();
				}
				this.runningEmulators.add( emulator );
			}
			
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
	

}
