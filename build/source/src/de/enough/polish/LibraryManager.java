/*
 * Created on 23-May-2004 at 22:20:13.
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
package de.enough.polish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages the libraries of devices.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class LibraryManager {
	
	private final String wtkLibPath;
	private final String projectLibPath;
	private final String polishLibPath;
	private final HashMap libraries = new HashMap();
	private final HashMap resolvedClassPaths = new HashMap();
	private final HashMap resolvedLibraryPaths = new HashMap();
	private final Map antProperties;

	/**
	 * Creates a new ApiManager.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param projectLibPath the default path to libraries. The default is "import".
	 * @param wtkHomePath the path to the wireless toolkit (if known)
	 * @param preverifyPath the path to the preverify executable
	 * @param is input stream for reading the apis.xml file
	 * @throws JDOMException when there are syntax errors in apis.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	public LibraryManager( Map antProperties, String projectLibPath, String wtkHomePath, String preverifyPath, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (wtkHomePath == null) {
			int pos = preverifyPath.lastIndexOf( File.separatorChar );
			wtkHomePath = preverifyPath.substring(0, pos );
			pos = preverifyPath.lastIndexOf( File.separatorChar );
			wtkHomePath = preverifyPath.substring(0, pos );
		} else if (wtkHomePath.endsWith( File.separator )) {
			wtkHomePath = wtkHomePath.substring(0, wtkHomePath.length() -1 );
		}
		if (!projectLibPath.endsWith( File.separator )) {
			projectLibPath += File.separator;
		}
		this.antProperties = antProperties;
		this.projectLibPath = projectLibPath;
		this.wtkLibPath = wtkHomePath + File.separatorChar + "lib" + File.separatorChar;
		String polishHomeProperty = (String) antProperties.get("polish.home");
		if (polishHomeProperty != null) {
			this.polishLibPath = polishHomeProperty + File.separatorChar + "import" + File.separatorChar;
		} else {
			this.polishLibPath = null;
		}
		loadLibraries( is );
	}

	/**
	 * Loads the apis.xml file.
	 * 
	 * @param is input stream for reading the apis.xml file
	 * @throws JDOMException when there are syntax errors in apis.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	private void loadLibraries(InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load apis.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			Library lib = new Library( this.antProperties, this.wtkLibPath, this.projectLibPath, this.polishLibPath, definition, this );
			Library existingLib = (Library) this.libraries.get( lib.getSymbol() ); 
			if ( existingLib != null ) {
				throw new InvalidComponentException("The library [" + lib.getFullName() 
						+ "] uses the symbol [" + lib.getSymbol() + "], which is already used by the "
						+ "library [" + existingLib.getFullName() 
						+ "]. Please adjust your settings in [apis.xml].");
			}
			String[] names = lib.getNames();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				existingLib = (Library) this.libraries.get( name ); 
				if ( existingLib != null ) {
					throw new InvalidComponentException("The library [" + lib.getFullName() 
							+ "] uses the name [" + name + "], which is already used by the "
							+ "library [" + existingLib.getFullName() 
							+ "]. Please adjust your settings in [apis.xml].");
				}
				this.libraries.put( name, lib );
			}
			this.libraries.put( lib.getSymbol(), lib );
		}		
	}
	
	/**
	 * Retrieves the classpath for the given device.
	 * 
	 * @param device the device for which the path should be retrieved
	 * @return the classpath for this device. When a supported library
	 *         could not be found, a warning will be printed to System.out.
	 */
	public String[] getClassPaths( Device device ) {
		if (device.getSupportedApisAsString() == null) {
			return new String[0];
		}
		// first check if the device's libraries have been resolved already:
		String[] resolvedPaths = (String[]) this.resolvedClassPaths.get( device.getSupportedApisAsString() );
		if (resolvedPaths != null) {
			return resolvedPaths;
		}
		// now resolve the path to each supported library:
		String[] libNames = device.getSupportedApis();
		ArrayList libPaths = new ArrayList();
		for (int i = 0; i < libNames.length; i++) {
			String libName = libNames[i];
			Library lib = (Library) this.libraries.get( libName );
			String libPath = null;
			if (lib != null ) {
				libPath = lib.getPath();
			} else {
				libPath = (String) this.resolvedLibraryPaths.get( libName );
				if (libPath == null) {
					// try to resolve the lib-path by the name:
					// try a jar file:
					String fileName = this.projectLibPath + libName + ".jar";
					File file = new File( fileName );
					if ( file.exists() ) {
						libPath = file.getAbsolutePath();
					} else {
						// try a zip-file:
						fileName = this.projectLibPath + libName + ".zip";
						file = new File( fileName );
						if ( file.exists() ) {
							libPath = file.getAbsolutePath();
						} else {
							// now check if an property has been defined for this api:
							String path = (String) this.antProperties.get( "polish.api." + libName );
							if (path != null) {
								File libFile = new File( path );
								if (libFile.exists()) {
									libPath = libFile.getAbsolutePath();
								} else {
									System.out.println("Warning: the Ant-property [polish.api." + libName + "] points to a non-existing file. When this leads to problems, please register this API in [apis.xml].");
								}
							} else {
								System.out.println("Warning: unable to resolve path to API [" + libName + "]. When this leads to problems, please register this API in [apis.xml].");
							}
						}
					}
				}
			} // if libPath == null
			if (libPath != null) {
				libPaths.add( libPath );
				this.resolvedLibraryPaths.put( libName, libPath );
			}
		}
		resolvedPaths = (String[]) libPaths.toArray( new String[ libPaths.size() ] );
		this.resolvedClassPaths.put( device.getSupportedApisAsString(), resolvedPaths );
		return resolvedPaths;
	}

	/**
	 * Retrieves the symbols for the specified library.
	 * 
	 * @param libName the name of the library
	 * @return the symbols for this library. When the library is not known, null is returned.
	 */
	public String[] getSymbols(String libName) {
		Library lib = (Library) this.libraries.get( libName );
		if (lib == null) { 
			return null;
		} else {
			return lib.getSymbols();
		}
	}

	/**
	 * Retrieves the specified library.
	 * 
	 * @param libName the name of the library
	 * @return the library. When the library is not known, null is returned.
	 */
	public Library getLibrary(String libName) {
		return (Library) this.libraries.get( libName );
	}

	/**
	 * Loads the custom-apis.xml from the current project.
	 * 
	 * @param customApisFile the file containing custom-apis.xml
	 * @throws JDOMException when there is a syntax error
	 * @throws InvalidComponentException when several libraries are duplicated for example
	 */
	public void loadCustomLibraries(File customApisFile ) 
	throws JDOMException, InvalidComponentException 
	{
		if (customApisFile.exists()) {
			try {
				loadLibraries( new FileInputStream( customApisFile ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-apis.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-apis.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "apis.xml", "custom-apis.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
		
	}
}
