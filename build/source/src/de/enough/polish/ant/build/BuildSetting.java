/*
 * Created on 22-Jan-2003 at 14:10:02.
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
package de.enough.polish.ant.build;

import de.enough.polish.*;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <p>Represents the build settings of a polish J2ME project.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BuildSetting {
	
	public final static String IMG_LOAD_BACKGROUND = "images.backgroundLoad";
	public final static String IMG_LOAD_FOREGROUND = "images.directLoad";
	
	private DebugSetting debugSetting;
	private MidletSetting midletSetting; 
	private ObfuscatorSetting obfuscatorSetting;
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
	private Variable[] variables;
	private boolean usePolishGui;
	private File midp1Path;
	private File midp2Path;
	private File preverify;
	private Project project;
	private boolean includeAntProperties;
	private ResourceUtil resourceUtil;
	private ArrayList sourceDirs;
	private File polishDir;
	private JadAttributes jadAttributes;
	private boolean defaultMidpPathUsed = true;
	
	/**
	 * Creates a new build setting.
	 * 
	 * @param project The corresponding ant-project.
	 */
	public BuildSetting( Project project ) {
		this.project = project;
		this.workDir = new File( project.getBaseDir().getAbsolutePath() + File.separator
				+ "build");
		this.destDir = new File( project.getBaseDir().getAbsolutePath() + File.separator
				+ "dist");
		this.apiDir = getFile("import");
		this.resDir = getFile ("resources");
		this.sourceDirs = new ArrayList();
		this.midp1Path = getFile( "import/midp1.jar" );
		this.midp2Path = getFile( "import/midp2.jar" );
		this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		this.resourceUtil = new ResourceUtil( this.getClass().getClassLoader() );
	}
	
	public void addConfiguredObfuscator( ObfuscatorSetting setting ) {
		if (setting.isActive(this.project)) {
			this.obfuscatorSetting = setting;
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
			throw new BuildException("Please use either the attribute [fullscreen] or the nested element [fullscreen], but not both!");
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
		this.variables = vars.getVariables();
	}
	
	public Variable[] getVariables() {
		return this.variables;
	}
	
	public void addConfiguredJad( JadAttributes attributes ) {
		this.jadAttributes = attributes;
	}
	
	public Attribute[] getJadAttributes() {
		if (this.jadAttributes == null) {
			return new Attribute[0];
		} else {
			return this.jadAttributes.getAttributes();
		}
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
	
	public void setUsePolishGui( boolean usePolishGui ) {
		this.usePolishGui = usePolishGui; 
	}
		
	/**
	 * Determines whether this project should use the polish GUI at all.
	 * The GUI is only used when the current device allows the use of the GUI.
	 * The GUI makes no sense for devices with black and white screens,
	 * for example.
	 * 
	 * @return true when this projects wants to use the polish GUI
	 */
	public boolean usesPolishGui() {
		return this.usePolishGui;
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
		return this.midletSetting.getMidlets();
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
	public void setWorkDir(String workPath) {
		File newWorkDir = new File( this.project.getBaseDir().getAbsolutePath() + File.separator + workPath );
		this.workDir = newWorkDir;
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
	 * @param destPath The path of the destination directory.
	 */
	public void setDestDir( String destPath ) {
		File newDestDir = getFile( destPath );
		this.destDir = newDestDir;
	}
	
	/**
	 * Retrieves the directory which contains the resources. Defaults to "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * If the resource directory does not exist, it will be created now.
	 * 
	 * @return The directory which contains the resources
	 */
	public File getResDir() {
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
	 * @param resPath The directory containing the resources.
	 */
	public void setResDir( String resPath ) {
		File newResDir = getFile( resPath );
		if (!newResDir.exists()) {
			throw new BuildException("The resource directory [" + newResDir.getAbsolutePath() + 
					"] does not exist. Please correct the attribute [resDir] " +
					"of the <build> element.");
		}
		this.resDir = newResDir;
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
		String[] paths = TextUtil.split( srcDir, ':');
		if (paths.length == 1) {
			paths = TextUtil.split( srcDir, ';' );
		}
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			File dir = getFile( path );
			if (!dir.exists()) {
				throw new BuildException("The source directory [" + path + "] does not exist. " +
						"Please correct the attribute [sourceDir] of the <build> element.");
			}
			this.sourceDirs.add( dir );
		}
	}
	
	/**
	 * Retrieves all external source directories.
	 * 
	 * @return an arrray with at least one source directory.
	 */
	public File[] getSourceDirs() {
		if (this.sourceDirs.size() == 0) {
			// add default directory: either source/src, scr or source:
			File src = getFile("source/src");
			if (src.exists()) {
				this.sourceDirs.add( src );
			} else {
				src = getFile("src");
				if (src.exists()) {
					this.sourceDirs.add( src );
				} else {
					src = getFile("source");
					if (src.exists()) {
						this.sourceDirs.add( src );
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
		File[] dirs = (File[]) this.sourceDirs.toArray( new File[ this.sourceDirs.size()]);
		return dirs;
	}
	
	/**
	 * Retrieves the apis.xml file as input stream.
	 * 
	 * @return Returns the apis.xml file as input stream.
	 */
	public InputStream openApis() {
		try {
			return getResource( this.apis, "apis.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open apis.xml: " + e.getMessage(), e );
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
	public InputStream getDevices() {
		try {
			return getResource( this.devices, "devices.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the device.xml file.
	 * 
	 * @param devices The path to the devices.xml
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
	public InputStream getGroups() {
		try {
			return getResource( this.groups, "groups.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
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
	public InputStream getVendors() {
		try {
			return getResource( this.vendors, "vendors.xml" );
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
	 * @param midp1Path The path to the MIDP/1.0-api-file
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
	 * Sets the path to the api-file of the MIDP/2.0 environment.
	 * When the midp1Path is not defined, it will use the same
	 * api-path as the given MIDP/2.0 environment.
	 *  
	 * @param midp2Path The path to the MIDP/2.0-api-file
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
		File newPreverify = getFile( preverifyPath );
		if (!newPreverify.exists()) {
			throw new BuildException("The path to the preverify-tool is invalid: [" + newPreverify.getAbsolutePath() + "] points to a non-existing file. Please correct the [preverify] attribute of the <build> element.");
		}
		this.preverify = newPreverify;
	}
	
	public File getPreverify() {
		return this.preverify;
	}

	/**
	 * Retrieves all the defined MIDlet-class-names.
	 * 
	 * @return The names of all midlet-classes in a String array. 
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletClassNames() {
		Midlet[] midlets = this.midletSetting.getMidlets();
		String[] midletClassNames = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletClassNames[i] = midlets[i].getClassName();
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
		Midlet[] midlets = this.midletSetting.getMidlets();
		String[] midletInfos = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletInfos[i] = midlets[i].getMidletInfo( defaultIcon );
		}
		return midletInfos;
	}

	/**
	 * @return The obfuscator which should be used
	 */
	public ObfuscatorSetting getObfuscatorSetting() {
		return this.obfuscatorSetting;
	}
	
	/**
	 * Sets the name of the obfuscator.
	 * 
	 * @param obfuscator The name of the obfuscator, e.g. "ProGuard" or "RetroGuard"
	 */
	public void setObfuscator( String obfuscator ) {
		if (this.obfuscatorSetting == null) {
			this.obfuscatorSetting = new ObfuscatorSetting();
		}
		this.obfuscatorSetting.setName( obfuscator );
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @return True when the jars should be obfuscated.
	 */
	public boolean doObfuscate() {
		if (this.obfuscatorSetting == null) {
			return false;
		}
		return this.obfuscatorSetting.isEnabled();
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @param obfuscate True when the jars should be obfuscated.
	 */
	public void setObfuscate( boolean obfuscate ) {
		if (this.obfuscatorSetting == null) {
			this.obfuscatorSetting = new ObfuscatorSetting();
		}
		this.obfuscatorSetting.setEnable( obfuscate );
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
	private InputStream getResource(File file, String name) 
	throws FileNotFoundException 
	{
		if (file != null ) {
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
	private File getFile( String path ) {
		File file = new File( this.project.getBaseDir().getAbsolutePath() + File.separator + path );
		if (!file.exists()) {
			file = new File( path );
		}
		return file;
	}

	
}
