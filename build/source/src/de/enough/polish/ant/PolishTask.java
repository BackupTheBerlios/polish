/*
 * Created on 21-Jan-2003 at 15:15:56.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant;

import de.enough.polish.*;
import de.enough.polish.ant.build.*;
import de.enough.polish.ant.emulator.Emulator;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.ant.info.InfoSetting;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.preprocess.*;
import de.enough.polish.preprocess.custom.PolishPreprocessor;
import de.enough.polish.preprocess.custom.TranslationPreprocessor;
import de.enough.polish.resources.ResourceManager;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;
import org.jdom.JDOMException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Manages a J2ME project from the preprocessing to the packaging and obfuscation.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishTask extends ConditionalTask {

	private static final String VERSION = "1.2-pre3";

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
	private File[] sourceDirs;
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
	private TextFile styleSheetFile;
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

	private Variable[] conditionalVariables;

	private boolean binaryLibrariesUpdated;

	private File binaryLibrariesDir;

	private JavaExtension[] javaExtensions;

	private CssAttributesManager cssAttributesManager;

	private PolishLogger polishLogger;
	private ArrayList runningEmulators;

	private ResourceManager resourceManager;

	private Locale[] supportedLocales;

	private TranslationPreprocessor translationPreprocessor;

	
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
		this.infoSetting = setting;
	}
	
	public void addConfiguredDeviceRequirements( Requirements requirements ) {
		if (requirements.isActive(this.project)) {
			this.deviceRequirements = requirements;
		}
	}
	
	/**
	 * Creates and adds the build settings for this project.
	 * 
	 * @return the new build setting.
	 */
	public BuildSetting createBuild() {
		this.buildSetting = new BuildSetting( this.project );
		return this.buildSetting;
	}
	
	/**
	 * Creates and adds a new run-setting for this project.
	 * 
	 * @return the new runsetting.
	 */
	public EmulatorSetting createEmulator() {
		this.emulatorSetting = new EmulatorSetting( this.project );
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
			checkSettings();
			initProject();
			selectDevices();
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
		preprocess( device, locale );
		compile( device );
		if (this.doObfuscate) {
			obfuscate( device, locale );
		}
		if (this.buildSetting.isInCompilerMode()) {
			finishProject();
			System.out.println();
			System.out.println("Successfully processed the activated compilerMode of J2ME Polish.");
			return;
		}
		preverify( device, locale );
		jar( device, locale );
		jad( device, locale );
		if (hasExtensions) {
			callExtensions( device, locale );
		}
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
	private void checkSettings() {
		//System.out.println( this.project.getBaseDir().getAbsolutePath() );
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
			throw new BuildException("Midlets need to be defined in the build section with either <midlets> or <midlet>.");
		}
		// now check if the midlets do exist:
		File[] sources = this.buildSetting.getSourceDirs();
		for (int i = 0; i < midlets.length; i++) {
			Midlet midlet = midlets[i];
			String fileName = TextUtil.replace( midlet.getClassName(), '.', File.separatorChar) + ".java";
			boolean midletFound = false;
			for (int j = 0; j < sources.length; j++) {
				File sourceDir = sources[j];
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
		// check if the ant-property WTK_HOME has been set:
		//e.g. with: <property name="wtk.home" value="c:\Java\wtk-1.0.4"/>
		this.wtkHome = this.project.getProperty("wtk.home");
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
	private void initProject() {
		// create debug manager:
		boolean isDebugEnabled = this.buildSetting.isDebugEnabled(); 
		DebugManager debugManager = null;
		if (isDebugEnabled) {
			try {
				debugManager = new DebugManager( this.buildSetting.getDebugSetting() );
			} catch (BuildException e) {
				throw new BuildException( e.getMessage(), e );
			}
		}
		// create project settings:
		this.polishProject = new PolishProject( this.buildSetting.usePolishGui(), isDebugEnabled, debugManager );
		if (debugManager != null && debugManager.isVerbose()) {
			this.polishProject.addFeature("debugVerbose");
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
		if (debugManager != null && this.buildSetting.getDebugSetting().useGui()) {
			this.polishProject.addFeature("useDebugGui");
		}
		FullScreenSetting fullScreenSetting = this.buildSetting.getFullScreenSetting();
		if (fullScreenSetting != null) {
			if (fullScreenSetting.isMenu()) {
				this.polishProject.addFeature("useMenuFullScreen");
				this.polishProject.addFeature("useFullScreen");
			} else if (fullScreenSetting.isEnabled()) {
				this.polishProject.addFeature("useFullScreen");
			}
		}
		// add all ant properties if desired: 
		if (this.buildSetting.includeAntProperties()) {
			Hashtable antProperties = this.project.getProperties();
			Set keySet = antProperties.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				this.polishProject.addDirectCapability( key, (String) antProperties.get(key) );
			}
		}
		// add all variables from the build.xml:
		Variable[] variables = this.buildSetting.getVariables();
		if (variables != null) {
			ArrayList conditionalVarsList = new ArrayList();
			for (int i = 0; i < variables.length; i++) {
				Variable var = variables[i];
				//System.out.println("adding variable [" + var.getName() + "]." );
				if (var.hasCondition()) {
					conditionalVarsList.add( var );
				} else {
					this.polishProject.addDirectCapability( var );
				}
			}
			this.conditionalVariables = (Variable[]) conditionalVarsList.toArray( new Variable[ conditionalVarsList.size() ]); 
		}
		// add all symbols from the build.xml:
		String symbolDefinition = this.buildSetting.getSymbols();
		if (symbolDefinition != null) {
			String[] symbols = TextUtil.splitAndTrim( symbolDefinition, ',' );
			for (int i = 0; i < symbols.length; i++) {
				this.polishProject.addDirectFeature( symbols[i] );
			}
		}
		
		// create LibraryManager:
		try {
			this.libraryManager = new LibraryManager( this.project.getProperties(), this.buildSetting.getApiDir().getAbsolutePath(), this.wtkHome, this.buildSetting.getPreverify().getAbsolutePath(), this.buildSetting.openApis() );
		} catch (JDOMException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create api manager: " + e.getMessage(), e );
		}

		// create vendor/group/device manager:
		try {
			VendorManager vendorManager = new VendorManager( this.polishProject, this.buildSetting.openVendors());
			DeviceGroupManager groupManager = new DeviceGroupManager( this.buildSetting.openGroups() ); 
			this.deviceManager = new DeviceManager( vendorManager, groupManager, this.libraryManager, this.buildSetting.openDevices() );
		} catch (JDOMException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (IOException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		} catch (InvalidComponentException e) {
			throw new BuildException("unable to create device manager: " + e.getMessage(), e );
		}
		
		// create CSS attributes manager:
		if (this.buildSetting.usePolishGui()) {
			this.cssAttributesManager = new CssAttributesManager( this.buildSetting.openStandardCssAttributes() );
			InputStream is = this.buildSetting.openCustomCssAttributes();
			if (is != null) {
				this.cssAttributesManager.addCssAttributes(is);
			}
		}
		
		// create preprocessor:
		this.preprocessor = new Preprocessor( this.polishProject, null, null, null, false, true, null );
		// init line processors:
		PreprocessorSetting[] settings = this.buildSetting.getPreprocessors();
		CustomPreprocessor[] processors = new CustomPreprocessor[ settings.length + 1];
		// add the default line processor:
		CustomPreprocessor lineProcessor = new PolishPreprocessor();
		lineProcessor.init(this.preprocessor);
		processors[0] = lineProcessor;
		for (int i = 0; i < settings.length; i++) {
			PreprocessorSetting setting = settings[i];
			processors[ i + 1] = CustomPreprocessor.getInstance(setting, this.preprocessor, this.project );
		}
		this.preprocessor.setCustomPreprocessors( processors );
		
		//	initialise the preprocessing-source-directories:
		DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setIncludes( new String[]{"**/*.java"} );
		File[] dirs = this.buildSetting.getSourceDirs();
		this.sourceDirs = new File[ dirs.length];
		this.sourceFiles = new TextFile[ dirs.length][];
		if (this.buildSetting.getPolishDir() != null) {
			// there is an explicit J2ME Polish directory:
			this.polishSourceDir = this.buildSetting.getPolishDir();
			dirScanner.setBasedir(this.polishSourceDir);
			dirScanner.scan();
			this.polishSourceFiles = getTextFiles( this.polishSourceDir,  dirScanner.getIncludedFiles() );
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
				this.polishSourceFiles = getTextFiles( "src", fileNames, lastModificationTime );
			} catch (IOException e) {
				throw new BuildException("Unable to load the J2ME source files from enough-j2mepolish-build.jar: " + e.getMessage(), e );
			}
		}
		
		// load the normal source files:
		for (int i = 0; i < dirs.length; i++) {
			File dir = dirs[i];
			if (!dir.exists()) {
				throw new BuildException("The source-directory [" + dir.getAbsolutePath() + "] does not exist. Please check your settings in the [sourceDir] attribute of the <build> element.");
			}
			this.sourceDirs[i] = dir; 
			dirScanner.setBasedir(dir);
			dirScanner.scan();
			this.sourceFiles[i] = getTextFiles( dir,  dirScanner.getIncludedFiles() );
		}
		if (this.buildSetting.usePolishGui() && this.styleSheetFile == null) {
			throw new BuildException("Did not find the file [StyleSheet.java] of the J2ME Polish GUI framework. Please adjust the [polishDir] attribute of the <build> element in the [build.xml] file. The [polishDir]-attribute should point to the directory which contains the J2ME Polish-Java-sources.");
		}
		
		// load third party binary libraries, if any.
		// When there are third party libraries, they will all be extracted
		// and copied to the build/binary folder for easier integration:
		File[] binaryLibraries = this.buildSetting.getBinaryLibraries();
		if (binaryLibraries != null) {
			File targetDir = new File( this.buildSetting.getWorkDir().getAbsolutePath() +
					File.separatorChar + "binary");
			String cacheDirName = this.buildSetting.getWorkDir().getAbsolutePath() +
				File.separatorChar + "binarycache" + File.separatorChar;
			this.binaryLibrariesDir = targetDir;
			for (int i = 0; i < binaryLibraries.length; i++) {
				File lib = binaryLibraries[i];
				if (lib.isDirectory()) {
					// a directory can contain library-files (jar, zip) as well
					// as plain class or resource files. Each library-file
					// will be extracted whereas other files will just be copied
					// to the build/binary folder. 
					DirectoryScanner binaryScanner = new DirectoryScanner();
					binaryScanner.setBasedir( lib );
					// just include all files in the directory:
					binaryScanner.scan();
					String[] includedFiles = binaryScanner.getIncludedFiles();
					for (int j = 0; j < includedFiles.length; j++) {
						String fileName = includedFiles[j];
						File file = new File( lib.getAbsolutePath()
								+ File.separatorChar + fileName );
						if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
							// this is a library file:
							// only extract it when the original is newer than the copy:
							File cacheCopy = new File( cacheDirName + fileName );
							if ( (!cacheCopy.exists())
									|| (file.lastModified() > cacheCopy.lastModified())) {
								this.binaryLibrariesUpdated = true;
								try {
									// copy the library to the cache:
									FileUtil.copy(file, cacheCopy );
									// unzip / unjar the content:
									JarUtil.unjar(file, targetDir);
								} catch (IOException e) {
									e.printStackTrace();
									throw new BuildException("Unable to extract the binary class files from the library [" + file.getAbsolutePath() + "]: " + e.toString(), e );
								}
								
							}
						} else {
							// this is a normal class or resource file:
							try {
								File targetFile = new File( targetDir.getAbsolutePath()
										+ File.separatorChar + fileName );
								// copy the file only when it is newer
								// than the existing copy: 
								if ( (!targetFile.exists()) 
										|| (file.lastModified() > targetFile.lastModified()) ) {
									this.binaryLibrariesUpdated = true;
									FileUtil.copy(file, targetFile);
								}
							} catch (IOException e) {
								e.printStackTrace();
								throw new BuildException("Unable to copy the binary class files from the library [" + lib.getAbsolutePath() + "]: " + e.toString(), e );
							}
						}
					}
				} else {
					// this is a library (jar or zip) file:
					// copy only when the original is newer than the cached copy: 
					File cacheCopy = new File( cacheDirName + lib.getName() );
					if ( (!cacheCopy.exists())
							|| (lib.lastModified() > cacheCopy.lastModified())) {
						try {
							this.binaryLibrariesUpdated = true;
							// copy the library to the cache:
							FileUtil.copy(lib, cacheCopy );
							// unzip / unjar the content:
							JarUtil.unjar(lib, targetDir);
						} catch (IOException e) {
							e.printStackTrace();
							throw new BuildException("Unable to extract the binary class files from the library [" + lib.getAbsolutePath() + "]: " + e.toString(), e );
						}
						
					}
				}
			}
		} // done preparing of binary libraries.
		
		// init boot class path:
		this.midp1BootClassPath = new Path( this.project, this.buildSetting.getMidp1Path().getAbsolutePath());
		this.midp2BootClassPath = new Path( this.project, this.buildSetting.getMidp2Path().getAbsolutePath());
		this.midp2Cldc11BootClassPath = new Path( this.project, this.buildSetting.getMidp2Cldc11Path().getAbsolutePath());
		
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
					Obfuscator obfuscator = Obfuscator.getInstance( obfuscatorSetting, this.project, this.buildSetting.getApiDir(), this.libraryManager ); 
					obfuscatorsList.add( obfuscator );
				}
				
			}
			this.doObfuscate = (obfuscatorsList.size() > 0);
			if (this.doObfuscate) {
				this.obfuscators = (Obfuscator[]) obfuscatorsList.toArray( new Obfuscator[ obfuscatorsList.size() ] );
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
		this.resourceManager = new ResourceManager( this.buildSetting.getResourceSetting(), this.project, this.preprocessor.getBooleanEvaluator() );
		this.supportedLocales = this.resourceManager.getLocales();
		if (this.supportedLocales != null) {
			// add the preprocessor for translations:
			PreprocessorSetting setting = new PreprocessorSetting();
			setting.setClass("de.enough.polish.preprocess.custom.TranslationPreprocessor");
			this.translationPreprocessor = (TranslationPreprocessor) CustomPreprocessor.getInstance(setting, this.preprocessor, this.project);
			this.preprocessor.addCustomPreprocessors( this.translationPreprocessor );
		}
		
		//check if there has been an error at the last run:
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
		Vector buildListeners = this.project.getBuildListeners();
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
					classPathTranslationsMap.put( file.getFileName(), file.getFile().getAbsolutePath() );
				}
			}
			this.polishLogger = new PolishLogger(logger, classPathTranslationsMap );
			this.project.addBuildListener( this.polishLogger );
			this.project.removeBuildListener(logger);
		} else {
			System.err.println("Warning: unable to replace Ant-logger. Compile errors will point to the preprocessed files instead of the original sources.");
		}
	}

	/**
	 * Creates an array of text files.
	 * 
	 * @param baseDir The base directory.
	 * @param fileNames The full names of the files.
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(File baseDir, String[] fileNames) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			try {
				TextFile file = new TextFile( baseDir.getAbsolutePath(), fileName );
				if (fileName.endsWith("StyleSheet.java") && fileName.startsWith("de")) {
					this.styleSheetFile = file;
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
	 * @return an array of text-files
	 */
	private TextFile[] getTextFiles(String baseDir, String[] fileNames, long lastModificationTime) 
	{
		TextFile[] files = new TextFile[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			TextFile file = new TextFile( baseDir, fileName, lastModificationTime, this.resourceUtil );
			if ( fileName.endsWith("StyleSheet.java") && fileName.startsWith("de")) {
				this.styleSheetFile = file;
			}
			files[i] = file;
		}
		return files;
	}
	
	/**
	 * Selects the actual devices for which optimal applications should be generated.
	 */
	private void selectDevices() {
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
		}
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
			String targetDir = this.buildSetting.getWorkDir().getAbsolutePath() 
				+ File.separatorChar + device.getVendorName() 
				+ File.separatorChar + device.getName();
			if (locale != null) {
				targetDir += File.separatorChar + locale.toString();
			}
			device.setBaseDir( targetDir );
			targetDir += File.separatorChar + "source";
			device.setSourceDir(targetDir);
			// initialise the preprocessor:
			this.preprocessor.setTargetDir( targetDir );
			// set variables and symbols:
			this.preprocessor.setSymbols( device.getFeatures() );
			this.preprocessor.setVariables( device.getCapabilities() );
			this.preprocessor.addVariable( "polish.identifier", device.getIdentifier() );
			this.preprocessor.addVariable( "polish.name", device.getName() );
			this.preprocessor.addVariable( "polish.vendor", device.getVendorName() );
			this.preprocessor.addVariable( "polish.version", this.infoSetting.getVersion() );
			long lastLocaleModification = 0;
			// set localization-variables:
			if (locale != null) {
				this.preprocessor.addVariable("polish.locale", locale.toString() );
				this.preprocessor.addVariable("polish.language", locale.getLanguage() );
				this.preprocessor.addVariable("polish.country", locale.getCountry() );
				
				// load localized messages, this also sets localized variables automatically:
				TranslationManager translationManager = this.resourceManager.getTranslationManager(device, locale, this.preprocessor.getVariables() );
				this.translationPreprocessor.setTranslationManager( translationManager );
				lastLocaleModification = translationManager.getLastModificationTime();
			}
			// set info-variables:
			String jarName = this.infoSetting.getJarName();
			jarName = PropertyUtil.writeProperties(jarName, this.preprocessor.getVariables());
			this.preprocessor.addVariable( "polish.jarName", jarName );
			String jadName = jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad";
			this.preprocessor.addVariable( "polish.jadName", jadName );
			
			// set conditional variables:
			if (this.conditionalVariables != null) {
				// add variables which fulfill the conditions: 
				BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
				for (int i = 0; i < this.conditionalVariables.length; i++) {
					Variable var = this.conditionalVariables[i];
					if (var.isConditionFulfilled( evaluator, this.project )) {
						this.preprocessor.addVariable(var.getName(), var.getValue() );
					}
				}
			}
			// get the last modfication time of the build.xml file
			// so that it can be checked whether there are any changes at all:
			File buildXml = new File( this.project.getBaseDir().getAbsolutePath() 
						+ File.separatorChar + "build.xml" );
			long buildXmlLastModified = buildXml.lastModified();
			// check if the polish gui is used at all:
			boolean usePolishGui = this.buildSetting.usePolishGui()
								  && device.supportsPolishGui();
			this.preprocessor.setUsePolishGui(usePolishGui);
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
			// preprocess each source file:
			for (int i = 0; i < this.sourceDirs.length; i++) {
				File sourceDir = this.sourceDirs[i];
				TextFile[] files = this.sourceFiles[i];
				processSourceDir(sourceDir, files, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, false);
			} // for each source folder
			this.preprocessor.notifyPolishPackageStart();
			// now process the J2ME package files:
			processSourceDir(this.polishSourceDir, this.polishSourceFiles, device, usePolishGui, targetDir, buildXmlLastModified, lastCssModification, true);
			// notify preprocessor about the end of preprocessing:
			this.preprocessor.notifyDeviceEnd( device, usePolishGui );
			
			
			// now all files have been preprocessed.
			// Now the StyleSheet.java file needs to be written,
			// but only when the polish GUI should be used:
			if (usePolishGui) {
				// check if the CSS declarations have changed since the last run:
				File targetFile = new File( targetDir + File.separatorChar + this.styleSheetFile.getFileName() );				
				boolean cssIsNew = (!targetFile.exists())
					|| ( lastCssModification > targetFile.lastModified() )
					|| ( buildXmlLastModified > targetFile.lastModified() );
				if (cssIsNew) {
					//System.out.println("CSS is new and the style sheet will be generated.");
					if (this.styleSheetCode == null) {
						// the style sheet has not been preprocessed:
						this.styleSheetCode = new StringList( this.styleSheetFile.getContent() );
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
					this.styleSheetFile.saveToDir(targetDir, this.styleSheetCode.getArray(), false );
					this.numberOfChangedFiles++;
				//} else {
				//	System.out.println("CSSS is not new - last CSS modification == " + lastCssModification + " <= StyleSheet.java.lastModified() == " + targetFile.lastModified() );
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
			e.printStackTrace();
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
	throws IOException
	{
		this.preprocessor.addVariable( "polish.source", sourceDir.getAbsolutePath() );
		//System.out.println("current source dir: " + sourceDir );
		// preprocess each file in that source-dir:
		for (int j = 0; j < files.length; j++) {
			TextFile file = files[j];
			// check if file needs to be preprocessed at all:
			long sourceLastModified = file.lastModified();
			File targetFile = new File( targetDir
					+ File.separatorChar + file.getFileName() );
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
					 || ( this.preprocessor.isInPreprocessQueue( file.getFileName() ) ); 
			boolean preprocess = ( saveInAnyCase )
					 || ( lastCssModification > targetLastModified);
			if (   preprocess ) {
				// preprocess this file:
				StringList sourceCode = new StringList( file.getContent() );
				// generate the class-name from the file-name:
				String className = file.getFileName();
				if (className.endsWith(".java")) {
					className = className.substring(0, className.length() - 5 );
					// in a jarfile the files always have a '/' as a path-seperator:
					className = TextUtil.replace(className, '/', '.' );
				}
				className = TextUtil.replace(className, File.separatorChar, '.' );
				// set the StyleSheet.display variable in all MIDlets
				if ( (this.midletClassesByName.get( className ) != null) 
						&& usePolishGui) {
					insertDisplaySetting( className, sourceCode );
					sourceCode.reset();
				}
				int result = this.preprocessor.preprocess( className, sourceCode );
				// only think about saving when the file should not be skipped 
				// and when it is not the StyleSheet.java file:
				if (file == this.styleSheetFile ) {
					this.styleSheetCode = sourceCode;
				} else  if (result != Preprocessor.SKIP_FILE) {
					if (!isInPolishPackage) {
						sourceCode.reset();
						// now replace the import statements:
						boolean changed = this.importConverter.processImports(usePolishGui, device.isMidp1(), sourceCode);
						if (changed) {
							result = Preprocessor.CHANGED;
						}
					}
					// save modified file:
					if ( ( saveInAnyCase ) 
					  || (result == Preprocessor.CHANGED) ) 
					{
						//System.out.println( "preprocessed [" + className + "]." );
						file.saveToDir(targetDir, sourceCode.getArray(), false );
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
				if (!sourceCode.hasNext()) {
					throw new BuildException("Unable to process MIDlet [" + className + "]: startApp method is not opened with '{': line [" + (++lineIndex) + "].");
				}
				sourceCode.insert("de.enough.polish.ui.StyleSheet.display = javax.microedition.lcdui.Display.getDisplay( this );");
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
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < classPaths.length; i++) {
			String path = classPaths[i];
			buffer.append( path )
			      .append( File.pathSeparatorChar );
		}
		device.setClassPath( buffer.toString() );

		// setting target directory:
		String targetDirName = device.getBaseDir() + File.separatorChar + "classes";
		device.setClassesDir( targetDirName );
		
		if (device.getNumberOfChangedFiles() == 0 && !this.lastRunFailed) {
			System.out.println("nothing to compile for device [" +  device.getIdentifier() + "]." );
			return;			
		}
		System.out.println("compiling for device [" +  device.getIdentifier() + "]." );
		
		// add binary class files, if there are any:
		if (this.binaryLibrariesUpdated) {
			try {
				FileUtil.copyDirectoryContents( this.binaryLibrariesDir, targetDirName, true );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to copy binary class files: " + e.toString() + ". Please report this error to j2mepolish@enough.de.", e );
			}
		}
		
		// init javac task:
		Javac javac = new Javac();
		javac.setProject( this.project );
		javac.setTaskName(getTaskName() + "-javac-" + device.getIdentifier() );
		//javac.target=1.1 or javac.target=1.2 is needed for the preverification:
		javac.setTarget( this.javacTarget );
		if (this.javacTarget == BuildSetting.TARGET_1_1) {
			// -source == 1.3 is needed for J2SE 1.5, but only when target == 1.1
			javac.setSource( this.sourceCompatibility );
		}
		if (this.buildSetting.isDebugEnabled()) {
			javac.setDebug(true);
			javac.setDebugLevel("lines,vars,source");
		}
		File targetDir;
		if (this.buildSetting.isInCompilerMode() && !this.doObfuscate) {
			targetDir = this.buildSetting.getCompilerDestDir();
		} else {
			targetDir = new File( targetDirName );
		}
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		javac.setDestdir( targetDir );
		javac.setSrcdir(new Path( this.project,  device.getSourceDir() ) );
		//javac.setSourcepath(new Path( this.project,  "" ));
		if (device.isMidp1()) {
			javac.setBootclasspath(this.midp1BootClassPath);
		} else {
			if (device.isCldc10()) {
				javac.setBootclasspath(this.midp2BootClassPath);
			} else {
				javac.setBootclasspath(this.midp2Cldc11BootClassPath);
			}
		}
		if (device.getClassPath() != null) {
			javac.setClasspath( new Path(this.project, device.getClassPath() ) );
		}
		// start compile:
		if (this.polishLogger != null) {
			this.polishLogger.setCompileMode( true );
		}
		try {
			javac.execute();
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
	 * Obfuscates the compiled source code.
	 *  
	 * @param device The device for which the obfuscation should be done.
	 * @param locale
	 */
	private void obfuscate( Device device, Locale locale ) {
		System.out.println("obfuscating for device [" + device.getIdentifier() + "].");
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
			throw new BuildException("Unable to prepare the obfuscation-jar: " + e.getMessage(), e );
		}
		//System.out.println("Jaring took " + ( System.currentTimeMillis() - time) + " ms.");
		
		
		
		File destFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
				+ File.separatorChar + "dest.jar");
		
		// start the obfuscation:
		int maxIndex = this.obfuscators.length - 1;
		for (int i=0; i <= maxIndex ; i++ ) {
			Obfuscator obfuscator = this.obfuscators[i];
			obfuscator.obfuscate(device, sourceFile, destFile, this.preserveClasses, bootPath );
			if ( i != maxIndex ) {
				sourceFile = destFile;
				destFile = new File( this.buildSetting.getWorkDir().getAbsolutePath()
						+ File.separatorChar + "dest" + (i + 1) + ".jar");
			}
		}
		
		
		//time = System.currentTimeMillis();
		//unjar destFile to build/[vendor]/[name]/obfuscated:
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
				targetDir.delete();
			}
			JarUtil.unjar( destFile,  targetDir  );
		} catch (IOException e) {
			throw new BuildException("Unable to prepare the obfuscation-jar: " + e.getMessage(), e );
		}
		
		if (this.polishLogger != null) {
			this.polishLogger.setObfuscateMode( false );
		}
	}

	/**
	 * Preverifies the compiled and a\obfuscated code.
	 *  
	 * @param device The device for which the preverification should be done.
	 * @param locale
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
		String[] commands = new String[] {
			preverify.getAbsolutePath(), 
			"-classpath", classPath,
			"-d", device.getClassesDir(), // destination-dir - default is ./output
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
		System.out.println("creating JAR for device [" + device.getIdentifier() + "]." );
		File classesDir = new File( device.getClassesDir() );
		
		try {
			// copy resources:
			this.resourceManager.copyResources(classesDir, device, locale);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to assemble resources: " + e.toString(), e );
		}		

		// retrieve the name of the jar-file:
		String jarName = this.preprocessor.getVariable("polish.jarName");
		File jarFile = new File( this.buildSetting.getDestDir().getAbsolutePath() 
						+ File.separatorChar + jarName );
		device.setJarFile( jarFile );
		String test = this.polishProject.getCapability("polish.license");
		if ( !this.infoSetting.getlicense().equals(test)) {
			throw new BuildException("Encountered invalid license.");
		}
		
		// create manifest:
		Jad jad = new Jad( this.preprocessor.getVariables() );
		boolean useAttributesFilter = this.buildSetting.hasManifestAttributesFilter();
		HashMap attributesByName = null;
		if (useAttributesFilter) {
			attributesByName = new HashMap();
			attributesByName.put( "Manifest-Version", new Attribute( "Manifest-Version", "1.0" ) ); 
		} else {
			jad.addAttribute( "Manifest-Version", "1.0" );
		}
		// set MicroEdition-Profile:
		String midp = InfoSetting.MIDP1;
		if (device.isMidp2()) {
			midp = InfoSetting.MIDP2;
		}
		if (useAttributesFilter) {
			attributesByName.put( InfoSetting.MICRO_EDITION_PROFILE, new Attribute(InfoSetting.MICRO_EDITION_PROFILE, midp) );
		} else {
			jad.addAttribute( InfoSetting.MICRO_EDITION_PROFILE, midp );
		}

		// set MicroEdition-Configuration:
		String config = InfoSetting.CLDC1_0;
		if (!device.isCldc10()) {
			config = InfoSetting.CLDC1_1;
		}
		if (useAttributesFilter) {
			attributesByName.put( InfoSetting.MICRO_EDITION_CONFIGURATION, new  Attribute(InfoSetting.MICRO_EDITION_CONFIGURATION, config) );
		} else {
			jad.addAttribute( InfoSetting.MICRO_EDITION_CONFIGURATION, config );
		}

		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getManifestAttributes();
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute attribute = jadAttributes[i];
			if (useAttributesFilter) {
				attributesByName.put( attribute.getName(), attribute );
			} else {
				jad.addAttribute( attribute );
			}
		}
		
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
		for (int i = 0; i < midletInfos.length; i++) {
			String info = midletInfos[i];
			if (useAttributesFilter) {
				attributesByName.put( InfoSetting.NMIDLET + (i+1), new  Attribute(InfoSetting.NMIDLET + (i+1), info ) );
			} else {
				jad.addAttribute( InfoSetting.NMIDLET + (i+1), info );
			}
		}
		
		// add user-defined attributes:
		Attribute[] attributes = this.buildSetting.getJadAttributes();
		BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			if (attribute.targetsManifest() && attribute.isConditionFulfilled(evaluator, this.project)) {
				if (useAttributesFilter) {
					attributesByName.put(attribute.getName(),
							attribute );
					
				} else {
					jad.addAttribute( attribute );
				}
			}
		}
		
		//add polish version:
		if (useAttributesFilter) {
			attributesByName.put( "Polish-Version", new Variable("Polish-Version", VERSION ) );
		} else {
			jad.addAttribute( "Polish-Version", VERSION );
		}
		
		// sort and filter the attributes if this is requested:
		if (useAttributesFilter) {
			Variable[] manifestAttributes = this.buildSetting.filterManifestAttributes(attributesByName);
			jad.setAttributes(manifestAttributes);
		}
		File manifestFile = new File( device.getClassesDir() 
				+ File.separator + "META-INF" + File.separator + "MANIFEST.MF");
		try {
			FileUtil.writeTextFile( manifestFile, jad.getContent() );		
			JarUtil.jar(classesDir, jarFile, true );
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
		System.out.println("creating JAD for device [" + device.getIdentifier() + "].");
		Jad jad = new Jad( this.preprocessor.getVariables() );
		HashMap attributesByName = null;
		boolean useAttributesFilter = this.buildSetting.hasJadAttributesFilter();
		if (useAttributesFilter) {
			attributesByName = new HashMap();
		}
		// add info attributes:
		Attribute[] jadAttributes = this.infoSetting.getJadAttributes();
		for (int i = 0; i < jadAttributes.length; i++) {
			Attribute var  = jadAttributes[i];
			if (useAttributesFilter) {
				attributesByName.put( var.getName(), 
					new Attribute(var.getName(), var.getValue() ) );
			} else {
				jad.addAttribute( var );
			}
		}
		
		// add build properties - midlet infos:
		String[] midletInfos = this.buildSetting.getMidletInfos( this.infoSetting.getIcon() );
		for (int i = 0; i < midletInfos.length; i++) {
			String info = midletInfos[i];
			if (useAttributesFilter) {
				attributesByName.put( InfoSetting.NMIDLET + (i+1), 
						new Attribute(InfoSetting.NMIDLET + (i+1), info) );
			} else {
				jad.addAttribute( InfoSetting.NMIDLET + (i+1), info );
			}
		}
		
		// add size of jar:
		long size = device.getJarFile().length();
		if (useAttributesFilter) {
			attributesByName.put(InfoSetting.MIDLET_JAR_SIZE,
					new Attribute( InfoSetting.MIDLET_JAR_SIZE, "" + size ) );
		} else {
			jad.addAttribute(  InfoSetting.MIDLET_JAR_SIZE, "" + size );
		}
		
		// add user-defined attributes:
		Attribute[] attributes = this.buildSetting.getJadAttributes();
		BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			if (attribute.targetsJad() && attribute.isConditionFulfilled(evaluator, this.project)) {
				if (useAttributesFilter) {
					attributesByName.put(attribute.getName(),
							new Attribute( attribute.getName(), attribute.getValue() ) );
					
				} else {
					jad.addAttribute( attribute );
				}
			}
		}
		
		// sort and filter the JAD attributes if requested:
		if (useAttributesFilter) {
			Attribute[] filteredAttributes = this.buildSetting.filterJadAttributes(attributesByName);
			jad.setAttributes( filteredAttributes );
		}
		
		String jadName = this.preprocessor.getVariable("polish.jadName");
		File jadFile = new File( this.buildSetting.getDestDir().getAbsolutePath() + File.separatorChar + jadName );
		try {
			FileUtil.writeTextFile(jadFile, jad.getContent() );
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
		HashMap infoProperties = new HashMap();
		infoProperties.put( "polish.identifier", device.getIdentifier() );
		infoProperties.put( "polish.name", device.getName() );
		infoProperties.put( "polish.vendor", device.getVendorName() );
		infoProperties.put( "polish.version", this.infoSetting.getVersion() );
		String jarName = this.infoSetting.getJarName();
		jarName = PropertyUtil.writeProperties(jarName, infoProperties);
		infoProperties.put( "polish.jarName", jarName );
		String jadName = jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad";
		infoProperties.put( "polish.jadName", jadName );
		BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
		
		for (int i = 0; i < this.javaExtensions.length; i++) {
			JavaExtension extension = this.javaExtensions[i];
			if (extension.isActive( evaluator )) {
				System.out.println("Executing <java> extension for device [" + device.getIdentifier() + "]." );
				// now call the extension:
				extension.execute(device, infoProperties);
			}
		}
	}
	
	/**
	 * Launches the emulator if the user wants to.
	 * 
	 * @param device the current device.
	 * @param locale
	 */
	private void runEmulator( Device device, Locale locale ) {
		if ( this.emulatorSetting.isActive(this.project) ) {
			BooleanEvaluator evaluator = this.preprocessor.getBooleanEvaluator();
			HashMap infoProperties = new HashMap();
			infoProperties.put( "polish.identifier", device.getIdentifier() );
			infoProperties.put( "polish.name", device.getName() );
			infoProperties.put( "polish.vendor", device.getVendorName() );
			infoProperties.put( "polish.version", this.infoSetting.getVersion() );
			String jarName = this.infoSetting.getJarName();
			jarName = PropertyUtil.writeProperties(jarName, infoProperties);
			infoProperties.put( "polish.jarName", jarName );
			String jadName = jarName.substring(0, jarName.lastIndexOf('.') ) + ".jad";
			infoProperties.put( "polish.jadName", jadName );
			String destDir = this.buildSetting.getDestDir().getAbsolutePath();
			infoProperties.put( "polish.destDir", destDir );
			String jadPath = destDir + File.separatorChar + jadName;
			infoProperties.put( "polish.jadPath", jadPath );
			String jarPath = destDir + File.separatorChar + jarName;
			infoProperties.put( "polish.jarPath", jarPath );
			Emulator emulator = Emulator.createEmulator(device, this.emulatorSetting, infoProperties, this.project, evaluator, this.wtkHome);
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
	
	/**
	 * <p>Accepts only non CSS-files.</p>
	 *
	 * <p>copyright Enough Software 2004</p>
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
