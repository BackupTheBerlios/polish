/*
 * Created on 11-Sep-2004 at 20:38:11.
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

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;

/**
 * <p>Filters resources by names.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceFilter {
	
	private final String[] startPatterns;
	private final String[] endPatterns;
	private final HashMap excludesByName;

	/**
	 * Creates a new filter.
	 * 
	 * @param userExcludes user defined exclused patterns, e.g *.db
	 * @param defaultExcludes default exclude patterns, e.g. "polish.css"
	 * @param useDefaultExcludes true when the default excludes should be used
	 */
	public ResourceFilter( String[] userExcludes, String[] defaultExcludes, boolean useDefaultExcludes ) {
		super();
		this.excludesByName = new HashMap();
		ArrayList startPatternsList = new ArrayList();
		ArrayList endPatternsList = new ArrayList();
		for (int i = 0; i < userExcludes.length; i++) {
			String exclude = userExcludes[i];
			if (exclude.indexOf('*') == -1) {
				this.excludesByName.put( exclude, Boolean.TRUE );
			} else if (exclude.charAt(0) == '*'){
				endPatternsList.add( exclude.substring( 1 ) );
			} else if (exclude.charAt( exclude.length()-1) == '*') {
				startPatternsList.add( exclude.substring( 0, exclude.length() -1 ) );
			} else {
				throw new BuildException("Invalid exclude-pattern [" + exclude + "]: stars are only allowed at either the start or the end of the pattern.");
			}
		}
		if (useDefaultExcludes) {
			for (int i = 0; i < defaultExcludes.length; i++) {
				String exclude = defaultExcludes[i];
				if (exclude.indexOf('*') == -1) {
					this.excludesByName.put( exclude, Boolean.TRUE );
				} else if (exclude.charAt(0) == '*'){
					endPatternsList.add( exclude.substring( 1 ) );
				} else if (exclude.charAt( exclude.length()-1) == '*') {
					startPatternsList.add( exclude.substring( 0, exclude.length() -1 ) );
				} else {
					throw new BuildException("Invalid exclude-pattern [" + exclude + "]: stars are only allowed at either the start or the end of the pattern.");
				}
			}
		}
		this.startPatterns = (String[]) startPatternsList.toArray( new String[ startPatternsList.size() ]);
		this.endPatterns = (String[]) endPatternsList.toArray( new String[ endPatternsList.size() ]);
	}
	
	public void addExclude( String fileName ) {
		this.excludesByName.put( fileName, Boolean.TRUE );
	}
	
	public void removeExclude( String fileName ) {
		this.excludesByName.remove( fileName );
	}
	
	/**
	 * Filters the given file names.
	 * 
	 * @param fileNames array of file names
	 * @return only those file-names which should be included
	 */
	public String[] filter( String[] fileNames ) {
		ArrayList list  = new ArrayList( fileNames.length );
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			//System.out.println("filter: checking " + fileName );
			if (this.excludesByName.get(fileName) != null) {
				// this file should not be included: 
				continue;
			}
			boolean include = true;
			for (int j = 0; j < this.startPatterns.length; j++) {
				String pattern = this.startPatterns[j];
				if (fileName.startsWith( pattern )) {
					// this file should not be included:
					include = false;
					break;
				}
			}
			for (int j = 0; j < this.endPatterns.length; j++) {
				String pattern = this.endPatterns[j];
				if (fileName.endsWith( pattern )) {
					// this file should not be included:
					include = false;
					break;
				}
			}
			if (include) {
				//System.out.println("filter: adding " + fileName );
				// okay, this file should really be included:
				list.add( fileName );
			}
		}
		return (String[]) list.toArray( new String[ list.size() ] );
	}

}
 