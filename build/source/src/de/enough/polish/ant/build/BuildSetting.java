/*
 * Created on 22-Jan-2003 at 14:10:02.
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
package de.enough.polish.ant.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Attribute;
import de.enough.polish.BooleanEvaluator;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents the build settings of a polish J2ME project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BuildSetting {
	
	public final static String IMG_LOAD_BACKGROUND = "images.backgroundLoad";
	public final static String IMG_LOAD_FOREGROUND = "images.directLoad";
	public final static String TARGET_1_1 = "1.1";
	public final static String TARGET_1_2 = "1.2";
	
	private static final String DEFAULT_JAD_FILTER_PATTERN = "MIDlet-Name, MIDlet-Version, MIDlet-Vendor, MIDlet-Jar-URL, MIDlet-Jar-Size, MIDlet-Description?, MIDlet-Icon?, MIDlet-Info-URL?, MIDlet-Data-Size?, MIDlet-*, *";
	private static final String DEFAULT_MANIFEST_FILTER_PATTERN = "Manifest-Version, MIDlet-Name, MIDlet-Version, MIDlet-Vendor, MIDlet-Description?, MIDlet-Icon?, MIDlet-Info-URL?, MIDlet-Data-Size?, MIDlet-*, *";
	
	private static final String DEFAULT_ENCODING = "UTF8";
	
	private final AttributesFilter defaultJadFilter;
	private final AttributesFilter defaultManifestFilter;

	
	private DebugSetting debugSetting;
	private MidletSetting midletSetting; 
	private ArrayList obfuscatorSettings;
	private boolean doObfuscate;
	private File workDir;
	private File apiDir;
	private File destDir;
	private File resDir;
	private String symbols;
	private String imageLoadStrategy;
	private FullScreenSetting fullScreenSetting;
	private File devices;
	private File vendors;
	private File groups;
	private File apis;
	private File extensions;
	private Variables variables;
	private boolean usePolishGui;
	private File midp1Path;
	private File midp2Path;
	private File midp2Cldc11Path;
	private File preverify;
	private final Project project;
	private boolean includeAntProperties;
	private ResourceUtil resourceUtil;
	private final ArrayList sourceSettings;
	private File polishDir;
	private JadAttributes jadAttributes;
	private boolean defaultMidpPathUsed = true;
	private ArrayList preprocessors;
	private ArrayList jadAttributesFilters;
	private ArrayList manifestAttributesFilters;
	private LibrariesSetting binaryLibraries;
	private String polishHomePath;
	private String projectBasePath;
	private ArrayList javaTasks;
	private String javacTarget;
	private boolean compilerMode;
	private boolean compilerModePreverify;
	private File compilerDestDir;
	private ResourceSetting resourceSetting;
	private boolean alwaysUsePolishGui;
	private PackageSetting packageSetting;
	private ArrayList compilers;
	private String encoding = DEFAULT_ENCODING;
	private boolean doPreverify = true;
	private ArrayList postCompilers;
	
	/**
	 * Creates a new build setting.
	 * 
	 * @param project The corresponding ant-project.
	 */
	public BuildSetting( Project project ) {
		this.polishHomePath = project.getProperty( "polish.home" );
		if (this.polishHomePath != null) {
			File polishHomeDir = new File( this.polishHomePath );
			if (!polishHomeDir.isAbsolute()) {
				polishHomeDir = new File( project.getBaseDir(), this.polishHomePath );
				this.polishHomePath = polishHomeDir.getAbsolutePath();
			}
			this.polishHomePath += File.separatorChar;
		} 
		this.projectBasePath = project.getBaseDir().getAbsolutePath() + File.separator;
		this.project = project;
		this.workDir = new File( this.projectBasePath + "build");
		this.destDir = new File( this.projectBasePath + "dist");
		this.apiDir = getFile("import");
		this.resDir = getFile ("resources");
		this.sourceSettings = new ArrayList();
		this.midp1Path = getFile( "import/midp1.jar" );
		this.midp2Path = getFile( "import/midp2.jar" );
		this.midp2Cldc11Path = getFile( "import/midp2-cldc11.jar" );
		this.apis = getFile("apis.xml");
		this.extensions = getFile("extensions.xml");
		this.vendors = getFile("vendors.xml");
		this.groups = getFile("groups.xml");
		this.devices = getFile("devices.xml");
		this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		this.resourceUtil = new ResourceUtil( this.getClass().getClassLoader() );
		
		this.defaultJadFilter = new AttributesFilter( DEFAULT_JAD_FILTER_PATTERN );
		this.defaultManifestFilter = new AttributesFilter( DEFAULT_MANIFEST_FILTER_PATTERN );
	}
	
	public void addConfiguredObfuscator( ObfuscatorSetting setting ) {
		if (this.obfuscatorSettings == null) {
			this.obfuscatorSettings = new ArrayList();
		}
		if (setting.isActive(this.project)) {
			setting.checkSettings( this );
			this.obfuscatorSettings.add( setting );
			if (setting.isEnabled()) {
				this.doObfuscate = true;
			}
		}
	}
	
	public void addConfiguredMidlets( MidletSetting setting ) {
		if (this.midletSetting != null) {
			throw new BuildException("Please use either <midlets> or <midlet> to define your midlets!");
		}
		this.midletSetting = setting;
	}
	
	public void addConfiguredMidlet( Midlet midlet ) {
		if (this.midletSetting == null ) {
			this.midletSetting = new MidletSetting();
		}
		this.midletSetting.addConfiguredMidlet( midlet );
	}
	
	public void addConfiguredFullscreen( FullScreenSetting setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute \"fullscreen\" or the nested element <fullscreen>, but not both!");
		}
		this.fullScreenSetting = setting;
	}
	
	public void addConfiguredDebug( DebugSetting setting ) {
		if (setting.isActive(this.project)) {
			this.debugSetting = setting;
		}
	}
	
	public void addConfiguredVariables( Variables vars ) {
		this.includeAntProperties = vars.includeAntProperties();
		this.variables = vars; //vars.getVariables();
	}
	
	public void addConfiguredManifestFilter( AttributesFilter filter ) {
		if (this.manifestAttributesFilters == null) {
			this.manifestAttributesFilters = new ArrayList();
		}
		this.manifestAttributesFilters.add( filter );
	}
		
	public void addConfiguredJad( JadAttributes attributes ) {
		if (this.jadAttributesFilters == null) {
			this.jadAttributesFilters = new ArrayList();
		}
		this.jadAttributes = attributes;
		this.jadAttributesFilters =  attributes.getFilters();
	}
	
	public void addConfiguredPackager( PackageSetting setting ) {
		this.packageSetting = setting;
	}
			
	public void addConfiguredPreprocessor( PreprocessorSetting preprocessor ) {
		if (preprocessor.getClassName() == null) {
			throw new BuildException("Invalid <preprocessor> element: please define the attribute \"class\" for each preprocessor.");
		}
		if (this.preprocessors == null) {
			this.preprocessors = new ArrayList();
		}
		this.preprocessors.add( preprocessor );
	}
	
	public void addConfiguredSources( SourcesSetting setting ) {
		if (setting.isActive( this.project ) ) {
			SourceSetting[] sources = setting.getSources();
			for (int i = 0; i < sources.length; i++) {
				SourceSetting source = sources[i];
				this.sourceSettings.add( source );
				/*
				if (source.isActive(this.project)) {
					this.sourceDirs.add( source.getDir() );
				}
				*/
			}
		}
	}
	
	public void addConfiguredCompiler(CompilerTask task) {
		if (this.compilers == null) {
			this.compilers = new ArrayList();
		}
		this.compilers.add( task );
	}
	
	public void addConfiguredPostCompiler(PostCompilerSetting setting) {
		if (this.postCompilers == null) {
			this.postCompilers = new ArrayList();
		}
		this.postCompilers.add( setting );
	}
	
	
	public PreprocessorSetting[] getPreprocessors() {
		if (this.preprocessors == null) { 
			return new PreprocessorSetting[0];
		} else {
			return (PreprocessorSetting[]) this.preprocessors.toArray( new PreprocessorSetting[ this.preprocessors.size()]);
		}
	}
	
	public JavaExtension createJava() {
		if (this.javaTasks == null) {
			this.javaTasks = new ArrayList();
		}
		JavaExtension java = new JavaExtension( this.project );
		this.javaTasks.add( java );
		return java;
	}
	
	public ResourceSetting createResources() {
		ResourceSetting setting = new ResourceSetting( this.project );
		this.resourceSetting = setting;
		return setting;
	}
	
	public Variables getVariables() {
		if (this.variables == null) {
			this.variables = new Variables();
		}
		return this.variables;
	}

	
	public JadAttributes getJadAttributes() {
		return this.jadAttributes;
	}
	
	/**
	 * Retrieves the setting for resource handling.
	 * 
	 * @return the setting for resource handling.
	 */
	public ResourceSetting getResourceSetting() {
		if (this.resourceSetting == null) {
			this.resourceSetting = new ResourceSetting( this.project );
			this.resourceSetting.setDir( getResDir() );
		}
		return this.resourceSetting;
	}
	
	/**
	 * @return Returns the includeAntProperties.
	 */
	public boolean includeAntProperties() {
		return this.includeAntProperties;
	}
	
	public void setSymbols( String symbols ) {
		this.symbols = symbols;
	}
	
	public void setUsePolishGui( String usePolishGuiStr ) {
		if ("always".equals(usePolishGuiStr)) {
			this.usePolishGui = true;
			this.alwaysUsePolishGui = true;
		} else if ( "true".equalsIgnoreCase(usePolishGuiStr) || "yes".equalsIgnoreCase(usePolishGuiStr)) {
			this.usePolishGui = true;
			this.alwaysUsePolishGui = false;
		} else {
			this.usePolishGui = false;
			this.alwaysUsePolishGui = false;
		}
	}
		
	/**
	 * Determines whether this project should use the polish GUI at all.
	 * The GUI is only used when the current device allows the use of the GUI.
	 * The GUI makes no sense for devices with black and white screens,
	 * for example.
	 * 
	 * @return true when this projects wants to use the polish GUI
	 */
	public boolean usePolishGui() {
		return this.usePolishGui;
	}
	
	/**
	 * Determines whether the J2ME Polish GUI should beused even for devices which do not "usually" support it.
	 *  
	 * @return true when the J2ME Polish GUI should be used for all devices
	 */
	public boolean alwaysUsePolishGui() {
		return this.alwaysUsePolishGui;
	}
	
	public void setImageLoadStrategy( String strategy ) {
		if ("background".equalsIgnoreCase(strategy) ) {
			this.imageLoadStrategy = IMG_LOAD_BACKGROUND;
		} else if ("foreground".equalsIgnoreCase(strategy)) {
			this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		} else {
			throw new BuildException("The build-attribute [imageLoadStrategy] needs to be either [background] or [foreground]. "
					+ "The strategy [" + strategy + "] is not supported.");
		}
	}
	
	/**
	 * Retrieves the strategy by which images should be loaded.
	 * 
	 * @return either IMG_LOAD_BACKGROUND or IMG_LOAD_FOREGROUND
	 * @see #IMG_LOAD_BACKGROUND
	 * @see #IMG_LOAD_FOREGROUND
	 */
	public String getImageLoadStrategy() {
		return this.imageLoadStrategy;
	}
	
	public void setFullscreen( String setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute [fullscreen] or the nested element [fullscreen], but not both!");
		}
		this.fullScreenSetting = new FullScreenSetting();
		if ("menu".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
			this.fullScreenSetting.setMenu( true );
		} else if ("yes".equalsIgnoreCase(setting) || "true".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
		} else if ("no".equalsIgnoreCase(setting) || "false".equalsIgnoreCase(setting)) {
			// keep the default setting
		} else {
			throw new BuildException("The build-attribute [fullscreen] needs to be either [yes], [no] or [menu]. "
					+ "The setting [" + setting + "] is not supported.");
		}
	}


	/**
	 * Retrieves the full screen setting.
	 * 
	 * @return the full screen setting
	 */
	public FullScreenSetting getFullScreenSetting() {
		return this.fullScreenSetting;
	}
	
	public DebugSetting getDebugSetting() {
		return this.debugSetting;
	}
	
	public Midlet[] getMidlets() {
		if (this.midletSetting == null) {
			return null;
		}
		return this.midletSetting.getMidlets( this.project );
	}

	/**
	 * Determines whether debugging is enabled.
	 * 
	 * @return true when debugging is enabled for this project.
	 */
	public boolean isDebugEnabled() {
		if (this.debugSetting == null) {
			return false;
		} else {
			return this.debugSetting.isEnabled();
		}
	}
	
	/**
	 * Retrieves the working directory.
	 * The default working directory is "./build".
	 * If the working directory does not exist, it will be created now.
	 * 
	 * @return Returns the working directory.
	 */
	public File getWorkDir() {
		if (!this.workDir.exists()) {
			this.workDir.mkdirs();
		}
		return this.workDir;
	}
	
	/**
	 * Sets the working directory. Defaults to "./build".
	 * 
	 * @param workDir The working directory to set.
	 */
	public void setWorkDir(File workDir) {
		//File newWorkDir = new File( this.project.getBaseDir().getAbsolutePath() + File.separator + workPath );
		this.workDir = workDir;
	}
	
	/**
	 * Retrieves the directory to which the ready-to-distribute jars should be copied to.
	 * Defaults to "./dist".
	 * If the distribution directory does not exist, it will be created now.
	 * 
	 * @return The destination directory.
	 */
	public File getDestDir() {
		if (! this.destDir.exists()) {
			this.destDir.mkdirs();
		}
		return this.destDir;
	}
	
	
	/**
	 * Sets the destination directory. Defaults to "./dist".
	 * 
	 * @param destDir The destination directory.
	 */
	public void setDestDir( File destDir ) {
		//File newDestDir = getFile( destPath );
		this.destDir = destDir;
	}
	
	/**
	 * Retrieves the directory which contains the resources. Defaults to "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * If the resource directory does not exist, it will be created now.
	 * 
	 * @return The directory which contains the resources
	 */
	private File getResDir() {
		if (!this.resDir.exists()) {
			this.resDir.mkdirs();
		}
		return this.resDir;
	}
	
	/**
	 * Sets the directory containing the resources of this project.
	 * Default resource directory is "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * 
	 * @param resDir The directory containing the resources.
	 */
	public void setResDir( File resDir ) {
		if (this.resourceSetting != null) {
			throw new BuildException("Please use either the \"resDir\"-attribute of the <build>-element or the <resources>-element, but not both.");
		}
		if (!resDir.exists()) {
			throw new BuildException("The resource directory [" + resDir.getAbsolutePath() + 
					"] does not exist. Please correct the attribute \"resDir\" " +
					"of the <build> element.");
		}
		this.resDir = resDir;
		this.resourceSetting = new ResourceSetting( this.project );
		this.resourceSetting.setDir( resDir );
	}
	
	/**
	 * Sets the directory containing the J2ME source code of polish.
	 * 
	 * @param polishPath the directory containing the J2ME source code of polish.
	 */
	public void setPolishDir( String polishPath ) {
		File newPolishDir = getFile( polishPath );
		if (!newPolishDir.exists()) {
			throw new BuildException("The J2ME Polish source directory [" + newPolishDir.getAbsolutePath() + 
					"] does not exist. " +
					"Please correct the [polishDir] attribute of the <build> element.");
		}
		String actualSourcePath = newPolishDir.getAbsolutePath() + File.separator
				+ "src";
		File actualSourceDir = new File( actualSourcePath );
		if ( actualSourceDir.exists()) {
			newPolishDir = actualSourceDir;
		}
		this.polishDir = newPolishDir;
	}
	
	/**
	 * Retrieves the directory containing the J2ME source code of polish.
	 * 
	 * @return the directory containing the J2ME source code of polish
	 */
	public File getPolishDir() {
		return this.polishDir;
	}

	public void setSrcdir( String srcDir ) {
		setSourceDir( srcDir );
	}
	public void setSrcDir( String srcDir ) {
		setSourceDir( srcDir );
	}
	/**
	 * Sets the source directory in which the source files for the application reside.
	 * 
	 * @param srcDir the source directory
	 */
	public void setSourceDir( String srcDir ) {
		String[] paths = StringUtil.split( srcDir, ':');
		if (paths.length == 1 || containsSingleCharPath( paths ) ) {
			paths = StringUtil.split( srcDir, ';' );
		}
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if ( File.separatorChar == '\\' && path.length() == 1 && i < paths.length -1 ) {
				// this is an absolute path on a windows machine, e.g. C:\project\source
				i++;
				path += ":" + paths[i];
			}
			File dir = getFile( path );
			if (!dir.exists()) {
				throw new BuildException("The source directory [" + path + "] does not exist. " +
						"Please correct the attribute [sourceDir] of the <build> element.");
			}
			this.sourceSettings.add( new SourceSetting( dir ) );
		}
	}
	
	/**
	 * Determines whether one of the given paths  is only one char long
	 * 
	 * @param paths the paths that are checked
	 * @return true when at one of the given paths is only one char long
	 */
	private boolean containsSingleCharPath(String[] paths) {
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].length() == 1 ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all external source directories.
	 * 
	 * @return an arrray with at least one source directory.
	 */
	public SourceSetting[] getSourceSettings() {
		if (this.sourceSettings.size() == 0) {
			// add default directory: either source/src, scr or source:
			File src = getFile("source/src", false );
			if (src.exists()) {
				this.sourceSettings.add( new SourceSetting( src  ) );
			} else {
				src = getFile("src", false );
				if (src.exists()) {
					this.sourceSettings.add( new SourceSetting( src  ) );
				} else {
					src = getFile("source", false);
					if (src.exists()) {
						this.sourceSettings.add( new SourceSetting( src ) );
					} else {
						throw new BuildException("Did not find any of the default " +
								"source directories [source/src], [src] or [source]. " +
								"Please specify the [sourceDir]-attribute of the " +
								"<build> element. " +
								"Base-directory is [" + this.project.getBaseDir().getAbsolutePath() + "].");
					}
				}
			}
		}
		SourceSetting[] settings = (SourceSetting[]) this.sourceSettings.toArray( new SourceSetting[ this.sourceSettings.size()]);
		return settings;
	}
	
	/**
	 * Retrieves the apis.xml file as input stream.
	 * 
	 * @return Returns the apis.xml file as input stream.
	 */
	public InputStream openApis() {
		try {
			return openResource( this.apis, "apis.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open apis.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * @return
	 */
	public InputStream openExtensions() {
		try {
			return openResource( this.extensions, "extensions.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open extensions.xml: " + e.getMessage(), e );
		}
	}

	
	/**
	 * Sets the path to the apis.xml file.
	 * 
	 * @param apisPath the path to the apis.xml file
	 */
	public void setApis( String apisPath ) {
		File newApis = getFile( apisPath );
		if (!newApis.exists()) {
			throw new BuildException("The [apis]-attribute of the <build> element points to a non existing file: [" + newApis.getAbsolutePath() + "].");
		}
		this.apis = newApis;		
	}

	/**
	 * @return Returns the the directory which contains device specific libraries.
	 */
	public File getApiDir() {
		if (!this.apiDir.exists()) {
			throw new BuildException("Did not find the api directory in the default path [" + this.apiDir.getAbsolutePath() + "]. Please specify either the [apiDir]-attribute of the <build> element or copy all device-specific jars to this path.");
		}
		return this.apiDir;
	}
	
	/**
	 * Sets the directory which contains device specific libraries
	 * 
	 * @param apiPath The directory which contains device specific libraries. Defaults to "./import"
	 */
	public void setApiDir(String apiPath) {
		File newApiDir = getFile( apiPath );
		if (!newApiDir.exists()) {
			throw new BuildException("The [apiDir]-attribute of the <build> element points to a non existing directory: [" + newApiDir.getAbsolutePath() + "].");
		}
		this.apiDir = newApiDir;
		if (this.defaultMidpPathUsed) {
			this.midp1Path = new File( newApiDir.getAbsolutePath() + File.separator + "midp1.jar" );
			this.midp2Path = new File( newApiDir.getAbsolutePath() + File.separator + "midp2.jar" );
		}
	}
	
	/**
	 * @return Returns the xml file containing the devices-data.
	 */
	public InputStream openDevices() {
		try {
			return openResource( this.devices, "devices.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the device.xml file.
	 * 
	 * @param devicesPath The path to the devices.xml
	 */
	public void setDevices(String devicesPath) {
		File newDevices = getFile( devicesPath );
		if (!newDevices.exists()) {
			throw new BuildException("The [devices]-attribute of the <build> element points to a non existing file: [" + newDevices.getAbsolutePath() + "].");
		}
		this.devices = newDevices;
	}
	
	/**
	 * @return Returns the groups.
	 */
	public InputStream openGroups() {
		try {
			return openResource( this.groups, "groups.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open groups.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the groups.xml file
	 * 
	 * @param groupsPath The path to the groups.xml file
	 */
	public void setGroups(String groupsPath) {
		File newGroups = getFile( groupsPath );
		if (!newGroups.exists()) {
			throw new BuildException("The [groups]-attribute of the <build> element points to a non existing file: [" + newGroups.getAbsolutePath() + "].");
		}
		this.groups = newGroups;
	}

	/**
	 * Retrieves the vendors.xml file as input stream.
	 * 
	 * @return Returns the vendors.xml file as input stream.
	 */
	public InputStream openVendors() {
		try {
			return openResource( this.vendors, "vendors.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open vendors.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the vendors.xml file
	 * 
	 * @param vendorsPath The path to the vendors.xml file
	 */
	public void setVendors(String vendorsPath) {
		File newVendors = getFile( vendorsPath );
		if (!newVendors.exists()) {
			throw new BuildException("The [vendors]-attribute of the <build> element points to a non existing file: [" + newVendors.getAbsolutePath() + "].");
		}
		this.vendors = newVendors;
	}

	/**
	 * Gets the path to the MIDP/1.0-api-file
	 * 
	 * @return The path to the api-file of the MIDP/1.0 environment 
	 */
	public File getMidp1Path() {
		if (!this.midp1Path.exists()) {
			throw new BuildException("The default path to the MIDP/1.0-API [" + this.midp1Path.getAbsolutePath() + "] points to a non-existing file. Please specify it with the [midp1Path] attribute of the <build> element.");
		}
		return this.midp1Path;
	}
	
	/**
	 * Sets the path to the api-file of the MIDP/1.0 environment
	 *  
	 * @param midp1PathStr The path to the MIDP/1.0-api-file
	 */
	public void setMidp1Path( String midp1PathStr ) {
		File newMidp1Path = getFile( midp1PathStr );
		if (!newMidp1Path.exists()) {
			throw new BuildException("Invalid path to the MIDP/1.0-API: [" + newMidp1Path.getAbsolutePath() + "] (File not found).");
		}
		this.midp1Path = newMidp1Path;
		this.defaultMidpPathUsed = false;
	}

	/**
	 * Gets the path to the MIDP/2.0-jar
	 * 
	 * @return The path to the api-file of the MIDP/2.0 environment 
	 */
	public File getMidp2Path() {
		if (!this.midp2Path.exists()) {
			throw new BuildException("The default path to the MIDP/2.0-API [" + this.midp2Path.getAbsolutePath() + "] points to a non-existing file. Please specify it with the [midp2Path] attribute of the <build> element.");
		}
		return this.midp2Path;
	}
	
	/**
	 * Gets the path to the MIDP/2.0/CLDC/1.1-jar
	 * 
	 * @return the path to the API-file for MIDP/2.0 devices which support the CLDC/1.1 configuration
	 */
	public File getMidp2Cldc11Path() {
		if (!this.midp2Cldc11Path.exists()) {
			throw new BuildException("The default path to the MIDP/2.0 / CLDC/1.1-API [" + this.midp2Cldc11Path.getAbsolutePath() + "] points to a non-existing file. Please specify it with the [midp2Cldc11Path] attribute of the <build> element.");
		}
		return this.midp2Cldc11Path;
	}
	
	/**
	 * Sets the path to the api-file of the MIDP/2.0 environment.
	 * When the midp1Path is not defined, it will use the same
	 * api-path as the given MIDP/2.0 environment.
	 *  
	 * @param midp2PathStr The path to the MIDP/2.0-api-file
	 */
	public void setMidp2Path( String midp2PathStr ) {
		File newMidp2Path = getFile( midp2PathStr );
		if (!newMidp2Path.exists()) {
			throw new BuildException("Invalid path to the MIDP/2.0-API: [" + newMidp2Path.getAbsolutePath() + "] (File not found).");
		}
		this.midp2Path = newMidp2Path;
		if (this.midp1Path == null) {
			this.midp1Path = newMidp2Path;
		}
		this.defaultMidpPathUsed = false;
	}

	/**
	 * Sets the path to the api-file of the MIDP/2.0 / CLDC/1.1 environment.
	 *  
	 * @param midp2Cldc11PathStr The path to the MIDP/2.0-api-file
	 */
	public void setMidp2Cldc11Path( String midp2Cldc11PathStr ) {
		File newMidp2Path = getFile( midp2Cldc11PathStr );
		if (!newMidp2Path.exists()) {
			throw new BuildException("Invalid path to the MIDP/2.0/CLDC/1.1-API: [" + newMidp2Path.getAbsolutePath() + "] (File not found).");
		}
		this.midp2Cldc11Path = newMidp2Path;
		this.defaultMidpPathUsed = false;
	}
	
	
	/**
	 * @return The user-defined symbols
	 */
	public String getSymbols() {
		return this.symbols;
	}
	
	/**
	 * Sets the path to the preverify executable.
	 * 
	 * @param preverifyPath the path to the preverify executable.
	 */
	public void setPreverify( String preverifyPath ) {
		if ( "true".equalsIgnoreCase(preverifyPath) || "yes".equalsIgnoreCase( preverifyPath)) {
			this.doPreverify = true;
			return;
		} else if ( "false".equalsIgnoreCase(preverifyPath) || "no".equalsIgnoreCase( preverifyPath)) {
			this.doPreverify = false;
			return;
		}
		File newPreverify = getFile( preverifyPath );
		if (!newPreverify.exists()) {
			throw new BuildException("The path to the preverify-tool is invalid: [" + newPreverify.getAbsolutePath() + "] points to a non-existing file. Please correct the [preverify] attribute of the <build> element.");
		}
		this.preverify = newPreverify;
	}
	
	public File getPreverify() {
		return this.preverify;
	}
	
	public boolean doPreverify() {
		return this.doPreverify;
	}

	/**
	 * Retrieves all the defined MIDlet-class-names.
	 * 
	 * @return The names of all midlet-classes in a String array. 
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletClassNames() {
		boolean useDefaultPackage = useDefaultPackage();
		Midlet[] midlets = getMidlets();
		String[] midletClassNames = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			String className = midlets[i].getClassName();
			if (useDefaultPackage) {
				int dotIndex = className.lastIndexOf('.');
				if (dotIndex != -1) {
					className = className.substring( dotIndex + 1 );
				}
			}
			midletClassNames[i] = className;
		}
		return midletClassNames;
	}
	
	/**
	 * Retrieves the infos for all midlets.
	 * The infos contain the name, the icon and the class of the midlet
	 * and are used for the JAD and the manifest.
	 * 
	 * @param defaultIcon the url of the default icon.
	 * @return The infos of all midlets in a String array.
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletInfos( String defaultIcon ) {
		boolean useDefaultPackage = useDefaultPackage();
		Midlet[] midlets = getMidlets();
		String[] midletInfos = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletInfos[i] = midlets[i].getMidletInfo( defaultIcon, useDefaultPackage );
		}
		return midletInfos;
	}

	/**
	 * @return The obfuscators which should be used
	 */
	public ObfuscatorSetting[] getObfuscatorSettings() {
		if (this.obfuscatorSettings == null) {
			return new ObfuscatorSetting[0];
		} else { 
			return (ObfuscatorSetting[]) this.obfuscatorSettings.toArray( new ObfuscatorSetting[ this.obfuscatorSettings.size() ] );
		}
	}
	
	/**
	 * Sets the name of the obfuscator.
	 * 
	 * @param obfuscator The name of the obfuscator, e.g. "ProGuard" or "RetroGuard"
	 */
	public void setObfuscator( String obfuscator ) {
		ObfuscatorSetting setting = new ObfuscatorSetting();
		setting.setName( obfuscator );
		addConfiguredObfuscator( setting );
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @return True when the jars should be obfuscated.
	 */
	public boolean doObfuscate() {
		return this.doObfuscate;
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @param obfuscate True when the jars should be obfuscated.
	 */
	public void setObfuscate( boolean obfuscate ) {
		if (obfuscate) {
			if (this.obfuscatorSettings == null) {
				this.obfuscatorSettings = new ArrayList();
				ObfuscatorSetting setting = new ObfuscatorSetting();
				setting.setEnable( true );
				addConfiguredObfuscator(setting);
			} else {
				ObfuscatorSetting setting = (ObfuscatorSetting) this.obfuscatorSettings.get(0);
				setting.setEnable(true);
				this.doObfuscate = true;
			}
			
		}
	}

	/**
	 * Retrieves the specified resource as an input stream.
	 * The caller is responsible for closing the returned input stream.
	 * 
	 * @param file the file which has been set. When the file is not null,
	 * 		  it needs to exists as well.
	 * @param name the name of the resource
	 * @return the input stream for the specified resource.
	 * @throws FileNotFoundException when the resource could not be found.
	 */
	private InputStream openResource(File file, String name) 
	throws FileNotFoundException 
	{
		if (file != null && file.exists() ) {
			try {
				return new FileInputStream( file );
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to open [" + file.getAbsolutePath() + "]: " + e.getMessage(), e );
			}
		}
		return this.resourceUtil.open( this.project.getBaseDir().getAbsolutePath(), name );
	}
	
	/**
	 * Resolves the given path and returns a file handle for that path.
	 * 
	 * @param path the relative or absolute path, e.g. "resources2"
	 * @return the file handle for the path
	 */
	protected File getFile( String path ) {
		return getFile( path, true );
	}
	
	/**
	 * Resolves the given path and returns a file handle for that path.
	 * 
	 * @param path the relative or absolute path, e.g. "resources2"
	 * @param tryPolishHomePath true when the file should also be searched in the polishHomePath 
	 * @return the file handle for the path
	 */
	protected File getFile( String path, boolean tryPolishHomePath ) {
		File absolute = new File( path );
		if (absolute.isAbsolute()) {
			return absolute;
		}
		File file = new File( this.projectBasePath + path );
		if (!file.exists() && tryPolishHomePath) {
			file = new File( this.polishHomePath + path );
		}
		if (!file.exists()) {
			file = new File( path );
		}
		return file;
	}

	/**
	 * Determines whether there is a filter registered for JAD attributes
	 * 
	 * @return true when there is a filter defined.
	 */
	public boolean hasUserDefinedJadAttributesFilter() {
		return (this.jadAttributesFilters != null);
	}

	/**
	 * Filters the given JAD attributes.
	 * 
	 * @param attributesMap a hash map containing the available attributes
	 *        with the attribute-names as keys.
	 * @param evaluator the evaluator for getting the correct filter
	 * @return an array of attributes in the correct order,
	 *         not all given attributes are guaranteed to be included.
	 * @throws NullPointerException when there is no JAD-attribute filter.
	 */
	public Attribute[] filterJadAttributes( HashMap attributesMap, BooleanEvaluator evaluator ) {
		if (this.jadAttributesFilters != null) {
			AttributesFilter[] filters = (AttributesFilter[]) this.jadAttributesFilters.toArray( new AttributesFilter[ this.jadAttributesFilters.size() ]);
			for (int i = 0; i < filters.length; i++) {
				AttributesFilter filter = filters[i];
				if (filter.isActive(evaluator, this.project)) {
					return filter.filterAttributes(attributesMap);
				}
			}
		} 
		return this.defaultJadFilter.filterAttributes(attributesMap);
	}
	
	/**
	 * Determines whether there is a filter registered for manifest attributes
	 * 
	 * @return true when there is a filter defined.
	 */
	public boolean hasUserDefinedManifestAttributesFilter() {
		return (this.manifestAttributesFilters != null);
	}

	/**
	 * Filters the given manifest attributes.
	 * 
	 * @param attributesMap a hash map containing the available attributes
	 *        with the attribute-names as keys.
	 * @param evaluator the boolean evaluator for getting the correct filter
	 * @return an array of attributes in the correct order,
	 *         not all given attributes are guaranteed to be included.
	 * @throws NullPointerException when there is no MANIFEST-attribute filter.
	 */
	public Attribute[] filterManifestAttributes( HashMap attributesMap, BooleanEvaluator evaluator ) {
		if (this.manifestAttributesFilters != null) {
			AttributesFilter[] filters = (AttributesFilter[]) this.manifestAttributesFilters.toArray( new AttributesFilter[ this.manifestAttributesFilters.size() ]);
			for (int i = 0; i < filters.length; i++) {
				AttributesFilter filter = filters[i];
				if (filter.isActive(evaluator, this.project)) {
					return filter.filterAttributes(attributesMap);
				}
			}
		} 
		return this.defaultManifestFilter.filterAttributes(attributesMap);
	}
	
	/**
	 * Same as setBinaryLibraries
	 * 
	 * @param librariesStr the paths to either a jar-file, a zip-file or a directory 
	 * 			containing class files, which are needed for the project.
	 * 			Several libraries can be seperated with either a colon or a semicolon. 
	 */
	public void setBinaryLibrary( String librariesStr ) {
		setBinaryLibraries(librariesStr);
	}
	
	/**
	 * Sets additional third party libraries which are only available in binary form.
	 * 
	 * @param librariesStr the paths to either a jar-file, a zip-file or a directory 
	 * 			containing class files, which are needed for the project.
	 * 			Several libraries can be seperated with either a colon or a semicolon. 
	 */
	public void setBinaryLibraries( String librariesStr ) {
		String[] libraryPaths = StringUtil.split(librariesStr, ':');
		if (libraryPaths.length == 1) {
			libraryPaths = StringUtil.split( librariesStr, ';' );
		}
		if (this.binaryLibraries == null ) {
			this.binaryLibraries = new LibrariesSetting();
		}
		for (int i = 0; i < libraryPaths.length; i++) {
			String libPath = libraryPaths[i];
			File file = getFile(libPath);
			if (!file.exists()) {
				// check if the file resides in the api folder (usually "import"):
				file = new File( this.apiDir.getAbsolutePath() 
						+ File.separatorChar + libPath );
				if (!file.exists()) {
					throw new BuildException("The binary library [" + libPath + "] could not be found - please check your \"binaryLibraries\"-attribute of the <build> element: File not found: " + file.getAbsolutePath() );
				}
			}
			LibrarySetting setting = new LibrarySetting();
			if (file.isDirectory()) {
				setting.setDir( file );
			} else {
				setting.setFile( file );
			}
			this.binaryLibraries.addConfiguredLibrary( setting  );
		}
	}
	
	/**
	 * Sets the complete library-settings
	 *  
	 * @param setting the libraries
	 */
	public void addConfiguredLibraries( LibrariesSetting setting ) {
		if ( this.binaryLibraries == null ) {
			this.binaryLibraries = setting;
		} else {
			this.binaryLibraries.add( setting );
		}
	}
	
	/**
	 * Retrieves third party libraries which are only available in binary form.
	 *  
	 * @return The libraries setting
	 */
	public LibrariesSetting getBinaryLibraries() {
		return this.binaryLibraries;
	}
	
	/**
	 * Retrieves the extensions with a java-element.
	 * 
	 * @return an array of JavaExtension
	 */
	public JavaExtension[] getJavaExtensions() {
		if (this.javaTasks == null) {
			return new JavaExtension[0];
		} else {
			return (JavaExtension[]) this.javaTasks.toArray( new JavaExtension[ this.javaTasks.size() ]);
		}
	}
	
	/**
	 * Opens the [standard-css-attributes.xml] file.
	 * 
	 * @return an input stream to that file
	 * @throws BuildException when the file could not be found
	 */
	public InputStream openStandardCssAttributes(){
		try {
			return openResource( getFile("standard-css-attributes.xml"), "standard-css-attributes.xml");
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to load [standard-css-attributes.xml].");
		}
	}

	/**
	 * Opens the [custom-css-attributes.xml] file.
	 * 
	 * @return an input stream to that file or null when the file could not be found.
	 */
	public InputStream openCustomCssAttributes(){
		try {
			return openResource( getFile("custom-css-attributes.xml"), "custom-css-attributes.xml");
		} catch (FileNotFoundException e) {
			System.out.println("Warning: Unable to load [custom-css-attributes.xml].");
			return null;
		}
	}

	/**
	 * Retrieves the target to which the java-sources should be compiled.
	 * When a specific target has been set, that one will be used.
	 * Otherwise a target will dynamically be created:
	 * <ul>
	 * <li>when a WTK-version smaller than 2.0 is used, the "1.1" target will be used;</li>
	 * <li>when the OS is Mac OS X, the "1.1" target will be used;</li>
	 * <li>in all other cases the "1.2" target is used.</li>
	 * </ul>
	 * 
	 * @return Returns the javac-target.
	 */
	public String getJavacTarget() {
		if (this.javacTarget != null) {
			return this.javacTarget;
		} else {
			// check for OS X:
			String osName = "OS-Name: " + System.getProperty("os.name");
			if (osName.indexOf("Mac OS X") != -1) {
				return TARGET_1_1;
			}
			// check for WTK version < 2.0:
			String wtkHomePath = this.project.getProperty("wtk.home");
			if (wtkHomePath != null) {
				if ((wtkHomePath.indexOf('1') != -1) && 
				(wtkHomePath.indexOf("1.") != -1 
						|| wtkHomePath.indexOf("WTK1") != -1
						|| wtkHomePath.indexOf("1_") != -1)) 
				{
					return TARGET_1_1;
				}
			}
						
			// return default:
			return TARGET_1_2;
		}
	}
	
	/**
	 * Sets the target to which the java-sources should be compiled.
	 * Should be either "1.1" or "1.2".
	 * 
	 * @param javacTarget The javac-target to set.
	 */
	public void setJavacTarget(String javacTarget) {
		if (javacTarget.equals( TARGET_1_1)) {
			this.javacTarget = TARGET_1_1;
		} else if (javacTarget.equals( TARGET_1_2)) {
			this.javacTarget = TARGET_1_2;
		} else {
			this.javacTarget = javacTarget;
		}
	}
	
	/**
	 * Enables or disables the compiler-mode of J2ME Polish.
	 * In the compiler mode only one device will be processed
	 * and the execution will be aborted before the packaging is done.
	 * 
	 * @param enable true when the compiler-mode should be enabled.
	 *        The mode is disabled by default.
	 */
	public void setCompilerMode( boolean enable ) {
		this.compilerMode = enable;
	}
	
	/**
	 * Determines whether the compiler-mode of J2ME Polish is activated.
	 * In the compiler mode only one device will be processed
	 * and the execution will be aborted before the packaging is done.
	 * The mode is disabled by default.
	 * 
	 * @return true when the compiler-mode should be enabled.
	 */
	public boolean isInCompilerMode() {
		return this.compilerMode;
	}
	
	/**
	 * Sets the destination directory for compiled classes.
	 * This setting is only used when the compiler-mode is activated.
	 * 
	 * @param destinationDir the target directory for compiled classes.
	 *        The default directory is "bin/classes". 
	 */
	public void setCompilerDestDir( File destinationDir ) {
		this.compilerDestDir = destinationDir;
	}
	
	/**
	 * Retrieves the target directory for compiled classes.
	 * 
	 * @return the target directory for compiled classes.
	 *        The default directory is "bin/classes". 
	 */
	public File getCompilerDestDir() {
		if (this.compilerDestDir == null) {
			return new File( this.projectBasePath + "bin/classes" );
		} else {
			return this.compilerDestDir;
		}
	}
	
	public void setCompilerModePreverify( boolean enable ) {
		this.compilerModePreverify = enable;
	}
	
	public boolean doPreverifyInCompilerMode() {
		return this.compilerModePreverify;
	}
	
	public PackageSetting getPackageSetting() {
		return this.packageSetting;
	}

	/**
	 * Determines whether all classes should be moved into the default package ("").
	 * 
	 * @return true when all classes should be moved into the default package.
	 */
	public boolean useDefaultPackage() {
		if (this.obfuscatorSettings == null) {
			return false;
		} else {
			ObfuscatorSetting[] settings = getObfuscatorSettings();
			for (int i = 0; i < settings.length; i++) {
				ObfuscatorSetting setting = settings[i];
				if (setting.useDefaultPackage()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the appropriate compiler setting
	 * 
	 * @param evaluator the evaluator for boolean conditions 
	 * @return a suitable compiler task
	 */
	public CompilerTask getCompiler( BooleanEvaluator evaluator ) {
		if (this.compilers != null) {
			CompilerTask[] tasks = (CompilerTask[]) this.compilers.toArray( new CompilerTask[this.compilers.size() ]);
			for (int i = 0; i < tasks.length; i++) {
				CompilerTask task = tasks[i];
				if (task.isActive(evaluator, this.project)) {
					return task.copy();
				}
			}
		}
		return new CompilerTask();
	}
	
	/**
	 * Sets the encoding for the JAD, MANIFEST.
	 * 
	 * @param encoding the encoding, defaults to "UTF8"
	 */
	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}
	
	/**
	 * Sets the encoding for the JAD, MANIFEST.
	 * 
	 * @return the encoding, defaults to "UTF8"
	 */
	public String getEncoding() {
		return this.encoding;
	}
	
	public PostCompilerSetting[] getPostCompilers() {
		if (this.postCompilers == null) {
			return new PostCompilerSetting[0]; 
		} else {
			return (PostCompilerSetting[]) this.postCompilers.toArray( new PostCompilerSetting[this.postCompilers.size()] );
		}
	}

	/**
	 * @return
	 */
	public boolean doPostCompile() {
		return (this.postCompilers != null);
	}
	
}
