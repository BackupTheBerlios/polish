/*
 * Created on 14-Apr-2005 at 22:12:37.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;

/**
 * <p>Allows to include a binary library depending on conditions.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LibrarySetting extends Setting {
	
	public File dir;
	public File file; 
	public File dirOrFile;
	private int id;
	private File cacheDir;
	private boolean libraryChanged;

	/**
	 * Creates a new library binding.
	 */
	public LibrarySetting() {
		super();
	}
	
	

	/**
	 * @return Returns the dir.
	 */
	public File getDir() {
		return this.dir;
	}
	/**
	 * @param dir The dir to set.
	 */
	public void setDir(File dir) {
		if (!dir.isDirectory()) {
			throw new BuildException("The <library>-dir [" + dir.getAbsolutePath() + "] is a file and not a directory. Please use the \"file\" attribute instead in your build.xml.");
		}
		if (this.file != null) {
			throw new BuildException("You cannot specify both the \"file\" as well as the \"dir\" attribute in one <library>-element. Please correct this in your build.xml.");
		}
		this.dir = dir;
		this.dirOrFile = dir;
	}
	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return this.file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		if (file.isDirectory()) {
			throw new BuildException("The <library>-file [" + file.getAbsolutePath() + "] is a directory and not a file. Please use the \"dir\" attribute instead in your build.xml.");
		}
		if (this.dir != null) {
			throw new BuildException("You cannot specify both the \"file\" as well as the \"dir\" attribute in one <library>-element. Please correct this in your build.xml.");
		}
		this.file = file;
		this.dirOrFile = file;
	}

	
	public File getDirOrFile() {
		return this.dirOrFile;
	}


	/**
	 * @return
	 */
	public boolean isDirectory() {
		return (this.dir != null);
	}

	/**
	 * @return
	 */
	public long lastModified() {
		return this.dirOrFile.lastModified();
	}
	
	public String getName() {
		return this.dirOrFile.getName();
	}
	
	public String getAbsolutePath() {
		return this.dirOrFile.getAbsolutePath();
	}



	/**
	 * @return
	 */
	public int getId() {
		return this.id;
	}
	
	public void setId( int id ) {
		this.id = id;
	}
	
	
	public boolean copyToCache( File cacheBaseDir ) {
		
		boolean changed = false;
		this.cacheDir = new File( cacheBaseDir, "" + this.id );
		File jarCacheDir = new File( cacheBaseDir, "" + this.id + "jar");
		//System.out.println(">>>>copyToCache: copying " + this.dirOrFile.getAbsolutePath() + " to " + this.cacheDir );
		if ( this.dir != null ) {
			// a directory can contain library-files (jar, zip) as well
			// as plain class or resource files. Each library-file
			// will be extracted whereas other files will just be copied
			// to the build/binary folder. 
			DirectoryScanner binaryScanner = new DirectoryScanner();
			binaryScanner.setBasedir( this.dir );
			// just include all files in the directory:
			binaryScanner.scan();
			String[] includedFiles = binaryScanner.getIncludedFiles();
			for (int j = 0; j < includedFiles.length; j++) {
				String fileName = includedFiles[j];
				File fileInDir = new File( this.dir, fileName );
				if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
					// this is a library file:
					// only extract it when the original is newer than the copy:
					File cacheCopy = new File( jarCacheDir, fileName );
					if ( (!cacheCopy.exists())
							|| (fileInDir.lastModified() > cacheCopy.lastModified())) {
						changed = true;
						try {
							// copy the library to the cache:
							FileUtil.copy(fileInDir, cacheCopy );
							// unzip / unjar the content:
							JarUtil.unjar(fileInDir, this.cacheDir );
						} catch (IOException e) {
							e.printStackTrace();
							throw new BuildException("Unable to extract the binary class files from the library [" + fileInDir.getAbsolutePath() + "]: " + e.toString(), e );
						}
					}
				} else {
					// this is a normal class or resource file:
					try {
						File targetFile = new File( this.cacheDir, fileName );
						// copy the file only when it is newer
						// than the existing copy: 
						if ( (!targetFile.exists()) 
								|| (fileInDir.lastModified() > targetFile.lastModified()) ) {
							changed = true;
							FileUtil.copy(fileInDir, targetFile);
						}
					} catch (IOException e) {
						e.printStackTrace();
						throw new BuildException("Unable to copy the binary class files from the library [" + this.dirOrFile.getAbsolutePath() + "]: " + e.toString(), e );
					}
				}
			}
		} else {
			// this is a library (jar or zip) file:
			// copy only when the original is newer than the cached copy: 
			if ( (!this.cacheDir.exists())
					|| (this.file.lastModified() > this.cacheDir.lastModified())) {
				changed = true;
				try {
					// copy the library to the cache:
					FileUtil.copy(this.file, new File( jarCacheDir, this.file.getName() ) );
					// unzip / unjar the content:
					JarUtil.unjar(this.file, this.cacheDir);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to extract the binary class files from the library [" + this.file.getAbsolutePath() + "]: " + e.toString(), e );
				}
			}
			/*
			File cacheCopy = new File( cachePath + lib.getId() + File.separatorChar + lib.getName() );
			if ( (!cacheCopy.exists())
					|| (lib.lastModified() > cacheCopy.lastModified())) {
				try {
					this.binaryLibrariesUpdated = true;
					// copy the library to the cache:
					FileUtil.copy(lib.getFile(), cacheCopy );
					// unzip / unjar the content:
					JarUtil.unjar(lib, binaryBaseDir);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to extract the binary class files from the library [" + lib.getAbsolutePath() + "]: " + e.toString(), e );
				}
			}
			*/
		}
		this.libraryChanged = changed;
		return changed;
	}
	
	public void copyFromCache( File targetDir ) {
		//targetDir = new File( targetDir, "" + this.id );
		//System.out.println("<<<<copyFromCache: copying " + this.cacheDir + " to " + targetDir );
		try {
			FileUtil.copyDirectoryContents( this.cacheDir, targetDir, true );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( "Unable to copy binary library [" + this.dirOrFile.getAbsolutePath() + "]: " + e.toString() );
		}
	}
	
	public boolean isLibraryChange() {
		return this.libraryChanged;
	}
}

