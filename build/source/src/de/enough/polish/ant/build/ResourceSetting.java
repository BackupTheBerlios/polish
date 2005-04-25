/*
 * Created on 10-Sep-2004 at 19:18:54.
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
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.ant.Setting;
import de.enough.polish.resources.ResourcesFileSet;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores all settings made in the &lt;resources&gt;-element.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceSetting extends Setting {

	private ArrayList localizationSettings;
	private ArrayList fileSets;
	private File dir;
	private boolean useDefaultExcludes = true;
	private String[] excludes = new String[0];
	private String baseDir;
	private Project project;
	private ArrayList copierSettings;

	/**
	 * Creates a new empty Resource Setting.
	 * 
	 * @param project the parent Ant project. 
	 * 			Is needed to resolve paths.
	 */
	public ResourceSetting( Project project ) {
		super();
		this.baseDir = project.getBaseDir().getAbsolutePath() + File.separator;
		this.project = project;
		this.localizationSettings = new ArrayList();
		this.fileSets = new ArrayList();
	}
	
	public void addConfiguredLocalization( LocalizationSetting setting ) {
		if (setting.includeAllLocales() == false && setting.getSupportedLocales() == null) {
			throw new BuildException("Invalid <localisation>-element in build.xml: please specify the attribute \"locales\"." );
		}
		this.localizationSettings.add( setting );
	}
	
	public void addConfiguredFileset( ResourcesFileSet fileSet ) {
		this.fileSets.add( fileSet );
	}
	
	public void addConfiguredCopier( ResourceCopierSetting setting ) {
		if (setting.getClassName() == null) {
			throw new BuildException("Invalid <copier>-element in build.xml: please specify the attribute \"class\"." );
		}
		if (this.copierSettings == null) {
			this.copierSettings = new ArrayList();
		}
		this.copierSettings.add( setting );
	}
	
	public void setDir( File dir ) {
		if (!dir.exists()) { 
			throw new BuildException("The specified resources-directory [" + dir.getAbsolutePath() + "] doe not exist. Please adjust the \"dir\"-attribute of the <resources>-element.");
		}
		this.dir = dir;
	}
	
	public File getDir() {
		if (this.dir == null) {
			this.dir = new File( this.baseDir + "resources" );
		}
		return this.dir;
	}
	
	public void setDefaultexcludes( boolean useExcludes ) {
		this.useDefaultExcludes = useExcludes;
	}
	
	public boolean useDefaultExcludes() {
		return this.useDefaultExcludes;
	}
	
	public void setExcludes( String excludes ) {
		this.excludes = StringUtil.splitAndTrim( excludes, ',' );
	}
	
	public String[] getExcludes() {
		return this.excludes;
	}
	
	public void setLocales( String locales ) {
		if (locales.length() == 0) {
			// ignore this setting
			return;
		}
		LocalizationSetting setting = new LocalizationSetting();
		setting.setLocales(locales);
		this.localizationSettings.add( setting );
	}
	
	public void setLocale( String locale ) {
		setLocales( locale );
	}
	
	/**
	 * Retrieves the first active localization setting or null when none was found.
	 * 
	 * @return the first active localization setting or null when none was found.
	 */
	public LocalizationSetting getLocalizationSetting() {
		for (Iterator iter = this.localizationSettings.iterator(); iter.hasNext();) {
			LocalizationSetting localizationSetting = (LocalizationSetting) iter.next();
			if (localizationSetting.isActive(this.project)) {
				return localizationSetting;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves an array of file-sets which should either include or exclude files.
	 * 
	 * @param evaluator the evaluator for complex terms
	 * @return an array of the appropriate ResourcesFileSet, can be empty but not null
	 */
	public ResourcesFileSet[] getFileSets( BooleanEvaluator evaluator ) {
		if (this.fileSets.size() == 0) {
			return new ResourcesFileSet[0];
		}
		ArrayList list = new ArrayList( this.fileSets.size());
		for (Iterator iter = this.fileSets.iterator(); iter.hasNext();) {
			ResourcesFileSet set = (ResourcesFileSet) iter.next();
			if (set.isActive(evaluator, this.project)) {
				list.add( set );
			}
		}
		ResourcesFileSet[] sets = (ResourcesFileSet[]) list.toArray( new ResourcesFileSet[list.size()] );
		return sets;
	}
	
	public ResourceCopierSetting getCopier( BooleanEvaluator evaluator ) {
		if (this.copierSettings == null) {
			return null;
		}
		ResourceCopierSetting[] settings = (ResourceCopierSetting[]) this.copierSettings.toArray( new ResourceCopierSetting[ this.copierSettings.size()]  );
		for (int i = 0; i < settings.length; i++) {
			ResourceCopierSetting setting = settings[i];
			if (setting.isActive(evaluator, this.project)) {
				return setting;
			}
		}
		return null;
	}


}
