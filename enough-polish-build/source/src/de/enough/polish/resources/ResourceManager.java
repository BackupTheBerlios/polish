/*
 * Created on 10-Sep-2004 at 16:35:12.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.ant.build.ResourceSetting;
import de.enough.polish.preprocess.BooleanEvaluator;
import de.enough.polish.preprocess.CssReader;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.StyleSheet;
import de.enough.polish.util.FileUtil;

/**
 * <p>Is responsible for the assembling of resources like images and localization messages.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        10-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceManager {
	private final static String[] DEFAULT_EXCLUDES = new String[]{ "polish.css", "*~", "*.bak" };
	private final ResourceSetting resourceSetting;
	private final Project project;
	private final BooleanEvaluator booleanEvaluator;
	private final File resourcesDir;
	private final Map resourceDirsByDevice;
	private final ResourceFilter resourceFilter;
	private final LocalizationSetting localizationSetting;

	/**
	 * Creates a new resource manager.
	 * 
	 * @param setting the configuration
	 * @param project the parent Ant project
	 * @param evaluator the boolean evaluator
	 * 
	 */
	public ResourceManager(ResourceSetting setting, 
				Project project, 
				BooleanEvaluator evaluator ) 
	{
		super();
		this.resourceSetting = setting;
		this.project = project;
		this.booleanEvaluator = evaluator;
		this.resourceDirsByDevice = new HashMap();
		File resDir = setting.getDir();
		if (!resDir.exists()) {
			System.err.println("Warning: the resources-directory [" + resDir.getAbsolutePath() + "] did not exist, J2ME Polish created it automatically now.");
			resDir.mkdir();
		} else if (!resDir.isDirectory()) {
			throw new BuildException("The resources-directory [" + resDir.getAbsolutePath() + "] is not a directory. Please adjust either your \"resources\"-attribute of the <build>-element or the \"dir\"-attribute of the <resources>-element.");
		}
		this.resourcesDir = resDir;
		// get localization setting:
		this.localizationSetting = this.resourceSetting.getLocalizationSetting();
		// creates resources-filter:
		this.resourceFilter = new ResourceFilter( setting.getExcludes(), DEFAULT_EXCLUDES, setting.useDefaultExcludes() );
		if (this.localizationSetting != null) {
			this.resourceFilter.addExclude( this.localizationSetting.getMessagesFileName() );
		}
		
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
		FileUtil.copy(resources, targetDir);
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
	 * @param preprocessor the preprocessor
	 * @return the translation manager
	 * @throws IOException when a messages-files could not be read
	 */
	public TranslationManager getTranslationManager( Device device, Locale locale, Preprocessor preprocessor) 
	throws IOException
	{
		return new TranslationManager( device, 
				locale, 
				preprocessor, 
				getResourceDirs(device, locale), 
				this.localizationSetting );
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
