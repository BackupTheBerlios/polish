/*
 * Created on 10-Sep-2004 at 16:35:12.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.ant.build.ResourceCopierSetting;
import de.enough.polish.ant.build.ResourceSetting;
import de.enough.polish.ant.requirements.SizeMatcher;
import de.enough.polish.preprocess.CssReader;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.StyleSheet;

/**
 * <p>Is responsible for the assembling of resources like images and localization messages.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceManager {
	private final static String[] DEFAULT_EXCLUDES = new String[]{ "polish.css", "*~", "*.bak", "Thumbs.db" };
	private final ResourceSetting resourceSetting;
	private final Project project;
	private final BooleanEvaluator booleanEvaluator;
	private final File resourcesDir;
	private final Map resourceDirsByDevice;
	private final ResourceFilter resourceFilter;
	private final LocalizationSetting localizationSetting;
	private final File[] dynamicScreenSizeDirs;
	private final SizeMatcher[] dynamicScreenSizeMatchers;
	private final File[] dynamicCanvasSizeDirs;
	private final SizeMatcher[] dynamicCanvasSizeMatchers;
	private final File[] dynamicFullCanvasSizeDirs;
	private final SizeMatcher[] dynamicFullCanvasSizeMatchers;
	private final ResourceCopier resourceCopier;
	private TranslationManager translationManager;
	private Preprocessor preprocessor;

	/**
	 * Creates a new resource manager.
	 * 
	 * @param setting the configuration
	 * @param manager the extension manager
	 * @param environment environment settings
	 */
	public ResourceManager(ResourceSetting setting,
				ExtensionManager manager,
				Environment environment ) 
	{
		super();
		this.resourceSetting = setting;
		this.project = environment.getProject();
		this.booleanEvaluator = environment.getBooleanEvaluator();
		this.resourceDirsByDevice = new HashMap();
		File resDir = setting.getDir();
		if (!resDir.exists()) {
			System.err.println("Warning: the resources-directory [" + resDir.getAbsolutePath() + "] did not exist, J2ME Polish created it automatically now.");
			resDir.mkdir();
		} else if (!resDir.isDirectory()) {
			throw new BuildException("The resources-directory [" + resDir.getAbsolutePath() + "] is not a directory. Please adjust either your \"resources\"-attribute of the <build>-element or the \"dir\"-attribute of the <resources>-element.");
		}
		this.resourcesDir = resDir;
		
		// getting all dynamic ScreenSize-directories:
		File[] resDirFiles = resDir.listFiles();
		Arrays.sort( resDirFiles );
		ArrayList dynamicScreenSizeDirsList = new ArrayList();
		ArrayList dynamicScreenSizeMatchersList = new ArrayList();
		for (int i = 0; i < resDirFiles.length; i++) {
			File file = resDirFiles[i];
			if (file.isDirectory() ) {
				String name = file.getName();
				if (name.startsWith("ScreenSize.") 
						&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
				{
					dynamicScreenSizeDirsList.add( file );
					String requirement = name.substring( "ScreenSize.".length() );
					SizeMatcher sizeMatcher = new SizeMatcher( requirement );
					dynamicScreenSizeMatchersList.add( sizeMatcher );
				}
			}
		}
		this.dynamicScreenSizeDirs = (File[]) dynamicScreenSizeDirsList.toArray( new File[dynamicScreenSizeDirsList.size()] );
		this.dynamicScreenSizeMatchers = (SizeMatcher[]) dynamicScreenSizeMatchersList.toArray( new SizeMatcher[dynamicScreenSizeMatchersList.size()] );

		// getting all dynamic CanvasSize-directories:
		dynamicScreenSizeDirsList = new ArrayList();
		dynamicScreenSizeMatchersList = new ArrayList();
		for (int i = 0; i < resDirFiles.length; i++) {
			File file = resDirFiles[i];
			if (file.isDirectory() ) {
				String name = file.getName();
				if (name.startsWith("CanvasSize.") 
						&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
				{
					dynamicScreenSizeDirsList.add( file );
					String requirement = name.substring( "CanvasSize.".length() );
					SizeMatcher sizeMatcher = new SizeMatcher( requirement );
					dynamicScreenSizeMatchersList.add( sizeMatcher );
				}
			}
		}
		this.dynamicCanvasSizeDirs = (File[]) dynamicScreenSizeDirsList.toArray( new File[dynamicScreenSizeDirsList.size()] );
		this.dynamicCanvasSizeMatchers = (SizeMatcher[]) dynamicScreenSizeMatchersList.toArray( new SizeMatcher[dynamicScreenSizeMatchersList.size()] );

		// getting all dynamic CanvasSize-directories:
		dynamicScreenSizeDirsList = new ArrayList();
		dynamicScreenSizeMatchersList = new ArrayList();
		for (int i = 0; i < resDirFiles.length; i++) {
			File file = resDirFiles[i];
			if (file.isDirectory() ) {
				String name = file.getName();
				if (name.startsWith("FullCanvasSize.") 
						&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
				{
					dynamicScreenSizeDirsList.add( file );
					String requirement = name.substring( "FullCanvasSize.".length() );
					SizeMatcher sizeMatcher = new SizeMatcher( requirement );
					dynamicScreenSizeMatchersList.add( sizeMatcher );
				}
			}
		}
		this.dynamicFullCanvasSizeDirs = (File[]) dynamicScreenSizeDirsList.toArray( new File[dynamicScreenSizeDirsList.size()] );
		this.dynamicFullCanvasSizeMatchers = (SizeMatcher[]) dynamicScreenSizeMatchersList.toArray( new SizeMatcher[dynamicScreenSizeMatchersList.size()] );

		// get localization setting:
		this.localizationSetting = this.resourceSetting.getLocalizationSetting();
		// creates resources-filter:
		this.resourceFilter = new ResourceFilter( setting.getExcludes(), DEFAULT_EXCLUDES, setting.useDefaultExcludes() );
		if (this.localizationSetting != null) {
			String messagesFileName = this.localizationSetting.getMessagesFileName() ;
			this.resourceFilter.addExclude( messagesFileName );
			// add filters for messages_de.txt etc! 
			int splitPos = messagesFileName.lastIndexOf('.');
			String start = null;
			String end = null;
			if (splitPos != -1) {
				start = messagesFileName.substring(0, splitPos) + "_";
				end = messagesFileName.substring(splitPos);
			}
			Locale[] locales = this.localizationSetting.getSupportedLocales();
			for (int i = 0; i < locales.length; i++) {
				Locale locale = locales[i];
				if (splitPos != -1) {
					this.resourceFilter.addExclude( start + locale.toString() + end );
					//System.out.println("excluding [" + start + locale.toString() + end + "]");
				} else {
					this.resourceFilter.addExclude(messagesFileName + locale.toString() );
				}
				if (locale.getCountry().length() > 0) {
					// okay, this locale has also a country defined,
					// so we need to look at the language-resources as well:
					if (splitPos != -1) {
						this.resourceFilter.addExclude( start + locale.getLanguage() + end );
						//System.out.println("excluding [" + start + locale.getLanguage() + end + "]");
					} else {
						this.resourceFilter.addExclude(messagesFileName + locale.getLanguage() );
					}
				}

			}
		}
		
		// creating resource copier:
		ResourceCopierSetting copierSetting = setting.getCopier( environment.getBooleanEvaluator() );
		this.resourceCopier = ResourceCopier.getInstance( copierSetting, manager, environment );
		
	}
	
	/**
	 * Copies all resources to the specified target directory.
	 * 
	 * @param targetDir the target directory
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @throws IOException when a resource could not be copied
	 */
	public void copyResources( File targetDir, Device device, Locale locale ) 
	throws IOException 
	{
		File[] resources = getResources( device, locale );
		//FileUtil.copy(resources, targetDir);
		this.resourceCopier.copyResources(device, locale, resources, targetDir);
		if (this.localizationSetting != null && this.localizationSetting.isDynamic()) {
			saveDynamicTranslations( targetDir, device );
		}
	}
	
	/**
	 * Creates and stores the dynamic translations.
	 * 
	 * @param targetDir the target directory
	 * @param device the current device
	 * @throws IOException when an error occurred
	 */
	private void saveDynamicTranslations(File targetDir, Device device ) 
	throws IOException 
	{
		Locale[] locales = this.localizationSetting.getSupportedLocales();
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i];
			TranslationManager manager = createTranslationManager( device, 
					locale, 
					this.preprocessor, 
					getResourceDirs(device, locale), 
					this.localizationSetting );
			manager.saveTranslations( targetDir, device, locale, this.translationManager );
		}
	}

	/**
	 * Retrieves all files for the give device and locale.
	 * 
	 * @param device the current device
	 * @param locale the current locale - can be null
	 * @return an array with File-references to all resources which should be copied
	 *       into the application-jar
	 */
	public File[] getResources( Device device, Locale locale ) {
		Map resourcesByName = new HashMap();
		
		// first step: read all resource file-names:
		File[] resourceDirectories;
	
		resourceDirectories = getResourceDirs( device, locale );
		for (int i = 0; i < resourceDirectories.length; i++) {
			File dir = resourceDirectories[i];
			File[] files = dir.listFiles();
			addFiles(files, resourcesByName);
		}
		// also load resources from the filesets:
		ResourcesFileSet[] fileSets = this.resourceSetting.getFileSets(this.booleanEvaluator);
		for (int i = 0; i < fileSets.length; i++) {
			ResourcesFileSet set = fileSets[i];
			File dir = set.getDir(this.project);
			if (!dir.exists()) {
				throw new BuildException("The referenced directory [" + dir.getAbsolutePath() + "] of <fileset> does point to a non-existing directory. Please correct the \"dir\"-attribute of the corresponding <fileset>-element.");
			}
			DirectoryScanner scanner = set.getDirectoryScanner(this.project);
			String[] fileNames = scanner.getIncludedFiles();
			addFiles( dir, fileNames, resourcesByName );
		}
	
		// second step:
		// filter the unwanted resources:
		return filterResources( resourcesByName );
	}

	/**
	 * Filters the given resources.
	 * 
	 * @param resourcesByName a map containing a file-reference for each found resource.
	 */
	private File[] filterResources(Map resourcesByName) {
		String[] fileNames = (String[]) resourcesByName.keySet().toArray( new String[ resourcesByName.size()] );
		String[] filteredNames = this.resourceFilter.filter(fileNames);
		File[] filteredFiles = new File[ filteredNames.length ];
		for (int i = 0; i < filteredNames.length; i++) {
			String fileName = filteredNames[i];
			filteredFiles[i] = (File) resourcesByName.get( fileName );
		}
		return filteredFiles;
	}

	/**
	 * Gets all directories containing resources for the specified device.
	 * 
	 * @param device the device
	 * @param locale the current locale, can be null
	 * @return an array of file containing all found directories for the given device
	 */
	private File[] getResourceDirs(Device device, Locale locale) {
		String key = device.getIdentifier();
		if (locale != null) {
			key += locale.toString();
		}
		File[] resourceDirectories = (File[]) this.resourceDirsByDevice.get( key );
		if (resourceDirectories != null) {
			return resourceDirectories;
		}
		ArrayList dirs = new ArrayList();
		// first dir is the general resources dir:
		dirs.add( this.resourcesDir );
		String resourcePath = this.resourcesDir.getAbsolutePath() + File.separator;
		// then the vendor specific resources:
		File resourceDir = new File( resourcePath + device.getVendorName() );
		if (resourceDir.exists()) {
			dirs.add( resourceDir );
		}
		// now add all dynamic ScreenSize-directories:
		String screenSize = device.getCapability("polish.ScreenSize");
		if (screenSize != null) {
			for (int i = 0; i < this.dynamicScreenSizeDirs.length; i++ ) {
				SizeMatcher matcher = this.dynamicScreenSizeMatchers[i];
				if (matcher.matches(screenSize)) {
					resourceDir = this.dynamicScreenSizeDirs[i];
					dirs.add( resourceDir );
				}
			}
		}
		// now add all dynamic CanvasSize-directories:
		String canvasSize = device.getCapability("polish.CanvasSize");
		if (canvasSize != null) {
			for (int i = 0; i < this.dynamicCanvasSizeDirs.length; i++ ) {
				SizeMatcher matcher = this.dynamicCanvasSizeMatchers[i];
				if (matcher.matches(canvasSize)) {
					resourceDir = this.dynamicCanvasSizeDirs[i];
					dirs.add( resourceDir );
				}
			}
		}
		// now add all dynamic FullCanvasSize-directories:
		String fullCanvasSize = device.getCapability("polish.FullCanvasSize");
		if (fullCanvasSize != null) {
			for (int i = 0; i < this.dynamicFullCanvasSizeDirs.length; i++ ) {
				SizeMatcher matcher = this.dynamicFullCanvasSizeMatchers[i];
				if (matcher.matches(fullCanvasSize)) {
					resourceDir = this.dynamicFullCanvasSizeDirs[i];
					dirs.add( resourceDir );
				}
			}
		}
		// now add all group resource-directories:
		String[] groups = device.getGroupNames();
		for (int i = 0; i < groups.length; i++) {
			String group = groups[i];
			resourceDir = new File( resourcePath + group );
			if (resourceDir.exists()) {
				dirs.add( resourceDir );
			}
		}
		// The last possible resource directory is the directory for the specific device:
		resourceDir = new File( resourcePath + device.getVendorName() 
							+ File.separatorChar + device.getName() );
		if (resourceDir.exists()) {
			dirs.add( resourceDir );
		}				
		if (locale != null) {
			// now add all locale-specific directories:
			String languageDirName = null;
			String localeDirName = locale.toString();
			if (locale.getCountry().length() > 0) {
				languageDirName = locale.getLanguage();
			}
			resourceDirectories = (File[]) dirs.toArray( new File[ dirs.size() ]);
			for (int i = 0; i < resourceDirectories.length; i++) {
				resourceDir = resourceDirectories[i];
				resourcePath = resourceDir.getAbsolutePath() + File.separator;
				if (languageDirName != null) {
					resourceDir = new File( resourcePath + languageDirName );
					if (resourceDir.exists()) {
						dirs.add( resourceDir );
					}
				}
				resourceDir = new File( resourcePath + localeDirName );
				if (resourceDir.exists()) {
					dirs.add( resourceDir );
				}				
			}

		}
		resourceDirectories = (File[]) dirs.toArray( new File[ dirs.size() ]);
		this.resourceDirsByDevice.put( key, resourceDirectories );
		return resourceDirectories;
	}

	/**
	 * Adds the found files to the given map.
	 * @param files the files
	 * @param resourcesByName the map.
	 */
	private void addFiles(File[] files, Map resourcesByName) {
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				resourcesByName.put( file.getName(), file );
			}
		}
	}
	
	/**
	 * Adds the found files to the given map.
	 * @param fileNames the files-names
	 * @param resourcesByName the map.
	 */
	private void addFiles(File baseDir, String[] fileNames, Map resourcesByName) {
		String basePath = baseDir.getAbsolutePath() + File.separator;
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File( basePath + fileNames[i] );
			resourcesByName.put( file.getName(), file );
		}
	}
	
	/**
	 * Retrieves all supported locales.
	 * 
	 * @return an array of supported locales, can be null
	 */
	public Locale[] getLocales() {
		if (this.localizationSetting == null) {
			return null;
		} else {
			return this.localizationSetting.getSupportedLocales();
		}
	}
	
	/**
	 * Retrieves the manager of translations.
	 * 
	 * @param device the current device
	 * @param locale the current locale (not null)
	 * @param currentPreprocessor the preprocessor
	 * @return the translation manager
	 * @throws IOException when a messages-files could not be read
	 */
	public TranslationManager getTranslationManager( Device device, Locale locale, Preprocessor currentPreprocessor) 
	throws IOException
	{
		this.preprocessor = currentPreprocessor;
		if (!this.localizationSetting.isDynamic()  
				|| this.translationManager == null  
				|| !this.translationManager.getLocale().equals( locale ) ) 
		{
			this.translationManager = 
				createTranslationManager( device, 
						locale, 
						currentPreprocessor, 
						getResourceDirs(device, locale), 
						this.localizationSetting ); 
		}
		// resetting preprocessing variables:
		currentPreprocessor.getEnvironment().addVariables( this.translationManager.getPreprocessingVariables() );
		return this.translationManager;
	}

	/**
	 * Creates an instance of a TranslationManager
	 * 
	 * @param device the current device
	 * @param locale the current locale (not null)
	 * @param currentPreprocessor the preprocessor
	 * @param resourceDirs the directories containing resources for this device and this locale
	 * @param setting the localization settings
	 * @return an instance of TranslationManager
	 * @throws IOException when some translation could not be loaded
	 */
	protected TranslationManager createTranslationManager(Device device, Locale locale, Preprocessor currentPreprocessor, File[] resourceDirs, LocalizationSetting setting) 
	throws IOException 
	{
		String className = this.localizationSetting.getTranslationManagerClassName();
		if (className != null) {
			try {
				Class managerClass = Class.forName( className );
				Constructor constructor = managerClass.getConstructor( new Class[]{ Project.class, Device.class, Locale.class, Preprocessor.class, File[].class, LocalizationSetting.class} );
				TranslationManager manager = (TranslationManager) constructor.newInstance( new Object[]{ this.project, device, locale, currentPreprocessor, resourceDirs, setting} );
				return manager;
			} catch (Exception e) {
				//just return a normal translation manager
				e.printStackTrace();
				throw new BuildException("The translation manager [" + className + "] could not be found, please adjust your <localization>-translationManager-setting.");
			}
		} else {
			return new TranslationManager( this.project,
				device, 
				locale, 
				currentPreprocessor, 
				getResourceDirs(device, locale), 
				this.localizationSetting );
		}
	}

	/**
	 * Reads the style sheet for the given device.
	 * 
	 * @param baseStyleSheet the base style sheet
	 * @param device the device
	 * @param locale the current locale, can be null
	 * @return the style sheet for that device
	 * @throws IOException when a sub-style sheet could not be loaded.
	 */
	public StyleSheet loadStyleSheet(StyleSheet baseStyleSheet, Device device, Locale locale) throws IOException {
		
		long lastCssModification = baseStyleSheet.lastModified();		
		CssReader cssReader = new CssReader( baseStyleSheet );
		
		File[] resourceDirs = getResourceDirs(device, locale);
		for (int i = 1; i < resourceDirs.length; i++) { // ignore the basic resource-file:
			File dir = resourceDirs[i];
			File cssFile = new File( dir.getAbsolutePath() + File.separator + "polish.css");
			if (cssFile.exists()) {
				//System.out.println("loading CSS file [" + cssFile.getAbsolutePath() + "].");
				if (cssFile.lastModified() > lastCssModification) {
					lastCssModification = cssFile.lastModified();
				}
				cssReader.add( cssFile );
			}
		}
		StyleSheet sheet = cssReader.getStyleSheet();
		sheet.setLastModified(lastCssModification);
		return sheet;
	}
	
}
